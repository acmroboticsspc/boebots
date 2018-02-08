/*
 * Copyright © 2002 Parallax Inc. All rights reserved.
 *
 * Using the DS1302 circuit from the Application Note #002 to be used as a
 * stop watch.
 * Version 1.0
 */

import stamp.core.*;
import stamp.peripheral.rtc.DS1302;

public class DS1302StopWatch {

  final static char HOME = 0x01;            // Position cursor upper-left
  final static char CLS = '\u0010';         // Clear Screen

  public static void main() {

    // Create new DS1302 object: pin1=data, pin2=clock, pin3=reset
    DS1302 sw = new DS1302(CPU.pin1,CPU.pin2,CPU.pin3);
    StringBuffer SB = new StringBuffer(9);  // Buffer to xfer data

    System.out.println(CLS);                // Clear Screen
    sw.writeTime(0, 0, 0, 0, 0, 0, 0);      // Set Time to zeros
    sw.halt(true);                          // halt clock

    while (true) {
      System.out.print(HOME);
      System.out.println("DS1302 Stop Watch\n");
      System.out.println("Select 'g' to go");
      System.out.println("Select 's' to stop");
      System.out.println("Select 'r' to reset stop watch");
      System.out.println("Select 'c' to clear screen");
      System.out.println("Select 'a' to archive time to RAM");
      System.out.println("Select 'd' to display archived time from RAM");
      System.out.print("\nPlease make a selction ");

      switch (Terminal.getChar()){

        case 'g':
          sw.halt(false);                        // Start clock
          System.out.print("\nStarted at: ");
          System.out.println(sw.readTime(true));
        break;

        case 's':
          sw.halt(true);                         // Stop clock
          System.out.print("\nStopped at: ");
          System.out.println(sw.readTime(true));
        break;

        case 'r':
          sw.writeTime(0, 0, 0, 0, 0, 0, 0);     // Set time to zeros
          sw.halt(true);                         // Stop clock
          System.out.print("\nClock Reset ");
          System.out.println(sw.readTime(true));
        break;

        case 'c':
          System.out.println(CLS);               // Clear screen
        break;

        case 'a':
          SB.clear();
          SB.append(sw.readTime(true));          // Store time into SB
          for (int x=0;x<9;x++) sw.writeRam(x,SB.charAt(x));  // store SB to RAM
          System.out.print("\nClock Saved           ");
        break;

        case 'd':
        System.out.print("\nFrom Ram: ");
        for (int x=0;x<9;x++) System.out.print((char)sw.readRam(x)); // read RAM
        System.out.print("  ");
        break;

      }//end case
    }//end while
  }// end main
}//end class: DS1302StopWatch
