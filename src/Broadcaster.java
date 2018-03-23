import java.util.HashMap;

public class Broadcaster extends Thread{
    private MultiUsers userBase;
    private HashMap<String, ServerClientThread> clients;
    private Message msg;
    private String sender;
    private boolean isServer;

    Broadcaster(MultiUsers userBase,HashMap<String, ServerClientThread> clients, Message msg, String sender,boolean isServer) {
        this.userBase = userBase;
        this.clients = clients;
        this.msg = msg;
        this.sender = sender;
        this.isServer = isServer;
    }

    public void run(){
      if(isServer)
      {
        for (String client: clients.keySet()){
                clients.get(client).sendToSocket(msg,sender);
        }
      }
      else
      {
        for (String client: clients.keySet()){
            if (client.compareTo(sender)!=0 && userBase.isFriend(sender,client))
                clients.get(client).sendToSocket(msg,sender);
        }
      }
    }
}
