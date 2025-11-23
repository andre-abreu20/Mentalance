# Mentalance

Plataforma web de bem-estar emocional construída com Spring Boot e Thymeleaf. Permite que usuários realizem check-ins diários, acompanhem relatórios com gráficos e recebam insights gerados por um serviço de IA (OpenAI GPT-4o mini, com fallback mock configurável). Inclui painel administrativo com estatísticas gerais.

## 🌐 Aplicação em Produção

**URL:** [https://mentalance-ddfkcqg9gffygpgz.brazilsouth-01.azurewebsites.net/](https://mentalance-ddfkcqg9gffygpgz.brazilsouth-01.azurewebsites.net/)

A aplicação está hospedada no **Azure Web App** (região: Brazil South) e está totalmente funcional. Você pode acessar e testar todas as funcionalidades, incluindo:

- ✅ Registro e login de usuários
- ✅ Check-ins diários com análise de IA
- ✅ Dashboard com gráficos e estatísticas
- ✅ Painel administrativo
- ✅ Internacionalização (PT-BR e EN-US)
- ✅ Mensageria assíncrona (RabbitMQ)
- ✅ Cache para otimização de performance
- ✅ Paginação em todas as listagens

**Credenciais de teste:**
- Usuário: `admin`
- Senha: `admin123`

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
| Cache | Caffeine (Spring Cache) |
| Paginação | Spring Data JPA Pageable |
| Build | Maven |

## Guia de Instalação e Execução

### Pré-requisitos

- **JDK 17+** (recomendado: OpenJDK 17 ou Oracle JDK 17)
- **Maven 3.9+** (ou use o wrapper `./mvnw` incluído no projeto)
- **Git** (para clonar o repositório)
- **Banco de dados** (H2 para desenvolvimento, Oracle para produção - opcional)

### 1. Clonar o Repositório

```bash
git clone https://github.com/andre-abreu20/Mentalance.git
cd Mentalance
```

### 2. Configurar Banco de Dados

#### Desenvolvimento (H2 - padrão)

O H2 está configurado por padrão em `application.properties`. Não é necessária configuração adicional.

#### Produção (Oracle - opcional)

Para usar Oracle em produção, configure as variáveis de ambiente ou crie um `application-prod.properties`:

```properties
spring.datasource.url=jdbc:oracle:thin:@host:porta/sid
spring.datasource.username=USUARIO
spring.datasource.password=SENHA
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
```

### 3. Configurar OpenAI (Opcional)

#### Opção A: Variáveis de Ambiente (Recomendado)

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="sk-..."
$env:OPENAI_ENABLED="true"
```

**Linux/macOS (Bash):**
```bash
export OPENAI_API_KEY="sk-..."
export OPENAI_ENABLED="true"
```

#### Opção B: application.properties

```properties
openai.enabled=true
openai.model=gpt-4o-mini
```

**Nota:** Se `OPENAI_API_KEY` não estiver configurada ou `openai.enabled=false`, a aplicação usará automaticamente o `MockIAFeedbackService` para gerar análises básicas.

### 4. Configurar RabbitMQ (Opcional)

Veja a seção [Integração com RabbitMQ](#integração-com-rabbitmq) abaixo para instruções detalhadas.

### 5. Executar Localmente

```bash
# Usando Maven Wrapper (recomendado)
./mvnw spring-boot:run

# Ou usando Maven instalado
mvn spring-boot:run
```

A aplicação estará disponível em: **http://localhost:8080**

### 6. Credenciais Padrão

- **Usuário:** `admin`
- **Senha:** `admin123`

### 7. Build para Deploy

```bash
# Build sem executar testes (útil para deploy)
mvn clean package -DskipTests

# Build executando testes
mvn clean package
```

O artefato gerado será: `target/mentalance-0.0.1-SNAPSHOT.jar`

### 8. Deploy no Azure Web App

1. **Criar Azure Web App** (Java 17, Linux ou Windows)
2. **Configurar variáveis de ambiente** no painel do Azure:
   - `OPENAI_API_KEY` (se quiser usar OpenAI real)
   - `OPENAI_ENABLED=true` (ou `false` para usar mock)
   - `spring.datasource.*` (se usar Oracle)
   - `RABBITMQ_HOST`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`, etc. (se usar RabbitMQ)
3. **Fazer deploy** via:
   - Azure CLI: `az webapp deploy --resource-group <grupo> --name <app-name> --src-path target/mentalance-0.0.1-SNAPSHOT.jar`
   - GitHub Actions (já configurado no projeto)
   - Portal do Azure (upload manual)

## Integração com OpenAI

### Como Funciona

A aplicação suporta dois modos de geração de análises:

1. **OpenAI Real** (quando `openai.enabled=true` e `OPENAI_API_KEY` configurada)
   - Usa o modelo GPT-4o mini da OpenAI
   - Gera análises personalizadas baseadas no contexto do check-in
   - Logs: `Análise de IA gerada usando OpenAI (modelo real): checkinId=X, usuarioId=Y, modelo=gpt-4o-mini`

2. **Serviço Mock** (quando `openai.enabled=false` ou `OPENAI_API_KEY` não configurada)
   - Usa respostas pré-definidas baseadas no humor do usuário
   - Útil para desenvolvimento offline ou quando não há API key
   - Logs: `Análise de IA gerada usando serviço MOCK (OpenAI não configurado): checkinId=X, usuarioId=Y`

### Configuração

1. **Obter API Key da OpenAI:**
   - Acesse [https://platform.openai.com/api-keys](https://platform.openai.com/api-keys)
   - Crie uma nova chave de API

2. **Configurar variável de ambiente:**
   ```bash
   # Windows (PowerShell)
   $env:OPENAI_API_KEY="sk-..."
   $env:OPENAI_ENABLED="true"
   
   # Linux/macOS (Bash)
   export OPENAI_API_KEY="sk-..."
   export OPENAI_ENABLED="true"
   ```

3. **Ou configurar no `application.properties`:**
   ```properties
   openai.enabled=true
   openai.model=gpt-4o-mini
   ```

### Verificar Qual Serviço Está Sendo Usado

Os logs da aplicação indicam qual serviço está ativo:

- **OpenAI Real:** `INFO ... OpenAiIAFeedbackService : Análise de IA gerada usando OpenAI (modelo real)`
- **Mock:** `INFO ... MockIAFeedbackService : Análise de IA gerada usando serviço MOCK`

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

## Cache e Performance

A aplicação utiliza **Caffeine Cache** para otimizar consultas frequentes:

- **Cache de check-ins recentes:** Lista de check-ins do usuário
- **Cache de resumo semanal:** Relatórios semanais
- **Cache de análises de IA:** Histórico de insights gerados
- **Cache de estatísticas globais:** Métricas do sistema

**Configuração padrão:**
- Tamanho máximo: 500 itens por cache
- Expiração: 10 minutos após escrita

## Paginação

Todas as listagens principais suportam paginação:

- **Dashboard:** Check-ins recentes paginados
- **Painel Admin:** Usuários e check-ins paginados
- **Histórico:** Check-ins do usuário paginados

A paginação é configurável via parâmetros de URL: `?page=0&size=10`

## Logs e Monitoramento

A aplicação gera logs informativos para facilitar o monitoramento:

- **Check-ins:** Logs quando um check-in é registrado
- **IA:** Logs indicando qual serviço de IA está sendo usado (OpenAI real ou Mock)
- **RabbitMQ:** Logs quando mensagens são enviadas para a fila
- **Erros:** Logs detalhados de exceções e falhas

## Estrutura do Projeto

```
Mentalance/
├── src/
│   ├── main/
│   │   ├── java/br/com/fiap/mentalance/
│   │   │   ├── config/          # Configurações (OpenAI, RabbitMQ, Cache, Security)
│   │   │   ├── controller/     # Controladores REST/Web
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── exception/      # Tratamento de exceções
│   │   │   ├── listener/       # Listeners RabbitMQ
│   │   │   ├── model/          # Entidades JPA
│   │   │   ├── repository/     # Repositórios Spring Data JPA
│   │   │   ├── security/      # Configuração Spring Security
│   │   │   └── service/        # Lógica de negócio
│   │   └── resources/
│   │       ├── i18n/           # Arquivos de internacionalização
│   │       ├── static/         # CSS, JS, imagens
│   │       └── templates/      # Templates Thymeleaf
│   └── test/                   # Testes unitários e de integração
├── pom.xml                      # Dependências Maven
└── README.md                    # Este arquivo
```

## Próximos Passos Sugeridos

- [ ] Adicionar testes automatizados (JUnit, Mockito)
- [ ] Implementar notificações por email
- [ ] Adicionar exportação de relatórios (PDF, Excel)
- [ ] Integração com APIs de saúde mental
- [ ] Dashboard de métricas avançadas
- [ ] Suporte a múltiplos idiomas adicionais

## Autores

| Nome | RM | GitHub |
|------|----|--------|
| André Luís Mesquita de Abreu | 558159 | [@andre-abreu20](https://github.com/andre-abreu20) |
| Maria Eduarda Brigidio | 558575 | [@dudabrigidio](https://github.com/dudabrigidio) |
| Rafael Bompadre Lima | 556459 | [@Rafa130206](https://github.com/Rafa130206) |

