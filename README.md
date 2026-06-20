# Sistema de Gestão de Eventos Comunitários (POO - P2)

Este projeto é a entrega da avaliação P2 da disciplina de Programação Orientada a Objetos. Trata-se de um sistema desktop em Java para gerenciar usuários, locais, eventos e inscrições de uma prefeitura comunitária.

## 🚀 Tecnologias Utilizadas
* **Linguagem:** Java 21
* **Interface Gráfica:** Java Swing (nativa)
* **Banco de Dados:** PostgreSQL 15 (via Docker)
* **Persistência:** JDBC puro (sem ORMs)
* **Gerenciador de Dependências:** Maven

## ⚙️ Instruções de Instalação e Execução

### Pré-requisitos
* Docker e Docker Compose instalados.
* JDK 21 (ou superior) configurado no `PATH`.
* Maven instalado (ou extensão do VS Code ativa).

### Passo a Passo
1. **Subir o Banco de Dados:**
   Na raiz do projeto, execute o comando para subir o container do PostgreSQL e do pgAdmin:
   ```bash
   docker-compose up -d

2. **Preparar as Tabelas:**

Acesse o banco de dados (via pgAdmin em http://localhost:8080 ou DBeaver) e execute o script ddl.sql fornecido na raiz do projeto para criar as tabelas e popular os dados iniciais obrigatórios (Locais e Recursos).

3. **Executar a Aplicação:**

Compile e execute a classe principal Main.java localizada em br.com.prefeitura.eventos.application.Main. 
O Dashboard do sistema será aberto.

### Regras de Negócio Implementadas
Para garantir a integridade do sistema, as seguintes regras foram desenvolvidas na camada de Service:

    Verificação de Lotação: Uma inscrição só é salva no banco se o número atual de inscritos for estritamente menor que a capacidade máxima do evento.

    Prevenção de Inscrição Duplicada: Um usuário não pode se inscrever duas vezes no mesmo evento (garantido via regra no Service e UNIQUE CONSTRAINT no banco de dados).

    Conflito de Horário em Locais: O sistema impede a criação de um novo evento caso o local selecionado já possua um evento ocorrendo no mesmo intervalo de datas e horários (intersecção de tempo).

### Decisões Arquiteturais
Padrão MVC / Camadas:
    Model(entidades java e enums) 
    View (Swing), 
    Service (Regras de Negócio),
    DAO (Acesso a Dados).

Tratamento de Exceções: Criação de exceções customizadas (DaoException e RegraNegocioException) para interceptar erros do backend e transformá-los em pop-ups amigáveis na View.