import javafx.application.Application;
/**
 * Designed to support encryption of passwords
 * */
class Bridge
{

    public static String user ="";
    public static String pass ="";

    static void runGUI()
    {
        new Thread(() -> Application.launch(Main.class)).start();
    }

    static boolean login(String text, String userPassText)
    {
        user = text;
        pass = userPassText;
        return ClientApplication.login(text,userPassText);
    }

    static boolean reg(String text, String text1)
    {
        user = text;
        pass = text1;
        return ClientApplication.registration(text,text1);
    }
}
