/*
 * Copyright © 2002 Parallax Inc. All rights reserved.
 */

package stamp.peripheral.appmod;
import  stamp.core.*;

/**
 * This LedTerminal class is for Parallax's "LED Terminal AppMod" (part #29112).
 *
 * The LED Terminal AppMod provides an interface for direct interaction between
 * a user and the host.  It offers a four digit alphanumeric LED display, six
 * individual LEDs, four pushbuttons, and a real-time clock.
 * <p>
 * Communication with the AppMod is performed, serially, via the Javelin's
 * I/O pin #6 using the UART class.  Most of the LED Terminal features are
 * available to you through methods within the LedTerminal class.  If you would
 * like to experiment with the AppMods other program strings, they are as follows:
 * (Note: All data must be sent as characters, in one continuous string)
 * <p><pre>
 * ASC - Send ASCII (text) data as well as LED control to the AppMod
 *       (4 char)(3 char)    (4 char)     (1 char)       (1 char)
 *       [init]  [directive] [ASCII Data] [LED highbyte] [LED lowbyte]
 *       !LT0    ASC         data         0x00           0x00
 *
 * BCD - Show Binary Coded Decimal data as received via serial interface.
 *       Note: data will be displayed as Hex values 0-9, A-F
 *       (4 char)(3 char)    (4 char)      (1 char)       (1 char)
 *       [init]  [directive] [binary Data] [LED highbyte] [LED lowbyte]
 *       !LT0    BCD         data          0x00           0x00
 *
 * BIN - Show decimal value of HEX input.
 *       Note: data consists of 2 bytes (0-270F)
 *             will be converted to decimal (0-9999)
 *       (4 char)(3 char)    (2 char)      (1 char)       (1 char)
 *       [init]  [directive] [binary Data] [LED highbyte] [LED lowbyte]
 *       !LT0    BIN         byte,byte     0x00           0x00
 *
 * RTI - Real-Time clock Initilization.  Set the real-time clock.
 *       (4 char)(3 char)    (4 char)         (2 char)
 *       [init]  [directive] [binary Data]    [unused]
 *       !LT0    RTI         day,hour,min,sec 0x00,0x00
 *
 * RTH - Real-Time clock Hide.  Do not display real-time clock.
 *       (4 char)(3 char)
 *       [init]  [directive]
 *       !LT0    RTH
 *
 * RTS - Real-Time clock Show.  Display the real-time clock.
 *       (4 char)(3 char)
 *       [init]  [directive]
 *       !LT0    RTS
 *
 * To control the LED's you will need two bytes: highbyte & lowbyte
 * highbyte lowbyte
 * XXXXXX00 00000000
 *       || |||||||`-> Decimal Point #1 (Right-most, Least Significant digit)
 *       || ||||||`--> Decimal Point #2                 .
 *       || |||||`---> Decimal Point #3                 .
 *       || ||||`----> Decimal Point #4 (Left-most, Most Significant digit)
 *       || |||`-----> LED #1 (Right-most)
 *       || ||`------> LED #2      .
 *       || |`-------> LED #3      .
 *       || `--------> LED #4 (Left-most)
 *       |'----------> Colon mini-LED (top)
 *       `-----------> Colon mini-LED (bottom)
 * </pre><p>
 * Note: The Javelin's current UART class version 1.0 (IDEv2.01) is designed
 *       for single-direction communication on a given I/O pin.  With this
 *       limitation, the LedTerminal.java class can not receive serial
 *       data from the Led Terminal AppMod (such as the state of the buttons)
 * <p>
 * @author Parallax Inc. (javelintech@parallaxinc.com)
 * @version 1.0 Aug 1, 2002
 */

