package org.example;

import org.example.service.RPSGameService;

import java.io.*;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.Objects;

public class MThreadsRMI implements Runnable {
    private Socket socket;
    private int clientNumber;
    private String serverAddress = "localhost";
    private int rmiPortNumber = 5005; // Adjust the appropriate RMI port here

    public MThreadsRMI(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
    }

    public void run() {
        try (
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                PrintWriter writer = new PrintWriter(output, true)
        ) {
            writer.println("Connected to RMI Rock-Paper-Scissors game server!");
            // Connect to the RMI server
            RPSGameService stub = (RPSGameService) Naming.lookup("rmi://localhost:5005/Game");

            while (true) {
                String userOption = reader.readLine();
                switch (userOption) {
                    case "option1": {
                        // Game initialization
                        String playerName = reader.readLine();
                        stub.initializeRPSGame(playerName);

                        String clientMessage;

                        // Read data sent by the client
                        while (!Objects.equals(clientMessage = reader.readLine(), "exit")) {
                            String response = stub.playRound(clientMessage);
                            writer.println(response);
                        }
                        break;
                    }
                    case "option2": {
                        System.out.println("TODO: Display game history!!");
                        break;
                    }
                    case "option3": {
                        System.out.println("Player " + clientNumber + " disconnected.");
                        socket.close();
                        break;
                    }
                }
            }

        } catch (IOException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
