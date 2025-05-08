package com.uerj.gymflow.dao;

import com.uerj.gymflow.database.DatabaseConnection;
import com.uerj.gymflow.model.Aluno;
import com.uerj.gymflow.model.AlunoTreino;
import com.uerj.gymflow.model.Treino;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlunoTreinoDAO {
    private static final Logger logger = LoggerFactory.getLogger(AlunoTreinoDAO.class);
    private final DatabaseConnection dbConnection;
    private final AlunoDAO alunoDAO;
    private final TreinoDAO treinoDAO;

    public AlunoTreinoDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.alunoDAO = new AlunoDAO();
        this.treinoDAO = new TreinoDAO();
    }

    /**
     * Associa um treino a um aluno
     * @param alunoTreino Objeto contendo os dados da associação
     */
    public void associarTreinoAoAluno(AlunoTreino alunoTreino) {
        String sql = "INSERT INTO Aluno_treino (id_aluno, id_treino, data_inicio, data_fim, observacoes) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, alunoTreino.getIdAluno());
            stmt.setInt(2, alunoTreino.getIdTreino());
            
            if (alunoTreino.getDataInicio() != null) {
                stmt.setDate(3, Date.valueOf(alunoTreino.getDataInicio()));
            } else {
                stmt.setDate(3, Date.valueOf(LocalDate.now()));
            }
            
            if (alunoTreino.getDataFim() != null) {
                stmt.setDate(4, Date.valueOf(alunoTreino.getDataFim()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            
            stmt.setString(5, alunoTreino.getObservacoes());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Treino associado ao aluno com sucesso.");
            }
        } catch (SQLException e) {
            logger.error("Erro ao associar treino ao aluno", e);
            throw new RuntimeException("Erro ao associar treino ao aluno", e);
        }
    }

    /**
     * Busca a associação entre um aluno e um treino específico
     * @param idAluno ID do aluno
     * @param idTreino ID do treino
     * @return Objeto AlunoTreino contendo os dados da associação ou null se não existe
     */
    public AlunoTreino buscar(Integer idAluno, Integer idTreino) {
        String sql = "SELECT id_aluno, id_treino, data_inicio, data_fim, observacoes " +
                     "FROM Aluno_treino WHERE id_aluno = ? AND id_treino = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAluno);
            stmt.setInt(2, idTreino);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AlunoTreino alunoTreino = extrairAlunoTreinoDoResultSet(rs);
                    
                    // Carregar objetos relacionados
                    Aluno aluno = alunoDAO.buscarPorId(idAluno);
                    Treino treino = treinoDAO.buscarPorId(idTreino);
                    
                    if (aluno != null) {
                        alunoTreino.setAluno(aluno);
                    }
                    
                    if (treino != null) {
                        alunoTreino.setTreino(treino);
                    }
                    
                    return alunoTreino;
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar associação aluno-treino", e);
            throw new RuntimeException("Erro ao buscar associação aluno-treino", e);
        }
        
        return null;
    }

    /**
     * Lista todos os treinos associados a um aluno específico
     * @param idAluno ID do aluno
     * @return Lista de objetos AlunoTreino com os treinos do aluno
     */
    public List<AlunoTreino> listarTreinosDoAluno(Integer idAluno) {
        List<AlunoTreino> treinosDoAluno = new ArrayList<>();
        String sql = "SELECT id_aluno, id_treino, data_inicio, data_fim, observacoes " +
                     "FROM Aluno_treino WHERE id_aluno = ? ORDER BY data_inicio DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAluno);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AlunoTreino alunoTreino = extrairAlunoTreinoDoResultSet(rs);
                    
                    // Carregar o treino
                    Treino treino = treinoDAO.buscarPorId(alunoTreino.getIdTreino());
                    if (treino != null) {
                        alunoTreino.setTreino(treino);
                    }
                    
                    treinosDoAluno.add(alunoTreino);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar treinos do aluno", e);
            throw new RuntimeException("Erro ao listar treinos do aluno", e);
        }
        
        return treinosDoAluno;
    }

    /**
     * Lista todos os alunos associados a um treino específico
     * @param idTreino ID do treino
     * @return Lista de objetos AlunoTreino com os alunos do treino
     */
    public List<AlunoTreino> listarAlunosDoTreino(Integer idTreino) {
        List<AlunoTreino> alunosDoTreino = new ArrayList<>();
        String sql = "SELECT id_aluno, id_treino, data_inicio, data_fim, observacoes " +
                     "FROM Aluno_treino WHERE id_treino = ? ORDER BY data_inicio DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTreino);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AlunoTreino alunoTreino = extrairAlunoTreinoDoResultSet(rs);
                    
                    // Carregar o aluno
                    Aluno aluno = alunoDAO.buscarPorId(alunoTreino.getIdAluno());
                    if (aluno != null) {
                        alunoTreino.setAluno(aluno);
                    }
                    
                    alunosDoTreino.add(alunoTreino);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar alunos do treino", e);
            throw new RuntimeException("Erro ao listar alunos do treino", e);
        }
        
        return alunosDoTreino;
    }

    /**
     * Lista os treinos ativos de um aluno (que ainda não expiraram)
     * @param idAluno ID do aluno
     * @return Lista de objetos AlunoTreino com os treinos ativos
     */
    public List<AlunoTreino> listarTreinosAtivosDoAluno(Integer idAluno) {
        List<AlunoTreino> treinosAtivos = new ArrayList<>();
        String sql = "SELECT id_aluno, id_treino, data_inicio, data_fim, observacoes " +
                     "FROM Aluno_treino " +
                     "WHERE id_aluno = ? AND (data_fim IS NULL OR data_fim >= ?) " +
                     "ORDER BY data_inicio DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAluno);
            stmt.setDate(2, Date.valueOf(LocalDate.now()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AlunoTreino alunoTreino = extrairAlunoTreinoDoResultSet(rs);
                    
                    // Carregar o treino
                    Treino treino = treinoDAO.buscarPorId(alunoTreino.getIdTreino());
                    if (treino != null) {
                        alunoTreino.setTreino(treino);
                    }
                    
                    treinosAtivos.add(alunoTreino);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar treinos ativos do aluno", e);
            throw new RuntimeException("Erro ao listar treinos ativos do aluno", e);
        }
        
        return treinosAtivos;
    }

    /**
     * Atualiza os dados da associação entre um aluno e um treino
     * @param alunoTreino Objeto contendo os novos dados
     */
    public void atualizar(AlunoTreino alunoTreino) {
        String sql = "UPDATE Aluno_treino SET data_inicio = ?, data_fim = ?, observacoes = ? " +
                     "WHERE id_aluno = ? AND id_treino = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (alunoTreino.getDataInicio() != null) {
                stmt.setDate(1, Date.valueOf(alunoTreino.getDataInicio()));
            } else {
                stmt.setNull(1, Types.DATE);
            }
            
            if (alunoTreino.getDataFim() != null) {
                stmt.setDate(2, Date.valueOf(alunoTreino.getDataFim()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            
            stmt.setString(3, alunoTreino.getObservacoes());
            stmt.setInt(4, alunoTreino.getIdAluno());
            stmt.setInt(5, alunoTreino.getIdTreino());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Associação aluno-treino atualizada com sucesso");
            } else {
                logger.warn("Nenhuma associação aluno-treino encontrada para atualização");
            }
        } catch (SQLException e) {
            logger.error("Erro ao atualizar associação aluno-treino", e);
            throw new RuntimeException("Erro ao atualizar associação aluno-treino", e);
        }
    }

    /**
     * Remove a associação entre um aluno e um treino
     * @param idAluno ID do aluno
     * @param idTreino ID do treino
     */
    public void removerTreinoDoAluno(Integer idAluno, Integer idTreino) {
        String sql = "DELETE FROM Aluno_treino WHERE id_aluno = ? AND id_treino = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAluno);
            stmt.setInt(2, idTreino);
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Treino removido do aluno com sucesso");
            } else {
                logger.warn("Nenhuma associação aluno-treino encontrada para remoção");
            }
        } catch (SQLException e) {
            logger.error("Erro ao remover treino do aluno", e);
            throw new RuntimeException("Erro ao remover treino do aluno", e);
        }
    }

    /**
     * Encerra um treino para um aluno definindo a data de fim como a data atual
     * @param idAluno ID do aluno
     * @param idTreino ID do treino
     */
    public void encerrarTreino(Integer idAluno, Integer idTreino) {
        String sql = "UPDATE Aluno_treino SET data_fim = ? WHERE id_aluno = ? AND id_treino = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, idAluno);
            stmt.setInt(3, idTreino);
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Treino encerrado para o aluno com sucesso");
            } else {
                logger.warn("Nenhuma associação aluno-treino encontrada para encerramento");
            }
        } catch (SQLException e) {
            logger.error("Erro ao encerrar treino para o aluno", e);
            throw new RuntimeException("Erro ao encerrar treino para o aluno", e);
        }
    }

    /**
     * Renova um treino para um aluno, estendendo a data de fim por um período específico
     * @param idAluno ID do aluno
     * @param idTreino ID do treino
     * @param diasExtensao Número de dias a estender o treino
     */
    public void renovarTreino(Integer idAluno, Integer idTreino, Integer diasExtensao) {
        String sqlSelect = "SELECT data_fim FROM Aluno_treino WHERE id_aluno = ? AND id_treino = ?";
        String sqlUpdate = "UPDATE Aluno_treino SET data_fim = ? WHERE id_aluno = ? AND id_treino = ?";

        try (Connection conn = dbConnection.getConnection()) {
            LocalDate novaDataFim;
            
            // Determinar a nova data de fim
            try (PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {
                stmt.setInt(1, idAluno);
                stmt.setInt(2, idTreino);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Date dataFimAtual = rs.getDate("data_fim");
                        
                        if (dataFimAtual != null) {
                            // Se já existe uma data fim, estender a partir dela
                            LocalDate dataFimLocal = dataFimAtual.toLocalDate();
                            novaDataFim = dataFimLocal.plusDays(diasExtensao);
                        } else {
                            // Se não existe data fim, estender a partir de hoje
                            novaDataFim = LocalDate.now().plusDays(diasExtensao);
                        }
                    } else {
                        logger.warn("Nenhuma associação aluno-treino encontrada para renovação");
                        return;
                    }
                }
            }
            
            // Atualizar a data fim
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setDate(1, Date.valueOf(novaDataFim));
                stmt.setInt(2, idAluno);
                stmt.setInt(3, idTreino);
                
                int linhasAfetadas = stmt.executeUpdate();
                
                if (linhasAfetadas > 0) {
                    logger.info("Treino renovado para o aluno com sucesso. Nova data fim: {}", novaDataFim);
                } else {
                    logger.warn("Falha ao renovar treino para o aluno");
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao renovar treino para o aluno", e);
            throw new RuntimeException("Erro ao renovar treino para o aluno", e);
        }
    }

    /**
     * Extrai um objeto AlunoTreino do ResultSet
     * @param rs ResultSet contendo os dados
     * @return Objeto AlunoTreino preenchido
     * @throws SQLException Em caso de erro de acesso aos dados
     */
    private AlunoTreino extrairAlunoTreinoDoResultSet(ResultSet rs) throws SQLException {
        AlunoTreino alunoTreino = new AlunoTreino();
        
        alunoTreino.setIdAluno(rs.getInt("id_aluno"));
        alunoTreino.setIdTreino(rs.getInt("id_treino"));
        
        Date dataInicio = rs.getDate("data_inicio");
        if (dataInicio != null) {
            alunoTreino.setDataInicio(dataInicio.toLocalDate());
        }
        
        Date dataFim = rs.getDate("data_fim");
        if (dataFim != null) {
            alunoTreino.setDataFim(dataFim.toLocalDate());
        }
        
        alunoTreino.setObservacoes(rs.getString("observacoes"));
        
        return alunoTreino;
    }
}
