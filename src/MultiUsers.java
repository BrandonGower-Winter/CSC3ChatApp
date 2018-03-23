import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class MultiUsers extends Thread
{
    static PseudoDatabase database = new PseudoDatabase();

    synchronized boolean registration(Message message)
    {
      return database.register(message.getTarget(),message.getContent(),false);
    }

    synchronized boolean login(Message message)
    {
      return database.login(message.getTarget(),message.getContent());
    }


    synchronized void logout(Message msg, HashMap<String, ServerClientThread> clients, String sender)
    {
        if (database.logout(msg.getTarget(),msg.getContent()))
        {
            clients.get(sender).sendToSocket(new Message(4,sender,"LOGGED OUT SUCCESSFULLY!"),sender);
            clients.remove(sender);
        }
        else
            clients.get(sender).sendToSocket(new Message(4,sender,"FAILED TO LOGOUT!"),sender);
    }

    synchronized int addFriend(Message msg, HashMap<String, ServerClientThread> clients, String sender)
    {
        return (database.addFriend(msg.getTarget(),sender));
    }


    synchronized boolean isFriend(String user, String friend)
    {
      return database.isFriend(user,friend);
    }


    synchronized int createGroup(Message msg, HashMap<String, ServerClientThread> clients, String sender) throws IOException {
       ArrayList<String> res = database.createGroup(msg.getTarget(),msg.getContent(), sender);

       if (res.size()!=0)
       {
           StringBuilder string = new StringBuilder();
           for (String s: res)
               string.append(s).append(", ");

           for (String s: res)
               if (clients.containsKey(s)) {
                   clients.get(s).sendToSocket(new Message(0,"","(GROUP HAS BEEN CREATED BY "+sender+" AND HAS THE FOLLOWING MEMBERS: "+string+")\n\n"),msg.getTarget());
               }
       }
       else
       {
           clients.get(sender).sendToSocket(new Message(0,"","Group already exists!")," Group already exists!");
       }

       return 0;
    }




    synchronized void addMem(Message msg, HashMap<String, ServerClientThread> clients, String sender) throws IOException {
        String[] res = database.addMem(msg.getTarget(),sender,msg.getContent());
        if (res!=null)
        {
            for (String ss: res)
            {
                if (clients.containsKey(ss))
                     clients.get(ss).sendToSocket(new Message(0,ss,"Added a new member to the group: "+res[res.length -1]),msg.getTarget());
            }
        }
    }

    synchronized void printStatus(String sender, HashMap<String, ServerClientThread> clients) {
        clients.get(sender).sendToSocket(new Message(8,"",database.printStatus(sender)),sender);
    }

    synchronized void groupMessage(Message msg, HashMap<String, ServerClientThread> clients, String sender) {
        new Thread(() -> {
            try {
                Scanner scanner = new Scanner(new FileInputStream("./resources/groups"));

                while (scanner.hasNextLine())
                {
                    String s = scanner.nextLine();
                    if (s.length()>0)
                    {
                        if (s.substring(0,s.indexOf("|")).compareTo(msg.getTarget())==0)
                        {
                            String[] messages = s.substring(s.indexOf("|")+1).split(",");

                            for (String string: messages)
                            {
                                if (clients.containsKey(string) && string.compareTo(sender)!=0)
                                    clients.get(string).sendToSocket(new Message(0,string,"("+sender+") "+msg.getContent()),msg.getTarget());
                            }
                        }
                    }

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }).start();
    }
}