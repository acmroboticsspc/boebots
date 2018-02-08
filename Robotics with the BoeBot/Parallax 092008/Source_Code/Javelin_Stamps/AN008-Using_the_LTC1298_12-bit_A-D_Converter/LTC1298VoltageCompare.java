import stamp.core.*;
import stamp.peripheral.io.ADC.*;

/*
 * This class will fully demonstrate the LTC1298 chips abilities.
 * It will read the individual channels 0 and 1 as well as compare the voltage
 * difference between them.
 *
 * Version 1.0 - 11/29/02
 */

public class LTC1298VoltageCompare {

  final static char HOME = 0x01;                 // Position cursor upper-left
  final static char CLS = '\u0010';              // Clear Screen

  public static void main() {
    System.out.print(CLS);                       // position cursor

    // Create a new LTC1298 object: pin1=data, pin2=clock, pin4=Chip Select
    LTC1298 adc = new LTC1298(CPU.pin1,CPU.pin2,CPU.pin4);

    while(true) {                                // do forever
      System.out.print(HOME);                    // position cursor

      // Loop through all channels
      for (int x=0;x<4;x++){

        adc.read(x);                             // read channel 'x'

        System.out.print("CHANNEL #");
        System.out.print(x);

        if (x==2)System.out.print("\nDifference between channel 1 from 0");
        else if (x==3)System.out.print("\nDifference between channel 1 from 0");

        // Will display the last read RAW value from the LTC1298 chip
        System.out.print("\nLast RAW value received: ");
        System.out.print(adc.lastRaw());
        System.out.println("   ");

        // Will display last known RAW value as a formatted voltage
        System.out.print("Display the formatted RAW value as a voltage: ");
        System.out.print(adc.lastVf());          // display volts
        System.out.print(" Volts");
        System.out.println("   \n");
      }// end for

    }// end while
  }// end method: main
}// end class: LTC1298VoltageCompare