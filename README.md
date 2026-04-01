# Atividade 1 — Departamento e Funcionário

API REST desenvolvida com Spring Boot para gerenciar o relacionamento entre departamentos e funcionários.

---

## 📋 Cenário

Um **departamento** pode ter vários **funcionários**, mas um funcionário pertence a apenas um departamento. O `Funcionario` é o lado dono do relacionamento e carrega a chave estrangeira `departamento_id`.

---

## 🗂️ Estrutura do Projeto

```
atividade1/
├── controller/
│   ├── DepartamentoController.java
│   └── FuncionarioController.java
├── service/
│   ├── DepartamentoService.java
│   └── FuncionarioService.java
├── repository/
│   ├── DepartamentoRepository.java
│   └── FuncionarioRepository.java
├── model/
│   ├── Departamento.java
│   └── Funcionario.java
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

## 🔗 Relacionamento entre Entidades

```
Departamento (1) ────── (N) Funcionario
```

| Entidade | Papel no relacionamento |
|---|---|
| `Departamento` | Lado inverso — possui `@OneToMany(mappedBy = "departamento")` |
| `Funcionario` | Lado dono — possui `@ManyToOne` e a FK `departamento_id` |

---

## 📦 DTOs

### Departamento

| DTO | Campos | Uso |
|---|---|---|
| `DepartamentoRequest` | `nome` | Cadastro |
| `DepartamentoResponse` | `id`, `nome` | Listagem |
| `DepartamentoDetalheResponse` | `id`, `nome`, `List<FuncionarioResumoResponse>` | Busca por ID (desafio extra) |

### Funcionário

| DTO | Campos | Uso |
|---|---|---|
| `FuncionarioRequest` | `nome`, `cargo`, `departamentoId` | Cadastro |
| `FuncionarioResponse` | `id`, `nome`, `cargo`, `departamentoId`, `departamentoNome` | Listagem e busca |
| `FuncionarioResumoResponse` | `id`, `nome`, `cargo` | Usado dentro do detalhe do departamento |

---

## 🛠️ Endpoints

### Departamentos — `/atividade1/departamentos`

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/atividade1/departamentos` | Cadastra um novo departamento |
| `GET` | `/atividade1/departamentos` | Lista todos os departamentos |
| `GET` | `/atividade1/departamentos/{id}` | Busca departamento por ID (retorna com lista de funcionários) |

### Funcionários — `/atividade1/funcionarios`

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/atividade1/funcionarios` | Cadastra um novo funcionário vinculado a um departamento |
| `GET` | `/atividade1/funcionarios` | Lista funcionários (com filtros opcionais) |
| `GET` | `/atividade1/funcionarios/{id}` | Busca funcionário por ID |

#### Parâmetros de filtro — `GET /atividade1/funcionarios`

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `departamentoId` | `Long` | Filtra funcionários de um departamento específico |
| `nome` | `String` | Filtra pelo nome (busca parcial, case-insensitive) |
| `id` + `nome` | combinados | Busca um funcionário pelo ID e nome simultaneamente |

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

### Buscar funcionários por departamento
```http
GET /atividade1/funcionarios?departamentoId=1
```

### Buscar funcionários por nome
```http
GET /atividade1/funcionarios?nome=joão
```

### Buscar funcionário por ID + nome
```http
GET /atividade1/funcionarios?id=1&nome=joão
```

### Buscar departamento por ID (retorna funcionários)
```http
GET /atividade1/departamentos/1
```

**Resposta:**
```json
{
  "id": 1,
  "nome": "Tecnologia",
  "funcionarios": [
    {
      "id": 1,
      "nome": "João Silva",
      "cargo": "Desenvolvedor"
    }
  ]
}
```

---

## ⚙️ Regras de Negócio

- Não é possível cadastrar um funcionário sem informar um departamento válido.
- O service de `Funcionario` valida a existência do departamento antes de salvar.
- A listagem de funcionários suporta filtragem combinada: quando `id` e `nome` são informados juntos, ambos são aplicados simultaneamente.

---

## 🔍 Métodos do Repository

### `FuncionarioRepository`

```java
List<Funcionario> findByDepartamentoId(Long departamentoId);
List<Funcionario> findByNomeContainingIgnoreCase(String nome);
Optional<Funcionario> findByIdAndNomeContainingIgnoreCase(Long id, String nome);
```

---

## 🚀 Tecnologias Utilizadas

- Java 17+
- Spring Boot
- Spring Data JPA
- Lombok
- Jakarta Validation (Bean Validation)
- Banco de dados relacional (H2 / PostgreSQL / MySQL)
