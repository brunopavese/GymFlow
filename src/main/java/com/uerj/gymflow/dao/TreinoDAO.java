package com.uerj.gymflow.dao;

import com.uerj.gymflow.database.DatabaseConnection;
import com.uerj.gymflow.model.Exercicio;
import com.uerj.gymflow.model.Professor;
import com.uerj.gymflow.model.Treino;
import com.uerj.gymflow.model.TreinoExercicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreinoDAO {
    private static final Logger logger = LoggerFactory.getLogger(TreinoDAO.class);
    private final DatabaseConnection dbConnection;
    private final ProfessorDAO professorDAO;
    private final ExercicioDAO exercicioDAO;

    public TreinoDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.professorDAO = new ProfessorDAO();
        this.exercicioDAO = new ExercicioDAO();
    }

    public void inserir(Treino treino) {
        String sql = "INSERT INTO Treino (nome_treino, data_criacao, observacoes, fk_professor) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, treino.getNomeTreino());
            stmt.setDate(2, Date.valueOf(treino.getDataCriacao()));
            stmt.setString(3, treino.getObservacoes());
            
            if (treino.getIdProfessor() != null) {
                stmt.setInt(4, treino.getIdProfessor());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        treino.setIdTreino(rs.getInt(1));
                        logger.info("Treino inserido com sucesso: ID {}", treino.getIdTreino());
                        
                        // Se houver exercícios associados, insere os relacionamentos
                        if (treino.getExercicios() != null && !treino.getExercicios().isEmpty()) {
                            associarExercicios(treino);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao inserir treino", e);
            throw new RuntimeException("Erro ao inserir treino", e);
        }
    }

    public void associarExercicios(Treino treino) {
        String sql = "INSERT INTO Treino_exercicio (id_treino, id_exercicio, repeticoes, series, carga, ordem, observacoes) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            try {
                int ordem = 1;
                for (Exercicio exercicio : treino.getExercicios()) {
                    stmt.setInt(1, treino.getIdTreino());
                    stmt.setInt(2, exercicio.getIdExercicio());
                    stmt.setInt(3, 12); // Valores padrão para repeticoes
                    stmt.setInt(4, 3);  // Valores padrão para series
                    stmt.setFloat(5, 0.0f); // Valores padrão para carga
                    stmt.setInt(6, ordem++);
                    stmt.setString(7, "");
                    
                    stmt.addBatch();
                }
                
                stmt.executeBatch();
                conn.commit();
                logger.info("Exercícios associados ao treino ID {} com sucesso", treino.getIdTreino());
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Erro ao associar exercícios ao treino", e);
                throw new RuntimeException("Erro ao associar exercícios ao treino", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.error("Erro de conexão ao associar exercícios ao treino", e);
            throw new RuntimeException("Erro de conexão ao associar exercícios ao treino", e);
        }
    }

    public void associarExercicio(TreinoExercicio treinoExercicio) {
        String sql = "INSERT INTO Treino_exercicio (id_treino, id_exercicio, repeticoes, series, carga, ordem, observacoes) VALUES (?, ?, ?, ?, ?, ?, ?)";

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
                Integer proximaOrdem = buscarProximaOrdemExercicio(treinoExercicio.getIdTreino());
                stmt.setInt(6, proximaOrdem);
            }
            
            stmt.setString(7, treinoExercicio.getObservacoes());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Exercício associado ao treino com sucesso");
            }
        } catch (SQLException e) {
            logger.error("Erro ao associar exercício ao treino", e);
            throw new RuntimeException("Erro ao associar exercício ao treino", e);
        }
    }

    private Integer buscarProximaOrdemExercicio(Integer idTreino) {
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
            logger.error("Erro ao buscar próxima ordem de exercício", e);
            throw new RuntimeException("Erro ao buscar próxima ordem de exercício", e);
        }
        
        return 1;
    }

    public void atualizarTreinoExercicio(TreinoExercicio treinoExercicio) {
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

    public void removerExercicio(Integer idTreino, Integer idExercicio) {
        String sql = "DELETE FROM Treino_exercicio WHERE id_treino = ? AND id_exercicio = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTreino);
            stmt.setInt(2, idExercicio);
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Exercício removido do treino com sucesso");
                
                // Reordenar os exercícios restantes
                reordenarExercicios(idTreino);
            } else {
                logger.warn("Nenhum exercício encontrado para remoção do treino");
            }
        } catch (SQLException e) {
            logger.error("Erro ao remover exercício do treino", e);
            throw new RuntimeException("Erro ao remover exercício do treino", e);
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

    public Treino buscarPorId(Integer id) {
        String sql = "SELECT id_treino, nome_treino, data_criacao, observacoes, fk_professor " +
                     "FROM Treino WHERE id_treino = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Treino treino = extrairTreinoDoResultSet(rs);
                    
                    // Carregar os exercícios do treino
                    treino.setExercicios(exercicioDAO.buscarPorTreino(treino.getIdTreino()));
                    
                    return treino;
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar treino por ID", e);
            throw new RuntimeException("Erro ao buscar treino", e);
        }
        
        return null;
    }

    public Treino buscarPorNome(String nome) {
        String sql = "SELECT id_treino, nome_treino, data_criacao, observacoes, fk_professor " +
                     "FROM Treino WHERE nome_treino = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Treino treino = extrairTreinoDoResultSet(rs);
                    
                    // Carregar os exercícios do treino
                    treino.setExercicios(exercicioDAO.buscarPorTreino(treino.getIdTreino()));
                    
                    return treino;
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar treino por nome", e);
            throw new RuntimeException("Erro ao buscar treino por nome", e);
        }
        
        return null;
    }

    public List<Treino> listarTodos() {
        List<Treino> treinos = new ArrayList<>();
        String sql = "SELECT id_treino, nome_treino, data_criacao, observacoes, fk_professor FROM Treino";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Treino treino = extrairTreinoDoResultSet(rs);
                treinos.add(treino);
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar treinos", e);
            throw new RuntimeException("Erro ao listar treinos", e);
        }
        
        return treinos;
    }

    public List<Treino> buscarPorProfessor(Integer idProfessor) {
        List<Treino> treinos = new ArrayList<>();
        String sql = "SELECT id_treino, nome_treino, data_criacao, observacoes, fk_professor " +
                     "FROM Treino WHERE fk_professor = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idProfessor);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Treino treino = extrairTreinoDoResultSet(rs);
                    treinos.add(treino);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar treinos por professor", e);
            throw new RuntimeException("Erro ao buscar treinos por professor", e);
        }
        
        return treinos;
    }

    public void atualizar(Treino treino) {
        String sql = "UPDATE Treino SET nome_treino = ?, data_criacao = ?, observacoes = ?, fk_professor = ? " +
                     "WHERE id_treino = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, treino.getNomeTreino());
            stmt.setDate(2, Date.valueOf(treino.getDataCriacao()));
            stmt.setString(3, treino.getObservacoes());
            
            if (treino.getIdProfessor() != null) {
                stmt.setInt(4, treino.getIdProfessor());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setInt(5, treino.getIdTreino());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Treino atualizado com sucesso: ID {}", treino.getIdTreino());
            } else {
                logger.warn("Nenhum treino encontrado para atualização com ID {}", treino.getIdTreino());
            }
        } catch (SQLException e) {
            logger.error("Erro ao atualizar treino", e);
            throw new RuntimeException("Erro ao atualizar treino", e);
        }
    }

    public void excluir(Integer id) {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Primeiro exclui as relações na tabela Treino_exercicio
                String sqlTreinoExercicio = "DELETE FROM Treino_exercicio WHERE id_treino = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlTreinoExercicio)) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                
                // Depois exclui as relações na tabela Aluno_treino
                String sqlAlunoTreino = "DELETE FROM Aluno_treino WHERE id_treino = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlAlunoTreino)) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                
                // Por fim, exclui o treino
                String sqlTreino = "DELETE FROM Treino WHERE id_treino = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlTreino)) {
                    stmt.setInt(1, id);
                    int linhasAfetadas = stmt.executeUpdate();
                    
                    if (linhasAfetadas > 0) {
                        logger.info("Treino excluído com sucesso: ID {}", id);
                    } else {
                        logger.warn("Nenhum treino encontrado para exclusão com ID {}", id);
                    }
                }
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Erro ao excluir treino", e);
                throw new RuntimeException("Erro ao excluir treino", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.error("Erro de conexão ao excluir treino", e);
            throw new RuntimeException("Erro de conexão ao excluir treino", e);
        }
    }

    public List<TreinoExercicio> buscarDetalhesExercicios(Integer idTreino) {
        List<TreinoExercicio> detalhes = new ArrayList<>();
        String sql = "SELECT te.id_treino, te.id_exercicio, te.repeticoes, te.series, te.carga, te.ordem, te.observacoes, " +
                     "e.nome_exercicio, e.descricao, e.grupo_muscular " +
                     "FROM Treino_exercicio te " +
                     "JOIN Exercicio e ON te.id_exercicio = e.id_exercicio " +
                     "WHERE te.id_treino = ? " +
                     "ORDER BY te.ordem";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTreino);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TreinoExercicio te = new TreinoExercicio();
                    te.setIdTreino(rs.getInt("id_treino"));
                    te.setIdExercicio(rs.getInt("id_exercicio"));
                    te.setRepeticoes(rs.getInt("repeticoes"));
                    if (rs.wasNull()) te.setRepeticoes(null);
                    te.setSeries(rs.getInt("series"));
                    if (rs.wasNull()) te.setSeries(null);
                    te.setCarga(rs.getFloat("carga"));
                    if (rs.wasNull()) te.setCarga(null);
                    te.setOrdem(rs.getInt("ordem"));
                    if (rs.wasNull()) te.setOrdem(null);
                    te.setObservacoes(rs.getString("observacoes"));
                    
                    Exercicio exercicio = new Exercicio();
                    exercicio.setIdExercicio(rs.getInt("id_exercicio"));
                    exercicio.setNomeExercicio(rs.getString("nome_exercicio"));
                    exercicio.setDescricao(rs.getString("descricao"));
                    exercicio.setGrupoMuscular(rs.getString("grupo_muscular"));
                    
                    te.setExercicio(exercicio);
                    detalhes.add(te);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar detalhes dos exercícios do treino", e);
            throw new RuntimeException("Erro ao buscar detalhes dos exercícios do treino", e);
        }
        
        return detalhes;
    }

    public List<Treino> buscarPorAluno(Integer idAluno) {
        List<Treino> treinos = new ArrayList<>();
        String sql = "SELECT t.id_treino, t.nome_treino, t.data_criacao, t.observacoes, t.fk_professor " +
                     "FROM Treino t " +
                     "JOIN Aluno_treino at ON t.id_treino = at.id_treino " +
                     "WHERE at.id_aluno = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAluno);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Treino treino = extrairTreinoDoResultSet(rs);
                    treinos.add(treino);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar treinos por aluno", e);
            throw new RuntimeException("Erro ao buscar treinos por aluno", e);
        }
        
        return treinos;
    }

    public TreinoExercicio buscarDetalheExercicio(Integer idTreino, Integer idExercicio) {
        String sql = "SELECT te.id_treino, te.id_exercicio, te.repeticoes, te.series, te.carga, te.ordem, te.observacoes, " +
                     "e.nome_exercicio, e.descricao, e.grupo_muscular " +
                     "FROM Treino_exercicio te " +
                     "JOIN Exercicio e ON te.id_exercicio = e.id_exercicio " +
                     "WHERE te.id_treino = ? AND te.id_exercicio = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTreino);
            stmt.setInt(2, idExercicio);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TreinoExercicio te = new TreinoExercicio();
                    te.setIdTreino(rs.getInt("id_treino"));
                    te.setIdExercicio(rs.getInt("id_exercicio"));
                    te.setRepeticoes(rs.getInt("repeticoes"));
                    if (rs.wasNull()) te.setRepeticoes(null);
                    te.setSeries(rs.getInt("series"));
                    if (rs.wasNull()) te.setSeries(null);
                    te.setCarga(rs.getFloat("carga"));
                    if (rs.wasNull()) te.setCarga(null);
                    te.setOrdem(rs.getInt("ordem"));
                    if (rs.wasNull()) te.setOrdem(null);
                    te.setObservacoes(rs.getString("observacoes"));
                    
                    Exercicio exercicio = new Exercicio();
                    exercicio.setIdExercicio(rs.getInt("id_exercicio"));
                    exercicio.setNomeExercicio(rs.getString("nome_exercicio"));
                    exercicio.setDescricao(rs.getString("descricao"));
                    exercicio.setGrupoMuscular(rs.getString("grupo_muscular"));
                    
                    te.setExercicio(exercicio);
                    return te;
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar detalhe do exercício do treino", e);
            throw new RuntimeException("Erro ao buscar detalhe do exercício do treino", e);
        }
        
        return null;
    }

    private Treino extrairTreinoDoResultSet(ResultSet rs) throws SQLException {
        Treino treino = new Treino();
        
        treino.setIdTreino(rs.getInt("id_treino"));
        treino.setNomeTreino(rs.getString("nome_treino"));
        
        Date dataCriacao = rs.getDate("data_criacao");
        if (dataCriacao != null) {
            treino.setDataCriacao(dataCriacao.toLocalDate());
        }
        
        treino.setObservacoes(rs.getString("observacoes"));
        
        Integer idProfessor = rs.getInt("fk_professor");
        if (!rs.wasNull()) {
            treino.setIdProfessor(idProfessor);
            Professor professor = professorDAO.buscarPorId(idProfessor);
            if (professor != null) {
                treino.setProfessor(professor);
            }
        }
        
        return treino;
    }
}
