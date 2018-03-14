import java.net.*;
import java.io.*;


public class ServerClientThread extends Thread
{
  protected String clientName;
  protected Socket serverClientConnection;
  protected Server server;
  public ServerClientThread(String clientName,Socket socket,Server server)
  {
    this.clientName = clientName;
    this.serverClientConnection = socket;
    this.server = server;
  }

  public void run()
  {
    try
    {
      System.out.println("Connected to " + serverClientConnection.getRemoteSocketAddress());
      DataInputStream in = new DataInputStream(serverClientConnection.getInputStream());
      while(true)
      {
        String toSend = in.readUTF();
        System.out.println("User: @" + clientName + " typed: " + toSend);
        server.send(Server.parseMesseage(toSend),clientName);
        //Ignore this does nothing healthy right now
        if(toSend.compareTo("exit") == 0)
          serverClientConnection.close();
          break;
      }
      serverClientConnection.close();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  public synchronized void sendToSocket(Message msg,String sender)
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

}
