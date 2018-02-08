/*
 * Copyright © 2002 Parallax Inc. All rights reserved.
 *
 * This class fully demonstrates Parallax's DS1302 class.
 * It exercises all the major methods.  And has detailed comments for your use.
 * Version 1.0
 */

import stamp.core.*;
import stamp.peripheral.rtc.DS1302;

public class DS1302Demo {

  final static char HOME = 0x01;            // Position cursor upper-left
  final static char CLS  = '\u0010';        // Clear Screen

  public static void main() {

    // Example for the DS1302 class constructor
    // Create new DS1302 object: pin1=data, pin2=clock, pin3=reset
    DS1302 t = new DS1302(CPU.pin1,CPU.pin2,CPU.pin3);

    // Example of writeTime
    // Set time to initial state: hour, min, sec, month, date, year, day_of_week
    t.writeTime(23, 58, 15, 12, 31, 2, 3);


    // Example for charge
    // This method will charge a SuperCap which can be used for uninterrupted power
    t.charge(true);

    // Force update for Time & Date Strings, this is normally done in each
    // of the methods.
    t.updateTimeDate();

    // Example for: updateTimeDate, readTime and readDate in 12hr format
    // This loop will display the Time(12hr) & Date
    System.out.println(CLS);                         // Clear the screen
    for(int x=1;x<100;x++) {
      System.out.print(HOME);                        // HOME screen
      System.out.print("Time(12hr): ");
      System.out.println(t.readTime(true));          // Display Time (12hr)
      System.out.print("Date: ");
      System.out.println(t.readDate());              // Display Date
      CPU.delay(100);   // one second
    }//end for


    // Example for: updateTimeDate, readTime and readDate in 24hr format
    // This loop will display the Time(24hr) & Date
    for(int x=1;x<100;x++) {
      System.out.print(HOME);                        // HOME screen
      System.out.print("Time(24hr): ");
      System.out.println(t.readTime(false));         // Display Time (24hr)
      System.out.print("Date: ");
      System.out.println(t.readDate());              // Display Date
      CPU.delay(100);   // one second
    }//end for

    // Example for: readTimeDate
    // This method will read the Time & Date into a string, then display the data
    int [] array = new int [7];                      // create array
    array=t.readTimeDate();                          // read data into array
    System.out.println(" ");
    for(int x=0;x<array.length;x++) {                // display data
      System.out.print("Array[");
      System.out.print(x);
      System.out.print("]=");
      System.out.println(array[x]);
    }//end for
    CPU.delay(32000);

    // Example for readDay
    // True = Long format, False = Short format
    System.out.print(CLS);                          // Clear screen
    System.out.print("Day Short: ");
    System.out.println(t.readDay(false));           // Display short day
    CPU.delay(32000);
    System.out.print("Day Long: ");
    System.out.println(t.readDay(true));            // Display full day
    CPU.delay(32000);

    // Example for readTD
    // This method will allow you to read a specific time or date register
    // directly from the DS1302 class by passing in a corresponding index:
    // 0=second, 1=minute, 2=hour, 3=date, 4=month, 5=dayOfWeek 6=year
    System.out.print(CLS);                           // Clear The Screen
    for (int x=0;x<200;x++){
      System.out.print(HOME);                        // Position cursor
      System.out.print("Data read in from 'readTD(0)' ");
      System.out.println(t.readTD(0));
    }//end for

    // Example for readRawTD
    // Read a specific DS1302 chip register contents in the RAW, unformatted form.
    // This is done by passing a DS1302 command specific for the data you wish
    // to receive.
    // Here is code to add so you can reference the commands
    // final READ_YEAR   = 0x8d;
    // final READ_MONTH  = 0x89;
    // final READ_DAY    = 0x8b;
    // final READ_DATE   = 0x87;
    // final READ_HOUR   = 0x85;
    // final READ_MINUTE = 0x83;
    // final READ_SECOND = 0x81;
    final int READ_HOUR = 0x85;                      // Set the constant
    System.out.print(CLS);                           // Clear The Screen
    System.out.print("Data read in from 'readRawTD(READ_HOUR)' ");
    System.out.println(t.readRawTD(READ_HOUR));
    CPU.delay(32000);
    System.out.print(CLS);                           // Clear The Screen

    // Example for readRam
    // The DS1302 chip contains 31 bytes of RAM.  This method reads the ram for a
    // specific location.  This method will autowrap RAM values above 31 bytes.
    // Example: RAM location 32 = Ram location 0
    System.out.println("Read contents of RAM locations 0-30");
    for (int x=0;x<31;x++) {                         // Loop thru all RAM 0-30
      System.out.print(t.readRam(x));
      System.out.print(", ");
    }//end for
    System.out.println(" ");
    CPU.delay(32000);

    // Example for writeRam
    // The DS1302 chip contains 31 bytes of RAM.  This method writes the ram for a
    // specific location.  This method will autowrap RAM values above 31 bytes.
    // Example: RAM location 31 = Ram location 0
    System.out.println("\nWrite contents of RAM locations 0-30");
    for (int x=0,y=10;x<31;x++,y++) {                // Loop thru all RAM 0-30
      t.writeRam(x,y);
      System.out.print(y);
      System.out.print(", ");
    }//end for
    System.out.println(" ");
    CPU.delay(32000);

    // Now let's read the RAM again, to verify
    System.out.println("\nRead contents of RAM locations 0-30");
    for (int x=0;x<31;x++) {                         // Loop thru all RAM 0-30
      System.out.print(t.readRam(x));
      System.out.print(", ");
    }//end for
    System.out.println(" ");
    CPU.delay(32000);

    // Example of halt
    // Pass a true and the clock will halt, pass a false and it will continue
    System.out.println(CLS);                         // Clear the screen
    // Loop to verify the clock is working
    for(int x=1;x<100;x++){
      System.out.print(HOME);
      System.out.println("Status of clock: Working");
      System.out.print("Time: ");
      System.out.println(t.readTime(true));
      System.out.print("Date: ");
      System.out.println(t.readDate());
    }

    t.halt(true);                                    // halt the clock
    // Loop to verify the clock has halted
    for(int x=1;x<100;x++){
      System.out.print(HOME);
      System.out.println("Status of clock: Halted ");
      System.out.print("Time: ");
      System.out.println(t.readTime(true));
      System.out.print("Date: ");
      System.out.println(t.readDate());
    }

    t.halt(false);
    // Loop to verify the clock continued from it's halted state
    for(int x=1;x<100;x++){
      System.out.print(HOME);
      System.out.println("Status of clock: Working");
      System.out.print("Time: ");
      System.out.println(t.readTime(true));
      System.out.print("Date: ");
      System.out.println(t.readDate());
    }

    // Example of protect
    // The protect method protects the chip from writes.  It's a write protect.
    // TRUE will set the protection so the chip will not accept any writes
    // FALSE will remove this protection.
    System.out.print(CLS);                               // Clear the screen
    System.out.print("Example of protect method.  Current RAM contents: ");
    System.out.println(t.readRam(20));                   // Contents of byte#20
    CPU.delay(32000);
    t.protect(true);                                     // Enable write protect
    System.out.print("Write Protect ON: ");
    t.writeRam(20,111);                                  // Write to byte#20
    System.out.println(t.readRam(20));                   // Contents of byte#20
    CPU.delay(32000);
    t.protect(false);                                    // Disable protection
    System.out.print("Write Protect OFF: ");
    t.writeRam(20,111);                                  // Write to byte#20
    System.out.println(t.readRam(20));                   // Verify write
    CPU.delay(32000);


    System.out.println(CLS);
    while (true) {
      System.out.print(HOME);
      System.out.print("Time 12hr: ");
      System.out.println(t.readTime(true));
      System.out.print("Time 24hr: ");
      System.out.println(t.readTime(false));
      System.out.print("Date: ");
      System.out.println(t.readDate());
      System.out.print("Day Long: ");
      System.out.println(t.readDay(true));
      CPU.delay(100);   // one second
    }//end while

  }// end main
}//end class: DS1302Demo