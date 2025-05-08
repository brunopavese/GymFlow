package com.uerj.gymflow.view;

import com.uerj.gymflow.model.Aluno;
import com.uerj.gymflow.model.Avaliacao;
import com.uerj.gymflow.model.Professor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe responsável pela interface de usuário para operações com Avaliações
 */
public class AvaliacaoView {
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AvaliacaoView(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Exibe o menu de opções para o gerenciamento de avaliações
     *
     * @return A opção escolhida pelo usuário
     */
    public int exibirMenu() {
        System.out.println("\n===== MENU AVALIAÇÃO =====");
        System.out.println("1. Cadastrar nova avaliação");
        System.out.println("2. Buscar avaliação por ID");
        System.out.println("3. Listar avaliações de um aluno");
        System.out.println("4. Atualizar avaliação");
        System.out.println("5. Excluir avaliação");
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
     * Coleta os dados para uma nova avaliação ou atualização
     *
     * @param avaliacaoExistente Avaliação existente para atualização (null para nova avaliação)
     * @param alunos Lista de alunos disponíveis para seleção
     * @param professores Lista de professores disponíveis para seleção
     * @return Objeto Avaliacao preenchido com os dados informados
     */
    public Avaliacao coletarDadosAvaliacao(Avaliacao avaliacaoExistente, List<Aluno> alunos, List<Professor> professores) {
        System.out.println("\n===== " + (avaliacaoExistente == null ? "CADASTRO" : "ATUALIZAÇÃO") + " DE AVALIAÇÃO =====");

        Avaliacao avaliacao;
        if (avaliacaoExistente != null) {
            avaliacao = avaliacaoExistente;
        } else {
            avaliacao = new Avaliacao();
            // Inicializa a data de avaliação com a data atual para novas avaliações
            avaliacao.setDataAvaliacao(LocalDate.now());
        }

        try {
            // Se for uma nova avaliação, primeiro selecionamos o aluno
            if (avaliacaoExistente == null) {
                // Seleção de aluno
                if (alunos == null || alunos.isEmpty()) {
                    System.out.println("Não há alunos cadastrados. Cadastre um aluno primeiro.");
                    return null;
                }

                System.out.println("\n===== ALUNOS DISPONÍVEIS =====");
                for (Aluno aluno : alunos) {
                    System.out.println(aluno.getIdAluno() + ". " + aluno.getNome() + " (CPF: " + formatarCpf(aluno.getCpf()) + ")");
                }

                System.out.print("Selecione o ID do aluno: ");
                try {
                    int alunoId = Integer.parseInt(scanner.nextLine().trim());
                    
                    // Busca o aluno na lista
                    boolean alunoEncontrado = false;
                    for (Aluno aluno : alunos) {
                        if (aluno.getIdAluno() == alunoId) {
                            avaliacao.setIdAluno(alunoId);
                            avaliacao.setAluno(aluno);
                            alunoEncontrado = true;
                            break;
                        }
                    }
                    
                    if (!alunoEncontrado) {
                        System.out.println("Aluno inválido. Por favor, selecione um ID válido.");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, digite um número válido.");
                    return null;
                }
            }

            // Seleção de professor (opcional)
            if (professores != null && !professores.isEmpty()) {
                System.out.println("\n===== PROFESSORES DISPONÍVEIS =====");
                for (Professor professor : professores) {
                    System.out.println(professor.getIdProfessor() + ". " + professor.getNome() +
                                      " (Especialidade: " + professor.getEspecialidade() + ", CREF: " + professor.getCref() + ")");
                }

                System.out.print("Selecione o ID do professor" +
                        (avaliacaoExistente != null && avaliacaoExistente.getIdProfessor() != null ?
                            " [" + avaliacaoExistente.getIdProfessor() + "]" : "") +
                        " (0 para nenhum): ");
                String professorIdStr = scanner.nextLine().trim();
                
                if (!professorIdStr.isEmpty()) {
                    try {
                        int professorId = Integer.parseInt(professorIdStr);
                        if (professorId > 0) {
                            // Busca o professor na lista
                            boolean professorEncontrado = false;
                            for (Professor professor : professores) {
                                if (professor.getIdProfessor() == professorId) {
                                    avaliacao.setIdProfessor(professorId);
                                    avaliacao.setProfessor(professor);
                                    professorEncontrado = true;
                                    break;
                                }
                            }
                            if (!professorEncontrado) {
                                System.out.println("Professor inválido. Por favor, selecione um ID válido.");
                                return null;
                            }
                        } else if (professorId == 0) {
                            avaliacao.setIdProfessor(null);
                            avaliacao.setProfessor(null);
                        } else {
                            System.out.println("Por favor, digite um ID válido ou 0 para nenhum professor.");
                            return null;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Por favor, digite um número válido.");
                        return null;
                    }
                } else if (avaliacaoExistente != null) {
                    // Mantém o professor anterior em caso de atualização
                    avaliacao.setIdProfessor(avaliacaoExistente.getIdProfessor());
                    avaliacao.setProfessor(avaliacaoExistente.getProfessor());
                }
            } else {
                System.out.println("Não há professores disponíveis no momento.");
                avaliacao.setIdProfessor(null);
                avaliacao.setProfessor(null);
            }

            // Data da avaliação
            System.out.print("Data da Avaliação (dd/mm/aaaa)" +
                    (avaliacao.getDataAvaliacao() != null ?
                        " [" + avaliacao.getDataAvaliacao().format(dateFormatter) + "]" :
                        " [" + LocalDate.now().format(dateFormatter) + "]") +
                    ": ");
            String dataAvaliacaoStr = scanner.nextLine().trim();
            if (!dataAvaliacaoStr.isEmpty()) {
                try {
                    LocalDate dataAvaliacao = LocalDate.parse(dataAvaliacaoStr, dateFormatter);
                    if (dataAvaliacao.isAfter(LocalDate.now())) {
                        System.out.println("A data da avaliação não pode ser no futuro.");
                        return null;
                    }
                    avaliacao.setDataAvaliacao(dataAvaliacao);
                } catch (DateTimeParseException e) {
                    System.out.println("Formato de data inválido. Use o formato dd/mm/aaaa.");
                    return null;
                }
            } else if (avaliacao.getDataAvaliacao() == null) {
                // Se for uma nova avaliação e nenhuma data for informada, use a data atual
                avaliacao.setDataAvaliacao(LocalDate.now());
            }

            // Peso
            System.out.print("Peso (kg)" +
                    (avaliacao.getPeso() != null ? " [" + avaliacao.getPeso() + "]" : "") +
                    ": ");
            String pesoStr = scanner.nextLine().trim();
            if (!pesoStr.isEmpty()) {
                try {
                    float peso = Float.parseFloat(pesoStr.replace(",", "."));
                    if (peso <= 0 || peso > 500) {
                        System.out.println("Por favor, informe um peso válido (entre 0 e 500 kg).");
                        return null;
                    }
                    avaliacao.setPeso(peso);
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, digite um número válido para o peso.");
                    return null;
                }
            } else if (avaliacaoExistente != null) {
                // Mantém o peso anterior em caso de atualização
                avaliacao.setPeso(avaliacaoExistente.getPeso());
            }

            // Altura
            System.out.print("Altura (em metros, ex: 1.75)" +
                    (avaliacao.getAltura() != null ? " [" + avaliacao.getAltura() + "]" : "") +
                    ": ");
            String alturaStr = scanner.nextLine().trim();
            if (!alturaStr.isEmpty()) {
                try {
                    float altura = Float.parseFloat(alturaStr.replace(",", "."));
                    if (altura <= 0 || altura > 3) {
                        System.out.println("Por favor, informe uma altura válida (entre 0 e 3 metros).");
                        return null;
                    }
                    avaliacao.setAltura(altura);
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, digite um número válido para a altura.");
                    return null;
                }
            } else if (avaliacaoExistente != null) {
                // Mantém a altura anterior em caso de atualização
                avaliacao.setAltura(avaliacaoExistente.getAltura());
            }

            // Observações
            System.out.print("Observações" +
                    (avaliacao.getObservacoes() != null && !avaliacao.getObservacoes().isEmpty() ?
                        " [" + avaliacao.getObservacoes() + "]" : "") +
                    ": ");
            String observacoes = scanner.nextLine().trim();
            if (!observacoes.isEmpty()) {
                avaliacao.setObservacoes(observacoes);
            } else if (avaliacaoExistente != null && avaliacaoExistente.getObservacoes() != null) {
                // Mantém as observações anteriores em caso de atualização
                avaliacao.setObservacoes(avaliacaoExistente.getObservacoes());
            }

            // Se peso e altura foram informados, exibe o IMC calculado
            if (avaliacao.getPeso() != null && avaliacao.getAltura() != null) {
                Float imc = avaliacao.calcularIMC();
                if (imc != null) {
                    System.out.println("\nIMC calculado: " + String.format("%.2f", imc) +
                                      " - Classificação: " + avaliacao.getClassificacaoIMC());
                }
            }

            return avaliacao;
        } catch (Exception e) {
            System.out.println("Erro ao coletar dados: " + e.getMessage());
            return null;
        }
    }

    /**
     * Exibe os detalhes de uma avaliação
     *
     * @param avaliacao A avaliação a ser exibida
     */
    public void exibirDadosAvaliacao(Avaliacao avaliacao) {
        if (avaliacao == null) {
            System.out.println("Avaliação não encontrada.");
            return;
        }

        System.out.println("\n------ DADOS DA AVALIAÇÃO ------");
        System.out.println("ID: " + avaliacao.getIdAvaliacao());
        System.out.println("Data: " + avaliacao.getDataAvaliacao().format(dateFormatter));
        
        // Dados do Aluno
        System.out.println("\n--- Dados do Aluno ---");
        if (avaliacao.getAluno() != null) {
            System.out.println("Nome: " + avaliacao.getAluno().getNome());
            System.out.println("CPF: " + formatarCpf(avaliacao.getAluno().getCpf()));
        } else {
            System.out.println("ID do Aluno: " + avaliacao.getIdAluno());
        }
        
        // Dados do Professor
        System.out.println("\n--- Dados do Professor ---");
        if (avaliacao.getProfessor() != null) {
            System.out.println("Nome: " + avaliacao.getProfessor().getNome());
            System.out.println("Especialidade: " + avaliacao.getProfessor().getEspecialidade());
            System.out.println("CREF: " + avaliacao.getProfessor().getCref());
        } else {
            System.out.println("Nenhum professor associado");
        }
        
        // Dados físicos
        System.out.println("\n--- Dados Físicos ---");
        System.out.println("Peso: " + (avaliacao.getPeso() != null ? avaliacao.getPeso() + " kg" : "Não informado"));
        System.out.println("Altura: " + (avaliacao.getAltura() != null ? avaliacao.getAltura() + " m" : "Não informada"));
        
        // IMC
        Float imc = avaliacao.calcularIMC();
        if (imc != null) {
            System.out.println("IMC: " + String.format("%.2f", imc));
            System.out.println("Classificação IMC: " + avaliacao.getClassificacaoIMC());
        } else {
            System.out.println("IMC: Não calculado (faltam dados)");
        }
        
        // Observações
        System.out.println("\n--- Observações ---");
        System.out.println(avaliacao.getObservacoes() != null && !avaliacao.getObservacoes().isEmpty() ?
                avaliacao.getObservacoes() : "Nenhuma observação registrada");
        
        System.out.println("-------------------------------");
    }

    /**
     * Exibe uma lista de avaliações de forma resumida
     *
     * @param avaliacoes Lista de avaliações a serem exibidas
     * @param aluno Aluno dono das avaliações (opcional)
     */
    public void exibirListaAvaliacoes(List<Avaliacao> avaliacoes, Aluno aluno) {
        if (avaliacoes == null || avaliacoes.isEmpty()) {
            if (aluno != null) {
                System.out.println("Não há avaliações cadastradas para " + aluno.getNome() + ".");
            } else {
                System.out.println("Não há avaliações cadastradas.");
            }
            return;
        }

        System.out.println("\n===== LISTA DE AVALIAÇÕES " + 
                          (aluno != null ? "DE " + aluno.getNome().toUpperCase() : "") + 
                          " =====");
        
        for (int i = 0; i < avaliacoes.size(); i++) {
            Avaliacao avaliacao = avaliacoes.get(i);
            System.out.println("---------------------------");
            System.out.println("ID: " + avaliacao.getIdAvaliacao());
            System.out.println("Data: " + avaliacao.getDataAvaliacao().format(dateFormatter));
            
            if (aluno == null && avaliacao.getAluno() != null) {
                System.out.println("Aluno: " + avaliacao.getAluno().getNome());
            }
            
            if (avaliacao.getProfessor() != null) {
                System.out.println("Professor: " + avaliacao.getProfessor().getNome());
            }
            
            System.out.println("Peso: " + (avaliacao.getPeso() != null ? avaliacao.getPeso() + " kg" : "Não informado"));
            System.out.println("Altura: " + (avaliacao.getAltura() != null ? avaliacao.getAltura() + " m" : "Não informada"));
            
            Float imc = avaliacao.calcularIMC();
            if (imc != null) {
                System.out.println("IMC: " + String.format("%.2f", imc) + " - " + avaliacao.getClassificacaoIMC());
            }
            
            // Se houver uma avaliação anterior, exibe comparação
            if (i < avaliacoes.size() - 1) {
                Avaliacao avaliacaoAnterior = avaliacoes.get(i + 1);
                String comparacao = avaliacao.compararCom(avaliacaoAnterior);
                if (comparacao != null && !comparacao.isEmpty()) {
                    System.out.println("\nComparação com avaliação anterior:");
                    System.out.println(comparacao);
                }
            }
        }
        
        System.out.println("---------------------------");
        System.out.println("Total de avaliações: " + avaliacoes.size());
    }

    /**
     * Solicita o ID de uma avaliação
     *
     * @return O ID informado ou null em caso de erro
     */
    public Integer solicitarIdAvaliacao() {
        System.out.print("Digite o ID da avaliação: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um ID válido.");
            return null;
        }
    }

    /**
     * Solicita o ID de um aluno para buscar suas avaliações
     *
     * @return O ID informado ou null em caso de erro
     */
    public Integer solicitarIdAluno() {
        System.out.print("Digite o ID do aluno: ");
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
        System.out.print("Tem certeza que deseja excluir esta avaliação? (S/N): ");
        String confirmacao = scanner.nextLine().trim().toUpperCase();
        return confirmacao.equals("S");
    }

    /**
     * Exibe mensagem de sucesso para cadastro
     *
     * @param avaliacao A avaliação cadastrada
     */
    public void exibirMensagemCadastroSucesso(Avaliacao avaliacao) {
        System.out.println("Avaliação cadastrada com sucesso! ID: " + avaliacao.getIdAvaliacao());
    }

    /**
     * Exibe mensagem de sucesso para atualização
     */
    public void exibirMensagemAtualizacaoSucesso() {
        System.out.println("Avaliação atualizada com sucesso!");
    }

    /**
     * Exibe mensagem de sucesso para exclusão
     */
    public void exibirMensagemExclusaoSucesso() {
        System.out.println("Avaliação excluída com sucesso!");
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
