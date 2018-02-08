import stamp.core.*;
import stamp.peripheral.io.ADC.*;

/*
 * This class will fully demonstrate the LTC1298, as well as the AtoD
 * library classes.  The AtoD abstract library has many methods which you
 * can use for you LTC1298 library.
 *
 * This class uses the readSmooth method which reads from the ADC chip multiple
 * times and can delay the response time of any adjustments to the potentiometer.
 *
 * Version 1.0 - 11/29/02
 */

public class LTC1298Demo {

  final static char HOME = 0x01;                 // Position cursor upper-left
  final static char CLS = '\u0010';              // Clear Screen

  public static void main() {
    System.out.print(CLS);                       // position cursor

    // Example of: The LTC1298 constructor
    // Create a new LTC1298 object: pin1=data, pin2=clock, pin4=Chip Select
    LTC1298 adc = new LTC1298(CPU.pin1,CPU.pin2,CPU.pin4);

    while(true) {                                // do forever
      System.out.print(HOME);                    // position cursor

      // Loop through both channels
      for (int x=0;x<2;x++){

        adc.read(x);                             // read channel 'x'

        System.out.print("CHANNEL #");
        System.out.print(x);

        // Example for: AtoD's lastRaw
        // Will display the last read RAW value from the LTC1298 chip
        System.out.print("\nLast RAW value received: ");
        System.out.print(adc.lastRaw());
        System.out.println("   ");

        // Example of: AtoD's lastVf
        // Will display last known measurement as a formatted voltage
        System.out.print("Display the formatted RAW value: ");
        System.out.print(adc.lastVf());          // display volts
        System.out.print(" Volts");
        System.out.println("   \n");
      }// end for

      // Example of: AtoD's readSmooth, lastVf methods
      // readSmooth will read the average RAW value.
      // Can read 2, 4, 8, 16, and 32 times.
      // Input 1,2,3,4,5 for reading 2,4,8,16,32 times.
      // lastVf will display the formatted voltage from last read.
      System.out.print("Average voltage of 8 reads from channel 0: ");
      adc.readSmooth(0,3);                       // 0=channel, 16=num of reads
      System.out.print(adc.lastVf());            // recall & display volts
      System.out.print(" volts");
      System.out.println("    ");

      // Example of: AtoD's lastMV
      // Will display last known voltage in millivolts
      System.out.print("Display last read RAW value as millivolts from channel 0: ");
      System.out.print(adc.lastMV());      // display millivolts
      System.out.println(" millivolts    \n");

      // Example for: AtoD's calcTemp
      // Display last value read into the temperature format,
      // for use with the LM34 series chips.
      // Sending a boolean true will attach a 'F' to the temperature.
      System.out.print("Convert last value from channel 1 into temperature format: ");
      adc.readSmooth(1,3);                       // 1=ADC chip, 18=num of reads
      System.out.print(adc.calcTemp(true));
      System.out.println("    \n");

      // Example for reading the difference between channel 1 from 0
      // Use read(2), and the LTC1298 will return a RAW value that corisponds
      // to the difference between 0 and 1, note 0 must be larger.
      adc.read(2);
      System.out.print("Difference between channel 1 from 0: ");
      System.out.print(adc.lastVf());       // display millivolts
      System.out.println(" volts  ");

      // Example for reading the difference between channel 0 from 1
      // Use read(2), and the LTC1298 will return a RAW value that corisponds
      // to the difference between 0 and 1, note 1 must be larger.
      adc.read(3);
      System.out.print("Difference between channel 0 from 1: ");
      System.out.print(adc.lastVf());       // display millivolts
      System.out.println(" volts  ");

    }// end while
  }// end method: main
}// end class: ADC0831Demo