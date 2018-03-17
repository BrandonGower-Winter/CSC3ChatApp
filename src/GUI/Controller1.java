package GUI;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class Controller1 {
    @FXML private JFXTabPane tabPane;
    @FXML private JFXTextArea chatSpace;
    @FXML private JFXHamburger hamburger;
    @FXML private HBox hbox;
    @FXML private JFXDrawer drawer;
    @FXML private JFXButton headerInfo;
    @FXML private JFXTextField sendingArea;
    private Tab groups, chats, contacts;
    private JFXListView<Label> groupList = new JFXListView<>();
    private JFXListView<Label> chatsList = new JFXListView<>();
    private JFXListView<Label> contactsList = new JFXListView<>();

    @FXML public void initialize()
    {

        //TODO initialize user profile --get user contacts, chats and groups
        contactsList.getItems().add(new Label("Broadcast"));//do not remove, user selects this option for a broadcast
        //remove
        for (int x = 0; x< 20; x++){
            contactsList.getItems().add(new Label("User"+x));
        }
        //remove
        for (int x = 0; x< 8; x++){
            chatsList.getItems().add(new Label("UserAlias_"+x));
        }
        //remove
        for (int x = 0; x< 3; x++){
            groupList.getItems().add(new Label("Group"+x));
        }




        groups = new Tab("Groups");
        groups.setContent(groupList);

        chats = new Tab("Chats");
        chats.setContent(chatsList);

        contacts = new Tab("Contacts");
        contacts.setContent(contactsList);

        tabPane.getTabs().addAll(contacts, chats, groups);


        groupList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switcher(newValue.getText());
        });

        chatsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switcher(newValue.getText());
        });

        contactsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switcher(newValue.getText());
        });



        //chatspace
        chatSpace.setText("Welcome");



        //handling hamburger
        HamburgerSlideCloseTransition burgerTask = new HamburgerSlideCloseTransition(hamburger);
        burgerTask.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
            burgerTask.setRate(burgerTask.getRate()*-1);
            burgerTask.play();
            burgerPressed();
        });


        try {
            VBox box = FXMLLoader.load(getClass().getResource("Drawer.fxml"));
            drawer.setSidePane(box);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    void burgerPressed(){
        if (drawer.isShown())
        {
            drawer.close();
            hbox.setPrefWidth(660);//not working
        }
        else
        {
            drawer.open();
        }
    }



    //TODO get current chat history and paste output to chatspace
    void switcher(String selected){
        System.out.println("Selected string: "+selected);


        chatSpace.setText(
                selected+" is selected\n"+
                "User1: Hi\n"+
                "User2: Hey\n"+
                "Me: Wassup\n"+
                "User1: Hi\n"+
                "User2: Hey\n"+
                "Me: Wassup\n"+
                "User1: Hi\n"+
                "User2: Hey\n"+
                "Me: Wassup\n"+
                "User1: Hi\n"+
                "User2: Hey\n"+
                "Me: Wassup\n"+
                "User1: Hi\n"+
                "User2: Hey\n"+
                "Me: Wassup\n"+
                "User1: Hi\n"+
                "User2: Hey\n"+
                "Me: Wassup");

        //TODO Update Header with relevant information
        headerInfo.setText(selected);
    }

    @FXML public void messaging()
    {
        if (sendingArea.getText().length()>0)
        {
            chatSpace.appendText("\nMe: "+sendingArea.getText());
            sendingArea.clear();
        }

        //TODO send message to specific users
    }
}
