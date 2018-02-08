package examples.protocol.i2cprimer;

/**
 * See AppNote003 I2C Primer - EEPROM Example.pdf for information
 * on this example program.
 */

import stamp.core.*;
import stamp.protocol.I2C;

public class MC24LC32FindChips {

  // Declare I2C object.
  final public static int SDAPin = CPU.pin6;
  final public static int SCLPin = CPU.pin7;
  public static I2C i2cbus = new I2C(SDAPin, SCLPin);

  // Declare constants for use with I2C class.
  final public static int READ_BIT      = 0x0001;
  final public static int WRITE_BIT     = 0x0000;
  final public static int CONTROL_CODE  = 0x00A0;
  final public static int SLAVE_ADDRESS = 0x0002;

  // Declare global variables.
  public static int controlByte;
  public static boolean reply;

  public static void main() {

    // Table heading.
    System.out.println("Chip ");
    System.out.println("Address    Reply");
    System.out.println("-------    -----");
    for(int slaveAddress = 0; slaveAddress < 8; slaveAddress ++){

      // I2C bus communication.
      controlByte = CONTROL_CODE | (slaveAddress << 1) | WRITE_BIT;
      i2cbus.start();
      reply = i2cbus.write(controlByte);

      // Display results.
      System.out.print(slaveAddress);
      System.out.print("          ");
      System.out.println( reply);
    }
  }
}