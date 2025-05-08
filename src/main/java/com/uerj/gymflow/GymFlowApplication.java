package com.uerj.gymflow;

import com.uerj.gymflow.view.MenuPrincipalView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Classe principal da aplicação GymFlow
 */
public class GymFlowApplication {
    private static final Logger logger = LoggerFactory.getLogger(GymFlowApplication.class);
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        logger.info("Iniciando aplicação GymFlow");
        
        try {
            MenuPrincipalView menuPrincipal = new MenuPrincipalView(scanner);
            menuPrincipal.exibirMenu();
        } catch (Exception e) {
            logger.error("Erro não tratado na aplicação", e);
            System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
        } finally {
            scanner.close();
            logger.info("Aplicação GymFlow finalizada");
        }
    }
}
