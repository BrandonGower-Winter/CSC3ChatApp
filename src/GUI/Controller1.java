package GUI;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

public class Controller1 {
    @FXML private JFXTabPane tabPane;
    @FXML private JFXTextArea chatSpace;

    private Tab groups, chats, contacts;
    JFXListView<Label> groupList = new JFXListView<>();
    JFXListView<Label> chatsList = new JFXListView<>();
    JFXListView<Label> contactsList = new JFXListView<>();

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
                "Me: Wassup"+
                "User1: Hi\n"+
                "User2: Hey\n"+
                "Me: Wassup\n"+
                "User1: Hi\n"+
                "User2: Hey\n"+
                "Me: Wassup");




    }
}
