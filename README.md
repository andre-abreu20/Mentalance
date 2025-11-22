# Mentalance

Plataforma web de bem-estar emocional construída com Spring Boot e Thymeleaf. Permite que usuários realizem check-ins diários, acompanhem relatórios com gráficos e recebam insights gerados por um serviço de IA (OpenAI GPT-4o mini, com fallback mock configurável). Inclui painel administrativo com estatísticas gerais.

## Funcionalidades

- Registro e autenticação de usuários (Spring Security + BCrypt).
- Check-ins diários com dados de humor, energia, sono e contexto textual.
- Geração de análises com OpenAI (ou serviço mock quando `openai.enabled=false`).
- Painel do usuário com gráficos (Chart.js) e histórico de análises.
- Painel administrativo com visão geral do sistema.
- Internacionalização (`pt-BR` e `en-US`).
- Validação de formulários e tratamento centralizado de erros.

## Stack

- Java 17
- Spring Boot 3.5.4 (Web, Data JPA, Security, Validation, Thymeleaf)
- H2 (Ambiente de desenvolvimento) / Oracle (produção)
- Thymeleaf + Chart.js

## Executando o projeto

```bash
./mvnw spring-boot:run
```

Aplicação disponível em `http://localhost:8080`.

### Credenciais iniciais

- usuário: `admin`
- senha: `admin123`

Faça login, crie novos usuários pelo formulário de registro e explore o dashboard.

## Configuração do banco

Por padrão o projeto utiliza H2 em memória. Para usar Oracle, ajuste `application.properties`:

```
spring.datasource.url=jdbc:oracle:thin:@host:porta:sid
spring.datasource.username=USUARIO
spring.datasource.password=SENHA
spring.datasource.driverClassName=oracle.jdbc.OracleDriver
```

## Integração com OpenAI

1. Configure a variável de ambiente com o token da OpenAI:
   - PowerShell: `$env:OPENAI_API_KEY="SEU_TOKEN"`
   - macOS/Linux: `export OPENAI_API_KEY="SEU_TOKEN"`
2. Ative o cliente real:
   ```
   openai.enabled=true
   openai.model=gpt-4o-mini # opcional
   ```
   Esses parâmetros podem ficar em `application.properties`, `application-prod.properties` ou variáveis de ambiente (`OPENAI_ENABLED=true` etc.).
3. Desativando a flag (`openai.enabled=false`) o projeto usa o `MockIAFeedbackService`, útil para desenvolvimento offline.

Os insights ficam salvos na tabela `analises`, vinculados a cada check-in, e são exibidos no quadro “Insights de IA” do dashboard.

## Próximos passos sugeridos

- Persistir análises históricas em serviços externos ou data lake.
- Automatizar deploy (Azure Web Apps ou Render).
- Adicionar testes automatizados para serviços e controladores.

## Autores

- Nome do Autor 1 — RM XXXXX
- Nome do Autor 2 — RM XXXXX
- Nome do Autor 3 — RM XXXXX

Atualize a seção com os participantes do time.

