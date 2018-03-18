import javafx.application.Application;

class Bridge
{
    public static String user ="";
    static void runGUI(){
        new Thread(() -> Application.launch(Main.class)).start();
    }

    static boolean login(String text, String userPassText){
        user = text;
        return ClientApplication.login(text,userPassText);
    }

    static boolean reg(String text, String text1) {
        user = text;
        return ClientApplication.registration(text,text1);
    }

    //TODO get user groups


    //TODO get status on whether the user is online or offline


    //TODO send file to user


    //TODO add a friend

    //TODO create a group


    //TODO add a friend to a group

    //TODO remove friend

    //TODO remove friend from a group

    //TODO logout
}
