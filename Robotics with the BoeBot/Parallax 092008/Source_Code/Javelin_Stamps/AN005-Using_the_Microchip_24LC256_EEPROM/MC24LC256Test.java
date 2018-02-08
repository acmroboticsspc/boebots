// Version 1.0
// This class will test the 24LC256 circuit from AppNote005
import stamp.peripheral.memory.eeprom.*;
import stamp.core.*;

//Test the 24LC256 circuit.
public class MC24LC256Test {
  public static void main() {
    final int sdaPin = CPU.pin4;  // Javelin's I/O pin P4, used for data
    final int sclPin = CPU.pin5;  // Javelin's I/O pin P5, used for timing
    MC24LC256 ee = new MC24LC256(sdaPin, sclPin);    // Create MC24LC256 object
    boolean test = true;                             // validate
    int num;                                         // value from eeprom

    // Write one byte of value 99 to memory location 20 on chip 0
    System.out.println("Attempting to write value 99 to location 20 on chip address 0");
    test = ee.writeOne(0,20,(byte) 99);
    if (!test) System.out.println("Write Failed, verify circut");
    if (test) System.out.println("Write Successful");

    // Read from location 20 from chip 0 and test for errors.
    System.out.println("\nAttempting to read from location 20 on chip address 0");
    try {
      num = ee.readRandom(0,20);
      System.out.print("Data read from location 20: ");
      System.out.println(num);
    }// end try
    catch (MC24LC256BadReadException bre) {
      System.out.println("Bad Read Detected Here");
    }// end catch
  }// end main
}// end class