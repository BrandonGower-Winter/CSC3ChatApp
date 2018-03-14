/*
* Will add later
*
*/

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class Server extends Thread
{
  private ServerSocket serverSocket;
  private HashMap<String,ServerClientThread> clients;

  public Server(int port) throws IOException
  {
    serverSocket = new ServerSocket(port);
    clients = new HashMap<String,ServerClientThread>();
  }

  public void run()
  {
    while(true)
    {
      try
      {
        System.out.println("Waiting for client on port " + serverSocket.getLocalPort());
        Socket clientConnection = serverSocket.accept();
        //Will make neater later
        DataInputStream tempInStream = new DataInputStream(clientConnection.getInputStream());
        String clientName = tempInStream.readUTF();
        ServerClientThread clientThread = new ServerClientThread(clientName,clientConnection,this);
        //Will add duplicate name check later
        clients.put(clientName,clientThread);
        System.out.println("Registered new user: " + clientName);
        clientThread.start();
      }
      catch(IOException e)
      {
        System.err.println("IO Exception!!!");
        e.printStackTrace();
        break;
      }
    }
  }
  /*
  *This is where the file codes should be managed
  * 0 is message. if target is all treat as broadcast.
  * 1 is file.
  */
  public synchronized void send(Message msg,String sender)
  {
    clients.get(msg.getTarget()).sendToSocket(msg,sender);
  }

  public static void main(String[] args)
  {
    int port = Integer.parseInt(args[0]);
    try
    {
      Server s = new Server(port);
      s.run(); //Intentional
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  //Possibly Temporary
  //Will add error management later
  public static Message parseMesseage(String message)
  {
    Scanner parser = new Scanner(message).useDelimiter("\\|");
    Message msg = new Message(parser.nextInt(),parser.next(),parser.next());
    //System.out.println(msg.toString());
    parser.close();
    return msg;
  }

}
