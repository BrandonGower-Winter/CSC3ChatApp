//Simply a data container;


public class Message
{
  private int command;
  private String target;
  private String content;


  public Message(int c, String t, String cont)
  {
    command = c;
    target = t;
    content = cont;
  }

  public Message()
  {
    
  }

  public int getCommand()
  {
    return command;
  }

  public String getTarget()
  {
    return target;
  }

  public String getContent()
  {
    return content;
  }

  public String toString()
  {
    return command + "|" + target + "|" + content;
  }

  public void setTarget(String t)
  {
    target = t;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
