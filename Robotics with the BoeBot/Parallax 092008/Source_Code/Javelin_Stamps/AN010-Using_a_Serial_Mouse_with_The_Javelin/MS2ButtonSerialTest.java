import stamp.core.*;
import stamp.peripheral.hid.mouse.serial.MS2ButtonSerial;

/* This program will test the mouse for proper configuration
 * by displaying the status of the buttons and the location of the mouse.
 * For more information see application note 10 (an010) from Parallax, Inc.
 *
 * Version 1.0 - 12/27/02
 */
public class MS2ButtonSerialTest {

  // Declare I/O pins connected to Mouse via RS232.
  final static int DTR = CPU.pin0;               // COM Port 1
  final static int RX  = CPU.pin1;               // COM Port 3
  final static int RTS = CPU.pin2;               // COM Port 8

  // Declare receiver Uart for serial mouse connection/data.
  static Uart rxUart = new Uart( Uart.dirReceive, RX, Uart.dontInvert,
                                 Uart.speed1200, Uart.stop1);

  // Create a mouse object with default settings.
  static MS2ButtonSerial mouse = new MS2ButtonSerial(rxUart,DTR,RTS);
  static int xDistance, yDistance;               // Store x & y mouse values
  static boolean mouseDetect;                    // Mouse status

  public static void main(){
    System.out.print('\u0010');                  // Clear output window.
    mouseDetect = mouse.bootSequence();          // Initialize mouse.

    // If mouse initialization succeeded, display button and distance info.
    if(mouseDetect){
      System.out.println("Mouse successfully detected.\n");
      System.out.println("Move mouse and click buttons to display data.");

      while(true){

        // Wait until the mouse object has captured data before updating display.
        if(mouse.event()){

          // Move mouse data from serial buffer into the mouse object
          mouse.update();

          // Send cursor to home position, then move to sixth line.
          System.out.print("\u0001\n\n\n\n\n");

          // Display button states.
          System.out.print("leftButton    ");
          System.out.print(mouse.leftButton);
          System.out.print("      \n");

          System.out.print("rightButton   ");
          System.out.print(mouse.rightButton);
          System.out.print("      \n\n");

          // Display distances traveled.
          System.out.print("xDistance     ");
          System.out.print(mouse.xDistance);
          System.out.print("      \n");

          System.out.print("yDistance     ");
          System.out.print(mouse.yDistance);
          System.out.print("      \n");

        }// end if mouse.event
      }// end while
    }// end if mouseDetect

    // If mouse initialization did not succeed, display error message.
    else{
      CPU.delay(20000);
      System.out.println("Mouse not detected!");
      System.out.println("Check wiring and try again.");
    }// end else
  }// end main
}// end class