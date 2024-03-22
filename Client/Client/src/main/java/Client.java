import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client {

    public static void main(String[] args) {
        try (
            Socket socket = new Socket("localhost", 5001); // Replace with your server's IP and port
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Choose communication method
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Choose communication method: RMI or RPC?");
            String communicationMethod = userInputReader.readLine();
            writer.println(communicationMethod);

            // Perform actions based on the chosen method
            if ("RMI".equalsIgnoreCase(communicationMethod)) {
                handleRmiCommunication(userInputReader, writer , reader);
            } else if ("RPC".equalsIgnoreCase(communicationMethod)) {
                handleRpcCommunication(userInputReader, writer ,reader);
            } else {
                System.out.println("Invalid communication method. Exiting.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRmiCommunication(BufferedReader userInputReader, PrintWriter writer , BufferedReader reader) throws IOException {
        while (true) {
            System.out.println("Choose an option: Play, View Game History, Exit");
            String clientChoice = userInputReader.readLine();
            writer.println(clientChoice);
            if("Play".equalsIgnoreCase(clientChoice)){
                 System.out.println("Enter your choice: Pierre, Papier, Ciseaux");
                 String clientChoice2 = userInputReader.readLine();
                 writer.println(clientChoice2);
                  
            }
            //ena 3amlitha bech nchouf history
            // Read and print server response
            if("Play".equalsIgnoreCase(clientChoice)){
           String serverResponse = reader.readLine();
           System.out.println(serverResponse);
            }
            else{
           String serverResponse;
               while((serverResponse = reader.readLine()) != null){
                    System.out.println(serverResponse);
                }
            }
            // Break the loop if the user chooses to exit
            if ("Exit".equalsIgnoreCase(clientChoice)) {
                break;
            }
        }
    }

    private static void handleRpcCommunication(BufferedReader userInputReader, PrintWriter writer, BufferedReader reader) throws IOException {
        // Implement RPC communication logic here
          while (true) {
            System.out.println("Choose an option: Play, View Game History, Exit");
            String clientChoice = userInputReader.readLine();
            writer.println(clientChoice);
            if("Play".equalsIgnoreCase(clientChoice)){
                 System.out.println("Enter your choice: Pierre, Papier, Ciseaux");
                 String clientChoice2 = userInputReader.readLine();
                 writer.println(clientChoice2);

                  
            }
            //ena 3amlitha bech nchouf history
            // Read and print server response
            if("Play".equalsIgnoreCase(clientChoice)){
           String serverResponse = reader.readLine();
           System.out.println(serverResponse);
            }
            else{
           String serverResponse;
               while((serverResponse = reader.readLine()) != null && !serverResponse.isEmpty() ){
                    System.out.println(serverResponse);
                }
            }
            // Break the loop if the user chooses to exit
            if ("Exit".equalsIgnoreCase(clientChoice)) {
                break;
            }
        }
    }}