public class LedTerminal {
/**
 * Null character
 */
  public final static char zero         = 0x00;
/**
 * Decimal LED Display #1 (Right most)
 */
  public static boolean dec1     = false;
/**
 * Decimal LED Display #2 (2nd from right)
 */
  public static boolean dec2     = false;
/**
 * Decimal LED Display #3  (2nd from left)
 */
  public static boolean dec3     = false;
/**
 * Decimal LED Display #4 (Left most)
 */
  public static boolean dec4     = false;
/**
 * Discrete LED #1 (Right most)
 */
  public static boolean led1     = false;
/**
 * Discrete LED #2  (2nd from right)
 */
  public static boolean led2     = false;
/**
 * Discrete LED #3 (2nd from left)
 */
  public static boolean led3     = false;
/**
 * Discrete LED #4 - (Left Most)
 */
  public static boolean led4     = false;
/**
 * Colon (Top)
 */
  public static boolean colonTop = false;
/**
 * Colon (Bottom)
 */
  public static boolean colonBot = false;

/**
 * Creates new Uart, and String Buffers
 */
  public LedTerminal() {
    txUart = new Uart(Uart.dirTransmit, CPU.pin6, Uart.dontInvert,
                      Uart.speed38400, Uart.stop1);
    strBuf = new StringBuffer(13);
    sb     = new StringBuffer(13);
  }// end Constructor (init)

/**
 * Scrolls a message across the display.
 * Will accept a String of any length, will automatically pad the begining and
 * ending of the String.
 * <p><code>
 *      String s="Parallax Inc";
 *      msg.banner(s,4000);
 * </code>
 * @param s    String to be scrolled
 * @param wait delay: 1 unit ~ 1/10 milisecond
 */
  public void banner(String s, int wait){
    // Append space to equal 4 characters if String is less than 4 characters
    if (s.length()<4){
      sb.clear();
      sb.append(s.toString());
      for (int i=1;i<4-s.length();i++) sb.append(" ");
      s=sb.toString();
    }
    // This routine will pad the leading message with spaces as it scrolls-in
    for(int i=1;i<=(4);i++){                // Execute 4 times for scroll-in
      strBuf.clear();                       // Clear StringBuffer
      strBuf.append("!LT0ASC");             // Append header code
      for(int j=1;j<(5-i);j++) strBuf.append(" ");      // Pad spaces
      for(int j=0;j<i;j++) strBuf.append(s.charAt(j));  // Copy message
      strBuf.append(zero);                  // Add LED & decimal codes
      strBuf.append(zero);                  // Add colon code
      strBuf=led(strBuf);                   // Add LEDs, colon & decimals
      strBuf=decimal(strBuf);               // Add decimal codes from message
      txUart.sendString(strBuf.toString()); // Send to AppMod
      CPU.delay(wait);                      // delay
    }//end for

    // This routine will scroll the entire message
    for(int i=1;i<(s.length()-3);i++){      // Loop thru message
      strBuf.clear();                       // Clear StringBuffer
      strBuf.append("!LT0ASC");             // Append header code
      for(int j=i;j<i+4;j++) strBuf.append(s.charAt(j));  // Append message
      strBuf.append(zero);                  // Add LED & decimal codes
      strBuf.append(zero);                  // Add colon code
      strBuf=led(strBuf);                   // Add LEDs, colon & decimals
      strBuf=decimal(strBuf);               // Add decimal codes from message
      txUart.sendString(strBuf.toString()); // Send to AppMod
      CPU.delay(wait);                      // delay
    }//end for

    // This routine will scroll the tail end of the message off the screen
    for(int i=(s.length()-3);i<s.length();i++){ // Execute 4 times for scroll-out
      strBuf.clear();                           // Clear StringBuffer
      strBuf.append("!LT0ASC");                 // Appedn header code
      for(int j=i;j<s.length();j++) strBuf.append(s.charAt(j));  // Copy message
      for(int j=0;j<(4-(s.length()-i));j++) strBuf.append(" ");  // Pad spaces
      strBuf.append(zero);                      // Add LED & decimal codes
      strBuf.append(zero);                      // Add colon code
      strBuf=led(strBuf);                       // Add LEDs, colon & decimals
      strBuf=decimal(strBuf);                   // Add decimal codes from message
      txUart.sendString(strBuf.toString());     // Send to AppMod
      CPU.delay(wait);                          // wait
    }//end for
    display("    ",0);                          // Clear display
    CPU.delay(wait);                            // wait
  }//end method: banner

/**
 * This routine will give you direct access over the AppMod.
 * @param xmit - Constructed data packet to be transmitted
 */
  public void direct(StringBuffer xmit) {
    txUart.sendString(xmit.toString());     // Send to AppMod
  }

/**
 * This routine will give you direct access over the AppMod.
 * @param xmit - Constructed data packet to be transmitted
 */
  public void direct(String xmit) {
    txUart.sendString(xmit.toString());     // Send to AppMod
  }


/**
 * This will display a message.
 * If the input String is greater than 4 characters, it will be truncated to 4
 * characters.  If the input string is less than 4 characters the output will
 * be right justified.
 * @param msg  Message to be displayed
 * @param wait delay: 1 unit ~ 1/10 milisecond
 */
  public void display(String msg, int wait) {
    strBuf.clear();                         // Clear StringBuffer
    strBuf.append("!LT0ASC");               // Append Header Codes

    // This routine will force the message to be exactly 4 characters in length
    if (msg.length()==4) strBuf.append(msg);// If s = 4 char, append as is, else
    else if (msg.length()<4) strBuf.append(fill(msg));      // pad with spaces
    else for(int i=0;i<4;i++) strBuf.append(msg.charAt(i)); // else truncate

    strBuf.append(zero);                    // Append LED & decimal codes
    strBuf.append(zero);                    // Append colon codes
    strBuf=led(strBuf);                     // Add LEDs, colon & decimals
    strBuf=decimal(strBuf);                 // Add decimal codes from message
    txUart.sendString(strBuf.toString());   // Send to AppMod
    CPU.delay(wait);                        // Pause
  }//end method: display


/**
 * This will turn all LEDs off and clear the screen.
 */
  public void clear() {
    dec1     = false;                       // Turn off decimal #1
    dec2     = false;                       // Turn off decimal #2
    dec3     = false;                       // Turn off decimal #3
    dec4     = false;                       // Turn off decimal #4
    led1     = false;                       // Turn off discrete LED #1
    led2     = false;                       // Turn off discrete LED #2
    led3     = false;                       // Turn off discrete LED #3
    led4     = false;                       // Turn off discrete LED #4
    colonTop = false;                       // Turn off top of colon
    colonBot = false;                       // Turn off bottom of colon
    strBuf.clear();                         // Clear StringBuffer
    strBuf.append("!LT0ASC    ");           // Append Header Codes with 4 ASCII blanks
    strBuf.append(zero);                    // Append LED & decimal codes
    strBuf.append(zero);                    // Append colon code
    txUart.sendString(strBuf.toString());   // Send to AppMod
  }//end method: clear


/**
 * The setTime method will set the internal clock, and display it.
 * When this device is communicating with the stamp, its internal clock
 * will halt.  When communication is complete, it will try to estimate the
 * correct time and adjust.  For most accurate time, keep communication
 * with AppMod to a minimum.
 * @param day  Time in day(s):    0-99
 * @param hour Time in hour(s):   0-23
 * @param min  Time in minute(s): 0-59
 * @param sec  Time in second(s): 0-59
 */
  public void setTime(int day, int hour, int min, int sec){
    timeFix();                              // Workaround Time bug;
    strBuf.clear();                         // Clear StrBuf
    strBuf.append("!LT0RTI");               // Real Time clock Initialize directive
    strBuf.append((char)day);               // Set day counter
    strBuf.append((char)hour);              // Set hour
    strBuf.append((char)min);               // Set minute
    strBuf.append((char)sec);               // Set seconds
    strBuf.append(zero);
    strBuf.append(zero);
    txUart.sendString(strBuf.toString());   // Send to AppMod
  }//end method: setTime

/**
 * This method will display or hide the time
 * @param display - Set TRUE to display time, set to FALSE to hide time
 */
  public void displayTime(boolean display){
    if (display) {
      timeFix();
      txUart.sendString("!LT0RTS");
      txUart.sendString("!LT0RTS");
    }
    else {
      txUart.sendString("!LT0RTH");
      display("",0);
    }
  }//end method: displayTime


//============================================================================
// Private methods and fields below this point.
//============================================================================
  private static Uart txUart;               // Transmit UART
  private static StringBuffer strBuf;       // Protocol Line for AppMod display
  private static StringBuffer sb;           // Misc Stringbuffer (no gc)

