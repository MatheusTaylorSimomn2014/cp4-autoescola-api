# Checkpoint 4 -  Arquitetura Orientada a Serviços (SOA) e Web Services:
# API REST – Autoescola

---

## Nomes e RM dos Integrantes

- Matheus Taylor – RM556211
- Henrique Maldonado – RM557270
- Yuri Silveira – RM557475

### **Turma:** 3ESPH – Manhã  
###  **Professor:** Carlos Eduardo Machado de Oliveira

---

## Breve Descreção:

Projeto desenvolvido para os Checkpoints 3 e 4 da disciplina **Arquitetura Orientada a Serviços (SOA) e Web Services**.

## Funcionalidades Principais

### Instrutores 
- Cadastro completo (nome, e-mail, telefone, CNH, especialidade, endereço)
- Listagem paginada (10/pág) ordenada por nome
- Atualização (só nome, telefone e endereço)
- Exclusão lógica (soft delete)

### Alunos 
- Cadastro completo (nome, e-mail, telefone, CPF, endereço)
- Listagem paginada (10/pág) ordenada por nome
- Atualização (só nome, telefone e endereço)
- **Exclusão lógica (soft delete)** ← ajustado no CP4

### Usuários e Autenticação 
- Cadastro de usuários com senha criptografada (BCrypt)
- Login com geração de token **JWT**
- Apenas **ADMIN** pode cadastrar, listar, atualizar perfil e excluir usuários
- Qualquer usuário autenticado pode **alterar a própria senha**
- Todos os endpoints (exceto `/api/auth/login`) exigem token JWT válido

### Agendamento de Instruções 
- Cadastro de instrução (aluno, instrutor, data/hora)
- Regras:
  - Seg–Sáb, 06:00–21:00
  - Duração fixa de 1h
  - Antecedência mínima de 30 min
  - Aluno e instrutor devem estar ativos
  - Máx. 2 instruções/dia por aluno
  - Instrutor não pode ter conflito de horário
  - Instrutor é **opcional** (senão, escolha aleatória)
- **Cancelamento** com motivo obrigatório (`ALUNO_DESISTIU`, `INSTRUTOR_CANCELOU`, `OUTROS`) e antecedência mínima de 24h

## Estrutura Geral do Projeto:
```text
cp4-autoescola-api/
├── pom.xml                                
├── README.md                              
├── src/main/java/br/com/fiap/autoescola/
│   ├── AutoEscolaApplication.java
│   ├── config
│   │   ├── DataInitializer.java           
│   │   └── SecurityConfig.java            
│   ├── controller
│   │   ├── AlunoController.java
│   │   ├── AuthController.java            
│   │   ├── InstrutorController.java
│   │   ├── InstrucaoController.java      
│   │   └── UsuarioController.java         
│   ├── dto
│   │   ├── ... 
│   │   ├── AlterarSenhaDTO.java           
│   │   ├── InstrucaoCreateDTO.java        
│   │   ├── InstrucaoCancelamentoDTO.java  
│   │   ├── LoginDTO.java                  
│   │   ├── TokenDTO.java                  
│   │   ├── UsuarioCreateDTO.java          
│   │   └── UsuarioUpdateDTO.java          
│   ├── entity
│   │   ├── ... 
│   │   ├── Instrucao.java                 
│   │   ├── MotivoCancelamento.java        
│   │   ├── Role.java                      
│   │   ├── StatusInstrucao.java          
│   │   └── Usuario.java                   
│   ├── exception
│   │   ├── ... 
│   │   └── GlobalSecurityExceptionHandler.java 
│   ├── mapper
│   │   ├── ... 
│   │   ├── InstrucaoMapper.java           
│   │   └── UsuarioMapper.java             
│   ├── repository
│   │   ├── ... 
│   │   ├── InstrucaoRepository.java       
│   │   └── UsuarioRepository.java         
│   ├── security
│   │   ├── JwtAuthenticationFilter.java   
│   │   ├── JwtService.java                
│   │   └── UserDetailsServiceImpl.java    
│   ├── service
│   │   ├── ... 
│   │   ├── AuthService.java               
│   │   ├── InstrucaoService.java          
│   │   └── UsuarioService.java            
│   └── vo
│       ├── ... 
│       ├── InstrucaoVO.java               
│       └── UsuarioVO.java                 
└── src/main/resources
    ├── application.yml                    
    └── db/migration
        ├── V1__create_tables.sql          
        ├── V2__add_ativo_alunos.sql       
        ├── V3__create_usuarios.sql        
        └── V4__create_instrucoes.sql      
```

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.2.5
- Spring Security + JWT (jjwt)
- Spring Data JPA
- Flyway
- PostgreSQL
- Bean Validation
- Lombok

