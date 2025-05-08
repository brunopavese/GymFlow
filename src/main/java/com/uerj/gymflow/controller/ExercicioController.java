package com.uerj.gymflow.controller;

import com.uerj.gymflow.dao.ExercicioDAO;
import com.uerj.gymflow.model.Exercicio;
import com.uerj.gymflow.view.ExercicioView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Controlador para gerenciar operações relacionadas a Exercícios
 * Implementa o padrão MVC, fazendo a intermediação entre View e DAO
 */
public class ExercicioController {
    private static final Logger logger = LoggerFactory.getLogger(ExercicioController.class);
    private final ExercicioDAO exercicioDAO;
    private final ExercicioView exercicioView;

    public ExercicioController(Scanner scanner) {
        this.exercicioDAO = new ExercicioDAO();
        this.exercicioView = new ExercicioView(scanner);
    }

    /**
     * Exibe o menu de opções de exercícios e processa a escolha do usuário
     */
    public void mostrarMenu() {
        int opcao = -1;
        
        while (opcao != 0) {
            try {
                opcao = exercicioView.exibirMenu();
                
                switch (opcao) {
                    case 1:
                        cadastrarExercicio();
                        break;
                    case 2:
                        buscarExercicioPorId();
                        break;
                    case 3:
                        listarTodosExercicios();
                        break;
                    case 4:
                        atualizarExercicio();
                        break;
                    case 5:
                        excluirExercicio();
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
                exercicioView.exibirMensagemErro(e.getMessage());
                logger.error("Erro no menu de exercícios", e);
            }
        }
    }

    /**
     * Coleta informações e cadastra um novo exercício
     */
    private void cadastrarExercicio() {
        try {
            Exercicio novoExercicio = exercicioView.coletarDadosExercicio(null);
            
            if (novoExercicio != null) {
                exercicioDAO.inserir(novoExercicio);
                exercicioView.exibirMensagemCadastroSucesso(novoExercicio);
                logger.info("Exercício cadastrado com sucesso: ID {}", novoExercicio.getIdExercicio());
                
                // Exibir os dados do exercício cadastrado
                exercicioView.exibirDadosExercicio(novoExercicio);
            }
        } catch (Exception e) {
            exercicioView.exibirMensagemErro("Erro ao cadastrar exercício: " + e.getMessage());
            logger.error("Erro ao cadastrar exercício", e);
        }
    }

    /**
     * Busca um exercício pelo ID
     */
    private void buscarExercicioPorId() {
        try {
            Integer id = exercicioView.solicitarIdExercicio();
            
            if (id != null) {
                Exercicio exercicio = exercicioDAO.buscarPorId(id);
                exercicioView.exibirDadosExercicio(exercicio);
                
                if (exercicio != null) {
                    logger.info("Exercício encontrado por ID: {}", id);
                } else {
                    logger.info("Nenhum exercício encontrado com ID: {}", id);
                }
            }
        } catch (Exception e) {
            exercicioView.exibirMensagemErro("Erro ao buscar exercício: " + e.getMessage());
            logger.error("Erro ao buscar exercício por ID", e);
        }
    }

    /**
     * Lista todos os exercícios cadastrados
     */
    private void listarTodosExercicios() {
        try {
            List<Exercicio> exercicios = exercicioDAO.listarTodos();
            exercicioView.exibirListaExercicios(exercicios);
            logger.info("Listagem de todos os exercícios concluída. Total: {}", exercicios.size());
        } catch (Exception e) {
            exercicioView.exibirMensagemErro("Erro ao listar exercícios: " + e.getMessage());
            logger.error("Erro ao listar todos os exercícios", e);
        }
    }

    /**
     * Atualiza os dados de um exercício existente
     */
    private void atualizarExercicio() {
        try {
            Integer id = exercicioView.solicitarIdExercicio();
            
            if (id != null) {
                Exercicio exercicioExistente = exercicioDAO.buscarPorId(id);
                
                if (exercicioExistente == null) {
                    exercicioView.exibirMensagemErro("Exercício não encontrado.");
                    return;
                }
                
                exercicioView.exibirDadosExercicio(exercicioExistente);
                
                Exercicio exercicioAtualizado = exercicioView.coletarDadosExercicio(exercicioExistente);
                
                if (exercicioAtualizado != null) {
                    // Preservar o ID original
                    exercicioAtualizado.setIdExercicio(id);
                    
                    exercicioDAO.atualizar(exercicioAtualizado);
                    exercicioView.exibirMensagemAtualizacaoSucesso();
                    logger.info("Exercício atualizado com sucesso: ID {}", id);
                    
                    // Exibir os dados atualizados
                    exercicioView.exibirDadosExercicio(exercicioAtualizado);
                }
            }
        } catch (Exception e) {
            exercicioView.exibirMensagemErro("Erro ao atualizar exercício: " + e.getMessage());
            logger.error("Erro ao atualizar exercício", e);
        }
    }

    /**
     * Exclui um exercício do sistema
     */
    private void excluirExercicio() {
        try {
            Integer id = exercicioView.solicitarIdExercicio();
            
            if (id != null) {
                Exercicio exercicioExistente = exercicioDAO.buscarPorId(id);
                
                if (exercicioExistente == null) {
                    exercicioView.exibirMensagemErro("Exercício não encontrado.");
                    return;
                }
                
                exercicioView.exibirDadosExercicio(exercicioExistente);
                
                // Verificar se o exercício está em uso em algum treino
                // Esta verificação depende da implementação da tabela de relacionamento
                // entre exercícios e treinos
                
                if (exercicioView.confirmarExclusao()) {
                    exercicioDAO.excluir(id);
                    exercicioView.exibirMensagemExclusaoSucesso();
                    logger.info("Exercício excluído com sucesso: ID {}", id);
                } else {
                    exercicioView.exibirMensagemOperacaoCancelada();
                    logger.info("Exclusão de exercício cancelada: ID {}", id);
                }
            }
        } catch (Exception e) {
            exercicioView.exibirMensagemErro("Erro ao excluir exercício: " + e.getMessage());
            logger.error("Erro ao excluir exercício", e);
        }
    }
}
