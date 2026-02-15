# Coupon Service - Technical Challenge Manual

## Visão Geral

Este projeto implementa um serviço de cupons de desconto em Java usando Spring Boot 3.2.0.  
O objetivo é expor apenas as ações de **criação** e **deleção (soft delete)** de cupons, aplicando regras de negócio específicas. O foco do teste é demonstrar arquitetura limpa e testabilidade, não apenas fornecer um CRUD genérico.  
Os cupons possuem os seguintes campos obrigatórios:

- `code` – código alfanumérico com tamanho **exato** de 6 caracteres (caracteres especiais recebidos na requisição são ignorados).  
- `description` – descrição textual.  
- `discountValue` – valor monetário do desconto, mínimo 0,5 (sem máximo definido).  
- `expirationDate` – data/hora de expiração; nunca pode estar no passado.  
- `published` – flag indicando se o cupom já está publicado.  

Além disso, é possível excluir um cupom a qualquer momento, realizando **soft delete** (o cupom não é removido fisicamente do banco e não pode ser deletado duas vezes).

## Arquitetura

O projeto segue princípios de **arquitetura hexagonal (Ports & Adapters)** e de **Domain‑Driven Design (DDD)**, buscando separar regras de negócio de detalhes de infraestrutura. O código está organizado em quatro camadas principais:

### 1. Domínio (`domain`)

Contém a entidade `Coupon` e exceções de domínio. Toda validação de regras de negócio acontece aqui:

- **Código**: sanitiza a string recebida, removendo caracteres que não sejam letras ou números; o código final deve ter exatamente seis caracteres.  
- **Valor do desconto**: deve ser ≥ 0,5.  
- **Data de expiração**: deve ser posterior ao momento atual.  
- **Exclusão**: cupons não podem ser excluídos duas vezes; um método `delete()` lança exceção caso o cupom já esteja marcado como deletado.

Expor apenas construtores de fábrica (`newCoupon` e `with`) garante que a entidade seja sempre criada em um estado válido. A factory `with` é usada apenas para reconstruir objetos a partir do banco.

### 2. Application (`application`)

Implementa **casos de uso** (Use Cases) que orquestram o fluxo de criação e exclusão sem conhecer detalhes de infraestrutura. Cada caso de uso possui:

- um **comando** (`CreateCouponCommand`, `DeleteCouponCommand`) encapsulando os dados de entrada;  
- uma **saída** (`CreateCouponOutput`, `DeleteCouponOutput`) com os dados que precisam ser retornados;  
- uma interface (`CreateCouponUseCase`, `DeleteCouponUseCase`) e uma implementação (`CreateCouponService`, `DeleteCouponService`).

Os services recebem apenas portas (`CouponGateway`) como dependências, evitando acoplamento com frameworks. Toda regra de negócio permanece no domínio; os services apenas orquestram chamada das entidades e persistência.

### 3. Infraestrutura (`infra`)

Responsável por integrar o domínio com tecnologias externas.  
Aqui encontramos:

- **Entidade JPA** `CouponJpaEntity`: mapeamento da classe `Coupon` para a tabela `coupons`.  
- **Repositório Spring Data JPA** `CouponRepository`: interface para operações básicas de persistência.  
- **Gateway** `CouponJpaGateway`: implementação de `CouponGateway` que traduz entidades do domínio para JPA e vice‑versa.  

Também há uma classe de configuração (`ApplicationConfig`) que registra os casos de uso como beans Spring sem poluir a camada de aplicação com anotações do Spring.

### 4. Camada de API (`api`)

Exposta via `CouponController`, usando Spring MVC.  
Dois endpoints principais:

| Método | Rota            | Descrição                                                 |
|-------|-----------------|------------------------------------------------------------|
| POST  | `/coupons`      | Cria um novo cupom.  Corpo: `CreateCouponRequest`.        |
| DELETE| `/coupons/{id}` | Soft‑delete de um cupom pelo `id`.                         |

O controller converte DTOs em comandos e invoca os casos de uso. Exceções de domínio são mapeadas para códigos HTTP apropriados (400 para validações, 404 para não encontrado). A validação de payload é feita com Bean Validation.

