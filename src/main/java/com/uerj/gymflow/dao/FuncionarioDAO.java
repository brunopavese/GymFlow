package com.uerj.gymflow.dao;

import com.uerj.gymflow.database.DatabaseConnection;
import com.uerj.gymflow.model.Funcionario;
import com.uerj.gymflow.model.Pessoa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {
    private static final Logger logger = LoggerFactory.getLogger(FuncionarioDAO.class);
    private final DatabaseConnection dbConnection;
    private final PessoaDAO pessoaDAO;

    public FuncionarioDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.pessoaDAO = new PessoaDAO();
    }

    public void inserir(Funcionario funcionario) {
        String sqlFuncionario = "INSERT INTO Funcionario (cargo, data_admissao, salario, fk_pessoa) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Usa o PessoaDAO para inserir a parte da Pessoa
                pessoaDAO.inserir(funcionario);

                try (PreparedStatement stmtFuncionario = conn.prepareStatement(sqlFuncionario, Statement.RETURN_GENERATED_KEYS)) {
                    stmtFuncionario.setString(1, funcionario.getCargo());
                    stmtFuncionario.setDate(2, Date.valueOf(funcionario.getDataAdmissao()));
                    stmtFuncionario.setFloat(3, funcionario.getSalario());
                    stmtFuncionario.setInt(4, funcionario.getIdPessoa());
                    stmtFuncionario.executeUpdate();

                    try (ResultSet rsFuncionario = stmtFuncionario.getGeneratedKeys()) {
                        if (rsFuncionario.next()) {
                            funcionario.setIdFuncionario(rsFuncionario.getInt(1));
                        }
                    }
                }

                conn.commit();
                logger.info("Funcionário inserido com sucesso: ID {}", funcionario.getIdFuncionario());
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Erro ao inserir funcionário", e);
                throw new RuntimeException("Erro ao inserir funcionário", e);
            }
        } catch (SQLException e) {
            logger.error("Erro de conexão ao inserir funcionário", e);
            throw new RuntimeException("Erro de conexão ao inserir funcionário", e);
        }
    }

    public Funcionario buscarPorId(Integer id) {
        String sql = "SELECT f.id_funcionario, f.cargo, f.data_admissao, f.salario, f.fk_pessoa " +
                "FROM Funcionario f WHERE f.id_funcionario = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Integer idPessoa = rs.getInt("fk_pessoa");

                    // Busca a pessoa usando o PessoaDAO
                    Pessoa pessoa = pessoaDAO.buscarPorId(idPessoa);
                    if (pessoa == null) {
                        return null;
                    }

                    // Cria e configura o funcionário
                    return criarFuncionario(rs, pessoa);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar funcionário por ID", e);
            throw new RuntimeException("Erro ao buscar funcionário", e);
        }

        return null;
    }

    public List<Funcionario> listarTodos() {
        List<Funcionario> funcionarios = new ArrayList<>();
        String sql = "SELECT f.id_funcionario, f.cargo, f.data_admissao, f.salario, f.fk_pessoa " +
                "FROM Funcionario f";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Integer idPessoa = rs.getInt("fk_pessoa");
                Pessoa pessoa = pessoaDAO.buscarPorId(idPessoa);

                if (pessoa != null) {
                    funcionarios.add(criarFuncionario(rs, pessoa));
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao listar funcionários", e);
            throw new RuntimeException("Erro ao listar funcionários", e);
        }

        return funcionarios;
    }

    public void atualizar(Funcionario funcionario) {
        String sqlFuncionario = "UPDATE Funcionario SET cargo = ?, data_admissao = ?, " +
                "salario = ? WHERE id_funcionario = ?";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Atualiza a parte da Pessoa usando o PessoaDAO
                pessoaDAO.atualizar(funcionario);

                // Atualiza a parte do Funcionário
                try (PreparedStatement stmtFuncionario = conn.prepareStatement(sqlFuncionario)) {
                    stmtFuncionario.setString(1, funcionario.getCargo());
                    stmtFuncionario.setDate(2, Date.valueOf(funcionario.getDataAdmissao()));
                    stmtFuncionario.setFloat(3, funcionario.getSalario());
                    stmtFuncionario.setInt(4, funcionario.getIdFuncionario());
                    stmtFuncionario.executeUpdate();
                }

                conn.commit();
                logger.info("Funcionário atualizado com sucesso: ID {}", funcionario.getIdFuncionario());
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Erro ao atualizar funcionário", e);
                throw new RuntimeException("Erro ao atualizar funcionário", e);
            }
        } catch (SQLException e) {
            logger.error("Erro de conexão ao atualizar funcionário", e);
            throw new RuntimeException("Erro de conexão ao atualizar funcionário", e);
        }
    }

    public void excluir(Integer id) {
        Funcionario funcionario = buscarPorId(id);
        if (funcionario == null) {
            logger.warn("Nenhum funcionário encontrado para exclusão com ID {}", id);
            return;
        }

        String sql = "DELETE FROM Funcionario WHERE id_funcionario = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                // Exclui a pessoa associada
                pessoaDAO.excluir(funcionario.getIdPessoa());

                logger.info("Funcionário excluído com sucesso: ID {}", id);
            } else {
                logger.warn("Nenhum funcionário encontrado para exclusão com ID {}", id);
            }
        } catch (SQLException e) {
            logger.error("Erro ao excluir funcionário", e);
            throw new RuntimeException("Erro ao excluir funcionário", e);
        }
    }

    public List<Funcionario> buscarPorCargo(String cargo) {
        List<Funcionario> funcionarios = new ArrayList<>();
        String sql = "SELECT f.id_funcionario, f.cargo, f.data_admissao, f.salario, f.fk_pessoa " +
                "FROM Funcionario f WHERE f.cargo LIKE ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + cargo + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Integer idPessoa = rs.getInt("fk_pessoa");
                    Pessoa pessoa = pessoaDAO.buscarPorId(idPessoa);

                    if (pessoa != null) {
                        funcionarios.add(criarFuncionario(rs, pessoa));
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar funcionários por cargo", e);
            throw new RuntimeException("Erro ao buscar funcionários por cargo", e);
        }

        return funcionarios;
    }

    public boolean verificarCpfOuEmailExistente(String cpf, String email) {
        // Reaproveita o método do PessoaDAO
        return pessoaDAO.verificarPessoaExistente(cpf, email);
    }

    private Funcionario criarFuncionario(ResultSet rs, Pessoa pessoa) throws SQLException {
        Funcionario funcionario = new Funcionario();

        // Define os atributos herdados de Pessoa
        funcionario.setIdPessoa(pessoa.getIdPessoa());
        funcionario.setNome(pessoa.getNome());
        funcionario.setDataNascimento(pessoa.getDataNascimento());
        funcionario.setCpf(pessoa.getCpf());
        funcionario.setTelefone(pessoa.getTelefone());
        funcionario.setEmail(pessoa.getEmail());

        // Define os atributos específicos do Funcionário
        funcionario.setIdFuncionario(rs.getInt("id_funcionario"));
        funcionario.setCargo(rs.getString("cargo"));
        funcionario.setDataAdmissao(rs.getDate("data_admissao").toLocalDate());
        funcionario.setSalario(rs.getFloat("salario"));

        return funcionario;
    }
}
