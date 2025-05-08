package com.uerj.gymflow.view;

import com.uerj.gymflow.model.Exercicio;
import com.uerj.gymflow.model.Professor;
import com.uerj.gymflow.model.Treino;
import com.uerj.gymflow.model.TreinoExercicio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * View para a interação com o usuário relacionada a Treinos
 */
public class TreinoView {
    private final Scanner scanner;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TreinoView(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Exibe o menu de opções relacionadas a treinos
     * @return A opção escolhida pelo usuário
     */
    public int exibirMenu() {
        System.out.println("\n===== GERENCIAMENTO DE TREINOS =====");
        System.out.println("1. Cadastrar Novo Treino");
        System.out.println("2. Listar Todos os Treinos");
        System.out.println("3. Buscar Treino por ID");
        System.out.println("4. Buscar Treinos por Professor");
        System.out.println("5. Atualizar Treino");
        System.out.println("6. Gerenciar Exercícios do Treino");
        System.out.println("7. Excluir Treino");
        System.out.println("0. Voltar ao Menu Principal");
        System.out.print("Escolha uma opção: ");

        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida. Por favor, digite um número.");
            return -1;
        }
    }
    
    /**
     * Exibe o submenu para gerenciar exercícios do treino
     * @return A opção escolhida pelo usuário
     */
    public int exibirMenuExercicios() {
        System.out.println("\n===== GERENCIAMENTO DE EXERCÍCIOS DO TREINO =====");
        System.out.println("1. Adicionar Exercício ao Treino");
        System.out.println("2. Listar Exercícios do Treino");
        System.out.println("3. Atualizar Detalhes do Exercício no Treino");
        System.out.println("4. Remover Exercício do Treino");
        System.out.println("0. Voltar ao Menu de Treinos");
        System.out.print("Escolha uma opção: ");

        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida. Por favor, digite um número.");
            return -1;
        }
    }

    /**
     * Coleta dados para criar um novo treino
     * @param professorId ID do professor (opcional, pode ser null)
     * @return Objeto Treino preenchido ou null se cancelado
     */
    public Treino coletarDadosTreino(Integer professorId) {
        Treino treino = new Treino();
        
        System.out.println("\n===== CADASTRO DE TREINO =====");
        System.out.println("Digite 'cancelar' a qualquer momento para cancelar a operação");
        
        // Nome do Treino
        System.out.print("Nome do Treino: ");
        String nomeTreino = scanner.nextLine();
        
        if (nomeTreino.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        if (nomeTreino.isEmpty()) {
            System.out.println("Nome do treino não pode ser vazio.");
            return null;
        }
        
        treino.setNomeTreino(nomeTreino);
        
        // Data de criação (padrão é a data atual)
        treino.setDataCriacao(LocalDate.now());
        
        // Observações
        System.out.print("Observações (opcional): ");
        String observacoes = scanner.nextLine();
        
        if (observacoes.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        treino.setObservacoes(observacoes);
        
        // ID do Professor
        if (professorId == null) {
            System.out.print("ID do Professor (ou 0 para nenhum): ");
            String idProfessorStr = scanner.nextLine();
            
            if (idProfessorStr.equalsIgnoreCase("cancelar")) {
                System.out.println("Operação cancelada pelo usuário.");
                return null;
            }
            
            try {
                int idProfessor = Integer.parseInt(idProfessorStr);
                if (idProfessor > 0) {
                    treino.setIdProfessor(idProfessor);
                }
            } catch (NumberFormatException e) {
                System.out.println("ID do professor inválido. Treino será criado sem associação a professor.");
            }
        } else if (professorId > 0) {
            treino.setIdProfessor(professorId);
        }
        
        return treino;
    }
    
    /**
     * Coleta dados para atualizar um treino existente
     * @param treinoExistente Treino a ser atualizado
     * @return Treino com dados atualizados ou null se cancelado
     */
    public Treino coletarDadosAtualizacaoTreino(Treino treinoExistente) {
        if (treinoExistente == null) {
            System.out.println("Treino não encontrado.");
            return null;
        }
        
        // Exibe dados atuais do treino
        exibirDadosTreino(treinoExistente);
        
        Treino treinoAtualizado = new Treino();
        treinoAtualizado.setIdTreino(treinoExistente.getIdTreino());
        
        System.out.println("\n===== ATUALIZAÇÃO DE TREINO =====");
        System.out.println("Deixe em branco para manter o valor atual. Digite 'cancelar' para cancelar a operação.");
        
        // Nome do Treino
        System.out.print("Nome do Treino [" + treinoExistente.getNomeTreino() + "]: ");
        String nomeTreino = scanner.nextLine();
        
        if (nomeTreino.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        treinoAtualizado.setNomeTreino(nomeTreino.isEmpty() ? treinoExistente.getNomeTreino() : nomeTreino);
        
        // Data de criação
        System.out.print("Data de Criação [" + treinoExistente.getDataCriacao().format(formatter) + "] (dd/MM/yyyy): ");
        String dataCriacaoStr = scanner.nextLine();
        
        if (dataCriacaoStr.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        if (dataCriacaoStr.isEmpty()) {
            treinoAtualizado.setDataCriacao(treinoExistente.getDataCriacao());
        } else {
            try {
                LocalDate dataCriacao = LocalDate.parse(dataCriacaoStr, formatter);
                treinoAtualizado.setDataCriacao(dataCriacao);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Mantendo a data original.");
                treinoAtualizado.setDataCriacao(treinoExistente.getDataCriacao());
            }
        }
        
        // Observações
        System.out.print("Observações [" + treinoExistente.getObservacoes() + "]: ");
        String observacoes = scanner.nextLine();
        
        if (observacoes.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        treinoAtualizado.setObservacoes(observacoes.isEmpty() ? treinoExistente.getObservacoes() : observacoes);
        
        // ID do Professor
        String valorAtual = treinoExistente.getIdProfessor() != null ? treinoExistente.getIdProfessor().toString() : "nenhum";
        System.out.print("ID do Professor [" + valorAtual + "] (ou 0 para nenhum): ");
        String idProfessorStr = scanner.nextLine();
        
        if (idProfessorStr.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        if (idProfessorStr.isEmpty()) {
            treinoAtualizado.setIdProfessor(treinoExistente.getIdProfessor());
        } else {
            try {
                int idProfessor = Integer.parseInt(idProfessorStr);
                if (idProfessor > 0) {
                    treinoAtualizado.setIdProfessor(idProfessor);
                } else {
                    treinoAtualizado.setIdProfessor(null);
                }
            } catch (NumberFormatException e) {
                System.out.println("ID do professor inválido. Mantendo o valor original.");
                treinoAtualizado.setIdProfessor(treinoExistente.getIdProfessor());
            }
        }
        
        // Preservar os exercícios do treino existente
        treinoAtualizado.setExercicios(treinoExistente.getExercicios());
        
        return treinoAtualizado;
    }
    
    /**
     * Exibe detalhes de um treino
     * @param treino Treino a ser exibido
     */
    public void exibirDadosTreino(Treino treino) {
        if (treino == null) {
            System.out.println("Nenhum treino para exibir.");
            return;
        }
        
        System.out.println("\n===== DETALHES DO TREINO =====");
        System.out.println("ID: " + treino.getIdTreino());
        System.out.println("Nome: " + treino.getNomeTreino());
        System.out.println("Data de Criação: " + treino.getDataCriacao().format(formatter));
        System.out.println("Observações: " + (treino.getObservacoes() != null ? treino.getObservacoes() : ""));
        
        if (treino.getProfessor() != null) {
            System.out.println("Professor: " + treino.getProfessor().getNome());
        } else if (treino.getIdProfessor() != null) {
            System.out.println("ID do Professor: " + treino.getIdProfessor());
        } else {
            System.out.println("Professor: Não atribuído");
        }
        
        // Exibir quantidade de exercícios
        int qtdExercicios = treino.getExercicios() != null ? treino.getExercicios().size() : 0;
        System.out.println("Quantidade de Exercícios: " + qtdExercicios);
        
        if (qtdExercicios > 0) {
            System.out.println("\nExercícios:");
            for (Exercicio exercicio : treino.getExercicios()) {
                System.out.println("- " + exercicio.getNomeExercicio() + " (" + exercicio.getGrupoMuscular() + ")");
            }
        }
    }
    
    /**
     * Exibe uma lista de treinos
     * @param treinos Lista de treinos a ser exibida
     */
    public void exibirListaTreinos(List<Treino> treinos) {
        if (treinos == null || treinos.isEmpty()) {
            System.out.println("Nenhum treino encontrado.");
            return;
        }
        
        System.out.println("\n===== LISTA DE TREINOS =====");
        System.out.printf("%-5s %-30s %-12s %-20s %s\n", "ID", "Nome", "Data", "Professor", "Exercícios");
        System.out.println("-".repeat(85));
        
        for (Treino treino : treinos) {
            String nomeProfessor = "Não atribuído";
            if (treino.getProfessor() != null) {
                nomeProfessor = treino.getProfessor().getNome();
            }
            
            int qtdExercicios = treino.getExercicios() != null ? treino.getExercicios().size() : 0;
            
            System.out.printf("%-5d %-30s %-12s %-20s %d\n", 
                    treino.getIdTreino(),
                    treino.getNomeTreino(),
                    treino.getDataCriacao().format(formatter),
                    nomeProfessor,
                    qtdExercicios);
        }
    }
    
    /**
     * Solicita o ID de um treino
     * @return ID do treino ou null se cancelado/inválido
     */
    public Integer solicitarIdTreino() {
        System.out.print("\nDigite o ID do Treino (ou 'cancelar'): ");
        String entrada = scanner.nextLine();
        
        if (entrada.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        try {
            Integer id = Integer.parseInt(entrada);
            if (id <= 0) {
                System.out.println("ID inválido. Deve ser um número positivo.");
                return null;
            }
            return id;
        } catch (NumberFormatException e) {
            System.out.println("ID inválido. Deve ser um número.");
            return null;
        }
    }
    
    /**
     * Solicita o ID de um professor
     * @return ID do professor ou null se cancelado/inválido
     */
    public Integer solicitarIdProfessor() {
        System.out.print("\nDigite o ID do Professor (ou 'cancelar'): ");
        String entrada = scanner.nextLine();
        
        if (entrada.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        try {
            Integer id = Integer.parseInt(entrada);
            if (id <= 0) {
                System.out.println("ID inválido. Deve ser um número positivo.");
                return null;
            }
            return id;
        } catch (NumberFormatException e) {
            System.out.println("ID inválido. Deve ser um número.");
            return null;
        }
    }
    
    /**
     * Confirma a exclusão de um treino
     * @param treino Treino a ser excluído
     * @return true se confirmado, false caso contrário
     */
    public boolean confirmarExclusao(Treino treino) {
        if (treino == null) {
            return false;
        }
        
        exibirDadosTreino(treino);
        
        System.out.print("\nTem certeza que deseja excluir este treino? (S/N): ");
        String resposta = scanner.nextLine();
        
        return resposta.equalsIgnoreCase("S");
    }
    
    /**
     * Solicita uma lista de IDs de exercícios para adicionar ao treino
     * @param exerciciosDisponiveis Lista de exercícios disponíveis
     * @param exerciciosAtuais Lista de exercícios já adicionados ao treino
     * @return Lista de exercícios selecionados ou null se cancelado
     */
    public List<Exercicio> selecionarExercicios(List<Exercicio> exerciciosDisponiveis, List<Exercicio> exerciciosAtuais) {
        if (exerciciosDisponiveis == null || exerciciosDisponiveis.isEmpty()) {
            System.out.println("Não há exercícios disponíveis para seleção.");
            return null;
        }
        
        // Criar uma lista com exercícios que ainda não estão no treino
        List<Exercicio> exerciciosNaoSelecionados = new ArrayList<>();
        
        for (Exercicio exercicio : exerciciosDisponiveis) {
            boolean jaAdicionado = false;
            
            if (exerciciosAtuais != null) {
                for (Exercicio exercicioAtual : exerciciosAtuais) {
                    if (exercicio.getIdExercicio().equals(exercicioAtual.getIdExercicio())) {
                        jaAdicionado = true;
                        break;
                    }
                }
            }
            
            if (!jaAdicionado) {
                exerciciosNaoSelecionados.add(exercicio);
            }
        }
        
        if (exerciciosNaoSelecionados.isEmpty()) {
            System.out.println("Todos os exercícios já foram adicionados a este treino.");
            return null;
        }
        
        System.out.println("\n===== EXERCÍCIOS DISPONÍVEIS =====");
        System.out.printf("%-5s %-30s %-15s %s\n", "ID", "Nome", "Grupo Muscular", "Descrição");
        System.out.println("-".repeat(90));
        
        for (Exercicio exercicio : exerciciosNaoSelecionados) {
            System.out.printf("%-5d %-30s %-15s %s\n", 
                    exercicio.getIdExercicio(),
                    exercicio.getNomeExercicio(),
                    exercicio.getGrupoMuscular(),
                    (exercicio.getDescricao() != null && exercicio.getDescricao().length() > 30) ? 
                            exercicio.getDescricao().substring(0, 27) + "..." : 
                            exercicio.getDescricao());
        }
        
        List<Exercicio> exerciciosSelecionados = new ArrayList<>();
        
        System.out.println("\nDigite os IDs dos exercícios separados por vírgula (ou 'cancelar'): ");
        String entrada = scanner.nextLine();
        
        if (entrada.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        String[] ids = entrada.split(",");
        
        for (String idStr : ids) {
            try {
                int id = Integer.parseInt(idStr.trim());
                
                // Buscar o exercício correspondente
                for (Exercicio exercicio : exerciciosNaoSelecionados) {
                    if (exercicio.getIdExercicio() == id) {
                        exerciciosSelecionados.add(exercicio);
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("ID inválido ignorado: " + idStr);
            }
        }
        
        if (exerciciosSelecionados.isEmpty()) {
            System.out.println("Nenhum exercício válido selecionado.");
            return null;
        }
        
        return exerciciosSelecionados;
    }
    
    /**
     * Exibe os detalhes dos exercícios de um treino
     * @param detalhes Lista de detalhes de exercícios no treino
     */
    public void exibirDetalhesExercicios(List<TreinoExercicio> detalhes) {
        if (detalhes == null || detalhes.isEmpty()) {
            System.out.println("Treino não possui exercícios.");
            return;
        }
        
        System.out.println("\n===== EXERCÍCIOS DO TREINO =====");
        System.out.printf("%-5s %-30s %-15s %-10s %-10s %-10s %s\n",
                "ID", "Nome", "Grupo Muscular", "Séries", "Repetições", "Carga(kg)", "Observações");
        System.out.println("-".repeat(100));
        
        for (TreinoExercicio detalhe : detalhes) {
            Exercicio ex = detalhe.getExercicio();
            String obs = detalhe.getObservacoes();
            if (obs != null && obs.length() > 20) {
                obs = obs.substring(0, 17) + "...";
            }
            
            System.out.printf("%-5d %-30s %-15s %-10s %-10s %-10s %s\n",
                    ex.getIdExercicio(),
                    ex.getNomeExercicio(),
                    ex.getGrupoMuscular(),
                    detalhe.getSeries() != null ? detalhe.getSeries() : "-",
                    detalhe.getRepeticoes() != null ? detalhe.getRepeticoes() : "-",
                    detalhe.getCarga() != null ? detalhe.getCarga() : "-",
                    obs != null ? obs : "");
        }
    }
    
    /**
     * Solicita o ID de um exercício do treino
     * @param detalhes Lista de detalhes de exercícios do treino
     * @return ID do exercício ou null se cancelado
     */
    public Integer selecionarExercicioDoTreino(List<TreinoExercicio> detalhes) {
        if (detalhes == null || detalhes.isEmpty()) {
            System.out.println("Treino não possui exercícios.");
            return null;
        }
        
        exibirDetalhesExercicios(detalhes);
        
        System.out.print("\nDigite o ID do exercício (ou 'cancelar'): ");
        String entrada = scanner.nextLine();
        
        if (entrada.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        try {
            Integer id = Integer.parseInt(entrada);
            
            // Verificar se o ID existe no treino
            boolean encontrado = false;
            for (TreinoExercicio detalhe : detalhes) {
                if (detalhe.getIdExercicio().equals(id)) {
                    encontrado = true;
                    break;
                }
            }
            
            if (!encontrado) {
                System.out.println("Exercício não encontrado no treino.");
                return null;
            }
            
            return id;
        } catch (NumberFormatException e) {
            System.out.println("ID inválido. Deve ser um número.");
            return null;
        }
    }
    
    /**
     * Coleta detalhes para atualizar um exercício no treino
     * @param detalhesAtuais Detalhes atuais do exercício no treino
     * @return Objeto com os novos detalhes ou null se cancelado
     */
    public TreinoExercicio coletarDetalhesExercicio(TreinoExercicio detalhesAtuais) {
        if (detalhesAtuais == null) {
            System.out.println("Detalhes do exercício não encontrados.");
            return null;
        }
        
        Exercicio exercicio = detalhesAtuais.getExercicio();
        
        System.out.println("\n===== ATUALIZAR DETALHES DO EXERCÍCIO =====");
        System.out.println("Exercício: " + exercicio.getNomeExercicio() + " (" + exercicio.getGrupoMuscular() + ")");
        System.out.println("Deixe em branco para manter o valor atual. Digite 'cancelar' para cancelar a operação.");
        
        TreinoExercicio novoDetalhe = new TreinoExercicio();
        novoDetalhe.setIdTreino(detalhesAtuais.getIdTreino());
        novoDetalhe.setIdExercicio(detalhesAtuais.getIdExercicio());
        
        // Séries
        System.out.print("Séries [" + (detalhesAtuais.getSeries() != null ? detalhesAtuais.getSeries() : "-") + "]: ");
        String seriesStr = scanner.nextLine();
        
        if (seriesStr.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        if (seriesStr.isEmpty()) {
            novoDetalhe.setSeries(detalhesAtuais.getSeries());
        } else {
            try {
                novoDetalhe.setSeries(Integer.parseInt(seriesStr));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Mantendo o valor atual.");
                novoDetalhe.setSeries(detalhesAtuais.getSeries());
            }
        }
        
        // Repetições
        System.out.print("Repetições [" + (detalhesAtuais.getRepeticoes() != null ? detalhesAtuais.getRepeticoes() : "-") + "]: ");
        String repeticoesStr = scanner.nextLine();
        
        if (repeticoesStr.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        if (repeticoesStr.isEmpty()) {
            novoDetalhe.setRepeticoes(detalhesAtuais.getRepeticoes());
        } else {
            try {
                novoDetalhe.setRepeticoes(Integer.parseInt(repeticoesStr));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Mantendo o valor atual.");
                novoDetalhe.setRepeticoes(detalhesAtuais.getRepeticoes());
            }
        }
        
        // Carga
        System.out.print("Carga em kg [" + (detalhesAtuais.getCarga() != null ? detalhesAtuais.getCarga() : "-") + "]: ");
        String cargaStr = scanner.nextLine();
        
        if (cargaStr.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        if (cargaStr.isEmpty()) {
            novoDetalhe.setCarga(detalhesAtuais.getCarga());
        } else {
            try {
                novoDetalhe.setCarga(Float.parseFloat(cargaStr));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Mantendo o valor atual.");
                novoDetalhe.setCarga(detalhesAtuais.getCarga());
            }
        }
        
        // Ordem
        novoDetalhe.setOrdem(detalhesAtuais.getOrdem());
        
        // Observações
        System.out.print("Observações [" + (detalhesAtuais.getObservacoes() != null ? detalhesAtuais.getObservacoes() : "") + "]: ");
        String observacoes = scanner.nextLine();
        
        if (observacoes.equalsIgnoreCase("cancelar")) {
            System.out.println("Operação cancelada pelo usuário.");
            return null;
        }
        
        novoDetalhe.setObservacoes(observacoes.isEmpty() ? detalhesAtuais.getObservacoes() : observacoes);
        
        return novoDetalhe;
    }
    
    /**
     * Confirma a remoção de um exercício do treino
     * @param exercicio Exercício a ser removido
     * @return true se confirmado, false caso contrário
     */
    public boolean confirmarRemocaoExercicio(Exercicio exercicio) {
        if (exercicio == null) {
            return false;
        }
        
        System.out.println("\nExercício: " + exercicio.getNomeExercicio() + " (" + exercicio.getGrupoMuscular() + ")");
        System.out.print("Tem certeza que deseja remover este exercício do treino? (S/N): ");
        String resposta = scanner.nextLine();
        
        return resposta.equalsIgnoreCase("S");
    }
    
    /**
     * Exibe mensagem de erro
     * @param mensagem Mensagem de erro
     */
    public void exibirMensagemErro(String mensagem) {
        System.out.println("\nERRO: " + mensagem);
    }
    
    /**
     * Exibe mensagem de sucesso
     * @param mensagem Mensagem de sucesso
     */
    public void exibirMensagemSucesso(String mensagem) {
        System.out.println("\nSUCESSO: " + mensagem);
    }
    
    /**
     * Exibe mensagem quando nenhum resultado for encontrado
     * @param mensagem Mensagem informativa
     */
    public void exibirMensagemNaoEncontrado(String mensagem) {
        System.out.println("\nINFO: " + mensagem);
    }
    
    /**
     * Exibe mensagem de operação cancelada
     */
    public void exibirMensagemOperacaoCancelada() {
        System.out.println("\nOperação cancelada pelo usuário.");
    }
}
