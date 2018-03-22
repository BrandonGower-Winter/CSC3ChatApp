import javafx.application.Application;

class Bridge
{
    public static String user ="";

    public static String flag ="";
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
}