### Swagger (OpenAPI)

A dependência `springdoc-openapi-starter-webmvc-ui` habilita a documentação automática da API.  
Após iniciar a aplicação, acesse `http://localhost:8080/swagger-ui.html` para explorar os endpoints e realizar requisições.

## Persistência (H2 In-Memory)

O banco em memória H2 simplifica a execução local e os testes. A configuração em `application.yml` utiliza:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:coupondb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password: ""
    driverClassName: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  h2:
    console:
      enabled: true
      path: /h2-console
```

### Soft delete

O campo `deleted` no `CouponJpaEntity` indica que o cupom foi excluído. O caso de uso de exclusão altera essa flag e atualiza o registro, evitando remoção física. Em produções futuras poderia ser aplicado um **@SQLDelete** ou filtros do Hibernate, mas para fins de teste preferiu‑se explicitamente atualizar o campo.

## Testes

A pasta `src/test/java` contém testes unitários utilizando JUnit 5 e Mockito.  
Os testes cobrem tanto as regras de negócio na camada de domínio (`CouponTest`) quanto os fluxos dos casos de uso (`CreateCouponUseCaseTest`, `DeleteCouponUseCaseTest`).  
A ideia é validar **comportamentos**, não detalhes de implementação. Exemplos:

- Garantir que um cupom com valor de desconto menor que 0,5 não seja criado.  
- Assegurar que códigos com caracteres especiais sejam sanitizados e respeitem o tamanho de 6 caracteres.  
- Verificar que não é possível excluir o mesmo cupom duas vezes.  
- Checar que o caso de uso de exclusão lança `NotFoundException` quando o cupom não existe.  

Para fins de medição, os testes cobrem a maior parte dos caminhos possíveis, aproximando-se de 80% de cobertura.

## Build e Execução

### Pré‑requisitos

- **Docker** (recomendado) ou Java 17 e Maven.  
- Opcionalmente `docker-compose` para subir a aplicação com um único comando.

### Utilizando Docker Compose

1. A partir da raiz do projeto (`coupon-service/`), execute:

   ```bash
   docker-compose build
   docker-compose up
   ```

   A aplicação irá expor a API na porta `8080` (`http://localhost:8080`) e a interface Swagger em `/swagger-ui.html`.

2. Para derrubar os containers:

   ```bash
   docker-compose down
   ```

### Compilação manual com Maven (caso tenha Maven instalado)

1. Execute `mvn clean package` na raiz do projeto (`coupon-service/`).  
2. O artefato será gerado em `target/coupon-service-0.0.1-SNAPSHOT.jar`.  
3. Inicie a aplicação:

   ```bash
   java -jar target/coupon-service-0.0.1-SNAPSHOT.jar
   ```

### Exemplos de uso

**Criação de cupom**

```
POST /coupons
Content-Type: application/json

{
  "code": "AB#CD12",
  "description": "10% de desconto no produto X",
  "discountValue": 10.0,
  "expirationDate": "2026-02-14T23:59:59",
  "published": true
}

Resposta (201 Created):
{
  "id": 1,
  "code": "ABCD12",
  "expirationDate": "2026-02-14T23:59:59"
}
```

**Exclusão de cupom**

```
DELETE /coupons/1
Resposta: 204 No Content
```

Se tentar excluir o mesmo cupom novamente, a resposta será 400 (Bad Request) com mensagem informando que o cupom já foi deletado.

## Considerações Finais

- A separação em camadas (domínio, aplicação, infra e API) facilita a manutenção, testes e eventuais mudanças de tecnologia.  
- Os casos de uso possuem **única responsabilidade** e dependem apenas de interfaces (princípio do porteiro), tornando o código mais flexível.  
- O projeto fornece configuração pronta para execução em memória com H2 e documentação via Swagger, atendendo os requisitos do desafio para nível pleno.  
- A documentação acima pode ser utilizada como guia em uma apresentação de code review, explicando decisões de design, regras de negócio e forma de execução.