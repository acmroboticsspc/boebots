import stamp.math.Int32;

/*
 * This class will demonstrate all the methods within the Int32 library class.
 *
 * @version 1.0 - February 10, 2003 (AppNote011)
 */

public class Int32Demo {

  final static char HOME = 0x01;                 // Position cursor upper-left
  final static char CLS = '\u0010';              // Clear Screen

  //-------------------- Constructors ---------------------------------------
  // Create a new Int32 object with initial value of zero.
  static Int32 num1 = new Int32();

  // Create a new Int32 object with the initial value set to the
  // 32-bit value of the Int32 argument.
  static Int32 num2 = new Int32(num1);

  // Create a new Int32 object with the initial value set to the
  // 16-bit value of the argument.
  // Note: the value of the argument will be sign extended.
  static Int32 num3 = new Int32(-10000);

  // Create a new Int32 object with the initial value set to 4,000,000.
  // Defined by two hexadecimal numbers (4000000 = hex:3D0900).
  // See the set method below for further discussion on constants.
  static Int32 num4 = new Int32(0x003D, 0x0900);

  public static void main() {

    //-------------------- Using high, low fields -----------------------------
    // An Int32 object contains two fields that are public:
    //    high - contains the high 16-bits of a 32-bit value
    //    low - contains the low 16-bits of a 32-bit value
    // These can be used whenever direct access to the high or low 16-bits
    // of the 32-bit integer is useful.
    int    nHigh   = (short)(50000000 >> 16);    // 02FA
    int    nLow    = (short)(50000000 & -1);     // F080

    int    nint    = 0;                          // General integer variable
    short  nshort  = 20000;                      // General short variable
    byte   nbyte   = -100;                       // General byte variable
    char   nchar   = 200;                        // General character variable

    System.out.println(CLS);
    System.out.print("This program demonstrates the methods availalbe to you ");
    System.out.println("in the Int32 library.");
    display("Display values of the Int32 objects");

    //-------------------- Set methods ----------------------------------------
    // Set the Int32 value to the value of the 16-bit argument.
    // Note: the value of the argument will be sign extended.
    // num1 = 24000
    num1.set(24000);

    // Set the value of an integer to the low 16-bits.
    nint = num4.low;
    System.out.print("The 'low' 16-bits of num4 is: ");
    System.out.println(nint);
    nint = num4.high;
    System.out.print("The 'high' 16-bits of num4 is: ");
    System.out.println(nint);

    // Set the Int32 value to the value of the Int32 argument.
    // num2 = num1
    num2.set(num1);
    display("\nnum1 and num2 are now equal to 24,000");

    // Set the Int32 value to the 32-bit value represented by the value
    // of two 16-bit arguments.
    // num2 = (nHigh << 16) + nLow
    num2.set(nHigh, nLow);
    display("The value of num2 is now 50,000,000");

    // Note: the compiler supports manipulating large constants as long as
    // the result is compatible with the variable type.  Integer variables
    // are signed, so a compiler error is generated if a positive constant
    // greater than 32767 is created (e.g. 0x8000 or above), in this case
    // you need to cast the constant to short as shown below.

    // Set the Int32 value to 10,000,000 (hex: 989680).
    // num1 = 10000000
    num1.set(0x0098, (short)0x9680 );

    // Set the Int32 value to 10,000,000 (using compiler arithmetic).
    // num2 = 10000000
    num2.set((short)(10000000 >> 16), (short)(10000000 & -1));

    // You can set the Int32 value to the numeric value of a string or
    // StringBuffer argument.  Leading whitespace is ignored and conversion
    // ends at the first character that is not a decimal digit.
    // Note: This is not as efficient as using numeric constants since the
    // string must be converted at runtime.
    // num3 = 12345678
    num3.set("12345678");
    // num4 = -123456
    StringBuffer sbuf = new StringBuffer(" -123456ABC");
    num4.set(sbuf);
    display("Current values have changed, current values are:");

    //-------------------- Arithmetic methods ---------------------------------
    // The Int32 arithmetic methods all support three types of method calls.
    // For example, the add method supports the following:
    //    void add(Int32 num)
    //    void add(int low)
    //    void add(int high, int low)
    // When these three types of method calls are combined with various data
    // types and constants a wide range of operations can be supported.

    // To add an 8-bit signed value, use the byte data type.
    // num1 = num1 + nbyte
    num1.add(nbyte);

    // To add an 8-bit unsigned value, use the char data type.
    // num2 = num2 + nchar
    num2.add(nchar);

    // To add a 16-bit signed value, use int or short data types.
    // num3 = num3 + nint
    num3.add(nint);

    // num4 = num4 + nshort
    num4.add(nshort);
    System.out.print("Add nbyte(-100) to num1, nchar(200) to num2,");
    display("\nnint(2304) to num3, and nshort(20000) to num4");

    // To add another Int32 value.
    // num1 = num1 + num2;
    num1.add(num2);

    // To add a 16-bit value (sign extended) to the 32-bit integer
    // num2 = num2 + 31437
    num2.add(31137);

    // To add a 16-bit unsigned value, use int or short data types and
    // set the high byte to zero.
    // num1 = num1 + nint
    num3.add(0, -10000);
    // num1 = num1 + nshort
    num4.add(0, 10000);
    System.out.println("Adding a 'signed' value of 10000, actually adds 55536.");
    display("To represent -10000 the compiler subtracts it from 65536.");

    // To add a 32-bit signed or unsigned value stored as two 16-bit values.
    // nHigh = 02FA, nLow = F080, num1 = num1 + (nHigh << 16) + nLow;
    num1.add(nHigh, nLow);

    // num1 = num1 + (-100)
    num2.add(-100);

    // 10000000(dec) = 00989680(hex)
    // num1 = num1 + 10000000
    num3.add(0x0098, (short)0x9680);

    // num1 = num1 + 10000000
    num4.add((short)(10000000 >> 16), (short)(10000000 & -1));
    display("Use a breakpoint and observe num1, num3 & num4, before and after.");

    // The following examples may not show but do support the same combination
    // of methods and data types shown above for the add method.

    // num1 = num1 - num2
    num1.subtract(num2);

    // Subtract a 16-bit value (sign extended) from the 32-bit integer.
    // num2 = num2 - 31327
    num2.subtract(31237);

    // Subtract a 32-bit value from the 32-bit integer.
    // num3 = num3 - 10000000
    num3.subtract(0x0098, (short)0x9680);
    display("Subtract num2 from num1, 31237 from num2, and 10,000,000 from num3");

    // Divide the 32-bit integer by another 32-bit integer
    // num1 = num1 / num2
    num1.divide(num4);

    // Divide the 32-bit integer by a 16-bit value
    // num2 = num2 /10000
    num2.divide(10000);

    // Divide the 32-bit integer by a 32-bit value
    // num3(00BD3A7B) = num3(00BD3A7B) / 0001F2A7
    num3.divide(0x0001, (short)0xF2A7);
    display("Divide num1 by num4, num2 by 10,000 and 0x0001F2A7 from num3");

    num1.set(0x0098, (short)0x9680);
    num2.set((short)0xF0F0, (short)0x0606);
    num3.set(0x1111, (short)0x1111);
    num4.set(425);
    display("Set new values");

    // Divide 32-bit unsigned integer by another 32-bit unsigned intager
    // num1 = num1 / num2 (unsigned)
    num1.udivide(num4);

    // Divide the 32-bit unsigned integer by a 16-bit unsigned value
    // num2 = num2 / 40000
    num2.udivide((short)0x9C40);

    // Divide the 32-bit unsigned integer by a 32-bit unsigned value
    //num3 = num3 /
    num3.udivide(0x0001,(short)0xF004);
    display("Unsigned divide: num1 by bum4, num2 by 0x9C40 and num3 by 0x1F004");

    //-------------------- Divide Operations ----------------------------------
    // The remainder of the last divide is available through the remainder method.

    num1.divide(89);
    System.out.print("Divide num1 by 89, the answer is ");
    System.out.print(num1.toString());
    System.out.print(" with a remainder of ");
    // num2 = remainder of last divide operation on num1
    num1.remainder(num2);
    System.out.println(num2.toString());
    display("\nCurrent Values:");

    // Multiply the 32-bit integer by another 32-bit integer
    // num1 = num1 * num3
    num1.multiply(num3);

    // Multiply the 32-bit integer by a 16-bit value
    // num2 = num2 * 23002
    num2.multiply(32002);

    // Multiply the 32-bit integer by a 32-bit value
    // num4 = num4 * 418696
    num4.multiply(0x6,0x6388);
    display("Multiply num1 by num3, num2 by 32002(16-bit), and num4 by 418696(32-bit)");

    // num1 = num1 >>> 3
    num1.shiftRight(3);

    // num1 = num1 << count (0 enters LSB)
    num3.shiftLeft(4);
    display("Shift the bits of num1 to the right by 3, and num3 to the left by 4");

    //-------------------- Comparisons ----------------------------------------
    // The compare method supports the same combination of methods and
    // data types shown above for the arithmetic operations.

    // Set the all values
    num1.set(1000);
    num2.set(2000);
    num3.set(1000);
    num4.set(-2000);
    display("Set all new values");
    System.out.println("Compare Operations");

    // Signed compare between two 32-bit integers
    // (note: ucompare can be used for unsigned comparisons)

    // Signed compare of num1 and num2
    if (num1.compare(num2) > 0) System.out.println("num1 > num2");
    if (num1.compare(num2) == 0) System.out.println("num1 == num2");
    if (num1.compare(num2) < 0) System.out.println("num1 < num2");

    // Signed compare of num1 and num3
    if (num1.compare(num3) > 0) System.out.println("num1 > num3");
    if (num1.compare(num3) == 0) System.out.println("num1 == num3");
    if (num1.compare(num3) < 0) System.out.println("num1 < num3");

    // Signed compare of num1 and num4
    if (num1.compare(num4) > 0) System.out.println("num1 > num4");
    if (num1.compare(num4) == 0) System.out.println("num1 == num4");
    if (num1.compare(num4) < 0) System.out.println("num1 < num4");

    // Signed compare of the 32-bit integer by a 16-bit integer
    // Check if num1 >= 1000
    if (num1.compare(1000) >= 0) System.out.println("num1 >= 1000");

    // signed compare of the 32-bit integer and a 32-bit value
    // check if num1 == num2
    if (num1.compare(0x6,0x6388) >= 0) System.out.println("num1 >= 66388");

    // Set num1 to an unsigned value
    num3.set((short)42000);
    display("\n\nSet num3 to an unsigned value (42000).");
    System.out.println("Unsigned Compare Operations");

    // Unsigned compare between two 32-bit integers
    if (num3.ucompare(num2) > 0) System.out.println("num3 > num2");
    if (num3.ucompare(num2) == 0) System.out.println("num3 == num2");
    if (num3.ucompare(num2) < 0) System.out.println("num3 < num2");

    // Unsigned compare of the 32-bit integer by a 16-bit integer
    // Check if num3 >= 1000
    if (num3.ucompare(20000) >= 0) System.out.println("num3 (42000) >= 20000");

    // Unsigned compare of the 32-bit integer and a 32-bit value
    // check if num3 >= 66388
    if (num3.ucompare(0x6,0x6388) >= 0) System.out.println("num3 (0xFFFFA410) >= 66388");

    // Tests equality between 2 32-bit integers.
    if (num2.equals(num4)) System.out.println("num1 == num2");
    else System.out.println("num2 != num4\n\n");

   //-------------------- Unary Operations -----------------------------------
    // Sets the 32-bit integer to its absolute value
    // num1 = absolute value of num1
    num4.abs();
    display("Set num4 to absoulte value");

    // Negates the 32-bit integer
    // num1 = -num1;
    num4.negate();
    display("Negate num4");

    //-------------------- Convert to String ----------------------------------
    // The value of an Int32 object is converted to a string using the
    // toString method. (note: utoString can be used for unsigned conversions.)

    // Convert the signed 32-bit integer to a String
    System.out.println("Convert the signed num3 integer into a String");
    System.out.println(num3.toString());

    // Convert the unsigned 32-bit integer to a String
    System.out.println("Convert the unsigned num3 integer into a String");
    System.out.println(num3.utoString());

    System.out.println("\nDemo complete.");

  } // end main

  public static void display(String s){
    System.out.println(s);
    System.out.print("num1 = ");
    System.out.println(num1.toString());
    System.out.print("num2 = ");
    System.out.println(num2.toString());
    System.out.print("num3 = ");
    System.out.println(num3.toString());
    System.out.print("num4 = ");
    System.out.println(num4.toString());
    System.out.println("\n");
  }

} // end class
