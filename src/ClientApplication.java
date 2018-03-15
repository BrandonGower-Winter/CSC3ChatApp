import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ClientApplication
{
  public static void main(String args[])
  {
    String serverName = "localhost";
    int port = 4444;
    String name;
    try
    {
      System.out.println("Connecting to " + serverName + " on port " + port);

      Socket client = new Socket(serverName,port);

      System.out.println("Just connected to " + client.getRemoteSocketAddress());
      Scanner input = new Scanner(System.in);
      DataOutputStream out =  new DataOutputStream(client.getOutputStream());
      //Will make neater
      System.out.println("Enter your Username:");
      name = input.nextLine();
      out.writeUTF(name);
      new ClientThread(name,client).start();
      //Manages user output.
      while(true)
      {
        String toSend = input.nextLine();
        out.writeUTF(toSend);
        //Ignore this.
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
