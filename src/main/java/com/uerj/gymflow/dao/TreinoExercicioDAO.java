package com.uerj.gymflow.dao;

import com.uerj.gymflow.database.DatabaseConnection;
import com.uerj.gymflow.model.Exercicio;
import com.uerj.gymflow.model.TreinoExercicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreinoExercicioDAO {
    private static final Logger logger = LoggerFactory.getLogger(TreinoExercicioDAO.class);
    private final DatabaseConnection dbConnection;
    private final ExercicioDAO exercicioDAO;

    public TreinoExercicioDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.exercicioDAO = new ExercicioDAO();
    }

    public void inserir(TreinoExercicio treinoExercicio) {
        String sql = "INSERT INTO Treino_exercicio (id_treino, id_exercicio, repeticoes, series, carga, ordem, observacoes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, treinoExercicio.getIdTreino());
            stmt.setInt(2, treinoExercicio.getIdExercicio());
            
            if (treinoExercicio.getRepeticoes() != null) {
                stmt.setInt(3, treinoExercicio.getRepeticoes());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            if (treinoExercicio.getSeries() != null) {
                stmt.setInt(4, treinoExercicio.getSeries());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            if (treinoExercicio.getCarga() != null) {
                stmt.setFloat(5, treinoExercicio.getCarga());
            } else {
                stmt.setNull(5, Types.FLOAT);
            }
            
            if (treinoExercicio.getOrdem() != null) {
                stmt.setInt(6, treinoExercicio.getOrdem());
            } else {
                // Se não houver ordem definida, busca a próxima disponível
                Integer proximaOrdem = buscarProximaOrdem(treinoExercicio.getIdTreino());
                stmt.setInt(6, proximaOrdem);
            }
            
            stmt.setString(7, treinoExercicio.getObservacoes());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Relação treino-exercício inserida com sucesso");
            }
        } catch (SQLException e) {
            logger.error("Erro ao inserir relação treino-exercício", e);
            throw new RuntimeException("Erro ao inserir relação treino-exercício", e);
        }
    }

    private Integer buscarProximaOrdem(Integer idTreino) {
        String sql = "SELECT MAX(ordem) FROM Treino_exercicio WHERE id_treino = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTreino);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Integer maxOrdem = rs.getInt(1);
                    return rs.wasNull() ? 1 : maxOrdem + 1;
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar próxima ordem", e);
            throw new RuntimeException("Erro ao buscar próxima ordem", e);
        }
        
        return 1;
    }

    public TreinoExercicio buscar(Integer idTreino, Integer idExercicio) {
        String sql = "SELECT id_treino, id_exercicio, repeticoes, series, carga, ordem, observacoes " +
                     "FROM Treino_exercicio WHERE id_treino = ? AND id_exercicio = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTreino);
            stmt.setInt(2, idExercicio);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TreinoExercicio treinoExercicio = extrairTreinoExercicioDoResultSet(rs);
                    
                    // Carregar o objeto Exercicio
                    Exercicio exercicio = exercicioDAO.buscarPorId(idExercicio);
                    if (exercicio != null) {
                        treinoExercicio.setExercicio(exercicio);
                    }
                    
                    return treinoExercicio;
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar relação treino-exercício", e);
            throw new RuntimeException("Erro ao buscar relação treino-exercício", e);
        }
        
        return null;
    }

    public List<TreinoExercicio> listarPorTreino(Integer idTreino) {
        List<TreinoExercicio> relacoes = new ArrayList<>();
        String sql = "SELECT id_treino, id_exercicio, repeticoes, series, carga, ordem, observacoes " +
                     "FROM Treino_exercicio WHERE id_treino = ? ORDER BY ordem";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTreino);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TreinoExercicio treinoExercicio = extrairTreinoExercicioDoResultSet(rs);
                    
                    // Carregar o objeto Exercicio
                    Exercicio exercicio = exercicioDAO.buscarPorId(treinoExercicio.getIdExercicio());
                    if (exercicio != null) {
                        treinoExercicio.setExercicio(exercicio);
                    }
                    
                    relacoes.add(treinoExercicio);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar relações treino-exercício por treino", e);
            throw new RuntimeException("Erro ao listar relações treino-exercício por treino", e);
        }
        
        return relacoes;
    }

    public List<TreinoExercicio> listarPorExercicio(Integer idExercicio) {
        List<TreinoExercicio> relacoes = new ArrayList<>();
        String sql = "SELECT id_treino, id_exercicio, repeticoes, series, carga, ordem, observacoes " +
                     "FROM Treino_exercicio WHERE id_exercicio = ? ORDER BY id_treino, ordem";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idExercicio);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TreinoExercicio treinoExercicio = extrairTreinoExercicioDoResultSet(rs);
                    
                    // Carregar o objeto Exercicio
                    Exercicio exercicio = exercicioDAO.buscarPorId(idExercicio);
                    if (exercicio != null) {
                        treinoExercicio.setExercicio(exercicio);
                    }
                    
                    relacoes.add(treinoExercicio);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar relações treino-exercício por exercício", e);
            throw new RuntimeException("Erro ao listar relações treino-exercício por exercício", e);
        }
        
        return relacoes;
    }

    public void atualizar(TreinoExercicio treinoExercicio) {
        String sql = "UPDATE Treino_exercicio SET repeticoes = ?, series = ?, carga = ?, ordem = ?, observacoes = ? " +
                     "WHERE id_treino = ? AND id_exercicio = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (treinoExercicio.getRepeticoes() != null) {
                stmt.setInt(1, treinoExercicio.getRepeticoes());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            
            if (treinoExercicio.getSeries() != null) {
                stmt.setInt(2, treinoExercicio.getSeries());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            
            if (treinoExercicio.getCarga() != null) {
                stmt.setFloat(3, treinoExercicio.getCarga());
            } else {
                stmt.setNull(3, Types.FLOAT);
            }
            
            if (treinoExercicio.getOrdem() != null) {
                stmt.setInt(4, treinoExercicio.getOrdem());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setString(5, treinoExercicio.getObservacoes());
            stmt.setInt(6, treinoExercicio.getIdTreino());
            stmt.setInt(7, treinoExercicio.getIdExercicio());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Relação treino-exercício atualizada com sucesso");
            } else {
                logger.warn("Nenhuma relação treino-exercício encontrada para atualização");
            }
        } catch (SQLException e) {
            logger.error("Erro ao atualizar relação treino-exercício", e);
            throw new RuntimeException("Erro ao atualizar relação treino-exercício", e);
        }
    }

    public void excluir(Integer idTreino, Integer idExercicio) {
        String sql = "DELETE FROM Treino_exercicio WHERE id_treino = ? AND id_exercicio = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTreino);
            stmt.setInt(2, idExercicio);
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Relação treino-exercício excluída com sucesso");
                
                // Reordenar os exercícios restantes
                reordenarExercicios(idTreino);
            } else {
                logger.warn("Nenhuma relação treino-exercício encontrada para exclusão");
            }
        } catch (SQLException e) {
            logger.error("Erro ao excluir relação treino-exercício", e);
            throw new RuntimeException("Erro ao excluir relação treino-exercício", e);
        }
    }

    private void reordenarExercicios(Integer idTreino) {
        String sqlSelect = "SELECT id_exercicio, ordem FROM Treino_exercicio WHERE id_treino = ? ORDER BY ordem";
        String sqlUpdate = "UPDATE Treino_exercicio SET ordem = ? WHERE id_treino = ? AND id_exercicio = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect);
             PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
            
            conn.setAutoCommit(false);
            
            try {
                stmtSelect.setInt(1, idTreino);
                List<TreinoExercicio> exercicios = new ArrayList<>();
                
                try (ResultSet rs = stmtSelect.executeQuery()) {
                    while (rs.next()) {
                        TreinoExercicio te = new TreinoExercicio();
                        te.setIdTreino(idTreino);
                        te.setIdExercicio(rs.getInt("id_exercicio"));
                        te.setOrdem(rs.getInt("ordem"));
                        exercicios.add(te);
                    }
                }
                
                int novaOrdem = 1;
                for (TreinoExercicio te : exercicios) {
                    stmtUpdate.setInt(1, novaOrdem++);
                    stmtUpdate.setInt(2, idTreino);
                    stmtUpdate.setInt(3, te.getIdExercicio());
                    stmtUpdate.addBatch();
                }
                
                stmtUpdate.executeBatch();
                conn.commit();
                logger.info("Exercícios do treino ID {} reordenados com sucesso", idTreino);
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Erro ao reordenar exercícios do treino", e);
                throw new RuntimeException("Erro ao reordenar exercícios do treino", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.error("Erro de conexão ao reordenar exercícios do treino", e);
            throw new RuntimeException("Erro de conexão ao reordenar exercícios do treino", e);
        }
    }

    public void alterarOrdem(Integer idTreino, Integer idExercicio, Integer novaOrdem) {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Buscar a ordem atual do exercício
                Integer ordemAtual;
                String sqlBuscarOrdem = "SELECT ordem FROM Treino_exercicio WHERE id_treino = ? AND id_exercicio = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlBuscarOrdem)) {
                    stmt.setInt(1, idTreino);
                    stmt.setInt(2, idExercicio);
                    
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            ordemAtual = rs.getInt("ordem");
                        } else {
                            logger.warn("Exercício não encontrado no treino");
                            return;
                        }
                    }
                }
                
                // Ajustar as ordens dos outros exercícios
                String sqlAjustarOrdens;
                if (ordemAtual < novaOrdem) {
                    // Movendo para baixo: diminui a ordem dos que estão entre a ordem atual e a nova
                    sqlAjustarOrdens = "UPDATE Treino_exercicio SET ordem = ordem - 1 " +
                                       "WHERE id_treino = ? AND ordem > ? AND ordem <= ?";
                } else {
                    // Movendo para cima: aumenta a ordem dos que estão entre a nova ordem e a atual
                    sqlAjustarOrdens = "UPDATE Treino_exercicio SET ordem = ordem + 1 " +
                                       "WHERE id_treino = ? AND ordem >= ? AND ordem < ?";
                }
                
                try (PreparedStatement stmt = conn.prepareStatement(sqlAjustarOrdens)) {
                    stmt.setInt(1, idTreino);
                    stmt.setInt(2, ordemAtual < novaOrdem ? ordemAtual : novaOrdem);
                    stmt.setInt(3, ordemAtual < novaOrdem ? novaOrdem : ordemAtual);
                    stmt.executeUpdate();
                }
                
                // Atualizar a ordem do exercício específico
                String sqlAtualizarOrdem = "UPDATE Treino_exercicio SET ordem = ? WHERE id_treino = ? AND id_exercicio = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlAtualizarOrdem)) {
                    stmt.setInt(1, novaOrdem);
                    stmt.setInt(2, idTreino);
                    stmt.setInt(3, idExercicio);
                    stmt.executeUpdate();
                }
                
                conn.commit();
                logger.info("Ordem do exercício alterada com sucesso");
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Erro ao alterar ordem do exercício", e);
                throw new RuntimeException("Erro ao alterar ordem do exercício", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.error("Erro de conexão ao alterar ordem do exercício", e);
            throw new RuntimeException("Erro de conexão ao alterar ordem do exercício", e);
        }
    }

    private TreinoExercicio extrairTreinoExercicioDoResultSet(ResultSet rs) throws SQLException {
        TreinoExercicio treinoExercicio = new TreinoExercicio();
        
        treinoExercicio.setIdTreino(rs.getInt("id_treino"));
        treinoExercicio.setIdExercicio(rs.getInt("id_exercicio"));
        
        treinoExercicio.setRepeticoes(rs.getInt("repeticoes"));
        if (rs.wasNull()) treinoExercicio.setRepeticoes(null);
        
        treinoExercicio.setSeries(rs.getInt("series"));
        if (rs.wasNull()) treinoExercicio.setSeries(null);
        
        treinoExercicio.setCarga(rs.getFloat("carga"));
        if (rs.wasNull()) treinoExercicio.setCarga(null);
        
        treinoExercicio.setOrdem(rs.getInt("ordem"));
        if (rs.wasNull()) treinoExercicio.setOrdem(null);
        
        treinoExercicio.setObservacoes(rs.getString("observacoes"));
        
        return treinoExercicio;
    }
}
