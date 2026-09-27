# Jogo Enigma API - V1 DevOps didatica

Arquitetura: Front HTML/JS -> BFF Node/Express -> API Spring Boot -> PostgreSQL. Inclui DTO, VO, Entity, Repository, Service e Controller; testes com JUnit 5/Mockito; Testcontainers para integracao com PostgreSQL; JaCoCo/PMD; PostgreSQL + pgAdmin; Prometheus + Grafana; Jenkins; Docker Hub; homologacao e Cypress E2E.

> **Alteracao desta versao:** Cucumber foi removido para simplificar e estabilizar a pratica. BDD/ATDD pode continuar sendo trabalhado como tecnica de especificacao de comportamento, sem depender do framework Cucumber. A aceitacao ponta a ponta permanece coberta pelo Cypress.

## Estrategia de testes
- **JUnit 5 + Mockito:** testes de unidade e das camadas Entity, VO, DTO, Service e Controller.
- **JUnit 5 + Testcontainers:** teste de Repository com PostgreSQL real em container.
- **JaCoCo:** mede a cobertura produzida pelos testes JUnit.
- **PMD:** analise estatica do codigo; nao mede cobertura.
- **Cypress:** testes de aceitacao/E2E no ambiente de homologacao.

## Profiles e bancos
- `dev`: PostgreSQL local via `docker-compose.dev.yml`.
- `homol`: PostgreSQL do `docker-compose.homol.yml`.
- Testes de Repository: **Testcontainers PostgreSQL**, nao H2. Isso evita diferencas de dialeto/comportamento entre teste e PostgreSQL real. `@WebMvcTest` e testes unitarios nao precisam de banco.
- Nao ha profile `prod` nesta V1. O pipeline para apos E2E/HOMOL e apenas registra que Production ainda nao foi implementado.

## Jenkins Credential
Cadastrar `Username with password`: ID `dockerhub-credentials`, username `andprof`, password/token do Docker Hub. Nenhuma senha do Docker Hub fica no Git.

## Pipeline
1. Checkout
2. Testes JUnit (`mvnw.cmd clean test`) + publicacao JUnit + relatorio JaCoCo
3. Analise estatica PMD
4. Package
5. Docker build
6. Push `andprof/jogo-enigma-api:BUILD_NUMBER` e `latest`
7. HOMOL faz pull da imagem e cria PostgreSQL, pgAdmin, API, BFF, Prometheus e Grafana
8. Health Check da API
9. Cypress E2E em HOMOL
10. Homologacao aprovada; Production propositalmente nao implementado

## Fluxo didatico
`Checkout -> JUnit -> JaCoCo/PMD -> Package -> Docker Build -> Docker Hub -> HOMOL -> Health Check -> Cypress -> Aprovacao`

## Portas
API 8080; BFF/front 3000; pgAdmin 5050; Prometheus 9090; Grafana 3001.

## Observacao didatica
As credenciais simples de PostgreSQL/Grafana sao apenas para ambiente didatico local/homologacao. Docker Hub usa Jenkins Credentials. Em evolucao futura, migrar demais secrets para Jenkins/secret manager e adicionar deploy de Production somente apos gate de aprovacao.


## Versão A2 - Jenkins CI/CD

Esta versão foi ajustada para a prática de Pipeline as Code.

### Correção principal do container
O Dockerfile passou a ser multi-stage. O próprio Docker compila o JAR em uma imagem Maven e copia o artefato para a imagem Java de execução. Assim, o `docker build` não depende de um `target/*.jar` previamente existente no workspace do Jenkins.

### Homologação local
O `docker-compose.homol.yml` agora possui `build:` para API e BFF. O pipeline consegue construir e subir o ambiente local de homologação sem exigir Docker Hub. O push ao registry continua disponível como etapa opcional.

### Portas
- API: 8080
- BFF/Front: 3000
- Grafana: 3001
- pgAdmin: 5050
- Prometheus: 9090

> Para esta prática, execute o Jenkins em outra porta, por exemplo 8180, para não conflitar com a API.
