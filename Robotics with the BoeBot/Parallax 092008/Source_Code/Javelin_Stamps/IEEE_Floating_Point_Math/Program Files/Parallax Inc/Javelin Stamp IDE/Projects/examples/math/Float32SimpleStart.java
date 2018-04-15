/**
  * Demonstrates a the basics of creating, initializing, and
  * doing calculations with Float32 objects.  The following example
  * programs are also published and demonstrate a variety of techniques
  * and operatioins that can be done using the math package.
  *
  * @see Float32Demo.java for examples that exercise the package's
  * constructors, and methods.
  *
  * @see Float32Math.java for examples that use expontentials, trig functions
  * and other useful math operations.
  *
  * @see Float32Calculator.java for an applicaiton example.
  *
  * @author Andy Lindsay, Parallax, Inc.
  * @version 1.0 - May 1, 2003
  */

import stamp.math.*;
import stamp.core.*;

public class Float32SimpleStart {

  // Declare two Float32 objects.
  static Float32 fnum1 = new Float32();
  static Float32 fnum2 = new Float32();

  public static void main() {

    // Set value of fnum1 using a String and fnum2 using an int, then display.
    fnum1.set("625.0125");
    fnum2.set(12345);

    System.out.println(fnum1.toString());
    System.out.println(fnum2.toString());

    /* The examples below were copied from Float32Demo.java. */

    // fnum1 = fnum1 + 0.125;
    fnum1.add(".125");
    System.out.println(fnum1.toString());

    // fnum1 = fum1 - fnum2
    fnum1.subtract(fnum2);
    System.out.println(fnum1.toString());

    // nfum1 = fnum2 * fnum1
    fnum1.multiply(fnum2);
    System.out.println(fnum1.toString());

    // fnum1 = fnum1 / fnum2
    fnum1.divide(fnum2);
    System.out.println(fnum1.toString());

    // fnum1 = -fnum1;
    fnum1.negate();
    System.out.println(fnum1.toString());

  }
}
