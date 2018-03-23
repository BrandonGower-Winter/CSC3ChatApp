import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerClientThread extends Thread
{
  private String clientName;
  private Socket serverClientConnection;
  private Server server;
  private HashMap<String,ArrayList<String>> fileBuffer;

  ServerClientThread(Socket socket, Server server)
  {
    this.clientName = "UNDEFINED";
    this.serverClientConnection = socket;
    this.server = server;
    fileBuffer = new HashMap<>();
  }

  public void run()
  {
    try
    {
      DataInputStream in = new DataInputStream(serverClientConnection.getInputStream());
      DataOutputStream tempOutputStream = new DataOutputStream(serverClientConnection.getOutputStream());
      //User login loop. Must login before sending messages.
      Message clientLoginDetails = new Message();
      ensureLoginLoop:while(true)
      {
        clientLoginDetails = Server.parseMesseage(in.readUTF());
        //Manage user login
        //Will add to function later
        //Add login and registration differentiation.
        switch(clientLoginDetails.getCommand())
        {
          case 2:
            if(server.register(clientLoginDetails)) //Successful registration
            {
              tempOutputStream.writeUTF("50|" + clientLoginDetails.getTarget() + "|2");
              clientName = clientLoginDetails.getTarget();
              server.addLoggedInClient(clientName,this);
              System.out.println("Registered new user: " + clientName);
              break ensureLoginLoop;
            }
            else //Registration Unsuccessful
            {
              tempOutputStream.writeUTF("50|" + clientLoginDetails.getTarget() + "|3");
              continue ensureLoginLoop; //Skip because we don't want to create this client
            }
          case 3:
            if(server.login(clientLoginDetails)) //Login registration
            {
              tempOutputStream.writeUTF("50|" + clientLoginDetails.getTarget() + "|0");
              clientName = clientLoginDetails.getTarget();
              server.addLoggedInClient(clientName,this);
              System.out.println("Registered new user: " + clientName);
              break ensureLoginLoop;
            }
            else //Login Unsuccessful
            {
              tempOutputStream.writeUTF("50|" + clientLoginDetails.getTarget() + "|1");
              continue ensureLoginLoop; //Skip because we don't want to create this client
            }
        }

      }
      System.out.println("Connected to " + serverClientConnection.getRemoteSocketAddress());
      while(true)
      {
        String toSend = in.readUTF();
        System.out.println("User: @" + clientName + " typed: " + toSend);
        server.send(Server.parseMesseage(toSend),clientName);
        //Ignore this does nothing healthy right now
        if(toSend.compareTo("exit") == 0)
          break;
      }
      serverClientConnection.close();
    }
    catch(IOException e)
    {
      System.out.println("Client logged out!!!");
      e.printStackTrace();
    }
  }

  void sendToSocket(Message msg, String sender)
  {
    try
    {
      msg.setTarget(sender);
      DataOutputStream out = new DataOutputStream(serverClientConnection.getOutputStream());
      out.writeUTF(msg.toString());
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  public void sendFilePermissionMessage(Message msg, String sender)
  {
    try
    {
      //System.out.println("Sender in permission is " + sender);
     // fileBuffer.put(sender,Networkfile.parseNetworkFile(msg.getContent())); //Means user can only send one file at a time. Maybe we can change later? TODO
      DataOutputStream out = new DataOutputStream(serverClientConnection.getOutputStream());
     // System.out.println(fileBuffer.get(sender).toString());
      String toSend = "51|"+ sender + "|" + msg.getContent();
      out.writeUTF(toSend);
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  public void sendFile(String sender,boolean sendFile)
  {
    try
    {
      System.out.println("Sender in file send is " + sender);
      if(sendFile)
      {
        DataOutputStream out = new DataOutputStream(serverClientConnection.getOutputStream());
        for(String bit : fileBuffer.get(sender))
        {
          String toSend = "52|"+ sender + "|" + bit;
          out.writeUTF(toSend);
        }
        fileBuffer.remove(sender);
      }
      else
      {
        fileBuffer.remove(sender);
      }
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  public void addBitToFileList(String sender, Message msg)
  {
    //Add file to hashmap that has an array list of strings
    if(fileBuffer.containsKey(sender))
    {
      fileBuffer.get(sender).add(msg.getContent());
    }
    else
    {
      ArrayList<String> fileBits = new ArrayList<String>();
      fileBits.add(msg.getContent());
      fileBuffer.put(sender,fileBits);
    }
  }

  void setClientName(String clientName) {
    this.clientName = clientName;
  }

}
