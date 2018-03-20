import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;

public class PseudoDatabase {

    //Used in database
    public static HashMap<String,ArrayList<String>> userData = new HashMap<>(0);
    private HashMap<String,ArrayList<String>> groupData = new HashMap<>(0);

    PseudoDatabase()
    {
      try
      {
        //Adding users
        Scanner scUserFile = new Scanner(new File("./resources/users"));
        while(scUserFile.hasNextLine())
        {
          Scanner scLine = new Scanner(scUserFile.nextLine()).useDelimiter("\\|");
          register(scLine.next(),scLine.next(),true);
          scLine.close();
        }
        scUserFile.close();

        //Giving the users friends.
        Scanner scFriendsFile = new Scanner(new File("./resources/friends"));
        while(scFriendsFile.hasNextLine())
        {
          Scanner scLine = new Scanner(scFriendsFile.nextLine()).useDelimiter("\\|");
          String username = scLine.next();
          Scanner scFriends = new Scanner(scLine.next()).useDelimiter(",");
          while(scFriends.hasNext())
          {
            addFriend(username,scFriends.next(),true);
          }
          scLine.close();
          scFriends.close();
        }
        scFriendsFile.close();
        //Read group data here

      }
      catch(FileNotFoundException e)
      {
        System.out.println("Users database not found");
      }
    }

    synchronized boolean register(String name, String password,boolean fromFile)
    {
        if (userData.containsKey(name))
            return false;

        else
        {
            ArrayList<String> flag = new ArrayList<>(0);
            flag.add(password);
            flag.add("0");
            userData.put(name,flag);
            if(!fromFile)
              writeNewUser(name,password);
            System.out.println("Registered User " + name + " with password " + password);
            return true;
        }
    }



    synchronized boolean login(String name, String password)
    {
        if (userData.containsKey(name) && userData.get(name).get(0).compareTo(password)==0)
        {
            userData.get(name).set(1,"1");
            return true;
        }
        else
            return false;
    }



    synchronized boolean logout(String name, String password)
    {
        if (userData.containsKey(name) && userData.get(name).get(0).compareTo(password)==0){
            userData.get(name).set(1,"0");
            return true;
        }
        else
            return false;

    }



    synchronized boolean addFriend(String user, String friend)
    {
        if (!isFriend(user,friend) && userData.get(user).get(1).compareTo("1")==0)
        {
            userData.get(user).add(friend);
            System.out.println(user + " is now friends with " + friend);
            writeNewFriend();
            return true;
        }
        else
            return false;
    }


    //Used on startup to setup friends list
    private synchronized  boolean addFriend(String user,String friend, boolean isServer)
    {
      if (userData.containsKey(friend) && userData.containsKey(user))
      {
          userData.get(user).add(friend);
          System.out.println(user + " is now friends with " + friend);
          return true;
      }
      else
          return false;
    }

    public synchronized boolean isFriend(String user, String friend)
    {
      if(userData.containsKey(friend) && userData.containsKey(user))
      {
        if(userData.get(user).contains(friend))
        {
          return true;
        }
        return false;
      }
      else
      {
        return false;
      }
    }

    synchronized ArrayList<String> createGroup(String name, String members, String owner) throws IOException {
        Scanner scanner = new Scanner(new FileInputStream("./resources/groups"));

        while (scanner.hasNextLine())
        {
            String s = scanner.nextLine();
            if (s.length()>0)
            {
                if (s.substring(0,s.indexOf("|")).compareTo(name)==0)
                {
                    System.out.println("group already exists");
                    return new ArrayList<>(0);
                }
            }

        }


        System.out.println("members: "+members);
        String[] strings = members.split(",");
        ArrayList<String> flag = new ArrayList<>(0);
        flag.add(owner);
        String mem = owner+",";
        for (String s: strings){
            if (userData.get(owner).contains(s)) {
                flag.add(s);
                mem+=s;
            }
        }

        FileWriter userFile = new FileWriter("./resources/groups",true);//True appends to file
        BufferedWriter userFileBuffer = new BufferedWriter(userFile);
        PrintWriter printer = new PrintWriter(userFileBuffer);
        printer.println(name + "|" + mem);
        printer.close();
        userFileBuffer.close();
        userFile.close();



        return flag;

    }



    synchronized String[] addMem(String groupName, String owner, String mem) throws IOException {

        Scanner scanner = new Scanner(new FileInputStream("./resources/groups"));

        boolean bool = false;
        int pos = 0;
        int count =0;
        String[] res = null;
        ArrayList<String> list = new ArrayList<>(0);
        while (scanner.hasNextLine())
        {
            String s = scanner.nextLine();
            if (s.length()>0)
            {
                if (s.substring(0,s.indexOf("|")).compareTo(groupName)==0 && userData.get(owner).contains(mem))
                {
                    s+=mem+",";
                    bool = true;
                    System.out.println("group exists");
                    String flag = s.substring(s.indexOf("|")+1);
                    res = flag.split(",");

                }
            }
            list.add(s);
            count++;
        }


        if (bool)
        {
            //not a pretty solution

            FileWriter userFile = new FileWriter("./resources/groups",false);
            BufferedWriter userFileBuffer = new BufferedWriter(userFile);
            PrintWriter printer = new PrintWriter(userFileBuffer);
            for (String string: list)
            {
                printer.println(string);
            }
            printer.close();
            userFileBuffer.close();
            userFile.close();

        }

        return res;
    }



    synchronized String printStatus(String userName)
    {
        String string = "\n";
        string+="Username: "+userName+"\nStatus: "+userData.get(userName).get(1)+"\nFriends: ";
        for (int x = 2; x<userData.get(userName).size(); x++)
        {
            string+=userData.get(userName).get(x)+", ";
        }
        string+="\nGroups: ";
        for (String p: groupData.keySet())
            if (groupData.get(p).contains(userName))
                string+= p+", ";

        return string;
    }


    private void writeNewUser(String user, String password)
    {
      try
      {
        FileWriter userFile = new FileWriter("./resources/users",true);//True appends to file
        BufferedWriter userFileBuffer = new BufferedWriter(userFile); //Use bw because writing to files is expensive;
        PrintWriter printer = new PrintWriter(userFileBuffer);

        printer.println(user + "|" + password);

        printer.close();
        userFileBuffer.close();
        userFile.close();
      }
      catch(IOException e)
      {
        e.printStackTrace();
      }
    }

    private void writeNewFriend()
    {
      try
      {
        FileWriter userFile = new FileWriter("./resources/friends",false);//false clears file
        BufferedWriter userFileBuffer = new BufferedWriter(userFile); //Use bw because writing to files is expensive;
        PrintWriter printer = new PrintWriter(userFileBuffer);

        for(String user : userData.keySet())
        {
          String toPrint = user + "|";
          ArrayList<String> userFriends = userData.get(user);
          if(userFriends.size() < 3)
          {
            continue;
          }
          for(int i = 0; i < userFriends.size(); i++)
          {
            if(i==0 || i== 1)
            {
              continue;
            }
            else if(i == userFriends.size()-1)
            {
              toPrint += userFriends.get(i);
            }
            else
            {
              toPrint += userFriends.get(i) + ",";
            }
          }
          printer.println(toPrint);
        }

        printer.close();
        userFileBuffer.close();
        userFile.close();
      }
      catch(IOException e)
      {
        e.printStackTrace();
      }
    }

    public HashMap<String, ArrayList<String>> getUserData() {
        return userData;
    }

    public HashMap<String, ArrayList<String>> getGroupData() {
        return groupData;
    }
}
