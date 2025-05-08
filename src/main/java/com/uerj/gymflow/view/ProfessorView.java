package com.uerj.gymflow.view;

import com.uerj.gymflow.model.Funcionario;
import com.uerj.gymflow.model.Professor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe responsável pela interface de usuário para operações com Professor
 */
public class ProfessorView {
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final FuncionarioView funcionarioView;

    public ProfessorView(Scanner scanner) {
        this.scanner = scanner;
        this.funcionarioView = new FuncionarioView(scanner);
    }

    /**
     * Exibe o menu de opções para o gerenciamento de professores
     *
     * @return A opção escolhida pelo usuário
     */
    public int exibirMenu() {
        System.out.println("\n===== MENU PROFESSOR =====");
        System.out.println("1. Cadastrar novo professor");
        System.out.println("2. Buscar professor por ID");
        System.out.println("3. Buscar professor por CREF");
        System.out.println("4. Listar todos os professores");
        System.out.println("5. Atualizar professor");
        System.out.println("6. Excluir professor");
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
     * Coleta os dados para um novo professor ou atualização
     *
     * @param professorExistente Professor existente para atualização (null para novo professor)
     * @return Objeto Professor preenchido com os dados informados
     */
    public Professor coletarDadosProfessor(Professor professorExistente) {
        System.out.println("\n===== " + (professorExistente == null ? "CADASTRO" : "ATUALIZAÇÃO") + " DE PROFESSOR =====");

        // Primeiro, coletamos os dados de funcionário
        Professor professor;
        if (professorExistente != null) {
            professor = professorExistente;
        } else {
            professor = new Professor();
            // Inicialize a data de nascimento e admissão para evitar NullPointerException
            professor.setDataNascimento(LocalDate.now());
            professor.setDataAdmissao(LocalDate.now());
        }

        // Usar o FuncionarioView para coletar os dados básicos de funcionário
        Funcionario funcionarioColetado = funcionarioView.coletarDadosFuncionario(professor);
        if (funcionarioColetado == null) {
            return null;
        }

        // Transferir os dados coletados para o professor se for um novo cadastro
        if (professorExistente == null) {
            professor.setNome(funcionarioColetado.getNome());
            professor.setDataNascimento(funcionarioColetado.getDataNascimento());
            professor.setCpf(funcionarioColetado.getCpf());
            professor.setTelefone(funcionarioColetado.getTelefone());
            professor.setEmail(funcionarioColetado.getEmail());
            professor.setCargo(funcionarioColetado.getCargo());
            professor.setDataAdmissao(funcionarioColetado.getDataAdmissao());
            professor.setSalario(funcionarioColetado.getSalario());
        }

        // Agora coletamos os dados específicos de professor
        try {
            System.out.print("Especialidade" +
                    (professorExistente != null ? " [" + professorExistente.getEspecialidade() + "]" : "") +
                    ": ");
            String especialidade = scanner.nextLine().trim();
            if (!especialidade.isEmpty()) {
                professor.setEspecialidade(especialidade);
            } else if (professorExistente == null) {
                System.out.println("A especialidade é obrigatória.");
                return null;
            }

            System.out.print("CREF (Registro do Conselho)" +
                    (professorExistente != null ? " [" + professorExistente.getCref() + "]" : "") +
                    ": ");
            String cref = scanner.nextLine().trim();
            if (!cref.isEmpty()) {
                professor.setCref(cref);
            } else if (professorExistente == null) {
                System.out.println("O CREF é obrigatório.");
                return null;
            }

            return professor;
        } catch (Exception e) {
            System.out.println("Erro ao coletar dados: " + e.getMessage());
            return null;
        }
    }

    /**
     * Exibe os detalhes de um professor
     *
     * @param professor O professor a ser exibido
     */
    public void exibirDadosProfessor(Professor professor) {
        if (professor == null) {
            System.out.println("Professor não encontrado.");
            return;
        }

        System.out.println("\n------ DADOS DO PROFESSOR ------");
        System.out.println("ID Professor: " + professor.getIdProfessor());
        System.out.println("ID Funcionário: " + professor.getIdFuncionario());
        System.out.println("ID Pessoa: " + professor.getIdPessoa());
        System.out.println("Nome: " + professor.getNome());
        
        // Verifica se a data de nascimento não é nula antes de formatar
        if (professor.getDataNascimento() != null) {
            System.out.println("Data de Nascimento: " + professor.getDataNascimento().format(dateFormatter) +
                    " (" + calcularIdade(professor.getDataNascimento()) + " anos)");
        } else {
            System.out.println("Data de Nascimento: Não informada");
        }
        
        System.out.println("CPF: " + formatarCpf(professor.getCpf()));
        System.out.println("Telefone: " + professor.getTelefone());
        System.out.println("E-mail: " + professor.getEmail());
        System.out.println("Cargo: " + professor.getCargo());
        
        // Verifica se a data de admissão não é nula antes de formatar
        if (professor.getDataAdmissao() != null) {
            System.out.println("Data de Admissão: " + professor.getDataAdmissao().format(dateFormatter));
        } else {
            System.out.println("Data de Admissão: Não informada");
        }
        
        System.out.println("Salário: R$ " + String.format("%.2f", professor.getSalario() != null ? professor.getSalario() : 0));
        System.out.println("Especialidade: " + professor.getEspecialidade());
        System.out.println("CREF: " + professor.getCref());
        System.out.println("----------------------------");
    }

    /**
     * Exibe uma lista de professores de forma resumida
     *
     * @param professores Lista de professores a serem exibidos
     */
    public void exibirListaProfessores(List<Professor> professores) {
        if (professores == null || professores.isEmpty()) {
            System.out.println("Não há professores cadastrados.");
            return;
        }

        System.out.println("\n===== LISTA DE PROFESSORES =====");
        for (Professor professor : professores) {
            System.out.println("---------------------------");
            System.out.println("ID: " + professor.getIdProfessor());
            System.out.println("Nome: " + professor.getNome());
            System.out.println("CPF: " + formatarCpf(professor.getCpf()));
            System.out.println("Especialidade: " + professor.getEspecialidade());
            System.out.println("CREF: " + professor.getCref());
        }
        System.out.println("---------------------------");
        System.out.println("Total de professores: " + professores.size());
    }

    /**
     * Solicita o ID de um professor
     *
     * @return O ID informado ou null em caso de erro
     */
    public Integer solicitarId() {
        System.out.print("Digite o ID do professor: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um ID válido.");
            return null;
        }
    }

    /**
     * Solicita um CREF para busca
     *
     * @return O CREF informado
     */
    public String solicitarCref() {
        System.out.print("Digite o CREF do professor: ");
        return scanner.nextLine().trim();
    }

    /**
     * Solicita confirmação para exclusão
     *
     * @return true se confirmado, false caso contrário
     */
    public boolean confirmarExclusao() {
        System.out.print("Tem certeza que deseja excluir este professor? (S/N): ");
        String confirmacao = scanner.nextLine().trim().toUpperCase();
        return confirmacao.equals("S");
    }

    /**
     * Exibe mensagem de sucesso para cadastro
     *
     * @param professor O professor cadastrado
     */
    public void exibirMensagemCadastroSucesso(Professor professor) {
        System.out.println("Professor cadastrado com sucesso! ID: " + professor.getIdProfessor());
    }

    /**
     * Exibe mensagem de sucesso para atualização
     */
    public void exibirMensagemAtualizacaoSucesso() {
        System.out.println("Professor atualizado com sucesso!");
    }

    /**
     * Exibe mensagem de sucesso para exclusão
     */
    public void exibirMensagemExclusaoSucesso() {
        System.out.println("Professor excluído com sucesso!");
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
