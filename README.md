# 🔐 Hackathon — Microserviço de Autenticação

[![Release - Build, Quality Gate and Deploy](https://github.com/FIAP-SOAT-G129/hackathon-auth-ms-fase5/actions/workflows/release.yml/badge.svg)](https://github.com/FIAP-SOAT-G129/hackathon-auth-ms-fase5/actions/workflows/release.yml)

![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

Este repositório implementa o **Microserviço de Autenticação**, desenvolvido em **Java 21 com Spring Boot 3**. Ele é responsável por gerenciar o registro de usuários, autenticação e geração de tokens JWT para acesso seguro aos demais microserviços da plataforma.

---

## 🧾 Objetivo do Projeto

Fornecer uma **API RESTful** robusta e segura para o gerenciamento de usuários e autenticação, incluindo funcionalidades de registro, login e validação de tokens JWT. Este serviço é a porta de entrada para a segurança da aplicação Fastfood, garantindo que apenas usuários autorizados possam interagir com os recursos protegidos.

> 📚 **Wiki do Projeto:** <br/> > https://github.com/FIAP-SOAT-G129/.github/wiki/Fase-5

---

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3**
- **Spring Security** (Autenticação e Autorização)
- **JWT (JSON Web Tokens)** (Geração e validação de tokens)
- **PostgreSQL** (Persistência de dados)
- **Maven** (Gerenciamento de dependências)
- **Docker & Docker Compose** (Containerização e orquestração)

---

## 🧩 Domínios Gerenciados

| Entidade | Descrição                                                                      |
|:---------|:-------------------------------------------------------------------------------|
| **User** | Informações do usuário, incluindo credenciais de acesso e perfis de segurança. |

---

## 🧠 Arquitetura

O serviço segue os princípios da **Arquitetura Hexagonal (Ports and Adapters)**, garantindo:

- Independência entre camadas
- Facilidade de manutenção e evolução
- Testabilidade e baixo acoplamento
- Separação entre regras de negócio e frameworks externos

Estrutura do projeto:

```
src/
 ├── main/
 │   ├── java/com/hackathon/
 │   │   ├── adapter/
 │   │   │   ├── in/                        # Adapters de entrada (Controllers, DTOs)
 │   │   │   │   ├── controller/
 │   │   │   │   └── dto/
 │   │   │   └── out/                       # Adapters de saída (JPA, Repositórios)
 │   │   │       ├── repository/
 │   │   │
 │   │   ├── application/
 │   │   │   └── usecase/                   # Casos de uso (regras de aplicação)
 │   │   │
 │   │   ├── config/                        # Configurações (Spring Security, JWT)
 │   │   │
 │   │   ├── domain/                        # Núcleo do domínio (regras puras de negócio)
 │   │   │   ├── entity/
 │   │   │   └── repository/                # Interfaces (ports)
 │   │   │
 │   │   └── AuthServiceApplication.java    # Classe principal da aplicação
 │   │
 │   └── resources/
 │       └── application.yml                # Configurações da aplicação
 │
 └── test/                                  # Testes unitários
```

---

## ⚙️ Como Rodar o Projeto

### ✅ Pré-requisitos
- `Java 21` (opcional, para rodar fora do container)
- `Maven` (opcional, para rodar fora do container)
- `Docker` (para rodar em container)
- `Docker Compose` (para orquestrar containers)

### 🔧 Configuração

A aplicação já vem configurada com valores padrão no `application.yml` para funcionar com o Docker Compose. Caso deseje alterar, as principais variáveis de ambiente são:

```env
APP_PORT=8080

DB_HOST=db
DB_PORT=5432
DB_NAME=auth_db
DB_USER=user
DB_PASSWORD=password

JWT_SECRET=q3s6v9y$B&E)H@McQfTjWnZr4u7x!A%C
JWT_ISSUER=hackathon-issuer
JWT_EXPIRATION=3600000
```

### 🐳 Executando o projeto com Docker Compose

No terminal, navegue até a raiz do projeto e execute:

```bash
docker-compose up --build
```

A aplicação estará disponível em: http://localhost:8080

Os serviços de infraestrutura estarão acessíveis em:
- **PostgreSQL:** `localhost:5432`

#### ⏹️ Parando os containers

Para parar e remover os containers, execute:

```bash
docker-compose down
```

---

## 🧪 Testes e Qualidade de Código

O projeto adota boas práticas de testes e qualidade de código, com foco em cobertura e comportamento previsível. Inclui testes de unidade utilizando:

- **JUnit 5**
- **Mockito**

### ▶️ Executando os testes

```bash
# Executar todos os testes
mvn test

# Executar testes com relatório de cobertura
mvn clean verify
```

---

## 👥 Equipe

Desenvolvido pela equipe **FIAP SOAT - G129** como parte do projeto de Arquitetura de Software.

---

## 📄 Licença

Este projeto é parte de um trabalho acadêmico da FIAP.
