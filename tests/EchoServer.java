//Just for testing purposes

import java.net.*;
import java.io.*;

public class EchoServer extends Thread
{
  private ServerSocket serverSocket;

  public EchoServer(int port) throws IOException
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
        new EchoThread(server).start();
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
      Thread t = new EchoServer(port);
      t.start();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

}
