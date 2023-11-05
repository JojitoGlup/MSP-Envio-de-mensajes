
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class UserManager {

    public static final String CLASS_NAME = UserManager.class.getSimpleName();
    public static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    private HashMap<String, Socket> userConnections;

    public UserManager() {
        super();
        userConnections = new HashMap<String, Socket>();
    }

    public boolean connect(String user, Socket socket) {
        boolean result = true;

       if (userConnections.containsKey(user)){
           result = false;
       }else {
           userConnections.put(user,socket);
       }
       return result;
    }
    public boolean disconnect(String user){
        boolean result = true;
        if (userConnections.containsKey(user)){
            userConnections.remove(user);
        }else {
            result = false;
        }
        return result;
    }

    public void send(String user, String message) {

        Socket socket = userConnections.get(user);
        if (socket!= null){
            try {
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                output.println(message);
            }catch (IOException e){
                LOGGER.severe(e.getMessage());
                e.printStackTrace();
            }
        }

    }
    public String getUserList(){
        StringBuilder userList = new StringBuilder();
        for (String user: userConnections.keySet()){
            userList.append(user).append(" ");
        }
        return userList.toString().trim();
    }
}
