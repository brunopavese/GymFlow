package com.uerj.gymflow.view;

import com.uerj.gymflow.model.Plano;

import java.util.List;
import java.util.Scanner;

/**
 * Classe responsável pela interface de usuário para operações com Plano
 */
public class PlanoView {
    private final Scanner scanner;

    public PlanoView(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Exibe o menu de opções para o gerenciamento de planos
     *
     * @return A opção escolhida pelo usuário
     */
    public int exibirMenu() {
        System.out.println("\n===== MENU PLANOS =====");
        System.out.println("1. Cadastrar novo plano");
        System.out.println("2. Buscar plano por ID");
        System.out.println("3. Listar todos os planos");
        System.out.println("4. Atualizar plano");
        System.out.println("5. Excluir plano");
        System.out.println("0. Voltar ao menu principal");
        System.out.print("Escolha uma opção: ");

        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um número válido.");
            return -1;
        }
    }

    /**
     * Coleta os dados para um novo plano ou atualização
     *
     * @param planoExistente Plano existente para atualização (null para novo plano)
     * @return Objeto Plano preenchido com os dados informados
     */
    public Plano coletarDadosPlano(Plano planoExistente) {
        System.out.println("\n===== " + (planoExistente == null ? "CADASTRO" : "ATUALIZAÇÃO") + " DE PLANO =====");

        Plano plano;
        if (planoExistente != null) {
            plano = planoExistente;
        } else {
            plano = new Plano();
        }

        try {
            System.out.print("Nome do Plano" +
                    (planoExistente != null ? " [" + planoExistente.getNomePlano() + "]" : "") +
                    ": ");
            String nomePlano = scanner.nextLine().trim();
            if (!nomePlano.isEmpty()) {
                plano.setNomePlano(nomePlano);
            } else if (planoExistente == null) {
                System.out.println("O nome do plano é obrigatório.");
                return null;
            }

            System.out.print("Descrição" +
                    (planoExistente != null ? " [" + planoExistente.getDescricao() + "]" : "") +
                    ": ");
            String descricao = scanner.nextLine().trim();
            if (!descricao.isEmpty()) {
                plano.setDescricao(descricao);
            } else if (planoExistente == null) {
                System.out.println("A descrição é obrigatória.");
                return null;
            }

            System.out.print("Duração (em meses)" +
                    (planoExistente != null ? " [" + planoExistente.getDuracao() + "]" : "") +
                    ": ");
            String duracaoStr = scanner.nextLine().trim();
            if (!duracaoStr.isEmpty()) {
                try {
                    int duracao = Integer.parseInt(duracaoStr);
                    if (duracao <= 0) {
                        System.out.println("A duração deve ser maior que zero.");
                        return null;
                    }
                    plano.setDuracao(duracao);
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, digite um valor válido para a duração.");
                    return null;
                }
            } else if (planoExistente == null) {
                System.out.println("A duração é obrigatória.");
                return null;
            }

            System.out.print("Valor Mensal (R$)" +
                    (planoExistente != null ? " [" + planoExistente.getValorMensal() + "]" : "") +
                    ": ");
            String valorStr = scanner.nextLine().trim().replace(",", ".");
            if (!valorStr.isEmpty()) {
                try {
                    float valor = Float.parseFloat(valorStr);
                    if (valor <= 0) {
                        System.out.println("O valor mensal deve ser maior que zero.");
                        return null;
                    }
                    plano.setValorMensal(valor);
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, digite um valor válido para o valor mensal.");
                    return null;
                }
            } else if (planoExistente == null) {
                System.out.println("O valor mensal é obrigatório.");
                return null;
            }

            return plano;
        } catch (Exception e) {
            System.out.println("Erro ao coletar dados: " + e.getMessage());
            return null;
        }
    }

    /**
     * Exibe os detalhes de um plano
     *
     * @param plano O plano a ser exibido
     */
    public void exibirDadosPlano(Plano plano) {
        if (plano == null) {
            System.out.println("Plano não encontrado.");
            return;
        }

        System.out.println("\n------ DADOS DO PLANO ------");
        System.out.println("ID: " + plano.getIdPlano());
        System.out.println("Nome: " + plano.getNomePlano());
        System.out.println("Descrição: " + plano.getDescricao());
        System.out.println("Duração: " + plano.getDuracao() + " meses");
        System.out.println("Valor Mensal: R$ " + String.format("%.2f", plano.getValorMensal()));
        System.out.println("Valor Total: R$ " + String.format("%.2f", plano.calcularValorTotal()));
        System.out.println("----------------------------");
    }

    /**
     * Exibe uma lista de planos de forma resumida
     *
     * @param planos Lista de planos a serem exibidos
     */
    public void exibirListaPlanos(List<Plano> planos) {
        if (planos == null || planos.isEmpty()) {
            System.out.println("Não há planos cadastrados.");
            return;
        }

        System.out.println("\n===== LISTA DE PLANOS =====");
        for (Plano plano : planos) {
            System.out.println("---------------------------");
            System.out.println("ID: " + plano.getIdPlano());
            System.out.println("Nome: " + plano.getNomePlano());
            System.out.println("Duração: " + plano.getDuracao() + " meses");
            System.out.println("Valor Mensal: R$ " + String.format("%.2f", plano.getValorMensal()));
        }
        System.out.println("---------------------------");
        System.out.println("Total de planos: " + planos.size());
    }

    /**
     * Solicita o ID de um plano
     *
     * @return O ID informado ou null em caso de erro
     */
    public Integer solicitarId() {
        System.out.print("Digite o ID do plano: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um ID válido.");
            return null;
        }
    }

    /**
     * Solicita confirmação para exclusão
     *
     * @return true se confirmado, false caso contrário
     */
    public boolean confirmarExclusao() {
        System.out.print("Tem certeza que deseja excluir este plano? (S/N): ");
        String confirmacao = scanner.nextLine().trim().toUpperCase();
        return confirmacao.equals("S");
    }

    /**
     * Exibe mensagem de sucesso para cadastro
     *
     * @param plano O plano cadastrado
     */
    public void exibirMensagemCadastroSucesso(Plano plano) {
        System.out.println("Plano cadastrado com sucesso! ID: " + plano.getIdPlano());
    }

    /**
     * Exibe mensagem de sucesso para atualização
     */
    public void exibirMensagemAtualizacaoSucesso() {
        System.out.println("Plano atualizado com sucesso!");
    }

    /**
     * Exibe mensagem de sucesso para exclusão
     */
    public void exibirMensagemExclusaoSucesso() {
        System.out.println("Plano excluído com sucesso!");
    }

    /**
     * Exibe mensagem de operação cancelada
     */
    public void exibirMensagemOperacaoCancelada() {
        System.out.println("Operação cancelada.");
    }

    /**
     * Exibe mensagem de erro
     *
     * @param mensagem A mensagem de erro
     */
    public void exibirMensagemErro(String mensagem) {
        System.out.println("Erro: " + mensagem);
    }
}
