package com.uerj.gymflow.view;

import com.uerj.gymflow.controller.AlunoController;
import com.uerj.gymflow.controller.AvaliacaoController;
import com.uerj.gymflow.controller.ExercicioController;
import com.uerj.gymflow.controller.FuncionarioController;
import com.uerj.gymflow.controller.PessoaController;
import com.uerj.gymflow.controller.PlanoController;
import com.uerj.gymflow.controller.ProfessorController;
import com.uerj.gymflow.controller.TreinoController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Classe responsável pela exibição do menu principal da aplicação
 */
public class MenuPrincipalView {
    private static final Logger logger = LoggerFactory.getLogger(MenuPrincipalView.class);
    private final Scanner scanner;

    public MenuPrincipalView(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Exibe o menu principal e gerencia as opções selecionadas
     */
    public void exibirMenu() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n===== GYMFLOW - SISTEMA DE GESTÃO DE ACADEMIA =====");
            System.out.println("1. Gerenciar Pessoas");
            System.out.println("2. Gerenciar Funcionários");
            System.out.println("3. Gerenciar Professores");
            System.out.println("4. Gerenciar Planos");
            System.out.println("5. Gerenciar Alunos");
            System.out.println("6. Gerenciar Avaliações");
            System.out.println("7. Gerenciar Exercícios");
            System.out.println("8. Gerenciar Treinos");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());

                switch (opcao) {
                    case 1:
                        new PessoaController(scanner).mostrarMenu();
                        break;
                    case 2:
                        new FuncionarioController(scanner).mostrarMenu();
                        break;
                    case 3:
                        new ProfessorController(scanner).mostrarMenu();
                        break;
                    case 4:
                        new PlanoController(scanner).mostrarMenu();
                        break;
                    case 5:
                        new AlunoController(scanner).mostrarMenu();
                        break;
                    case 6:
                        new AvaliacaoController(scanner).mostrarMenu();
                        break;
                    case 7:
                        new ExercicioController(scanner).mostrarMenu();
                        break;
                    case 8:
                        new TreinoController(scanner).mostrarMenu();
                        break;
                    case 0:
                        System.out.println("Saindo do sistema...");
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido.");
                logger.error("Erro de formato de número no menu principal", e);
            } catch (Exception e) {
                System.out.println("Ocorreu um erro: " + e.getMessage());
                logger.error("Erro no menu principal", e);
            }
        }
    }
}

