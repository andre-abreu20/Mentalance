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

## Tecnologias Utilizadas

| Categoria | Tecnologias |
|-----------|-------------|
| Linguagem | Java 17 |
| Framework web | Spring Boot 3.5.4 (Web, Data JPA, Validation, Security, Thymeleaf) |
| Banco de dados | H2 (dev) / Oracle (prod) |
| Autenticação | Spring Security + BCrypt |
| Front-end | Thymeleaf, Bootstrap 5, Chart.js |
| IA generativa | OpenAI GPT-4o mini (client oficial `openai-java`) |
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

Os insights ficam salvos na tabela `analises`, vinculados a cada check-in, e são exibidos no quadro “Insights de IA” do dashboard.

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

