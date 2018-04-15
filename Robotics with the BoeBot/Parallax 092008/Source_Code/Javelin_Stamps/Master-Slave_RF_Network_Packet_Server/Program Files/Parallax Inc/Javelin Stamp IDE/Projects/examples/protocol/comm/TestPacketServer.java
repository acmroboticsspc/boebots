import Stamp.protocol.Comm.*;
import stamp.core.*;

/**
 * test PacketServer class.
 *
 * @version 1.0 1/23/2003
 * @author Bill Wong
 */

public class TestPacketServer {
  public static void main() {
    PacketServer packetServer =
      new PacketServer ( PacketServer.noPorts
                                      // port
                       , CPU.pin10    // input pin
                       , CPU.pin11    // output pin
                       , false        // half duplex
                       , true         // data invert
                       , Uart.speed9600 ) ;

    StringBuffer sendBuffer = new StringBuffer(20);
    StringBuffer recvBuffer = new StringBuffer(50);

    sendBuffer.append("test");

    System.out.println("Sending");

    packetServer.setReceiveBuffer(recvBuffer);
    //packetServer.broadcast(sendBuffer) ;
    packetServer.send(sendBuffer) ;
    while(true) {
      if ( packetServer.receiveBufferFull()) {
        packetServer.setReceiveBuffer(null);
        System.out.println(packetServer.receivedBroadcast()
                          ?"Got broadcast"
                          :"Got buffer");
//        System.out.println(packetServer.getSourcePort());
//        System.out.println(packetServer.getDestinationPort());
        // break ;
      }

      if ( packetServer.bufferSent()) {
        System.out.println(packetServer.sendError()
                          ?"Error"
                          :"Received ack");
        break;
      }
    }
    System.out.println("All done");
  }
}