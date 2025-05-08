package com.uerj.gymflow.view;

import com.uerj.gymflow.model.Exercicio;

import java.util.List;
import java.util.Scanner;

/**
 * Classe responsável pela interface de usuário para operações com Exercícios
 */
public class ExercicioView {
    private final Scanner scanner;
    private static final String[] GRUPOS_MUSCULARES = {
            "Peito", "Costas", "Ombros", "Bíceps", "Tríceps", 
            "Antebraço", "Abdômen", "Lombar", "Quadríceps", 
            "Posterior de Coxa", "Glúteos", "Panturrilha", "Cardio", "Mobilidade"
    };

    public ExercicioView(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Exibe o menu de opções para o gerenciamento de exercícios
     *
     * @return A opção escolhida pelo usuário
     */
    public int exibirMenu() {
        System.out.println("\n===== MENU EXERCÍCIOS =====");
        System.out.println("1. Cadastrar novo exercício");
        System.out.println("2. Buscar exercício por ID");
        System.out.println("3. Listar todos os exercícios");
        System.out.println("4. Atualizar exercício");
        System.out.println("5. Excluir exercício");
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
     * Coleta os dados para um novo exercício ou atualização
     *
     * @param exercicioExistente Exercício existente para atualização (null para novo exercício)
     * @return Objeto Exercicio preenchido com os dados informados
     */
    public Exercicio coletarDadosExercicio(Exercicio exercicioExistente) {
        System.out.println("\n===== " + (exercicioExistente == null ? "CADASTRO" : "ATUALIZAÇÃO") + " DE EXERCÍCIO =====");

        Exercicio exercicio;
        if (exercicioExistente != null) {
            exercicio = exercicioExistente;
        } else {
            exercicio = new Exercicio();
        }

        try {
            // Nome do exercício
            System.out.print("Nome do exercício" +
                    (exercicio.getNomeExercicio() != null ? " [" + exercicio.getNomeExercicio() + "]" : "") +
                    ": ");
            String nome = scanner.nextLine().trim();
            if (!nome.isEmpty()) {
                exercicio.setNomeExercicio(nome);
            } else if (exercicio.getNomeExercicio() == null) {
                System.out.println("O nome do exercício é obrigatório.");
                return null;
            }

            // Grupo muscular
            exibirGruposMusculares();
            System.out.print("Grupo muscular" +
                    (exercicio.getGrupoMuscular() != null ? " [" + exercicio.getGrupoMuscular() + "]" : "") +
                    ": ");
            String grupoMuscular = scanner.nextLine().trim();
            if (!grupoMuscular.isEmpty()) {
                exercicio.setGrupoMuscular(grupoMuscular);
            } else if (exercicio.getGrupoMuscular() == null) {
                System.out.println("O grupo muscular é obrigatório.");
                return null;
            }

            // Descrição
            System.out.println("Descrição do exercício (deixe em branco para manter a atual):");
            System.out.println("(Digite várias linhas e termine com uma linha em branco)");
            if (exercicio.getDescricao() != null) {
                System.out.println("Descrição atual: " + exercicio.getDescricao());
            }
            
            StringBuilder descricao = new StringBuilder();
            String linha;
            while (!(linha = scanner.nextLine()).isEmpty()) {
                descricao.append(linha).append("\n");
            }
            
            if (descricao.length() > 0) {
                // Remove a última quebra de linha
                descricao.setLength(descricao.length() - 1);
                exercicio.setDescricao(descricao.toString());
            } else if (exercicioExistente == null) {
                // Para novos exercícios, exige a descrição
                System.out.println("A descrição é obrigatória para novos exercícios.");
                return null;
            }

            return exercicio;
        } catch (Exception e) {
            System.out.println("Erro ao coletar dados: " + e.getMessage());
            return null;
        }
    }

    /**
     * Exibe os grupos musculares disponíveis para seleção
     */
    private void exibirGruposMusculares() {
        System.out.println("Grupos musculares disponíveis:");
        for (int i = 0; i < GRUPOS_MUSCULARES.length; i++) {
            System.out.print(GRUPOS_MUSCULARES[i]);
            // Adiciona vírgula se não for o último item
            if (i < GRUPOS_MUSCULARES.length - 1) {
                System.out.print(", ");
            }
            // Quebra a linha a cada 4 grupos
            if ((i + 1) % 4 == 0) {
                System.out.println();
            }
        }
        System.out.println("\n(Você pode digitar um grupo diferente dos listados acima)");
    }

    /**
     * Exibe os detalhes de um exercício
     *
     * @param exercicio O exercício a ser exibido
     */
    public void exibirDadosExercicio(Exercicio exercicio) {
        if (exercicio == null) {
            System.out.println("Exercício não encontrado.");
            return;
        }

        System.out.println("\n------ DADOS DO EXERCÍCIO ------");
        System.out.println("ID: " + exercicio.getIdExercicio());
        System.out.println("Nome: " + exercicio.getNomeExercicio());
        System.out.println("Grupo Muscular: " + exercicio.getGrupoMuscular());
        System.out.println("\nDescrição:");
        System.out.println(exercicio.getDescricao());
        System.out.println("-------------------------------");
    }

    /**
     * Exibe uma lista de exercícios
     *
     * @param exercicios Lista de exercícios a serem exibidos
     */
    public void exibirListaExercicios(List<Exercicio> exercicios) {
        if (exercicios == null || exercicios.isEmpty()) {
            System.out.println("Não há exercícios cadastrados.");
            return;
        }
    
        System.out.println("\n===== LISTA DE EXERCÍCIOS =====");
        
        for (Exercicio exercicio : exercicios) {
            System.out.println("---------------------------");
            System.out.println("ID: " + exercicio.getIdExercicio());
            System.out.println("Nome: " + exercicio.getNomeExercicio());
            System.out.println("Grupo Muscular: " + exercicio.getGrupoMuscular());
            
            // Exibe apenas as primeiras linhas da descrição se for muito longa
            String descricao = exercicio.getDescricao();
            if (descricao != null && !descricao.isEmpty()) {
                String resumo;
                if (descricao.length() > 100) {
                    resumo = descricao.substring(0, 97) + "...";
                } else {
                    resumo = descricao;
                }
                System.out.println("Descrição: " + resumo);
            }
        }
        
        System.out.println("---------------------------");
        System.out.println("Total de exercícios: " + exercicios.size());
    }
    
    /**
     * Solicita o ID de um exercício
     *
     * @return O ID informado ou null em caso de erro
     */
    public Integer solicitarIdExercicio() {
        System.out.print("Digite o ID do exercício: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um ID válido.");
            return null;
        }
    }
    
    // Método solicitarGrupoMuscular removido

    /**
     * Solicita confirmação para exclusão
     *
     * @return true se confirmado, false caso contrário
     */
    public boolean confirmarExclusao() {
        System.out.print("Tem certeza que deseja excluir este exercício? (S/N): ");
        String confirmacao = scanner.nextLine().trim().toUpperCase();
        return confirmacao.equals("S");
    }

    /**
     * Exibe mensagem de sucesso para cadastro
     *
     * @param exercicio O exercício cadastrado
     */
    public void exibirMensagemCadastroSucesso(Exercicio exercicio) {
        System.out.println("Exercício '" + exercicio.getNomeExercicio() + "' cadastrado com sucesso! ID: " + exercicio.getIdExercicio());
    }

    /**
     * Exibe mensagem de sucesso para atualização
     */
    public void exibirMensagemAtualizacaoSucesso() {
        System.out.println("Exercício atualizado com sucesso!");
    }

    /**
     * Exibe mensagem de sucesso para exclusão
     */
    public void exibirMensagemExclusaoSucesso() {
        System.out.println("Exercício excluído com sucesso!");
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
