/*
 * Copyright © 2002 Parallax, Inc. All rights reserved.
 */

package stamp.peripheral.hid.mouse.serial;
import stamp.core.*;

/**
 * <H2>Overview</H2>
 * This library tracks recent button and motion events by processing
 * information sent by a Microsoft 2-button serial mouse.
 *
 * <p>
 * @see <i>AppNote010</i> for circuit diagrams, examples, and instructions on
 * how to use this class, the Javelin Stamp and a Microsoft serial 2-button
 * mouse.  This application note is available from
 * <a href="www.javelinstamp.com"> www.javelinstamp.com</a>.
 * <p>
 * @author Andy Lindsay of Parallax, Inc.
 * @version 1.0, 12/27/02
 */
public class MS2ButtonSerial{

  /**
   * Indicates the state of the left button.  <code>true</code> if pressed,
   * <code>false</code> if not pressed.
   */
  public static boolean leftButton = false;

  /**
   * Indicates the state of the right button.  <code>true</code> if pressed,
   * <code>false</code> if not pressed.
   */
  public static boolean rightButton = false;

  /**
   * Indicates the previous state of the left button.  <code>true</code> if
   * pressed, <code>false</code> if not pressed.
   */
  public static boolean leftButtonOld = false;

  /**
   * Indicates the previous state of the right button.  <code>true</code> if
   * pressed, <code>false</code> if not pressed.
   */
  public static boolean rightButtonOld = false;

  /**
   * Indicates whether or not a drag operation is in progress with the left
   * mouse button pressed and held.  <code>true</code> if pressed and held,
   * <code>false</code> if a drag operation is not in progress.
   */
  public static boolean leftDrag = false;

  /**
   * Indicates whether or not a drag operation is in progress with the right
   * mouse button pressed and held.  <code>true</code> if pressed and held,
   * <code>false</code> if a drag operation is not in progress.
   */
  public static boolean rightDrag = false;

  /**
   * Indicates whether or not a left-drop operation has occurred, meaning that
   * the left-mouse button has been released. <code>true</code> if pressed,
   * <code>false</code> if not pressed.
   */
  public static boolean leftDrop = false;

  /**
   * Indicates whether or not a right-drop operation has occurred meaning that
   * the right-mouse button has been released. <code>true</code> if pressed,
   * <code>false</code> if not pressed.
   */
  public static boolean rightDrop = false;

  /**
   * Stores the most recent x-coordinate recorded at the beginning of a drag
   * operation.
   */
  public static int xDragStart;

  /**
   * Stores the most recent x-coordinate recorded when a drop operation
   * occurred.
   */
  public static int xDragEnd;

  /**
   * Stores the most recent y-coordinate recorded at the beginning of a drag
   * operation.
   */
  public static int yDragStart;

  /**
   * Stores the most recent y-coordinate recorded when a drop operation
   * occurred.
   */
  public static int yDragEnd;

  /**
   * Stores cumulative <code>x-distance</code> traveled by the mouse.
   * Distances traveled to the right are added to this value, and distances
   * traveled to the left are subtracted from this value.
   */
  public static int xDistance;

  /**
   * Stores cumulative <code>y-distance</code> traveled by the mouse.
   * Distances traveled downward are added to this value, and distances traveled
   * upward are subtracted from this value.
   */
  public static int yDistance;

  /**
   * Stores the maximum allowable <code>xDistance</code> value.
   */
  public static int xMax;

  /**
   * Stores the minimum allowable <code>xDistance</code> value.
   */
  public static int xMin;

  /**
   * Stores the maximum allowable <code>yDistance</code> value.
   */
  public static int yMax;

  /**
   * Stores the minimum allowable <code>yDistance</code> value.
   */
  public static int yMin;

  /**
   * Stores a scale value that can be used to slow the mouse's travel
   * on a given display.
   */
  public static int scale;

  /**
   * I/O pin connected to the mouse's DTR line via an RS232 transceiver.
   */
  public static int DTR;

  /**
   * I/O pin connected to the mouse's RTS line via an RS232 transceiver.
   */
  public static int RTS;

  /**
   * Creates a new mouse object with default settings:
   * <code> scale = 1,
   * xMin = yMin = -10000,
   * xMax = yMax = +10000,
   * xDistance = yDistance = 0</code>
   *
   * @param rxUart Uart object setup for serial mouse communication.
   * @param DTR I/O pin connected to the DTR line through an RS232 transceiver.
   * @param RTS I/O pin connected to the RTS line through an RS232 transceiver.
   */
  public MS2ButtonSerial(Uart rxUart, int DTR, int RTS){
    this.rxUart = rxUart;
    this.DTR = DTR;
    this.RTS = RTS;
    this.scale = 1;
    this.xMin = -10000;
    this.yMin = -10000;
    this.xMax = 10000;
    this.yMax = 10000;
    this.xDistance = 0;
    this.yDistance = 0;
  }

