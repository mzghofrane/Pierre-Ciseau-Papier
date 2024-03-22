package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class GameGateway {
    public static void main(String[] args) {
        int portNumber = 5001;
        int clientNumber = 0;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientNumber++;
                System.out.println("Client " + clientNumber + " connected.");

                InputStream in = clientSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String serverChoice = reader.readLine();

                if (Objects.equals(serverChoice, "1")) {
                    handleRMI(clientSocket, clientNumber);
                } else {
                    handleRPC(clientSocket, clientNumber);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRMI(Socket clientSocket, int clientNumber) throws IOException {

        MThreadsRMI rmi = new MThreadsRMI(clientSocket, clientNumber);
        Thread tr = new Thread(rmi);
        tr.start();
        System.out.println("Started RMI thread for Client" + clientNumber);
    }

    private static void handleRPC(Socket clientSocket, int clientNumber) throws IOException {
        System.out.println("Started RPC thread for Client" + clientNumber);
    }
}
