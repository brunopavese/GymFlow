package com.uerj.gymflow.view;

import com.uerj.gymflow.model.Aluno;
import com.uerj.gymflow.model.AlunoTreino;
import com.uerj.gymflow.model.Plano;
import com.uerj.gymflow.model.Treino;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe responsável pela interface de usuário para operações com Aluno
 */
public class AlunoView {
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final PessoaView pessoaView;

    public Scanner getScanner() {
        return scanner;
    }
    
    public AlunoView(Scanner scanner) {
        this.scanner = scanner;
        this.pessoaView = new PessoaView(scanner);
    }

    /**
     * Exibe o menu de opções para o gerenciamento de alunos
     *
     * @return A opção escolhida pelo usuário
     */
    public int exibirMenu() {
        System.out.println("\n===== MENU ALUNO =====");
        System.out.println("1. Cadastrar novo aluno");
        System.out.println("2. Buscar aluno por ID");
        System.out.println("3. Buscar aluno por CPF");
        System.out.println("4. Listar todos os alunos");
        System.out.println("5. Atualizar aluno");
        System.out.println("6. Excluir aluno");
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
     * Coleta os dados para um novo aluno ou atualização
     *
     * @param alunoExistente Aluno existente para atualização (null para novo aluno)
     * @param planosDisponiveis Lista de planos disponíveis para seleção
     * @return Objeto Aluno preenchido com os dados informados
     */
    public Aluno coletarDadosAluno(Aluno alunoExistente, List<Plano> planosDisponiveis) {
        System.out.println("\n===== " + (alunoExistente == null ? "CADASTRO" : "ATUALIZAÇÃO") + " DE ALUNO =====");

        // Primeiro, coletamos os dados de pessoa
        Aluno aluno;
        if (alunoExistente != null) {
            aluno = alunoExistente;
        } else {
            aluno = new Aluno();
            // Inicialize a data de nascimento para evitar NullPointerException
            aluno.setDataNascimento(LocalDate.now());
            // Inicialize a data de matrícula com a data atual para novos alunos
            aluno.setDataMatricula(LocalDate.now());
        }

        // Usar o PessoaView para coletar os dados básicos de pessoa
        Aluno pessoaColetada = (Aluno) pessoaView.coletarDadosPessoa(aluno);
        if (pessoaColetada == null) {
            return null;
        }

        // Transferir os dados coletados para o aluno
        if (alunoExistente == null) {
            aluno.setNome(pessoaColetada.getNome());
            aluno.setDataNascimento(pessoaColetada.getDataNascimento());
            aluno.setCpf(pessoaColetada.getCpf());
            aluno.setTelefone(pessoaColetada.getTelefone());
            aluno.setEmail(pessoaColetada.getEmail());
        }

        // Agora coletamos os dados específicos de aluno
        try {
            // Data de matrícula
            System.out.print("Data de Matrícula (dd/mm/aaaa)" +
                    (alunoExistente != null && alunoExistente.getDataMatricula() != null ? 
                        " [" + alunoExistente.getDataMatricula().format(dateFormatter) + "]" : 
                        " [" + LocalDate.now().format(dateFormatter) + "]") +
                    ": ");
            String dataMatriculaStr = scanner.nextLine().trim();
            if (!dataMatriculaStr.isEmpty()) {
                try {
                    LocalDate dataMatricula = LocalDate.parse(dataMatriculaStr, dateFormatter);
                    if (dataMatricula.isAfter(LocalDate.now())) {
                        System.out.println("A data de matrícula não pode ser no futuro.");
                        return null;
                    }
                    aluno.setDataMatricula(dataMatricula);
                } catch (DateTimeParseException e) {
                    System.out.println("Formato de data inválido. Use o formato dd/mm/aaaa.");
                    return null;
                }
            } else if (alunoExistente == null) {
                // Se for um novo aluno, use a data atual como padrão
                aluno.setDataMatricula(LocalDate.now());
            }

            // Data de assinatura
            System.out.print("Data de Assinatura do Contrato (dd/mm/aaaa)" +
                    (alunoExistente != null && alunoExistente.getDataAssinatura() != null ? 
                        " [" + alunoExistente.getDataAssinatura().format(dateFormatter) + "]" : "") +
                    ": ");
            String dataAssinaturaStr = scanner.nextLine().trim();
            if (!dataAssinaturaStr.isEmpty()) {
                try {
                    LocalDate dataAssinatura = LocalDate.parse(dataAssinaturaStr, dateFormatter);
                    if (dataAssinatura.isAfter(LocalDate.now())) {
                        System.out.println("A data de assinatura não pode ser no futuro.");
                        return null;
                    }
                    aluno.setDataAssinatura(dataAssinatura);
                } catch (DateTimeParseException e) {
                    System.out.println("Formato de data inválido. Use o formato dd/mm/aaaa.");
                    return null;
                }
            } else if (alunoExistente != null && alunoExistente.getDataAssinatura() != null) {
                // Mantém a data de assinatura anterior em caso de atualização
                aluno.setDataAssinatura(alunoExistente.getDataAssinatura());
            }

            // Seleção de plano
            if (planosDisponiveis != null && !planosDisponiveis.isEmpty()) {
                System.out.println("\n===== PLANOS DISPONÍVEIS =====");
                for (Plano plano : planosDisponiveis) {
                    System.out.println(plano.getIdPlano() + ". " + plano.getNomePlano() + 
                                       " - R$ " + String.format("%.2f", plano.getValorMensal()) + 
                                       " (Duração: " + plano.getDuracao() + " meses)");
                }

                System.out.print("Selecione o ID do plano" +
                        (alunoExistente != null && alunoExistente.getIdPlano() != null ? 
                            " [" + alunoExistente.getIdPlano() + "]" : "") +
                        " (0 para nenhum): ");
                String planoIdStr = scanner.nextLine().trim();
                
                if (!planoIdStr.isEmpty()) {
                    try {
                        int planoId = Integer.parseInt(planoIdStr);
                        if (planoId > 0) {
                            // Busca o plano na lista
                            boolean planoEncontrado = false;
                            for (Plano plano : planosDisponiveis) {
                                if (plano.getIdPlano() == planoId) {
                                    aluno.setIdPlano(planoId);
                                    aluno.setPlano(plano);
                                    planoEncontrado = true;
                                    break;
                                }
                            }
                            if (!planoEncontrado) {
                                System.out.println("Plano inválido. Por favor, selecione um ID válido.");
                                return null;
                            }
                        } else if (planoId == 0) {
                            aluno.setIdPlano(null);
                            aluno.setPlano(null);
                        } else {
                            System.out.println("Por favor, digite um ID válido ou 0 para nenhum plano.");
                            return null;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Por favor, digite um número válido.");
                        return null;
                    }
                } else if (alunoExistente != null) {
                    // Mantém o plano anterior em caso de atualização
                    aluno.setIdPlano(alunoExistente.getIdPlano());
                    aluno.setPlano(alunoExistente.getPlano());
                }
            } else {
                System.out.println("Não há planos disponíveis no momento.");
                aluno.setIdPlano(null);
                aluno.setPlano(null);
            }

            return aluno;
        } catch (Exception e) {
            System.out.println("Erro ao coletar dados: " + e.getMessage());
            return null;
        }
    }

    /**
     * Exibe os detalhes de um aluno
     *
     * @param aluno O aluno a ser exibido
     */
    public void exibirDadosAluno(Aluno aluno) {
        if (aluno == null) {
            System.out.println("Aluno não encontrado.");
            return;
        }

        System.out.println("\n------ DADOS DO ALUNO ------");
        System.out.println("ID Aluno: " + aluno.getIdAluno());
        System.out.println("ID Pessoa: " + aluno.getIdPessoa());
        System.out.println("Nome: " + aluno.getNome());
        
        // Verifica se a data de nascimento não é nula antes de formatar
        if (aluno.getDataNascimento() != null) {
            System.out.println("Data de Nascimento: " + aluno.getDataNascimento().format(dateFormatter) +
                    " (" + calcularIdade(aluno.getDataNascimento()) + " anos)");
        } else {
            System.out.println("Data de Nascimento: Não informada");
        }
        
        System.out.println("CPF: " + formatarCpf(aluno.getCpf()));
        System.out.println("Telefone: " + aluno.getTelefone());
        System.out.println("E-mail: " + aluno.getEmail());
        
        // Verifica se a data de matrícula não é nula antes de formatar
        if (aluno.getDataMatricula() != null) {
            System.out.println("Data de Matrícula: " + aluno.getDataMatricula().format(dateFormatter));
        } else {
            System.out.println("Data de Matrícula: Não informada");
        }
        
        // Verifica se a data de assinatura não é nula antes de formatar
        if (aluno.getDataAssinatura() != null) {
            System.out.println("Data de Assinatura: " + aluno.getDataAssinatura().format(dateFormatter));
        } else {
            System.out.println("Data de Assinatura: Não informada");
        }
        
        // Exibe informações do plano se existir
        if (aluno.getPlano() != null) {
            System.out.println("\n--- Plano Contratado ---");
            System.out.println("Nome do Plano: " + aluno.getPlano().getNomePlano());
            System.out.println("Descrição: " + aluno.getPlano().getDescricao());
            System.out.println("Duração: " + aluno.getPlano().getDuracao() + " meses");
            System.out.println("Valor Mensal: R$ " + String.format("%.2f", aluno.getPlano().getValorMensal()));
            System.out.println("Valor Total: R$ " + String.format("%.2f", aluno.getPlano().calcularValorTotal()));
        } else {
            System.out.println("Plano: Nenhum plano associado");
        }
        
        System.out.println("----------------------------");
    }

    /**
     * Exibe uma lista de alunos de forma resumida
     *
     * @param alunos Lista de alunos a serem exibidos
     */
    public void exibirListaAlunos(List<Aluno> alunos) {
        if (alunos == null || alunos.isEmpty()) {
            System.out.println("Não há alunos cadastrados.");
            return;
        }

        System.out.println("\n===== LISTA DE ALUNOS =====");
        for (Aluno aluno : alunos) {
            System.out.println("---------------------------");
            System.out.println("ID: " + aluno.getIdAluno());
            System.out.println("Nome: " + aluno.getNome());
            System.out.println("CPF: " + formatarCpf(aluno.getCpf()));
            System.out.println("Data de Matrícula: " + 
                    (aluno.getDataMatricula() != null ? aluno.getDataMatricula().format(dateFormatter) : "Não informada"));
            System.out.println("Plano: " + 
                    (aluno.getPlano() != null ? aluno.getPlano().getNomePlano() : "Nenhum"));
        }
        System.out.println("---------------------------");
        System.out.println("Total de alunos: " + alunos.size());
    }

    /**
     * Solicita o ID de um aluno
     *
     * @return O ID informado ou null em caso de erro
     */
    public Integer solicitarId() {
        System.out.print("Digite o ID do aluno: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um ID válido.");
            return null;
        }
    }

    /**
     * Solicita um CPF para busca
     *
     * @return O CPF informado
     */
    public String solicitarCpf() {
        System.out.print("Digite o CPF do aluno (apenas números): ");
        String cpf = scanner.nextLine().trim();
        
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");
        
        if (cpf.length() != 11) {
            System.out.println("CPF deve conter 11 dígitos numéricos.");
            return null;
        }
        
        return cpf;
    }

    /**
     * Solicita confirmação para exclusão
     *
     * @return true se confirmado, false caso contrário
     */
    public boolean confirmarExclusao() {
        System.out.print("Tem certeza que deseja excluir este aluno? (S/N): ");
        String confirmacao = scanner.nextLine().trim().toUpperCase();
        return confirmacao.equals("S");
    }

    /**
     * Exibe mensagem de sucesso para cadastro
     *
     * @param aluno O aluno cadastrado
     */
    public void exibirMensagemCadastroSucesso(Aluno aluno) {
        System.out.println("Aluno cadastrado com sucesso! ID: " + aluno.getIdAluno());
    }

    /**
     * Exibe mensagem de sucesso para atualização
     */
    public void exibirMensagemAtualizacaoSucesso() {
        System.out.println("Aluno atualizado com sucesso!");
    }

    /**
     * Exibe mensagem de sucesso para exclusão
     */
    public void exibirMensagemExclusaoSucesso() {
        System.out.println("Aluno excluído com sucesso!");
    }

    /**
     * Exibe mensagem de operação cancelada
     */
    public void exibirMensagemOperacaoCancelada() {
        System.out.println("Operação cancelada.");
    }
    
    /**
     * Exibe o menu de opções para o gerenciamento de treinos do aluno
     *
     * @param aluno O aluno cujos treinos serão gerenciados
     * @return A opção escolhida pelo usuário
     */
    public int exibirMenuTreinosAluno(Aluno aluno) {
        System.out.println("\n===== GERENCIAMENTO DE TREINOS DO ALUNO =====");
        System.out.println("Aluno: " + aluno.getNome() + " (ID: " + aluno.getIdAluno() + ")");
        System.out.println("1. Associar Novo Treino ao Aluno");
        System.out.println("2. Listar Treinos do Aluno");
        System.out.println("3. Listar Treinos Ativos do Aluno");
        System.out.println("4. Atualizar Informações do Treino");
        System.out.println("5. Encerrar Treino");
        System.out.println("6. Renovar Treino");
        System.out.println("7. Remover Treino do Aluno");
        System.out.println("0. Voltar ao Menu de Alunos");
        System.out.print("Escolha uma opção: ");
    
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida. Por favor, digite um número.");
            return -1;
        }
    }
    
