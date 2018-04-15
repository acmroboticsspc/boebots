package Stamp.protocol.Comm;

import stamp.core.*;

/**
 * Full duplex, packet oriented communication between Javelins
 * <p>
 * Used to send or receive packets.
 *
 * Must be polled by calling one of the following:
 *   1. poll()
 *   2. bufferSent()
 *   3. bufferReceived()
 *
 * TBA List:
 * 1. expose reset support
 * 2. nak matching headers with bad checksums
 *
 * @version 1.1 02-03-2003  Do not send/update sequence # for broadcast
 * @version 1.0 01-23-2003
 * @author William Wong
 */

public class PacketServer {
  /**
   * Maximum send retry count
   */
  public int maxRetries = 3 ;

  // Header constants

  final static int sendBlock       = 0 ;  // sending n bytes
  final static int sendBlock1      = 1 ;  // sending 1 byte
  final static int sendBlock2      = 2 ;  // sending 2 bytes
  final static int broadcastBlock  = 3 ;  // broadcasting n bytes
  final static int broadcastBlock1 = 4 ;  // broadcasting 1 byte
  final static int broadcastBlock2 = 5 ;  // broadcasting 2 bytes
  final static int ack             = 6 ;  // received data
  final static int resend          = 7 ;  // resend data
  final static int busy            = 8 ;  // no buffer available
  final static int reset           = 9 ;  // reset system

  /**
   * Maximum time (msec) for receiving a packet
   */
  public int  receiveTimeout ;

  /**
   * Maximum time (msec) for a response to sending a packet
   */
  public int  sendTimeout ;

  /**
   * Time (msec) to wait until no more data when flushing input.
   * This should be around 2 character times.
   */
  public int  flushTimeout ;

  /**
   * Source port when using send(StringBuffer);
   */
  public int  defaultSource = 0 ;

  /**
   * Destination port when using send(StringBuffer);
   */
  public int  defaultDestination = 0 ;

  // Protected variables

  /**
   * Non-zero if port addressing is used
   */
  protected int     receivePortFlags ;    // port 0 is bit 0, etc.

  public    Uart    uartIn ;              // communication links
  public    Uart    uartOut ;

  protected Timer   receiveTimer = new Timer ();
  protected Timer   sendTimer = new Timer () ;

  protected boolean halfDuplex ;
  protected int     inPin ;
  protected int     outPin ;
  protected int     hsInPin ;
  protected int     hsOutPin ;
  protected boolean dataInvert ;
  protected boolean hsInvert ;
  protected int     baudRate ;
  protected int     stopBits ;

  protected boolean checkRecvTimeout ;
  protected boolean checkSendTimeout ;

  protected boolean checkReceiver ;
  protected boolean sendDataPacket ;
  protected boolean sendResponsePacket ;

  protected boolean sendError ;
  protected int     sendRetries ;
  protected char    sendHeaderSeq ;
  protected char    sendDestination ;
  protected char    sendSrcDest ;
  protected char    sendChecksum ;
  protected boolean waitForAck ;
  protected boolean waitingForAck ;
  protected boolean sendingPacket ;
  protected char    sendSeqNumbers[] = new char[16];

  protected char    responseHeaderSeq ;
  protected char    responseSrcDest ;

  protected int     receiveState ;
  protected char    receiveHeader ;
  protected char    receiveSeq ;
  protected char    receiveSrcDest ;
  protected char    receiveSize ;
  protected char    receiveData ;
  protected char    receiveChecksum ;
  protected char    receiveSeqNumbers[] = new char[16];


  protected boolean broadcastReceived ;
  protected boolean bufferReceived ;
  protected boolean bufferSent ;        // not used with broadcast

  public StringBuffer recvBuffer ;      // data transfer buffers
  public StringBuffer sendBuffer ;

  static final public int noPorts = -1 ;

  /**
   * Create object that does not use ports.
   *
   * @param inPin pin to use for serial input
   * @param outPin pin to use for serial output
   * @param halfDuplex operate in half duplex mode
   * @param dataInvert the data should be inverted
   * @param baudRate serial port speed
   */
  public PacketServer ( int inPin
                      , int outPin
                      , boolean halfDuplex
                      , boolean dataInvert
                      , int baudRate ) {
    this(noPorts,inPin,outPin,0,0,halfDuplex,dataInvert,false,baudRate,1);
  }

