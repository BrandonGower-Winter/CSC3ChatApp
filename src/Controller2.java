import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;


public class Controller2 {

    @FXML JFXTextField a,b,c,d,e;
    @FXML JFXButton f;
    @FXML public void AddFriend(ActionEvent event) throws IOException {
        ClientApplication.addFriend(Bridge.user,a.getText());//TODO notify user whether the friend is added or not
        a.clear();//TODO ensure that friend is registered before adding
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
