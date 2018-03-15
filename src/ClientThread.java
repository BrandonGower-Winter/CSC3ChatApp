import java.net.*;
import java.io.*;

public class ClientThread extends Thread
{
  private Socket client;
  private String name;

  public ClientThread(String name,Socket client)
  {
    this.name = name;
    this.client = client;
  }

  public void run()
  {
    try
    {
      DataInputStream in = new DataInputStream(client.getInputStream());
      //Manages user Input.
      while(true)
      {
        Message msg = Server.parseMesseage(in.readUTF());
        //Msg code management would go here
        System.out.println("@" + msg.getTarget() + ": " + msg.getContent());
        //Ignore this.
        if(msg.getContent().compareTo("exit") == 0)
          break;

      }
      client.close();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }
}