  /**
   * Create object that supports one or more incoming ports.
   *
   * @param port port to accept incoming packets (0 - 15, noPorts)
   * @param inPin pin to use for serial input
   * @param outPin pin to use for serial output
   * @param halfDuplex operate in half duplex mode
   * @param dataInvert the data should be inverted
   * @param baudRate serial port speed
   */
  public PacketServer ( int port
                      , int inPin
                      , int outPin
                      , boolean halfDuplex
                      , boolean dataInvert
                      , int baudRate ) {
    this(port,inPin,outPin,0,0,halfDuplex,dataInvert,false,baudRate,1);
  }

  /**
   * Create object that supports one or more incoming ports.
   *
   * @param port port to accept incoming packets (0 - 15, noPorts)
   * @param inPin pin to use for serial input
   * @param outPin pin to use for serial output
   * @param hsInPin pin to use for input hardware handshaking (0 = none)
   * @param hsOutPin pin to use for output hardware handshaking (0 = none)
   * @param halfDuplex operate in half duplex mode
   * @param dataInvert the data should be inverted
   * @param hsInvert the logic of the handshaking pin should be inverted
   * @param baudRate serial port speed
   * @param stopBits number of stop bits to use
   */
  public PacketServer ( int port
                      , int inPin
                      , int outPin
                      , int hsInPin
                      , int hsOutPin
                      , boolean halfDuplex
                      , boolean dataInvert
                      , boolean hsInvert
                      , int baudRate
                      , int stopBits ) {
    uartIn    = new Uart ( Uart.dirReceive
                         , inPin
                         , dataInvert
                         , hsInPin
                         , hsInvert
                         , baudRate
                         , stopBits ) ;
    if ( halfDuplex ) {
      uartOut = uartIn ;
    } else {
      uartOut = new Uart ( Uart.dirTransmit
                         , outPin
                         , dataInvert
                         , hsOutPin
                         , hsInvert
                         , baudRate
                         , stopBits ) ;
    }

    this.halfDuplex  = halfDuplex ;
    this.inPin       = inPin ;
    this.outPin      = outPin ;
    this.hsInPin     = hsInPin ;
    this.hsOutPin    = hsOutPin ;
    this.dataInvert  = dataInvert ;
    this.hsInvert    = hsInvert ;
    this.baudRate    = baudRate ;
    this.stopBits    = stopBits ;

    receivePortFlags = ( port == noPorts ) ? 0 : ( 1 << port ) ;
    defaultSource    = port ;
    receiveChecksum  = 0 ;
    checkReceiver    = true ;
    receiveState     = recvHeader ;

    // Timeouts based on baudrate values from Uart version 1.3
    receiveTimeout   = baudRate * 10 ;  // timeout while waiting for packet
    sendTimeout      = baudRate * 100 ; // timeout for ack response
    flushTimeout     = baudRate / 2 ;   // timeout for a character
  }

  /**
   * Accept data on an additional port.
   * Normally called after object is created.
   * Port ignored if object not created to support ports.
   * Call listen() before ignore() when switching ports.
   *
   * @param port port number (0-15)
   */
  public void listen ( int port ) {
    if ( receivePortFlags != 0 ) {
      receivePortFlags |= ( 1 << port ) ;
    }
  }

  /**
   * Ignore data on an port.
   * At least one port must be active.
   * This method will do nothing if you try to ignore the last port.
   * Call listen() before ignore() when switching ports.
   *
   * @param port port snumber (0-15)
   */
  public void ignore ( int port ) {
    int newPortFlags = receivePortFlags & ~ ( 1 << port ) ;

    if ( newPortFlags != 0 ) {
      receivePortFlags = newPortFlags ;
    }
  }

  /**
   * Utility function, get high nibble of low byte
   *
   * @param value variable to get nibble from
   *
   * @returns nibble
   */
  static public int getHighNibble ( int value ) {
    return ( value >> 4 ) & 15 ;
  }

  /**
   * Abort send/broadcast if send is pending.
   */
  public void abortSend() {
    if ( sendDataPacket ) {
      sendDataPacket = false ;
      bufferSent     = true ;
      sendError      = true ;
    }
  }

