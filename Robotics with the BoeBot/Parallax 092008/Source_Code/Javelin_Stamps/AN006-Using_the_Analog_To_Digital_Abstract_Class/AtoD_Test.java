import stamp.core.*;
import stamp.peripheral.io.ADC.*;

/*
 * Tests the ADC0831 circuit from the Application Note #006.
 * Version 1.0 - 11/29/02
 */

public class AtoD_Test {

  final static char HOME = 0x01;          // Position cursor upper-left
  final static char CLS = '\u0010';       // Clear Screen

  public static void main() {
    ADC0831 adc = new ADC0831(CPU.pin0,CPU.pin6,CPU.pin7);  // create ADC object
    System.out.print(CLS);                                  // Clear the display

    do {
      System.out.print(HOME);             // position cursor
      adc.read(0);                        // read and store values
      System.out.print(adc.lastVf());     // display formatted volts
      System.out.println(" volts ");
    } while (true);                       // do forever

  }// end method: main
}// end class: AtoD_Test