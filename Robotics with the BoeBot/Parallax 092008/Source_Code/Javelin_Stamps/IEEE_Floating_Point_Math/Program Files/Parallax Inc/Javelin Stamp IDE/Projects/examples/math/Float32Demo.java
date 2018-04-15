/*
 * Copyright © Micromega Corporation 2002-2003. All rights reserved
 */
import stamp.math.*;
import stamp.core.*;
/**
 * Demonstration routine for the Float32 class.
 *
 * @author Cam Thompson, Micromega Corporation
 * Copyright © Micromega Corporation 2002-2003. All rights reserved
 *
 * @version V1.2 - May 2, 2003
 * changes:
 *    V1.2 - May 2, 2003
 *      - fixed comments
 *    V1.1 - April 30, 2003
 *      - changes Float32 package location
 *    V1.0 - April 24, 2003
 *      - original version
 */

public class Float32Demo {

  // variables used in examples
  static int    nint    = -10000;
  static short  nshort  = 20000;
  static byte   nbyte   = -100;
  static char   nchar   = 200;
  static char   count   = 8;

  static Float32 pi            = new Float32();
  static Float32 diameterIn    = new Float32();
  static Float32 circumference = new Float32();
  static Float32 area          = new Float32();

  public static void main() {

    System.out.println("\u0010Float32Demo\r\n");
    System.out.println("This program demonstrates the use of the Float32 class.");

    //-------------------- Constructors ---------------------------------------
    // There are four types of constructor method calls:
    //    new Float32()
    //    new Float32(Float32 fnum)
    //    new Float32(int num)
    //    new Float32(int high, int low)
    System.out.println("\r\nConstructors");

    // Create a new Float32 object with initial value of zero.
    Float32 fnum1 = new Float32();

    // Create a new Float32 object with the initial value set to the
    // value of another Float32.
    Float32 fnum2 = new Float32(fnum1);

    // Create a new Float32 object with the initial value set to the
    // value of an integer.
    Float32 fnum3 = new Float32(nint);

    // Create a new Float32 object with the initial value set to the
    // value of an integer constant.
    Float32 fnum4 = new Float32(250);

   // Create a new floating point number and set to the binary floating point
   // value (internal representation contained in two 16-bit integers).
   // 10000000.0 (hex: 4B189680)
    Float32 fnum5 = new Float32(0x4B18, (short)0x9680);

    // Display results of the constructors
    System.out.print("fnum1 = ");
    System.out.println(fnum1.toString());
    System.out.print("fnum2 = ");
    System.out.println(fnum2.toString());
    System.out.print("fnum3 = ");
    System.out.println(fnum3.toString());
    System.out.print("fnum4 = ");
    System.out.println(fnum4.toString());
    System.out.print("fnum5 = ");
    System.out.println(fnum5.toString());

    //-------------------- Using high and low fields --------------------------
    // An Float32 object contains two fields that are public:
    //    high - contains the high 16-bits of the floating point value
    //    low - contains the low 16-bits of the floating point value
    // These can be used whenever direct access to the high or low 16-bits
    // of the floating point number is useful.
    System.out.println("\r\nUsing high and low fields");

    // Check if fnum1 is zero (checks for both positive and negative zero)
    if ((fnum1.high & 0x7FFF) == 0 && fnum1.low == 0) {
      System.out.println("fnum1 is zero");
    }

    // Check if fnum3 is negative.
    if (fnum3.high < 0) {
      System.out.println("fnum3 is negative");
    }

    //-------------------- set methods ----------------------------------------
    // The set method is used to set the value of Float32 objects.
    // There are five types of set method calls:
    //    set(Float32 fnum)
    //    set(int high, int low)
    //    set(int num)
    //    set(String s)
    //    set(StringBuffer sb)
    System.out.println("\r\nSet Methods");

    // Set the Float32 value to the value of the Float32 argument.
    // fnum1 = fnum5
    fnum1.set(fnum5);

    // Set the Float32 value to the value of the integer argument.
    // fnum2 = nint
    fnum2.set(nint);

    // Note: the compiler supports manipulating large constants as long as
    // the result is compatible with the variable type.  Integer variables
    // are signed, so a compiler error is generated if a positive constant
    // greater than 32767 is created (e.g. 0x8000 or above), in this case
    // you need to cast the constant to short as shown below.

    // Set the floating point value to the binary floating point value
    // (internal representation contained in two 16-bit integers).
    // fnum3 = 10000000.0 (hex: 4B189680)
    fnum3.set(0x4B18, (short)0x9680);

    // Set the Float32 value to the numeric value of the string argument.
    // Note: this is not as efficient as using numeric constants since
    // the string must be converted at runtime.
    // fnum4 = 625.0125
    fnum4.set("625.0125");

    // Set the Float32 value to the numeric value of the string contained
    // in the StringBuffer. Note: leading whitespace is ignored and conversion
    // ends at the first character that is not a decimal digit.
    // fnum5 = -0.00000015
    StringBuffer sbuf = new StringBuffer(" -1.5e-7xxx");
    fnum5.set(sbuf);

    // Display results of the set methods.
    System.out.print("fnum1 = ");
    System.out.println(fnum1.toString());
    System.out.print("fnum2 = ");
    System.out.println(fnum2.toString());
    System.out.print("fnum3 = ");
    System.out.println(fnum3.toString());
    System.out.print("fnum4 = ");
    System.out.println(fnum4.toString());
    System.out.print("fnum5 = ");
    System.out.println(fnum5.toString());

    //-------------------- setToConstant method -------------------------------
    // The setToConstant method is used to set the value to one of the
    // special constants: POSITIVE_INFINITY, NEGATIVE_INFINITY, NaN,
    // MAX_VALUE, MIN_VALUE, or PI.
    // fnum1 = PI
    System.out.println("\r\nSet Constant Method");
    fnum1.setToConstant(Float32.PI);
    System.out.print("fnum1 = ");
    System.out.println(fnum1.toString());

    //-------------------- Arithmetic methods ---------------------------------
    // All Float32 arithmetic methods support four types of method calls.
    // For example, the add method supports the following:
    //    add(Float32 fnum)
    //    add(int num)
    //    add(int high, int low)
    //    add(String)
    // When these three types of method calls are combined with various data
    // types and constants a wide range of operations can be supported.
    System.out.println("\r\nArithmetic Methods");

    // To add an 8-bit signed value, use the byte data type.
    // fnum1 = fnum1 + nbyte
    fnum1.add(nbyte);

    // To add an 8-bit unsigned value, use the char data type.
    // fnum1 = fnum1 + nchar
    fnum1.add(nchar);

    // To add a 16-bit signed value, use int or short data types.
    // fnum1 = fnum1 + nint
    fnum1.add(nint);
    // fnum1 = fnum1 + nshort
    fnum1.add(nshort);

    // To add an integer constant.
    // fnum1 = fnum1 + 100
    fnum1.add(100);
    // fnum1 = fnum1 + (-100)
    fnum1.add(-100);

    // To add another Float32 value.
    // fnum1 = fnum1 + num2;
    fnum1.add(fnum2);
    // fnum1 = fnum1 + 10000000.0
    fnum1.add(0x4B18, (short)0x9680);

    // To add the value of a string.
    // fnum1 = fnum1 + 0.125;
    fnum1.add(".125");

    // In the following examples only one type of method call is demonstrated,
    // but all of these methods support the same combination of methods and
    // data types shown above for the add method.

    // fnum1 = fum1 - fnum2
    fnum1.subtract(fnum2);

    // nfum1 = fnum2 * fnum1
    fnum1.multiply(fnum2);

    // fnum1 = fnum1 / fnum2
    fnum1.divide(fnum2);

    // fnum1 = -fnum1;
    fnum1.negate();

    //-------------------- Special Values -------------------------------------
    // There are no exceptions produced by floating point operations in Java.
    // An operation that overflows produces a signed infinity, and an operation
    // that underflows produces a denormalized number or a signed zero.  An
    // operation that has no mathematically defined result produces
    // Not-a-Number (NaN).
    System.out.println("\r\nSpecial Values");

    // Divide by zero produces infinity
    fnum1.set(100);
    fnum1.divide(0);
    System.out.print("fnum1 = ");
    System.out.println(fnum1.toString());

    // Zero divided by zero produces NaN
    fnum1.set(0);
    fnum1.divide(0);
    System.out.print("fnum1 = ");
    System.out.println(fnum1.toString());

    //-------------------- Comparisons ----------------------------------------
    // The compare method supports the same combination of methods and
    // data types shown above for the arithmetic operations.
    System.out.println("\r\nComparisons");

    // Set the values of fnum1 and fnum2
    fnum1.set(1000);
    fnum2.set(2000);

    // Display the current values of fnum1 and fnum2
    System.out.print("fnum1 = ");
    System.out.println(fnum1.toString());
    System.out.print("fnum2 = ");
    System.out.println(fnum2.toString());

    // Compare fnum1 and fnum2
    if (fnum1.compare(fnum2) > 0) System.out.println("fnum1 > fnum2");
    if (fnum1.compare(fnum2) == 0) System.out.println("fnum1 == fnum2");
    if (fnum1.compare(fnum2) < 0) System.out.println("fnum1 < fnum2");

    // Check if fnum1 >= 1000
    if (fnum1.compare(1000) >= 0) System.out.println("fnum1 >= 1000");

    // Check if fnum1 >= 999.99
    if (fnum1.compare("999.99") >= 0) System.out.println("fnum1 >= 999.99");

    // Check if fnum1 == fnum2
    if (fnum1.equals(fnum2)) System.out.println("fnum1 == fnum2");


    //-------------------- Get Integer Value ----------------------------------

    System.out.println("\r\nGet integer value");
    System.out.print("fnum4 = ");
    System.out.println(fnum4.toString());
    System.out.print("integer value = ");
    System.out.println(fnum4.intValue());

    //-------------------- Convert to String ----------------------------------
    // The value of an Float32 object is converted to a string using the
    // toString method
    System.out.println("\r\nConvert to String");

    // print the value of an Float32 object
    System.out.print("fnum2 = ");
    System.out.print(fnum2.toString());
    System.out.print(", hex value = ");
    System.out.println(fnum2.toHexString());

    //-------------------- Code Sample ----------------------------------------
    // The following example takes an integer value representing the diameter
    // of a circle in centimeters, converts the value to inches and calculates
    // the circumference and area in inches and square inches.

    System.out.println("\r\nConversion Example");
    int diameter = 25;
    System.out.print("Diameter (cm):       ");
    System.out.println(diameter);

    // convert diameter from centimeters to inches
    // diameterIn = diameter / 2.54
    diameterIn.set(diameter);
    diameterIn.divide("2.54");
    System.out.print("Diameter (in.):      ");
    System.out.println(diameterIn.toString());

    // get value of pi
    pi.setToConstant(Float32.PI);

    // circumference = diameter * pi;
    circumference.set(diameterIn);
    circumference.multiply(pi);
    System.out.print("Circumference (in.): ");
    System.out.println(circumference.toString());

    // area = (diameter / 2)^2 * pi;
    area.set(diameterIn);
    area.divide(2);
    area.multiply(area);
    area.multiply(pi);
    System.out.print("Area (sq.in.):       ");
    System.out.println(area.toString());

    System.out.println("\r\nDone.");

  } // end main
} // end class