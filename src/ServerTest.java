package chatAppTester;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {

  public void sendFile(Socket socket) throws IOException{

	  final String FILE_TO_SEND = "/Users/seanpietersen/Desktop/jason.png";
	  
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    OutputStream os = null;
	    ServerSocket servsock = null;
	    
	    try {
	      
	      while (true) {
	        System.out.println("Waiting...");
	        try {
	          
	          System.out.println("Accepted connection : " + socket);
	          
	          // send file
	          File myFile = new File (FILE_TO_SEND);
	          byte [] buffer  = new byte [(int)myFile.length()];
	          fis = new FileInputStream(myFile);
	          bis = new BufferedInputStream(fis);
	          bis.read(buffer,0,buffer.length);
	          os = socket.getOutputStream();
	          System.out.println("Sending " + FILE_TO_SEND + "(" + buffer.length + " bytes)");
	          os.write(buffer,0,buffer.length);
	          os.flush();
	          System.out.println("Done.");
	          bis.close();
	          //os.close();
	        }
	        finally {
	          //if (bis != null) bis.close();
	          //if (os != null) os.close();
	          //if (socket!=null) socket.close();
	        }
	      }
	    }
	    finally {
	     // if (servsock != null) servsock.close();
	    }
	  }
	  
	  
	//*************************DEBUGGING*****************************************************
  	//	SECOND METHOD
/*		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		FileInputStream fis = new FileInputStream(FILE_TO_SEND);
		byte[] buffer = new byte[4096];
		
		while (fis.read(buffer) > 0) {
			dos.write(buffer);
		}
		*/
		//fis.close();
		//dos.close(); 
		
		//*************************DEBUGGING***************************************************
	  
	
  }
  



