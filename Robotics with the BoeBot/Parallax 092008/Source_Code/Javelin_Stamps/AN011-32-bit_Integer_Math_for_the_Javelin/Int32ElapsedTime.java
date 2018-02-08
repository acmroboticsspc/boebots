import stamp.core.*;
import stamp.math.Int32;

/*
 * The following example demonstrates the use of Int32 objects.
 * The example takes a 32-bit timer reading before an activity and another
 * 32-bit timer reading after an activity. It displays these start and stop
 * times in ticks, then calculates and displays the elapsed time in ticks and
 * in microseconds.  The Timer methods return the number of 8.68 microsecond
 * ticks since the timer was started.
 *
 * To convert ticks to microseconds we will use the formula:
 * elapsedTime = ((elapsedTime * 868) + 50) / 100
 *
 * @version 1.0 - February 10, 2003 (AppNote011)
 */

public class Int32ElapsedTime {

  public static void main() {

    Timer  t = new Timer();
    int    nHigh;
    int    nLow;

    // Create 2 new Int32 object with initial value of zero.
    Int32 num1 = new Int32();
    Int32 num2 = new Int32();
    Int32 num3 = new Int32(0x0001, (short)0xE078);   // set num2 = 123,000

    System.out.println("\r\nElapsed Time");

    // get the start time and store in num1
    nLow  = t.tickLo();
    nHigh = t.tickHi();
    num1.set(nHigh, nLow);

    // the activity to be timed goes below here
    num3.multiply(5000);
    // the activity to be timed goes above here

    // get the stop time and store in num2
    nLow = t.tickLo();
    nHigh = t.tickHi();
    num2.set(nHigh, nLow);

    System.out.print("123,000 * 5,000 = ");
    System.out.println(num3.toString());

    // display the start and stop time in ticks
    System.out.print("Start time   (ticks)  = ");
    System.out.println(num1.toString());
    System.out.print("Stop time    (ticks)  = ");
    System.out.println(num2.toString());

    // elapsed time = stop time - start time
    // num2 = num2 - num1
    num2.subtract(num1);
    System.out.print("Elapsed Time (ticks)  = ");
    System.out.println(num2.toString());

    // convert to microseconds
    // num2 = ((num2 * 868) + 50) / 100;
    num2.multiply(868);
    num2.add(50);
    num2.udivide(100);

    // display elapsed time
    System.out.print("Elapsed time (usecs)  = ");
    System.out.println(num2.toString());

    System.out.println("\nDone.");

  } // end main

} // end class