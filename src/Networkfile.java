import java.util.Scanner;

public class Networkfile
{
  private String fileName;
  private String fileContent;
  private int fileSize;
  private int filePart;

  public Networkfile(String fn, int fs, int fp,String fc)
  {
    this.fileName = fn;
    this.fileContent = fc;
    this.fileSize = fs;
    this.filePart = fp;
  }

  public String toString()
  {
    return fileName + "%" + fileSize + "%" + filePart + "%" +fileContent;
  }

  public String getName()
  {
    return fileName;
  }

  public String getContent()
  {
    return fileContent;
  }

  public int getFileSize()
  {
    return fileSize;
  }

  public int getFilePart()
  {
    return filePart;
  }

  public static Networkfile parseNetworkFile(String toParse)
  {
      Scanner scMessage = new Scanner(toParse).useDelimiter("%");
      return new Networkfile(scMessage.next(),scMessage.nextInt(),scMessage.nextInt(),scMessage.next());
  }
}
