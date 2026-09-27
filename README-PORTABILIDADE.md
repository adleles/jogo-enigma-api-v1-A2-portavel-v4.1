# V4.1 portavel para laboratorio - Jenkins + Docker

Esta versao foi preparada para reduzir diferencas entre as maquinas dos alunos e corrigir o conflito observado entre Jenkins e a API na porta 8080.

## Pre-requisitos

1. Jenkins executando com **JDK 17 ou superior**.
2. Docker Desktop iniciado e acessivel pelo usuario/servico do Jenkins.
3. Git configurado para o checkout do repositorio.

**Nao e necessario instalar globalmente:** Maven, Node.js, npm ou Cypress.

## Correcoes da V4.1

- `mvnw.cmd` faz bootstrap do Maven 3.9.11 no primeiro build.
- Cypress executa em container (`cypress/included:14.5.4`).
- **API publicada em 8081 no host**, evitando conflito com Jenkins em 8080.
- A API continua usando **8080 dentro do container**; BFF, Prometheus e demais servicos internos continuam acessando `api:8080`.
- BFF/Front volta para 3000, pgAdmin 5050, Prometheus 9091 e Grafana 3001.
- Stage 0 verifica Java, Docker e Docker Compose.
- Antes do deploy, o pipeline verifica se as portas externas estao livres.
- Health Check aponta para `http://localhost:8081/actuator/health` por meio da variavel `API_PORT`.
- Em falha de health check, o pipeline mostra `docker compose ps` e os ultimos logs da API.

## Portas padrao da V4.1

| Servico | Host | Container |
|---|---:|---:|
| API | **8081** | 8080 |
| BFF/Front | 3000 | 3000 |
| pgAdmin | 5050 | 80 |
| Prometheus | 9091 | 9090 |
| Grafana | 3001 | 3000 |

A comunicacao interna nao muda. Exemplo: o BFF continua usando `http://api:8080`.

## Teste local antes do Jenkins

Na raiz do projeto:

```powershell
docker compose -f docker-compose.homol.yml up --build
```

Depois acesse:

- Aplicacao: `http://localhost:3000`
- API: `http://localhost:8081`
- Health: `http://localhost:8081/actuator/health`
- pgAdmin: `http://localhost:5050`
- Prometheus: `http://localhost:9091`
- Grafana: `http://localhost:3001`

Para encerrar:

```powershell
docker compose -f docker-compose.homol.yml down
```

## Teste pelo Jenkins

Com o Docker Desktop ativo, execute o job normalmente. Nao execute `docker compose up` manualmente ao mesmo tempo que o pipeline. O Stage 7 e responsavel pelo deploy de HOMOL.

## Se uma porta estiver ocupada

Altere somente a variavel correspondente no bloco `environment` do `Jenkinsfile`; nao altere a porta interna do container.

## Testcontainers

O teste de repositorio usa Testcontainers. Por isso o Docker precisa estar ativo desde o Stage 2.


## Correção V4.1 — bootstrap Maven no Jenkins

A V4.1 corrige a falha observada no Stage 2 da V4. O `mvnw.cmd` não chama `mvn` do PATH do Windows e não usa mais `Invoke-WebRequest`. Quando Maven 3.9.11 ainda não está no cache do usuário que executa Jenkins, o script usa `System.Net.WebClient` via PowerShell para baixar a distribuição oficial do Maven Central, extrai em `%USERPROFILE%\.m2\wrapper\dists` e executa `mvn.cmd` a partir dali.

Isso mantém o build independente do Maven global instalado/configurado na máquina. O JDK 17 ou superior continua sendo pré-requisito.
