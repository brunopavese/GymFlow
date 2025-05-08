package com.uerj.gymflow.controller;

import com.uerj.gymflow.dao.PlanoDAO;
import com.uerj.gymflow.model.Plano;
import com.uerj.gymflow.view.PlanoView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Controlador para gerenciar operações relacionadas a Planos
 * Implementa o padrão MVC, fazendo a intermediação entre View e DAO
 */
public class PlanoController {
    private static final Logger logger = LoggerFactory.getLogger(PlanoController.class);
    private final PlanoDAO planoDAO;
    private final PlanoView planoView;

    public PlanoController(Scanner scanner) {
        this.planoDAO = new PlanoDAO();
        this.planoView = new PlanoView(scanner);
    }

    /**
     * Exibe o menu de opções de plano e processa a escolha do usuário
     */
    public void mostrarMenu() {
        int opcao = -1;
        
        while (opcao != 0) {
            try {
                opcao = planoView.exibirMenu();
                
                switch (opcao) {
                    case 1:
                        cadastrarPlano();
                        break;
                    case 2:
                        buscarPlanoPorId();
                        break;
                    case 3:
                        listarTodosPlanos();
                        break;
                    case 4:
                        atualizarPlano();
                        break;
                    case 5:
                        excluirPlano();
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
                planoView.exibirMensagemErro(e.getMessage());
                logger.error("Erro no menu de plano", e);
            }
        }
    }

    /**
     * Coleta informações e cadastra um novo plano
     */
    private void cadastrarPlano() {
        try {
            Plano novoPlano = planoView.coletarDadosPlano(null);
            
            if (novoPlano != null) {
                if (planoDAO.verificarNomePlanoExistente(novoPlano.getNomePlano())) {
                    planoView.exibirMensagemErro("Já existe um plano com este nome.");
                    return;
                }
                
                planoDAO.inserir(novoPlano);
                planoView.exibirMensagemCadastroSucesso(novoPlano);
                logger.info("Plano cadastrado com sucesso: ID {}", novoPlano.getIdPlano());
            }
        } catch (Exception e) {
            planoView.exibirMensagemErro("Erro ao cadastrar plano: " + e.getMessage());
            logger.error("Erro ao cadastrar plano", e);
        }
    }

    /**
     * Busca um plano pelo ID
     */
    private void buscarPlanoPorId() {
        try {
            Integer id = planoView.solicitarId();
            
            if (id != null) {
                Plano plano = planoDAO.buscarPorId(id);
                planoView.exibirDadosPlano(plano);
                
                if (plano != null) {
                    logger.info("Plano encontrado por ID: {}", id);
                } else {
                    logger.info("Nenhum plano encontrado com ID: {}", id);
                }
            }
        } catch (Exception e) {
            planoView.exibirMensagemErro("Erro ao buscar plano: " + e.getMessage());
            logger.error("Erro ao buscar plano por ID", e);
        }
    }

    /**
     * Lista todos os planos cadastrados
     */
    private void listarTodosPlanos() {
        try {
            List<Plano> planos = planoDAO.listarTodos();
            planoView.exibirListaPlanos(planos);
            logger.info("Listagem de planos concluída. Total: {}", planos.size());
        } catch (Exception e) {
            planoView.exibirMensagemErro("Erro ao listar planos: " + e.getMessage());
            logger.error("Erro ao listar planos", e);
        }
    }

    /**
     * Atualiza os dados de um plano existente
     */
    private void atualizarPlano() {
        try {
            Integer id = planoView.solicitarId();
            
            if (id != null) {
                Plano planoExistente = planoDAO.buscarPorId(id);
                
                if (planoExistente == null) {
                    planoView.exibirMensagemErro("Plano não encontrado.");
                    return;
                }
                
                planoView.exibirDadosPlano(planoExistente);
                
                Plano planoAtualizado = planoView.coletarDadosPlano(planoExistente);
                
                if (planoAtualizado != null) {
                    planoAtualizado.setIdPlano(id);
                    
                    // Verificar se o nome atualizado já existe em outro plano
                    if (!planoAtualizado.getNomePlano().equals(planoExistente.getNomePlano()) && 
                        planoDAO.verificarNomePlanoExistente(planoAtualizado.getNomePlano())) {
                        planoView.exibirMensagemErro("Já existe um plano com este nome.");
                        return;
                    }
                    
                    planoDAO.atualizar(planoAtualizado);
                    planoView.exibirMensagemAtualizacaoSucesso();
                    logger.info("Plano atualizado com sucesso: ID {}", id);
                }
            }
        } catch (Exception e) {
            planoView.exibirMensagemErro("Erro ao atualizar plano: " + e.getMessage());
            logger.error("Erro ao atualizar plano", e);
        }
    }

    /**
     * Exclui um plano do sistema
     */
    private void excluirPlano() {
        try {
            Integer id = planoView.solicitarId();
            
            if (id != null) {
                Plano planoExistente = planoDAO.buscarPorId(id);
                
                if (planoExistente == null) {
                    planoView.exibirMensagemErro("Plano não encontrado.");
                    return;
                }
                
                planoView.exibirDadosPlano(planoExistente);
                
                if (planoView.confirmarExclusao()) {
                    planoDAO.excluir(id);
                    planoView.exibirMensagemExclusaoSucesso();
                    logger.info("Plano excluído com sucesso: ID {}", id);
                } else {
                    planoView.exibirMensagemOperacaoCancelada();
                    logger.info("Exclusão de plano cancelada: ID {}", id);
                }
            }
        } catch (Exception e) {
            planoView.exibirMensagemErro("Erro ao excluir plano: " + e.getMessage());
            logger.error("Erro ao excluir plano", e);
        }
    }
}
