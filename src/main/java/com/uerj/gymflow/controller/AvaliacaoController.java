package com.uerj.gymflow.controller;

import com.uerj.gymflow.dao.AlunoDAO;
import com.uerj.gymflow.dao.AvaliacaoDAO;
import com.uerj.gymflow.dao.ProfessorDAO;
import com.uerj.gymflow.model.Aluno;
import com.uerj.gymflow.model.Avaliacao;
import com.uerj.gymflow.model.Professor;
import com.uerj.gymflow.view.AvaliacaoView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Controlador para gerenciar operações relacionadas a Avaliações
 * Implementa o padrão MVC, fazendo a intermediação entre View e DAO
 */
public class AvaliacaoController {
    private static final Logger logger = LoggerFactory.getLogger(AvaliacaoController.class);
    private final AvaliacaoDAO avaliacaoDAO;
    private final AlunoDAO alunoDAO;
    private final ProfessorDAO professorDAO;
    private final AvaliacaoView avaliacaoView;

    public AvaliacaoController(Scanner scanner) {
        this.avaliacaoDAO = new AvaliacaoDAO();
        this.alunoDAO = new AlunoDAO();
        this.professorDAO = new ProfessorDAO();
        this.avaliacaoView = new AvaliacaoView(scanner);
    }

    /**
     * Exibe o menu de opções de avaliação e processa a escolha do usuário
     */
    public void mostrarMenu() {
        int opcao = -1;
        
        while (opcao != 0) {
            try {
                opcao = avaliacaoView.exibirMenu();
                
                switch (opcao) {
                    case 1:
                        cadastrarAvaliacao();
                        break;
                    case 2:
                        buscarAvaliacaoPorId();
                        break;
                    case 3:
                        listarAvaliacoesPorAluno();
                        break;
                    case 4:
                        atualizarAvaliacao();
                        break;
                    case 5:
                        excluirAvaliacao();
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
                avaliacaoView.exibirMensagemErro(e.getMessage());
                logger.error("Erro no menu de avaliação", e);
            }
        }
    }

    /**
     * Coleta informações e cadastra uma nova avaliação
     */
    private void cadastrarAvaliacao() {
        try {
            // Carregar lista de alunos disponíveis
            List<Aluno> alunosDisponiveis = alunoDAO.listarTodos();
            if (alunosDisponiveis.isEmpty()) {
                avaliacaoView.exibirMensagemErro("Não há alunos cadastrados no sistema. Cadastre um aluno primeiro.");
                return;
            }
            
            // Carregar lista de professores disponíveis
            List<Professor> professoresDisponiveis = professorDAO.listarTodos();
            if (professoresDisponiveis.isEmpty()) {
                avaliacaoView.exibirMensagemErro("Não há professores cadastrados no sistema. Os dados do professor são opcionais, mas recomendados.");
            }
            
            // Coletar dados da avaliação, incluindo a seleção de aluno e professor
            Avaliacao novaAvaliacao = avaliacaoView.coletarDadosAvaliacao(null, alunosDisponiveis, professoresDisponiveis);
            
            if (novaAvaliacao != null) {
                // Inserir a avaliação
                Avaliacao avaliacaoInserida = avaliacaoDAO.inserir(novaAvaliacao);
                avaliacaoView.exibirMensagemCadastroSucesso(avaliacaoInserida);
                logger.info("Avaliação cadastrada com sucesso: ID {}", avaliacaoInserida.getIdAvaliacao());
                
                // Exibir os dados da avaliação cadastrada
                avaliacaoView.exibirDadosAvaliacao(avaliacaoInserida);
            }
        } catch (Exception e) {
            avaliacaoView.exibirMensagemErro("Erro ao cadastrar avaliação: " + e.getMessage());
            logger.error("Erro ao cadastrar avaliação", e);
        }
    }

    /**
     * Busca uma avaliação pelo ID
     */
    private void buscarAvaliacaoPorId() {
        try {
            Integer id = avaliacaoView.solicitarIdAvaliacao();
            
            if (id != null) {
                Avaliacao avaliacao = avaliacaoDAO.buscarPorId(id);
                avaliacaoView.exibirDadosAvaliacao(avaliacao);
                
                if (avaliacao != null) {
                    logger.info("Avaliação encontrada por ID: {}", id);
                } else {
                    logger.info("Nenhuma avaliação encontrada com ID: {}", id);
                }
            }
        } catch (Exception e) {
            avaliacaoView.exibirMensagemErro("Erro ao buscar avaliação: " + e.getMessage());
            logger.error("Erro ao buscar avaliação por ID", e);
        }
    }

    /**
     * Lista todas as avaliações de um aluno
     */
    private void listarAvaliacoesPorAluno() {
        try {
            Integer idAluno = avaliacaoView.solicitarIdAluno();
            
            if (idAluno != null) {
                Aluno aluno = alunoDAO.buscarPorId(idAluno);
                
                if (aluno == null) {
                    avaliacaoView.exibirMensagemErro("Aluno não encontrado.");
                    return;
                }
                
                List<Avaliacao> avaliacoes = avaliacaoDAO.listarPorAluno(idAluno);
                avaliacaoView.exibirListaAvaliacoes(avaliacoes, aluno);
                logger.info("Listagem de avaliações do aluno ID {} concluída. Total: {}", idAluno, avaliacoes.size());
            }
        } catch (Exception e) {
            avaliacaoView.exibirMensagemErro("Erro ao listar avaliações: " + e.getMessage());
            logger.error("Erro ao listar avaliações do aluno", e);
        }
    }

    /**
     * Atualiza os dados de uma avaliação existente
     */
    private void atualizarAvaliacao() {
        try {
            Integer id = avaliacaoView.solicitarIdAvaliacao();
            
            if (id != null) {
                Avaliacao avaliacaoExistente = avaliacaoDAO.buscarPorId(id);
                
                if (avaliacaoExistente == null) {
                    avaliacaoView.exibirMensagemErro("Avaliação não encontrada.");
                    return;
                }
                
                avaliacaoView.exibirDadosAvaliacao(avaliacaoExistente);
                
                // Carregar lista de professores disponíveis para possível troca de professor
                List<Professor> professoresDisponiveis = professorDAO.listarTodos();
                
                // Na atualização, não permitimos trocar o aluno, apenas os dados da avaliação e o professor
                Avaliacao avaliacaoAtualizada = avaliacaoView.coletarDadosAvaliacao(
                        avaliacaoExistente, null, professoresDisponiveis);
                
                if (avaliacaoAtualizada != null) {
                    // Preservar o ID original
                    avaliacaoAtualizada.setIdAvaliacao(id);
                    
                    // Preservar o ID do aluno
                    avaliacaoAtualizada.setIdAluno(avaliacaoExistente.getIdAluno());
                    avaliacaoAtualizada.setAluno(avaliacaoExistente.getAluno());
                    
                    avaliacaoDAO.atualizar(avaliacaoAtualizada);
                    avaliacaoView.exibirMensagemAtualizacaoSucesso();
                    logger.info("Avaliação atualizada com sucesso: ID {}", id);
                    
                    // Exibir os dados atualizados
                    Avaliacao avaliacaoAtualizadaCompleta = avaliacaoDAO.buscarPorId(id);
                    if (avaliacaoAtualizadaCompleta != null) {
                        avaliacaoView.exibirDadosAvaliacao(avaliacaoAtualizadaCompleta);
                    }
                }
            }
        } catch (Exception e) {
            avaliacaoView.exibirMensagemErro("Erro ao atualizar avaliação: " + e.getMessage());
            logger.error("Erro ao atualizar avaliação", e);
        }
    }

    /**
     * Exclui uma avaliação do sistema
     */
    private void excluirAvaliacao() {
        try {
            Integer id = avaliacaoView.solicitarIdAvaliacao();
            
            if (id != null) {
                Avaliacao avaliacaoExistente = avaliacaoDAO.buscarPorId(id);
                
                if (avaliacaoExistente == null) {
                    avaliacaoView.exibirMensagemErro("Avaliação não encontrada.");
                    return;
                }
                
                avaliacaoView.exibirDadosAvaliacao(avaliacaoExistente);
                
                if (avaliacaoView.confirmarExclusao()) {
                    avaliacaoDAO.excluir(id);
                    avaliacaoView.exibirMensagemExclusaoSucesso();
                    logger.info("Avaliação excluída com sucesso: ID {}", id);
                } else {
                    avaliacaoView.exibirMensagemOperacaoCancelada();
                    logger.info("Exclusão de avaliação cancelada: ID {}", id);
                }
            }
        } catch (Exception e) {
            avaliacaoView.exibirMensagemErro("Erro ao excluir avaliação: " + e.getMessage());
            logger.error("Erro ao excluir avaliação", e);
        }
    }
}
