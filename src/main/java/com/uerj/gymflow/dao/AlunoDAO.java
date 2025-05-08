package com.uerj.gymflow.dao;

import com.uerj.gymflow.database.DatabaseConnection;
import com.uerj.gymflow.model.Aluno;
import com.uerj.gymflow.model.Pessoa;
import com.uerj.gymflow.model.Plano;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {
    private static final Logger logger = LoggerFactory.getLogger(AlunoDAO.class);
    private final DatabaseConnection dbConnection;
    private final PessoaDAO pessoaDAO;
    private final PlanoDAO planoDAO;

    public AlunoDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.pessoaDAO = new PessoaDAO();
        this.planoDAO = new PlanoDAO();
    }

    public void inserir(Aluno aluno) {
        String sqlAluno = "INSERT INTO Aluno (data_matricula, data_assinatura, fk_pessoa, fk_plano) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                pessoaDAO.inserir(aluno);
                
                if (aluno.getIdPessoa() == null) {
                    throw new SQLException("Falha ao obter ID da pessoa após inserção");
                }
                
                try (PreparedStatement stmtAluno = conn.prepareStatement(sqlAluno, Statement.RETURN_GENERATED_KEYS)) {
                    stmtAluno.setDate(1, Date.valueOf(aluno.getDataMatricula()));
                    
                    if (aluno.getDataAssinatura() != null) {
                        stmtAluno.setDate(2, Date.valueOf(aluno.getDataAssinatura()));
                    } else {
                        stmtAluno.setNull(2, Types.DATE);
                    }
                    
                    stmtAluno.setInt(3, aluno.getIdPessoa());
                    
                    if (aluno.getIdPlano() != null) {
                        stmtAluno.setInt(4, aluno.getIdPlano());
                    } else {
                        stmtAluno.setNull(4, Types.INTEGER);
                    }
                    
                    stmtAluno.executeUpdate();

                    try (ResultSet rs = stmtAluno.getGeneratedKeys()) {
                        if (rs.next()) {
                            aluno.setIdAluno(rs.getInt(1));
                        }
                    }
                }
                
                conn.commit();
                logger.info("Aluno inserido com sucesso: ID {}", aluno.getIdAluno());
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Erro ao inserir aluno", e);
                throw new RuntimeException("Erro ao inserir aluno", e);
            }
        } catch (SQLException e) {
            logger.error("Erro de conexão ao inserir aluno", e);
            throw new RuntimeException("Erro de conexão ao inserir aluno", e);
        }
    }

    public Aluno buscarPorId(Integer id) {
        String sql = "SELECT a.id_aluno, a.data_matricula, a.data_assinatura, a.fk_pessoa, a.fk_plano " +
                     "FROM Aluno a WHERE a.id_aluno = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarAluno(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar aluno por ID", e);
            throw new RuntimeException("Erro ao buscar aluno", e);
        }
        
        return null;
    }

    public List<Aluno> listarTodos() {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT a.id_aluno, a.data_matricula, a.data_assinatura, a.fk_pessoa, a.fk_plano FROM Aluno a";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Aluno aluno = montarAluno(rs);
                if (aluno != null) {
                    alunos.add(aluno);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar alunos", e);
            throw new RuntimeException("Erro ao listar alunos", e);
        }
        
        return alunos;
    }

    public void atualizar(Aluno aluno) {
        String sqlAluno = "UPDATE Aluno SET data_matricula = ?, data_assinatura = ?, fk_plano = ? WHERE id_aluno = ?";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                pessoaDAO.atualizar(aluno);
                
                try (PreparedStatement stmtAluno = conn.prepareStatement(sqlAluno)) {
                    stmtAluno.setDate(1, Date.valueOf(aluno.getDataMatricula()));
                    
                    if (aluno.getDataAssinatura() != null) {
                        stmtAluno.setDate(2, Date.valueOf(aluno.getDataAssinatura()));
                    } else {
                        stmtAluno.setNull(2, Types.DATE);
                    }
                    
                    if (aluno.getIdPlano() != null) {
                        stmtAluno.setInt(3, aluno.getIdPlano());
                    } else {
                        stmtAluno.setNull(3, Types.INTEGER);
                    }
                    
                    stmtAluno.setInt(4, aluno.getIdAluno());
                    stmtAluno.executeUpdate();
                }
                
                conn.commit();
                logger.info("Aluno atualizado com sucesso: ID {}", aluno.getIdAluno());
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Erro ao atualizar aluno", e);
                throw new RuntimeException("Erro ao atualizar aluno", e);
            }
        } catch (SQLException e) {
            logger.error("Erro de conexão ao atualizar aluno", e);
            throw new RuntimeException("Erro de conexão ao atualizar aluno", e);
        }
    }

    public void excluir(Integer id) {
        Aluno aluno = buscarPorId(id);
        if (aluno == null) {
            logger.warn("Nenhum aluno encontrado para exclusão com ID {}", id);
            return;
        }

        String sql = "DELETE FROM Aluno WHERE id_aluno = ?";
        
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    int linhasAfetadas = stmt.executeUpdate();
                    
                    if (linhasAfetadas == 0) {
                        throw new SQLException("Nenhum aluno encontrado para exclusão com ID " + id);
                    }
                }
                
                pessoaDAO.excluir(aluno.getIdPessoa());
                
                conn.commit();
                logger.info("Aluno excluído com sucesso: ID {}", id);
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Erro ao excluir aluno", e);
                throw new RuntimeException("Erro ao excluir aluno", e);
            }
        } catch (SQLException e) {
            logger.error("Erro de conexão ao excluir aluno", e);
            throw new RuntimeException("Erro de conexão ao excluir aluno", e);
        }
    }

    public Aluno buscarPorCpf(String cpf) {
        String sql = "SELECT a.id_aluno, a.data_matricula, a.data_assinatura, a.fk_pessoa, a.fk_plano " +
                     "FROM Aluno a INNER JOIN Pessoa p ON a.fk_pessoa = p.id_pessoa " +
                     "WHERE p.cpf = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cpf);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarAluno(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar aluno por CPF", e);
            throw new RuntimeException("Erro ao buscar aluno por CPF", e);
        }
        
        return null;
    }

    private Aluno montarAluno(ResultSet rs) throws SQLException {
        Integer idPessoa = rs.getInt("fk_pessoa");
        Pessoa pessoa = pessoaDAO.buscarPorId(idPessoa);
        
        if (pessoa == null) {
            logger.warn("Pessoa não encontrada para o ID {}", idPessoa);
            return null;
        }
        
        Aluno aluno = new Aluno();
        aluno.setIdAluno(rs.getInt("id_aluno"));
        
        if (rs.getDate("data_matricula") != null) {
            aluno.setDataMatricula(rs.getDate("data_matricula").toLocalDate());
        }
        
        if (rs.getDate("data_assinatura") != null) {
            aluno.setDataAssinatura(rs.getDate("data_assinatura").toLocalDate());
        }

        // Copia os dados da pessoa
        aluno.setIdPessoa(pessoa.getIdPessoa());
        aluno.setNome(pessoa.getNome());
        aluno.setDataNascimento(pessoa.getDataNascimento());
        aluno.setCpf(pessoa.getCpf());
        aluno.setTelefone(pessoa.getTelefone());
        aluno.setEmail(pessoa.getEmail());

        // Busca e configura o plano se existir
        Integer idPlano = rs.getInt("fk_plano");
        if (!rs.wasNull()) {
            aluno.setIdPlano(idPlano);
            Plano plano = planoDAO.buscarPorId(idPlano);
            if (plano != null) {
                aluno.setPlano(plano);
            }
        }
        
        return aluno;
    }
}
