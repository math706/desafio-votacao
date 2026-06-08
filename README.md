# Votação em Assembleia

## Descrição

API REST para gerenciar sessões de votação em assembleias cooperativas. A aplicação permite cadastrar pautas, abrir sessões de votação com duração configurável, registrar votos e consultar os resultados. Foi desenvolvida com Spring Boot, JPA, PostgreSQL, Flyway, Lombok, Swagger e Java 21.

## Tecnologias Utilizadas

- **Spring Boot:** Framework principal para construção da aplicação;
- **JPA (Java Persistence API):** Para mapeamento objeto-relacional e persistência dos dados;
- **Lombok:** Para redução de código boilerplate;
- **Docker:** Para conteinerização do banco de dados PostgreSQL no perfil de produção;
- **PostgreSQL:** Banco de dados relacional usado em produção;
- **H2:** Banco de dados em memória usado no perfil de desenvolvimento;
- **Flyway:** Para versionamento e migração do schema do banco em produção;
- **Swagger:** Para documentação interativa da API;
- **JUnit & Mockito:** Para testes unitários e de integração;
- **Java 21:** Versão utilizada no desenvolvimento.

## Funcionalidades

- Cadastro e consulta de pautas;
- Abertura de sessões de votação com duração customizável (padrão: 1 minuto);
- Registro de votos por associado (SIM ou NÃO);
- Consulta de resultados da votação;
- Validação de CPF com retorno aleatório de elegibilidade (Bônus).

## Regras

- Cada associado pode votar apenas uma vez por pauta;
- Após o encerramento da sessão, novos votos não são aceitos;
- Cada pauta comporta apenas uma sessão de votação;
- Votos só podem ser registrados em sessões ativas.

## Como Executar

### Pré-requisitos

- Java 21 e Maven

### Perfil `dev` (padrão) — sem Docker

```bash
./mvnw spring-boot:run
```

O banco H2 é criado em memória automaticamente. Ao subir, a aplicação popula o banco com dados de exemplo para facilitar os testes: pautas com sessões ativas e votos, uma sessão já encerrada com resultado disponível, e pautas sem sessão ainda.

### Perfil `prod` — PostgreSQL com Docker

Somente o banco:
```bash
docker compose up postgres -d
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
```

Tudo via Docker:
```bash
docker compose --profile full up --build
```

### Testes

```bash
./mvnw test
```

---

Após subir, a aplicação estará disponível em:
- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- Console H2 (apenas dev): `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:votacao` · User: `sa` · Password: *(vazio)*

### Variáveis de ambiente (perfil `prod`)

| Variável | Padrão |
|---|---|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/votacao` |
| `DATABASE_USERNAME` | `votacao` |
| `DATABASE_PASSWORD` | `votacao` |
| `PORT` | `8080` |

## Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/pautas` | Cadastrar pauta |
| `GET` | `/api/v1/pautas` | Listar pautas |
| `GET` | `/api/v1/pautas/{id}` | Buscar pauta por ID |
| `GET` | `/api/v1/pautas/{id}/resultado` | Resultado da votação |
| `POST` | `/api/v1/pautas/{id}/sessoes` | Abrir sessão de votação |
| `GET` | `/api/v1/pautas/{id}/sessoes/{sessaoId}` | Consultar sessão |
| `POST` | `/api/v1/sessoes/{sessaoId}/votos` | Registrar voto |
| `GET` | `/api/v1/users/{cpf}` | Verificar elegibilidade do CPF |

## Decisões Técnicas

- A API foi estruturada em camadas (Controller → Service → Repository), padrão que facilita a manutenção e os testes sem adicionar complexidade desnecessária ao projeto;
- O versionamento foi feito por path (`/api/v1/`), por ser mais explícito nas URLs e funcionar sem configuração especial em qualquer cliente HTTP;
- A sessão de votação não usa um campo booleano `ativa` que precisaria de um job para ser atualizado. Em vez disso, `isAtiva()` é calculado em tempo real comparando `LocalDateTime.now()` com o horário de encerramento;
- A unicidade de voto é garantida na camada de serviço e reforçada por um unique constraint no banco (`sessao_id + associado_id`), evitando duplicatas mesmo sob concorrência;
- A contagem de votos usa queries `COUNT` direto no banco, sem carregar entidades em memória, o que mantém a performance mesmo com grandes volumes de dados;
- O Swagger documenta apenas os status codes não óbvios (409, 422, 404), evitando ruído na documentação.
