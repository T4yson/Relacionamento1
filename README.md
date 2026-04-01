# Relacionamento1 — API REST de Departamentos e Funcionários

> API RESTful desenvolvida com **Spring Boot** para gerenciar o relacionamento `OneToMany` entre departamentos e funcionários, com foco em boas práticas de arquitetura em camadas, validação de entrada e separação de responsabilidades via DTOs.

---

## 📑 Índice

- [Visão Geral](#-visão-geral)
- [Stack e Versões](#-stack-e-versões)
- [Arquitetura e Estrutura do Projeto](#-arquitetura-e-estrutura-do-projeto)
- [Modelo de Domínio](#-modelo-de-domínio)
- [Decisões Técnicas](#-decisões-técnicas)
- [DTOs e Validações](#-dtos-e-validações)
- [Endpoints](#️-endpoints)
- [Exemplos de Requisição](#-exemplos-de-requisição)
- [Regras de Negócio](#️-regras-de-negócio)
- [Consultas Derivadas do Repository](#-consultas-derivadas-do-repository)
- [Como Executar](#-como-executar)
- [Autor](#-autor)

---

## 🔎 Visão Geral

O projeto implementa a **Atividade 1** de um conjunto de exercícios sobre relacionamentos JPA. O cenário modela uma empresa onde:

- Um **Departamento** pode ter múltiplos **Funcionários** (1:N);
- Um **Funcionário** pertence a exatamente um **Departamento** (N:1);
- O `Funcionario` é o **lado dono** do relacionamento, carregando a chave estrangeira `departamento_id`.

---

## 🛠 Stack e Versões

| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.5 |
| Spring Data JPA | (gerenciado pelo Spring Boot) |
| H2 Database | (em memória, runtime) |
| Lombok | (gerenciado pelo Spring Boot) |
| Jakarta Bean Validation | (gerenciado pelo Spring Boot) |
| Spring Boot DevTools | (runtime, opcional) |
| Build | Maven Wrapper (`mvnw`) |

---

## 🗂️ Arquitetura e Estrutura do Projeto

O projeto segue uma **arquitetura em camadas clássica** (Controller → Service → Repository → Model), com isolamento de entrada/saída via DTOs:

```
Relacionamento2.0/
└── src/main/java/com/example/Relacionamento1/
    ├── Application.java                        # Ponto de entrada Spring Boot
    └── atividade1/
        ├── controller/
        │   ├── DepartamentoController.java     # Endpoints REST de departamento
        │   └── FuncionarioController.java      # Endpoints REST de funcionário
        ├── service/
        │   ├── DepartamentoService.java        # Regras de negócio de departamento
        │   └── FuncionarioService.java         # Regras de negócio de funcionário
        ├── repository/
        │   ├── DepartamentoRepository.java     # Acesso a dados de departamento
        │   └── FuncionarioRepository.java      # Acesso a dados + consultas derivadas
        ├── model/
        │   ├── Departamento.java               # Entidade JPA — lado inverso (1)
        │   └── Funcionario.java                # Entidade JPA — lado dono (N), porta FK
        └── dto/
            ├── departamento/
            │   ├── DepartamentoRequest.java
            │   ├── DepartamentoResponse.java
            │   └── DepartamentoDetalheResponse.java
            └── funcionario/
                ├── FuncionarioRequest.java
                ├── FuncionarioResponse.java
                └── FuncionarioResumoResponse.java
```

---

## 🔗 Modelo de Domínio

```
┌─────────────────────┐           ┌──────────────────────────┐
│     Departamento    │  1──────N │       Funcionario        │
│─────────────────────│           │──────────────────────────│
│ id   : Long (PK)    │           │ id            : Long (PK)│
│ nome : String       │◄──────────│ departamento  : Depart.  │
│ funcionarios : List │           │ nome          : String   │
│   (@OneToMany)      │           │ cargo         : String   │
│   cascade = ALL     │           │   (@ManyToOne LAZY)      │
└─────────────────────┘           │   FK: departamento_id    │
                                  └──────────────────────────┘
```

| Entidade | Anotação JPA | Papel |
|---|---|---|
| `Departamento` | `@OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL)` | Lado inverso — sem FK |
| `Funcionario` | `@ManyToOne(fetch = FetchType.LAZY)` + `@JoinColumn(name = "departamento_id", nullable = false)` | Lado dono — porta a FK |

---

## ⚙️ Decisões Técnicas

### 1. Java Records como DTOs
Todos os DTOs são implementados como `record`, garantindo **imutabilidade**, geração automática de `equals`, `hashCode` e `toString`, além de sintaxe concisa — alinhado ao paradigma moderno do Java 17+.

### 2. FetchType.LAZY no relacionamento ManyToOne
O carregamento do `Departamento` a partir do `Funcionario` é feito de forma **lazy**, evitando consultas desnecessárias ao banco em listagens que não precisam dos dados do departamento completos.

### 3. CascadeType.ALL no OneToMany
O cascade `ALL` no `Departamento` garante que operações de persistência (incluindo exclusão) sejam propagadas aos funcionários vinculados, simplificando o gerenciamento do ciclo de vida das entidades.

### 4. Injeção via construtor com `@RequiredArgsConstructor`
Todos os serviços e controllers utilizam injeção de dependência pelo construtor, gerada pelo Lombok via `@RequiredArgsConstructor`. Isso garante **imutabilidade das dependências** e facilita testes unitários.

### 5. Reaproveitamento entre Services
`FuncionarioService` delega para `DepartamentoService.findEntityById()` ao invés de acessar `DepartamentoRepository` diretamente. Isso centraliza a lógica de "departamento não encontrado" em um único ponto, seguindo o princípio DRY.

### 6. Banco H2 em Memória
A configuração padrão utiliza H2 sem nenhuma datasource explícita no `application.properties`, permitindo execução imediata sem infraestrutura externa. O schema é criado automaticamente pelo Hibernate (DDL auto).

### 7. Validação de entrada com Bean Validation
Campos obrigatórios nos `Request` DTOs são anotados com `@NotBlank` e `@NotNull`, e os controllers utilizam `@Valid` para ativar a validação automaticamente antes da chegada ao service.

### 8. Filtros combinados na listagem de funcionários
A lógica de filtragem no `FuncionarioService.listar()` suporta quatro cenários mutuamente exclusivos, resolvidos em cascata:
1. `id` + `nome` → busca exata combinada
2. `departamentoId` → filtra por departamento
3. `nome` → busca parcial case-insensitive
4. sem parâmetros → retorna todos

---

## 📦 DTOs e Validações

### Departamento

| DTO | Campos | Validações | Uso |
|---|---|---|---|
| `DepartamentoRequest` | `nome` | `@NotBlank` | Corpo do `POST` |
| `DepartamentoResponse` | `id`, `nome` | — | Listagem e resposta de criação |
| `DepartamentoDetalheResponse` | `id`, `nome`, `List<FuncionarioResumoResponse>` | — | `GET /{id}` — retorna funcionários aninhados |

### Funcionário

| DTO | Campos | Validações | Uso |
|---|---|---|---|
| `FuncionarioRequest` | `nome`, `cargo`, `departamentoId` | `@NotBlank` (nome, cargo), `@NotNull` (departamentoId) | Corpo do `POST` |
| `FuncionarioResponse` | `id`, `nome`, `cargo`, `departamentoId`, `departamentoNome` | — | Listagem e busca |
| `FuncionarioResumoResponse` | `id`, `nome`, `cargo` | — | Embutido em `DepartamentoDetalheResponse` |

---

## 🌐 Endpoints

### Departamentos — `/atividade1/departamentos`

| Método | Rota | Status de Sucesso | Descrição |
|---|---|---|---|
| `POST` | `/atividade1/departamentos` | `201 Created` | Cadastra um novo departamento |
| `GET` | `/atividade1/departamentos` | `200 OK` | Lista todos os departamentos |
| `GET` | `/atividade1/departamentos/{id}` | `200 OK` | Busca departamento por ID com lista de funcionários |

### Funcionários — `/atividade1/funcionarios`

| Método | Rota | Status de Sucesso | Descrição |
|---|---|---|---|
| `POST` | `/atividade1/funcionarios` | `201 Created` | Cadastra funcionário vinculado a um departamento |
| `GET` | `/atividade1/funcionarios` | `200 OK` | Lista funcionários (com filtros opcionais) |
| `GET` | `/atividade1/funcionarios/{id}` | `200 OK` | Busca funcionário por ID |

#### Query params — `GET /atividade1/funcionarios`

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `departamentoId` | `Long` | Filtra funcionários de um departamento específico |
| `nome` | `String` | Busca parcial no nome (case-insensitive) |
| `id` + `nome` | combinados | Busca simultânea por ID e nome |

---

## 📝 Exemplos de Requisição

### Cadastrar Departamento
```http
POST /atividade1/departamentos
Content-Type: application/json

{
  "nome": "Tecnologia"
}
```
```json
// 201 Created
{ "id": 1, "nome": "Tecnologia" }
```

### Cadastrar Funcionário
```http
POST /atividade1/funcionarios
Content-Type: application/json

{
  "nome": "João Silva",
  "cargo": "Desenvolvedor",
  "departamentoId": 1
}
```
```json
// 201 Created
{
  "id": 1,
  "nome": "João Silva",
  "cargo": "Desenvolvedor",
  "departamentoId": 1,
  "departamentoNome": "Tecnologia"
}
```

### Buscar departamento por ID (com funcionários)
```http
GET /atividade1/departamentos/1
```
```json
{
  "id": 1,
  "nome": "Tecnologia",
  "funcionarios": [
    { "id": 1, "nome": "João Silva", "cargo": "Desenvolvedor" }
  ]
}
```

### Filtros de funcionários
```http
GET /atividade1/funcionarios?departamentoId=1   # por departamento
GET /atividade1/funcionarios?nome=joão          # busca parcial
GET /atividade1/funcionarios?id=1&nome=joão     # id + nome combinados
GET /atividade1/funcionarios/{id}               # por ID via path
```

---

## ⚖️ Regras de Negócio

- Um funcionário **não pode ser cadastrado** sem informar um `departamentoId` válido.
- O `FuncionarioService` valida a existência do departamento (via `DepartamentoService.findEntityById`) **antes** de persistir o funcionário.
- A listagem de funcionários aplica filtros em ordem de prioridade: `id+nome` > `departamentoId` > `nome` > sem filtro (todos).
- Campos obrigatórios nos requests (`nome`, `cargo`, `departamentoId`) são validados automaticamente pela Bean Validation; requisições inválidas são rejeitadas antes de chegar ao service.

---

## 🔍 Consultas Derivadas do Repository

### `FuncionarioRepository`

```java
// Filtra todos os funcionários de um departamento
List<Funcionario> findByDepartamentoId(Long departamentoId);

// Busca parcial e case-insensitive pelo nome
List<Funcionario> findByNomeContainingIgnoreCase(String nome);

// Busca combinada por ID e nome (case-insensitive)
Optional<Funcionario> findByIdAndNomeContainingIgnoreCase(Long id, String nome);
```

O `DepartamentoRepository` herda apenas os métodos padrão de `JpaRepository`, pois não requer consultas customizadas.

---

## 🚀 Como Executar

**Pré-requisitos:** Java 21, Maven (ou use o wrapper incluso).

```bash
# Clone o repositório
git clone https://github.com/T4yson/Relacionamento1.git
cd Relacionamento1/Relacionamento2.0

# Execute com o Maven Wrapper (sem instalar Maven)
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

A API sobe na porta padrão **`8080`**. O banco H2 em memória é criado automaticamente na inicialização — nenhuma configuração adicional é necessária.

> Para acessar o console H2: `http://localhost:8080/h2-console`  
> JDBC URL: `jdbc:h2:mem:testdb` (padrão Spring Boot)

---

## 👤 Autor

**T4yson**

[![GitHub](https://img.shields.io/badge/GitHub-T4yson-181717?style=flat&logo=github)](https://github.com/T4yson)
