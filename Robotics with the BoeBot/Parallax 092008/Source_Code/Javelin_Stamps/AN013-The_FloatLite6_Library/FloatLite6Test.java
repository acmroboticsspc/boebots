import stamp.math.*;

/*
 * A simple test which will add 2 FloatLite6 objects.
 * For more information please refer to Application Note #013.
 * Version 1.0 - 03/20/03
 */
public class FloatLite6Test {

  public static void main() {

    // create objects from static strings
    FloatLite6 float1 = new FloatLite6("25513.0024");
    FloatLite6 float2 = new FloatLite6("123.03489");

    System.out.print(" ");
    System.out.println(float1.toString());
    System.out.print("+ ");
    System.out.println(float2.toString());
    System.out.println("-----------");

    float1.add(float2);                     // Add float2 to float1
    System.out.println(float1.toString());

  }
}