  /**
   * Send data block. Do not wait for acknowledgement.
   *
   * @param buffer StringBuffer of data to send
   */
  public void broadcast ( StringBuffer buffer ) {
    send(defaultSource,defaultDestination,buffer,true);
  }

  /**
   * Send data block. Do not wait for acknowledgement.
   *
   * @param dest destination port
   * @param buffer StringBuffer of data to send
   */
  public void broadcast ( int dest, StringBuffer buffer ) {
    send(defaultSource,dest,buffer,true);
  }

  /**
   * Send data block. Do not wait for acknowledgement.
   *
   * @param source source port
   * @param dest destination port
   * @param buffer StringBuffer of data to send
   */
  public void broadcast ( int source, int dest, StringBuffer buffer ) {
    send(source,dest,buffer,true);
  }

  /**
   * Send data block. Wait for acknowledgement.
   * Call bufferSent() to determine when buffer has been sent.
   * Call sendError() to determine if an error occurred.
   * These are valid until another buffer is sent.
   *
   * @param buffer StringBuffer of data to send
   */
  public void send ( StringBuffer buffer ) {
    send(defaultSource,defaultDestination,buffer,false);
  }

  /**
   * Send data block. Wait for acknowledgement.
   *
   * @param dest destination port
   * @param buffer StringBuffer of data to send
   */
  public void send ( int dest, StringBuffer buffer ) {
    send(defaultSource,dest,buffer,false);
  }

  /**
   * Send data block. Wait for acknowledgement.
   *
   * @param source source port
   * @param dest destination port
   * @param buffer StringBuffer of data to send
   */
  public void send ( int source, int dest, StringBuffer buffer ) {
    send(source,dest,buffer,false);
  }

  /**
   * Send data block. Wait for acknowledgement.
   *
   * @param source source port
   * @param dest destination port
   * @param buffer StringBuffer of data to send
   * @param broadcast <code>true</code> if no acknoledgement required
   */
  public void send ( int source, int dest, StringBuffer buffer, boolean broadcast ) {
    int sz = buffer.length();

    // setup header
    if ( broadcast ) {
      switch ( sz ) {
      case 1:
        sendHeaderSeq = broadcastBlock1 ;
        break;

      case 2:
        sendHeaderSeq = broadcastBlock2 ;
        break;

      default:
        sendHeaderSeq = broadcastBlock ;
        break;
      }
    } else {
      switch ( sz ) {
      case 1:
        sendHeaderSeq = sendBlock1 ;
        break;

      case 2:
        sendHeaderSeq = sendBlock2 ;
        break;

      default:
        sendHeaderSeq = sendBlock ;
        break;
      }
      // Setup header/sequence byte
      sendHeaderSeq +=
        (char)(( sendSeqNumbers[sendDestination] & 15 ) << 4) ;
    }

    // Setup source/destination byte
    sendDestination = (char) dest ;
    sendSrcDest = (char) (( source << 4 ) + dest ) ;

    // Setup flags
    waitForAck     = ! broadcast ;
    sendRetries    = maxRetries ;
    sendBuffer     = buffer ;
    sendDataPacket = true ;
    bufferSent     = false ;
    sendError      = false ;
    poll();
  }

  /**
   * Check if data block sent.
   * Call sendError() if method returns true.
   *
   * @returns true when buffer sent
   */
  public boolean bufferSent () {
    poll();
    return bufferSent ;
  }

  /**
   * Check if data block sent without errors.
   * Only call after bufferSent() returns true.
   *
   * @returns true if block not acknowledged
   */
  public boolean sendError() {
    return sendError ;
  }

  /**
   * Set buffer to receive data. No packet can be received
   * if a buffer is not available or if the receive buffer
   * is full. Calling this method with the same object
   * will allow the buffer to be used again.
   * <p>
   * Setting a buffer will reset receive data available from:
   *   receiveBufferFull()
   *   receivedBroadcast()
   *   getSourcePort()
   *   getDestinationPort()
   *
   * @param buffer StringBuffer to receive data (may be null)
   *
   * @returns old buffer
   */
  public StringBuffer setReceiveBuffer ( StringBuffer buffer ) {
    StringBuffer old = recvBuffer ;

    recvBuffer = buffer ;
    bufferReceived = false ;
    return recvBuffer ;
  }

