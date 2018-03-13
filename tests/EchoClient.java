//For testing

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class EchoClient
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
      System.out.println("Type to echo");
      Scanner input = new Scanner(System.in);
      DataInputStream in = new DataInputStream(client.getInputStream());
      DataOutputStream out =  new DataOutputStream(client.getOutputStream());
      while(true)
      {
        String toSend = input.nextLine();
        out.writeUTF(toSend);
        System.out.println(in.readUTF());
        if(toSend.compareTo("exit") == 0)
          break;
      }
      input.close();
      client.close();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }

  }
}
