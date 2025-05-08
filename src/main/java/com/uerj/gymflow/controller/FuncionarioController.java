package com.uerj.gymflow.controller;

import com.uerj.gymflow.dao.FuncionarioDAO;
import com.uerj.gymflow.model.Funcionario;
import com.uerj.gymflow.view.FuncionarioView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Controlador para gerenciar operações relacionadas a Funcionários
 * Implementa o padrão MVC, fazendo a intermediação entre View e DAO
 */
public class FuncionarioController {
    private static final Logger logger = LoggerFactory.getLogger(FuncionarioController.class);
    private final FuncionarioDAO funcionarioDAO;
    private final FuncionarioView funcionarioView;

    public FuncionarioController(Scanner scanner) {
        this.funcionarioDAO = new FuncionarioDAO();
        this.funcionarioView = new FuncionarioView(scanner);
    }

    /**
     * Exibe o menu de opções de funcionário e processa a escolha do usuário
     */
    public void mostrarMenu() {
        int opcao = -1;
        
        while (opcao != 0) {
            try {
                opcao = funcionarioView.exibirMenu();
                
                switch (opcao) {
                    case 1:
                        cadastrarFuncionario();
                        break;
                    case 2:
                        buscarFuncionarioPorId();
                        break;
                    case 3:
                        buscarFuncionarioPorCargo();
                        break;
                    case 4:
                        listarTodosFuncionarios();
                        break;
                    case 5:
                        atualizarFuncionario();
                        break;
                    case 6:
                        excluirFuncionario();
                        break;
                    case 0:
                        System.out.println("Voltando ao menu principal...");
                        break;
                    default:
                        if (opcao != -1) {
                            System.out.println("Opção inválida. Tente novamente.");
                        }
                }
            } catch (Exception e) {
                funcionarioView.exibirMensagemErro(e.getMessage());
                logger.error("Erro no menu de funcionário", e);
            }
        }
    }

    /**
     * Coleta informações e cadastra um novo funcionário
     */
    private void cadastrarFuncionario() {
        try {
            Funcionario novoFuncionario = funcionarioView.coletarDadosFuncionario(null);
            
            if (novoFuncionario != null) {
                if (funcionarioDAO.verificarCpfOuEmailExistente(novoFuncionario.getCpf(), novoFuncionario.getEmail())) {
                    funcionarioView.exibirMensagemErro("Já existe uma pessoa com este CPF ou e-mail.");
                    return;
                }
                
                funcionarioDAO.inserir(novoFuncionario);
                funcionarioView.exibirMensagemCadastroSucesso(novoFuncionario);
                logger.info("Funcionário cadastrado com sucesso: ID {}", novoFuncionario.getIdFuncionario());
            }
        } catch (Exception e) {
            funcionarioView.exibirMensagemErro("Erro ao cadastrar funcionário: " + e.getMessage());
            logger.error("Erro ao cadastrar funcionário", e);
        }
    }

    /**
     * Busca um funcionário pelo ID
     */
    private void buscarFuncionarioPorId() {
        try {
            Integer id = funcionarioView.solicitarId();
            
            if (id != null) {
                Funcionario funcionario = funcionarioDAO.buscarPorId(id);
                funcionarioView.exibirDadosFuncionario(funcionario);
                
                if (funcionario != null) {
                    logger.info("Funcionário encontrado por ID: {}", id);
                } else {
                    logger.info("Nenhum funcionário encontrado com ID: {}", id);
                }
            }
        } catch (Exception e) {
            funcionarioView.exibirMensagemErro("Erro ao buscar funcionário: " + e.getMessage());
            logger.error("Erro ao buscar funcionário por ID", e);
        }
    }

    /**
     * Busca funcionários por cargo
     */
    private void buscarFuncionarioPorCargo() {
        try {
            String cargo = funcionarioView.solicitarCargo();
            
            if (cargo != null && !cargo.isEmpty()) {
                List<Funcionario> funcionarios = funcionarioDAO.buscarPorCargo(cargo);
                funcionarioView.exibirListaFuncionarios(funcionarios);
                
                logger.info("Busca por cargo '{}' concluída. Resultados: {}", cargo, funcionarios.size());
            }
        } catch (Exception e) {
            funcionarioView.exibirMensagemErro("Erro ao buscar funcionários por cargo: " + e.getMessage());
            logger.error("Erro ao buscar funcionários por cargo", e);
        }
    }

    /**
     * Lista todos os funcionários cadastrados
     */
    private void listarTodosFuncionarios() {
        try {
            List<Funcionario> funcionarios = funcionarioDAO.listarTodos();
            funcionarioView.exibirListaFuncionarios(funcionarios);
            logger.info("Listagem de funcionários concluída. Total: {}", funcionarios.size());
        } catch (Exception e) {
            funcionarioView.exibirMensagemErro("Erro ao listar funcionários: " + e.getMessage());
            logger.error("Erro ao listar funcionários", e);
        }
    }

    /**
     * Atualiza os dados de um funcionário existente
     */
    private void atualizarFuncionario() {
        try {
            Integer id = funcionarioView.solicitarId();
            
            if (id != null) {
                Funcionario funcionarioExistente = funcionarioDAO.buscarPorId(id);
                
                if (funcionarioExistente == null) {
                    funcionarioView.exibirMensagemErro("Funcionário não encontrado.");
                    return;
                }
                
                funcionarioView.exibirDadosFuncionario(funcionarioExistente);
                
                Funcionario funcionarioAtualizado = funcionarioView.coletarDadosFuncionario(funcionarioExistente);
                
                if (funcionarioAtualizado != null) {
                    funcionarioAtualizado.setIdFuncionario(id);
                    funcionarioDAO.atualizar(funcionarioAtualizado);
                    funcionarioView.exibirMensagemAtualizacaoSucesso();
                    logger.info("Funcionário atualizado com sucesso: ID {}", id);
                }
            }
        } catch (Exception e) {
            funcionarioView.exibirMensagemErro("Erro ao atualizar funcionário: " + e.getMessage());
            logger.error("Erro ao atualizar funcionário", e);
        }
    }

    /**
     * Exclui um funcionário do sistema
     */
    private void excluirFuncionario() {
        try {
            Integer id = funcionarioView.solicitarId();
            
            if (id != null) {
                Funcionario funcionarioExistente = funcionarioDAO.buscarPorId(id);
                
                if (funcionarioExistente == null) {
                    funcionarioView.exibirMensagemErro("Funcionário não encontrado.");
                    return;
                }
                
                funcionarioView.exibirDadosFuncionario(funcionarioExistente);
                
                if (funcionarioView.confirmarExclusao()) {
                    funcionarioDAO.excluir(id);
                    funcionarioView.exibirMensagemExclusaoSucesso();
                    logger.info("Funcionário excluído com sucesso: ID {}", id);
                } else {
                    funcionarioView.exibirMensagemOperacaoCancelada();
                    logger.info("Exclusão de funcionário cancelada: ID {}", id);
                }
            }
        } catch (Exception e) {
            funcionarioView.exibirMensagemErro("Erro ao excluir funcionário: " + e.getMessage());
            logger.error("Erro ao excluir funcionário", e);
        }
    }
}
