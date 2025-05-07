package com.uerj.gymflow.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseCreator {

    public static void main(String[] args) {
        String url = "jdbc:sqlite:gymflow.db";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            String sqlAluno = "CREATE TABLE Aluno (" +
                    "    id_aluno INTEGER PRIMARY KEY," +
                    "    data_matricula DATE," +
                    "    data_assinatura DATE," +
                    "    fk_pessoa INTEGER," +
                    "    fk_plano INTEGER," +
                    "    FOREIGN KEY (fk_pessoa) REFERENCES Pessoa(id_pessoa) ON DELETE CASCADE," +
                    "    FOREIGN KEY (fk_plano) REFERENCES Plano(id_plano)" +
                    ");";
            stmt.execute(sqlAluno);
            System.out.println("Tabela Aluno criada com sucesso.");

            String sqlFuncionario = "CREATE TABLE Funcionario (" +
                    "    id_funcionario INTEGER PRIMARY KEY," +
                    "    cargo VARCHAR," +
                    "    data_admissao DATE," +
                    "    salario FLOAT," +
                    "    fk_pessoa INTEGER," +
                    "    FOREIGN KEY (fk_pessoa) REFERENCES Pessoa(id_pessoa) ON DELETE CASCADE" +
                    ");";
            stmt.execute(sqlFuncionario);
            System.out.println("Tabela Funcionario criada com sucesso.");

            String sqlPessoa = "CREATE TABLE Pessoa (" +
                    "    id_pessoa INTEGER PRIMARY KEY," +
                    "    nome VARCHAR," +
                    "    data_nascimento DATE," +
                    "    cpf VARCHAR," +
                    "    telefone VARCHAR," +
                    "    email VARCHAR," +
                    "    UNIQUE (cpf, email)" +
                    ");";
            stmt.execute(sqlPessoa);
            System.out.println("Tabela Pessoa criada com sucesso.");

            String sqlPlano = "CREATE TABLE Plano (" +
                    "    id_plano INTEGER PRIMARY KEY," +
                    "    nome_plano VARCHAR UNIQUE," +
                    "    descricao VARCHAR," +
                    "    duracao INTEGER," +
                    "    valor_mensal FLOAT" +
                    ");";
            stmt.execute(sqlPlano);
            System.out.println("Tabela Plano criada com sucesso.");

            String sqlExercicio = "CREATE TABLE Exercicio (" +
                    "    id_exercicio INTEGER PRIMARY KEY," +
                    "    nome_exercicio VARCHAR UNIQUE," +
                    "    descricao VARCHAR," +
                    "    grupo_muscular VARCHAR" +
                    ");";
            stmt.execute(sqlExercicio);
            System.out.println("Tabela Exercicio criada com sucesso.");

            String sqlAvaliacao = "CREATE TABLE Avaliacao (" +
                    "    id_avaliacao INTEGER PRIMARY KEY," +
                    "    data_avaliacao DATE," +
                    "    peso FLOAT," +
                    "    altura FLOAT," +
                    "    percentual_gordura FLOAT," +
                    "    observacoes VARCHAR," +
                    "    fk_professor INTEGER," +
                    "    fk_aluno INTEGER," +
                    "    FOREIGN KEY (fk_professor) REFERENCES Professor(id_professor)," +
                    "    FOREIGN KEY (fk_aluno) REFERENCES Aluno(id_aluno)" +
                    ");";
            stmt.execute(sqlAvaliacao);
            System.out.println("Tabela Avaliacao criada com sucesso.");

            String sqlMensalidade = "CREATE TABLE Mensalidade (" +
                    "    id_mensalidade INTEGER PRIMARY KEY," +
                    "    data_vencimento DATE," +
                    "    data_pagamento DATE," +
                    "    valor_pago FLOAT," +
                    "    status_pagamento VARCHAR," +
                    "    fk_plano INTEGER," +
                    "    FOREIGN KEY (fk_plano) REFERENCES Plano(id_plano)" +
                    ");";
            stmt.execute(sqlMensalidade);
            System.out.println("Tabela Mensalidade criada com sucesso.");

            String sqlProfessor = "CREATE TABLE Professor (" +
                    "    id_professor INTEGER PRIMARY KEY," +
                    "    especialidade VARCHAR," +
                    "    cref VARCHAR," +
                    "    fk_funcionario INTEGER," +
                    "    FOREIGN KEY (fk_funcionario) REFERENCES Funcionario(id_funcionario)" +
                    ");";
            stmt.execute(sqlProfessor);
            System.out.println("Tabela Professor criada com sucesso.");

            String sqlTreino = "CREATE TABLE Treino (" +
                    "    id_treino INTEGER PRIMARY KEY," +
                    "    nome_treino VARCHAR UNIQUE," +
                    "    data_criacao DATE," +
                    "    observacoes VARCHAR," +
                    "    fk_professor INTEGER," +
                    "    FOREIGN KEY (fk_professor) REFERENCES Professor(id_professor)" +
                    ");";
            stmt.execute(sqlTreino);
            System.out.println("Tabela Treino criada com sucesso.");

            String sqlTreinoExercicio = "CREATE TABLE Treino_exercicio (" +
                    "    id_treino INTEGER," +
                    "    id_exercicio INTEGER," +
                    "    repeticoes INTEGER," +
                    "    series INTEGER," +
                    "    carga FLOAT," +
                    "    ordem INTEGER," +
                    "    observacoes VARCHAR," +
                    "    PRIMARY KEY (id_treino, id_exercicio)," +
                    "    FOREIGN KEY (id_treino) REFERENCES Treino(id_treino)," +
                    "    FOREIGN KEY (id_exercicio) REFERENCES Exercicio(id_exercicio)" +
                    ");";
            stmt.execute(sqlTreinoExercicio);
            System.out.println("Tabela Treino_exercicio criada com sucesso.");

            String sqlAlunoTreino = "CREATE TABLE Aluno_treino (" +
                    "    id_aluno INTEGER," +
                    "    id_treino INTEGER," +
                    "    data_inicio DATE," +
                    "    data_fim DATE," +
                    "    observacoes VARCHAR," +
                    "    PRIMARY KEY (id_aluno, id_treino)," +
                    "    FOREIGN KEY (id_aluno) REFERENCES Aluno(id_aluno)," +
                    "    FOREIGN KEY (id_treino) REFERENCES Treino(id_treino)" +
                    ");";
            stmt.execute(sqlAlunoTreino);
            System.out.println("Tabela Aluno_treino criada com sucesso.");


            System.out.println("Todas as tabelas criadas com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao criar as tabelas: " + e.getMessage());
        }
    }
}