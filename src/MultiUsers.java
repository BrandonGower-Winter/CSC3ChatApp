import java.util.ArrayList;
import java.util.HashMap;

class MultiUsers extends Thread
{
    static PseudoDatabase database = new PseudoDatabase();

    synchronized boolean registration(Message message)
    {
      return database.register(message.getTarget(),message.getContent(),false);
    }

    synchronized boolean login(Message message)
    {
      return database.login(message.getTarget(),message.getContent());
    }


    synchronized void logout(Message msg, HashMap<String, ServerClientThread> clients, String sender)
    {
        if (database.logout(msg.getTarget(),msg.getContent()))
        {
            clients.get(sender).sendToSocket(new Message(4,sender,"LOGGED OUT SUCCESSFULLY!"),sender);
            clients.remove(sender).interrupt();
        }
        else
            clients.get(sender).sendToSocket(new Message(4,sender,"FAILED TO LOGOUT!"),sender);
    }

    synchronized void addFriend(Message msg, HashMap<String, ServerClientThread> clients, String sender)
    {
        database.addFriend(msg.getTarget(),msg.getContent());
    }


    synchronized boolean isFriend(String user, String friend)
    {
      return database.isFriend(user,friend);
    }


    void createGroup(Message msg, HashMap<String, ServerClientThread> clients, String sender) {
       ArrayList<String> res = database.createGroup(msg.getTarget(),msg.getContent(), sender);
        StringBuilder string = new StringBuilder();
        for (String s: res)
            string.append(s).append(", ");
        System.out.println("GROUP: "+msg.getTarget()+" HAS BEEN CREATED BY "+sender+" AND HAS THE FOLLOWING MEMBERS: "+string);

        for (String s: res)
            clients.get(s).sendToSocket(new Message(0,s,"GROUP: "+msg.getTarget()+" HAS BEEN CREATED BY "+sender+" AND HAS THE FOLLOWING MEMBERS: "+string),msg.getTarget());
    }

    void addMem(Message msg, HashMap<String, ServerClientThread> clients, String sender) {
        if (database.addMem(msg.getTarget(),sender,msg.getContent()))
            clients.get(sender).sendToSocket(new Message(5,sender,"SUCCESSFULLY ADDED: "+msg.getContent()+" AS A MEMBER OF GROUP: "+msg.getTarget()),sender);
        else
            clients.get(sender).sendToSocket(new Message(5,sender,"FAILED TO ADD: "+msg.getContent()+" AS A MEMBER OF GROUP: "+msg.getTarget()),sender);
    }

    void printStatus(String sender, HashMap<String, ServerClientThread> clients) {
        clients.get(sender).sendToSocket(new Message(8,"",database.printStatus(sender)),sender);
    }

    void groupMessage(Message msg, HashMap<String, ServerClientThread> clients, String sender) {
        new Thread(() -> {
            for (String string: database.getGroupData().get(msg.getTarget()))
            {
                for (String client: clients.keySet()){
                    if (client.compareTo(sender)!=0 && string.compareTo(client)==0)
                        clients.get(client).sendToSocket(msg,sender);
                }
            }
        }).start();
    }
}