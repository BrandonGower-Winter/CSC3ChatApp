import java.net.*;
import java.io.*;


public class EchoThread extends Thread
{
  protected Socket server;
  public EchoThread(Socket socket)
  {
    this.server = socket;
  }

  public void run()
  {
    try
    {
      System.out.println("Connected to " + server.getRemoteSocketAddress());
      DataInputStream in = new DataInputStream(server.getInputStream());
      DataOutputStream out = new DataOutputStream(server.getOutputStream());
      while(true)
      {
        String toEcho = in.readUTF();
        if(toEcho.compareTo("exit") != 0)
          out.writeUTF("@Echo: " + toEcho);
        else
          break;
      }
      server.close();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }
}
