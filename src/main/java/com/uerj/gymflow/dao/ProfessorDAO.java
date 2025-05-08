package com.uerj.gymflow.dao;

import com.uerj.gymflow.database.DatabaseConnection;
import com.uerj.gymflow.model.Funcionario;
import com.uerj.gymflow.model.Professor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessorDAO {
    private static final Logger logger = LoggerFactory.getLogger(ProfessorDAO.class);
    private final DatabaseConnection dbConnection;
    private final FuncionarioDAO funcionarioDAO;

    public ProfessorDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.funcionarioDAO = new FuncionarioDAO();
    }

    public void inserir(Professor professor) {
        String sqlProfessor = "INSERT INTO Professor (especialidade, cref, fk_funcionario) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                funcionarioDAO.inserir(professor);
                
                if (professor.getIdFuncionario() == null) {
                    throw new SQLException("Falha ao obter ID do funcionário após inserção");
                }
                
                try (PreparedStatement stmtProfessor = conn.prepareStatement(sqlProfessor, Statement.RETURN_GENERATED_KEYS)) {
                    stmtProfessor.setString(1, professor.getEspecialidade());
                    stmtProfessor.setString(2, professor.getCref());
                    stmtProfessor.setInt(3, professor.getIdFuncionario());
                    stmtProfessor.executeUpdate();

                    try (ResultSet rs = stmtProfessor.getGeneratedKeys()) {
                        if (rs.next()) {
                            professor.setIdProfessor(rs.getInt(1));
                        }
                    }
                }
                
                conn.commit();
                logger.info("Professor inserido com sucesso: ID {}", professor.getIdProfessor());
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Erro ao inserir professor", e);
                throw new RuntimeException("Erro ao inserir professor", e);
            }
        } catch (SQLException e) {
            logger.error("Erro de conexão ao inserir professor", e);
            throw new RuntimeException("Erro de conexão ao inserir professor", e);
        }
    }

    public Professor buscarPorId(Integer id) {
        String sql = "SELECT p.id_professor, p.especialidade, p.cref, p.fk_funcionario " +
                     "FROM Professor p WHERE p.id_professor = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Integer idFuncionario = rs.getInt("fk_funcionario");
                    
                    Funcionario funcionario = funcionarioDAO.buscarPorId(idFuncionario);
                    if (funcionario == null) {
                        return null;
                    }
                    
                    return criarProfessorDoResultSet(rs, funcionario);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar professor por ID", e);
            throw new RuntimeException("Erro ao buscar professor", e);
        }
        
        return null;
    }

    public List<Professor> listarTodos() {
        List<Professor> professores = new ArrayList<>();
        String sql = "SELECT p.id_professor, p.especialidade, p.cref, p.fk_funcionario FROM Professor p";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Integer idFuncionario = rs.getInt("fk_funcionario");
                Funcionario funcionario = funcionarioDAO.buscarPorId(idFuncionario);
                
                if (funcionario != null) {
                    professores.add(criarProfessorDoResultSet(rs, funcionario));
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar professores", e);
            throw new RuntimeException("Erro ao listar professores", e);
        }
        
        return professores;
    }

    public void atualizar(Professor professor) {
        String sqlProfessor = "UPDATE Professor SET especialidade = ?, cref = ? WHERE id_professor = ?";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                funcionarioDAO.atualizar(professor);
                
                try (PreparedStatement stmtProfessor = conn.prepareStatement(sqlProfessor)) {
                    stmtProfessor.setString(1, professor.getEspecialidade());
                    stmtProfessor.setString(2, professor.getCref());
                    stmtProfessor.setInt(3, professor.getIdProfessor());
                    stmtProfessor.executeUpdate();
                }
                
                conn.commit();
                logger.info("Professor atualizado com sucesso: ID {}", professor.getIdProfessor());
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Erro ao atualizar professor", e);
                throw new RuntimeException("Erro ao atualizar professor", e);
            }
        } catch (SQLException e) {
            logger.error("Erro de conexão ao atualizar professor", e);
            throw new RuntimeException("Erro de conexão ao atualizar professor", e);
        }
    }

    public void excluir(Integer id) {
        Professor professor = buscarPorId(id);
        if (professor == null) {
            logger.warn("Nenhum professor encontrado para exclusão com ID {}", id);
            return;
        }

        String sql = "DELETE FROM Professor WHERE id_professor = ?";
        
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    int linhasAfetadas = stmt.executeUpdate();
                    
                    if (linhasAfetadas == 0) {
                        throw new SQLException("Nenhum professor encontrado para exclusão com ID " + id);
                    }
                }
                
                funcionarioDAO.excluir(professor.getIdFuncionario());
                
                conn.commit();
                logger.info("Professor excluído com sucesso: ID {}", id);
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Erro ao excluir professor", e);
                throw new RuntimeException("Erro ao excluir professor", e);
            }
        } catch (SQLException e) {
            logger.error("Erro de conexão ao excluir professor", e);
            throw new RuntimeException("Erro de conexão ao excluir professor", e);
        }
    }

    public Professor buscarPorCref(String cref) {
        String sql = "SELECT p.id_professor, p.especialidade, p.cref, p.fk_funcionario " +
                     "FROM Professor p WHERE p.cref = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cref);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Integer idFuncionario = rs.getInt("fk_funcionario");
                    Funcionario funcionario = funcionarioDAO.buscarPorId(idFuncionario);
                    
                    if (funcionario != null) {
                        return criarProfessorDoResultSet(rs, funcionario);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar professor por CREF", e);
            throw new RuntimeException("Erro ao buscar professor por CREF", e);
        }
        
        return null;
    }

    private Professor criarProfessorDoResultSet(ResultSet rs, Funcionario funcionario) throws SQLException {
        Professor professor = new Professor();
        
        professor.setIdPessoa(funcionario.getIdPessoa());
        professor.setNome(funcionario.getNome());
        professor.setDataNascimento(funcionario.getDataNascimento());
        professor.setCpf(funcionario.getCpf());
        professor.setTelefone(funcionario.getTelefone());
        professor.setEmail(funcionario.getEmail());
        
        professor.setIdFuncionario(funcionario.getIdFuncionario());
        professor.setCargo(funcionario.getCargo());
        professor.setDataAdmissao(funcionario.getDataAdmissao());
        professor.setSalario(funcionario.getSalario());
        
        professor.setIdProfessor(rs.getInt("id_professor"));
        professor.setEspecialidade(rs.getString("especialidade"));
        professor.setCref(rs.getString("cref"));
        
        return professor;
    }
}
