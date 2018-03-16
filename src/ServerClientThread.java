import java.net.*;
import java.io.*;


public class ServerClientThread extends Thread
{
  protected String clientName;
  protected Socket serverClientConnection;
  protected Server server;
  public ServerClientThread(Socket socket,Server server)
  {
    this.clientName = "UNDEFINED";
    this.serverClientConnection = socket;
    this.server = server;
  }

  public void run()
  {
    try
    {
      DataInputStream in = new DataInputStream(serverClientConnection.getInputStream());
      DataOutputStream tempOutputStream = new DataOutputStream(serverClientConnection.getOutputStream());
      //User login loop. Must login before sending messages.
      Message clientLoginDetails = new Message();
      while(true)
      {
        clientLoginDetails = Server.parseMesseage(in.readUTF());
        //Manage user login
        //Will add to function later
        //Add login and registration differentiation.
        if(server.register(clientLoginDetails)) //Successful registration
        {
          tempOutputStream.writeUTF("50|" + clientLoginDetails.getTarget() + "|0");
          clientName = clientLoginDetails.getTarget();
          server.addLoggedInClient(clientName,this);
          System.out.println("Registered new user: " + clientName);
          break;
        }
        else //Registration Unsuccessful
        {
          tempOutputStream.writeUTF("50|" + clientLoginDetails.getTarget() + "|1");
          continue; //Skip because we don't want to create this client
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
      e.printStackTrace();
    }
  }

  public void sendToSocket(Message msg,String sender)
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

  //** * * *  * * * **  * *         * * *   * * * * * *     * * * * * * * * *
  public void setClientName(String clientName) {
    this.clientName = clientName;
  }
  //** * * *  * * * **  * *         * * *   * * * * * *     * * * * * * * * *

}
