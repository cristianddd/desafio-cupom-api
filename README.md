# Coupon Service - Desafio Técnico

Este projeto implementa um serviço REST de cupons de desconto utilizando **Java 17** e **Spring Boot 3.2**. A principal finalidade é demonstrar como aplicar princípios de **Arquitetura Hexagonal (Ports & Adapters)** e **Domain-Driven Design (DDD)** para construir uma aplicação limpa, testável e de baixo acoplamento.

## Visão Geral

O serviço expõe operações para:
* Criar um novo cupom.
* Consultar um cupom pelo identificador.
* Excluir um cupom (soft delete). A exclusão não remove o registro; apenas marca o cupom como deletado.

## Dicionário de Dados

Um cupom possui os seguintes campos obrigatórios:

| Campo | Descrição |
| :--- | :--- |
| `id` | Identificador único do cupom (gerado pelo sistema). |
| `code` | Código alfanumérico (apenas letras e números) com **tamanho exato de 6 caracteres**. |
| `description` | Texto descritivo do cupom. |
| `discountValue` | Valor monetário do desconto. Deve ser **>= 0,5** (não há máximo definido). |
| `expirationDate` | Data/hora de expiração (formato ISO-8601). Deve estar **no futuro**. |
| `published` | Booleano indicando se o cupom já está publicado. |
| `deleted` | Booleano indicando se o cupom foi deletado (soft delete). |

### Regras de Negócio

1.  **Sanitização:** O código é sanitizado removendo todos os caracteres não alfanuméricos e convertido para maiúsculas; o resultado deve ter exatamente 6 caracteres.
2.  **Valor Mínimo:** O valor do desconto (`discountValue`) deve ser maior ou igual a 0,5.
3.  **Expiração:** A data de expiração (`expirationDate`) deve estar no futuro (em relação ao momento da criação).
4.  **Exclusão Única:** Um cupom não pode ser excluído duas vezes.

---

## Arquitetura

O projeto está dividido em quatro camadas principais, de acordo com a Arquitetura Hexagonal:

### 1. Domínio (domain)
Contém as entidades e regras de negócio. A classe `Coupon` encapsula todos os campos e validações.

### 2. Aplicação (application)
Implementa os casos de uso (Use Cases) responsáveis por orquestrar as operações do domínio sem conhecer detalhes de infraestrutura.
* **Comando (Command):** Objeto de entrada (ex: `CreateCouponCommand`).
* **Saída (Output):** Objeto retornado (ex: `CreateCouponOutput`).
* **Interface:** Contrato que define o método `execute`.

Os casos de uso dependem apenas de interfaces (`CouponPort`) para interagir com a persistência.

### 3. Infraestrutura (infra)
Conecta a aplicação com frameworks externos (Banco de dados, JPA).
* `CouponJpaEntity`: Mapeamento para tabela no banco.
* `CouponRepository`: Interface Spring Data JPA.
* `CouponJpaPort`: Implementação da porta que traduz objetos de domínio para entidades JPA.

### 4. API (api)
Responsável por expor a API HTTP via Spring MVC (`CouponController`).

---

## API Endpoints

| Método | Rota | Descrição |
| :--- | :--- | :--- |
| **POST** | `/coupons` | Cria um novo cupom. Corpo: `CreateCouponRequest`  |
| **GET** | `/coupons/{id}` | Busca um cupom pelo identificador. |
| **DELETE** | `/coupons/{id}` | Marca o cupom como deletado (soft delete). |

### Documentação (Swagger)
O projeto utiliza `springdoc-openapi`. Após iniciar a aplicação, acesse:
* [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) 

---

## Persistência e Configuração

A aplicação utiliza **H2 Database** em memória.
* **Console H2:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console) 
* **JDBC URL:** `jdbc:h2:mem:coupondb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE` 
* **User:** `sa` 
* **Password:** (vazio) 

**Soft Delete:** O campo `deleted` na `CouponJpaEntity` indica se o cupom foi excluído. O caso de uso de exclusão muda essa flag para `true`, preservando o registro no banco.

---

## Como Executar

### Com Docker (Recomendado) e Maven instalado
Na raiz do projeto, execute:

```bash
mvn clean package
docker-compose up --build -d
```

---

# Testes

## Postman Collection

### A collection do Postman está disponível em:
postman/Coupon Service API - Full Test Collection.postman_collection.json

### Para importar:

1. Abra o Postman
2. Clique em Import
3. Selecione o arquivo