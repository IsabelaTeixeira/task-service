# Task Service API

Uma API REST simples para gerenciamento de tarefas, construída com Spring Boot, MongoDB e Java, usando boas práticas como enums, validações e tratamento centralizado de erros.

---

## Tecnologias

- Java 17+
- Spring Boot 3+
- Spring Data MongoDB
- Lombok
- Docker
- Maven

---

## Estrutura da aplicação

- **domain** → entidades `Task`, enums `TaskStatus` e `TaskPriority`  
- **repository** → interface `TaskRepository` (MongoDB)  
- **service** → `TaskService` 
- **controller** → `TaskController` 
- **exception** → `ApiException` e `GlobalExceptionHandler`  

---

## Regras de negócio

- Status permitidos: `pending`, `in_progress`, `completed`, `cancelled`  
- Prioridade permitida: `low`, `medium`, `high`  
- Título obrigatório (mín. 3 / máx. 100 caracteres)  
- Status padrão: `pending`  
- Data de vencimento (`dueDate`) não pode estar no passado  
- Tarefas com status `completed` não podem ser editadas (apenas deletadas)  

---

## Endpoints

### 1. Criar Tarefa

**POST /tasks**

**Body:**
```
{
  "title": "Estudar Golang",
  "description": "Revisar conceitos de goroutines",
  "priority": "high",
  "status": "pending",
  "dueDate": "2026-03-05"
}
```
Regras para Criação de Tarefas

- **status**: opcional, padrão `pending`
- **priority**: obrigatória
- **dueDate**: não pode estar no passado

**Retorno:** `201 CREATED` com o objeto criado

## 2. Listar Tarefas

**GET /tasks** 

**Query Params (opcionais):**

- `status` → filtra pelo status (`pending`, `in_progress`, etc.)
- `priority` → filtra pela prioridade (`low`, `medium`, `high`)

**Exemplo:**
GET /tasks?status=pending&priority=high

- Retorna todas as tarefas que atendem ao filtro
- Se nenhum filtro for enviado → retorna todas

---

## 3. Buscar Tarefa por ID

**GET /tasks/{id}** 

- Retorna a tarefa correspondente
**Exemplo:**
```
{
	"id": "69a88c6db93a164f5648fb81",
	"title": "Estudar mat",
	"description": "Revisar newton",
	"status": "pending",
	"priority": "low",
	"dueDate": "2027-09-01",
	"createdAt": "2026-03-04T16:47:57.3884411",
	"updatedAt": "2026-03-04T16:47:57.3884411"
}
- Se não encontrada → retorna `404 NOT FOUND` com JSON:
```
{
  "timestamp": "2026-03-03T23:32:51",
  "status": 404,
  "error": "Task not found"
}

## 4. Atualizar Tarefa por ID

**PUT /tasks/{id}**
```
{
  "title": "Estudar Golang - Atualizado",
  "status": "in_progress"
}
```
- Não permite editar tarefas completed

**Retorna:**
```
{
	"id": "69a88c6db93a164f5648fb81",
	"title": "Estudar mat",
	"description": "Revisar newton",
	"status": "pending",
	"priority": "low",
	"dueDate": "2027-09-01",
	"createdAt": "2026-03-04T16:47:57.3884411",
	"updatedAt": "2026-03-04T16:47:57.3884411"
}
```
- Se não encontrada → retorna `404 NOT FOUND` com JSON:
```
{
  "timestamp": "2026-03-03T23:32:51",
  "status": 404,
  "error": "Task not found"
}
```
## 5. Deletar Tarefa por ID

**DELETE /tasks/{id}**

Remove a tarefa do banco

- Se não encontrada → retorna `404 NOT FOUND` com JSON:
```
{
  "timestamp": "2026-03-03T23:32:51",
  "status": 404,
  "error": "Task not found"
}
```

Retorna 201 OK se deletado

---

## Tratamento de erros

Erros da API são tratados com ApiException e interceptados pelo GlobalExceptionHandler, retornando JSON padronizado:
```
{
  "timestamp": "2026-03-03T23:32:51",
  "status": 400,
  "error": "Completed tasks cannot be edited"
}
```
