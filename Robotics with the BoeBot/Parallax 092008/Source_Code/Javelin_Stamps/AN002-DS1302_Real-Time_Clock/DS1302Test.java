/*
 * Copyright © 2002 Parallax Inc. All rights reserved.
 *
 * This class tests the DS1302 circuit from the Application Note #002.
 * Version 1.0
 */

import stamp.core.*;
import stamp.peripheral.rtc.DS1302;

public class DS1302Test {

  final static char HOME = 0x01;            // Position cursor upper-left
  final static char CLS = '\u0010';         // Clear Screen

  public static void main() {

    // Example of the DS1302 class constructor
    // Create new DS1302 object: pin1=data, pin2=clock, pin3=reset
    DS1302 t = new DS1302(CPU.pin1,CPU.pin2,CPU.pin3);

    // Example of writeTime
    // Set time to initial state: hour, min, sec, month, date, year, day_of_week
    t.writeTime(23, 58, 15, 12, 31, 2, 3);

    System.out.println(CLS);
    while (true) {
      System.out.print(HOME);
      System.out.print("Date: ");
      System.out.println(t.readDate());
      System.out.print("Time 12hr: ");
      System.out.println(t.readTime(true));
      CPU.delay(100);   // one second
    }//end while

  }// end main
}//end class: DS1302Test