import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application
{

    static  Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        //primaryStage.initStyle(StageStyle.TRANSPARENT);
        Parent root = FXMLLoader.load(getClass().getResource("resources/login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        stage = primaryStage;
    }


}
