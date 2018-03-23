import java.util.Scanner;
/*
* Network file class object. Used to store data about a file being sent over the network
* @author Brandon
 */
public class Networkfile
{
  //Local Variables
  private String fileName;
  private String fileContent;
  private int fileSize;
  private int filePart;
  /*
  * Constructor of Networkfile
  * @param fn The filename
  * @param fs The file size
  * @param fp The position at which this file part belongs in the Networkfile array
  * @param fc The data of the file. Usually in Base64
   */
  public Networkfile(String fn, int fs, int fp,String fc)
  {
    this.fileName = fn;
    this.fileContent = fc;
    this.fileSize = fs;
    this.filePart = fp;
  }
  /*
  * Overridden toString method to return the data of the networkfile in a message ready format
   */
  public String toString()
  {
    return fileName + "%" + fileSize + "%" + filePart + "%" +fileContent;
  }
  /*
  * Gets the name of the file
  * @return String
   */
  public String getName()
  {
    return fileName;
  }
  /*
  * Gets the content of the file
  * @return String
   */
  public String getContent()
  {
    return fileContent;
  }
  /*
  * Gets the size of the entire file
  * @return int
   */
  public int getFileSize()
  {
    return fileSize;
  }
  /*
  *Gets the number to which this file part exists
  * @return int
   */
  public int getFilePart()
  {
    return filePart;
  }
  /*
  * Method to parse string in format <filename>%<filesize>%<filepart><filecontent> to a networkfile object
  * @param toParse The string that need to be parsed
   */
  public static Networkfile parseNetworkFile(String toParse)
  {
      Scanner scMessage = new Scanner(toParse).useDelimiter("%");
      return new Networkfile(scMessage.next(),scMessage.nextInt(),scMessage.nextInt(),scMessage.next());
  }
}
