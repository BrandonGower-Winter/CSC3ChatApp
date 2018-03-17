import java.util.Scanner;

public class Networkfile
{
  String fileName;
  String fileContent;

  public Networkfile(String fn, String fc)
  {
    this.fileName = fn;
    this.fileContent = fc;
  }

  public String toString()
  {
    return fileName + "%" + fileContent;
  }

  public String getName()
  {
    return fileName;
  }

  public static Networkfile parseNetworkFile(String toParse)
  {
      Scanner scMessage = new Scanner(toParse).useDelimiter("%");
      return new Networkfile(scMessage.next(),scMessage.next());
  }
}
