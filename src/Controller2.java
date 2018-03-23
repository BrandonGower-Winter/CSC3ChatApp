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
        Thread thread =
        new Thread(new Runnable() {
            @Override
            public void run() {
                Controller1.logData();
                Controller1.officialExit();
            }
        });
        thread.start();
        try {
            thread.join();
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}