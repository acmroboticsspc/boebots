import stamp.core.*;
import stamp.peripheral.io.ADC.*;

/*
 * This class tests the ADC0831 circuit from the Application Note #007.
 * Version 1.0 - 12/12/02
 */

public class ADC0831Test {

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
}// end class: ADC0831Test