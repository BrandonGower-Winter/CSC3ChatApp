import java.util.HashMap;
/**
 * Need to clean up and optimize
 * */
public class Broadcaster extends Thread{
    private HashMap<String, ServerClientThread> clients;
    private Message msg;
    private String sender;

    Broadcaster(HashMap<String, ServerClientThread> clients, Message msg, String sender) {
        this.clients = clients;
        this.msg = msg;
        this.sender = sender;
    }

    public void run(){
        for (String client: clients.keySet()){
            if (client.compareTo(sender)!=0)
                clients.get(client).sendToSocket(msg,sender);
        }
    }
}
