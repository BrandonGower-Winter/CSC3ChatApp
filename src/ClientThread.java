import java.net.*;
import java.io.*;

public class ClientThread extends Thread
{
  private Socket client;

  public ClientThread(Socket client)
  {
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
        switch(msg.getCommand())
        {
          case 0:
                System.out.println("@" + msg.getTarget() + ": " + msg.getContent());
                break;
          case 50:
                notifyClientLoginStatus(msg);
                break;
        }
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

  private void notifyClientLoginStatus(Message msg)
  {
    int status = Integer.parseInt(msg.getContent());
    if(status == 0)
    {
      ClientApplication.changeClientLoginStatus(1);
    }
    else if(status == 1)
    {
      ClientApplication.changeClientLoginStatus(-1);
    }
  }

}