  private final static char dec[]  ={0x01,0x02,0x04,0x08}; // LowByte decimal values
  private final static char led[]  ={0x10,0x20,0x40,0x80}; // LowByte LED values
  private final static char colon[]={0x01,0x02};           // HighByte Colon values

// Called internally with the 13 byte stringBuffer before tranmitting to AppMod
// This method will add any of the boolean LED variables if set or will remove
// them if they are not set.  This method will replace the LowByte
// No error checking performed if stringBuffer is not 13 characters.
  private StringBuffer led(StringBuffer s){
    char c = s.charAt(11);                  // Extract LED code

    // Sets LED codes to match the public boolean values
    if (led1) c|=led[0];
    else c&=~led[0];
    if (led2) c|=led[1];
    else c&=~led[1];
    if (led3) c|=led[2];
    else c&=~led[2];
    if (led4) c|=led[3];
    else c&=~led[3];

    // Sets decimal codes to match the public boolean values
    if (dec1) c|=dec[0];
    else c&=~dec[0];
    if (dec2) c|=dec[1];
    else c&=~dec[1];
    if (dec3) c|=dec[2];
    else c&=~dec[2];
    if (dec4) c|=dec[3];
    else c&=~dec[3];

    // Store the new code
    s.insert(11,c);
    s.delete(12,13);

    c = s.charAt(12);                       // Extract colon code
    // Sets colon codes to match the public boolean values
    if (colonTop) c|=colon[0];
    else c&=~colon[0];
    if (colonBot) c|=colon[1];
    else c&=~colon[1];

    // Store the new code
    s.insert(12,c);
    s.delete(13,14);

    return (s);
  }// end method: led

// This method will pad a less-than 4 character message with leading spaces
  private String fill(String s){
    sb.clear();                             // Clear StringBuffer
    for (int i=1;i<=(4-s.length());i++) sb.append(" ");    // Pad sb with spaces
    for (int i=0;i<s.length();i++) sb.append(s.charAt(i)); // Copy msg data over
    s=sb.toString();                        // Copy the new data back into string
  return (s);
  }// end method: fill


// Called internally with the 13 byte stringBuffer before transmitting to AppMod
// This method will search for '.' and replace with a space and the correct HighByte
// No error checking performed if stringBuffer is not 13 characters.
// This method must be called AFTER the led method for the decimal to remain
  private StringBuffer decimal(StringBuffer s) {
    for (int i=7;i<=10;i++) {               // Loop thru LED message
      if (s.charAt(i)=='.') {               // If a decimal is found then
        s.insert(i,' ');                    // Replace with a space
        s.delete(i+1,i+2);

        // (5-(i-6) converts index i into correct LED Display
        // 1<<(q-1) converts LED# to decimal control code
        // S.charAt(11) reads existing decimal control code
        // then ORs the contents of current decimal code with new code
        s.insert(11,(char)((1<<(((5-(i-6)))-1))|(s.charAt(11))));
        s.delete(12,13);
      }// end if
    }//end for
  return (s);
  }//end method: decimal


  // This is a fix for the time display, which does not set itself to the proper mode.
  private void timeFix(){
    strBuf.clear();
    strBuf.append("!LT0BIN");
    strBuf.append(zero);
    strBuf.append(zero);
    strBuf.append(zero);
    strBuf.append(zero);
    txUart.sendString(strBuf.toString());
  }//end method: timeFix

}//end LedMsg Class