    /**
     * Exibe a lista de treinos de um aluno
     *
     * @param treinosDoAluno Lista de treinos associados ao aluno
     */
    public void exibirTreinosAluno(List<AlunoTreino> treinosDoAluno) {
        if (treinosDoAluno == null || treinosDoAluno.isEmpty()) {
            System.out.println("\nO aluno não possui treinos associados.");
            return;
        }
        
        System.out.println("\n===== TREINOS DO ALUNO =====");
        System.out.printf("%-5s %-30s %-12s %-12s %-15s %s\n", 
                "ID", "Nome do Treino", "Data Início", "Data Fim", "Status", "Observações");
        System.out.println("-".repeat(100));
        
        for (AlunoTreino alunoTreino : treinosDoAluno) {
            Treino treino = alunoTreino.getTreino();
            String dataInicio = alunoTreino.getDataInicio() != null ? 
                    alunoTreino.getDataInicio().format(dateFormatter) : "-";
            String dataFim = alunoTreino.getDataFim() != null ? 
                    alunoTreino.getDataFim().format(dateFormatter) : "Sem data fim";
            
            String status = "Inativo";
            if (alunoTreino.isAtivo()) {
                Integer diasRestantes = alunoTreino.diasAteExpirar();
                if (diasRestantes == null) {
                    status = "Ativo";
                } else if (diasRestantes < 0) {
                    status = "Expirado";
                } else if (diasRestantes < 7) {
                    status = "Expira em " + diasRestantes + " dias";
                } else {
                    status = "Ativo";
                }
            }
            
            String obs = alunoTreino.getObservacoes();
            if (obs != null && obs.length() > 20) {
                obs = obs.substring(0, 17) + "...";
            }
            
            System.out.printf("%-5d %-30s %-12s %-12s %-15s %s\n", 
                    treino.getIdTreino(),
                    treino.getNomeTreino(),
                    dataInicio,
                    dataFim,
                    status,
                    obs != null ? obs : "");
        }
    }
    
