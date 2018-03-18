import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import com.sun.xml.internal.ws.server.sei.MessageFiller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

    private static HashMap<String, String> tempHist = new HashMap<>(0);


    String selectedUser="";

    @FXML public void initialize()
    {

        contactsList.getItems().add(new Label("Broadcast"));
        tempHist.put("Broadcast","");

        for (int x =2; x< MultiUsers.database.getUserData().get(Bridge.user).size(); x++){
            String string = MultiUsers.database.getUserData().get(Bridge.user).get(x);
            contactsList.getItems().add(new Label(string));
            tempHist.put(string,"");
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
        chatSpace.setText("Welcome "+Bridge.user+"!");



        //handling hamburger
        HamburgerSlideCloseTransition burgerTask = new HamburgerSlideCloseTransition(hamburger);
        burgerTask.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
            burgerTask.setRate(burgerTask.getRate()*-1);
            burgerTask.play();
            burgerPressed();
        });


        try {
            VBox box = FXMLLoader.load(getClass().getResource("resources/Drawer.fxml"));
            drawer.setSidePane(box);
        } catch (IOException e) {
            e.printStackTrace();
        }

        headerInfo.setText("Welcome "+Bridge.user+"!");
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



    //TODO bug: no response when selecting chat history
    void switcher(String selected){

        selectedUser = selected;
        chatSpace.setText("");

        headerInfo.setText("Chatting with: "+selected);
        headerInfo.setStyle("-fx-background-color: orange;");

        if (tempHist.get(selectedUser).length()>0)
            chatSpace.appendText(tempHist.get(selectedUser));

        setChats();
    }


    void setChats(){
        for (String key: tempHist.keySet()){
            if (tempHist.get(key).length()>0){
                boolean flag =false;
                for (int x = 0; x< chatsList.getItems().size(); x++){
                    if (chatsList.getItems().get(x).getText().compareTo(key)==0 ){
                        flag =true;
                        break;
                    }
                }
                if (!flag)
                    chatsList.getItems().add(new Label(key));
            }

        }
    }


    @FXML public void messaging() throws IOException {
        if (sendingArea.getText().length()>0)
        {
            ClientApplication.message(selectedUser,sendingArea.getText());
            chatSpace.appendText("\nMe: "+sendingArea.getText());
            tempHist.replace(selectedUser,chatSpace.getText());
            sendingArea.clear();
        }
        setChats();
    }


    public static void receiveMessage(String text, String text1)
    {
        JFXButton button = ((JFXButton)(Main.stage.getScene().getRoot().lookup("#headerInfo")));
        button.setStyle("-fx-background-color: red;");
        ((JFXTextArea)(Main.stage.getScene().getRoot().lookup("#chatSpace"))).appendText(text1);
        tempHist.replace(text,tempHist.get(text)+text1);
    }
}
