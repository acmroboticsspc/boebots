import stamp.math.*;
import stamp.core.*;

/*
 * A FloatLite6 example for Application Note #013.
 * This example will allow you to add or subtract 2 float numbers.
 * Version 1.0 - 03/20/03
 */
public class FloatLite6Adder {

  public static void main() {

    FloatLite6 FL1 = new FloatLite6("0");   // FloatLite6 object
    FloatLite6 FL2 = new FloatLite6("0");   // FloatLite6 object

    StringBuffer sb = new StringBuffer(12); // Temp buffer
    int inputValue;                         // Keyboard input value
    char op;                                // Operation (Addition/Subtraction)

    System.out.println("This example will allow you to add or subtract 2 values"+
                       " with a range from -32767.999999 to +32767.999999");
    System.out.println("You may select '+' for addition, '-' for subtraction "+
                       "or 'q' to quit.");

    while (true) {
      // Get operation (+/-) from Javelin Terminal Window
      System.out.print("\nSelect Operation (+- or q): ");
      op = (char)Terminal.getChar();
      Terminal.getChar();
      if (op=='q'||op==13)
        break;
      if (op!='+' && op!='-')  {
        System.out.println("Not an operation.");
        continue;
      }

      // Get 1st value from Terminal Window
      sb.clear();
      System.out.print("\nEnter first value: ");
      inputValue = Terminal.getChar();
      if (inputValue==13) sb.append(0);
      while (inputValue!=13) {
        sb.append((char)inputValue);
        inputValue = Terminal.getChar();
      }

      FL1.setValue(sb);                     // Store as float

      // Get 2nd value from Terminal Window
      sb.clear();
      System.out.print("\nEnter the second value: ");
      inputValue = Terminal.getChar();
      if (inputValue==13) sb.append(0);
      while (inputValue!=13) {
        sb.append((char)inputValue);
        inputValue = Terminal.getChar();
      }
      FL2.setValue(sb);                     // Store as float

      // Print the 2 values entered (if invalid a zero will substitute)
      System.out.print("\n\n    ");
      align(FL1);
      System.out.println(FL1.toString());
      System.out.print("(");
      System.out.print(op);
      System.out.print(") ");

      align(FL2);
      System.out.println(FL2.toString());
      System.out.println("-----------------");

      // Perform calculation based on operation supplied by user
      switch (op) {
        case '+' : FL1.add(FL2);
                   break;
        case '-' : FL1.subtract(FL2);
                   break;
      }

      // Print the answer for the specified operation
      System.out.print("    ");
      align(FL1);
      System.out.println(FL1.toString());
    }// end while

    System.out.println("\nDone");

  }// end method: main

  /* Align with spaces so decimal points are in-line */
  static public void align(FloatLite6 FL){
    int x=FL.getInteger();
    int xNeg=1;
    if (x==0) xNeg++;
    if (FL.getSign()) xNeg--;
    int xLen=0;
    while (x>0){
      x/=10;
      xLen++;
    }
    for(x=0;x<(6-xLen-xNeg);x++)
      System.out.print(" ");
  }
}// end class: FloatLite6Adder