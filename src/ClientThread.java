import java.net.*;
import java.io.*;
import javax.swing.JOptionPane;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Base64;

public class ClientThread extends Thread
{
  private Socket client;
  private HashMap<String,ArrayList<Networkfile>> filesToWrite;

  public ClientThread(Socket client)
  {
    this.client = client;
    filesToWrite = new HashMap<String,ArrayList<Networkfile>>();
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
              Scanner scFileMessage = new Scanner(msg.getContent()).useDelimiter("%");
              System.out.println("File permissions: " + msg.getContent());
              if(JOptionPane.showConfirmDialog(null,"@" + msg.getTarget() + " wants to send file: " + scFileMessage.next() + " ("+scFileMessage.nextInt()+") bytes","File from " + msg.getTarget(),JOptionPane.YES_NO_OPTION)==0)
              {
                sendFileConfirmation(msg.getTarget(),true);
              }
              else
              {
                sendFileConfirmation(msg.getTarget(),false);
              }
              break;
          case 52:

              Networkfile toAdd = Networkfile.parseNetworkFile(msg.getContent());
              if(filesToWrite.containsKey(msg.getTarget()))
              {
                filesToWrite.get(msg.getTarget()).add(toAdd);
              }
              else
              {
                ArrayList<Networkfile> fileBits = new ArrayList<Networkfile>();
                fileBits.add(toAdd);
                filesToWrite.put(msg.getTarget(),fileBits);
              }
              if(filesToWrite.get(msg.getTarget()).size() == Math.ceil(toAdd.getFileSize()/(double)16000))
              {
                //System.out.println("Time to write file on part: " + toAdd.getFilePart());
                FileOutputStream out = new FileOutputStream(toAdd.getName());
                for(Networkfile filebit : filesToWrite.get(msg.getTarget()))
                {
                  byte[] b = Base64.getDecoder().decode(filebit.getContent());
                  out.write(b);
                }
                out.close();
                filesToWrite.remove(msg.getTarget());
              }

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