  /**
   * Check if receive buffer is full.
   *
   * @returns true if buffer is valid
   */
  public boolean receiveBufferFull() {
    poll();
    return bufferReceived ;
  }

  /**
   * Check if broadcast packet received
   *
   * @returns true if broadcast packet received.
   */
  public boolean receivedBroadcast() {
    return broadcastReceived ;
  }

  /**
   * Get port that sent packet in receiveBuffer.
   *
   * @returns source port (0-15)
   */
  public int getSourcePort() {
    return getHighNibble(receiveSrcDest) ;
  }

  /**
   * Get destination port for packet in receiveBuffer.
   *
   * @returns destination port (0-15)
   */
  public int getDestinationPort() {
    return receiveSrcDest & 15 ;
  }

  // ====  Notification methods for subclasses  ====

  /**
   * The buffer has been sent and ack or max retries exceeded.
   */
  protected void setBufferSent ( boolean success ) {
    sendError  = ! success ;
    bufferSent = true ;
    ++ sendSeqNumbers[sendDestination] ;
  }

  /**
   * Something in the receive buffer.
   */
  protected void setBufferReceived() {
    bufferReceived = true ;

    switch ( receiveHeader ) {
    case sendBlock:
    case sendBlock1:
    case sendBlock2:
      broadcastReceived = false ;
      break;

    default:
      broadcastReceived = true ;
      break;
    }
  }

  // ====  Utility methods ====

  /**
   * Setup to send data
   */
  protected void setToSend () {
    sendingPacket = true ;
    waitingForAck = false ;
    sendChecksum  = 0 ;
    if ( halfDuplex ) {
      uartOut.restart ( Uart.dirTransmit
                      , outPin
                      , dataInvert
                      , hsOutPin
                      , hsInvert
                      , baudRate
                      , stopBits ) ;
      checkReceiver = false ;
    }
  }

  /**
   * Setup to receive data
   */
  protected void setToReceive() {
    if ( halfDuplex ) {
      resetReceive() ;
      checkReceiver = true ;
      uartIn.restart ( Uart.dirReceive
                     , inPin
                     , dataInvert
                     , hsInPin
                     , hsInvert
                     , baudRate
                     , stopBits ) ;
    }
  }

  /**
   * Send packet of data.
   * Assumes full duplex or receiver is not active.
   */
  protected void sendDataPacket () {
    // Setup to send packet
    setToSend () ;

    if ( ! ( halfDuplex && ( recvHeader != receiveState ))) {
      int sz = sendBuffer.length ();

      // Send header/sequence byte
      send ( sendHeaderSeq ) ;

      // Send source/destination if needed
      if ( receivePortFlags != 0 ) {
        send ( sendSrcDest ) ;
      }

      // Send size byte if needed
      if ( sz > 2 ) {
        send ((char) sz );
      }

      // Send block of data
      for ( int i = 0; i < sz; ++ i ) {
        send (sendBuffer.charAt(i));
      }

      // send checksum
      sendChecksum () ;
    }
  }

  /**
   * Send a response packet of data.
   * Assumes full duplex or receiver is not active.
   */
  protected void sendResponsePacket () {
    // Setup to send packet
    setToSend () ;

    // Send header/sequence byte
    send ( responseHeaderSeq ) ;

    // Send source/destination if needed
    if ( receivePortFlags != 0 ) {
      send ( responseSrcDest ) ;
    }

    // Send checksum
    sendChecksum () ;
  }

  /**
   * Setup a response packet to be sent as soon as possible.
   */
  protected void sendResponse ( int header ) {
    sendResponsePacket = true ;
    responseSrcDest    = (char)(   getHighNibble(receiveSrcDest)
                                 + ( receiveSrcDest << 4 )) ;
    responseHeaderSeq  = (char) ( header + ( receiveSeq << 4 )) ;
  }

  /**
   * Set to idle mode. Reset checksum.
   */
  protected void resetReceive() {
    receiveChecksum  = 0 ;
    receiveState = recvHeader ;
    checkRecvTimeout = false ;
  }

  // poll() variables
  protected boolean ackReceiveData ;
  protected int     receiveDataLeft ;
  protected boolean ignoreInput ;


  // poll() state constants

