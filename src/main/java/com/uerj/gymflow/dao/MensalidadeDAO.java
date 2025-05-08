package com.uerj.gymflow.dao;

import com.uerj.gymflow.database.DatabaseConnection;
import com.uerj.gymflow.model.Aluno;
import com.uerj.gymflow.model.Mensalidade;
import com.uerj.gymflow.model.Plano;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MensalidadeDAO {
    private static final Logger logger = LoggerFactory.getLogger(MensalidadeDAO.class);
    private final DatabaseConnection dbConnection;
    private final PlanoDAO planoDAO;
    private final AlunoDAO alunoDAO;

    public MensalidadeDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.planoDAO = new PlanoDAO();
        this.alunoDAO = new AlunoDAO();
    }

    public void inserir(Mensalidade mensalidade) {
        String sql = "INSERT INTO Mensalidade (data_vencimento, data_pagamento, valor_pago, status_pagamento, fk_plano, fk_aluno) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setDate(1, Date.valueOf(mensalidade.getDataVencimento()));
            
            if (mensalidade.getDataPagamento() != null) {
                stmt.setDate(2, Date.valueOf(mensalidade.getDataPagamento()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            
            stmt.setFloat(3, mensalidade.getValorPago());
            stmt.setString(4, mensalidade.getStatusPagamento());
            
            if (mensalidade.getIdPlano() != null) {
                stmt.setInt(5, mensalidade.getIdPlano());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            if (mensalidade.getIdAluno() != null) {
                stmt.setInt(6, mensalidade.getIdAluno());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        mensalidade.setIdMensalidade(rs.getInt(1));
                        logger.info("Mensalidade inserida com sucesso: ID {}", mensalidade.getIdMensalidade());
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao inserir mensalidade", e);
            throw new RuntimeException("Erro ao inserir mensalidade", e);
        }
    }

    public void registrarPagamento(Integer idMensalidade, LocalDate dataPagamento, Float valorPago) {
        String sql = "UPDATE Mensalidade SET data_pagamento = ?, valor_pago = ?, status_pagamento = ? " +
                     "WHERE id_mensalidade = ?";
                     
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setDate(1, Date.valueOf(dataPagamento));
            stmt.setFloat(2, valorPago);
            stmt.setString(3, "Pago");
            stmt.setInt(4, idMensalidade);
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Pagamento registrado com sucesso para mensalidade ID: {}", idMensalidade);
            } else {
                logger.warn("Nenhuma mensalidade encontrada para ID: {}", idMensalidade);
            }
        } catch (SQLException e) {
            logger.error("Erro ao registrar pagamento", e);
            throw new RuntimeException("Erro ao registrar pagamento", e);
        }
    }

    public Mensalidade buscarPorId(Integer id) {
        String sql = "SELECT id_mensalidade, data_vencimento, data_pagamento, valor_pago, " +
                     "status_pagamento, fk_plano, fk_aluno " +
                     "FROM Mensalidade WHERE id_mensalidade = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairMensalidadeDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar mensalidade por ID", e);
            throw new RuntimeException("Erro ao buscar mensalidade", e);
        }
        
        return null;
    }

    public List<Mensalidade> listarTodas() {
        List<Mensalidade> mensalidades = new ArrayList<>();
        String sql = "SELECT id_mensalidade, data_vencimento, data_pagamento, valor_pago, " +
                     "status_pagamento, fk_plano, fk_aluno FROM Mensalidade";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                mensalidades.add(extrairMensalidadeDoResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar mensalidades", e);
            throw new RuntimeException("Erro ao listar mensalidades", e);
        }
        
        return mensalidades;
    }

    public void atualizar(Mensalidade mensalidade) {
        String sql = "UPDATE Mensalidade SET data_vencimento = ?, data_pagamento = ?, " +
                     "valor_pago = ?, status_pagamento = ?, fk_plano = ?, fk_aluno = ? " +
                     "WHERE id_mensalidade = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(mensalidade.getDataVencimento()));
            
            if (mensalidade.getDataPagamento() != null) {
                stmt.setDate(2, Date.valueOf(mensalidade.getDataPagamento()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            
            stmt.setFloat(3, mensalidade.getValorPago());
            stmt.setString(4, mensalidade.getStatusPagamento());
            
            if (mensalidade.getIdPlano() != null) {
                stmt.setInt(5, mensalidade.getIdPlano());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            if (mensalidade.getIdAluno() != null) {
                stmt.setInt(6, mensalidade.getIdAluno());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            stmt.setInt(7, mensalidade.getIdMensalidade());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Mensalidade atualizada com sucesso: ID {}", mensalidade.getIdMensalidade());
            } else {
                logger.warn("Nenhuma mensalidade encontrada para atualização com ID {}", mensalidade.getIdMensalidade());
            }
        } catch (SQLException e) {
            logger.error("Erro ao atualizar mensalidade", e);
            throw new RuntimeException("Erro ao atualizar mensalidade", e);
        }
    }

    public void excluir(Integer id) {
        String sql = "DELETE FROM Mensalidade WHERE id_mensalidade = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                logger.info("Mensalidade excluída com sucesso: ID {}", id);
            } else {
                logger.warn("Nenhuma mensalidade encontrada para exclusão com ID {}", id);
            }
        } catch (SQLException e) {
            logger.error("Erro ao excluir mensalidade", e);
            throw new RuntimeException("Erro ao excluir mensalidade", e);
        }
    }

    public List<Mensalidade> buscarPorAluno(Integer idAluno) {
        List<Mensalidade> mensalidades = new ArrayList<>();
        String sql = "SELECT id_mensalidade, data_vencimento, data_pagamento, valor_pago, " +
                     "status_pagamento, fk_plano, fk_aluno " +
                     "FROM Mensalidade WHERE fk_aluno = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idAluno);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mensalidades.add(extrairMensalidadeDoResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar mensalidades por aluno", e);
            throw new RuntimeException("Erro ao buscar mensalidades por aluno", e);
        }
        
        return mensalidades;
    }

    public List<Mensalidade> buscarPorPlano(Integer idPlano) {
        List<Mensalidade> mensalidades = new ArrayList<>();
        String sql = "SELECT id_mensalidade, data_vencimento, data_pagamento, valor_pago, " +
                     "status_pagamento, fk_plano, fk_aluno " +
                     "FROM Mensalidade WHERE fk_plano = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPlano);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mensalidades.add(extrairMensalidadeDoResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar mensalidades por plano", e);
            throw new RuntimeException("Erro ao buscar mensalidades por plano", e);
        }
        
        return mensalidades;
    }

    public List<Mensalidade> buscarPorStatus(String status) {
        List<Mensalidade> mensalidades = new ArrayList<>();
        String sql = "SELECT id_mensalidade, data_vencimento, data_pagamento, valor_pago, " +
                     "status_pagamento, fk_plano, fk_aluno " +
                     "FROM Mensalidade WHERE status_pagamento = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mensalidades.add(extrairMensalidadeDoResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar mensalidades por status", e);
            throw new RuntimeException("Erro ao buscar mensalidades por status", e);
        }
        
        return mensalidades;
    }

    public List<Mensalidade> buscarMensalidadesAtrasadas() {
        List<Mensalidade> mensalidades = new ArrayList<>();
        String sql = "SELECT id_mensalidade, data_vencimento, data_pagamento, valor_pago, " +
                     "status_pagamento, fk_plano, fk_aluno " +
                     "FROM Mensalidade " +
                     "WHERE data_vencimento < ? AND (status_pagamento != 'Pago' OR status_pagamento IS NULL)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mensalidades.add(extrairMensalidadeDoResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar mensalidades atrasadas", e);
            throw new RuntimeException("Erro ao buscar mensalidades atrasadas", e);
        }
        
        return mensalidades;
    }

    public List<Mensalidade> buscarPorPeriodoVencimento(LocalDate dataInicio, LocalDate dataFim) {
        List<Mensalidade> mensalidades = new ArrayList<>();
        String sql = "SELECT id_mensalidade, data_vencimento, data_pagamento, valor_pago, " +
                     "status_pagamento, fk_plano, fk_aluno " +
                     "FROM Mensalidade WHERE data_vencimento BETWEEN ? AND ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(dataInicio));
            stmt.setDate(2, Date.valueOf(dataFim));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mensalidades.add(extrairMensalidadeDoResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar mensalidades por período de vencimento", e);
            throw new RuntimeException("Erro ao buscar mensalidades por período de vencimento", e);
        }
        
        return mensalidades;
    }

    public List<Mensalidade> buscarPorPeriodoPagamento(LocalDate dataInicio, LocalDate dataFim) {
        List<Mensalidade> mensalidades = new ArrayList<>();
        String sql = "SELECT id_mensalidade, data_vencimento, data_pagamento, valor_pago, " +
                     "status_pagamento, fk_plano, fk_aluno " +
                     "FROM Mensalidade WHERE data_pagamento BETWEEN ? AND ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(dataInicio));
            stmt.setDate(2, Date.valueOf(dataFim));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mensalidades.add(extrairMensalidadeDoResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar mensalidades por período de pagamento", e);
            throw new RuntimeException("Erro ao buscar mensalidades por período de pagamento", e);
        }
        
        return mensalidades;
    }

    public void gerarMensalidadesAutomaticas(Integer idAluno, Integer idPlano, int meses) {
        Aluno aluno = alunoDAO.buscarPorId(idAluno);
        Plano plano = planoDAO.buscarPorId(idPlano);
        
        if (aluno == null || plano == null) {
            logger.warn("Aluno ou Plano não encontrado para gerar mensalidades automáticas");
            return;
        }
        
        LocalDate dataInicial = LocalDate.now();
        
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                for (int i = 0; i < meses; i++) {
                    LocalDate dataVencimento = dataInicial.plusMonths(i);
                    
                    Mensalidade mensalidade = new Mensalidade();
                    mensalidade.setDataVencimento(dataVencimento);
                    mensalidade.setValorPago(plano.getValorMensal());
                    mensalidade.setStatusPagamento("Pendente");
                    mensalidade.setIdPlano(idPlano);
                    mensalidade.setIdAluno(idAluno);
                    
                    inserir(mensalidade);
                }
                
                conn.commit();
                logger.info("Mensalidades automáticas geradas com sucesso para aluno ID {}", idAluno);
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Erro ao gerar mensalidades automáticas", e);
                throw new RuntimeException("Erro ao gerar mensalidades automáticas", e);
            }
        } catch (SQLException e) {
            logger.error("Erro de conexão ao gerar mensalidades automáticas", e);
            throw new RuntimeException("Erro de conexão ao gerar mensalidades automáticas", e);
        }
    }

    private Mensalidade extrairMensalidadeDoResultSet(ResultSet rs) throws SQLException {
        Mensalidade mensalidade = new Mensalidade();
        
        mensalidade.setIdMensalidade(rs.getInt("id_mensalidade"));
        mensalidade.setDataVencimento(rs.getDate("data_vencimento").toLocalDate());
        
        Date dataPagamento = rs.getDate("data_pagamento");
        if (dataPagamento != null) {
            mensalidade.setDataPagamento(dataPagamento.toLocalDate());
        }
        
        mensalidade.setValorPago(rs.getFloat("valor_pago"));
        mensalidade.setStatusPagamento(rs.getString("status_pagamento"));
        
        Integer idPlano = rs.getInt("fk_plano");
        if (!rs.wasNull()) {
            mensalidade.setIdPlano(idPlano);
            Plano plano = planoDAO.buscarPorId(idPlano);
            if (plano != null) {
                mensalidade.setPlano(plano);
            }
        }
        
        Integer idAluno = rs.getInt("fk_aluno");
        if (!rs.wasNull()) {
            mensalidade.setIdAluno(idAluno);
            Aluno aluno = alunoDAO.buscarPorId(idAluno);
            if (aluno != null) {
                mensalidade.setAluno(aluno);
            }
        }
        
        return mensalidade;
    }
}
