package stamp.peripheral.onewire;

/*
 * Copyright © 2002 Parallax, Inc. All rights reserved.
 */

import stamp.core.*;

/**
 * <H2>Overview</H2>
 * The DS2480B chip is a transceiver that the Javelin Stamp can use to communicate
 * with devices on a 1-Wire bus.  Each instance of the DS2480B library supports
 * communication between the Javelin Stamp and a DS2480B chip.
 *
 * <p>
 * @see <i>Getting Started with 1-Wire</i> for circuit diagrams, examples, and
 * instructions on how to use this class with the DS2480B and devices on a
 * 1-Wire bus.  This application note is available from
 * <a href="www.javelinstamp.com"> www.javelinstamp.com</a>.
 * <p>
 * @author Parallax, Inc.
 * @version 1.2, 11/20/02
 */
public class DS2480B {

  /**
   * To place the DS2480B into command mode, use the <code>message</code>
   * method to send it this value.
   */
  public final static int COMMAND = 0xE3;

  /**
   * To place the DS2480B into data mode, use the <code>message</code>
   * method to send it this value.
   */
  public final static int DATA = 0xE1;

  /**
   * To enable the DS2480B's strong pullup resistor, pass this value to
   * the <code>strongPullup</code> method.
   */
  public final static int INFINITE_DURATION = 0x3F;

  /**
   * To disable the DS2480B's strong pullup resistor, pass this value to
   * the <code>strongPullup</code> method.
   */
  public final static int TERMINATE_DURATION = 0xF1;

  /**
   * To send a binary-1 to the 1-Wire bus, place the DS2480B in command mode,
   * then use the <code>message</code> method to send this value.
   */
  public final static int BIT_WRITE_1 = 0x91;

  /**
   * To send a binary-0 to the 1-Wire bus, place the DS2480B in command mode,
   * then use the <code>message</code> method to send this value.
   */
  public final static int BIT_WRITE_0 = 0x81;

  /**
   * The DS2480B replies to the Javelin Stamp with this value if it relayed a
   * binary-1 from the Javelin Stamp (<code>BIT_WRITE_1</code>) to the
   * 1-Wire bus and received a binary-1 reply.
   */
  public final static int READ_1_WROTE_1 = 0x93;

  /**
   * The DS2480B replies to the Javelin Stamp with this value if it relayed a
   * binary-1 from the Javelin Stamp (<code>BIT_WRITE_1</code>) to the
   * 1-Wire bus and received a binary-0 reply.
   */
  public final static int READ_0_WROTE_1 = 0x90;

  /**
   * The DS2480B replies to the Javelin Stamp with this value if it relayed a
   * binary-0 from the Javelin Stamp (<code>BIT_WRITE_0</code>) to the
   * 1-Wire bus and received a binary-1 reply.
   */
  public final static int READ_1_WROTE_0 = 0x83;

  /**
   * The DS2480B replies to the Javelin Stamp with this value if it relayed a
   * binary-0 from the Javelin Stamp (<code>BIT_WRITE_0</code>) to the
   * 1-Wire bus and received a binary-0 reply.
   */
  public final static int READ_0_WROTE_0 = 0x80;

  /**
   * To enable the DS2480B's search accelerator mode, place it into command
   * mode, then use the <code>message</code> method to send it this value.
   */
  public final static int SEARCH_ACCELERATOR_ON = 0xB1;

  /**
   * To disable the DS2480B's search accelerator mode, place it into command
   * mode, then use the <code>message</code> method to send it this value.
   */
  public final static int SEARCH_ACCELERATOR_OFF = 0xA1;

  /**
   * Constructs a Uart to 1-Wire serial interface using the specified I/O
   * pins for Uart communication between the DS2480B and the Javelin Stamp.
   *
   * @param txPin sends control messages and/or data messages
   *              to the DS2480B.  Data messages are relayed to
   *              the 1-Wire bus by the DS2480B. Control messages are
   *              acted upon by the DS2480B.
   *
   * @param rxPin receives control confirmations and/or data messages from
   *              the 1-Wire bus relayed by the DS2480B.
   */
  public DS2480B(int txPin, int rxPin) {
    txUart = new Uart( Uart.dirTransmit, txPin, Uart.dontInvert,
                             Uart.speed9600, Uart.stop1 );
    rxUart = new Uart( Uart.dirReceive, rxPin, Uart.dontInvert,
                             Uart.speed9600, Uart.stop1 );
  }

  /**
   * Sends a byte to the DS2480B.
   *
   * @param b the <code>int</code> value containing the byte sent to
   * the DS2480B in its lower eight bits (least significant byte).
   */
  public void message(int b){
    txUart.sendByte(b);
  }

