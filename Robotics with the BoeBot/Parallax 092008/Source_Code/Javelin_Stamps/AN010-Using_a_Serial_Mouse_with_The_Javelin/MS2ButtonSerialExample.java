import stamp.core.*;
import stamp.peripheral.hid.mouse.serial.MS2ButtonSerial;
import examples.peripheral.hid.mouse.serial.MS2ButtonSerialTerminalHelper;

/* This program will allow you to drag and drop characters within a box on the
 * IDE's "Messages from the Javelin" window.
 * For more information see application note 10 (an010) from Parallax, Inc.
 *
 * Version 1.0 - 12/27/02
 */
public class MS2ButtonSerialExample {

  // Declare I/O pins connected to Mouse via RS232.
  final static int DTR = CPU.pin0;               // COM Port 1
  final static int RX  = CPU.pin1;               // COM Port 3
  final static int RTS = CPU.pin2;               // COM Port 8

  // Declare receiver Uart for serial mouse connection/data.
  static Uart rxUart = new Uart( Uart.dirReceive, RX, Uart.dontInvert,
                                 Uart.speed1200, Uart.stop1);

  // Initialize values that will be passed to the mouse object.
  // See MS2ButtonSerial javadoc (MS2ButtonSerial.pdf) for details.
  static int x=15, y=7, xOld=15, yOld=7, scale=40, xMin=2, yMin=3, xMax=28, yMax=11;

  // Use values to create and initialize mouse object.
  public static MS2ButtonSerial mouse = new MS2ButtonSerial
  (rxUart, DTR, RTS, scale, xMin, yMin, xMax, yMax, x, y );

  // Declare a display object.  This display is for demonstration purposes only.
  static MS2ButtonSerialTerminalHelper display = new MS2ButtonSerialTerminalHelper();

  // Declare intermediate variables that store values.
  static int xInitial, xFinal, yInitial, yFinal;
  static char c;

  public static void main(){

    // Initialize display object.
    display.cls();
    display.indexInit();
    display.indexDisplay();

    // Initialize mouse to communicate with the Javelin Stamp.
    mouse.bootSequence();

    // Place display pointer.  We'll control the motion of an asterisk
    // using the mouse.
    display.placeChar(x,y,'*');

    // Get initial position information from the mouse object.  These
    // positions were set in the mouse object's constructor.
    x = mouse.xDistance/scale;
    y = mouse.yDistance/scale;

    // This infinite loop calls the update method, then processes and displays
    // the resulting information.  Distance values are scaled for a small
    // display.
    while(true){

      mouse.update();

      x = mouse.xDistance/scale;
      y = mouse.yDistance/scale;

      // When a drag operation is detected, record the initial values and
      // replace the asterisk cursor with the character being dragged.
      if(mouse.leftDrag){
        xInitial = mouse.xDragStart/scale;
        yInitial = mouse.yDragStart/scale;
        c = display.index(xInitial,yInitial);
        display.placeChar(x,y,c);
      }

      // When a drop is detected, copy the character and update the display
      // if the character is dropped in the lower portion of the display;
      // otherwise discard the action and display the cursor.
      else if(mouse.leftDrop){

        // Copy the character from the start of the drag to the location of
        // the drop and update the display object using the indexUpdate method.
        if(!display.cursorInRegion(2,2,30,8)){
          xFinal = mouse.xDragEnd/scale;
          yFinal = mouse.yDragEnd/scale;
          display.indexUpdate(xFinal,yFinal,c);
          display.placeChar(x,y,c);
          mouse.clearStatus();
        }

        // Update the cursor location and forget about the drag and drop.
        else{
          display.placeChar(x,y,'*');
          mouse.clearStatus();
        }
      }

      // If no drag and drop activity, update the cursor location.
      else{
        display.placeChar(x,y,'*');
      }

      // If right-click and release, clear the display and forget the
      // character location date.
      if(mouse.rightDrop){
        display.cls();
        display.indexReInit();
        display.indexDisplay();
        x = mouse.xDistance/scale;
        y = mouse.yDistance/scale;
        display.placeChar(x,y,'*');
        mouse.clearStatus();
      }

      // Place the old character where the cursor is displayed.  This has
      // two purposes.  First, the cursor flashes on/off and displays the
      // character under it, and second, when the cursor moves to a new cell,
      // the old cell remains unchanged.
      display.placeChar(xOld,yOld,display.index(xOld,yOld));
      xOld = x;
      yOld = y;
    }
  }
}