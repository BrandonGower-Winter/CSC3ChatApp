import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PseudoDatabase {
    private HashMap<String,ArrayList<String>> userData = new HashMap<>(0);
    private HashMap<String,ArrayList<String>> groupData = new HashMap<>(0);
    HashMap<String,ArrayList<String>> pendingData = new HashMap<>(0);   //will use this to save data sent to offline users

    public PseudoDatabase()
    {
      ArrayList<String> flag = new ArrayList<>(0);
      flag.add("123");
      flag.add("0");
      userData.put("guest",flag);
    }

    synchronized boolean register(String name, String password)
    {
        if (userData.containsKey(name))
            return false;

        else
        {
            ArrayList<String> flag = new ArrayList<>(0);
            flag.add(password);
            flag.add("0");
            userData.put(name,flag);
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


    /**
     * \TODO Make sure that user is logged on before allowing adding of friends
     * */
    synchronized boolean addFriend(String user, String friend)
    {
        if (userData.containsKey(friend) && userData.containsKey(user) && userData.get(user).get(1).compareTo("1")==0)
        {
            userData.get(user).add(friend);
            return true;
        }
        else
            return false;
    }



    synchronized ArrayList<String> createGroup(String name, String members, String owner)
    {
        if (groupData.containsKey(name))
            return new ArrayList<String>(Collections.singletonList("NO MEMBERS!"));
        else {
            String[] strings = members.split("\\.");
            ArrayList<String> flag = new ArrayList<>(0);
            flag.add(owner);
            for (String s: strings)
                if (userData.get(owner).contains(s))
                    flag.add(s);
            groupData.put(name,flag);
            return flag;
        }
    }



    synchronized boolean addMem(String groupName, String owner, String mem)
    {
        if (groupData.containsKey(groupName) && groupData.get(groupName).get(0).compareTo(owner)==0 && userData.get(owner).contains(mem))
        {
            groupData.get(groupName).add(mem);
            return true;
        }
        else
            return false;
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


    public HashMap<String, ArrayList<String>> getUserData() {
        return userData;
    }

    public HashMap<String, ArrayList<String>> getGroupData() {
        return groupData;
    }

    public HashMap<String, ArrayList<String>> getPendingData() {
        return pendingData;
    }
}
