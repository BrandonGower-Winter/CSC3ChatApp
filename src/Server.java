/*
* Will add later
*
*/

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

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

    void send(Message msg, String sender)
    {
        switch (msg.getCommand())
        {
            case 0:
                if (msg.getTarget().compareTo("all")==0)    //sends message to all users the sender is friends with.
                    new Broadcaster(multiUsers,clients,msg,sender,false).start();
                else if(multiUsers.isFriend(sender,msg.getTarget()))
                    clients.get(msg.getTarget()).sendToSocket(msg,sender);
                else
                    System.out.println(sender + " attempted to message " + msg.getTarget() + " but they are not friends.");
                break;

            case 4:
                multiUsers.logout(msg,clients,sender);
                break;

            case 5:
                multiUsers.addFriend(msg,clients,sender);
                break;

            case 6:
                multiUsers.createGroup(msg,clients,sender);
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
        if(args.length > 1)
        {
          port = Integer.parseInt(args[0]);
        }
        else
        {
          port = 4444;
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
