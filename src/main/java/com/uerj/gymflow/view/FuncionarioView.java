package com.uerj.gymflow.view;

import com.uerj.gymflow.model.Funcionario;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe responsável pela interface de usuário para operações com Funcionário
 */
public class FuncionarioView {
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final PessoaView pessoaView;

    public FuncionarioView(Scanner scanner) {
        this.scanner = scanner;
        this.pessoaView = new PessoaView(scanner);
    }

    /**
     * Exibe o menu de opções para o gerenciamento de funcionários
     *
     * @return A opção escolhida pelo usuário
     */
    public int exibirMenu() {
        System.out.println("\n===== MENU FUNCIONÁRIO =====");
        System.out.println("1. Cadastrar novo funcionário");
        System.out.println("2. Buscar funcionário por ID");
        System.out.println("3. Listar todos os funcionários");
        System.out.println("4. Atualizar funcionário");
        System.out.println("5. Excluir funcionário");
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
     * Coleta os dados para um novo funcionário ou atualização
     *
     * @param funcionarioExistente Funcionário existente para atualização (null para novo funcionário)
     * @return Objeto Funcionário preenchido com os dados informados
     */
    public Funcionario coletarDadosFuncionario(Funcionario funcionarioExistente) {
        System.out.println("\n===== " + (funcionarioExistente == null ? "CADASTRO" : "ATUALIZAÇÃO") + " DE FUNCIONÁRIO =====");

        // Primeiro, coletamos os dados de pessoa
        Funcionario funcionario;
        if (funcionarioExistente != null) {
            funcionario = funcionarioExistente;
        } else {
            funcionario = new Funcionario();
            // Inicialize a data de nascimento para evitar NullPointerException
            funcionario.setDataNascimento(LocalDate.now());
        }

        // Usar o PessoaView para coletar os dados básicos de pessoa
        Funcionario pessoaColetada = (Funcionario) pessoaView.coletarDadosPessoa(funcionario);
        if (pessoaColetada == null) {
            return null;
        }

        // Transferir os dados coletados para o funcionário
        if (funcionarioExistente == null) {
            funcionario.setNome(pessoaColetada.getNome());
            funcionario.setDataNascimento(pessoaColetada.getDataNascimento());
            funcionario.setCpf(pessoaColetada.getCpf());
            funcionario.setTelefone(pessoaColetada.getTelefone());
            funcionario.setEmail(pessoaColetada.getEmail());
        }

        // Agora coletamos os dados específicos de funcionário
        try {
            System.out.print("Cargo" +
                    (funcionarioExistente != null ? " [" + funcionarioExistente.getCargo() + "]" : "") +
                    ": ");
            String cargo = scanner.nextLine().trim();
            if (!cargo.isEmpty()) {
                funcionario.setCargo(cargo);
            } else if (funcionarioExistente == null) {
                System.out.println("O cargo é obrigatório.");
                return null;
            }

            System.out.print("Data de Admissão (dd/mm/aaaa)" +
                    (funcionarioExistente != null && funcionarioExistente.getDataAdmissao() != null ? 
                        " [" + funcionarioExistente.getDataAdmissao().format(dateFormatter) + "]" : "") +
                    ": ");
            String dataStr = scanner.nextLine().trim();
            if (!dataStr.isEmpty()) {
                try {
                    LocalDate dataAdmissao = LocalDate.parse(dataStr, dateFormatter);
                    if (dataAdmissao.isAfter(LocalDate.now())) {
                        System.out.println("A data de admissão não pode ser no futuro.");
                        return null;
                    }
                    funcionario.setDataAdmissao(dataAdmissao);
                } catch (DateTimeParseException e) {
                    System.out.println("Formato de data inválido. Use o formato dd/mm/aaaa.");
                    return null;
                }
            } else if (funcionarioExistente == null) {
                System.out.println("A data de admissão é obrigatória.");
                return null;
            }

            System.out.print("Salário" +
                    (funcionarioExistente != null ? " [" + funcionarioExistente.getSalario() + "]" : "") +
                    ": ");
            String salarioStr = scanner.nextLine().trim().replace(",", ".");
            if (!salarioStr.isEmpty()) {
                try {
                    float salario = Float.parseFloat(salarioStr);
                    if (salario <= 0) {
                        System.out.println("O salário deve ser maior que zero.");
                        return null;
                    }
                    funcionario.setSalario(salario);
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, digite um valor válido para o salário.");
                    return null;
                }
            } else if (funcionarioExistente == null) {
                System.out.println("O salário é obrigatório.");
                return null;
            }

            return funcionario;
        } catch (Exception e) {
            System.out.println("Erro ao coletar dados: " + e.getMessage());
            return null;
        }
    }

    /**
     * Exibe os detalhes de um funcionário
     *
     * @param funcionario O funcionário a ser exibido
     */
    public void exibirDadosFuncionario(Funcionario funcionario) {
        if (funcionario == null) {
            System.out.println("Funcionário não encontrado.");
            return;
        }

        System.out.println("\n------ DADOS DO FUNCIONÁRIO ------");
        System.out.println("ID Funcionário: " + funcionario.getIdFuncionario());
        System.out.println("ID Pessoa: " + funcionario.getIdPessoa());
        System.out.println("Nome: " + funcionario.getNome());
        
        // Verifica se a data de nascimento não é nula antes de formatar
        if (funcionario.getDataNascimento() != null) {
            System.out.println("Data de Nascimento: " + funcionario.getDataNascimento().format(dateFormatter) +
                    " (" + calcularIdade(funcionario.getDataNascimento()) + " anos)");
        } else {
            System.out.println("Data de Nascimento: Não informada");
        }
        
        System.out.println("CPF: " + formatarCpf(funcionario.getCpf()));
        System.out.println("Telefone: " + funcionario.getTelefone());
        System.out.println("E-mail: " + funcionario.getEmail());
        System.out.println("Cargo: " + funcionario.getCargo());
        
        // Verifica se a data de admissão não é nula antes de formatar
        if (funcionario.getDataAdmissao() != null) {
            System.out.println("Data de Admissão: " + funcionario.getDataAdmissao().format(dateFormatter));
        } else {
            System.out.println("Data de Admissão: Não informada");
        }
        
        System.out.println("Salário: R$ " + String.format("%.2f", funcionario.getSalario() != null ? funcionario.getSalario() : 0));
        System.out.println("----------------------------");
    }

    /**
     * Solicita o ID de um funcionário
     *
     * @return O ID informado ou null em caso de erro
     */
    public Integer solicitarId() {
        System.out.print("Digite o ID do funcionário: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um ID válido.");
            return null;
        }
    }

