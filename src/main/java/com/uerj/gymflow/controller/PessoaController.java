package com.uerj.gymflow.controller;

import com.uerj.gymflow.dao.PessoaDAO;
import com.uerj.gymflow.model.Pessoa;
import com.uerj.gymflow.view.PessoaView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Controlador para gerenciar operações relacionadas a Pessoas
 * Implementa o padrão MVC, fazendo a intermediação entre View e DAO
 */
public class PessoaController {
    private static final Logger logger = LoggerFactory.getLogger(PessoaController.class);
    private final PessoaDAO pessoaDAO;
    private final PessoaView pessoaView;

    public PessoaController(Scanner scanner) {
        this.pessoaDAO = new PessoaDAO();
        this.pessoaView = new PessoaView(scanner);
    }

    /**
     * Exibe o menu de opções de pessoa e processa a escolha do usuário
     */
    public void mostrarMenu() {
        int opcao = -1;
        
        while (opcao != 0) {
            try {
                opcao = pessoaView.exibirMenu();
                
                switch (opcao) {
                    case 1:
                        cadastrarPessoa();
                        break;
                    case 2:
                        buscarPessoaPorId();
                        break;
                    case 3:
                        buscarPessoaPorCpf();
                        break;
                    case 4:
                        listarTodasPessoas();
                        break;
                    case 5:
                        atualizarPessoa();
                        break;
                    case 6:
                        excluirPessoa();
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
                pessoaView.exibirMensagemErro(e.getMessage());
                logger.error("Erro no menu de pessoa", e);
            }
        }
    }

    /**
     * Coleta informações e cadastra uma nova pessoa
     */
    private void cadastrarPessoa() {
        try {
            Pessoa novaPessoa = pessoaView.coletarDadosPessoa(null);
            
            if (novaPessoa != null) {
                if (pessoaDAO.verificarPessoaExistente(novaPessoa.getCpf(), novaPessoa.getEmail())) {
                    pessoaView.exibirMensagemErro("Já existe uma pessoa com este CPF ou e-mail.");
                    return;
                }
                
                pessoaDAO.inserir(novaPessoa);
                pessoaView.exibirMensagemCadastroSucesso(novaPessoa);
                logger.info("Pessoa cadastrada com sucesso: ID {}", novaPessoa.getIdPessoa());
            }
        } catch (Exception e) {
            pessoaView.exibirMensagemErro("Erro ao cadastrar pessoa: " + e.getMessage());
            logger.error("Erro ao cadastrar pessoa", e);
        }
    }

    /**
     * Busca uma pessoa pelo ID
     */
    private void buscarPessoaPorId() {
        try {
            Integer id = pessoaView.solicitarId();
            
            if (id != null) {
                Pessoa pessoa = pessoaDAO.buscarPorId(id);
                pessoaView.exibirDadosPessoa(pessoa);
                
                if (pessoa != null) {
                    logger.info("Pessoa encontrada por ID: {}", id);
                } else {
                    logger.info("Nenhuma pessoa encontrada com ID: {}", id);
                }
            }
        } catch (Exception e) {
            pessoaView.exibirMensagemErro("Erro ao buscar pessoa: " + e.getMessage());
            logger.error("Erro ao buscar pessoa por ID", e);
        }
    }

    /**
     * Busca uma pessoa pelo CPF
     */
    private void buscarPessoaPorCpf() {
        try {
            String cpf = pessoaView.solicitarCpf();
            
            if (cpf != null && !cpf.isEmpty()) {
                Pessoa pessoa = pessoaDAO.buscarPorCpf(cpf);
                pessoaView.exibirDadosPessoa(pessoa);
                
                if (pessoa != null) {
                    logger.info("Pessoa encontrada por CPF: {}", cpf);
                } else {
                    logger.info("Nenhuma pessoa encontrada com CPF: {}", cpf);
                }
            }
        } catch (Exception e) {
            pessoaView.exibirMensagemErro("Erro ao buscar pessoa: " + e.getMessage());
            logger.error("Erro ao buscar pessoa por CPF", e);
        }
    }

    /**
     * Lista todas as pessoas cadastradas
     */
    private void listarTodasPessoas() {
        try {
            List<Pessoa> pessoas = pessoaDAO.listarTodos();
            pessoaView.exibirListaPessoas(pessoas);
            logger.info("Listagem de pessoas concluída. Total: {}", pessoas.size());
        } catch (Exception e) {
            pessoaView.exibirMensagemErro("Erro ao listar pessoas: " + e.getMessage());
            logger.error("Erro ao listar pessoas", e);
        }
    }

    /**
     * Atualiza os dados de uma pessoa existente
     */
    private void atualizarPessoa() {
        try {
            Integer id = pessoaView.solicitarId();
            
            if (id != null) {
                Pessoa pessoaExistente = pessoaDAO.buscarPorId(id);
                
                if (pessoaExistente == null) {
                    pessoaView.exibirMensagemErro("Pessoa não encontrada.");
                    return;
                }
                
                pessoaView.exibirDadosPessoa(pessoaExistente);
                
                Pessoa pessoaAtualizada = pessoaView.coletarDadosPessoa(pessoaExistente);
                
                if (pessoaAtualizada != null) {
                    pessoaAtualizada.setIdPessoa(id);
                    pessoaDAO.atualizar(pessoaAtualizada);
                    pessoaView.exibirMensagemAtualizacaoSucesso();
                    logger.info("Pessoa atualizada com sucesso: ID {}", id);
                }
            }
        } catch (Exception e) {
            pessoaView.exibirMensagemErro("Erro ao atualizar pessoa: " + e.getMessage());
            logger.error("Erro ao atualizar pessoa", e);
        }
    }

    /**
     * Exclui uma pessoa do sistema
     */
    private void excluirPessoa() {
        try {
            Integer id = pessoaView.solicitarId();
            
            if (id != null) {
                Pessoa pessoaExistente = pessoaDAO.buscarPorId(id);
                
                if (pessoaExistente == null) {
                    pessoaView.exibirMensagemErro("Pessoa não encontrada.");
                    return;
                }
                
                pessoaView.exibirDadosPessoa(pessoaExistente);
                
                if (pessoaView.confirmarExclusao()) {
                    pessoaDAO.excluir(id);
                    pessoaView.exibirMensagemExclusaoSucesso();
                    logger.info("Pessoa excluída com sucesso: ID {}", id);
                } else {
                    pessoaView.exibirMensagemOperacaoCancelada();
                    logger.info("Exclusão de pessoa cancelada: ID {}", id);
                }
            }
        } catch (Exception e) {
            pessoaView.exibirMensagemErro("Erro ao excluir pessoa: " + e.getMessage());
            logger.error("Erro ao excluir pessoa", e);
        }
    }
}
