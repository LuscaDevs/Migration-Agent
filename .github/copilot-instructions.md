# Instruções do Projeto Migration-Agent

## Visão geral
Este projeto é uma aplicação Spring Boot que recebe webhooks do GitHub e processa comandos de migração dentro de comentários de issue.
A solução foi projetada como um agente de migração automátizada para repositórios Java hospedados no GitHub.

## Fluxo principal
1. O controlador `GithubWebhookController` expõe o endpoint `POST /api/github/webhook`.
2. `ProcessWebhookUseCase` resolve o tipo de evento GitHub e mapeia o payload JSON para DTOs específicos.
3. Os handlers em `webhook.application.handler` processam eventos suportados, atualmente `IssueCommentHandler` para `issue_comment`.
4. `IssueCommentHandler` usa `GithubCommandParser` para detectar comandos no formato `@migration-agent migrate javaXX`.
5. Quando o comando é válido, `CommandDispatcherService` encaminha o `MigrationCommand` para `JobDispatcherService`.
6. `JobDispatcherService` cria um `MigrationJob` e o executa assíncronamente em `MigrationJobExecutor`.

## Camadas e pacotes principais
- `com.luscadevs.migrationagent`
  - `MigrationagentApplication`: ponto de entrada Spring Boot.

- `webhook`
  - `controller`: `GithubWebhookController`
  - `application`: `ProcessWebhookUseCase`, `mapper`, handlers e DTOs de webhook
  - `dto`: modelos de payload GitHub
  - `domain`: classe `WebhookEvent`
  - `service`: integrações com webhook e event handlers

- `github`
  - `application.parser`: `GithubCommandParser` para extrair comandos de comentários.
  - `application.handler`: `IssueCommentHandler` implementa `GithubEventHandler`.
  - `application.service`: serviços de GitHub App (`GithubJwtService`, `GithubInstallationTokenService`), clonagem (`GithubCloneService`) e dispatcher.
  - `domain`: modelos de comando e contexto de execução.

- `orchestration`
  - `application.factory`: `JobFactory` cria instâncias de `MigrationJob`.
  - `application.service`: `JobDispatcherService`, `MigrationJobExecutor`, `ProjectAnalyzerService`.
  - `domain`: `MigrationJob`, `ProjectMetadata`, enums `BuildTool`, `JobStatus`, `JobType`.

- `rewrite`
  - `application`: `MigrationEngine` (interface de máquina de migração).
  - `infrastructure`: `OpenRewriteMigrationEngine`, `OpenRewriteEngineExecutor` usa OpenRewrite para migrar código.

- `shared.config`
  - `AsyncConfig`: habilita execução assíncrona de jobs com `@EnableAsync`.

## Comportamento de migração
- O executor de jobs clona o repositório Git usando JGit e token de instalação GitHub.
- `ProjectAnalyzerService` detecta:
  - ferramenta de build (`pom.xml` => Maven, `build.gradle`/`build.gradle.kts` => Gradle)
  - versão Java suportada no `pom.xml`
  - uso de Spring Boot
  - uso de JAX-RS
- `OpenRewriteMigrationEngine` aplica receitas OpenRewrite para atualização de Java.
- Atualmente, o projeto contém `org.openrewrite.java.migrate.UpgradeJavaVersion(21)`.

## Configuração necessária
- Java 25 compatível com o `pom.xml`.
- Variáveis de ambiente:
  - `GITHUB_APP_ID`
  - `GITHUB_PRIVATE_KEY_PATH`
- `src/main/resources/application.yaml` usa as variáveis acima.

## Comandos úteis
- Construir e testar: `./mvnw -B test`
- Executar a aplicação: `./mvnw spring-boot:run`
- Verificar toda a build: `./mvnw -B verify`

## Pontos importantes para desenvolvedores
- A aplicação não expõe persistência de jobs; o estado é mantido apenas em memória via `MigrationJob`.
- O processamento da migração é assíncrono e pode continuar após a resposta HTTP do webhook.
- Adições de novos comandos do GitHub devem incluir parser, handler e possivelmente um novo fluxo de `MigrationCommand`.
- Para suportar novos eventos GitHub, adicione `GithubEventType`, novo DTO e um `GithubEventHandler` apropriado.

## Arquitetura resumida
- Entrada HTTP -> Webhook -> Payload Mapper -> Evento -> Handler
- Handler -> Parser de comando -> Dispatcher -> Job
- Job -> Token GitHub + clone -> análise do projeto -> OSS Rewrite -> resultado de migração

## Observações
- O projeto foca em migração de código usando OpenRewrite e integração com GitHub App.
- O endpoint principal está em `/api/github/webhook` e espera o cabeçalho `X-GitHub-Event`.
