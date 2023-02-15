import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Chat {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Socket s = null;
        boolean server = false;
        while (true) {
            System.out.println("Enter <port> in order to start the chat server "
                    + "or <host>:<port> in order to connect to a running server. "
                    + "Enter exit for exiting the chat.\n");
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                System.out.println("Exiting");
                return;
            }

            int colonIndex = input.indexOf(":");
            try {
                if (colonIndex != -1) {
                    s = new Socket(InetAddress.getByName(input.substring(0, colonIndex)),
                            Integer.parseInt(input.substring(colonIndex + 1)));
                } else {
                    ServerSocket serverSocket = new ServerSocket(Integer.parseInt(input));
                    System.out.println("Server is started, expecting connections!");
                    s = serverSocket.accept();
                    serverSocket.close();
                    server = true;
                }
                break;
            } catch (UnknownHostException e) {
                System.out.println("Host is unknown, please, try again!");
            } catch (NumberFormatException e) {
                System.out.println("Port is invalid, enter again!");
            } catch (ConnectException e) {
                System.out.println("Cannot connect, please, try again!");
            } catch (IOException e) {
                System.out.println("Error occurred, please, try again!");
            }
        }

        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter pw = new PrintWriter(s.getOutputStream(), true)) {
            boolean running = true;
            if (server) {
                System.out.println("Connection established! Now you can send a message!");
                System.out.print("> ");
                String input = scanner.next();
                if (input.equals("exit")) {
                    running = false;
                } else {
                    pw.println(input);
                }
            }
            while (running) {
                String in = br.readLine();
                if (in.equals("exit")) {
                    System.out.println("Exiting!");
                    break;
                }
                System.out.println(in);
                System.out.print("> ");
                String input = scanner.next();
                if (input.equals("exit")) {
                    running = false;
                    System.out.println("Exiting");
                } else {
                    pw.println(input);
                }

            }
        } catch (IOException e) {
            System.out.println("Something's wrong! Please, try again!");
        } finally {
            try {
                s.close();
            } catch (IOException e) {
                System.out.println("Command denied!");
            }
        }
    }
}
