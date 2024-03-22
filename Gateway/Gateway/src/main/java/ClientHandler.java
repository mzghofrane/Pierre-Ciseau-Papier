import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import service.RemoteInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.rmi.Naming;
import java.util.UUID;
public class ClientHandler implements Runnable {
    private RemoteInterface rmiRemoteObject;
    private XmlRpcClient xmlrpcRemoteObject;  // Make sure this declaration exists in your class

    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    private RemoteInterface initRmiRemoteObject() {
        try {
              
            return (RemoteInterface) Naming.lookup("rmi://localhost:5005/MyRemoteServer");
        } catch (Exception e) {
            throw new RuntimeException("Error initializing RMI RemoteInterface: " + e.getMessage(), e);
        }
    }



    private XmlRpcClient initXmlRpcRemoteObject() {
        try {
   
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://localhost:5005/xmlrpc")); // Adjust the URL based on your server's address and port

            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            return client;
        } catch (MalformedURLException e) {
            // Handle the MalformedURLException
            System.out.println("MalformedURLException during initializing XML-RPC client: " + e.getMessage());
            return null; // Or throw an exception, depending on your error handling strategy
        }
    }


    @Override
    public void run() {
        try (

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            
        ) {
            // Ask the client to send the communication method choice
           
            String communicationMethod = reader.readLine();
            System.out.println(communicationMethod);
            String clientUID = UUID.randomUUID().toString();
            if ("RMI".equalsIgnoreCase(communicationMethod)) {
                 this.rmiRemoteObject = initRmiRemoteObject();
                handleRmiCommunication(reader, writer, clientUID);
            } else if ("RPC".equalsIgnoreCase(communicationMethod)) {
                this.xmlrpcRemoteObject = initXmlRpcRemoteObject();
                handleRpcCommunication(reader, writer, clientUID);
            } else {
                writer.println("Invalid communication method. Exiting.");
            }
        } catch (IOException e) {
          
            System.out.println("IOException in client handler: " + e.getMessage());
        } catch (Exception e) {
          
            System.out.println("Exception in client handler: " + e.getMessage());
        } finally {
            try {
                // Close the client socket when the client disconnects
                clientSocket.close();
                System.out.println("Client disconnected: " + clientSocket);
            } catch (IOException e) {
                // Handle IOException during socket close, e.g., log it
                System.out.println("IOException while closing client socket: " + e.getMessage());
            }
        }
    }

    private void handleRmiCommunication(BufferedReader reader, PrintWriter writer , String clientUID) {
        while (true) {
          
            String clientChoice = readInput(reader);
            System.out.println("playing ");
            if ("Play".equalsIgnoreCase(clientChoice)) {
                playGame(reader, writer , clientUID);
            } else if ("View Game History".equalsIgnoreCase(clientChoice)) {
                viewHistory(writer, clientUID);
            } else if ("Exit".equalsIgnoreCase(clientChoice)) {
                 clearUserGameHistory(clientUID);
                break;
            } else {
                writer.println("Invalid choice. Try again.");
            }
        }
    }

    private void handleRpcCommunication(BufferedReader reader, PrintWriter writer, String clientUID) {
        while (true) {
            // writer.println("Send action choice: Play, ViewHistory, Exit");
            String clientChoice = readInput(reader);
            System.out.println("playing ");

            if ("Play".equalsIgnoreCase(clientChoice)) {
                System.out.println("Playing game");
                playGameRPC(reader, writer, clientUID);
            } else if ("View Game History".equalsIgnoreCase(clientChoice)) {
                viewHistoryRPC(writer, clientUID);
            } else if ("Exit".equalsIgnoreCase(clientChoice)) {
                clearUserGameHistoryRPC(clientUID);
                break;
            } else {
                writer.println("Invalid choice. Try again.");
            }
        }
    }

    private void playGame(BufferedReader reader, PrintWriter writer, String clientUID) {
        try {
            //writer.println("Enter your choice: Pierre, Papier, Ciseaux");
            String playerChoice = readInput(reader);
            String response = rmiRemoteObject.playRound(clientUID, playerChoice);
            writer.println(response);
        } catch (IOException e) {
            System.out.println("IOException during playGame: " + e.getMessage());
        }
    }
    

    private void viewHistory(PrintWriter writer , String clientUID) {
        try {
            String response = rmiRemoteObject.getUserGameHistory(clientUID);
            writer.println(response);
        } catch (IOException e) {
            System.out.println("IOException during viewHistory: " + e.getMessage());
        }
    }private void clearUserGameHistory(String clientUID) {
        try {
           rmiRemoteObject.clearUserGameHistory(clientUID);
           System.out.println("exit");
          
        } catch (IOException e) {
            System.out.println("IOException during viewHistory: " + e.getMessage());
        }
    }
    private void playGameRPC(BufferedReader reader, PrintWriter writer, String clientUID) {
        try {
            System.out.println("Playing game RPC");
            if (xmlrpcRemoteObject == null) {
                // Handle the situation when there is a problem connecting to the XML-RPC server
                writer.println("Error connecting to XML-RPC server. Exiting.");
                return;
            }

            Object[] params = new Object[]{clientUID, readInput(reader)};
            String response = (String) xmlrpcRemoteObject.execute("gameService.playRound", params);
            writer.println(response);
        } catch (XmlRpcException e) {
            System.out.println("Exception during playGame: " + e.getMessage());
        }
    }
    
    private void viewHistoryRPC(PrintWriter writer, String clientUID) {
        try {
            
            Object[] params = new Object[]{clientUID};
            String response = (String)  xmlrpcRemoteObject.execute("gameService.getUserGameHistory", params);
            writer.println(response);
        } catch ( XmlRpcException e) {
            // Handle exceptions, e.g., log them
            System.out.println("Exception during viewHistory: " + e.getMessage());
        }
    }

    private void clearUserGameHistoryRPC(String clientUID) {
        try {
            Object[] params = new Object[]{clientUID};
             xmlrpcRemoteObject.execute("gameService.clearUserGameHistory", params);
            System.out.println("exit");
        } catch (XmlRpcException e) {
            // Handle exceptions, e.g., log them
            System.out.println("Exception during clearUserGameHistory: " + e.getMessage());
        }
    }

 
    private String readInput(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {

            System.out.println("IOException during readInput: " + e.getMessage());
            return ""; 
        }
    }

    private void closeClientSocket() {
        try {
            // Close the client socket when the client disconnects
            clientSocket.close();
            System.out.println("Client disconnected: " + clientSocket);
        } catch (IOException e) {
            handleException("IOException while closing client socket: " + e.getMessage(), e);
        }
    }

    private void handleException(String message, Exception e) {
        // Handle exceptions, e.g., log it
        System.out.println(message);
        e.printStackTrace();
    }
}
