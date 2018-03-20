import java.io.IOException;
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


    synchronized void createGroup(Message msg, HashMap<String, ServerClientThread> clients, String sender) throws IOException {
       ArrayList<String> res = database.createGroup(msg.getTarget(),msg.getContent(), sender);

       if (res.size()!=0)
       {
           StringBuilder string = new StringBuilder();
           for (String s: res)
               string.append(s).append(", ");
           System.out.println("GROUP: "+msg.getTarget()+" HAS BEEN CREATED BY "+sender+" AND HAS THE FOLLOWING MEMBERS: "+string);
           for (String s: res)
               if (clients.containsKey(s))
                   clients.get(s).sendToSocket(new Message(0,s,"GROUP: "+msg.getTarget()+" HAS BEEN CREATED BY "+sender+" AND HAS THE FOLLOWING MEMBERS: "+string),msg.getTarget());
       }
    }




    synchronized void addMem(Message msg, HashMap<String, ServerClientThread> clients, String sender) throws IOException {
        String[] res = database.addMem(msg.getTarget(),sender,msg.getContent());
        if (res!=null)
        {
            for (String ss: res)
            {
                System.out.println("memberszzz: "+ss);
                if (clients.containsKey(ss))
                     clients.get(ss).sendToSocket(new Message(0,ss,"Added a new member to the group: "+res[res.length -1]),msg.getTarget());
            }
        }
    }

    synchronized void printStatus(String sender, HashMap<String, ServerClientThread> clients) {
        clients.get(sender).sendToSocket(new Message(8,"",database.printStatus(sender)),sender);
    }

    synchronized void groupMessage(Message msg, HashMap<String, ServerClientThread> clients, String sender) {
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