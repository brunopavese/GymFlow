package com.uerj.gymflow.controller;

import com.uerj.gymflow.dao.AlunoDAO;
import com.uerj.gymflow.dao.AlunoTreinoDAO;
import com.uerj.gymflow.dao.PlanoDAO;
import com.uerj.gymflow.dao.TreinoDAO;
import com.uerj.gymflow.model.Aluno;
import com.uerj.gymflow.model.AlunoTreino;
import com.uerj.gymflow.model.Plano;
import com.uerj.gymflow.model.Treino;
import com.uerj.gymflow.view.AlunoView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Controlador para gerenciar operações relacionadas a Alunos
 * Implementa o padrão MVC, fazendo a intermediação entre View e DAO
 */
public class AlunoController {
    private static final Logger logger = LoggerFactory.getLogger(AlunoController.class);
    private final AlunoDAO alunoDAO;
    private final PlanoDAO planoDAO;
    private final TreinoDAO treinoDAO;
    private final AlunoTreinoDAO alunoTreinoDAO;
    private final AlunoView alunoView;
    
    public AlunoController(Scanner scanner) {
        this.alunoDAO = new AlunoDAO();
        this.planoDAO = new PlanoDAO();
        this.treinoDAO = new TreinoDAO();
        this.alunoTreinoDAO = new AlunoTreinoDAO();
        this.alunoView = new AlunoView(scanner);
    }

