// Author: Parallax Inc. (javelintech@parallaxinc.com)
// Version: 1.0 Aug 1, 2002

import stamp.core.*;
import stamp.peripheral.appmod.*;

// This class is for the Parallax Inc "LED Terminal AppMod"
// It will demonstrate the methods available to you with the LedMsg class.
// The LED AppMod is not case-sensitive; lower case letters will appear as
// uppercase.  Not all symbols can be displayed.

public class LedTerminalDemo {
  public static void main() {
    LedTerminal msg  = new LedTerminal();   // Create the Led Message Object
    final int w1     = 1000;                // Wait delay: 1/10 second
    final int w10    = 10000;               // Wait delay: 1 seconds
    final int w20    = 20000;               // Wait delay: 2 seconds

    msg.display("****",w20);                // Display the contents of String 's'

    // This loop will 'flash' two messages to the display
    for(int i=0;i<10;i++){                  // Loop for 10 times
      msg.display("XOOX",w1);               // Display the message "xoox"
      msg.display("OXXO",w1);               // Display the message "oxxo"
    }//end for

    msg.display("123",w20);     // Less than 4 characters will be right justified
    msg.display("ABCDEFG",w20); // Greater than 4 characters will be truncated
    msg.display("",w20);        // Will blank the display

    // There are boolean variables that you can set that will control discrete LEDs
    // These states will be reflected in every message displayed.
    // For complete information check the LedMsg class
    msg.led1=true;                          // Set led1 'ON'
    msg.display("LED1",w10);                // Display message & LED1
    msg.led1=false;                         // Set led1 'OFF'
    msg.display("",w10);                    // Clear screen, clear LED1
    msg.dec1=true;                          // Set dec1 'ON'
    msg.display("",w10);                    // Add decimal #1 to display
    msg.dec4=true;                          // Set dec4 'ON'
    msg.display("",w10);                    // Add decimal #4 to display
    msg.dec1=false;                         // Set dec1 'OFF'
    msg.dec4=false;                         // Set dec4 'OFF'
    msg.display("",w20);                    // Remove from display

    // Use the banner routine to scroll messages, you set the delay.
    msg.banner("01234.abcd",w10);           // Scroll message, delay 10000
    msg.banner("01234.abcd",w1);            // Scroll message, delay 1000
    msg.banner("01234.abcd",0);             // Scroll message, delay 0
    CPU.delay(5000);                        // Pause

    String s="Parallax Inc";                // Place the message in a string
    msg.banner(s,4000);                     // Then pass the string with a delay

    // This next section will demonstrate how you can access the display
    // directly.  This will allow you more control over the discrete LEDs etc.
    // You can also access other programming formats not used within this example.
    StringBuffer sb = new StringBuffer(13); // Create a StringBuffer
    sb.append("!LT0ASC----");               // Set ASCII message directive + message
    sb.append((char)0x00);                  // Modify for LEDs & decimals
    sb.append((char)0x00);                  // Modify for colon
    msg.direct(sb);                         // Display message

    // This loop will perform binary shifting on the character #12.
    // Which is in numerical location #11
    char c = 1;                             // Start off with a binary 1
    for(int i=0;i<8;i++){                   // Loop thru all 8 binary digits
      sb.insert(11,c);                      // Insert new number in StringBuffer
      sb.delete(12,13);                     // Delete old number in StringBuffer
      msg.direct(sb);                       // Display Message (watch the LED's)
      CPU.delay(10000);                     // Pause
      c=(char)(c<<1);                       // Shift to next binary digit
    }

    msg.setTime(0,3,17,0);                  // Set & dsiplay clock for 3:17am
    CPU.delay(32000);                       // Wait
    CPU.delay(32000);                       // Wait some more...

    // Notice the time will no longer display after a message is displayed
    msg.display("OOPS",w10);
    msg.display("TIME",w10);
    msg.display("GONE",w20);
    CPU.delay(32000);                       // Wait

    msg.displayTime(true);                  // Display current Time

  }// end main
}//end class