//For testing

import java.net.*;
import java.io.*;

public class GreetingsClient
{
  public static void main(String args[])
  {
    String serverName = args[0];
    int port = Integer.parseInt(args[1]);

    try
    {
      System.out.println("Connecting to " + serverName + " on port " + port);

      Socket client = new Socket(serverName,port);

      System.out.println("Just connected to " + client.getRemoteSocketAddress());
      InputStream inFromServer = client.getInputStream();
      DataInputStream in = new DataInputStream(inFromServer);

      System.out.println("Server says " + in.readUTF());
      client.close();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }

  }
}
