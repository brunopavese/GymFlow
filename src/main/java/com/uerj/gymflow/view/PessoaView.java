package com.uerj.gymflow.view;

import com.uerj.gymflow.model.Pessoa;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe responsável pela interface de usuário para operações com Pessoa
 */
public class PessoaView {
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PessoaView(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Exibe o menu de opções para o gerenciamento de pessoas
     * @return A opção escolhida pelo usuário
     */
    public int exibirMenu() {
        System.out.println("\n===== MENU PESSOA =====");
        System.out.println("1. Cadastrar nova pessoa");
        System.out.println("2. Buscar pessoa por ID");
        System.out.println("3. Buscar pessoa por CPF");
        System.out.println("4. Listar todas as pessoas");
        System.out.println("5. Atualizar pessoa");
        System.out.println("6. Excluir pessoa");
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
     * Coleta os dados para uma nova pessoa ou atualização
     * @param pessoaExistente Pessoa existente para atualização (null para nova pessoa)
     * @return Objeto Pessoa preenchido com os dados informados
     */
    public Pessoa coletarDadosPessoa(Pessoa pessoaExistente) {
        System.out.println("\n===== " + (pessoaExistente == null ? "CADASTRO" : "ATUALIZAÇÃO") + " DE PESSOA =====");
        
        Pessoa pessoa = pessoaExistente != null ? pessoaExistente : new Pessoa();
        
        try {
            System.out.print("Nome completo" + 
                            (pessoaExistente != null ? " [" + pessoaExistente.getNome() + "]" : "") + 
                            ": ");
            String nome = scanner.nextLine().trim();
            if (!nome.isEmpty()) {
                pessoa.setNome(nome);
            } else if (pessoaExistente == null) {
                System.out.println("O nome é obrigatório.");
                return null;
            }
            
            System.out.print("Data de Nascimento (dd/mm/aaaa)" + 
                            (pessoaExistente != null ? " [" + pessoaExistente.getDataNascimento().format(dateFormatter) + "]" : "") + 
                            ": ");
            String dataStr = scanner.nextLine().trim();
            if (!dataStr.isEmpty()) {
                try {
                    LocalDate dataNascimento = LocalDate.parse(dataStr, dateFormatter);
                    if (dataNascimento.isAfter(LocalDate.now())) {
                        System.out.println("A data de nascimento não pode ser no futuro.");
                        return null;
                    }
                    pessoa.setDataNascimento(dataNascimento);
                } catch (DateTimeParseException e) {
                    System.out.println("Formato de data inválido. Use o formato dd/mm/aaaa.");
                    return null;
                }
            } else if (pessoaExistente == null) {
                System.out.println("A data de nascimento é obrigatória.");
                return null;
            }
            
            System.out.print("CPF (apenas números)" + 
                            (pessoaExistente != null ? " [" + pessoaExistente.getCpf() + "]" : "") + 
                            ": ");
            String cpf = scanner.nextLine().trim().replaceAll("[^0-9]", "");
            if (!cpf.isEmpty()) {
                if (cpf.length() != 11) {
                    System.out.println("CPF deve conter 11 dígitos.");
                    return null;
                }
                pessoa.setCpf(cpf);
            } else if (pessoaExistente == null) {
                System.out.println("O CPF é obrigatório.");
                return null;
            }
            
            System.out.print("Telefone (apenas números)" + 
                            (pessoaExistente != null ? " [" + pessoaExistente.getTelefone() + "]" : "") + 
                            ": ");
            String telefone = scanner.nextLine().trim().replaceAll("[^0-9]", "");
            if (!telefone.isEmpty()) {
                pessoa.setTelefone(telefone);
            } else if (pessoaExistente == null) {
                System.out.println("O telefone é obrigatório.");
                return null;
            }
            
            System.out.print("E-mail" + 
                            (pessoaExistente != null ? " [" + pessoaExistente.getEmail() + "]" : "") + 
                            ": ");
            String email = scanner.nextLine().trim();
            if (!email.isEmpty()) {
                if (!email.contains("@") || !email.contains(".")) {
                    System.out.println("E-mail inválido.");
                    return null;
                }
                pessoa.setEmail(email);
            } else if (pessoaExistente == null) {
                System.out.println("O e-mail é obrigatório.");
                return null;
            }
            
            return pessoa;
        } catch (Exception e) {
            System.out.println("Erro ao coletar dados: " + e.getMessage());
            return null;
        }
    }

    /**
     * Exibe os detalhes de uma pessoa
     * @param pessoa A pessoa a ser exibida
     */
    public void exibirDadosPessoa(Pessoa pessoa) {
        if (pessoa == null) {
            System.out.println("Pessoa não encontrada.");
            return;
        }
        
        System.out.println("\n------ DADOS DA PESSOA ------");
        System.out.println("ID: " + pessoa.getIdPessoa());
        System.out.println("Nome: " + pessoa.getNome());
        
        // Verifica se a data de nascimento não é nula antes de formatar
        if (pessoa.getDataNascimento() != null) {
            System.out.println("Data de Nascimento: " + pessoa.getDataNascimento().format(dateFormatter) + 
                           " (" + calcularIdade(pessoa.getDataNascimento()) + " anos)");
        } else {
            System.out.println("Data de Nascimento: Não informada");
        }
        
        System.out.println("CPF: " + formatarCpf(pessoa.getCpf()));
        System.out.println("Telefone: " + pessoa.getTelefone());
        System.out.println("E-mail: " + pessoa.getEmail());
        System.out.println("----------------------------");
    }

    /**
     * Solicita o ID de uma pessoa
     * @return O ID informado ou null em caso de erro
     */
    public Integer solicitarId() {
        System.out.print("Digite o ID da pessoa: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um ID válido.");
            return null;
        }
    }

    /**
     * Solicita o CPF de uma pessoa
     * @return O CPF informado
     */
    public String solicitarCpf() {
        System.out.print("Digite o CPF da pessoa (apenas números): ");
        return scanner.nextLine().replaceAll("[^0-9]", "");
    }

    /**
     * Solicita a faixa etária para busca de pessoas
     * @return Um array com [idadeMinima, idadeMaxima] ou null em caso de erro
     */
    public int[] solicitarFaixaEtaria() {
        try {
            System.out.print("Idade mínima: ");
            int idadeMinima = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Idade máxima: ");
            int idadeMaxima = Integer.parseInt(scanner.nextLine());
            
            if (idadeMinima > idadeMaxima) {
                System.out.println("A idade mínima não pode ser maior que a idade máxima.");
                return null;
            }
            
            return new int[] {idadeMinima, idadeMaxima};
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite números válidos para as idades.");
            return null;
        }
    }

    /**
     * Solicita confirmação para exclusão
     * @return true se confirmado, false caso contrário
     */
    public boolean confirmarExclusao() {
        System.out.print("Tem certeza que deseja excluir esta pessoa? (S/N): ");
        String confirmacao = scanner.nextLine().trim().toUpperCase();
        return confirmacao.equals("S");
    }

    /**
     * Exibe uma lista de pessoas de forma resumida
     * @param pessoas Lista de pessoas a serem exibidas
     */
    public void exibirListaPessoas(List<Pessoa> pessoas) {
        if (pessoas == null || pessoas.isEmpty()) {
            System.out.println("Não há pessoas cadastradas.");
            return;
        }
        
        System.out.println("\n===== LISTA DE PESSOAS =====");
        for (Pessoa pessoa : pessoas) {
            System.out.println("---------------------------");
            System.out.println("ID: " + pessoa.getIdPessoa());
            System.out.println("Nome: " + pessoa.getNome());
            System.out.println("CPF: " + formatarCpf(pessoa.getCpf()));
            System.out.println("E-mail: " + pessoa.getEmail());
        }
        System.out.println("---------------------------");
        System.out.println("Total de pessoas: " + pessoas.size());
    }

    /**
     * Exibe mensagem de sucesso para cadastro
     * @param pessoa A pessoa cadastrada
     */
    public void exibirMensagemCadastroSucesso(Pessoa pessoa) {
        System.out.println("Pessoa cadastrada com sucesso! ID: " + pessoa.getIdPessoa());
    }

    /**
     * Exibe mensagem de sucesso para atualização
     */
    public void exibirMensagemAtualizacaoSucesso() {
        System.out.println("Pessoa atualizada com sucesso!");
    }

    /**
     * Exibe mensagem de sucesso para exclusão
     */
    public void exibirMensagemExclusaoSucesso() {
        System.out.println("Pessoa excluída com sucesso!");
    }

    /**
     * Exibe mensagem de operação cancelada
     */
    public void exibirMensagemOperacaoCancelada() {
        System.out.println("Operação cancelada.");
    }

    /**
     * Exibe mensagem de erro
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
