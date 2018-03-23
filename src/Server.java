/*
* Server application class.
*
* This class manages the message handling for the input data received from client applications.
* @author Brandon
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Server extends Thread
{
    //Server object variables
    private ServerSocket serverSocket; //Socket connection of the server
    private HashMap<String,ServerClientThread> clients; //Hashmap that stores online users
    private MultiUsers multiUsers = new MultiUsers(); //User database. (Manages friends and groups)

    /*
    * Server constructor
    * @param port Port number to which the server must be bound to.
    * @throws IOException
     */
    public Server(int port) throws IOException
    {
        serverSocket = new ServerSocket(port);
        clients = new HashMap<>();
    }
    /*
    * Sever run method.
    * Overrides Thread method
    * Used to keep server waiting for connections on separate thread to the main thread.
     */
    public void run()
    {
        while(true) //Infinite loop
        {
            try
            {
                System.out.println("Waiting for client on port " + serverSocket.getLocalPort());
                Socket clientConnection = serverSocket.accept(); //Accept connection from client application
                //Start new Server Client thread when socket receives connection.
                new ServerClientThread(clientConnection,this).start();
            }
            catch(IOException e)
            {
                System.err.println("IO Exception!!!");
                e.printStackTrace();
                break;
            }
        }
    }
    /*
    * Register function
    * Registers user to the database and logs them in
    * @param msg Msg send by client to server
    * @return boolean
    * @see Message
     */
    boolean register(Message msg) //Registers and logs in new user
    {
        return multiUsers.registration(msg) && multiUsers.login(msg);
    }
    /*
     * Login attempt function
     * Attempts to log user into the server
     * @param msg Msg send by client to server
     * @return boolean
     * @see Message
     */
    boolean login(Message msg)
    {
        return multiUsers.login(msg);
    }
    /*
     * Login function
     * Once validated by @login the client is registered to the online users hashmap clients
     * @param name Username of client
     * @param thread ServerClientThread to which the client's connection is bound.
     */
    void addLoggedInClient(String name, ServerClientThread thread)
    {
      clients.put(name ,thread);
    }
    /*
     * Server send function
     * This function takes in a message object received from a client and decides on the appropriate response (See readme.md for a full list of codes and their functionality).
     * @param msg The message which the client sent to the server.
     * @param sender The name of the client who sent the message.
     * @see Message
     */
    void send(Message msg, String sender) throws IOException {
        switch (msg.getCommand()) //Switch to determine appropriate action.
        {
            case 0: //Message sending
                if (msg.getTarget().compareTo("all")==0)    //Broadcast message: sends message to all users the sender is friends with.
                    new Broadcaster(multiUsers,clients,msg,sender,false).start(); //Starts broadcast thread.
                else if(multiUsers.isFriend(sender,msg.getTarget()))//Checks if sender and user are friends.
                {
                    if (clients.containsKey(msg.getTarget())) //Check if friend is online
                        clients.get(msg.getTarget()).sendToSocket(msg,sender); //Send message to user
                    else
                        clients.get(sender).sendToSocket(new Message(0,"","0"),"0000");         //0000 is code for target client is offline
                }
                else//If the sender and recipient are not friends the server ignores the message.
                    System.out.println(sender + " attempted to message " + msg.getTarget() + " but they are not friends.");
                break;

            case 1:
                //System.out.println(msg.getContent());
                //Store data in serverclient thread
                //Ask client if they want to receive file
                //if no delete data.
                //if yes send data.
                if (PseudoDatabase.groupData.containsKey(msg.getTarget())) //Checks to see if file is being send to a group chat
                {

                    for (String string: PseudoDatabase.groupData.get(msg.getTarget())) //Loop through each member in the group chat
                    {
                        if (string.compareTo(sender)!=0) //Ensure file isn't sent to the user who initiated the send
                            if (clients.containsKey(string))       //send file to online users
                            {
                                clients.get(string).sendFilePermissionMessage(msg,sender); //Send file permission message
                            }
                    }
                }
                else //Target is a single user
                    {
                        if(multiUsers.isFriend(sender,msg.getTarget())) //Determine if users are friends
                        {
                            System.out.println("User: @" + sender + " is sending a file to " +msg.getTarget());
                            if (clients.containsKey(msg.getTarget())) //if client is online
                                clients.get(msg.getTarget()).sendFilePermissionMessage(msg,sender); //Send file permission message
                            else //client is offline
                                clients.get(sender).sendToSocket(new Message(0,"","0"),"0000");         //0000 is code for target client is offline
                        }
                        else //Reject file if clients are not friends
                            System.out.println(sender + " attempted to message " + msg.getTarget() + " but they are not friends.");
                    }

                break;

            case 4: //Client logout
                System.out.println("Client requested to leave");
                multiUsers.logout(msg,clients,sender); //Logs out user
                System.out.println("Client left***");
                clients.remove(sender); //removes client from online users hashmap
                break;

            case 5: //Send friend request offer
                clients.get(msg.getContent()).sendToSocket(new Message(53,sender,"VOID"),sender); //Sends friend request
                break;

            case 6: //Create Group
                try {
                    multiUsers.createGroup(msg,clients,sender);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 7://Add member to group
                multiUsers.addMem(msg,clients,sender);
                break;
            case 8://Print status of online users
                multiUsers.printStatus(sender,clients);
                break;
            case 9: // Send group message
                multiUsers.groupMessage(msg,clients,sender);
                break;
            case 10: //Confirm file sending
                if(Integer.parseInt(msg.getContent()) == 0) //Yes the client wants the file
                    clients.get(sender).sendFile(msg.getTarget(),true);
                    //System.out.println("Sender is confirming " + msg.getTarget() + "'s file");
                else //No the client does not want the file
                    clients.get(sender).sendFile(msg.getTarget(),false);
                //System.out.println("Sender is denying " + msg.getTarget() + "'s file");
            case 11: //Send a file bit to user(s)
                if (PseudoDatabase.groupData.containsKey(msg.getTarget())) //If the target is a group
                {
                    for (String string: PseudoDatabase.groupData.get(msg.getTarget())) //Send file bit to each member in the group
                    {
                        if (string.compareTo(sender)!=0)
                            if (clients.containsKey(string))      //sending to online users and not to sending user
                                clients.get(string).addBitToFileList(sender,msg); //Sends file bit
                    }
                }
                else
                    clients.get(msg.getTarget()).addBitToFileList(sender,msg);
                break;
            case 12:
                //accept or deny friend request.
                if(msg.getContent().compareTo("1") == 0) { //Friend Request denied
                    return;
                }
                switch (multiUsers.addFriend(msg,clients,sender)) //Attempt to add friend
                {
                    case 0:
                        clients.get(msg.getTarget()).sendToSocket(new Message(0,"","0"),"0001");
                        break;
                    case 1:
                        clients.get(msg.getTarget()).sendToSocket(new Message(0,"","0"),"0002");

                        if (clients.containsKey(msg.getContent()))              //does not send notification if user is offline
                            clients.get(msg.getContent()).sendToSocket(new Message(0,sender,sender),"0004");
                        break;

                    default:
                        clients.get(msg.getTarget()).sendToSocket(new Message(0,"","0"),"0003");
                        break;
                }
                break;

            case 13:
                //sends friends list

                if (MultiUsers.database.getUserData().containsKey(msg.getTarget()))
                {
                    System.out.println("got here");
                    String friends="";
                    for (int x =2; x< MultiUsers.database.getUserData().get(msg.getTarget()).size(); x++)
                    {
                        friends += MultiUsers.database.getUserData().get(msg.getTarget()).get(x)+"@";
                    }
                    clients.get(msg.getTarget()).sendToSocket(new Message(0,"blank for fix",friends),"W1000");


                    String group = "";
                    for (String string: MultiUsers.database.getGroupData().keySet())
                    {
                        if (MultiUsers.database.getGroupData().get(string).contains(msg.getTarget()))
                            group+=string+"@";
                    }
                    clients.get(msg.getTarget()).sendToSocket(new Message(0,"blank for fix",group),"W1001");

                }
                break;
        }

    }
    /*
    * Private function to start a server broadcast to all users
    * @param messageContent Content of the message the server wishes to send.
     */
    private synchronized void serverBroadcast(String messageContent)
    {
      Message msg = new Message(0,"all",messageContent);
      new Broadcaster(multiUsers,clients,msg,"Server",true).start();
    }
    /*
    * Main function
     */
    public static void main(String[] args)
    {
        int port;
        if(args.length < 0) //No longer used
        {
          port = Integer.parseInt(args[0]);
        }
        else //Get  server preferences from a bile
        {
            try
            {
                Scanner scPrefer = new Scanner(new File("./resources/preferences"));
                port = Integer.parseInt(scPrefer.nextLine());
                System.out.println("Creating server on port: " + port);
                scPrefer.close();
            }
            catch(FileNotFoundException e) //If not file found default values used
            {
                System.out.println("No server preferences found. Setting to port to 4444");
                port = 4444;
            }

        }
        try //Start up server
        {
            Server s = new Server(port);
            s.start(); //Start server object on own thread
            Scanner serverCommands = new Scanner(System.in); //Input from server side allowing for server functions
            serverLifeTimeLoop:while(true)
            {
              String command = serverCommands.nextLine();
              switch(command)
              {
                case "broadcast": //Broadcase message to all online users
                  System.out.println("Enter broadcast message:");
                  s.serverBroadcast(serverCommands.nextLine());
                  break;
                case "save": //Never implemented
                  System.out.println("Saving userbase");
                  //save userbase.
                  break;
                case "exit": //Exit the server
                  System.out.println("Shutting down server...");
                  //save userbase
                  break serverLifeTimeLoop;
              }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /*
    * Message parsing function to turn all strings in the format <code>|<target>|<content> to a message object
    * @param message The string message received from a socket
    * @returns Message object
    * @see Message
     */
    static Message parseMesseage(String message)
    {
        Scanner parser = new Scanner(message).useDelimiter("\\|");
        Message msg = new Message(parser.nextInt(),parser.next(),parser.next());
        //System.out.println(msg.toString());
        parser.close();
        return msg;
    }

}
