/*
* Simple a object used to store data received from messages sent over the sockets.
* @author Brandon
 */
public class Message
{
  //Local Variables
  private int command;
  private String target;
  private String content;

  /*
  *Constructor used to initialize message object
  * @param c Code value of message
  * @param t Target value of message
  * @param cont Content of message
   */
  public Message(int c, String t, String cont)
  {
    command = c;
    target = t;
    content = cont;
  }
  /*
  * Default constructor
   */
  public Message()
  {
    
  }
  /*
  * Returns the command code
  * @return int
   */
  public int getCommand()
  {
    return command;
  }
  /*
  * Returns the target of the message object
  * @return String
   */
  public String getTarget()
  {
    return target;
  }
  /*
  * Returns the content of the message object
   */
  public String getContent()
  {
    return content;
  }
  /*
   * Overridden toString function that returns the message in correct format to be sent by sockets
   * @return String
   */
  public String toString()
  {
    return command + "|" + target + "|" + content;
  }
  /*
  * Sets the target value of the message
  * @param t New target of message
   */
  public void setTarget(String t)
  {
    target = t;
  }
  /*
   * Sets the content value of the message
   * @param content New content of message
   */
  public void setContent(String content) {
    this.content = content;
  }
}
