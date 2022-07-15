package server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Lobby {

    private Player jugadores[];
    private int numero;
    private boolean lleno;
    private int turn = 0;

    public Lobby() {

        jugadores = new Player[4];
        numero = 0;
        lleno = false;
    }

    public boolean getIsFull() {
        return lleno;
    }

    public int getNumberOfPlayers() {
        return numero;
    }

    public boolean addPlayer(String nickname, Socket socket) {

        if (numero == 3) {
            lleno = true;
        }

        if (numero != 0) {

            for (int i = 0; i < numero; i++) {

                if (jugadores[i].getNickname().compareTo(nickname) == 0) {

                    return false;
                }
            }
        }

        jugadores[numero] = new Player(nickname, socket);
        numero += 1;

        return true;
    }

    public void startGame() {

        for (int i = 0; i < numero; i++) {

            try {

                // Crear la variable para escribir al cliente actual
                PrintStream output = new PrintStream(jugadores[i].getSocket().getOutputStream());
                String message = "Game Start";
                // Crear el mensaje completo
                String fullMessage;

                if (turn == i) {
                    fullMessage = "tu turno " + message;
                } else {
                    fullMessage = message;
                }

                // Enviar el mensaje completo
                output.println(fullMessage);
            } catch (IOException e) {
                System.out.println("No consigo comunicarme " + jugadores[i].getNickname());
                e.printStackTrace();
            }
        }

        turn += 1;
    }

    public void sendToAllPlayers(String nickname, String message) {

        for (int i = 0; i < numero; i++) {
            if (jugadores[i].getNickname().compareTo(nickname) != 0) {
                try {

                    // Crear la variable para escribir al cliente actual
                    PrintStream output = new PrintStream(jugadores[i].getSocket().getOutputStream());
                    // Crear el mensaje completo
                    String fullMessage;

                    if (turn == i) {
                        fullMessage = "Tu turno " + message;
                    } else {
                        fullMessage = message;
                    }

                    // Enviar el mensaje completo
                    output.println(fullMessage);
                } catch (IOException e) {
                    System.out.println("No consigo comunicarme " + jugadores[i].getNickname());
                    e.printStackTrace();
                }
            }
        }

        turn += 1;

        if (turn == 4) {
            turn = 0;
        }
    }
}
