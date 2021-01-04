import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;


//AUTHOR: TAHER ANJARY 21600329 CS 421
//LAST UPDATED: 24 DECEMBER 2019

class Sender
{  
  public static void main(String[] args) throws Exception
  {  
     //java Sender image.png 60000 10 10
     //python receiver.py 60000 10 0 5
     //GET CMD INPUTS
     //Scanner sc = new Scanner(System.in); 
     //System.out.println("Please enter, in order, with single space separation, the following arguments: "); 
     //System.out.println("fileName(or path) recvPort windowN timeout");
     //System.out.println("---------------------------------");
     //String cmdInput = sc.nextLine();
     //System.out.println("---------------------------------");
     //String[] cmdArgs = cmdInput.split(" ");
     String fileName = args[0];                                              //System.out.println(fileName);
     int clientPort = Integer.parseInt(args[1].trim());                      //System.out.println("" + (clientPort+1));
     int N = Integer.parseInt(args[2].trim());                               //System.out.println("" + (N+1));
     int timeout = Integer.parseInt(args[3].trim());                         //System.out.println("" + (timeout+1));
     
     // VARIABLES/CONSTANTS
     int serverPort = 50000;
     DatagramSocket socket = new DatagramSocket();
     socket.setSoTimeout(1);
     byte[] recvBytes = new byte[2];                                            //receive packet 2 byte header, represents ACK number
     InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
     //int clientPort = 60000;
     int ACK_no = 0;
     //int timeout = 10; 
     //int N = 10;
     //String fileName = "image.png";
     byte[] data;                                                               //chunk of 1022 bytes, except for last chunk, used to construct thread
         
     //OBTAIN ALL IMAGE FILE BYTES
     File img = new File(fileName);
     byte[] alldata = Files.readAllBytes(img.toPath());
     System.out.println("file size: " + alldata.length + " Bytes.");
     
     
     // PREP THREADS/CHUNKS
     
     //determine how many segments/threads needed ( note that threads[0] will be null for convenient indexing )
     int need = (int)Math.floor((alldata.length)/1022) + 2;
     System.out.println("Total " + (need-1) + " data packets.");
     myThread[] threads = new myThread[need+1];
     threads[need] = new myThread(need, new byte[]{0}, timeout, socket, IPAddress, clientPort);
     threads[need].ACKed = true;
     
     //divide alldata bytes into chunks, and prep all threads
     int from ;
     int to;
     int i = 1;
     while( i < need )
     {
        from = (i-1)*1022;
        if( i > need-2 ){ to = alldata.length; }else{  to = i*1022;  }
        data = Arrays.copyOfRange(alldata, from, to);
        threads[i] = new myThread(i, data, timeout, socket, IPAddress, clientPort);
        i++;
     }
     
     //BEGIN TRANSMISSION... WITH SELECTIVE REPEAT IMPLEMENTATION
     System.out.println("---------------------------------");
     System.out.println("SENDING...");
     int send_base = 1;
     int nextSeqNum = 1;
     boolean done = false;
     
     //SERIOUSLY, START!
     while(!done)
     {            
        //keep sending packets in window
        while(nextSeqNum < send_base + N & nextSeqNum < need)
        {
           threads[nextSeqNum].start();
           nextSeqNum++;
        }
        
        //receive ACKs and extract ACK number
        DatagramPacket recvPacket = new DatagramPacket(recvBytes, recvBytes.length);
        try{  socket.receive(recvPacket);  }catch(SocketTimeoutException e){}
        ACK_no = (Integer)(((recvBytes[0] & 0xFF) << 8) | (recvBytes[1] & 0xFF));
              
        //mark ACKed packet and stop corresponding thread
        if(ACK_no >= send_base & ACK_no < send_base+N )
        {
           threads[ACK_no].interrupt();
        }      
        
        //update send_base
        while(send_base < need & threads[send_base].ACKed)
        {
           send_base++;
           //stop once all acks have arrived
           if(send_base > need-1){ done = true; }
        }
        
        
     }
     //ALL ACKS RECEIVED
     
     //SEND TERMINATION SIGNAL
     byte[] terminalSig = new byte[]{0,0};
     DatagramPacket terminus = new DatagramPacket(terminalSig, terminalSig.length, IPAddress, clientPort);
     try{  socket.send(terminus);  }catch( IOException e ){  e.printStackTrace();  }
     System.out.println("DONE!");
     System.out.println("---------------------------------");

     
     //TO CALCULATE TIME, TO BE USED IN bps CALCULATION
     //long startTime = System.currentTimeMillis();
     //long stoptTime = System.currentTimeMillis();
     //long elapsedTime = stopTime - startTime;
     //System.out.println(elapsedTime);
  }
}