    /**
     * Solicita um cargo para busca
     *
     * @return O cargo informado
     */
    public String solicitarCargo() {
        System.out.print("Digite o cargo a ser buscado: ");
        return scanner.nextLine().trim();
    }

    /**
     * Solicita confirmação para exclusão
     *
     * @return true se confirmado, false caso contrário
     */
    public boolean confirmarExclusao() {
        System.out.print("Tem certeza que deseja excluir este funcionário? (S/N): ");
        String confirmacao = scanner.nextLine().trim().toUpperCase();
        return confirmacao.equals("S");
    }

    /**
     * Exibe uma lista de funcionários de forma resumida
     *
     * @param funcionarios Lista de funcionários a serem exibidos
     */
    public void exibirListaFuncionarios(List<Funcionario> funcionarios) {
        if (funcionarios == null || funcionarios.isEmpty()) {
            System.out.println("Não há funcionários cadastrados.");
            return;
        }

        System.out.println("\n===== LISTA DE FUNCIONÁRIOS =====");
        for (Funcionario funcionario : funcionarios) {
            System.out.println("---------------------------");
            System.out.println("ID: " + funcionario.getIdFuncionario());
            System.out.println("Nome: " + funcionario.getNome());
            System.out.println("CPF: " + formatarCpf(funcionario.getCpf()));
            System.out.println("Cargo: " + funcionario.getCargo());
            System.out.println("Salário: R$ " + String.format("%.2f", funcionario.getSalario()));
        }
        System.out.println("---------------------------");
        System.out.println("Total de funcionários: " + funcionarios.size());
    }

    /**
     * Exibe mensagem de sucesso para cadastro
     *
     * @param funcionario O funcionário cadastrado
     */
    public void exibirMensagemCadastroSucesso(Funcionario funcionario) {
        System.out.println("Funcionário cadastrado com sucesso! ID: " + funcionario.getIdFuncionario());
    }

    /**
     * Exibe mensagem de sucesso para atualização
     */
    public void exibirMensagemAtualizacaoSucesso() {
        System.out.println("Funcionário atualizado com sucesso!");
    }

    /**
     * Exibe mensagem de sucesso para exclusão
     */
    public void exibirMensagemExclusaoSucesso() {
        System.out.println("Funcionário excluído com sucesso!");
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

    /**
     * Calcula a idade a partir da data de nascimento
     */
    private int calcularIdade(LocalDate dataNascimento) {
        return LocalDate.now().getYear() - dataNascimento.getYear();
    }

    /**
     * Formata o CPF para exibição (XXX.XXX.XXX-XX)
     */
    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" + cpf.substring(9);
    }
}
