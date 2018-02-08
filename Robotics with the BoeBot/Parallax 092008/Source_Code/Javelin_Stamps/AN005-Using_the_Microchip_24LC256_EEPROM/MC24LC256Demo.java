// Version 1.0
// This class is a full demonstration of the MC24LC256 library (AppNote005)
import stamp.core.*;
import java.util.*;                                  // needed for random call
import stamp.peripheral.memory.eeprom.*;

/*
 * This is an exercise class for the MC24LC256 class which supports
 * the 24LC256 EEPROM chip -- as written this class only exercises
 * the first chip - to test others change the device number to match
 * the appropriate chip.
 */
public class MC24LC256Demo
  {
  public static void main()
    {
    final int sdaPin = CPU.pin4;  // Javelin's I/O pin P4, used for data
    final int sclPin = CPU.pin5;  // Javelin's I/O pin P5, used for timing
    int dev = 0;                  // Device ID
    MC24LC256 ee = new MC24LC256(sdaPin, sclPin);    // Create MC24LC256 object
    byte[] var = new byte[10];                       // Data to be written
    boolean test;                                    // validate
    int val,n;                                       // Misc

    // Seeded random number always produces same value.
    // To test other values change the seed (14) to another value.
    Random r1 = new Random(14);                      // Create random number object
    val = r1.next();                                 // Get a Random Number
    if (val > 9) val = val%10;                       // Force it between 1 & 10
    if (val == 0) val = 10;

    // Fill test array 'var'
    for (n = 0; n < 10; n++)
      {
      var[n] = (byte) (val * n);
      }

    System.out.println("This program tests a MC24LC256 Object.\n");
    System.out.println("It takes a seeded random number and");
    System.out.println("creates an array of int values using");
    System.out.println("array item number * generated value.");
    System.out.println("It then writes the array to the eeprom");
    System.out.println("using the various write methods");
    System.out.println("provided by the class, and\n");
    System.out.println("then reads them back and writes them");
    System.out.println("to the message box.\n\n");

    // Test for method: writePage
    // Test of the array write -- starting at 1st memory location
    test = ee.writePage(dev,0,var);
    CPU.delay(100);
    if (test)
      System.out.println("Test 1: writePage [OK]");
    else
      System.out.println("Test 1: writePage [BAD]");

    // Test a write across a page boundary.
    // This write begins at the location of the 61st byte
    // the page boundary occurs at the 64th byte -- so this
    // tests a page write across the boundary
    test = ee.writePage(dev,60,var);
    CPU.delay(100);
    if (test)
      System.out.println("Test 2: writePage [OK]");
    else
      System.out.println("Test 2: writePage [BAD]");

    // Test for method: writeOne
    // Now write directly to memory cells
    // starting at location 10 since the first test above ran from 0-9
    test=true;
    for (n = 10; n < 20; n++)
      {
      test = ee.writeOne(dev,n,var[n-10]);
      if (!test)
        {
        System.out.print("Test 3: writeOne failed at ");
        System.out.println(n);
        test=false;
        }
      CPU.delay(50);
      }
    System.out.println("Test 3: writeOne [OK]");

    // Test for method: writeVerify
    // write verify test at byte location 20
    var[0] = (byte) -20;
    test = ee.writeVerify(dev,20,var[0]);
    if (test)
      System.out.println("Test 4: writeVerify [OK]");
    else
      System.out.println("Test 4: writeVerify [BAD]");

    CPU.delay(20000);
    System.out.println("\nNow Read the data Written\n");

    System.out.println("Data are an array based on val * n");
    System.out.println("where n is the array index");
    System.out.print("and val is random seeded value: ");
    System.out.println(val);

    System.out.println("\nArray values from readRandom");
    System.out.println("and readNext methods");
    System.out.println("\nResults -- two identical sets of");
    System.out.println("10 numbers the first written using");
    System.out.println("writePage, the second set written");
    System.out.println("using writeOne method.\n");

   // Test for method: readRandom
   // This try/catch detects errors when setting/reading address of eeprom
    try {
      n = ee.readRandom(dev,0);
    } catch (MC24LC256BadReadException bre)
        {
        System.out.println("Bad Read Detected Here");
        }
    System.out.println(n);

    // Test for method: readNext
    // Now read the next 20 locations from current eeprom address
    for (n = 1; n < 20; n++)
      System.out.println(ee.readNext(dev));

    // Previously wrote a -20 into location 20
    // Byte was shifted into the int so if negative values are
    // possible need to check for them.
    System.out.println("\nNext read is location written using");
    System.out.println("writeVerify value should be -20\n");

   // This try/catch detects errors when setting/reading address of eeprom
    try {
      n = ee.readRandom(dev,20);
      } catch (MC24LC256BadReadException bre)
        {
        System.out.println("Bad Read Detected");
        test = false;
        }
    if (test)
      {
      if (n > 128)
        n -= 256;
      System.out.println(n);
      }

    // Print out the page write across.
    System.out.println("\nPage Read of write across boundary\n");
    System.out.println("Same array values from a different");
    System.out.println("memory location\n");

   // This try/catch detects errors when setting/reading address of eeprom
   try {
      n = ee.readRandom(dev,60);
    } catch (MC24LC256BadReadException bre)
         {
         System.out.println("Bad Read Detected Here");
         }
    System.out.println(n);
    for (n = 1; n < 10; n++)
      System.out.println(ee.readNext(dev));

    } // end main
  } // end class