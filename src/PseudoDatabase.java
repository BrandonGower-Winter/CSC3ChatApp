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

        //Read group data
          Scanner scGroupFile = new Scanner((new File("./resources/groups")));

          while(scGroupFile.hasNextLine())
          {
              Scanner scGroupLine = new Scanner(scGroupFile.nextLine()).useDelimiter("\\|");
              createGroup(scGroupLine.next(),scGroupLine.next());
              scGroupLine.close();
          }
          scGroupFile.close();

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
            if(userData.get(name).get(1).compareTo("1") == 0)
                return false;

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



    synchronized int addFriend(String user, String friend)
    {

        if (isFriend(user,friend))
        {
            System.out.println((user + " and " + friend + " are already friends."));
            return 0;
        }
        else if (!isFriend(user,friend))
        {
            userData.get(user).add(friend);
            userData.get(friend).add(user);//what if user is offline
            System.out.println(user + " is now friends with " + friend);
            writeNewFriend();
            return 1;
        }
        else
            return 2;
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

    synchronized boolean isFriend(String user, String friend)
    {
        /*
        try {
            Scanner scanner = new Scanner(new FileInputStream("./resources/friends"));
            while (scanner.hasNextLine())
            {
                String s = scanner.nextLine();
                if (s.substring(0,s.indexOf("|")).compareTo(user)==0)
                {
                    s = s.substring(s.indexOf("|")+1);
                    String ss[] = s.split(",");
                    for (String p: ss)
                        if (p.compareTo(friend)==0)
                            return true;
                    return false;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        for(int i = 0; i < userData.get(user).size(); i++)
        {
            if(i != 0 && i != 1)
            {
                if(userData.get(user).get(i).compareTo(friend) == 0)
                    return true;
            }
        }
        return false;
    }

    private synchronized boolean userExists(String user)
    {
        return userData.containsKey(user);
    }

    synchronized ArrayList<String> createGroup(String name, String members, String owner) throws IOException {
        /*
        Scanner scanner = new Scanner(new FileInputStream("./resources/groups"));

        while (scanner.hasNextLine())
        {
            String s = scanner.nextLine();
            if (s.length()>0)
                if (s.substring(0,s.indexOf("|")).compareTo(name)==0)
                    return new ArrayList<>(0);


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
        */

        ArrayList<String> membersList  = new ArrayList<String>();
        membersList.add(owner);
        for(String s : members.split(","))
        {
            if(userData.get(owner).contains(s))
            {
                membersList.add(s);
            }
        }
        groupData.put(name,membersList);
        writeNewGroup(name,membersList);
        return membersList;

    }

    private synchronized  void createGroup(String name, String members) //Specifically for server use
    {
        ArrayList<String> membersList  = new ArrayList<String>();
        for(String s : members.split(","))
        {
            membersList.add(s);

        }
        groupData.put(name,membersList);
    }

    synchronized String[] addMem(String groupName, String owner, String mem) throws IOException {
        /*
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
        */

        if(groupData.containsKey(groupName) && !groupData.get(groupName).contains(mem))
        {
            groupData.get(groupName).add(mem);
            reWriteGroupFile();
            return (String[]) groupData.get(groupName).toArray();
        }
        else
        {
            return null;
        }
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


    private void writeNewGroup(String groupName, ArrayList<String> members)
    {
        try
        {
            String memberString = "";
            for(int i = 0; i < members.size(); i++)
            {
                if(i + 1 == members.size())
                {
                    memberString += members.get(i);
                }
                else
                {
                    memberString += members.get(i) + ",";
                }
            }
            FileWriter userFile = new FileWriter("./resources/groups",true);//True appends to file
            BufferedWriter userFileBuffer = new BufferedWriter(userFile);
            PrintWriter printer = new PrintWriter(userFileBuffer);
            printer.println(groupName + "|" + memberString);
            printer.close();
            userFileBuffer.close();
            userFile.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    }

    private void reWriteGroupFile()
    {
        try
        {
            PrintWriter clearer = new PrintWriter(new File("./resources/groups")); //Clears groups file
            clearer.close();
            for(String groupName : groupData.keySet())
            {
                writeNewGroup(groupName,groupData.get(groupName));
            }
        }
        catch(FileNotFoundException e)
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
