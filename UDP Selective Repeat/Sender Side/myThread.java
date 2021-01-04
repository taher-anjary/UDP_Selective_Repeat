import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class myThread extends Thread
{
   //PROPERTIES
   int seq_no;
   boolean ACKed = false;
   int timeout;
   byte[] data;
   byte[] header = new byte[2];
   byte[] payload;
   DatagramPacket packet;
   DatagramSocket socket;
   InetAddress IPAddress;
   int port;
   
   //CONSTRUCTOR
   myThread(int no, byte[] data, int timeout, DatagramSocket socket, InetAddress IPAddress, int port)
   {
      seq_no = no;
      this.data = data;
      this.timeout = timeout;
      this.socket = socket;
      this.IPAddress = IPAddress;
      this.port = port;
      
      //convert segment number into 2 bytes
      short seq_num = (short)seq_no;
      header[1] = (byte)(seq_num & 0xff);
      header[0] = (byte)((seq_num >> 8) & 0xff);
      
      //concatenate sequence number header and data into payload bytes
      try {  ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
             outputStream.write( header );
             outputStream.write( data );
             payload = outputStream.toByteArray( ); 
         
      } catch( IOException e ) { e.printStackTrace(); }
      
      //make packet
      packet = new DatagramPacket(payload, payload.length, IPAddress, port);
      
      
   }
   
   // IMPORTANT RUN METHOD
   public void run()
   {
      try
      {
         while(!ACKed)
         {  
            try{  socket.send(packet);  }catch( IOException e ){  e.printStackTrace();  }
            Thread.sleep(timeout);
         }
      }
      catch(InterruptedException e)
      { 
         ACKed = true; 
         return; 
      }
   }
   
}