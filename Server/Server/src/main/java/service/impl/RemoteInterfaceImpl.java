package service.impl;

import service.RemoteInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RemoteInterfaceImpl extends UnicastRemoteObject implements RemoteInterface {
    private Map<String, Map<String, String>> userGameHistoryMap = new HashMap<>();

    public RemoteInterfaceImpl() throws RemoteException {
        super();
    }

    @Override
    public String playRound(String userId, String playerChoice) throws RemoteException {
        String[] choices = {"Pierre", "Papier", "Ciseaux"};
        String serverChoice = choices[new Random().nextInt(choices.length)];
        System.out.println(playerChoice);
        // Simple game logic for demonstration
        String result = determineWinner(playerChoice, serverChoice);

        // Update game history for the user
        updateGameHistory(userId, playerChoice, serverChoice, result);

        System.out.println(getUserGameHistory(userId));
        return result;
    }

    @Override
    public String getUserGameHistory(String userId) throws RemoteException {
        Map<String, String> userHistory = userGameHistoryMap.getOrDefault(userId, new HashMap<>());
        StringBuilder history = new StringBuilder();
        for (Map.Entry<String, String> entry : userHistory.entrySet()) {
            history.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return history.toString();
    }

    @Override
    public void clearUserGameHistory(String userId) throws RemoteException {
        userGameHistoryMap.remove(userId);
    }

    private void updateGameHistory(String userId, String playerChoice, String serverChoice, String result) {
        // Get user history for the given user ID
        Map<String, String> userHistory = userGameHistoryMap.getOrDefault(userId, new HashMap<>());

        // Update the user history with the new round
        userHistory.put("Round " + userHistory.size(), "Player: " + playerChoice + ", Server: " + serverChoice + ", Result: " + result);

        // Update the user history in the map
        userGameHistoryMap.put(userId, userHistory);
    }

    private String determineWinner(String playerChoice, String serverChoice) {
        // Simple game logic for demonstration
        if (playerChoice.equals(serverChoice)) {
            return "Tie";
        } else if (
                (playerChoice.equals("Pierre") && serverChoice.equals("Ciseaux")) ||
                        (playerChoice.equals("Papier") && serverChoice.equals("Pierre")) ||
                        (playerChoice.equals("Ciseaux") && serverChoice.equals("Papier"))
        ) {
            return "Player wins";
        } else {
            return "Server wins";
        }
    }
}
