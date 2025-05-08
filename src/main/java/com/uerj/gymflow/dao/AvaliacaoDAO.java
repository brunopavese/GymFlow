package com.uerj.gymflow.dao;

import com.uerj.gymflow.database.DatabaseConnection;
import com.uerj.gymflow.model.Aluno;
import com.uerj.gymflow.model.Avaliacao;
import com.uerj.gymflow.model.Professor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AvaliacaoDAO {
    private static final Logger logger = LoggerFactory.getLogger(AvaliacaoDAO.class);
    private final DatabaseConnection dbConnection;
    private final AlunoDAO alunoDAO;
    private final ProfessorDAO professorDAO;

    public AvaliacaoDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.alunoDAO = new AlunoDAO();
        this.professorDAO = new ProfessorDAO();
    }

    /**
     * Insere uma nova avaliação no banco de dados
     * @param avaliacao A avaliação a ser inserida
     * @return A avaliação com o ID gerado
     */
    public Avaliacao inserir(Avaliacao avaliacao) {
        String sql = "INSERT INTO Avaliacao (data_avaliacao, peso, altura, observacoes, fk_aluno, fk_professor) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Preencher os parâmetros
            if (avaliacao.getDataAvaliacao() != null) {
                stmt.setDate(1, Date.valueOf(avaliacao.getDataAvaliacao()));
            } else {
                stmt.setDate(1, Date.valueOf(LocalDate.now()));
            }
            
            setFloatOrNull(stmt, 2, avaliacao.getPeso());
            setFloatOrNull(stmt, 3, avaliacao.getAltura());
            stmt.setString(4, avaliacao.getObservacoes());
            stmt.setInt(5, avaliacao.getIdAluno());
            
            if (avaliacao.getIdProfessor() != null) {
                stmt.setInt(6, avaliacao.getIdProfessor());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                // Obter o ID gerado
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        avaliacao.setIdAvaliacao(rs.getInt(1));
                        logger.info("Avaliação inserida com sucesso. ID: {}", avaliacao.getIdAvaliacao());
                        return avaliacao;
                    }
                }
            }
            
            throw new RuntimeException("Falha ao inserir avaliação - nenhum ID gerado.");
        } catch (SQLException e) {
            logger.error("Erro ao inserir avaliação", e);
            throw new RuntimeException("Erro ao inserir avaliação", e);
        }
    }

    /**
     * Busca uma avaliação pelo ID
     * @param idAvaliacao ID da avaliação
     * @return A avaliação encontrada ou null se não existir
     */
    public Avaliacao buscarPorId(Integer idAvaliacao) {
        String sql = "SELECT * FROM Avaliacao WHERE id_avaliacao = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAvaliacao);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Avaliacao avaliacao = extrairAvaliacaoDoResultSet(rs);
                    
                    // Carregar objetos relacionados
                    if (avaliacao.getIdAluno() != null) {
                        Aluno aluno = alunoDAO.buscarPorId(avaliacao.getIdAluno());
                        avaliacao.setAluno(aluno);
                    }
                    
                    if (avaliacao.getIdProfessor() != null) {
                        Professor professor = professorDAO.buscarPorId(avaliacao.getIdProfessor());
                        avaliacao.setProfessor(professor);
                    }
                    
                    return avaliacao;
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar avaliação por ID", e);
            throw new RuntimeException("Erro ao buscar avaliação por ID", e);
        }
        
        return null;
    }

    /**
     * Lista todas as avaliações de um aluno
     * @param idAluno ID do aluno
     * @return Lista de avaliações do aluno, ordenadas pela data mais recente primeiro
     */
    public List<Avaliacao> listarPorAluno(Integer idAluno) {
        List<Avaliacao> avaliacoes = new ArrayList<>();
        String sql = "SELECT * FROM Avaliacao WHERE fk_aluno = ? ORDER BY data_avaliacao DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAluno);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Avaliacao avaliacao = extrairAvaliacaoDoResultSet(rs);
                    
                    // Carregar objeto Professor relacionado
                    if (avaliacao.getIdProfessor() != null) {
                        Professor professor = professorDAO.buscarPorId(avaliacao.getIdProfessor());
                        avaliacao.setProfessor(professor);
                    }
                    
                    avaliacoes.add(avaliacao);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar avaliações por aluno", e);
            throw new RuntimeException("Erro ao listar avaliações por aluno", e);
        }
        
        return avaliacoes;
    }

    /**
     * Atualiza uma avaliação existente
     * @param avaliacao A avaliação com os dados atualizados
     */
    public void atualizar(Avaliacao avaliacao) {
        String sql = "UPDATE Avaliacao SET data_avaliacao = ?, peso = ?, altura = ?, observacoes = ?, " +
                     "fk_professor = ? WHERE id_avaliacao = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (avaliacao.getDataAvaliacao() != null) {
                stmt.setDate(1, Date.valueOf(avaliacao.getDataAvaliacao()));
            } else {
                stmt.setNull(1, Types.DATE);
            }
            
            setFloatOrNull(stmt, 2, avaliacao.getPeso());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Avaliação atualizada com sucesso. ID: {}", avaliacao.getIdAvaliacao());
            } else {
                logger.warn("Nenhuma avaliação encontrada para atualização com ID: {}", avaliacao.getIdAvaliacao());
            }
        } catch (SQLException e) {
            logger.error("Erro ao atualizar avaliação", e);
            throw new RuntimeException("Erro ao atualizar avaliação", e);
        }
    }

    /**
     * Exclui uma avaliação do banco de dados
     * @param idAvaliacao ID da avaliação a ser excluída
     */
    public void excluir(Integer idAvaliacao) {
        String sql = "DELETE FROM Avaliacao WHERE id_avaliacao = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAvaliacao);
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Avaliação excluída com sucesso. ID: {}", idAvaliacao);
            } else {
                logger.warn("Nenhuma avaliação encontrada para exclusão com ID: {}", idAvaliacao);
            }
        } catch (SQLException e) {
            logger.error("Erro ao excluir avaliação", e);
            throw new RuntimeException("Erro ao excluir avaliação", e);
        }
    }

    /**
     * Extrai uma avaliação do ResultSet
     * @param rs ResultSet contendo os dados
     * @return Objeto Avaliacao preenchido
     * @throws SQLException Em caso de erro de acesso aos dados
     */
    private Avaliacao extrairAvaliacaoDoResultSet(ResultSet rs) throws SQLException {
        Avaliacao avaliacao = new Avaliacao();
        
        avaliacao.setIdAvaliacao(rs.getInt("id_avaliacao"));
        
        Date dataAvaliacao = rs.getDate("data_avaliacao");
        if (dataAvaliacao != null) {
            avaliacao.setDataAvaliacao(dataAvaliacao.toLocalDate());
        }
        
        float peso = rs.getFloat("peso");
        if (!rs.wasNull()) {
            avaliacao.setPeso(peso);
        }
        
        float altura = rs.getFloat("altura");
        if (!rs.wasNull()) {
            avaliacao.setAltura(altura);
        }
        
        avaliacao.setObservacoes(rs.getString("observacoes"));
        avaliacao.setIdAluno(rs.getInt("fk_aluno"));
        
        int idProfessor = rs.getInt("fk_professor");
        if (!rs.wasNull()) {
            avaliacao.setIdProfessor(idProfessor);
        }
        
        return avaliacao;
    }
    
    /**
     * Método utilitário para definir um valor Float ou NULL em um PreparedStatement
     * @param stmt O PreparedStatement
     * @param paramIndex O índice do parâmetro
     * @param value O valor a ser definido
     * @throws SQLException Em caso de erro ao definir o parâmetro
     */
    private void setFloatOrNull(PreparedStatement stmt, int paramIndex, Float value) throws SQLException {
        if (value != null) {
            stmt.setFloat(paramIndex, value);
        } else {
            stmt.setNull(paramIndex, Types.FLOAT);
        }
    }
}
