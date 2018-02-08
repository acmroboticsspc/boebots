import stamp.core.*;
import stamp.peripheral.display.lcd.serial.BPI216;

/* Will display an animated sequence with custom generated characters for the
 * BPI-216 serial LCD.
 *
 * For more information see AppNote009 from Parallax Inc.
 * Version 1.0 Dec. 24th, 2002
 */
public class BPI216Animation {

  final static int LCD_PIN = CPU.pin10;              // define Javelin I/O pin
  static Uart txOut   = new Uart( Uart.dirTransmit, LCD_PIN, Uart.invert,
                                  Uart.speed9600, Uart.stop1 );  // create Uart
  static BPI216 myLCD = new BPI216(txOut);           // create LCD object

  // Define Graphic bitmaps
  final static char[] MOUTH0 = {0x0E,0x1F,0x1F,0x1F,0x1F,0x1F,0x0E,0x00};
  final static char[] MOUTH1 = {0x0E,0x1F,0x1F,0x18,0x1F,0x1F,0x0E,0x00};
  final static char[] MOUTH2 = {0x0E,0x1F,0x1C,0x18,0x1C,0x1F,0x0E,0x00};
  final static char[] SMILEY = {0x00,0x0A,0x0A,0x00,0x11,0x0E,0x06,0x00};
  final static int[]  CELLS  = {2,1,0,1};            // animation sequence

  static String msg = new String(" IS VERY COOL!  ");

  public static void main() {

    // Store custom characters in CGRAM.
    myLCD.createChar5x8(0, MOUTH0);
    myLCD.createChar5x8(1, MOUTH1);
    myLCD.createChar5x8(2, MOUTH2);
    myLCD.createChar5x8(3, SMILEY);

    while (true) {

      myLCD.CLS();
      CPU.delay(5000);
      myLCD.write("  The Javelin");
      CPU.delay(10000);

      // Animation cycle.
      for (int addr = 0; addr < 16; addr++) {        // loop length of screen
        for (int cycle = 0; cycle <= 4; cycle++) {   // cycle CG graphics
          myLCD.cursorMove(1, addr);
          if (cycle < 4) {
           myLCD.write(CELLS[cycle]);                // write CG image # of CELLS
          }// if
          else {
           myLCD.write(msg.charAt(addr));            // write msg up to cursor
          }// else
          CPU.delay(750);
        }// for cycle
      }// for addr

      // Flash display on/off.
      for (int cycle = 0; cycle <= 4; cycle++) {
        myLCD.displayOff();
        CPU.delay(2500);
        myLCD.displayOn();
        CPU.delay(2500);
      }// for cycle
      CPU.delay(5000);
    }// while
  }// main
}// class