  final static int flushInput          = 0 ;
  final static int recvHeader          = 1 ;
  final static int recvSrcDest         = 2 ;
  final static int recvSrcDestResponse = 3 ;
  final static int recvData            = 4 ;
  final static int recvChecksum        = 5 ;

  /**
   * Check the system. Will be idling, sending or receiving packets.
   */
  public void poll () {
    // Check output queue. Switch uart if half duplex and not sending.
    if ( sendingPacket && uartOut.sendBufferEmpty()) {
      sendingPacket = false ;
      waitingForAck = waitForAck ;

      if ( halfDuplex ) {
        setToReceive();  // enable receiver
      }
    }

    // Check input queue
    if ( checkReceiver ) {
      while (true) {
        // Check for receiver timeouts.
        // Receiver timeouts started after header and second byte is received.
        if ( checkRecvTimeout ) {
          if (receiveState == flushInput) {
            if ( receiveTimer.timeout ( flushTimeout )) {
              // Input queue flushed. Start looking for a packet.
              resetReceive();
            }
          } else if ( receiveTimer.timeout ( receiveTimeout )) {
            // Timeout has occurred. Reset timeout checking.
            checkRecvTimeout = false ;

            if ( ! ignoreInput ) {
              // Respond with a resend byte
              sendResponse ( resend ) ;
            }
          }
        }

        // exit from loop when no more bytes to process
        if ( ! uartIn.byteAvailable ()) {
          break ;
        }

        receiveData = (char) uartIn.receiveByte() ;
        receiveChecksum = computeChecksum ( receiveChecksum, receiveData ) ;

        // Show incoming data when debugging
//        System.out.print ("<");
//        System.out.print ((int)receiveData);
//        System.out.print (">");

        switch ( receiveState ) {
        case flushInput:
          // just ignore character
          receiveTimer.mark();
          continue;

        case recvHeader:
          ackReceiveData = false ;
          receiveHeader = (char)(receiveData & 15 ) ;
          receiveSeq    = (char)getHighNibble(receiveData) ;
          switch ( receiveHeader ) {
          case sendBlock:
            ackReceiveData = true ;
          case broadcastBlock:
            receiveDataLeft = 0 ;
            break;

          case sendBlock1:
            ackReceiveData = true ;
          case broadcastBlock1:
            receiveDataLeft = 1 ;
            break;

          case sendBlock2:
            ackReceiveData = true ;
          case broadcastBlock2:
            receiveDataLeft = 2 ;
            break;

          case ack:
          case resend:
          case reset:
            receiveState = ( receivePortFlags != 0 )
                         ? recvSrcDestResponse
                         : recvChecksum ;
            continue;

          default:
            receiveState = flushInput ;
            checkRecvTimeout = true ;
            receiveTimer.mark();
            continue ;
          }

          // Ignore input if no buffer available
          ignoreInput  = bufferReceived || ( recvBuffer == null ) ;

          // Clear buffer if receiving data

          if ( ! ignoreInput ) {
            recvBuffer.clear();
          }

          // Setup next state
          receiveState = recvSrcDest ;
          break;

        case recvSrcDestResponse:
        case recvSrcDest:
          if ( receivePortFlags == 0 ) {
            receiveSrcDest = 0 ;
            receiveState = recvData ;
          } else {
            receiveSrcDest = receiveData ;
            receiveData = (char) ( receiveData & 15 ) ; // get destination

            // need to check destination and if buffer was sent
            if (    ((( 1 << receiveData ) & receivePortFlags ) == 0 )
                 && (waitingForAck && ( sendDestination != receiveData))) {
              // packet destined for port on another device
              ignoreInput = true ;
            }

            // Enable timeout
            checkRecvTimeout = true ;
            receiveTimer.mark () ;

            // Setup next state
            receiveState = ( receiveState == recvSrcDestResponse )
                         ? recvChecksum   // responses have no data
                         : recvData ;
            break;
          }

        case recvData:
          if ( receiveDataLeft == 0 ) {
            receiveDataLeft = receiveData ;
          } else {
            if ( ! ignoreInput ) {
              recvBuffer.append ( receiveData ) ;
            }
            -- receiveDataLeft ;
            if ( receiveDataLeft == 0 ) {
              receiveState = recvChecksum ;
            }
          }
          break ;

        case recvChecksum:
          if ( ! ignoreInput ) {
            // Process packet checksum
            if (receiveChecksumValid ()) {
              // Packet is valid. Process action
              switch ( receiveHeader ) {
              case ack:
                if ( waitingForAck ) {
                  setBufferSent ( true ) ;
                }
                break;

              case resend:
                if ( waitingForAck ) {
                  resendDataPacket() ;
                }
                break;

              case reset:
                if ( waitingForAck ) {
                  setBufferSent ( false ) ;
                }
                break;

              case broadcastBlock:
              case broadcastBlock1:
              case broadcastBlock2:
                // No response needed. Broadcasts only occur once.
                setBufferReceived();
                break;

              case sendBlock:
              case sendBlock1:
              case sendBlock2:
                int destPort = getHighNibble(receiveSrcDest) ;
                int destSeq  = receiveSeqNumbers[ destPort ] ;

                if ( receiveSeq == destSeq ) {
                  receiveSeqNumbers[ destPort ] = (char)((destSeq + 1) & 15) ;

                  // Header was send or broadcast
                  setBufferReceived();
                  sendResponse(ack);
                } else if ( receiveSeq == (( destSeq - 1 ) & 15 )) {
                  // Acknowledge prior packet that may have missed an ack.
                  sendResponse(ack);
                } else {
                  // Bad sequence number. May need to do a reset.
                  sendResponse(resend);
                }
                break;
              }
            }
          }

          // Reset receive mode
          resetReceive() ;
        }
      }
    }

    // Check for send timeouts
    if ( checkSendTimeout ) {
      if ( sendTimer.timeout ( sendTimeout )) {
        // Timeout has occurred. Reset timeout flag.
        checkSendTimeout = false ;
        resendDataPacket();
      }
    }

    // Send data if not in half duplex mode and not receiving a packet
    if (( ! halfDuplex ) || ( recvHeader == receiveState )) {
      if ( sendDataPacket ) {
        // Send data packet
        sendDataPacket = false ;
        sendDataPacket () ;

        switch ( sendHeaderSeq & 15 ) {
        case broadcastBlock:
        case broadcastBlock1:
        case broadcastBlock2:
          setBufferSent(true) ;
          break;

        case sendBlock:
        case sendBlock1:
        case sendBlock2:
          sendTimer.mark();
          checkSendTimeout = true ;
          break;
        }
      }

      // See if response packet needs to be sent
      if ( sendResponsePacket ) {
        sendResponsePacket = false ;
        sendResponsePacket () ;
      }
    }
  }