  /**
   * Sends a two message bytes to the DS2480B.
   *
   * @param b the <code>int</code> value containing the byte sent to
   * the DS2480B in its lower eight bits (least significant byte).
   * @param c the <code>int</code> value containing the byte sent to
   * the DS2480B in its lower eight bits (least significant byte).
   */
  public void message(int b, int c){
    txUart.sendByte(b);
    txUart.sendByte(c);
  }

  /**
   * Sends a three message bytes to the DS2480B.
   *
   * @param b the <code>int</code> value containing the byte sent to
   * the DS2480B in its lower eight bits (least significant byte).
   * @param c the <code>int</code> value containing the byte sent to
   * the DS2480B in its lower eight bits (least significant byte).
   * @param d the <code>int</code> value containing the byte sent to
   * the DS2480B in its lower eight bits (least significant byte).
   */
  public void message(int b, int c, int d){
    txUart.sendByte(b);
    txUart.sendByte(c);
    txUart.sendByte(d);
  }

  /**
   * Sends a specified number of message bytes from an array of int values.
   *
   * @param number of bytes from the array to be sent to the DS2480B.
   * @param values [] the <code>int</code> array containing the byte values
   * to be sent to the DS2480B.
   */
  public void message(int number, int values []){
    for(int i = 0; i < number; i++){
      txUart.sendByte(values[i]);
    }
  }

  /**
   * Sends a reset pulse to the DS2480B's 1-Wire bus.
   *
   * @return <code>true</code> if a presence pulse was returned from the
   * 1-Wire bus, <code>false</code> if a return pulse was not received from
   * the 1-Wire bus.
   */
  public boolean reset(){
    boolean b;
    clearRxBuffer();
    txUart.sendByte(COMMAND);
    txUart.sendByte(RESET_PULSE);
    CPU.delay(10);
    if(!rxUart.byteAvailable() && ((rxUart.receiveByte() & 0x00FF) == PRESENCE)){
      b = true;
    }
    else{
      b = false;
    }
    txUart.sendByte(0xE1);
    return b;
  }

  /**
   * Retrieves data bytes from the 1-Wire bus and stores them in an array.
   *
   * @param bytecount the number of bytes to read from the 1-Wire bus.
   * @param array [] the array of <code>int</code> values the bytes are to
   * be stored in.
   * @param command the message byte that causes the device on the 1-Wire
   * bus to send the requested data.
   */
  public void getData(int bytecount, int array [], int command){
    for(int i = 0; i < bytecount; i++){
      message(GET_DATA);
    }
    while(rxUart.byteAvailable() && ((rxUart.receiveByte() & 0x00FF) != (command & 0x00FF)));
    for(int i = 0; i < bytecount; i++){
      array[i] = rxUart.receiveByte() & 0x00FF;
    }
  }

  /**
   * Reads a bit value from the 1-Wire bus.
   *
   * @return <code>true</code> if the bit value is 1, <code>false</code> if
   * the bit value is 0.
   */
  public boolean getBit(){
    int i;
    boolean b;
    message(COMMAND);
    clearRxBuffer();
    message(BIT_WRITE_1);
    i = rxUart.receiveByte() & 0x00FF;
    message(DATA);
    if(i == READ_1_WROTE_1){
      return true;
    }
    else{
      return false;
    }
  }

  /**
   * Enables or disables the DS2480B's 4.7 k "strong pullup resistor".
   *
   * @param parameter byte value that enables or disables the pullup resistor.
   * Use <code>INFINITE_DURATION</code> to enable and
   * <code>TERMINATE_DURATION</code> to disable the pullup resistor.
   */
  public void strongPullup(int parameter){
    message(COMMAND,parameter,DATA);
  }

//============================================================================
// Methods and fields below this point are private.
//============================================================================

  // When the DS2480B is in command mode, it will send a reset pulse to its
  // 1-Wire bus when it receives this value from the <code>txUart</code>.
  private final static int RESET_PULSE = 0xC1;

  // DS2480B sends this value to the <code>rxUart</code> when it detects a
  // presence pulse on the 1-Wire bus.
  private final static int PRESENCE = 0xCD;

  // DS2480B receives this value from the <code>txUart</code> line and replies
  // with a response byte from the 1-Wire bus on the <code>rxUart</code> line.
  private final static int GET_DATA = 0xFF;

  // Declare Uart receiver for use by this class and any subclasses.
  private Uart rxUart;

  // Declare Uart transmitter for use by this class and any subclasses.
  private Uart txUart;

  /*
   * Clears the rxUart object's receive buffer.
   */
  private void clearRxBuffer(){
    while(rxUart.byteAvailable()){
      rxUart.receiveByte();
    }
  }

}