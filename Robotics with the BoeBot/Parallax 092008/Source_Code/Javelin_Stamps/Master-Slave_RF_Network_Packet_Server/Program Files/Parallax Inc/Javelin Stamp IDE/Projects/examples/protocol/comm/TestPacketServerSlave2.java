import Stamp.protocol.Comm.*;
import stamp.core.*;

/**
 * test PacketServer class.
 *
 * @version 1.0 1/23/2003
 * @author Bill Wong
 */

public class TestPacketServerSlave2 {
  public static void main() {
    PacketServer packetServer =
      new PacketServer ( 2            // receive port
                       , CPU.pin15    // input pin
                       , CPU.pin14    // output pin
                       , false        // half duplex
                       , true         // data invert
                       , Uart.speed9600 ) ;
    boolean sending = false ;
    int source, destination ;

    StringBuffer buffer = new StringBuffer(100);

    packetServer.setReceiveBuffer(buffer);

    while(true) {
      if ( packetServer.receiveBufferFull()) {
        packetServer.setReceiveBuffer(null);

        source=packetServer.getSourcePort();
        destination=packetServer.getDestinationPort();

        System.out.print("Received <");
        System.out.print(buffer.toString());
        System.out.print("> from " );
        System.out.print(source);
        System.out.print("," );
        System.out.println(destination);

        if ( packetServer.receivedBroadcast()) {
          packetServer.send(4,buffer);
        } else {
          packetServer.broadcast(5,buffer);
        }

        sending = true ;
      }

      if ( sending && packetServer.bufferSent()) {
        sending = false ;
        packetServer.setReceiveBuffer(buffer);
      }
    }
  }
}