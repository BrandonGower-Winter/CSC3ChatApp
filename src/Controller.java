import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    @FXML JFXTextField userN;
    @FXML JFXPasswordField userPass;

    @FXML void login(ActionEvent event)
    {
        if (Bridge.login(userN.getText(),userPass.getText()))
        {
            try
            {
                ((Stage) ((Node)event.getSource()).getScene().getWindow()).setScene(new Scene(FXMLLoader.load(getClass().getResource("resources/Home.fxml"))));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            userN.clear();
            userN.setPromptText("Failed to login!");
            userPass.clear();
        }

    }

    @FXML void reg(ActionEvent event){
        if (Bridge.reg(userN.getText(),userPass.getText()))
            try
            {
                ((Stage) ((Node)event.getSource()).getScene().getWindow()).setScene(new Scene(FXMLLoader.load(getClass().getResource("resources/Home.fxml"))));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        else
            {
                userN.clear();
                userN.setPromptText("Username taken!");
                userPass.clear();
            }

    }

}