  /**
   * Creates a new mouse object user specified settings:
   *
   * @param rxUart Uart object setup for serial mouse communication.
   * @param DTR I/O pin connected to the DTR line through an RS232 transceiver.
   * @param RTS I/O pin connected to the RTS line through an RS232 transceiver.
   * @param scale value slows apparent pointer motion on the display.
   * @param xMin sets the minimum value that <code>xDistance</code> will not
   *        be allowed to go below.
   * @param yMin sets the minimum value that <code>yDistance</code> will not
   *        be allowed to go below.
   * @param xMax sets the maximum value that <code>xDistance</code> will not
   *        be allowed to go above.
   * @param yMax sets the maximum value that <code>yDistance</code> will not
   *        be allowed to go above.
   * @param xDistance initializes the value of <code>xDistance</code> for
   *        initial cursor value.
   * @param yDistance initializes the value of <code>yDistance</code> for
   *        initial cursor value.
   */
  public MS2ButtonSerial(Uart rxUart, int DTR, int RTS, int scale, int xMin,
                          int yMin, int xMax, int yMax, int xDistance,
                          int yDistance){
    this.rxUart = rxUart;
    this.DTR = DTR;
    this.RTS = RTS;
    this.scale = scale;
    this.xMin = xMin*scale;
    this.yMin = yMin*scale;
    this.xMax = xMax*scale;
    this.yMax = yMax*scale;
    this.xDistance = xDistance*scale+(scale/2);
    this.yDistance = yDistance*scale+(scale/2);
  }

  /**
   * Emulate PC boot sequence at the Mouse's serial connections.
   *
   * IMPORTANT: This method must be called before attempting to use other
   * methods in this library.
   *
   * @return <code>true</code> if a Microsoft serial mouse is detected,
   *         <code>false</code> if the mouse is not detected.
   */
  public boolean bootSequence(){
    int number;
    CPU.writePin(DTR, false);
    CPU.writePin(RTS, false);
    CPU.delay(2000);
    CPU.pulseOut(24000,RTS);
    CPU.delay(2000);
    if(rxUart.byteAvailable()){
      number = rxUart.receiveByte();
      number = number & 0x007F;
      if(number == 77){
        return true;
      }
    }
    return false;
  }

  /**
   * Determines if data sent by the mouse is waiting in the <code>rxUart</code>
   * buffer, indicating that an event has occurred since the last call to the
   * <code>update</code> method.
   *
   * @return <code>true</code> if an event has occurred or <code>false</code>
   * if an event has not occurred.
   */
  public boolean event(){
    return rxUart.byteAvailable();
  }

  /**
   * Clears the status of all button triggered event flags.
   */
  public void clearStatus(){
    leftButtonOld  = false;
    rightButtonOld = false;
    leftDrag  = false;
    rightDrag = false;
    leftDrop  = false;
    rightDrop = false;
  }

  /**
   * Processes all information stored in the rxUart buffer and updates distance
   * measurements and button triggered event flags.
   */
  public void update(){
    if(rxUart.byteAvailable()){
      while(rxUart.byteAvailable()){
        read();
        if(leftButton == pressed){
          if(leftButtonOld == notPressed){
            leftDrop   = false;
            leftDrag   = true;
            xDragStart = xDistance;
            yDragStart = yDistance;
          }
        }else if(leftButton == notPressed){
          if(leftButtonOld == pressed){
            leftDrag = false;
            leftDrop = true;
            xDragEnd = xDistance;
            yDragEnd = yDistance;
          }
        }
        if(rightButton == pressed){
          if(rightButtonOld == notPressed){
            rightDrop  = false;
            rightDrag  = true;
            xDragStart = xDistance;
            yDragStart = yDistance;
          }
        }else if(rightButton == notPressed){
          if(rightButtonOld == pressed){
            rightDrag = false;
            rightDrop = true;
            xDragEnd  = xDistance;
            yDragEnd  = yDistance;
          }
        }
        leftButtonOld  = leftButton;
        rightButtonOld = rightButton;
        xDistance += xIncrement;
        yDistance += yIncrement;
      }
    }
    if(xDistance<xMin) xDistance = xMin;
    if(xDistance>xMax) xDistance = xMax;
    if(yDistance<yMin) yDistance = yMin;
    if(yDistance>yMax) yDistance = yMax;
  }

  /**
   * 3-byte mouse information packet
   */
  protected static int packet[] = new int[3];

//============================================================================
// Private methods and fields below this point.
//============================================================================

  final private static boolean pressed = true;
  final private static boolean notPressed = false;

  private static int xIncrement;
  private static int yIncrement;

  private Uart rxUart;

  // This read method was designed based on information obtained from:
  // http://www.hut.fi/~then/mytexts/mouse.html
  private void read(){
    if(!rxUart.byteAvailable()){
      xIncrement = 0;
      yIncrement = 0;
    }else{
      // Capture a 3-byte mouse information packet (mouse info).
      do
        packet[0] = rxUart.receiveByte();
      while ((packet[0] & 64) != 64);

      packet[1] = rxUart.receiveByte();
      packet[2] = rxUart.receiveByte();

      // Button states
      leftButton = (32==(packet[0] & 32));
      rightButton = (16 == (packet[0] & 16));

      // Y distance (negative is up, positive is down.
      yIncrement = 0;
      yIncrement = packet[0] & 0x000C;
      yIncrement = yIncrement << 4;
      packet[2] = packet[2] & 0x003F;
      yIncrement = yIncrement | packet[2];
      if ((yIncrement & 128) == 128){
        yIncrement = yIncrement ^ 0x00FF;
        if(yIncrement==0)
          yIncrement = -1;
        else{
          yIncrement = -yIncrement;
        }// else
      }// if

      // X distance (negative is left, positive is right.
      xIncrement = 0;
      xIncrement = packet[0] & 0x0003;
      xIncrement = xIncrement << 6;
      packet[1] = packet[1] & 0x003F;
      xIncrement = xIncrement | packet[1];
      if ((xIncrement & 128) == 128){
        xIncrement = xIncrement ^ 0x00FF;
          if(xIncrement==0)
            xIncrement = -1;
        else{
          xIncrement = -xIncrement;
        }// else
      }// if
    }// else
  }// read

}//class