package cliente.Client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;

public class Client implements ObservableLobby, ObservableGame {

    // Instancia de singleton
    private static Client instance = null;
    private static ArrayList<ObserverLobby> lst1 = new ArrayList<ObserverLobby>();
    private static ArrayList<ObserverGame> lst2 = new ArrayList<ObserverGame>();

    private Socket socket;
    private PrintStream output;
    private Scanner input;

    private static boolean hasNickname = false;

    public static Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    public void startClient() {
        // Se conecta con un servidor pasando por el ip
        try {
            socket = new Socket("localhost", 12345);
        } catch (IOException e) {
            System.out.println("no fue posible conectarse a la red");
            return;
        }
        input = new Scanner(System.in);

        // Se conecto corretamente
        if (socket.isConnected()) {

            threadMessages(socket);

            try {
                output = new PrintStream(socket.getOutputStream());
            } catch (IOException e) {
                System.out.println("no fue posible escribir el socket");
                return;
            }

        }
    }

    private static void threadMessages(Socket server) {

        // Variable para abrir el servidor
        Scanner scanner;
        try {
            scanner = new Scanner(server.getInputStream());
        } catch (IOException e2) {
            System.out.println("impossible abrir servidor");
            e2.printStackTrace();
            return;
        }

        // Se conecto, una thread para abri el servidor
        Thread messageThread = new Thread() {

            @Override
            public void run() {

                try {
                    while (scanner.hasNextLine()) {

                        String msg = scanner.nextLine();

                        ListIterator<ObserverLobby> li = lst1.listIterator();

                        if (msg.compareTo("Valido nombre") == 0) {

                            // si el servidor responde "Valid Nickname"
                            hasNickname = true;
                            System.out.println("Nombre configurado correctamente");

                            // aciona o observer
                            while (li.hasNext()) {

                                li.next().receivedNicknameAvaiable();
                            }

                            // Se já houver um nome como o que o cliente enviou espera outro resposta do servidor para um novo nome
                        } else if (msg.compareTo("Invalido nombre") == 0) {
                            System.out.println("Nombre ya existente, digite atro:");

                            while (li.hasNext()) {

                                li.next().receivedNicknameUnavaiable();
                            }

                        } else if (msg.contains("Game Start")) {

                            while (li.hasNext()) {

                                li.next().receivedGameStart();
                            }

                        } else {
                            System.out.println("Recibe una jugada!");
                            ListIterator<ObserverGame> listIt2 = lst2.listIterator();

                            while (listIt2.hasNext()) {
                                listIt2.next().receivedPlay(msg);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error al abrir servidor, desconectando");
                    e.printStackTrace();
                    try {
                        server.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };

        messageThread.start();
    }

    public void sendMessage(String message) {

        try {
            output = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("No fue posible escribir en el socket");
            return;
        }

        output.println(message);
    }

    public void closeClient() {

        System.out.println("O cliente termino de ejecutar!");

        output.close();
        input.close();
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Imposíble mandar socket");
        }
    }

    @Override
    public void addObserver(ObserverLobby o) {

        lst1.add(o);
    }

    @Override
    public void removeObserver(ObserverLobby o) {
    }

    @Override
    public void notifyObserver(ObserverLobby o) {
    }

    @Override
    public void addObserver(ObserverGame o) {

        lst2.add(o);
    }

    @Override
    public void removeObserver(ObserverGame o) {
    }

    @Override
    public void notifyObserver(ObserverGame o) {
    }
}
