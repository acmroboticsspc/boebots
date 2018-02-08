import stamp.core.*;
import stamp.peripheral.io.ADC.*;

/*
 * Tests the LTC1298 circuit from the Application Note #008.
 * Version 1.0 - 11/29/02
 */

public class LTC1298Test {

  final static char HOME = 0x01;            // Position cursor upper-left
  final static char CLS = '\u0010';         // Clear Screen

  public static void main() {
    LTC1298 adc = new LTC1298(CPU.pin1,CPU.pin2,CPU.pin4);  // Create ADC object
    System.out.print(CLS);                                  // Clear the display

    do {
      System.out.print(HOME);               // position cursor

      // Test channel 0
      adc.read(0);                          // read channel 0, store values
      System.out.print("Channel 0: ");
      System.out.print(adc.lastVf());       // display formatted volts
      System.out.print(" volts ");

      // Test channel 1
      adc.read(1);                         // read channel 1, store values
      System.out.print("\nChannel 1: ");
      System.out.print(adc.lastVf());      // display formatted volts
      System.out.print(" volts ");

    } while (true);                        // do forever

  }// end method: main
}// end class: LTC1298Test