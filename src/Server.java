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
    private MultiUsers multiUsers = new MultiUsers(); // *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *

    public Server(int port) throws IOException
    {
        serverSocket = new ServerSocket(port);
        clients = new HashMap<String,ServerClientThread>();
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
                DataInputStream tempInStream = new DataInputStream(clientConnection.getInputStream());
                DataOutputStream tempOutputStream = new DataOutputStream(clientConnection.getOutputStream());

                Message clientLoginDetails = parseMesseage(tempInStream.readUTF());
                //Manage user login
                //Will add to function later
                //Add login and registration differentiation.
                if(multiUsers.registration(clientLoginDetails)) //Successful registration
                {
                  tempOutputStream.writeUTF("50|" + clientLoginDetails.getTarget() + "|0");
                }
                else //Registration Unsuccessful
                {
                  tempOutputStream.writeUTF("50|" + clientLoginDetails.getTarget() + "|1");
                  continue; //Skip rest because we don't want to create this client
                }
                ServerClientThread clientThread = new ServerClientThread(clientLoginDetails.getTarget() ,clientConnection,this);
                //Will add duplicate name check later
                clients.put(clientLoginDetails.getTarget() ,clientThread);
                System.out.println("Registered new user: " + clientLoginDetails.getTarget());
                clientThread.start();
            }
            catch(IOException e)
            {
                System.err.println("IO Exception!!!");
                e.printStackTrace();
                break;
            }
        }
    }

    public void send(Message msg,String sender)
    {
        switch (msg.getCommand())// *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *
        {
            case 0:
                if (msg.getTarget().compareTo("all")==0)                                                                //sends update to all clients in the server
                    new Broadcaster(clients,msg,sender).start();
                else
                    clients.get(msg.getTarget()).sendToSocket(msg,sender);                                              //one to one communication without registration
                break;

            case 1:
                //file functionality
                break;

            case 2:
                multiUsers.registration(msg,sender,clients);                                                                              //registration
                break;

            case 3:
                multiUsers.login(msg,clients,sender);
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
        }                       // *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *

    }

    public static void main(String[] args)
    {
        int port;
        if(args.length < 1)
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
            s.run(); //Intentional
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    //Possibly Temporary
    //Will add error management later
    public static Message parseMesseage(String message)
    {
        Scanner parser = new Scanner(message).useDelimiter("\\|");
        Message msg = new Message(parser.nextInt(),parser.next(),parser.next());
        //System.out.println(msg.toString());
        parser.close();
        return msg;
    }

}
