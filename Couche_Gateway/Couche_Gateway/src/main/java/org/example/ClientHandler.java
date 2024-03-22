package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.Naming;
import org.example.service.GameService;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private GameService gameService;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            String serverChoice = in.readLine();

            if (serverChoice.equals("1")) { // RMI
                gameService = (GameService) Naming.lookup("rmi://127.0.0.1:5005/Game");
                out.println("Server choice received. You can now start the game.");

                while (true) {
                    String option = in.readLine();
                    if (option.equals("3")) {
                        break;
                    }

                    String playerID = in.readLine();
                    if (option.equals("1")) {
                        String playerChoice = in.readLine();
                        String result = gameService.play(playerID, playerChoice);
                        out.println(result);
                    } else if (option.equals("2")) {
                        String[] history = gameService.gameHistory(playerID);
                        out.println("Historique des parties : " + String.join(", ", history));
                    }
                }
            } else if (serverChoice.equals("2")) { // RPC
                // Ajoutez ici le code pour se connecter au serveur RPC
            }

            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}