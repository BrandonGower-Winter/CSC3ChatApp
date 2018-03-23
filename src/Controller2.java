import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;


public class Controller2 {

    @FXML JFXTextField a,b,c;
    @FXML public void AddFriend(ActionEvent event) throws IOException {
        ClientApplication.addFriend(Bridge.user,a.getText());
        a.clear();
    }

    @FXML public void createGroup(ActionEvent event) throws IOException {
        ClientApplication.createGroup(b.getText(),Bridge.user);
        b.clear();
    }

    @FXML public void addFriendToGroup(ActionEvent event) throws IOException {
        ClientApplication.addGroupMember(c.getText());
        c.clear();
    }

    @FXML public void logout(ActionEvent event)
    {
        Platform.exit();
        new Thread(new Runnable() {
            @Override
            public void run() {
                logData();
            }
        }).start();
    }
    static void logData()
    {
        HashMap<String, String> hashMap = Controller1.tempHist;

        try
        {
            FileWriter userFile = new FileWriter("./resources/chats"+Bridge.user+"data",false);
            BufferedWriter userFileBuffer = new BufferedWriter(userFile);
            PrintWriter printer = new PrintWriter(userFileBuffer);

            for(String user : hashMap.keySet())
            {
                if (user.contains("*"))
                {
                    printer.print("##$"+user+hashMap.get(user)+"\n");
                }

                else if (user.contains("Broadcast"))
                {
                    printer.print("##^"+user+hashMap.get(user)+"\n");
                }
                else
                {
                    printer.print("##@"+user+hashMap.get(user)+"\n");
                }

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

}
