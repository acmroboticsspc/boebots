import stamp.core.*;
import stamp.peripheral.io.ADC.*;

/*
 * This class will demonstrate the AtoD library class (AppNote006) using the
 * ADC0831 as an example.  For this class, set vRef at +5V, and tie -Vin to
 * ground.  For other chip configurations please see the ADC0831 class AppNote007.
 *
 * Version 1.0 - 11/29/02
 */

public class AtoD_Demo {

  final static char HOME = 0x01;                 // Position cursor upper-left
  final static char CLS = '\u0010';              // Clear Screen

  public static void main() {
    System.out.print(CLS);                      // position cursor

    // Example of: The ADC0831 constructor
    // Create a new ADC0831 object: pin5=data, pin6=clock, pin7=Chip Select
    // This class extends the AtoD class, all AtoD methods will be availalbe.
    ADC0831 adc = new ADC0831(CPU.pin0,CPU.pin6,CPU.pin7);

    while(true) {                               // do forever
      System.out.print(HOME);                   // position cursor

      // Example of: ADC0831 Library's read
      adc.read(0);                              // read/store calculated values

      // Example for: AtoD's lastRaw
      // Will display the last read RAW value from the ADC0831 chip.
      System.out.print("Current RAW value received from Chip: ");
      System.out.print(adc.lastRaw());
      System.out.println("   ");

      // Example of: AtoD's lastVf
      // Will display the formatted voltage.
      System.out.print("Formatted RAW value for volts: ");
      System.out.print(adc.lastVf());             // display voltage
      System.out.print(" volts");
      System.out.println("   ");

      // Example of: AtoD's lastMV
      // Will display just the last known millivolts.
      System.out.print("Display only the millivolt part: ");
      System.out.print(adc.lastMV());            // display millivolts
      System.out.println("   ");

      // Example of: AtoD's readSmooth, lastVf methods
      // readSmooth will read the average RAW value.
      // Can read 2, 4, 8, 16, and 32 times.
      // Input 1,2,3,4,5 for reading 2,4,8,16,32 times.
      // lastVf will display the formatted voltage from last read.
      // The response time on this method will increase with higher reads.
      System.out.print("\nAverage volts of 4 reads: ");
      adc.readSmooth(0,4);                      // 0=ADC chip, 18=num of reads
      System.out.print(adc.lastVf());            // calculate & display volts
      System.out.print(" volts");
      System.out.println("    ");

      // Example for: AtoD's calcTemp
      // Display last value read into the temperature format,
      // for use with the LM34 series chips.
      // Sending a boolean true will attach a 'F' to the temperature.
      System.out.print("\nConvert last value into a temperature format: ");
      System.out.print(adc.calcTemp(true));
      System.out.println("    ");

    }// end while
  }// end method: main
}// end class: AtoD_Demo