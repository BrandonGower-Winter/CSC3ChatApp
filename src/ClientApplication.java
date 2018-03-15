import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ClientApplication
{

  private static int clientLoginStatus = 0;

  public static synchronized void changeClientLoginStatus(int i)
  {
    if(i > -2 && i < 2)
    {
      clientLoginStatus = i;
    }
  }

  public static void main(String args[])
  {
    String serverName;
    int port;
    if(args.length < 2)
    {
      serverName = "localhost";
      port = 4444;
    }
    else
    {
      serverName = args[0];
      port = Integer.parseInt(args[1]);
    }
    String name;
    try
    {
      System.out.println("Connecting to " + serverName + " on port " + port);

      Socket client = new Socket(serverName,port);

      System.out.println("Just connected to " + client.getRemoteSocketAddress());
      Scanner input = new Scanner(System.in);
      DataOutputStream out =  new DataOutputStream(client.getOutputStream());

      new ClientThread(client).start();
      //Will make neater
      while(!manageUserLogin(out,input)){}

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

  private static boolean manageUserLogin(DataOutputStream out, Scanner input)
  {
    try
    {
      System.out.println("Type 1 to create account and 2 to login");
      if(Integer.parseInt(input.nextLine())== 1)
      {
        System.out.println("Register... Type: 2|<Username>|<Password>");
        out.writeUTF(input.nextLine());
        boolean response = waitClientLoginStatus();
        if(response)
        {
          System.out.println("Registration Successful!");
        }
        else
        {
          System.out.println("Registration Failure! Account Already Exists!!!");
          clientLoginStatus = 0;
        }
        return response;
      }
      else
      {
        System.out.println("Login... Type: 3|<Username>|<Password>");
        out.writeUTF(input.nextLine());
        boolean response = waitClientLoginStatus();
        if(response)
        {
          System.out.println("Login Successful!");
        }
        else
        {
          System.out.println("Login Failure! Username or password incorrect!!!");
          clientLoginStatus = 0;
        }
        return response;
      }
    }
    catch(IOException e)
    {
      e.printStackTrace();
      return false;
    }

  }
  private static boolean waitClientLoginStatus()
  {
    try
    {
      while(true)
      {
        if(clientLoginStatus == 1)
        {
          return true;
        }
        if(clientLoginStatus == -1)
        {
          return false;
        }
        Thread.sleep(100);
      }
    }
    catch(InterruptedException e)
    {
      System.out.println("Thread was interrupted during sleep.");
      return false;
    }
  }
}
