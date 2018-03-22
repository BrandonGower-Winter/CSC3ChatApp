import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;


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

    @FXML public void logout(ActionEvent event) {
        //TODO Save data and notify other clients that user has gone offline
        Platform.exit();
        System.exit(0);
    }

}
