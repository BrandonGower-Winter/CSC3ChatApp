package GUI;

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
        //TODO have to check with the databse whether login credentials are valid

        if (userN.getText().compareTo("user")==0 && userPass.getText().compareTo("password")==0)
        {
            try
            {
                ((Stage) ((Node)event.getSource()).getScene().getWindow()).setScene(new Scene(FXMLLoader.load(getClass().getResource("Home.fxml"))));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            userN.clear();
            userPass.clear();
        }

    }


}
