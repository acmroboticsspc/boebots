// Author: Parallax Inc. (javelintech@parallaxinc.com)
// Version: 1.0 Sept 23, 2002

import stamp.core.*;
import stamp.peripheral.appmod.*;

// This class is for the Parallax Inc "LED Terminal AppMod"

public class LedTerminalDisplay {
  public static void main() {
    LedTerminal msg  = new LedTerminal();   // Create the Led Message Object

    for(int i=0;i<10;i++){                  // Loop for 10 times
      msg.display("XOOX",1000);             // Display the message "xoox"
      msg.display("OXXO",1000);             // Display the message "oxxo"
    }//end for

    // Use the banner routine to scroll messages, you set the delay.
    msg.banner("The time will be set to 10am",2000);   // msg, delay

    msg.setTime(0,10,0,0);                  // day, hour, min, sec
    msg.displayTime(true);                  // Display current Time

  }// end main
}//end class