    /**
     * Coleta dados para associar um treino a um aluno
     *
     * @param treino O treino a ser associado
     * @return Objeto AlunoTreino preenchido ou null se cancelado
     */
    public AlunoTreino coletarDadosAlunoTreino(Treino treino) {
        AlunoTreino alunoTreino = new AlunoTreino();
        alunoTreino.setIdTreino(treino.getIdTreino());
        alunoTreino.setTreino(treino);
        
        System.out.println("\n===== ASSOCIAR TREINO AO ALUNO =====");
        System.out.println("Treino: " + treino.getNomeTreino() + " (ID: " + treino.getIdTreino() + ")");
        System.out.println("Digite 'cancelar' a qualquer momento para cancelar a operação");
        
        // Data de início (padrão é a data atual)
        System.out.print("Data de início [" + LocalDate.now().format(dateFormatter) + "] (dd/MM/yyyy): ");
        String dataInicioStr = scanner.nextLine();
        
        if (dataInicioStr.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        if (dataInicioStr.isEmpty()) {
            alunoTreino.setDataInicio(LocalDate.now());
        } else {
            try {
                LocalDate dataInicio = LocalDate.parse(dataInicioStr, dateFormatter);
                alunoTreino.setDataInicio(dataInicio);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Usando a data atual.");
                alunoTreino.setDataInicio(LocalDate.now());
            }
        }
        
        // Data fim
        System.out.print("Data de fim (dd/MM/yyyy) [opcional, deixe em branco para sem data fim]: ");
        String dataFimStr = scanner.nextLine();
        
        if (dataFimStr.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        if (!dataFimStr.isEmpty()) {
            try {
                LocalDate dataFim = LocalDate.parse(dataFimStr, dateFormatter);
                
                // Verificar se a data fim é posterior à data início
                if (dataFim.isBefore(alunoTreino.getDataInicio())) {
                    System.out.println("Data de fim deve ser posterior à data de início. Treino será cadastrado sem data fim.");
                } else {
                    alunoTreino.setDataFim(dataFim);
                }
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Treino será cadastrado sem data fim.");
            }
        }
        
        // Observações
        System.out.print("Observações (opcional): ");
        String observacoes = scanner.nextLine();
        
        if (observacoes.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        alunoTreino.setObservacoes(observacoes);
        
        return alunoTreino;
    }
    
    /**
     * Coleta dados para atualizar a associação de um treino com um aluno
     *
     * @param alunoTreinoAtual Dados atuais da associação
     * @return Objeto AlunoTreino atualizado ou null se cancelado
     */
    public AlunoTreino coletarDadosAtualizacaoAlunoTreino(AlunoTreino alunoTreinoAtual) {
        if (alunoTreinoAtual == null || alunoTreinoAtual.getTreino() == null) {
            System.out.println("Dados inválidos para atualização.");
            return null;
        }
        
        Treino treino = alunoTreinoAtual.getTreino();
        
        System.out.println("\n===== ATUALIZAR TREINO DO ALUNO =====");
        System.out.println("Treino: " + treino.getNomeTreino() + " (ID: " + treino.getIdTreino() + ")");
        System.out.println("Deixe em branco para manter o valor atual. Digite 'cancelar' para cancelar a operação.");
        
        AlunoTreino alunoTreinoAtualizado = new AlunoTreino();
        alunoTreinoAtualizado.setIdAluno(alunoTreinoAtual.getIdAluno());
        alunoTreinoAtualizado.setIdTreino(alunoTreinoAtual.getIdTreino());
        
        // Data de início
        String dataInicioAtual = alunoTreinoAtual.getDataInicio() != null ? 
                alunoTreinoAtual.getDataInicio().format(dateFormatter) : "Não definida";
        System.out.print("Data de início [" + dataInicioAtual + "] (dd/MM/yyyy): ");
        String dataInicioStr = scanner.nextLine();
        
        if (dataInicioStr.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        if (dataInicioStr.isEmpty()) {
            alunoTreinoAtualizado.setDataInicio(alunoTreinoAtual.getDataInicio());
        } else {
            try {
                LocalDate dataInicio = LocalDate.parse(dataInicioStr, dateFormatter);
                alunoTreinoAtualizado.setDataInicio(dataInicio);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Mantendo a data atual.");
                alunoTreinoAtualizado.setDataInicio(alunoTreinoAtual.getDataInicio());
            }
        }
        
        // Data fim
        String dataFimAtual = alunoTreinoAtual.getDataFim() != null ? 
                alunoTreinoAtual.getDataFim().format(dateFormatter) : "Sem data fim";
        System.out.print("Data de fim [" + dataFimAtual + "] (dd/MM/yyyy ou 'remover' para remover a data fim): ");
        String dataFimStr = scanner.nextLine();
        
        if (dataFimStr.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        if (dataFimStr.isEmpty()) {
            alunoTreinoAtualizado.setDataFim(alunoTreinoAtual.getDataFim());
        } else if (dataFimStr.equalsIgnoreCase("remover")) {
            alunoTreinoAtualizado.setDataFim(null);
        } else {
            try {
                LocalDate dataFim = LocalDate.parse(dataFimStr, dateFormatter);
                
                // Verificar se a data fim é posterior à data início
                if (dataFim.isBefore(alunoTreinoAtualizado.getDataInicio())) {
                    System.out.println("Data de fim deve ser posterior à data de início. Mantendo a data atual.");
                    alunoTreinoAtualizado.setDataFim(alunoTreinoAtual.getDataFim());
                } else {
                    alunoTreinoAtualizado.setDataFim(dataFim);
                }
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Mantendo a data atual.");
                alunoTreinoAtualizado.setDataFim(alunoTreinoAtual.getDataFim());
            }
        }
        
        // Observações
        String obsAtuais = alunoTreinoAtual.getObservacoes() != null ? alunoTreinoAtual.getObservacoes() : "";
        System.out.print("Observações [" + obsAtuais + "]: ");
        String observacoes = scanner.nextLine();
        
        if (observacoes.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        alunoTreinoAtualizado.setObservacoes(observacoes.isEmpty() ? alunoTreinoAtual.getObservacoes() : observacoes);
        
        return alunoTreinoAtualizado;
    }
    
    /**
     * Solicita o número de dias para renovação do treino
     *
     * @return Número de dias para renovação ou null se cancelado
     */
    public Integer solicitarDiasRenovacao() {
        System.out.print("\nDigite o número de dias para renovar o treino (ou 'cancelar'): ");
        String entrada = scanner.nextLine();
        
        if (entrada.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        try {
            Integer dias = Integer.parseInt(entrada);
            if (dias <= 0) {
                System.out.println("O número de dias deve ser positivo.");
                return null;
            }
            return dias;
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido. Digite um número.");
            return null;
        }
    }
    
    /**
     * Confirma a remoção de um treino do aluno
     *
     * @param alunoTreino Associação aluno-treino a ser removida
     * @return true se confirmado, false caso contrário
     */
    public boolean confirmarRemocaoTreino(AlunoTreino alunoTreino) {
        if (alunoTreino == null || alunoTreino.getTreino() == null) {
            return false;
        }
        
        Treino treino = alunoTreino.getTreino();
        
        System.out.println("\nTreino: " + treino.getNomeTreino() + " (ID: " + treino.getIdTreino() + ")");
        System.out.println("Data início: " + (alunoTreino.getDataInicio() != null ? 
                alunoTreino.getDataInicio().format(dateFormatter) : "-"));
        System.out.println("Data fim: " + (alunoTreino.getDataFim() != null ? 
                alunoTreino.getDataFim().format(dateFormatter) : "Sem data fim"));
        
        System.out.print("\nTem certeza que deseja remover este treino do aluno? (S/N): ");
        String resposta = scanner.nextLine();
        
        return resposta.equalsIgnoreCase("S");
    }
    
    /**
     * Exibe mensagem de sucesso para operações com treinos
     *
     * @param mensagem Mensagem de sucesso
     */
    public void exibirMensagemSucesso(String mensagem) {
        System.out.println("\nSUCESSO: " + mensagem);
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