  /**
   * Resend data packet if more retries
   */
  protected void resendDataPacket() {
    // Check if time to resend.
    -- sendRetries ;
    if ( sendRetries == 0 ) {
      // Retries used up. Abort.
      setBufferSent(false) ;
    } else {
      // Try to send the buffer again.
      sendDataPacket = true ;
    }
  }

  /**
   * Send a raw byte
   *
   * @param data byte value to send
   */
  protected void sendRawByte ( char data ) {
    uartOut.sendByte ((byte) data ) ;
  }

  /**
   * Send a block of data, checksum data
   *
   * @param data block of data to send
   */
  protected void sendBlock ( int size, char data[] ) {
    for ( int i = 0 ; i < size ; ++ i ) {
      send ( data[i] ) ;
    }
  }

  /**
   * Send a byte
   *
   * @param data byte value to send
   */
  protected void send ( char data ) {
    sendChecksum = computeChecksum ( sendChecksum, data ) ;
    sendRawByte ( data ) ;
  }

  /**
   * Send checksum byte
   */
  protected void sendChecksum () {
    // send byte that will make the receive checksum 255
    sendRawByte ((char) ( 255 - sendChecksum )) ;
  }

  /**
   * Check receive checksum
   *
   * @returns true if checksum was valid
   */
  protected boolean receiveChecksumValid () {
    // receive checksum is valid if it is 255
    return -1 == receiveChecksum ;
  }

  /**
   * Compute simple checksum byte
   *
   * @param buffer StringBuffer containing data
   *
   * @return checksum byte
   */
  protected char computeChecksum ( char checksum, char data ) {
    checksum += data ;
    if (checksum > 255) {
      // Byte overflow occurred.
      checksum -= 255 ;
    }
    return checksum ;
  }

}