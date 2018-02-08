import stamp.core.*;
import stamp.peripheral.display.lcd.serial.BPI216;

/* Test program to verify the BPI-216 serial LCD is properly configured.
 *
 * For more information see AppNote009 from Parallax Inc.
 * Version 1.0 Dec. 24th, 2002
 */
public class BPI216Test {

  final static int LCD_PIN = CPU.pin10;
  static Uart txOut   = new Uart( Uart.dirTransmit, LCD_PIN, Uart.invert,
                                  Uart.speed9600, Uart.stop1);
  static BPI216 myLCD = new BPI216(txOut);  // create BPI216 object

  public static void main() {
    myLCD.CLS();                            // clear the screen
    myLCD.write("Hello World!");            // display message
  }
}

