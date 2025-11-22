# Mentalance

Plataforma web de bem-estar emocional construída com Spring Boot e Thymeleaf. Permite que usuários realizem check-ins diários, acompanhem relatórios com gráficos e recebam insights gerados por um serviço de IA (OpenAI GPT-4o mini, com fallback mock configurável). Inclui painel administrativo com estatísticas gerais.

## Funcionalidades

- Registro e autenticação de usuários (Spring Security + BCrypt).
- Check-ins diários com dados de humor, energia, sono e contexto textual.
- Geração de análises com OpenAI (ou serviço mock quando `openai.enabled=false`).
- **Mensageria assíncrona com RabbitMQ** para processamento de eventos de check-in.
- Painel do usuário com gráficos (Chart.js) e histórico de análises.
- Painel administrativo com visão geral do sistema.
- Internacionalização (`pt-BR` e `en-US`).
- Validação de formulários e tratamento centralizado de erros.

## Tecnologias Utilizadas

| Categoria | Tecnologias |
|-----------|-------------|
| Linguagem | Java 17 |
| Framework web | Spring Boot 3.5.4 (Web, Data JPA, Validation, Security, Thymeleaf) |
| Banco de dados | H2 (dev) / Oracle (prod) |
| Autenticação | Spring Security + BCrypt |
| Front-end | Thymeleaf, Bootstrap 5, Chart.js |
| IA generativa | OpenAI GPT-4o mini (client oficial `openai-java`) |
| Mensageria | RabbitMQ (Spring AMQP) |
| Build | Maven |

## Guia de Instalação e Execução

1. **Pré-requisitos**
   - JDK 17+
   - Maven 3.9+ (ou o wrapper `./mvnw`)
   - Git

2. **Clonar o repositório**
   ```bash
   git clone https://github.com/andre-abreu20/Mentalance.git
   cd Mentalance
   ```

3. **Variáveis essenciais**
   - Defina o datasource padrão (H2 já configurado em `application.properties`).
   - Para Oracle, crie um `application-prod.properties` ou defina:
     ```
     spring.datasource.url=jdbc:oracle:thin:@host:porta/sid
     spring.datasource.username=USUARIO
     spring.datasource.password=SENHA
     spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
     ```

4. **Configurar OpenAI (opcional)**
   - Exportar a chave:
     - PowerShell: `$env:OPENAI_API_KEY="SEU_TOKEN"`
     - Bash: `export OPENAI_API_KEY="SEU_TOKEN"`
   - Definir a flag:
     ```
     openai.enabled=true
     openai.model=gpt-4o-mini # opcional
     ```
   - Para desenvolvimento sem IA real, mantenha `openai.enabled=false`.

5. **Rodar em ambiente local**
   ```bash
   ./mvnw spring-boot:run
   # ou
   mvn spring-boot:run
   ```
   A aplicação sobe em `http://localhost:8080`.

6. **Credenciais padrão**
   - usuário: `admin`
   - senha: `admin123`

7. **Build para deploy**
   ```bash
   mvn clean package
   ```
   O artefato `target/mentalance-0.0.1-SNAPSHOT.jar` pode ser publicado em Azure Web App (Java 17 + Tomcat 10 ou container próprio). Lembre-se de configurar as variáveis de ambiente (`OPENAI_API_KEY`, `openai.enabled`, `spring.datasource.*`) no painel do Azure.

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

Os insights ficam salvos na tabela `analises`, vinculados a cada check-in, e são exibidos no quadro "Insights de IA" do dashboard.

## Integração com RabbitMQ

A aplicação envia mensagens assíncronas para o RabbitMQ sempre que um check-in é registrado. Isso permite processamento em background, integrações externas e escalabilidade.

### Opção A: CloudAMQP (Recomendado para produção)

1. **Criar conta no CloudAMQP** (free tier disponível):
   - Acesse [https://www.cloudamqp.com/](https://www.cloudamqp.com/)
   - Crie uma instância gratuita (Little Lemur)
   - Copie as credenciais de conexão

2. **Configurar variáveis de ambiente**:
   ```bash
   # PowerShell (Windows)
   $env:RABBITMQ_HOST="amqps://seu-host.cloudamqp.com"
   $env:RABBITMQ_PORT="5671"
   $env:RABBITMQ_USERNAME="seu-usuario"
   $env:RABBITMQ_PASSWORD="sua-senha"
   $env:RABBITMQ_VHOST="/"
   $env:RABBITMQ_SSL_ENABLED="true"
   
   # Bash (Linux/macOS)
   export RABBITMQ_HOST="amqps://seu-host.cloudamqp.com"
   export RABBITMQ_PORT="5671"
   export RABBITMQ_USERNAME="seu-usuario"
   export RABBITMQ_PASSWORD="sua-senha"
   export RABBITMQ_VHOST="/"
   export RABBITMQ_SSL_ENABLED="true"
   ```

3. **Ou configurar no `application.properties`** (não recomendado para produção):
   ```properties
   spring.rabbitmq.host=seu-host.cloudamqp.com
   spring.rabbitmq.port=5671
   spring.rabbitmq.username=seu-usuario
   spring.rabbitmq.password=sua-senha
   spring.rabbitmq.virtual-host=/
   spring.rabbitmq.ssl.enabled=true
   ```

### Opção B: RabbitMQ Local (Desenvolvimento)

1. **Instalar RabbitMQ localmente**:
   - Windows: Use Docker: `docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management`
   - Linux/macOS: `brew install rabbitmq` ou `apt-get install rabbitmq-server`

2. **Configuração padrão** (já configurada no `application.properties`):
   ```properties
   spring.rabbitmq.host=localhost
   spring.rabbitmq.port=5672
   spring.rabbitmq.username=guest
   spring.rabbitmq.password=guest
   ```

### Como funciona

- Quando um check-in é registrado, uma mensagem JSON é enviada para a fila `mentalance.checkin`.
- O `MessageProducer` envia a mensagem de forma assíncrona (não bloqueia o fluxo principal).
- O `CheckinMessageListener` (opcional) processa as mensagens para logs, notificações ou integrações.
- Se o RabbitMQ não estiver configurado, a aplicação funciona normalmente (mensageria é opcional).

### Estrutura da mensagem

```json
{
  "checkinId": 1,
  "usuarioId": 1,
  "usuarioNome": "João Silva",
  "usuarioEmail": "joao@example.com",
  "humor": "BEM",
  "energia": 7,
  "sono": 8,
  "contexto": "Dia produtivo",
  "data": "2025-01-22",
  "criadoEm": "2025-01-22T10:30:00",
  "analiseGerada": true,
  "modeloAnalise": "openai"
}
```

### Desabilitar RabbitMQ

Se não quiser usar mensageria, simplesmente não configure `spring.rabbitmq.host`. A aplicação funcionará normalmente sem enviar mensagens.

## Próximos passos sugeridos

- Persistir análises históricas em serviços externos ou data lake.
- Automatizar deploy (Azure Web Apps ou Render).
- Adicionar testes automatizados para serviços e controladores.

## Autores

| Nome | RM | GitHub |
|------|----|--------|
| André Luís Mesquita de Abreu | 558159 | [@andre-abreu20](https://github.com/andre-abreu20) |
| Maria Eduarda Brigidio | 558575 | [@dudabrigidio](https://github.com/dudabrigidio) |
| Rafael Bompadre Lima | 556459 | [@Rafa130206](https://github.com/Rafa130206) |

