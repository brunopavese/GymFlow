package com.uerj.gymflow.controller;

import com.uerj.gymflow.dao.ProfessorDAO;
import com.uerj.gymflow.model.Professor;
import com.uerj.gymflow.view.ProfessorView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Controlador para gerenciar operações relacionadas a Professores
 * Implementa o padrão MVC, fazendo a intermediação entre View e DAO
 */
public class ProfessorController {
    private static final Logger logger = LoggerFactory.getLogger(ProfessorController.class);
    private final ProfessorDAO professorDAO;
    private final ProfessorView professorView;

    public ProfessorController(Scanner scanner) {
        this.professorDAO = new ProfessorDAO();
        this.professorView = new ProfessorView(scanner);
    }

    /**
     * Exibe o menu de opções de professor e processa a escolha do usuário
     */
    public void mostrarMenu() {
        int opcao = -1;
        
        while (opcao != 0) {
            try {
                opcao = professorView.exibirMenu();
                
                switch (opcao) {
                    case 1:
                        cadastrarProfessor();
                        break;
                    case 2:
                        buscarProfessorPorId();
                        break;
                    case 3:
                        buscarProfessorPorCref();
                        break;
                    case 4:
                        listarTodosProfessores();
                        break;
                    case 5:
                        atualizarProfessor();
                        break;
                    case 6:
                        excluirProfessor();
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
                professorView.exibirMensagemErro(e.getMessage());
                logger.error("Erro no menu de professor", e);
            }
        }
    }

    /**
     * Coleta informações e cadastra um novo professor
     */
    private void cadastrarProfessor() {
        try {
            Professor novoProfessor = professorView.coletarDadosProfessor(null);
            
            if (novoProfessor != null) {
                // Verificar se já existe um professor com este CPF, e-mail ou CREF
                Professor professorExistente = professorDAO.buscarPorCref(novoProfessor.getCref());
                if (professorExistente != null) {
                    professorView.exibirMensagemErro("Já existe um professor com este CREF.");
                    return;
                }
                
                professorDAO.inserir(novoProfessor);
                professorView.exibirMensagemCadastroSucesso(novoProfessor);
                logger.info("Professor cadastrado com sucesso: ID {}", novoProfessor.getIdProfessor());
            }
        } catch (Exception e) {
            professorView.exibirMensagemErro("Erro ao cadastrar professor: " + e.getMessage());
            logger.error("Erro ao cadastrar professor", e);
        }
    }

    /**
     * Busca um professor pelo ID
     */
    private void buscarProfessorPorId() {
        try {
            Integer id = professorView.solicitarId();
            
            if (id != null) {
                Professor professor = professorDAO.buscarPorId(id);
                professorView.exibirDadosProfessor(professor);
                
                if (professor != null) {
                    logger.info("Professor encontrado por ID: {}", id);
                } else {
                    logger.info("Nenhum professor encontrado com ID: {}", id);
                }
            }
        } catch (Exception e) {
            professorView.exibirMensagemErro("Erro ao buscar professor: " + e.getMessage());
            logger.error("Erro ao buscar professor por ID", e);
        }
    }

    /**
     * Busca um professor pelo CREF
     */
    private void buscarProfessorPorCref() {
        try {
            String cref = professorView.solicitarCref();
            
            if (cref != null && !cref.isEmpty()) {
                Professor professor = professorDAO.buscarPorCref(cref);
                professorView.exibirDadosProfessor(professor);
                
                if (professor != null) {
                    logger.info("Professor encontrado por CREF: {}", cref);
                } else {
                    logger.info("Nenhum professor encontrado com CREF: {}", cref);
                }
            }
        } catch (Exception e) {
            professorView.exibirMensagemErro("Erro ao buscar professor por CREF: " + e.getMessage());
            logger.error("Erro ao buscar professor por CREF", e);
        }
    }

    /**
     * Lista todos os professores cadastrados
     */
    private void listarTodosProfessores() {
        try {
            List<Professor> professores = professorDAO.listarTodos();
            professorView.exibirListaProfessores(professores);
            logger.info("Listagem de professores concluída. Total: {}", professores.size());
        } catch (Exception e) {
            professorView.exibirMensagemErro("Erro ao listar professores: " + e.getMessage());
            logger.error("Erro ao listar professores", e);
        }
    }

    /**
     * Atualiza os dados de um professor existente
     */
    private void atualizarProfessor() {
        try {
            Integer id = professorView.solicitarId();
            
            if (id != null) {
                Professor professorExistente = professorDAO.buscarPorId(id);
                
                if (professorExistente == null) {
                    professorView.exibirMensagemErro("Professor não encontrado.");
                    return;
                }
                
                professorView.exibirDadosProfessor(professorExistente);
                
                Professor professorAtualizado = professorView.coletarDadosProfessor(professorExistente);
                
                if (professorAtualizado != null) {
                    // Verificar se o CREF foi alterado e se já existe outro professor com este CREF
                    if (!professorAtualizado.getCref().equals(professorExistente.getCref())) {
                        Professor professorComMesmoCref = professorDAO.buscarPorCref(professorAtualizado.getCref());
                        if (professorComMesmoCref != null && !professorComMesmoCref.getIdProfessor().equals(id)) {
                            professorView.exibirMensagemErro("Já existe outro professor com este CREF.");
                            return;
                        }
                    }
                    
                    professorAtualizado.setIdProfessor(id);
                    professorDAO.atualizar(professorAtualizado);
                    professorView.exibirMensagemAtualizacaoSucesso();
                    logger.info("Professor atualizado com sucesso: ID {}", id);
                }
            }
        } catch (Exception e) {
            professorView.exibirMensagemErro("Erro ao atualizar professor: " + e.getMessage());
            logger.error("Erro ao atualizar professor", e);
        }
    }

    /**
     * Exclui um professor do sistema
     */
    private void excluirProfessor() {
        try {
            Integer id = professorView.solicitarId();
            
            if (id != null) {
                Professor professorExistente = professorDAO.buscarPorId(id);
                
                if (professorExistente == null) {
                    professorView.exibirMensagemErro("Professor não encontrado.");
                    return;
                }
                
                professorView.exibirDadosProfessor(professorExistente);
                
                if (professorView.confirmarExclusao()) {
                    professorDAO.excluir(id);
                    professorView.exibirMensagemExclusaoSucesso();
                    logger.info("Professor excluído com sucesso: ID {}", id);
                } else {
                    professorView.exibirMensagemOperacaoCancelada();
                    logger.info("Exclusão de professor cancelada: ID {}", id);
                }
            }
        } catch (Exception e) {
            professorView.exibirMensagemErro("Erro ao excluir professor: " + e.getMessage());
            logger.error("Erro ao excluir professor", e);
        }
    }
}
