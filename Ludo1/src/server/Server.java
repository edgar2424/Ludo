package server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class Server {

    // instancia única
    private static Server instance = null;

    // vestíbulo actual incompleto
    private Lobby currentLobby;
    // variable que indica si el cliente debe continuar ejecutándose o no
    private boolean serverIsRunning = true;

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public void runServer() {
        // Intente abrir un servidor en el puerto 12345
        ServerSocket server;
        try {
            server = new ServerSocket(12345);
        } catch (IOException e) {
            System.out.println("No consigue abrir puerto 12345");
            e.printStackTrace();
            return;
        }
        System.out.println("puerto 12345 abierto!");

        // inicializando un lobby 
        currentLobby = new Lobby();

        // llamando al hilo del servidor para aceptar nuevos clientes
        threadAcceptClients(server);
    }

    private void threadAcceptClients(ServerSocket server) {

        Thread acceptClients = new Thread() {

            @Override
            public void run() {

                // Mientras el servidor se está ejecutando
                while (serverIsRunning) {

                    try {

                        //  aceptar cliente
                        Socket newClient = server.accept();

                        System.out.println("Nueva conexion del cliente " + newClient.getInetAddress().getHostAddress());

                        // Crear un hilo para escuchar al nuevo cliente
                        threadListenNewClient(newClient);

                    } catch (IOException e) {
                        System.out.println("El servidor no pudo aceptar nuevos cliente");
                        e.printStackTrace();
                    }
                }
            }
        };

        acceptClients.run();
    }

    private void threadListenNewClient(Socket newClient) {

        Thread listenClient = new Thread() {

            @Override
            public void run() {

                Scanner scanner;

                if (currentLobby.getIsFull()) {
                    System.out.println("criando lobby novo");
                    currentLobby = new Lobby();
                }

                Lobby lobby = currentLobby;

                // Mientras se ejecuta el servidor
                while (serverIsRunning) {
                    String nickname = null;

                    try {
                        // Variable para escuchar al nuevo cliente
                        scanner = new Scanner(newClient.getInputStream());

                        // Mientras haya algo que escuchar
                        while (scanner.hasNextLine()) {
                            // recibir el siguiente mensaje
                            String message = scanner.nextLine();

                            System.out.println("recebi: " + message);

                            // Si "###" finaliza la conexión
                            if (message.equals("###")) {
                                // terminar conexion

                                // Si comienza con la cadena 'Nickname' significa que está enviando un Nickname
                            } else if (message.startsWith("Nickname ")) {

                                // solo toma el Nickname
                                nickname = message.substring(9);

                                // Variable para escribir en el socket del cliente
                                PrintStream output = new PrintStream(newClient.getOutputStream());

                                if (lobby.addPlayer(nickname, newClient)) {
                                    output.println("Valid Nickname");

                                    if (lobby.getIsFull()) {
                                        System.out.println("Começando o juego");
                                        lobby.startGame();
                                    }
                                } else {
                                    // Enviar mensaje de apodo no válido
                                    output.println("Invalid Nickname");
                                }

                                // Entonces, y un mensaje que debe reenviarse a todos los demás.
                            } else {
                                // Llame a la función para enviar a todos los clientes
                                lobby.sendToAllPlayers(nickname, message);
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Servidor não conseguiu ouvir cliente");
                        e.printStackTrace();
                    }
                }
            }
        };

        listenClient.start();
    }
}
