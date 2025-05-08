package com.uerj.gymflow.controller;

import com.uerj.gymflow.dao.ExercicioDAO;
import com.uerj.gymflow.dao.ProfessorDAO;
import com.uerj.gymflow.dao.TreinoDAO;
import com.uerj.gymflow.model.Exercicio;
import com.uerj.gymflow.model.Professor;
import com.uerj.gymflow.model.Treino;
import com.uerj.gymflow.model.TreinoExercicio;
import com.uerj.gymflow.view.TreinoView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Controlador para gerenciar operações relacionadas a Treinos
 * Implementa o padrão MVC, fazendo a intermediação entre View e DAO
 */
public class TreinoController {
    private static final Logger logger = LoggerFactory.getLogger(TreinoController.class);
    private final TreinoDAO treinoDAO;
    private final TreinoView treinoView;
    private final ExercicioDAO exercicioDAO;
    private final ProfessorDAO professorDAO;

    public TreinoController(Scanner scanner) {
        this.treinoDAO = new TreinoDAO();
        this.treinoView = new TreinoView(scanner);
        this.exercicioDAO = new ExercicioDAO();
        this.professorDAO = new ProfessorDAO();
    }

    /**
     * Exibe o menu de opções de treinos e processa a escolha do usuário
     */
    public void mostrarMenu() {
        int opcao = -1;
        
        while (opcao != 0) {
            try {
                opcao = treinoView.exibirMenu();
                
                switch (opcao) {
                    case 1:
                        cadastrarTreino(null);
                        break;
                    case 2:
                        listarTreinos();
                        break;
                    case 3:
                        buscarTreinoPorId();
                        break;
                    case 4:
                        buscarTreinosPorProfessor();
                        break;
                    case 5:
                        atualizarTreino();
                        break;
                    case 6:
                        gerenciarExerciciosTreino();
                        break;
                    case 7:
                        excluirTreino();
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
                treinoView.exibirMensagemErro(e.getMessage());
                logger.error("Erro no menu de treinos", e);
            }
        }
    }

    /**
     * Cadastra um novo treino
     * @param professorId ID do professor (opcional, pode ser null)
     */
    private void cadastrarTreino(Integer professorId) {
        try {
            Treino novoTreino = treinoView.coletarDadosTreino(professorId);
            
            if (novoTreino != null) {
                // Verificar se o professor existe, caso tenha sido informado
                if (novoTreino.getIdProfessor() != null) {
                    Professor professor = professorDAO.buscarPorId(novoTreino.getIdProfessor());
                    if (professor == null) {
                        treinoView.exibirMensagemErro("Professor com ID " + novoTreino.getIdProfessor() + " não encontrado.");
                        logger.warn("Tentativa de cadastrar treino com professor inexistente: ID {}", novoTreino.getIdProfessor());
                        return;
                    }
                    novoTreino.setProfessor(professor);
                    logger.info("Professor associado ao treino: {}", professor.getNome());
                }
                
                // Inserir treino no banco de dados
                treinoDAO.inserir(novoTreino);
                logger.info("Treino cadastrado com sucesso: ID {}", novoTreino.getIdTreino());
                
                // Solicitar seleção de exercícios para o treino
                List<Exercicio> exerciciosDisponiveis = exercicioDAO.listarTodos();
                List<Exercicio> exerciciosSelecionados = treinoView.selecionarExercicios(exerciciosDisponiveis, null);
                
                if (exerciciosSelecionados != null && !exerciciosSelecionados.isEmpty()) {
                    novoTreino.setExercicios(exerciciosSelecionados);
                    treinoDAO.associarExercicios(novoTreino);
                    logger.info("{} exercícios associados ao treino ID {}", exerciciosSelecionados.size(), novoTreino.getIdTreino());
                } else {
                    logger.info("Nenhum exercício foi selecionado para o treino ID {}", novoTreino.getIdTreino());
                }
                
                treinoView.exibirMensagemSucesso("Treino cadastrado com sucesso. ID: " + novoTreino.getIdTreino());
                
                // Exibir os dados do treino cadastrado
                Treino treinoCadastrado = treinoDAO.buscarPorId(novoTreino.getIdTreino());
                treinoCadastrado.setExercicios(exercicioDAO.buscarPorTreino(novoTreino.getIdTreino()));
                treinoView.exibirDadosTreino(treinoCadastrado);
            }
        } catch (Exception e) {
            treinoView.exibirMensagemErro("Erro ao cadastrar treino: " + e.getMessage());
            logger.error("Erro ao cadastrar treino", e);
        }
    }

    /**
     * Lista todos os treinos cadastrados
     */
    private void listarTreinos() {
        try {
            List<Treino> treinos = treinoDAO.listarTodos();
            logger.info("Listagem de todos os treinos. Total: {}", treinos.size());
            
            // Carregar exercícios para cada treino
            for (Treino treino : treinos) {
                treino.setExercicios(exercicioDAO.buscarPorTreino(treino.getIdTreino()));
            }
            
            treinoView.exibirListaTreinos(treinos);
        } catch (Exception e) {
            treinoView.exibirMensagemErro("Erro ao listar treinos: " + e.getMessage());
            logger.error("Erro ao listar treinos", e);
        }
    }

    /**
     * Busca um treino pelo ID
     * @return Treino encontrado ou null
     */
    private Treino buscarTreinoPorId() {
        try {
            Integer id = treinoView.solicitarIdTreino();
            
            if (id != null) {
                logger.info("Buscando treino com ID: {}", id);
                Treino treino = treinoDAO.buscarPorId(id);
                
                if (treino != null) {
                    // Carregar exercícios do treino
                    treino.setExercicios(exercicioDAO.buscarPorTreino(treino.getIdTreino()));
                    
                    treinoView.exibirDadosTreino(treino);
                    logger.info("Treino encontrado por ID: {}", id);
                    return treino;
                } else {
                    treinoView.exibirMensagemNaoEncontrado("Treino com ID " + id + " não encontrado.");
                    logger.info("Nenhum treino encontrado com ID: {}", id);
                    return null;
                }
            }
            return null;
        } catch (Exception e) {
            treinoView.exibirMensagemErro("Erro ao buscar treino: " + e.getMessage());
            logger.error("Erro ao buscar treino por ID", e);
            return null;
        }
    }

    /**
     * Busca treinos associados a um professor
     */
    private void buscarTreinosPorProfessor() {
        try {
            Integer idProfessor = treinoView.solicitarIdProfessor();
            
            if (idProfessor != null) {
                logger.info("Buscando treinos do professor com ID: {}", idProfessor);
                
                // Verificar se o professor existe
                Professor professor = professorDAO.buscarPorId(idProfessor);
                if (professor == null) {
                    treinoView.exibirMensagemNaoEncontrado("Professor com ID " + idProfessor + " não encontrado.");
                    logger.warn("Professor com ID {} não encontrado", idProfessor);
                    return;
                }
                
                List<Treino> treinos = treinoDAO.buscarPorProfessor(idProfessor);
                logger.info("Encontrados {} treinos para o professor {}", treinos.size(), professor.getNome());
                
                // Carregar exercícios para cada treino
                for (Treino treino : treinos) {
                    treino.setExercicios(exercicioDAO.buscarPorTreino(treino.getIdTreino()));
                    treino.setProfessor(professor);
                }
                
                if (treinos.isEmpty()) {
                    treinoView.exibirMensagemNaoEncontrado("Nenhum treino encontrado para o professor " + professor.getNome() + ".");
                } else {
                    treinoView.exibirListaTreinos(treinos);
                }
            }
        } catch (Exception e) {
            treinoView.exibirMensagemErro("Erro ao buscar treinos por professor: " + e.getMessage());
            logger.error("Erro ao buscar treinos por professor", e);
        }
    }

    /**
     * Atualiza os dados de um treino existente
     */
    private void atualizarTreino() {
        try {
            Treino treinoExistente = buscarTreinoPorId();
            
            if (treinoExistente == null) {
                return;
            }
            
            Treino treinoAtualizado = treinoView.coletarDadosAtualizacaoTreino(treinoExistente);
            
            if (treinoAtualizado != null) {
                // Verificar se o professor existe, caso tenha sido alterado
                if (treinoAtualizado.getIdProfessor() != null && 
                    !treinoAtualizado.getIdProfessor().equals(treinoExistente.getIdProfessor())) {
                    
                    Professor professor = professorDAO.buscarPorId(treinoAtualizado.getIdProfessor());
                    if (professor == null) {
                        treinoView.exibirMensagemErro("Professor com ID " + treinoAtualizado.getIdProfessor() + " não encontrado.");
                        logger.warn("Professor com ID {} não encontrado durante atualização de treino", treinoAtualizado.getIdProfessor());
                        return;
                    }
                    treinoAtualizado.setProfessor(professor);
                    logger.info("Professor do treino alterado para: {}", professor.getNome());
                }
                
                treinoDAO.atualizar(treinoAtualizado);
                treinoView.exibirMensagemSucesso("Treino atualizado com sucesso.");
                logger.info("Treino atualizado com sucesso: ID {}", treinoAtualizado.getIdTreino());
                
                // Buscar treino atualizado para exibir
                Treino treinoAposAtualizacao = treinoDAO.buscarPorId(treinoAtualizado.getIdTreino());
                treinoAposAtualizacao.setExercicios(exercicioDAO.buscarPorTreino(treinoAtualizado.getIdTreino()));
                treinoView.exibirDadosTreino(treinoAposAtualizacao);
            }
        } catch (Exception e) {
            treinoView.exibirMensagemErro("Erro ao atualizar treino: " + e.getMessage());
            logger.error("Erro ao atualizar treino", e);
        }
    }

    /**
     * Exclui um treino do sistema
     */
    private void excluirTreino() {
        try {
            Treino treino = buscarTreinoPorId();
            
            if (treino == null) {
                return;
            }
            
            if (treinoView.confirmarExclusao(treino)) {
                treinoDAO.excluir(treino.getIdTreino());
                treinoView.exibirMensagemSucesso("Treino excluído com sucesso.");
                logger.info("Treino excluído com sucesso: ID {}", treino.getIdTreino());
            } else {
                treinoView.exibirMensagemOperacaoCancelada();
                logger.info("Exclusão de treino cancelada: ID {}", treino.getIdTreino());
            }
        } catch (Exception e) {
            treinoView.exibirMensagemErro("Erro ao excluir treino: " + e.getMessage());
            logger.error("Erro ao excluir treino", e);
        }
    }

    /**
     * Gerencia os exercícios de um treino
     */
    private void gerenciarExerciciosTreino() {
        try {
            Treino treino = buscarTreinoPorId();
            
            if (treino == null) {
                return;
            }
            
            logger.info("Gerenciando exercícios do treino ID: {} - {}", treino.getIdTreino(), treino.getNomeTreino());
            
            int opcao = -1;
            do {
                try {
                    opcao = treinoView.exibirMenuExercicios();
                    
                    switch (opcao) {
                        case 1:
                            adicionarExercicioAoTreino(treino);
                            break;
                        case 2:
                            listarExerciciosDoTreino(treino);
                            break;
                        case 3:
                            atualizarDetalhesExercicio(treino);
                            break;
                        case 4:
                            removerExercicioDoTreino(treino);
                            break;
                        case 0:
                            logger.info("Saindo do gerenciamento de exercícios do treino ID: {}", treino.getIdTreino());
                            break;
                        default:
                            if (opcao != -1) {
                                System.out.println("Opção inválida. Tente novamente.");
                            }
                    }
                } catch (Exception e) {
                    treinoView.exibirMensagemErro(e.getMessage());
                    logger.error("Erro no menu de exercícios do treino", e);
                }
            } while (opcao != 0);
        } catch (Exception e) {
            treinoView.exibirMensagemErro("Erro ao gerenciar exercícios do treino: " + e.getMessage());
            logger.error("Erro ao gerenciar exercícios do treino", e);
        }
    }

    /**
     * Adiciona um ou mais exercícios a um treino
     * @param treino Treino ao qual os exercícios serão adicionados
     */
    private void adicionarExercicioAoTreino(Treino treino) {
        try {
            // Buscar todos os exercícios disponíveis
            List<Exercicio> exerciciosDisponiveis = exercicioDAO.listarTodos();
            logger.info("Total de exercícios disponíveis: {}", exerciciosDisponiveis.size());
            
            // Buscar exercícios já associados ao treino
            List<Exercicio> exerciciosAtuais = exercicioDAO.buscarPorTreino(treino.getIdTreino());
            logger.info("Exercícios já associados ao treino: {}", exerciciosAtuais.size());
            
            // Solicitar seleção de exercícios
            List<Exercicio> exerciciosSelecionados = treinoView.selecionarExercicios(exerciciosDisponiveis, exerciciosAtuais);
            
            if (exerciciosSelecionados == null || exerciciosSelecionados.isEmpty()) {
                treinoView.exibirMensagemOperacaoCancelada();
                logger.info("Operação de adicionar exercícios cancelada para o treino ID: {}", treino.getIdTreino());
                return;
            }
            
            // Adicionar exercícios ao treino
            treino.setExercicios(exerciciosSelecionados);
            treinoDAO.associarExercicios(treino);
            
            treinoView.exibirMensagemSucesso(exerciciosSelecionados.size() + " exercício(s) adicionado(s) ao treino com sucesso.");
            logger.info("{} exercício(s) adicionado(s) ao treino ID: {}", exerciciosSelecionados.size(), treino.getIdTreino());
        } catch (Exception e) {
            treinoView.exibirMensagemErro("Erro ao adicionar exercícios ao treino: " + e.getMessage());
            logger.error("Erro ao adicionar exercícios ao treino", e);
        }
    }

    /**
     * Lista os exercícios de um treino com seus detalhes
     * @param treino Treino cujos exercícios serão listados
     */
    private void listarExerciciosDoTreino(Treino treino) {
        try {
            List<TreinoExercicio> detalhes = treinoDAO.buscarDetalhesExercicios(treino.getIdTreino());
            logger.info("Listando {} exercícios do treino ID: {}", detalhes.size(), treino.getIdTreino());
            
            treinoView.exibirDetalhesExercicios(detalhes);
        } catch (Exception e) {
            treinoView.exibirMensagemErro("Erro ao listar exercícios do treino: " + e.getMessage());
            logger.error("Erro ao listar exercícios do treino", e);
        }
    }

    /**
     * Atualiza os detalhes de um exercício no treino
     * @param treino Treino que contém o exercício
     */
    private void atualizarDetalhesExercicio(Treino treino) {
        try {
            List<TreinoExercicio> detalhes = treinoDAO.buscarDetalhesExercicios(treino.getIdTreino());
            
            if (detalhes.isEmpty()) {
                treinoView.exibirMensagemNaoEncontrado("Treino não possui exercícios.");
                logger.info("Tentativa de atualizar exercícios em treino sem exercícios: ID {}", treino.getIdTreino());
                return;
            }
            
            Integer idExercicio = treinoView.selecionarExercicioDoTreino(detalhes);
            
            if (idExercicio == null) {
                logger.info("Seleção de exercício cancelada para atualização");
                return;
            }
            
            logger.info("Atualizando detalhes do exercício ID: {} no treino ID: {}", idExercicio, treino.getIdTreino());
            
            // Buscar detalhes específicos do exercício selecionado
            TreinoExercicio detalhesAtuais = treinoDAO.buscarDetalheExercicio(treino.getIdTreino(), idExercicio);
            
            if (detalhesAtuais == null) {
                treinoView.exibirMensagemErro("Detalhes do exercício não encontrados.");
                logger.warn("Detalhes do exercício ID: {} não encontrados no treino ID: {}", idExercicio, treino.getIdTreino());
                return;
            }
            
            // Coletar novos detalhes
            TreinoExercicio novosDetalhes = treinoView.coletarDetalhesExercicio(detalhesAtuais);
            
            if (novosDetalhes == null) {
                treinoView.exibirMensagemOperacaoCancelada();
                logger.info("Atualização de detalhes do exercício cancelada pelo usuário");
                return;
            }
            
            // Atualizar detalhes no banco de dados
            treinoDAO.atualizarTreinoExercicio(novosDetalhes);
            treinoView.exibirMensagemSucesso("Detalhes do exercício atualizados com sucesso.");
            logger.info("Detalhes do exercício ID: {} atualizados no treino ID: {}", idExercicio, treino.getIdTreino());
        } catch (Exception e) {
            treinoView.exibirMensagemErro("Erro ao atualizar detalhes do exercício: " + e.getMessage());
            logger.error("Erro ao atualizar detalhes do exercício", e);
        }
    }

    /**
     * Remove um exercício de um treino
     * @param treino Treino do qual o exercício será removido
     */
    private void removerExercicioDoTreino(Treino treino) {
        try {
            List<TreinoExercicio> detalhes = treinoDAO.buscarDetalhesExercicios(treino.getIdTreino());
            
            if (detalhes.isEmpty()) {
                treinoView.exibirMensagemNaoEncontrado("Treino não possui exercícios.");
                logger.info("Tentativa de remover exercício de treino sem exercícios: ID {}", treino.getIdTreino());
                return;
            }
            
            Integer idExercicio = treinoView.selecionarExercicioDoTreino(detalhes);
            
            if (idExercicio == null) {
                logger.info("Seleção de exercício cancelada para remoção");
                return;
            }
            
            // Buscar o exercício para confirmação
            Exercicio exercicio = null;
            for (TreinoExercicio detalhe : detalhes) {
                if (detalhe.getIdExercicio().equals(idExercicio)) {
                    exercicio = detalhe.getExercicio();
                    break;
                }
            }
            
            if (exercicio == null) {
                treinoView.exibirMensagemErro("Exercício não encontrado.");
                logger.warn("Exercício ID: {} não encontrado no treino ID: {} durante remoção", idExercicio, treino.getIdTreino());
                return;
            }
            
            if (treinoView.confirmarRemocaoExercicio(exercicio)) {
                treinoDAO.removerExercicio(treino.getIdTreino(), idExercicio);
                treinoView.exibirMensagemSucesso("Exercício removido do treino com sucesso.");
                logger.info("Exercício ID: {} ({}) removido do treino ID: {}", 
                        exercicio.getIdExercicio(), exercicio.getNomeExercicio(), treino.getIdTreino());
            } else {
                treinoView.exibirMensagemOperacaoCancelada();
                logger.info("Remoção de exercício cancelada pelo usuário");
            }
        } catch (Exception e) {
            treinoView.exibirMensagemErro("Erro ao remover exercício do treino: " + e.getMessage());
            logger.error("Erro ao remover exercício do treino", e);
        }
    }
}
