import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Controller for the Home.fxml file
 * */

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

    static HashMap<String, String> tempHist = new HashMap<>(0);
    private static String selectedUser="";


    @FXML public void initialize()
    {
        //saves current user data and notifies server before closing the application
        Main.stage.setOnCloseRequest(event -> {
            logData();
            officialExit();
        });

        ClientApplication.requestData(Bridge.user);

        contactsList.getItems().add(new Label("Broadcast"));
        tempHist.put("Broadcast","");


        groups = new Tab("Groups");
        groups.setContent(groupList);

        chats = new Tab("Chats");
        chats.setContent(chatsList);

        contacts = new Tab("Contacts");
        contacts.setContent(contactsList);

        tabPane.getTabs().addAll(contacts, chats, groups);



        groupList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> switcher(observable.getValue().getText()));

        chatsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> switcher(observable.getValue().getText()));

        contactsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> switcher(observable.getValue().getText()));


        chatSpace.setText("Welcome "+Bridge.user+"!");



        //Revealing more options to user
        HamburgerSlideCloseTransition burgerTask = new HamburgerSlideCloseTransition(hamburger);
        burgerTask.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->
        {
            burgerTask.setRate(burgerTask.getRate()*-1);
            burgerTask.play();
            burgerPressed();
        });


        //sets up drawer from a different fxml file with a different controller
        try
        {
            VBox box = FXMLLoader.load(getClass().getResource("resources/Drawer.fxml"));
            drawer.setSidePane(box);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        headerInfo.setText("Welcome "+Bridge.user+"!");
        refresh();
    }



    void burgerPressed(){
        if (drawer.isShown())
        {
            drawer.close();
            hbox.setPrefWidth(660);//bug: not responding
        }
        else
        {
            drawer.open();
        }
    }

    void switcher(String selected){
        sendingArea.clear();

        selectedUser = selected;
        chatSpace.setText("");

        headerInfo.setText("Chatting with: "+selected);
        headerInfo.setStyle("-fx-background-color: orange;");

        if (tempHist.containsKey(selectedUser))
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


    @FXML void refresh()
    {
        ClientApplication.requestData(Bridge.user);

        //TODO add new friends
        for (String string: tempHist.keySet())
        {
            if (string.substring(0,1).compareTo("*")!=0)
            {
                {
                    boolean Nomatch = true;
                    for (Label label: contactsList.getItems())
                    {
                        if (label.getText().compareTo(string)==0)
                        {
                            Nomatch = false;
                        }
                    }

                    if (Nomatch)
                    {
                        contactsList.getItems().add(new Label(string));
                    }
                }
            }

        }



        //handles group refreshes
        for (String string: tempHist.keySet())
        {
            if (string.substring(0,1).compareTo("*")==0)
            {
                boolean Nomatch = true;
                for (Label label: groupList.getItems())
                {
                    if (label.getText().compareTo(string)==0)
                    {
                        Nomatch = false;
                    }
                }

                if (Nomatch)
                {
                    groupList.getItems().add(new Label(string));
                }
            }
        }
        setChats();
    }





    static void receiveMessage(String text, String text1)
    {
        JFXButton button = ((JFXButton)(Main.stage.getScene().getRoot().lookup("#headerInfo")));

        if (button!=null && text.compareTo("W1001")!=0 && text.compareTo("W1000")!=0)
        {
            if ((text1.contains("Group already exists!"))) {
                Platform.runLater(() -> {
                    button.setText("Group already exists!");
                    button.setStyle("-fx-background-color: white;");
                });
                return;         //order matters for this block
            }

            button.setStyle("-fx-background-color: red;");
            Platform.runLater(() -> button.setText("New Message from: "+text));

            if (tempHist.containsKey(text))
                tempHist.replace(text,tempHist.get(text)+text1);


            if (text.substring(0,1).compareTo("*")==0 && !tempHist.containsKey(text))
            {
                button.setStyle("-fx-background-color: blue;");
                if (!tempHist.containsKey(text))
                {
                    tempHist.put((text),text1);
                }
            }

            if (selectedUser.compareTo(text)==0)
                Platform.runLater(() ->
                {
                    JFXTextArea textArea = (JFXTextArea)Main.stage.getScene().getRoot().lookup("#chatSpace");
                    textArea.appendText(text1);
                });


            if ((text.compareTo("0000")==0))
                Platform.runLater(() -> {
                    button.setText("Offline at the moment... :(");
                    button.setStyle("-fx-background-color: white;");
                });
            else if ((text.compareTo("0001")==0))
            {
                Platform.runLater(() -> {
                    button.setText("You were friends before now... :)");
                    button.setStyle("-fx-background-color: white;");
                });
            }
            else if ((text.compareTo("0002")==0))
            {
                Platform.runLater(() -> {
                    button.setText("Successfully added...");
                    button.setStyle("-fx-background-color: blue;");
                });
            }
            else if ((text.compareTo("0003")==0))
            {
                Platform.runLater(() -> {
                    button.setText("Username not found in our servers :(");
                    button.setStyle("-fx-background-color: white;");
                });
            }
            else if ((text.compareTo("0004")==0))
            {
                Platform.runLater(() -> {
                    button.setText(text1.substring(text1.indexOf(":")+1)+" has added you as their friend! (click to refresh)");
                    button.setStyle("-fx-background-color: blue;");
                });
            }

            else if (text1.contains("GROUP"))
            {
                Platform.runLater(() -> {
                    button.setText(text1.substring(0,text1.indexOf(":")+1)+" is a new group! (click to refresh)");
                    button.setStyle("-fx-background-color: blue;");
                });
            }

            else if (text1.contains("Added a new member to the group:"))
            {
                System.out.println("heere");
                Platform.runLater(() -> {
                    button.setText("new group member added (click to refresh)");
                    button.setStyle("-fx-background-color: blue;");
                });
                //order matters for this block
            }
        }
        else {
            if ((text.compareTo("W1000")==0) && text1.compareTo("\nW1000: *")!=0)
            {
                String t = text1.substring(text1.indexOf(":")+2);
                String users[] = t.split("@");
                for (String sr: users)
                {
                    tempHist.put(sr,"");
                }

            }

            else if ((text.compareTo("W1001")==0) && text1.compareTo("\nW1001: *")!=0)
            {
                String t = text1.substring(text1.indexOf(":")+2);
                String group[] = t.split("@");
                for (String sr: group)
                {
                    tempHist.put(sr,"");
                }
            }
        }





    }

    @FXML public void messaging() throws IOException {
        if (selectedUser.length()>0)    //ensures that user has selected a target
        {
            if (sendingArea.getText().length()>0)
            {
                Platform.runLater(() ->
                {
                    Main.stage.getScene().getRoot().lookup("#headerInfo").setStyle("-fx-background-color: orange;");
                    ((JFXButton)Main.stage.getScene().getRoot().lookup("#headerInfo")).setText("Chatting with: "+selectedUser);
                });
                ClientApplication.message(selectedUser,sendingArea.getText());
                chatSpace.appendText("\nMe: "+sendingArea.getText());
                tempHist.replace(selectedUser,chatSpace.getText());
                sendingArea.clear();
            }
            setChats();
        }
        else
        {
            sendingArea.clear();
            sendingArea.setPromptText("Please select a person you want to send a message to");
        }
    }


    @FXML void upload() throws IOException {
        if (selectedUser.length()>0)                    //Ensures that user cannot upload file to unselected user which throws as exception in the server if not handled
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select file");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File file = fileChooser.showOpenDialog(Main.stage);
            Desktop.getDesktop();
            if (file!=null && file.exists())
            {
                ClientApplication.sendFile(selectedUser,file);
                ClientApplication.message(selectedUser,"(SENT YOU A FILE: "+file.getName()+")");
                chatSpace.appendText("\nFILE SENT");
                tempHist.replace(selectedUser,chatSpace.getText());
            }

            else
                System.out.println("No file found to be sent to the user: "+selectedUser);
        }


    }

    static void logData()
    {
        HashMap<String, String> hashMap = Controller1.tempHist;

        try
        {
            FileWriter userFile = new FileWriter("./resources/chats"+Bridge.user+"data",false);
            BufferedWriter userFileBuffer = new BufferedWriter(userFile);
            PrintWriter printer = new PrintWriter(userFileBuffer);

            for(String user : hashMap.keySet())
            {
                if (user.contains("*"))
                {
                    printer.print("##$"+user+hashMap.get(user)+"\n");
                }

                else if (user.contains("Broadcast"))
                {
                    printer.print("##^"+user+hashMap.get(user)+"\n");
                }
                else
                {
                    printer.print("##@"+user+hashMap.get(user)+"\n");
                }

            }

            printer.close();
            userFileBuffer.close();
            userFile.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    static void exiting()
    {
        logData();
        Platform.runLater(() ->
        {
            Main.stage.getScene().getRoot().setDisable(true);
            JFXButton button = ((JFXButton)(Main.stage.getScene().getRoot().lookup("#headerInfo")));
            if (button!=null)
            {
                button.setText("Server is offline- Restart");
                button.setStyle("-fx-background-color: violet;");
            }
        });
    }

    static void officialExit()
    {
        ClientApplication.logOut(Bridge.user,Bridge.pass);
        System.exit(0);
    }
}
