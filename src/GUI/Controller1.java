package GUI;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class Controller1 {
    @FXML private JFXTabPane tabPane;
    @FXML private JFXTextArea chatSpace;
    @FXML private JFXHamburger hamburger;
    @FXML private HBox hBox;
    @FXML private JFXDrawer drawer;

    private Tab groups, chats, contacts;
    private JFXListView<Label> groupList = new JFXListView<>();
    private JFXListView<Label> chatsList = new JFXListView<>();
    private JFXListView<Label> contactsList = new JFXListView<>();

    @FXML public void initialize()
    {




        //TODO initialize user profile
        //just for viewing purposes
        for (int x = 0; x< 20; x++){
            contactsList.getItems().add(new Label("User"+x));
            chatsList.getItems().add(new Label("User"+x));
            groupList.getItems().add(new Label("Group"+x));
        }


        groups = new Tab("Groups");
        groups.setContent(groupList);

        chats = new Tab("Chats");
        chats.setContent(chatsList);

        contacts = new Tab("Contacts");
        contacts.setContent(contactsList);

        tabPane.getTabs().addAll(contacts, chats, groups);



        //chatspace
        chatSpace.setText("User1: Hi\n"+
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

        ;
    }



    void burgerPressed(){
        if (drawer.isShown())
        {
            drawer.close();
        }
        else
        {
            drawer.open();
        }
    }
}
