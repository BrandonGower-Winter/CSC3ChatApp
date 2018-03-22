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
                    clients.get(msg.getTarget()).sendFilePermissionMessage(msg,sender);
                }
                else
                    System.out.println(sender + " attempted to message " + msg.getTarget() + " but they are not friends.");
                break;

            case 4:
                multiUsers.logout(msg,clients,sender);
                break;

            case 5:
                switch (multiUsers.addFriend(msg,clients,sender))
                {
                    case 0:
                        clients.get(sender).sendToSocket(new Message(0,"","0"),"0001");
                        break;
                    case 1:
                        clients.get(sender).sendToSocket(new Message(0,"","0"),"0002");
                        break;

                    default:
                        clients.get(sender).sendToSocket(new Message(0,"","0"),"0003");
                        break;
                }

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

                File f = new File(msg.getContent());
                if (f.exists())
                {
                    byte[] fileBytes = Files.readAllBytes(f.toPath());

                    System.out.println("Sending file: "+fileBytes.length);
                    int partsToSend = (int)Math.ceil(fileBytes.length/(double)16000);
                    System.out.println("File will be sent in " + partsToSend + " parts");

                    for(int i = 0; i < partsToSend; i++)
                    {
                        String fileData = f.getName() + "%" + fileBytes.length + "%" + (i+1) + "%";
                        if(i+1 != partsToSend)
                        {
                            //fileData+= Arrays.copyOfRange(fileBytes,16000*(i),16000*(i+1)).length;
                            fileData += Base64.getEncoder().encodeToString(Arrays.copyOfRange(fileBytes,16000*(i),16000*(i+1)));
                        }
                        else
                        {
                            //fileData+= Arrays.copyOfRange(fileBytes,16000*(i),fileBytes.length).length;
                            fileData += Base64.getEncoder().encodeToString(Arrays.copyOfRange(fileBytes,16000*(i),fileBytes.length));
                        }
                        msg.setContent(fileData);
                        clients.get(msg.getTarget()).addBitToFileList(sender,msg);
                    }

                    Message message = new Message(1,msg.getTarget(),f.getName()+"%"+fileBytes.length);
                    send(message,sender);
                    clients.get(msg.getTarget()).addBitToFileList(sender,msg);
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
