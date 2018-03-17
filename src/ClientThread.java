import java.net.*;
import java.io.*;
import javax.swing.JOptionPane;
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
          case 51:
              if(JOptionPane.showConfirmDialog(null,"@" + msg.getTarget() + " wants to send file: " + msg.getContent(),"File from " + msg.getTarget(),JOptionPane.YES_NO_OPTION)==0)
              {
                sendFileConfirmation(msg.getTarget(),true);
              }
              else
              {
                sendFileConfirmation(msg.getTarget(),false);
              }
              break;
          case 52:
              System.out.println("Received" + msg.toString());
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

  private void sendFileConfirmation(String target,boolean receiveFile)
  {
    try
    {
      DataOutputStream out = new DataOutputStream(client.getOutputStream());
      if(receiveFile)
      {
        out.writeUTF("10|"+target+"|0");
      }
      else
      {
        out.writeUTF("10|"+target+"|1");
      }
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  private void notifyClientLoginStatus(Message msg)
  {
    switch(Integer.parseInt(msg.getContent()))
    {
      case 0:
      case 2:
      ClientApplication.changeClientLoginStatus(1);
      break;
      case 1:
      case 3:
      ClientApplication.changeClientLoginStatus(-1);
      break;
    }
  }

}
