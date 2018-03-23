/*
* Will add later
*
*/

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Server extends Thread
{
    private ServerSocket serverSocket;
    private HashMap<String,ServerClientThread> clients;
    private MultiUsers multiUsers = new MultiUsers();

    public Server(int port) throws IOException
    {
        serverSocket = new ServerSocket(port);
        clients = new HashMap<>();
    }

    public void run()
    {
        while(true)
        {
            try
            {
                System.out.println("Waiting for client on port " + serverSocket.getLocalPort());
                Socket clientConnection = serverSocket.accept();
                //Will make neater later
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

    boolean register(Message msg) //Registers and logs in new user
    {
        return multiUsers.registration(msg) && multiUsers.login(msg);
    }

    boolean login(Message msg)
    {
        return multiUsers.login(msg);
    }

    void addLoggedInClient(String name, ServerClientThread thread)
    {
      clients.put(name ,thread);
    }

    void send(Message msg, String sender) throws IOException {
        switch (msg.getCommand())
        {
            case 0:
                if (msg.getTarget().compareTo("all")==0)    //sends message to all users the sender is friends with.
                    new Broadcaster(multiUsers,clients,msg,sender,false).start();
                else if(multiUsers.isFriend(sender,msg.getTarget()))
                {
                    if (clients.containsKey(msg.getTarget()))
                        clients.get(msg.getTarget()).sendToSocket(msg,sender);
                    else
                        clients.get(sender).sendToSocket(new Message(0,"","0"),"0000");         //0000 is code for target client is offline
                }
                else
                    System.out.println(sender + " attempted to message " + msg.getTarget() + " but they are not friends.");
                break;

            case 1:
                System.out.println("File Recieved");
                //System.out.println(msg.getContent());
                //Store data in serverclient thread
                //Ask client if they want to receive file
                //if no delete data.
                //if yes send data.
                if(multiUsers.isFriend(sender,msg.getTarget()))
                {
                    System.out.println("User: @" + sender + " is sending a file to " +msg.getTarget());
                    if (clients.containsKey(msg.getTarget()))
                        clients.get(msg.getTarget()).sendFilePermissionMessage(msg,sender);
                    else
                        clients.get(sender).sendToSocket(new Message(0,"","0"),"0000");         //0000 is code for target client is offline
                }
                else
                    System.out.println(sender + " attempted to message " + msg.getTarget() + " but they are not friends.");
                break;

            case 4:
                multiUsers.logout(msg,clients,sender);
                break;

            case 5:
                //Send friend request offer
                clients.get(msg.getContent()).sendToSocket(new Message(53,sender,"VOID"),sender);
                break;

            case 6:
                try {
                    multiUsers.createGroup(msg,clients,sender);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 7:
                multiUsers.addMem(msg,clients,sender);
                break;
            case 8:
                multiUsers.printStatus(sender,clients);
                break;
            case 9:
                multiUsers.groupMessage(msg,clients,sender);
                break;
            case 10:
                if(Integer.parseInt(msg.getContent()) == 0)
                    clients.get(sender).sendFile(msg.getTarget(),true);
                    //System.out.println("Sender is confirming " + msg.getTarget() + "'s file");
                else
                    clients.get(sender).sendFile(msg.getTarget(),false);
                //System.out.println("Sender is denying " + msg.getTarget() + "'s file");
            case 11:
                clients.get(msg.getTarget()).addBitToFileList(sender,msg);
                break;
            case 12:
                //accept or deny friend request.
                if(msg.getContent().compareTo("1") == 0) {
                    return;
                }
                switch (multiUsers.addFriend(msg,clients,sender))
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
        }

    }

    private synchronized void serverBroadcast(String messageContent)
    {
      Message msg = new Message(0,"all",messageContent);
      new Broadcaster(multiUsers,clients,msg,"Server",true).start();
    }

    public static void main(String[] args)
    {
        int port;
        if(args.length < 0)
        {
          port = Integer.parseInt(args[0]);
        }
        else
        {
            try
            {
                Scanner scPrefer = new Scanner(new File("./resources/preferences"));
                port = Integer.parseInt(scPrefer.nextLine());
                System.out.println("Creating server on port: " + port);
                scPrefer.close();
            }
            catch(FileNotFoundException e)
            {
                System.out.println("No server preferences found. Setting to port to 4444");
                port = 4444;
            }

        }
        try
        {
            Server s = new Server(port);
            s.start();
            Scanner serverCommands = new Scanner(System.in);
            serverLifeTimeLoop:while(true)
            {
              String command = serverCommands.nextLine();
              switch(command)
              {
                case "broadcast":
                  System.out.println("Enter broadcast message:");
                  s.serverBroadcast(serverCommands.nextLine());
                  break;
                case "save":
                  System.out.println("Saving userbase");
                  //save userbase.
                  break;
                case "exit":
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

    //Possibly Temporary
    //Will add error management later
    static Message parseMesseage(String message)
    {
        Scanner parser = new Scanner(message).useDelimiter("\\|");
        Message msg = new Message(parser.nextInt(),parser.next(),parser.next());
        //System.out.println(msg.toString());
        parser.close();
        return msg;
    }

}
