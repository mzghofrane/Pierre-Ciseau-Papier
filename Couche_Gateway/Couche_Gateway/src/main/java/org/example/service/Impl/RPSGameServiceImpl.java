package org.example.service.Impl;

import org.example.service.RPSGameService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Random;

public class RPSGameServiceImpl extends UnicastRemoteObject implements RPSGameService {

    private int roundNumber;
    private String playerChoice;

    public RPSGameServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void initializeRPSGame(String playerName) throws RemoteException {
        System.out.println("Initialized RPS game for player " + playerName);
        startNewRound();
    }

    @Override
    public String playRound(String userChoice) throws RemoteException {
        if (roundNumber <= 3) {
            playerChoice = userChoice;

            // Generate server's choice
            String serverChoice = generateServerChoice();

            // Determine round result
            String roundResult = determineRoundResult(userChoice, serverChoice);

            // TODO: Save round information or perform any other actions

            roundNumber++;

            if (roundNumber > 3) {
                return "La partie est terminée. Votre choix final était : " + playerChoice
                        + ". Choix du serveur : " + serverChoice + ". Résultat du jeu : " + roundResult;
            } else {
                return "Round " + roundNumber + " terminé. Votre choix était : " + userChoice
                        + ". Choix du serveur : " + serverChoice + ". Résultat du round : " + roundResult;
            }
        } else {
            return "La partie est terminée. Démarrez une nouvelle partie.";
        }
    }

    @Override
    public String getGameHistory(String playerName) throws RemoteException {
        // TODO: Implement logic to retrieve and return the game history for the player
        return "TODO: Game history for player " + playerName;
    }

    private void startNewRound() {
        roundNumber = 1;
        playerChoice = null;
    }

    private String generateServerChoice() {
        String[] choices = {"Pierre", "Papier", "Ciseaux"};
        return choices[new Random().nextInt(choices.length)];
    }

    private String determineRoundResult(String userChoice, String serverChoice) {
        if (userChoice.equals(serverChoice)) {
            return "Égalité";
        }

        if ((userChoice.equals("Pierre") && serverChoice.equals("Ciseaux")) ||
                (userChoice.equals("Ciseaux") && serverChoice.equals("Papier")) ||
                (userChoice.equals("Papier") && serverChoice.equals("Pierre"))) {
            return "Vous avez gagné ce round !";
        } else {
            return "Le serveur a gagné ce round.";
        }
    }
}
