package com.uerj.gymflow.dao;

import com.uerj.gymflow.database.DatabaseConnection;
import com.uerj.gymflow.model.Exercicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExercicioDAO {
    private static final Logger logger = LoggerFactory.getLogger(ExercicioDAO.class);
    private final DatabaseConnection dbConnection;

    public ExercicioDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public void inserir(Exercicio exercicio) {
        String sql = "INSERT INTO Exercicio (nome_exercicio, descricao, grupo_muscular) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, exercicio.getNomeExercicio());
            stmt.setString(2, exercicio.getDescricao());
            stmt.setString(3, exercicio.getGrupoMuscular());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        exercicio.setIdExercicio(rs.getInt(1));
                        logger.info("Exercício inserido com sucesso: ID {}", exercicio.getIdExercicio());
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao inserir exercício", e);
            throw new RuntimeException("Erro ao inserir exercício", e);
        }
    }

    public Exercicio buscarPorId(Integer id) {
        String sql = "SELECT id_exercicio, nome_exercicio, descricao, grupo_muscular FROM Exercicio WHERE id_exercicio = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairExercicioDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar exercício por ID", e);
            throw new RuntimeException("Erro ao buscar exercício", e);
        }
        
        return null;
    }

    public List<Exercicio> listarTodos() {
        List<Exercicio> exercicios = new ArrayList<>();
        String sql = "SELECT id_exercicio, nome_exercicio, descricao, grupo_muscular FROM Exercicio";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                exercicios.add(extrairExercicioDoResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar exercícios", e);
            throw new RuntimeException("Erro ao listar exercícios", e);
        }
        
        return exercicios;
    }

    public void atualizar(Exercicio exercicio) {
        String sql = "UPDATE Exercicio SET nome_exercicio = ?, descricao = ?, grupo_muscular = ? WHERE id_exercicio = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, exercicio.getNomeExercicio());
            stmt.setString(2, exercicio.getDescricao());
            stmt.setString(3, exercicio.getGrupoMuscular());
            stmt.setInt(4, exercicio.getIdExercicio());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Exercício atualizado com sucesso: ID {}", exercicio.getIdExercicio());
            } else {
                logger.warn("Nenhum exercício encontrado para atualização com ID {}", exercicio.getIdExercicio());
            }
        } catch (SQLException e) {
            logger.error("Erro ao atualizar exercício", e);
            throw new RuntimeException("Erro ao atualizar exercício", e);
        }
    }

    public void excluir(Integer id) {
        String sql = "DELETE FROM Exercicio WHERE id_exercicio = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Exercício excluído com sucesso: ID {}", id);
            } else {
                logger.warn("Nenhum exercício encontrado para exclusão com ID {}", id);
            }
        } catch (SQLException e) {
            logger.error("Erro ao excluir exercício", e);
            throw new RuntimeException("Erro ao excluir exercício", e);
        }
    }

    public List<Exercicio> buscarPorTreino(Integer idTreino) {
        List<Exercicio> exercicios = new ArrayList<>();
        String sql = "SELECT e.id_exercicio, e.nome_exercicio, e.descricao, e.grupo_muscular " +
                     "FROM Exercicio e " +
                     "INNER JOIN Treino_exercicio te ON e.id_exercicio = te.id_exercicio " +
                     "WHERE te.id_treino = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTreino);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    exercicios.add(extrairExercicioDoResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar exercícios por treino", e);
            throw new RuntimeException("Erro ao buscar exercícios por treino", e);
        }
        
        return exercicios;
    }

    private Exercicio extrairExercicioDoResultSet(ResultSet rs) throws SQLException {
        Exercicio exercicio = new Exercicio();
        
        exercicio.setIdExercicio(rs.getInt("id_exercicio"));
        exercicio.setNomeExercicio(rs.getString("nome_exercicio"));
        exercicio.setDescricao(rs.getString("descricao"));
        exercicio.setGrupoMuscular(rs.getString("grupo_muscular"));
        
        return exercicio;
    }
}
