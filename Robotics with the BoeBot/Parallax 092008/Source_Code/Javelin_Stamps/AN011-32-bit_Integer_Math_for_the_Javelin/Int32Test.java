import stamp.math.Int32;
/*
 * This is a simple math test for Application Note #11
 * @version 1.0 - February 10, 2003 (AppNote011)
 */


public class Int32Test {

  public static void main() {

    Int32 num1 = new Int32(12239);

    System.out.print(num1.toString());
    num1.multiply(73);
    System.out.print(" * 73 = ");
    System.out.println(num1.toString());
  }
}