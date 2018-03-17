import java.util.ArrayList;
import java.util.HashMap;
/**
 * \TODO Need to clean up and optimize repeated lines
 * */
class MultiUsers extends Thread
{
    private PseudoDatabase database = new PseudoDatabase();

    public synchronized boolean registration(Message message)
    {
      return database.register(message.getTarget(),message.getContent(),false);
    }

    public synchronized boolean login(Message message)
    {
      return database.login(message.getTarget(),message.getContent());
    }

    synchronized void registration(Message message, String sender, HashMap<String,ServerClientThread> clients)
    {
        if (database.register(message.getTarget(),message.getContent(),false))
            clients.get(sender).sendToSocket(new Message(2,sender,"REGISTRATION SUCCESSFUL!\nPLEASE LOGIN TO USE YOUR ACCOUNT"),sender);
        else
            clients.get(sender).sendToSocket(new Message(2,sender,"USERNAME ALREADY TAKEN!"),sender);
    }


    /**
     * \TODO Need to handle multiple logins by the same user
     * */
    synchronized void login(Message msg, HashMap<String, ServerClientThread> clients, String sender)
    {
        if (database.login(msg.getTarget(),msg.getContent()))
        {
            ServerClientThread obj = clients.remove(sender);        //renaming key
            obj.setClientName(msg.getTarget());                     //renaming client name in ServerClientThread
            clients.put(msg.getTarget(), obj);
            clients.get(msg.getTarget()).sendToSocket(new Message(3,msg.getTarget(),"LOGIN SUCCESSFUL!"),msg.getTarget());
        }
        else
            clients.get(sender).sendToSocket(new Message(3,sender,"LOGIN FAILED!"),sender);
    }



    /**
     * \TODO Have to safely close socket-connection first
     * */
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


    /**
     * \TODO Notify added friend and avoid adding same friend more than once
     * */
    synchronized void addFriend(Message msg, HashMap<String, ServerClientThread> clients, String sender)
    {
        if (database.addFriend(msg.getTarget(),msg.getContent()))
            clients.get(sender).sendToSocket(new Message(5,sender,"SUCCESSFULLY ADDED: "+msg.getContent()+" AS YOUR FRIEND"),sender);
        else
            clients.get(sender).sendToSocket(new Message(5,sender,msg.getContent()+" DOES NOT EXIST OR INVALID INPUT\nREGISTER THE CONTACT FIRST."),sender);
    }


    public synchronized boolean isFriend(String user,String friend)
    {
      return database.isFriend(user,friend);
    }

    //TODO Ensure that all users in a group are notfied that they have been added on a new group
    void createGroup(Message msg, HashMap<String, ServerClientThread> clients, String sender) {
        ArrayList<String> res = database.createGroup(msg.getTarget(),msg.getContent(), sender);
        String string = "";
        for (String s: res)
            string+=s+", ";
        clients.get(sender).sendToSocket(new Message(6,sender,"GROUP: "+msg.getTarget()+" HAS BEEN CREATED BY "+sender+" AND HAS THE FOLLOWING MEMBERS: "+string),sender);
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
        new Thread(new Runnable()
        {   @Override
        public void run()
        {
            for (String string: database.getGroupData().get(msg.getTarget()))
            {
                for (String client: clients.keySet()){
                    if (client.compareTo(sender)!=0 && string.compareTo(client)==0)
                        clients.get(client).sendToSocket(msg,sender);
                }
            }

        }}).start();
    }
}
