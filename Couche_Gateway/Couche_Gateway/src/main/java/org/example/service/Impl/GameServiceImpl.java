package org.example.service.Impl;

import org.example.service.GameService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameServiceImpl extends UnicastRemoteObject implements GameService {

    private Map<String, Integer> playerScores;
    private Map<String, String[]> history;
    private Map<String, Integer> roundCounts;

    public GameServiceImpl() throws RemoteException {
        super();
        this.playerScores = new HashMap<>();
        this.history = new HashMap<>();
        this.roundCounts = new HashMap<>();
    }

    @Override
    public String play(String playerID, String playerChoice) throws RemoteException {
        if (!isRoundOver(playerID)) {

            String[] choices = {"Pierre", "Papier", "Ciseaux"};

            String serverChoice = choices[new Random().nextInt(choices.length)];

            String roundResult = determineTheWinner(playerChoice, serverChoice);

            updatePlayerScore(playerID, roundResult);

            saveGameHistory(playerID, playerChoice, serverChoice, roundResult);

            if (isRoundOver(playerID)) {
                startNewRound(playerID);
                int finalScore = getFinalScore(playerID);
                return "La partie est terminée. Score final du joueur " + playerID + ": " + finalScore;
            }

            return roundResult;
        } else {
            return "La partie est terminée. Démarrez une nouvelle partie.";
        }
    }

    @Override
    public int getFinalScore(String playerID) throws RemoteException {
        return playerScores.getOrDefault(playerID, 0);
    }

    @Override
    public String[] gameHistory(String playerID) throws RemoteException {
        return history.getOrDefault(playerID, new String[]{});
    }

    private void updatePlayerScore(String playerID, String roundResult) {

        int currentScore = playerScores.getOrDefault(playerID, 0);

        switch (roundResult) {
            case "Joueur gagne":
                currentScore += 1;
                break;
            case "Serveur gagne":
                currentScore -= 1;
                break;
        }

        playerScores.put(playerID, currentScore);
    }

    private void saveGameHistory(String playerID, String playerChoice, String serverChoice, String roundResult) {
        String[] roundInfo = {playerChoice, serverChoice, roundResult};
        history.put(playerID, roundInfo);
    }

    private boolean isRoundOver(String playerID) {
        return roundCounts.getOrDefault(playerID, 0) >= 3;
    }

    private void startNewRound(String playerID) {
        roundCounts.put(playerID, roundCounts.getOrDefault(playerID, 0) + 1);
    }

    public String determineTheWinner(String playerChoice, String serverChoice) throws RemoteException {
        if (playerChoice.equals(serverChoice)) {
            return "Égalité";
        }

        if (
                (playerChoice.equals("Pierre") && serverChoice.equals("Ciseaux")) ||
                        (playerChoice.equals("Ciseaux") && serverChoice.equals("Papier")) ||
                        (playerChoice.equals("Papier") && serverChoice.equals("Pierre"))
        ) {
            return "Joueur gagne";
        } else {
            return "Serveur gagne";
        }
    }
}
