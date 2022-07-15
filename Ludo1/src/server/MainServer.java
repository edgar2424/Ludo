package server;

public class MainServer {

    public static void main(String[] args) {

        Server servidor = Server.getInstance();

        servidor.runServer();

        return;
    }

}
