import Stamp.protocol.Comm.*;
import stamp.core.*;

/**
 * test PacketServer class.
 *
 * @version 1.0 1/23/2003
 * @author Bill Wong
 */

public class TestPacketServerMaster {
  public static void main() {
    PacketServer packetServer =
      new PacketServer ( PacketServer.noPorts
                                          // ports
                       , CPU.pin15    // input pin
                       , CPU.pin14    // output pin
                       , false        // half duplex
                       , true         // data invert
                       , Uart.speed9600 ) ;
    boolean sending = true ;

    System.out.println("Starting") ;

    StringBuffer sendBuffer = new StringBuffer(100);
    StringBuffer recvBuffer = new StringBuffer(100);

    packetServer.setReceiveBuffer(recvBuffer);

    sendBuffer.append ( "Broadcast message" ) ;

    packetServer.broadcast(sendBuffer);

    for ( int i = 0 ; i < 2 ; ) {
      if ( packetServer.receiveBufferFull()) {
        packetServer.setReceiveBuffer(null);
        if ( packetServer.receivedBroadcast()) {
          System.out.print ( "Got broadcast <" );
          System.out.print ( recvBuffer.toString ());
          System.out.println ( ">" );
        } else {
          System.out.print ( "Got send <" );
          System.out.print ( recvBuffer.toString ());
          System.out.println ( ">" );
        }

        ++i;
        sendBuffer.clear() ;
        sendBuffer.append ( "Send message" ) ;
        packetServer.send(sendBuffer);
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