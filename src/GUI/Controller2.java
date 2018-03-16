package GUI;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;


public class Controller2 {

    @FXML JFXTextField a,b,c,d,e;
    @FXML JFXButton f;
    @FXML public void AddFriend(ActionEvent event) {
        if (true)
        {
            a.clear();
        }
        else
        {
            a.clear();
        }
    }

    @FXML public void createFriend(ActionEvent event) {
        if (true)
        {
            b.clear();
        }
        else
        {
            b.clear();
        }
    }

    @FXML public void addFriendToGroup(ActionEvent event) {
        if (true)
        {
            c.clear();
        }
        else
        {
            c.clear();
        }
    }

    @FXML public void RemoveFriend(ActionEvent event) {
        if (true)
        {
            d.clear();
        }
        else
        {
            d.clear();
        }
    }

    @FXML public void removeFriendFromGroup(ActionEvent event) {
        if (true)
        {
            e.clear();
        }
        else
        {
            e.clear();
        }
    }

    @FXML public void logout(ActionEvent event) {
        //TODO Save data and notify other clients that user has gone offline
        Platform.exit();
    }
}
