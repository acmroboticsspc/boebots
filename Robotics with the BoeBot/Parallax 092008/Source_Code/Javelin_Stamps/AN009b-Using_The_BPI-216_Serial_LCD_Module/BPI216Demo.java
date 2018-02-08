import stamp.core.*;
import stamp.peripheral.display.lcd.serial.BPI216;

/* Demonstrate all methods from the BPI216 library for the BPI-216 serial LCD.
 *
 * For more information see AppNote009 from Parallax Inc.
 * Version 1.0 Dec. 24th, 2002
 */
public class BPI216Demo {

  final static int LCD_PIN = CPU.pin10;
  static Uart txOut   = new Uart( Uart.dirTransmit, LCD_PIN, Uart.invert,
                                  Uart.speed9600, Uart.stop1);
  static BPI216 myLCD = new BPI216(txOut);

  public static void main() {

    // Example for: CLS()
    // Clear the screen.
    myLCD.CLS();

    // Example for: write()
    // Write a string to the LCD display
    myLCD.write("Testing 123...");          // write a string
    wait();

    // Example for: write()
    // Write a StringBuffer to the LCD display
    myLCD.CLS();
    StringBuffer sb = new StringBuffer(16); // create a StringBuffer
    sb.append("The Javelin");               // add ASCII message
    myLCD.write(sb);                        // write the StringBuffer
    wait();

    // Example for: cursorSet()
    // Moves LCD cursor to specified line and cursor position
    myLCD.cursorMove(2,3);                      // set cursor to row=2, column=3
    myLCD.write("from Parallax");
    wait();

    // Example for: displayOnUL()
    // Turn display on with an underline cursor
    myLCD.CLS();
    myLCD.displayOnUL();
    myLCD.cursorMove(1,0);
    myLCD.write("Cursor:");
    wait();

    // Example for: displayOnBL()
    // Turns display on with a blinking-block cursor
    myLCD.displayOnBL();
    myLCD.cursorMove(1,0);
    myLCD.write("Cursor:");
    wait();

    // Example for: displayOn()
    // Turns display on without a cursor
    myLCD.cursorMove(1,0);
    myLCD.write("Cursor:");
    myLCD.displayOn();
    wait();

    // Example for: displayOff()
    // Turns display off
    myLCD.displayOff();
    wait();
    myLCD.displayOnBL();
    wait();

    // Example for: home()
    // Moves LCD cursor to home position (line 1, column 0)
    myLCD.CLS();
    myLCD.write("Hi!");
    myLCD.home();
    myLCD.write("Home");
    wait();

    // Example for: createChar5x8()
    // Takes bitmap of a graphic 5x8 character and stores it in CGRam #0
    final char[] SMILEY = {0x00,0x0A,0x0A,0x00,0x11,0x0E,0x06,0x00};
    myLCD.createChar5x8(7, SMILEY);

    // Example for: write()
    // Writes CGRam location at cursor position
    myLCD.CLS();
    myLCD.write(7);
    wait();

    // Example for: scrollRT() & scrollLF()
    myLCD.CLS();
    myLCD.displayOn();
    myLCD.write("Scrolling Screen");
    // Scroll display image to the right (16 times)
    for(int i=1;i<17;i++){
      wait();
      myLCD.scrollRT();
    }
    // Scroll display image to the left (56 times, notice wrap-around)
     for(int i=56;i>0;i--){
      wait();
      myLCD.scrollLF();
    }

    // Example for: cursorRT() & cursorLF()
    myLCD.CLS();
    myLCD.write("Moving Cursor");
    myLCD.displayOnBL();
    // Scroll cursor to the left (6 times)
    for(int i=0;i<6;i++){
      wait();
      myLCD.cursorLF();
    }
     // Scroll display image to the right (6 times)
     for(int i=0;i<6;i++){
      wait();
      myLCD.cursorRT();
    }

    //All Done
    myLCD.displayOn();
    myLCD.CLS();
    myLCD.write("The End");

  }// end main

  public static void wait(){
    while(CPU.readPin(CPU.pins[1])==false){}// wait for pushbutton to be pressed
    while(CPU.readPin(CPU.pins[1])==true){} // wait for pushbutton to be released
    CPU.delay(100);                         // delay for pushbutton bounce back
  }

  }// end class
