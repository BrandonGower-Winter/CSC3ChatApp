import java.util.HashMap;
/**
 * Need to clean up and optimize
 * */
public class Broadcaster extends Thread{
    private MultiUsers userBase;
    private HashMap<String, ServerClientThread> clients;
    private Message msg;
    private String sender;

    Broadcaster(MultiUsers userBase,HashMap<String, ServerClientThread> clients, Message msg, String sender) {
        this.userBase = userBase;
        this.clients = clients;
        this.msg = msg;
        this.sender = sender;
    }

    public void run(){
        for (String client: clients.keySet()){
            if (client.compareTo(sender)!=0 && userBase.isFriend(sender,client))
                clients.get(client).sendToSocket(msg,sender);
        }
    }
}
