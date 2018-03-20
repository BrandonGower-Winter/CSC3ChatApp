

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTest {

  public final static String FILE_TO_RECEIVED = "/Users/seanpietersen/Desktop/TEMPFOLDER/jason.png";  
                                                          
  public final static int FILE_SIZE = 6022386;
  
  public void saveFile(Socket socket) throws UnknownHostException, IOException{
	  
	    int bytesRead;
	    int current = 0;
	    FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
	    try {
	    	
	      System.out.println("Connecting...");

	      // receive file
	      byte [] buffer  = new byte [FILE_SIZE];
	      InputStream is = socket.getInputStream();
	      fos = new FileOutputStream(FILE_TO_RECEIVED);
	      bos = new BufferedOutputStream(fos);
	      bytesRead = is.read(buffer,0,buffer.length);
	      current = bytesRead;
	      
	      do {
	         bytesRead =
	            is.read(buffer, current, (buffer.length-current));
	         if(bytesRead >= 0){
	        	 current += bytesRead;
	        	 System.out.println(current);
	        	 }
	         
	      } while(bytesRead > -1);
	      
	      bos.write(buffer, 0 , current);
	      bos.flush();
	      System.out.println("File " + FILE_TO_RECEIVED
	          + " downloaded (" + current + " bytes read)");
	    }
	    finally {
	     // if (fos != null) fos.close();
	      //if (bos != null) bos.close();
	      //if (sock != null) sock.close();
	    }
	  }
	  
	//*************************DEBUGGING*****************************
	  //SECOND METHOD	
/*	  
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		FileOutputStream fos = new FileOutputStream(FILE_TO_RECEIVED);
		byte[] buffer = new byte[4096];
		
		int filesize = 15123;
		int read = 0;
		int totalRead = 0;
		int remaining = filesize;
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			fos.write(buffer, 0, read);
		}*/
		
		//fos.close();
		//dis.close();
		
	//*************************DEBUGGING*****************************
}
  
