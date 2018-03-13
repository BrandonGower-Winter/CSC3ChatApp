//Just for testing purposes

import java.net.*;
import java.io.*;

public class GreetingsServer extends Thread
{
  private ServerSocket serverSocket;

  public GreetingsServer(int port) throws IOException
  {
    serverSocket = new ServerSocket(port);
  }

  public void run()
  {
    while(true)
    {
      try
      {
        System.out.println("Waiting for client on port " + serverSocket.getLocalPort());
        Socket server = serverSocket.accept();

        System.out.println("Connected to " + server.getRemoteSocketAddress());
        DataOutputStream out = new DataOutputStream(server.getOutputStream());
        out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress() + "\nGoodbye!");
        server.close();


      }
      catch(IOException e)
      {
        System.err.println("IO Exception!!!");
        e.printStackTrace();
        break;
      }
    }
  }


  public static void main(String[] args)
  {
    int port = Integer.parseInt(args[0]);
    try
    {
      Thread t = new GreetingsServer(port);
      t.start();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

}
