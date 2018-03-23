import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

public class ClientApplication
{
  static DataOutputStream out;
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
      try
      {
        Scanner scPrefer = new Scanner(new File("./resources/preferences"));
        port = Integer.parseInt(scPrefer.nextLine());
        serverName = scPrefer.next();
        System.out.println("Connecting to server "+ serverName +" on port: " + port);
        scPrefer.close();
      }
      catch(FileNotFoundException e)
      {
        System.out.println("No user preferences found. Setting port to 4444 and server name to localhost.");
        port = 4444;
        serverName = "localhost";
      }
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
      out =  new DataOutputStream(client.getOutputStream());

      new ClientThread(client).start();
      Bridge.runGUI();


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


  static boolean registration(String text1, String text2)
  {

    try {
        out.writeUTF("2|"+text1+"|"+text2);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      boolean response = waitClientLoginStatus();
      if(response)
      {
        System.out.println("Registration Successful!");
      }
      else
      { clientLoginStatus = 0;
        return false;
      }
    return true;
  }

  static boolean addFriend(String me, String friend) throws IOException {
    out.writeUTF("5|"+me+"|"+friend);
    return true;
  }

  static boolean createGroup(String GroupName,String sender) throws IOException {
    String temp = GroupName.substring(GroupName.indexOf("|")+1);
    GroupName = GroupName.substring(0,GroupName.indexOf("|"));
    out.writeUTF("6|*"+GroupName+"|"+sender+","+temp);
    return true;
  }


  public static boolean login(String text1, String text2)
  {
    try
    {
      {
        System.out.println("Login... Type: 3|<Username>|<Password>");
        out.writeUTF("3|"+text1+"|"+text2);
        boolean response = waitClientLoginStatus();
        if(response)
        {
          System.out.println("Login Successful!");
          return true;
        }
        else
        {
          System.out.println("Login Failure! Username or password incorrect!!!");
          clientLoginStatus = 0;
          return false;
        }
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

  static void message(String target, String text) throws IOException {
    if (target.compareTo("Broadcast")==0)
      out.writeUTF("0|all|"+text);
    else if (target.substring(0,1).compareTo("*")==0)
      out.writeUTF("9|"+target+"|"+text);
    else
      out.writeUTF("0|"+target+"|"+text);
  }

  static void addGroupMember(String text) throws IOException {
    out.writeUTF("7|*"+text);
  }

  static void sendFile(String selectedUser, File f) throws IOException {

    if (f.exists())
    {
      byte[] fileBytes = Files.readAllBytes(f.toPath());

      System.out.println("Sending file: "+fileBytes.length);
      int partsToSend = (int)Math.ceil(fileBytes.length/(double)16000);
      System.out.println("File will be sent in " + partsToSend + " parts");

      for(int i = 0; i < partsToSend; i++)
      {
        String fileData = f.getName() + "%" + fileBytes.length + "%" + (i+1) + "%";
        if(i+1 != partsToSend)
        {
          //fileData+= Arrays.copyOfRange(fileBytes,16000*(i),16000*(i+1)).length;
          fileData += Base64.getEncoder().encodeToString(Arrays.copyOfRange(fileBytes,16000*(i),16000*(i+1)));
        }
        else
        {
          //fileData+= Arrays.copyOfRange(fileBytes,16000*(i),fileBytes.length).length;
          fileData += Base64.getEncoder().encodeToString(Arrays.copyOfRange(fileBytes,16000*(i),fileBytes.length));
        }
        out.writeUTF("11|"+selectedUser+"|"+fileData);
      }
      out.writeUTF("1|"+selectedUser+"|"+f.getName()+"%"+fileBytes.length);
    }
  }

}
