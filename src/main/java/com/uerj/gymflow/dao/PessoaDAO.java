package com.uerj.gymflow.dao;

import com.uerj.gymflow.database.DatabaseConnection;
import com.uerj.gymflow.model.Pessoa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PessoaDAO {
    private static final Logger logger = LoggerFactory.getLogger(PessoaDAO.class);
    private final DatabaseConnection dbConnection;

    public PessoaDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public void inserir(Pessoa pessoa) {
        String sql = "INSERT INTO Pessoa (nome, data_nascimento, cpf, telefone, email) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, pessoa.getNome());
            stmt.setDate(2, Date.valueOf(pessoa.getDataNascimento()));
            stmt.setString(3, pessoa.getCpf());
            stmt.setString(4, pessoa.getTelefone());
            stmt.setString(5, pessoa.getEmail());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pessoa.setIdPessoa(rs.getInt(1));
                        logger.info("Pessoa inserida com sucesso: ID {}", pessoa.getIdPessoa());
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao inserir pessoa", e);
            throw new RuntimeException("Erro ao inserir pessoa", e);
        }
    }

    public Pessoa buscarPorId(Integer id) {
        String sql = "SELECT id_pessoa, nome, data_nascimento, cpf, telefone, email " +
                     "FROM Pessoa WHERE id_pessoa = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairPessoaDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar pessoa por ID", e);
            throw new RuntimeException("Erro ao buscar pessoa", e);
        }
        
        return null;
    }

    public Pessoa buscarPorCpf(String cpf) {
        String sql = "SELECT id_pessoa, nome, data_nascimento, cpf, telefone, email " +
                     "FROM Pessoa WHERE cpf = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cpf);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairPessoaDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar pessoa por CPF", e);
            throw new RuntimeException("Erro ao buscar pessoa por CPF", e);
        }
        
        return null;
    }

    public List<Pessoa> listarTodos() {
        List<Pessoa> pessoas = new ArrayList<>();
        String sql = "SELECT id_pessoa, nome, data_nascimento, cpf, telefone, email FROM Pessoa";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                pessoas.add(extrairPessoaDoResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar pessoas", e);
            throw new RuntimeException("Erro ao listar pessoas", e);
        }
        
        return pessoas;
    }

    public void atualizar(Pessoa pessoa) {
        String sql = "UPDATE Pessoa SET nome = ?, data_nascimento = ?, cpf = ?, " +
                     "telefone = ?, email = ? WHERE id_pessoa = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, pessoa.getNome());
            stmt.setDate(2, Date.valueOf(pessoa.getDataNascimento()));
            stmt.setString(3, pessoa.getCpf());
            stmt.setString(4, pessoa.getTelefone());
            stmt.setString(5, pessoa.getEmail());
            stmt.setInt(6, pessoa.getIdPessoa());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Pessoa atualizada com sucesso: ID {}", pessoa.getIdPessoa());
            } else {
                logger.warn("Nenhuma pessoa encontrada para atualização com ID {}", pessoa.getIdPessoa());
            }
        } catch (SQLException e) {
            logger.error("Erro ao atualizar pessoa", e);
            throw new RuntimeException("Erro ao atualizar pessoa", e);
        }
    }

    public void excluir(Integer id) {
        String sql = "DELETE FROM Pessoa WHERE id_pessoa = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Pessoa excluída com sucesso: ID {}", id);
            } else {
                logger.warn("Nenhuma pessoa encontrada para exclusão com ID {}", id);
            }
        } catch (SQLException e) {
            logger.error("Erro ao excluir pessoa", e);
            throw new RuntimeException("Erro ao excluir pessoa", e);
        }
    }

    public boolean verificarPessoaExistente(String cpf, String email) {
        String sql = "SELECT COUNT(*) FROM Pessoa WHERE cpf = ? OR email = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cpf);
            stmt.setString(2, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao verificar pessoa existente", e);
            throw new RuntimeException("Erro ao verificar pessoa existente", e);
        }
        
        return false;
    }

    private Pessoa extrairPessoaDoResultSet(ResultSet rs) throws SQLException {
        Pessoa pessoa = new Pessoa();
        
        pessoa.setIdPessoa(rs.getInt("id_pessoa"));
        pessoa.setNome(rs.getString("nome"));
        pessoa.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        pessoa.setCpf(rs.getString("cpf"));
        pessoa.setTelefone(rs.getString("telefone"));
        pessoa.setEmail(rs.getString("email"));
        
        return pessoa;
    }
}
