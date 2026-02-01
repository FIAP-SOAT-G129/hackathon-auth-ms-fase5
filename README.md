# API Gateway com Kong + Auth Service (Java)

## 📌 Visão Geral

Este projeto demonstra a configuração e execução de um **API Gateway utilizando Kong**, integrado a um **Serviço de Autenticação em Java (Spring Boot)**, seguindo **Arquitetura Hexagonal**. A infraestrutura é orquestrada com **Docker Compose**.

---

## 🗂 Estrutura do Projeto

```bash
.
├── auth-service/        # Serviço de Autenticação (Spring Boot + Arquitetura Hexagonal)
├── kong/                # Configurações do Kong Gateway (kong.yml)
├── docker-compose.yml   # Orquestração dos containers
└── README.md
```

### Descrição dos componentes

* **auth-service/**: API responsável por cadastro, login e validação de usuários.
* **kong/**: Contém o arquivo `kong.yml` com rotas e plugins (JWT, Rate Limit, etc.).
* **docker-compose.yml**: Responsável por subir PostgreSQL, Kong Gateway e o Auth Service.

---

## ▶️ Como Executar o Projeto

### Pré-requisitos

* Docker
* Docker Compose

---

### Passo 1: Subir a Infraestrutura

Na raiz do projeto, execute:

```bash
docker compose up -d
```

Esse comando irá iniciar:

* PostgreSQL
* Kong Gateway
* Auth Service (Java)

---

### Passo 2: Verificar o Kong Gateway

O Kong carrega automaticamente as configurações definidas em `kong/kong.yml`.

Para validar se os serviços e rotas foram criados corretamente, acesse a **API de Administração do Kong**:

```
http://localhost:8001/services
```

---

## 🔐 Testando o Fluxo de Autenticação

### 1️⃣ Cadastro de Usuário

```bash
curl -X POST http://localhost:8000/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario1",
    "email": "user@test.com",
    "password": "senha123"
  }'
```

---

### 2️⃣ Login (Obter Token JWT)

```bash
curl -X POST http://localhost:8000/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario1",
    "password": "senha123"
  }'
```

A resposta retornará um **JWT**, que será utilizado para acessar rotas protegidas.

---

### 3️⃣ Acessar Rota Protegida

Substitua `<TOKEN_AQUI>` pelo token recebido no login:

```bash
curl -H "Authorization: Bearer <TOKEN_AQUI>" \
  http://localhost:8000/auth/me
```

---

## 🧠 Detalhes da Implementação

### 🧱 Arquitetura

* **Arquitetura Hexagonal (Ports and Adapters)**:

    * **Domínio**: regras de negócio
    * **Aplicação**: casos de uso
    * **Adaptadores**: Web (REST) e Persistência (Banco)

### 🔐 Segurança

* O **Kong valida o JWT** antes de encaminhar a requisição para o microserviço.
* O Auth Service recebe apenas requisições autenticadas.

### 🚦 Rate Limit

* Configurado no Kong
* Limite: **10 requisições por minuto** para o serviço de autenticação

---

## ✅ Observações Finais

* Todas as chamadas externas devem passar pelo **API Gateway (porta 8000)**.
* A API de administração do Kong fica disponível na **porta 8001**.
* Este projeto serve como base para arquiteturas de **microserviços com gateway centralizado**.
