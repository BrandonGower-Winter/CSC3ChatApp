import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
/*
* MultiUsers class that acts as a manager for the persistent user and group databases.
* @author Solomon
 */
class MultiUsers extends Thread
{
    static PseudoDatabase database = new PseudoDatabase(); //The database
    /*
    * Registration function
    * Register a user on the database
    * @param message Message send by client to register
    * @return boolean
     */
    synchronized boolean registration(Message message)
    {
      return database.register(message.getTarget(),message.getContent(),false);
    }
    /*
     * Login function
     * Attempts to log a user into the database
     * @param message Message send by client to login
     * @return boolean
     */
    synchronized boolean login(Message message)
    {
      return database.login(message.getTarget(),message.getContent());
    }

    /*
     * Logout function
     * Logout a user out of the application (Server side)
     * @param message Message send by client to logout
     * @param clients The clients that are online at this time
     * @param sender The client trying to logout
     */
    synchronized void logout(Message msg, HashMap<String, ServerClientThread> clients, String sender)
    {
        if (database.logout(msg.getTarget(),msg.getContent())) //If Logout successful
        {
            clients.get(sender).sendToSocket(new Message(4,sender,"LOGGED OUT SUCCESSFULLY!"),sender);
            clients.remove(sender); //Remove user from online user hashmap
        }
        else
            clients.get(sender).sendToSocket(new Message(4,sender,"FAILED TO LOGOUT!"),sender);
    }
    /*
     * addFriend function
     * Add to clients as friends
     * @param message Message send by client to logout
     * @param clients The clients that are online at this time
     * @param sender The client trying to add a friend
     * @return int
     */
    synchronized int addFriend(Message msg, HashMap<String, ServerClientThread> clients, String sender)
    {
        return (database.addFriend(msg.getTarget(),sender)); //Add friends
    }

    /*
     * Is friend function
     * Determines if two clients are friends
     * @param user First client
     * @param friend Second client
     * @return boolean
     */
    synchronized boolean isFriend(String user, String friend)
    {
      return database.isFriend(user,friend);
    }

    /*
     * Create group function
     * Creates a group of clients
     * @param msg Message sent by group creator
     * @param clients Online users
     * @param sender The client who is attempting to create the group
     * @return int
     */
    synchronized int createGroup(Message msg, HashMap<String, ServerClientThread> clients, String sender) throws IOException {
       ArrayList<String> res = database.createGroup(msg.getTarget(),msg.getContent(), sender); //Create group

       if (res.size()!=0) //If number of members in group is not empty
       {
           StringBuilder string = new StringBuilder();
           for (String s: res)
               string.append(s).append(", ");

           for (String s: res)
               if (clients.containsKey(s)) {
                   clients.get(s).sendToSocket(new Message(0,"","(GROUP HAS BEEN CREATED BY "+sender+" AND HAS THE FOLLOWING MEMBERS: "+string+")\n\n"),msg.getTarget()); //Send message to each member notifying them that they are in a group
               }
       }
       else //Send error because group already exists
       {
           clients.get(sender).sendToSocket(new Message(0,"","Group already exists!")," Group already exists!");
       }

       return 0;
    }



    /*
    * Add member function
    * Adds member to group
    * @param msg Message sent by client who wishes to add a friend
    * @param clients Hashmap of all online clients
    * @param sender The client who sent the request
    * @throws IOException
     */
    synchronized void addMem(Message msg, HashMap<String, ServerClientThread> clients, String sender) throws IOException {
        String[] res = database.addMem(msg.getTarget(),sender,msg.getContent());
        if (res!=null)
        {
            for (String ss: res) //Notify all group members that a new user was added
            {
                if (clients.containsKey(ss))
                     clients.get(ss).sendToSocket(new Message(0,ss,"Added a new member to the group: "+res[res.length -1]),msg.getTarget());
            }
        }
    }
    /*
     * Print status function
     * not used
     * Prints the status of all online users
     */
    synchronized void printStatus(String sender, HashMap<String, ServerClientThread> clients) {
        clients.get(sender).sendToSocket(new Message(8,"",database.printStatus(sender)),sender);
    }
    /*
     * Group Message function
     * Sends message to all the members of a group
     * @param message Message send by client to logout
     * @param clients The clients that are online at this time
     * @param sender The client trying to logout
     */
    synchronized void groupMessage(Message msg, HashMap<String, ServerClientThread> clients, String sender) {
        if(!database.getGroupData().containsKey(msg.getTarget()))
        {
            return;
        }
        new Thread(() -> { //Start new thread that sends msg to all the online users in the group.
                for(String member : database.getGroupData().get(msg.getTarget()))
                {
                    System.out.println("on the server: "+member+" redirecting to :"+msg.getTarget());
                    if(clients.containsKey(member) && member.compareTo(sender) != 0)
                    {
                        clients.get(member).sendToSocket(new Message(0,msg.getTarget(),"(" +sender +") "+ msg.getContent()),msg.getTarget());
                    }
                }

        }).start();
    }
}