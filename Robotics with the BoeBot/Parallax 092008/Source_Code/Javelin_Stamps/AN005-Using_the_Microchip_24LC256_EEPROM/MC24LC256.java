package stamp.peripheral.memory.eeprom;
import stamp.core.*;
import stamp.protocol.*;                    // Location of I2C protocol

/**
 * This class will record a value onto an external Microchip 24LC256 eeprom.
 * You may use up to 8 eeproms, each one will have it's own unique address.
 * For technical specifications on wiring the chip for a unique address
 * refer to Microchip's website: www.microchip.com.<p>
 *
 * Constructor requires data pin and clock pin as parameters.
 * Device numbers in read and write methods refer to the unique address
 * of the target 24LC256 chip (numbered 0-7).
 *
 *
 * The class is based on the Parallax I2C class (v1.1) for bus communication.<p>
 *
 * <pre>
 * Revision History:
 * 10/21/02 Ver 1.1 - Final class submission to Parallax Inc. from John Cole.
 *
 * @author John Cole
 * @version 1.1
 */
public class MC24LC256
  {
  /**  I2C class to handle communication
   */
  static I2C i2c;
  /**
   * Constructor requiring CPU.pins for data and clock pins
   * @param sdaPin data pin from stamp
   * @param sclPin clock pin from stamp
   */
  public MC24LC256(int sdaPin, int sclPin)
    {
    i2c = new I2C(sdaPin,sclPin);
    }

  /** Method to write an array of bytes to the external eeprom using
   *  the page write functions.
   *  This method will catch any 'roll over' to the start of the
   *  next page if you go beyond a page boundary (64 bytes).
   *
   * @param dev device number for external eeprom
   * @param add address on the device
   * @param vals byte array of values to be written
   * @return -1 if error occurs 1 otherwise
   */
  public static boolean writePage(int dev, int add, byte[] vals)
    {
    int outValue = 0,tmpAdd;
    boolean retVal=false;
    int i;

    i2c.start();                                     // send the start signal

    /* The first 3 bytes that are transferred to the 24LC256 chip
       provide the chip (device) number, and the high byte of the
       address integer value, and the low byte of the address integer
       value
    */
    for (i = 0; i < vals.length + 3; i++)
      {
      switch(i)
        {
        case 0:
          outValue = 0xA0 | (dev << 1); // 1010DEV0
          break;
        case 1:
          outValue = add >>> 8;
          break;
        case 2:
          outValue = add; // no need to shift outValue is shifted
          break;
        default:
          outValue = 0x00 | vals[i-3];
          break;
        }// end switch

      // write the byte and test the return value
      retVal = i2c.write((int) outValue);
      if (!retVal)
        break;

      // Test to see if the next byte will cross over a page boundary.
      // If it does then stop the i2c bus and restart it on the other side of
      // the page.
      // Page boundaries are at 64 byte intervals.
      // Use (i - 2 + add) to get the address location value for the current byte.
      // Since the first 3 times i is incremented it relates to control byte
      // addressing and you are testing the NEXT byte.
      // Skip the first 3 times thru the loop.
      if (i > 2)
        if ((i - 2 + add) % 64 == 0)         // next write is on a page boundary
          {
          i2c.stop();
          CPU.delay(100);
          i2c.start();
          // write the device
          outValue = 0xA0 | (dev << 1); // 1010DEV0
          retVal = i2c.write((int) outValue);
          // always test the return value
          if (!retVal)
            break;
          // write the next address step 1
          tmpAdd = i - 2 + add;
          outValue = tmpAdd >>> 8;
          retVal = i2c.write((int) outValue);
          // always test the return value
          if (!retVal)
            break;
          // write the next address step 2
          outValue = tmpAdd; // no need to shift outValue is shifted
          retVal = i2c.write((int) outValue);
          // always test the return value
          if (!retVal)
            break;
          }// end if
      }// end for

    i2c.stop();                                      // send the stop
    return retVal;
    }// end method: writePage

  /** Method to write a single byte to the external eeprom.
   *
   * @param dev device number for external eeprom
   * @param add address on the device
   * @param val byte to be written
   */
  public static boolean writeOne(int dev, int add, byte vals)
    {
    int outValue = 0;
    boolean retVal=false;
    int i;
    // send the start signal
    i2c.start();

    for (i = 0; i < 4; i++)
      {
      switch(i)
        {
        case 0:
          outValue = 0xA0 | (dev << 1); // 1010DEV0
          break;
        case 1:
          outValue = add >>> 8;
          break;
        case 2:
          outValue = add; // no need to shift outValue is shifted
          break;
        default:
          outValue = 0x00 | vals;
          break;
        }// end switch
      retVal = i2c.write((int) outValue);
      if (!retVal)
        break;
      }// end for

    i2c.stop();                                      // send the stop
    return retVal;
    }// end method: writeOne

  /** Method to write a single byte to the external eeprom
   *  and verify that it was written correctly
   *
   * @param dev device number for external eeprom
   * @param add address on the device
   * @param val byte to be written
   *
   * @return boolean value verifying correct value written
   */
  public static boolean writeVerify(int dev, int add, byte vals)
    {
    boolean good;
    byte test = (byte) (vals -  5);         // set test to different value
    good = writeOne(dev,add,vals);
    if (!good)
      return good;
    CPU.delay(150);                         // pause to allow write to complete
    try {
      test = (byte) readRandom(dev,add);
      } catch (MC24LC256BadReadException bre)
          {
          System.out.println("BadReadException");
          return false;
          }
    return (test == vals);
    }// end method: writeVerify

  /** Method to read a single byte from the external eeprom at
   *  the current address
   *
   * @param dev device number for external eeprom
   * @return read value
   */
  public static int readNext(int dev)
    {
    int outValue = 0;
    int retVal;
    boolean good;
    int i;
    // send the start signal
    i2c.start();

    outValue = 0xA0 | (dev << 1) | 0x01;    // 1010DEV1
    good = i2c.write((int) outValue);
    retVal = i2c.read(1);

    // send the stop without an ack
    i2c.stop();

    return retVal;
    } // end method: readNext

  /** Method to read a single byte from the external eeprom at
   * a random address
   *
   * @param dev device number for external eeprom
   * @param add address on the device
   */
  public static int readRandom(int dev, int add) throws MC24LC256BadReadException
    {
    // For random read must send control (with write bit),
    // then address 2 bytes,
    // then start string again,
    // then control with (read),
    // receive the byte,
    // no ack at the end,
    // then stop.
    int outValue = 0;
    int retVal;
    boolean good;
    int i;
    // first time thru
    // send the start signal
    i2c.start();

    for (i = 0; i < 3; i++)
      {
      switch(i)
        {
        case 0:
          outValue = 0xA0 | (dev << 1);     // 1010DEV0
          break;
        case 1:
          outValue = add >>> 8;
          break;
        case 2:
          outValue = 0x00 |add;             // no need to shift outValue is shifted
          break;
        } // end switch
      good = i2c.write(outValue);
      if (!good)
        throw new MC24LC256BadReadException();
      } // end for

    // send the start signal for the 2nd time
    i2c.start();
    outValue = 0xA0 | (dev << 1) | 0x01;    // 1010DEV1
    good = i2c.write(outValue);
    if (!good)
      throw new MC24LC256BadReadException();

    retVal = i2c.read(1);                   // read the record

    i2c.stop();                             // send the stop without an ack

    return retVal;
    } // end method: readRandom
  } // end class: MC24LC256