    /**
     * Exibe o menu de opções de aluno e processa a escolha do usuário
     */
    public void mostrarMenu() {
        int opcao = -1;
        
        while (opcao != 0) {
            try {
                opcao = alunoView.exibirMenu();
                
                switch (opcao) {
                    case 1:
                        cadastrarAluno();
                        break;
                    case 2:
                        buscarAlunoPorId();
                        break;
                    case 3:
                        buscarAlunoPorCpf();
                        break;
                    case 4:
                        listarTodosAlunos();
                        break;
                    case 5:
                        atualizarAluno();
                        break;
                    case 6:
                        excluirAluno();
                        break;
                    case 7:
                        gerenciarTreinosAluno();
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
                alunoView.exibirMensagemErro(e.getMessage());
                logger.error("Erro no menu de aluno", e);
            }
        }
    }

    /**
     * Coleta informações e cadastra um novo aluno
     */
    private void cadastrarAluno() {
        try {
            // Carregar lista de planos disponíveis
            List<Plano> planosDisponiveis = planoDAO.listarTodos();
            
            // Coletar dados do aluno, incluindo a seleção de plano
            Aluno novoAluno = alunoView.coletarDadosAluno(null, planosDisponiveis);
            
            if (novoAluno != null) {
                // Verificar se já existe pessoa com mesmo CPF ou e-mail
                if (alunoDAO.buscarPorCpf(novoAluno.getCpf()) != null) {
                    alunoView.exibirMensagemErro("Já existe um aluno com este CPF.");
                    return;
                }
                
                // Inserir o aluno (isso também insere a pessoa automaticamente no AlunoDAO)
                alunoDAO.inserir(novoAluno);
                alunoView.exibirMensagemCadastroSucesso(novoAluno);
                logger.info("Aluno cadastrado com sucesso: ID {}", novoAluno.getIdAluno());
            }
        } catch (Exception e) {
            alunoView.exibirMensagemErro("Erro ao cadastrar aluno: " + e.getMessage());
            logger.error("Erro ao cadastrar aluno", e);
        }
    }

    /**
     * Busca um aluno pelo ID
     */
    private void buscarAlunoPorId() {
        try {
            Integer id = alunoView.solicitarId();
            
            if (id != null) {
                Aluno aluno = alunoDAO.buscarPorId(id);
                alunoView.exibirDadosAluno(aluno);
                
                if (aluno != null) {
                    logger.info("Aluno encontrado por ID: {}", id);
                } else {
                    logger.info("Nenhum aluno encontrado com ID: {}", id);
                }
            }
        } catch (Exception e) {
            alunoView.exibirMensagemErro("Erro ao buscar aluno: " + e.getMessage());
            logger.error("Erro ao buscar aluno por ID", e);
        }
    }

    /**
     * Busca um aluno pelo CPF
     */
    private void buscarAlunoPorCpf() {
        try {
            String cpf = alunoView.solicitarCpf();
            
            if (cpf != null) {
                Aluno aluno = alunoDAO.buscarPorCpf(cpf);
                alunoView.exibirDadosAluno(aluno);
                
                if (aluno != null) {
                    logger.info("Aluno encontrado por CPF: {}", cpf);
                } else {
                    logger.info("Nenhum aluno encontrado com CPF: {}", cpf);
                }
            }
        } catch (Exception e) {
            alunoView.exibirMensagemErro("Erro ao buscar aluno: " + e.getMessage());
            logger.error("Erro ao buscar aluno por CPF", e);
        }
    }

    /**
     * Lista todos os alunos cadastrados
     */
    private void listarTodosAlunos() {
        try {
            List<Aluno> alunos = alunoDAO.listarTodos();
            alunoView.exibirListaAlunos(alunos);
            logger.info("Listagem de alunos concluída. Total: {}", alunos.size());
        } catch (Exception e) {
            alunoView.exibirMensagemErro("Erro ao listar alunos: " + e.getMessage());
            logger.error("Erro ao listar alunos", e);
        }
    }

    /**
     * Atualiza os dados de um aluno existente
     */
    private void atualizarAluno() {
        try {
            Integer id = alunoView.solicitarId();
            
            if (id != null) {
                Aluno alunoExistente = alunoDAO.buscarPorId(id);
                
                if (alunoExistente == null) {
                    alunoView.exibirMensagemErro("Aluno não encontrado.");
                    return;
                }
                
                alunoView.exibirDadosAluno(alunoExistente);
                
                // Carregar lista de planos disponíveis
                List<Plano> planosDisponiveis = planoDAO.listarTodos();
                
                Aluno alunoAtualizado = alunoView.coletarDadosAluno(alunoExistente, planosDisponiveis);
                
                if (alunoAtualizado != null) {
                    // Preservar IDs originais
                    alunoAtualizado.setIdAluno(id);
                    alunoAtualizado.setIdPessoa(alunoExistente.getIdPessoa());
                    
                    // Verificar se o CPF foi alterado e se já existe outro aluno com o novo CPF
                    if (!alunoAtualizado.getCpf().equals(alunoExistente.getCpf())) {
                        Aluno alunoCpfExistente = alunoDAO.buscarPorCpf(alunoAtualizado.getCpf());
                        if (alunoCpfExistente != null && !alunoCpfExistente.getIdAluno().equals(id)) {
                            alunoView.exibirMensagemErro("Já existe um aluno com este CPF.");
                            return;
                        }
                    }
                    
                    alunoDAO.atualizar(alunoAtualizado);
                    alunoView.exibirMensagemAtualizacaoSucesso();
                    logger.info("Aluno atualizado com sucesso: ID {}", id);
                }
            }
        } catch (Exception e) {
            alunoView.exibirMensagemErro("Erro ao atualizar aluno: " + e.getMessage());
            logger.error("Erro ao atualizar aluno", e);
        }
    }

    /**
     * Exclui um aluno do sistema
     */
    private void excluirAluno() {
        try {
            Integer id = alunoView.solicitarId();
            
            if (id != null) {
                Aluno alunoExistente = alunoDAO.buscarPorId(id);
                
                if (alunoExistente == null) {
                    alunoView.exibirMensagemErro("Aluno não encontrado.");
                    return;
                }
                
                alunoView.exibirDadosAluno(alunoExistente);
                
                if (alunoView.confirmarExclusao()) {
                    // Aqui você poderia verificar se existem avaliações ou treinos relacionados ao aluno
                    // Antes de permitir a exclusão
                    
                    alunoDAO.excluir(id);
                    alunoView.exibirMensagemExclusaoSucesso();
                    logger.info("Aluno excluído com sucesso: ID {}", id);
                } else {
                    alunoView.exibirMensagemOperacaoCancelada();
                    logger.info("Exclusão de aluno cancelada: ID {}", id);
                }
            }
        } catch (Exception e) {
            alunoView.exibirMensagemErro("Erro ao excluir aluno: " + e.getMessage());
            logger.error("Erro ao excluir aluno", e);
        }
    }
    
    /**
     * Gerencia os treinos de um aluno específico
     */
    private void gerenciarTreinosAluno() {
        try {
            // Buscar o aluno primeiro
            Integer idAluno = alunoView.solicitarId();
            
            if (idAluno == null) {
                return;
            }
            
            Aluno aluno = alunoDAO.buscarPorId(idAluno);
            
            if (aluno == null) {
                alunoView.exibirMensagemErro("Aluno não encontrado.");
                logger.warn("Tentativa de gerenciar treinos de aluno inexistente: ID {}", idAluno);
                return;
            }
            
            // Exibir informações básicas do aluno
            alunoView.exibirDadosAluno(aluno);
            logger.info("Gerenciando treinos do aluno: ID {} - {}", aluno.getIdAluno(), aluno.getNome());
            
            // Menu de gerenciamento de treinos do aluno
            int opcao = -1;
            do {
                try {
                    opcao = alunoView.exibirMenuTreinosAluno(aluno);
                    
                    switch (opcao) {
                        case 1:
                            associarTreinoAoAluno(aluno);
                            break;
                        case 2:
                            listarTreinosDoAluno(aluno);
                            break;
                        case 3:
                            listarTreinosAtivosDoAluno(aluno);
                            break;
                        case 4:
                            atualizarTreinoDoAluno(aluno);
                            break;
                        case 5:
                            encerrarTreinoDoAluno(aluno);
                            break;
                        case 6:
                            renovarTreinoDoAluno(aluno);
                            break;
                        case 7:
                            removerTreinoDoAluno(aluno);
                            break;
                        case 0:
                            logger.info("Saindo do gerenciamento de treinos do aluno: ID {}", aluno.getIdAluno());
                            break;
                        default:
                            if (opcao != -1) {
                                System.out.println("Opção inválida. Tente novamente.");
                            }
                    }
                } catch (Exception e) {
                    alunoView.exibirMensagemErro("Erro: " + e.getMessage());
                    logger.error("Erro no menu de treinos do aluno", e);
                }
            } while (opcao != 0);
            
        } catch (Exception e) {
            alunoView.exibirMensagemErro("Erro ao gerenciar treinos do aluno: " + e.getMessage());
            logger.error("Erro ao gerenciar treinos do aluno", e);
        }
    }
    
    /**
     * Associa um novo treino ao aluno
     * @param aluno Aluno ao qual o treino será associado
     */
    private void associarTreinoAoAluno(Aluno aluno) {
        try {
            // Solicitar o ID do treino
            System.out.print("\nDigite o ID do treino para associar ao aluno (ou '0' para cancelar): ");
            String entrada = alunoView.getScanner().nextLine();
            
            if (entrada.equals("0")) {
                alunoView.exibirMensagemOperacaoCancelada();
                return;
            }
            
            Integer idTreino;
            try {
                idTreino = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                alunoView.exibirMensagemErro("ID inválido. Digite um número.");
                return;
            }
            
            // Buscar o treino pelo ID
            Treino treino = treinoDAO.buscarPorId(idTreino);
            
            if (treino == null) {
                alunoView.exibirMensagemErro("Treino não encontrado.");
                return;
            }
            
            // Verificar se este treino já está associado ao aluno
            AlunoTreino alunoTreinoExistente = alunoTreinoDAO.buscar(aluno.getIdAluno(), idTreino);
            
            if (alunoTreinoExistente != null) {
                alunoView.exibirMensagemErro("Este treino já está associado ao aluno.");
                return;
            }
            
            // Coletar dados da associação
            AlunoTreino novoAlunoTreino = alunoView.coletarDadosAlunoTreino(treino);
            
            if (novoAlunoTreino != null) {
                // Associar o aluno
                novoAlunoTreino.setIdAluno(aluno.getIdAluno());
                novoAlunoTreino.setAluno(aluno);
                
                // Persistir no banco
                alunoTreinoDAO.associarTreinoAoAluno(novoAlunoTreino);
                
                alunoView.exibirMensagemSucesso("Treino '" + treino.getNomeTreino() + "' associado ao aluno com sucesso!");
                logger.info("Treino ID {} associado ao aluno ID {}", idTreino, aluno.getIdAluno());
            }
        } catch (Exception e) {
            alunoView.exibirMensagemErro("Erro ao associar treino: " + e.getMessage());
            logger.error("Erro ao associar treino ao aluno", e);
        }
    }
    
    /**
     * Lista todos os treinos associados ao aluno
     * @param aluno Aluno cujos treinos serão listados
     */
    private void listarTreinosDoAluno(Aluno aluno) {
        try {
            List<AlunoTreino> treinosDoAluno = alunoTreinoDAO.listarTreinosDoAluno(aluno.getIdAluno());
            
            alunoView.exibirTreinosAluno(treinosDoAluno);
            logger.info("Listados {} treinos do aluno ID {}", treinosDoAluno.size(), aluno.getIdAluno());
        } catch (Exception e) {
            alunoView.exibirMensagemErro("Erro ao listar treinos: " + e.getMessage());
            logger.error("Erro ao listar treinos do aluno", e);
        }
    }
    
    /**
     * Lista os treinos ativos do aluno (que ainda não expiraram)
     * @param aluno Aluno cujos treinos ativos serão listados
     */
    private void listarTreinosAtivosDoAluno(Aluno aluno) {
        try {
            List<AlunoTreino> treinosAtivos = alunoTreinoDAO.listarTreinosAtivosDoAluno(aluno.getIdAluno());
            
            System.out.println("\n===== TREINOS ATIVOS DO ALUNO =====");
            alunoView.exibirTreinosAluno(treinosAtivos);
            logger.info("Listados {} treinos ativos do aluno ID {}", treinosAtivos.size(), aluno.getIdAluno());
        } catch (Exception e) {
            alunoView.exibirMensagemErro("Erro ao listar treinos ativos: " + e.getMessage());
            logger.error("Erro ao listar treinos ativos do aluno", e);
        }
    }
    
    /**
     * Atualiza os dados de uma associação entre aluno e treino
     * @param aluno Aluno cujo treino será atualizado
     */
    private void atualizarTreinoDoAluno(Aluno aluno) {
        try {
            // Listar os treinos do aluno primeiro
            List<AlunoTreino> treinosDoAluno = alunoTreinoDAO.listarTreinosDoAluno(aluno.getIdAluno());
            
            if (treinosDoAluno.isEmpty()) {
                alunoView.exibirMensagemErro("O aluno não possui treinos associados.");
                return;
            }
            
            alunoView.exibirTreinosAluno(treinosDoAluno);
            
            // Solicitar o ID do treino a ser atualizado
            System.out.print("\nDigite o ID do treino que deseja atualizar (ou '0' para cancelar): ");
            String entrada = alunoView.getScanner().nextLine();
            
            if (entrada.equals("0")) {
                alunoView.exibirMensagemOperacaoCancelada();
                return;
            }
            
            Integer idTreino;
            try {
                idTreino = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                alunoView.exibirMensagemErro("ID inválido. Digite um número.");
                return;
            }
            
            // Buscar a associação específica
            AlunoTreino alunoTreinoExistente = alunoTreinoDAO.buscar(aluno.getIdAluno(), idTreino);
            
            if (alunoTreinoExistente == null) {
                alunoView.exibirMensagemErro("Este treino não está associado ao aluno.");
                return;
            }
            
            // Coletar dados para atualização
            AlunoTreino alunoTreinoAtualizado = alunoView.coletarDadosAtualizacaoAlunoTreino(alunoTreinoExistente);
            
            if (alunoTreinoAtualizado != null) {
                // Persistir no banco
                alunoTreinoDAO.atualizar(alunoTreinoAtualizado);
                
                alunoView.exibirMensagemSucesso("Informações do treino atualizadas com sucesso!");
                logger.info("Informações do treino ID {} do aluno ID {} atualizadas", idTreino, aluno.getIdAluno());
            }
        } catch (Exception e) {
            alunoView.exibirMensagemErro("Erro ao atualizar treino: " + e.getMessage());
            logger.error("Erro ao atualizar treino do aluno", e);
        }
    }
    
    /**
     * Encerra um treino definindo a data de fim como a data atual
     * @param aluno Aluno cujo treino será encerrado
     */
    private void encerrarTreinoDoAluno(Aluno aluno) {
        try {
            // Listar treinos ativos do aluno
            List<AlunoTreino> treinosAtivos = alunoTreinoDAO.listarTreinosAtivosDoAluno(aluno.getIdAluno());
            
            if (treinosAtivos.isEmpty()) {
                alunoView.exibirMensagemErro("O aluno não possui treinos ativos para encerrar.");
                return;
            }
            
            System.out.println("\n===== ENCERRAR TREINO DO ALUNO =====");
            alunoView.exibirTreinosAluno(treinosAtivos);
            
            // Solicitar o ID do treino a ser encerrado
            System.out.print("\nDigite o ID do treino que deseja encerrar (ou '0' para cancelar): ");
            String entrada = alunoView.getScanner().nextLine();
            
            if (entrada.equals("0")) {
                alunoView.exibirMensagemOperacaoCancelada();
                return;
            }
            
            Integer idTreino;
            try {
                idTreino = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                alunoView.exibirMensagemErro("ID inválido. Digite um número.");
                return;
            }
            
            // Verificar se este treino está ativo para o aluno
            boolean treinoEncontrado = false;
            for (AlunoTreino at : treinosAtivos) {
                if (at.getIdTreino().equals(idTreino)) {
                    treinoEncontrado = true;
                    break;
                }
            }
            
            if (!treinoEncontrado) {
                alunoView.exibirMensagemErro("Este treino não está ativo para o aluno ou não existe.");
                return;
            }
            
            // Confirmar o encerramento
            System.out.print("\nTem certeza que deseja encerrar este treino? (S/N): ");
            String confirmacao = alunoView.getScanner().nextLine();
            
            if (!confirmacao.equalsIgnoreCase("S")) {
                alunoView.exibirMensagemOperacaoCancelada();
                return;
            }
            
            // Encerrar o treino
            alunoTreinoDAO.encerrarTreino(aluno.getIdAluno(), idTreino);
            
            alunoView.exibirMensagemSucesso("Treino encerrado com sucesso!");
            logger.info("Treino ID {} do aluno ID {} encerrado", idTreino, aluno.getIdAluno());
        } catch (Exception e) {
            alunoView.exibirMensagemErro("Erro ao encerrar treino: " + e.getMessage());
            logger.error("Erro ao encerrar treino do aluno", e);
        }
    }
    
    /**
     * Renova um treino estendendo a data de fim
     * @param aluno Aluno cujo treino será renovado
     */
    private void renovarTreinoDoAluno(Aluno aluno) {
        try {
            // Listar todos os treinos do aluno
            List<AlunoTreino> treinosDoAluno = alunoTreinoDAO.listarTreinosDoAluno(aluno.getIdAluno());
            
            if (treinosDoAluno.isEmpty()) {
                alunoView.exibirMensagemErro("O aluno não possui treinos para renovar.");
                return;
            }
            
            System.out.println("\n===== RENOVAR TREINO DO ALUNO =====");
            alunoView.exibirTreinosAluno(treinosDoAluno);
            
            // Solicitar o ID do treino a ser renovado
            System.out.print("\nDigite o ID do treino que deseja renovar (ou '0' para cancelar): ");
            String entrada = alunoView.getScanner().nextLine();
            
            if (entrada.equals("0")) {
                alunoView.exibirMensagemOperacaoCancelada();
                return;
            }
            
            Integer idTreino;
            try {
                idTreino = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                alunoView.exibirMensagemErro("ID inválido. Digite um número.");
                return;
            }
            
            // Verificar se este treino está associado ao aluno
            boolean treinoEncontrado = false;
            for (AlunoTreino at : treinosDoAluno) {
                if (at.getIdTreino().equals(idTreino)) {
                    treinoEncontrado = true;
                    break;
                }
            }
            
            if (!treinoEncontrado) {
                alunoView.exibirMensagemErro("Este treino não está associado ao aluno.");
                return;
            }
            
            // Solicitar o número de dias para renovação
            Integer diasRenovacao = alunoView.solicitarDiasRenovacao();
            
            if (diasRenovacao == null) {
                return;
            }
            
            // Renovar o treino
            alunoTreinoDAO.renovarTreino(aluno.getIdAluno(), idTreino, diasRenovacao);
            
            alunoView.exibirMensagemSucesso("Treino renovado por mais " + diasRenovacao + " dias com sucesso!");
            logger.info("Treino ID {} do aluno ID {} renovado por {} dias", idTreino, aluno.getIdAluno(), diasRenovacao);
        } catch (Exception e) {
            alunoView.exibirMensagemErro("Erro ao renovar treino: " + e.getMessage());
            logger.error("Erro ao renovar treino do aluno", e);
        }
    }
    
    /**
     * Remove a associação entre um aluno e um treino
     * @param aluno Aluno cujo treino será removido
     */
    private void removerTreinoDoAluno(Aluno aluno) {
        try {
            // Listar todos os treinos do aluno
            List<AlunoTreino> treinosDoAluno = alunoTreinoDAO.listarTreinosDoAluno(aluno.getIdAluno());
            
            if (treinosDoAluno.isEmpty()) {
                alunoView.exibirMensagemErro("O aluno não possui treinos para remover.");
                return;
            }
            
            System.out.println("\n===== REMOVER TREINO DO ALUNO =====");
            alunoView.exibirTreinosAluno(treinosDoAluno);
            
            // Solicitar o ID do treino a ser removido
            System.out.print("\nDigite o ID do treino que deseja remover (ou '0' para cancelar): ");
            String entrada = alunoView.getScanner().nextLine();
            
            if (entrada.equals("0")) {
                alunoView.exibirMensagemOperacaoCancelada();
                return;
            }
            
            Integer idTreino;
            try {
                idTreino = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                alunoView.exibirMensagemErro("ID inválido. Digite um número.");
                return;
            }
            
            // Buscar a associação específica
            AlunoTreino alunoTreino = null;
            for (AlunoTreino at : treinosDoAluno) {
                if (at.getIdTreino().equals(idTreino)) {
                    alunoTreino = at;
                    break;
                }
            }
            
            if (alunoTreino == null) {
                alunoView.exibirMensagemErro("Este treino não está associado ao aluno.");
                return;
            }
            
            // Confirmar a remoção
            if (!alunoView.confirmarRemocaoTreino(alunoTreino)) {
                alunoView.exibirMensagemOperacaoCancelada();
                return;
            }
            
            // Remover o treino do aluno
            alunoTreinoDAO.removerTreinoDoAluno(aluno.getIdAluno(), idTreino);
            
            alunoView.exibirMensagemSucesso("Treino removido do aluno com sucesso!");
            logger.info("Treino ID {} removido do aluno ID {}", idTreino, aluno.getIdAluno());
        } catch (Exception e) {
            alunoView.exibirMensagemErro("Erro ao remover treino: " + e.getMessage());
            logger.error("Erro ao remover treino do aluno", e);
        }
    }
}
