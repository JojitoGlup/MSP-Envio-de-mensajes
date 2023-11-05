

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class ConnectionHandler implements Runnable {

    public static final String CLASS_NAME = ConnectionHandler.class.getSimpleName();
    public static final Logger LOGGER = Logger.getLogger(CLASS_NAME);


    private UserManager users;
    private Socket clientSocket = null;

    private BufferedReader input;
    private PrintWriter output;


    public ConnectionHandler(UserManager u, Socket s) {
        users = u;
        clientSocket = s;

        try {
            input = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{
            String buffer = null ;
            while (true) {
                String userList = users.getUserList();
                buffer = input.readLine();

                if (buffer== null){
                    break;
                }

                String command = buffer.trim();
                String[] contenido = command.split(" ");
                switch (contenido[0]){
                    case "CONNECT":
                        String userName = command.substring(command.indexOf(' ')).trim();
                        System.out.println(userName);
                        users.connect(userName,clientSocket);
                        boolean isConnected =  users.connect(userName,clientSocket);
                        if (!isConnected){
                            output.println("OK");
                        }else {
                            output.println("FAIL");
                        }
                        break;
                    case "DISCONNECT":
                        String userNameD = command.substring(command.indexOf(' ')+1).trim();
                        System.out.println(userNameD);
                        boolean disconnected = users.disconnect(userNameD);
                        if (disconnected){
                            System.out.println(userList);
                            output.println(userNameD+" DISCONNECTED");
                        }else {
                            output.println("No se pudo");
                        }
                        break;
                    case "SEND":
                        if (contenido[1].charAt(0)== '#'){
                            String message = command.substring(command.indexOf('#')+1,
                                    command.indexOf('@'));
                            System.out.println(message);
                            if (message.length()<140){
                                String userNameS = command.substring(command.indexOf('@')+1).trim();
                                ArrayList<String> usuarios = new ArrayList<>(Arrays.asList(userList.split(" ")));
                                if (usuarios.contains(userNameS)){
                                    users.send(userNameS, message);
                                }else {
                                    output.println("El usuario no existe, esta desconectado o escribio mal el nombre");
                                }
                            }else {
                                output.println("el mensaje no debe contener mas de 140 caracteres");
                            }

                        }else{
                            output.println("El mensaje debe empezar con '#' ");
                        }
//                        String userNameS = command.substring(command.indexOf('@')+1).trim();
//                        users.send(userNameS, message);
                        break;
                    case "LIST":
                        output.println(userList);
                        break;
                    default:
                        output.println("El comando no se reconoce.");
                        break;
                }

            }
        }catch (IOException ex){
            LOGGER.severe(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
