import stamp.math.*;

/*
 * A step-by-step demonstration of many of the methods available to you
 * within the FloatLite6 & FloatLite6Math libraries.
 * For more information please refer to Application Note #013.
 * Version 1.0 - 03/20/03
 */
public class FloatLite6Demo {

  public static void main() {

   /* There are 4 constructors you can use to create Float objects by passing
    * in a String, StringBuffer, Character Array or another FloatLite object.*/

    // create object from string
    FloatLite6 FL1 = new FloatLite6("13.0024");

    // create object from stringbuffer
    StringBuffer sb = new StringBuffer(25);
    sb.append("-4000.001");
    FloatLite6 FL2 = new FloatLite6(sb);

    // create object from character Array
    char[] cAry = {'1','2','3','4','5','.','6','7','8','9','0'};
    FloatLite6 FL3 = new FloatLite6(cAry);

    // create object from another FloatLite6 object
    FloatLite6 FL4 = new FloatLite6(FL1);

    System.out.println("FloatLite6 Demonstration\n");
    System.out.print("Create FL1 object with string: ");
    System.out.println(FL1.toString());
    System.out.print("Create FL2 object with stringbuffer: ");
    System.out.println(FL2.toString());
    System.out.print("Create FL3 object with character array: ");
    System.out.println(FL3.toString());
    System.out.print("Create FL4 object from a FloatLite6 object: ");
    System.out.println(FL4.toString());

    /* This method allows you to zero out a float object */
    System.out.print("\nZero out FL3 object: ");
    FL3.zero();
    System.out.println(FL3.toString());

    /* Adding to an existing float object */
    System.out.print("\nAdd the value of FL2, ");
    System.out.print(FL2.toString());
    System.out.print(", to the value of FL1: ");
    System.out.println(FL1.toString());
    FL1.add(FL2);
    System.out.print("New value of FL1: ");
    System.out.println(FL1.toString());

    /* Add an integer to an existing float object */
    System.out.print("\nAdd the integer ");
    int xInt=456;
    System.out.print(xInt);
    System.out.print(", to the value of FL2: ");
    System.out.println(FL2.toString());
    FL2.add(xInt);
    System.out.print("New value of FL2: ");
    System.out.println(FL2.toString());

    /* Add a string to an existing float object */
    System.out.print("\nAdd the value of the string '");
    String S="903.9423";
    System.out.print(S);
    System.out.print("', to the value of FL3: ");
    System.out.println(FL3.toString());
    FL3.add(S);
    System.out.print("New value of FL3: ");
    System.out.println(FL3.toString());

    /* Subtract from an existing float object */
    System.out.print("\nSubtract the value of FL4, ");
    System.out.print(FL4.toString());
    System.out.print(", from the value of FL1: ");
    System.out.println(FL1.toString());
    FL1.subtract(FL4);
    System.out.print("New value of FL4: ");
    System.out.println(FL1.toString());

    /* Subtract an integer from an existing float object */
    System.out.print("\nSubtract the integer ");
    xInt=25897;
    System.out.print(xInt);
    System.out.print(", from the value of FL2: ");
    System.out.println(FL2.toString());
    FL2.subtract(xInt);
    System.out.print("New value of FL2: ");
    System.out.println(FL2.toString());

    /* Subtract a string from an existing float object */
    System.out.print("\nSubtract the value of the string '");
    S="7854.478963";
    System.out.print(S);
    System.out.print("', from the value of FL3: ");
    System.out.println(FL3.toString());
    FL3.subtract(S);
    System.out.print("New value of FL3: ");
    System.out.println(FL3.toString());

   /* There are 5 set methods you can use to set the values of the float objects
    * by passing in a String, StringBuffer, Character Array, Int or another
    * FloatLite object */
    FL1.setValue(xInt);
    System.out.print("\nSet FL1 with the value of an integer: ");
    System.out.println(FL1.toString());

    FL1.setValue("3492.93");
    FL2.setValue(sb);
    FL3.setValue(cAry);
    FL4.setValue(FL1);

    System.out.print("\nSet FL1 with the value of the string: ");
    System.out.println(FL1.toString());
    System.out.print("Set FL2 with the value of the stringbuffer: ");
    System.out.println(FL2.toString());
    System.out.print("Set FL3 with the value of the character array: ");
    System.out.println(FL3.toString());
    System.out.print("Set FL4 with value from FL1: ");
    System.out.println(FL4.toString());

    /* tests for equals, compare and absCompare */
    // Boolean test equality between two float objects (FL1 & FL2)
    System.out.print("\nFL1 ");
    if (FL1.equals(FL2))
      System.out.print("equals FL2 ");
    else
      System.out.print("does not equal FL2\n");

    // Boolean test equality between two float objects (FL1 & FL4)
    System.out.print("FL1 ");
    if (FL1.equals(FL4))
      System.out.println("equals FL4");
    else
      System.out.println("does not equal FL4");

    // Compare values between two float objects (FL1 & FL2)
    System.out.print("\nFL1 is ");
    switch(FL1.compare(FL2))
      {
      case -1:
        System.out.print("< ");
        break;
      case  0:
        System.out.print("= ");
        break;
      case 1:
        System.out.print("> ");
        break;
      }
    System.out.println("FL2");

    // Compare values between two float objects (FL1 & FL3)
    System.out.print("FL1 is ");
    switch(FL1.compare(FL3))
      {
      case -1:
        System.out.print("< ");
        break;
      case  0:
        System.out.print("= ");
        break;
      case 1:
        System.out.print("> ");
        break;
      }
    System.out.println("FL3");

    // Compare values between two float objects (FL1 & FL4)
    System.out.print("FL1 is ");
    switch(FL1.compare(FL4))
      {
      case -1:
        System.out.print("< ");
        break;
      case  0:
        System.out.print("= ");
        break;
      case 1:
        System.out.print("> ");
        break;
      }
    System.out.println("FL4\n ");

    // Compare the absolute values between two float objects (FL1 & FL2)
    System.out.print("abs(FL1) is ");
    switch(FL1.absCompare(FL2))
      {
      case -1:
        System.out.print("< ");
        break;
      case  0:
        System.out.print("= ");
        break;
      case 1:
        System.out.print("> ");
        break;
      }
    System.out.println("abs(FL2) ");

    /* Get sign of floatLite6 object */
    System.out.print("The sign of FL1 is ");
    if (FL1.getSign()) System.out.println("Positive");
    else System.out.println("Negative");

    System.out.print("The sign of FL2 is ");
    if (FL2.getSign()) System.out.println("Positive");
    else System.out.println("Negative");

    /* Test minimum, maximum, average and summation of float array */
    // Create an array of float objects
    FloatLite6 [] fAry = new FloatLite6[5];
    fAry[0] = new FloatLite6("1.23");
    fAry[1] = new FloatLite6("3.256");
    fAry[2] = new FloatLite6("2.001403");
    fAry[3] = new FloatLite6("0.45");
    fAry[4] = new FloatLite6("5.1");

    System.out.println("\nValues of the Array 'fAry':");
    for (int x=0;x<5;x++){
      System.out.print("  fAry[");
      System.out.print(x);
      System.out.print("] = ");
      System.out.println(fAry[x].toString());
    }

    // demonstrate setValue
    FL1.setValue(fAry[0]);
    System.out.print("FL1 set to vals[0], FL1 now equals: ");
    System.out.println(FL1.toString());

    // Add entire array into FL1
    for (int x=0;x<5;x++)
      FL1.add(fAry[x]);
    System.out.print("Sum of FL1 and entire fAry = ");
    System.out.println(FL1.toString());

    /* The following Math methods are from the FloatLite6Math library */
    FloatLite6Math FL6Math = new FloatLite6Math();
    System.out.println("\nFloatLite6Math Methods:");

    // Find the maximum value from the array of floats
    System.out.print("Maximum Value from fAry = ");
    FL2.setValue(FL6Math.max(fAry));
    System.out.println(FL2.toString());

    // Find the minimum value from the array of floats
    System.out.print("Minimum Value from fAry = ");
    FL3.setValue(FL6Math.min(fAry));
    System.out.println(FL3.toString());

    // Find the average value from the array of floats
    System.out.print("Average Value from fAry = ");
    FL4.setValue(FL6Math.average(fAry));
    System.out.println(FL4.toString());

    /* Demonstration math functions that use no heap */
    System.out.println("\nMath Functions that do not use the heap");
    //  demo max
    System.out.print("max (FL1)= ");
    FL6Math.max(fAry,FL1);
    System.out.println(FL1.toString());
    // demo min
    System.out.print("min (FL1)= ");
    FL6Math.min(fAry,FL1);
    System.out.println(FL1.toString());
    // demo average
    System.out.print("avg= ");
    FL6Math.average(fAry,FL1);
    System.out.println(FL1.toString());

    System.out.println("\nDemo Complete");
    }
  }