## Como executar

1. Crie o banco PostgreSQL `autoescola`.
2. Ajuste credenciais em `application.yml` se necessário.
3. Execute:
   ```bash
   mvn spring-boot:run
   ```
4. Usuário admin padrão (criado automaticamente): **admin / admin123** (altere após o primeiro login).

## Autenticação

1. `POST /api/auth/login` → retorna o token.
2. Envie `Authorization: Bearer <TOKEN>` nas demais requisições.

## Endpoints principais

| Método | Endpoint | Descrição | Perfil |
|--------|----------|-----------|--------|
| POST | `/api/auth/login` | Login | Público |
| POST | `/api/usuarios` | Cadastrar usuário | ADMIN |
| GET | `/api/usuarios` | Listar usuários | ADMIN |
| GET | `/api/usuarios/{id}` | Buscar usuário | ADMIN |
| PUT | `/api/usuarios/{id}/perfil` | Atualizar perfil | ADMIN |
| DELETE | `/api/usuarios/{id}` | Excluir usuário | ADMIN |
| PUT | `/api/usuarios/me/senha` | Alterar própria senha | Autenticado |
| POST | `/api/instrutores` | Cadastrar instrutor | Autenticado |
| GET | `/api/instrutores?page=0` | Listar instrutores | Autenticado |
| PUT | `/api/instrutores/{id}` | Atualizar instrutor | Autenticado |
| DELETE | `/api/instrutores/{id}` | Inativar instrutor | Autenticado |
| POST | `/api/alunos` | Cadastrar aluno | Autenticado |
| GET | `/api/alunos?page=0` | Listar alunos | Autenticado |
| PUT | `/api/alunos/{id}` | Atualizar aluno | Autenticado |
| DELETE | `/api/alunos/{id}` | Inativar aluno | Autenticado |
| POST | `/api/instrucoes` | Agendar instrução | Autenticado |
| GET | `/api/instrucoes` | Listar instruções | Autenticado |
| PUT | `/api/instrucoes/{id}/cancelamento` | Cancelar instrução | Autenticado |

## Exemplos de Requisição

### Cadastrar Instrutor
```json
{
  "nome": "João Silva",
  "email": "joao.silva@email.com",
  "telefone": "(11) 99999-9999",
  "cnh": "12345678900",
  "especialidade": "CARROS",
  "endereco": {
    "logradouro": "Rua das Flores",
    "numero": "123",
    "complemento": "Apto 45",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01001-000"
  }
}
```

### Atualizar Instrutor
```json
{
  "nome": "João Silva Santos",
  "telefone": "(11) 98888-8888",
  "endereco": {
    "logradouro": "Av. Paulista",
    "numero": "1000",
    "complemento": "Sala 10",
    "bairro": "Bela Vista",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01310-100"
  }
}
```

### Cadastrar Aluno
```json
{
  "nome": "Maria Oliveira",
  "email": "maria.oliveira@email.com",
  "telefone": "(11) 97777-7777",
  "cpf": "123.456.789-00",
  "endereco": {
    "logradouro": "Rua dos Pinheiros",
    "numero": "456",
    "bairro": "Pinheiros",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "05422-000"
  }
}
```

---

## Observações

- O campo `telefone` do instrutor não é exibido na listagem, conforme solicitado.
- A exclusão de instrutor é lógica (soft delete), mantendo os dados no banco com `ativo = false`.
- As migrações do banco são controladas pelo Flyway.

---