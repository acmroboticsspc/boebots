import stamp.core.*;
import stamp.peripheral.io.ADC.*;

/*
 * This class will allow you to set the bit value to match the input voltage
 * on vRef.  The voltage on vRef can be set by using a potentiometer connected
 * to the vRef pin.  A voltmeter will be needed to watch/verify the volts at vRef.
 * For complete instructions on using this class, refer to Application Note #007.
 *
 * Version 1.0 - 12/12/02
 */

public class ADC0831CustomBitValue {

  final static char HOME = 0x01;                 // Position cursor upper-left
  final static char CLS = '\u0010';              // Clear Screen

  public static void main() {
    System.out.print(CLS);                      // position cursor

    // Create a new ADC0831 object: pin5=data, pin6=clock, pin7=Chip Select
    ADC0831 adc = new ADC0831(CPU.pin0,CPU.pin6,CPU.pin7);

    while(true) {                               // do forever
      System.out.println("Step 1) Measure the millivolts at vRef with a voltmeter.");
      System.out.println("Step 2) Divide this value by 256 (8-bits).");
      System.out.println("The whole number on the left side of the decimal is your highBit value");
      System.out.print("Step 3) Enter this number here: ");
      int highBit=getInt();
      System.out.println("\nStep 4) Take the decimal portion and multiply by 65536.");
      System.out.println("If your answer from above is greater than 32767 then,");
      System.out.println("Step 5) subtract 65536.");
      System.out.println("This is your lowBit value, it might be negative");
      System.out.print("Step 6) Enter this value including the negative: ");
      int lowBit=getInt();
      adc.setBitValue(highBit,lowBit,0);

      System.out.print(CLS);
      for (int loop=100;loop>0;loop--){

        System.out.print(HOME);                      //Clear display
        System.out.print("Reads remaining: ");
        System.out.print(loop);
        System.out.println("  ");

        // Read the current voltage
        System.out.print("\nMeasured Value: ");
        adc.read(0);                            // 0=ADC chip
        System.out.print(adc.lastVf());         // display formatted volts
        System.out.print(" volts");
        System.out.println("    ");

      }//end for
      System.out.println(CLS);

    }// end while
  }// end method: main

  // Terminal Helper method which reads an integer from the keyboard.
  static char keyboardChar;
  static StringBuffer keyboardMsg = new StringBuffer(30);

  public static int getInt() {
    keyboardMsg.clear();
    while ( (keyboardChar = Terminal.getChar()) != '\r' ) {
      keyboardMsg.append(keyboardChar);
    }// end while
    return Integer.parseInt(keyboardMsg);
  }// end method: getInt

}// end class: ADC0831CustomBitValue
