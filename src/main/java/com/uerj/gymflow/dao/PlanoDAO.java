package com.uerj.gymflow.dao;

import com.uerj.gymflow.database.DatabaseConnection;
import com.uerj.gymflow.model.Plano;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanoDAO {
    private static final Logger logger = LoggerFactory.getLogger(PlanoDAO.class);
    private final DatabaseConnection dbConnection;

    public PlanoDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public void inserir(Plano plano) {
        String sql = "INSERT INTO Plano (nome_plano, descricao, duracao, valor_mensal) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, plano.getNomePlano());
            stmt.setString(2, plano.getDescricao());
            stmt.setInt(3, plano.getDuracao());
            stmt.setFloat(4, plano.getValorMensal());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        plano.setIdPlano(rs.getInt(1));
                        logger.info("Plano inserido com sucesso: ID {}", plano.getIdPlano());
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao inserir plano", e);
            throw new RuntimeException("Erro ao inserir plano", e);
        }
    }

    public Plano buscarPorId(Integer id) {
        String sql = "SELECT id_plano, nome_plano, descricao, duracao, valor_mensal FROM Plano WHERE id_plano = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairPlanoDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar plano por ID", e);
            throw new RuntimeException("Erro ao buscar plano", e);
        }
        
        return null;
    }

    public List<Plano> listarTodos() {
        List<Plano> planos = new ArrayList<>();
        String sql = "SELECT id_plano, nome_plano, descricao, duracao, valor_mensal FROM Plano";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                planos.add(extrairPlanoDoResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar planos", e);
            throw new RuntimeException("Erro ao listar planos", e);
        }
        
        return planos;
    }

    public void atualizar(Plano plano) {
        String sql = "UPDATE Plano SET nome_plano = ?, descricao = ?, duracao = ?, valor_mensal = ? WHERE id_plano = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, plano.getNomePlano());
            stmt.setString(2, plano.getDescricao());
            stmt.setInt(3, plano.getDuracao());
            stmt.setFloat(4, plano.getValorMensal());
            stmt.setInt(5, plano.getIdPlano());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Plano atualizado com sucesso: ID {}", plano.getIdPlano());
            } else {
                logger.warn("Nenhum plano encontrado para atualização com ID {}", plano.getIdPlano());
            }
        } catch (SQLException e) {
            logger.error("Erro ao atualizar plano", e);
            throw new RuntimeException("Erro ao atualizar plano", e);
        }
    }

    public void excluir(Integer id) {
        String sql = "DELETE FROM Plano WHERE id_plano = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Plano excluído com sucesso: ID {}", id);
            } else {
                logger.warn("Nenhum plano encontrado para exclusão com ID {}", id);
            }
        } catch (SQLException e) {
            logger.error("Erro ao excluir plano", e);
            throw new RuntimeException("Erro ao excluir plano", e);
        }
    }

    public boolean verificarNomePlanoExistente(String nomePlano) {
        String sql = "SELECT COUNT(*) FROM Plano WHERE nome_plano = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nomePlano);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao verificar nome de plano existente", e);
            throw new RuntimeException("Erro ao verificar nome de plano existente", e);
        }
        
        return false;
    }

    private Plano extrairPlanoDoResultSet(ResultSet rs) throws SQLException {
        Plano plano = new Plano();
        
        plano.setIdPlano(rs.getInt("id_plano"));
        plano.setNomePlano(rs.getString("nome_plano"));
        plano.setDescricao(rs.getString("descricao"));
        plano.setDuracao(rs.getInt("duracao"));
        plano.setValorMensal(rs.getFloat("valor_mensal"));
        
        return plano;
    }
}
