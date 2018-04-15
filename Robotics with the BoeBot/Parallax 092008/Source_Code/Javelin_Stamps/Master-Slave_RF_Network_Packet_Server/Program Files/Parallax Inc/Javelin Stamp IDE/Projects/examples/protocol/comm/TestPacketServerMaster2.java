import Stamp.protocol.Comm.*;
import stamp.core.*;

/**
 * test PacketServer class.
 *
 * @version 1.0 1/23/2003
 * @author Bill Wong
 */

public class TestPacketServerMaster2 {
  public static void main() {
    PacketServer packetServer =
      new PacketServer ( 1            // receive port
                       , CPU.pin15    // input pin
                       , CPU.pin14    // output pin
                       , false        // half duplex
                       , true         // data invert
                       , Uart.speed9600 ) ;
    packetServer.listen(4);
    packetServer.listen(5);
    boolean sending = true ;

    System.out.println("Starting") ;

    StringBuffer sendBuffer = new StringBuffer(100);
    StringBuffer recvBuffer = new StringBuffer(100);

    packetServer.setReceiveBuffer(recvBuffer);

    sendBuffer.append ( "Broadcast message" ) ;

    packetServer.broadcast(2,sendBuffer);

    for ( int i = 0 ; i < 2 ; ) {
      if ( packetServer.receiveBufferFull()) {
        System.out.print ( "Got " );
        if ( packetServer.receivedBroadcast()) {
          System.out.print ( "broadcast <" );
          System.out.print ( recvBuffer.toString ());
        } else {
          System.out.print ( "send <" );
          System.out.print ( recvBuffer.toString ());
        }

        System.out.print ( "> from " );
        System.out.print ( packetServer.getSourcePort());
        System.out.print ( "," );
        System.out.println ( packetServer.getDestinationPort());

        packetServer.setReceiveBuffer(null);

        ++i;
        sendBuffer.clear() ;
        sendBuffer.append ( "Send message" ) ;
        packetServer.send(4,2,sendBuffer);
        sending = true ;
      }

      if ( sending && packetServer.bufferSent()) {
        sending = false ;
        System.out.println("Buffer sent");
        packetServer.setReceiveBuffer(recvBuffer);
      }
    }

    System.out.println("All done") ;
  }
}