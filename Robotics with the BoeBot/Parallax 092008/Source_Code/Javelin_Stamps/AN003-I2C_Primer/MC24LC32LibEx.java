package stamp.peripheral.memory.eeprom;

import stamp.core.*;
import stamp.protocol.I2C;

/**
 * This class is for demonstration purposes only.  It should be used in
 * conjunction with Javelin Stamp Application Note 3: I2C Primer - EEPROM
 * example.<p>
 *
 * This class can be instantiated for each 24LC32 on a given I2C bus, and
 * it contains methods that enable bytewise and multi-byte read and write
 * operations.
 * <p>
 *
 * @version 1.0 8 October 2002
 * @author Andy Lindsay, Parallax Inc.
 */
 public class MC24LC32LibEx {

  final private static int READ_BIT     = 0x0001;
  final private static int WRITE_BIT    = 0x0000;
  final private static int CONTROL_CODE = 0x00A0;

  private I2C i2cbus;
  private int deviceAddress, data;

  /**
   * Create MC24LC32 object by passing an I2C bus and the 24LC32's chip
   * address to this constructor.  For example:
   * <p><code>
   *  // Create an I2C bus object named i2cbus.
   *  final public static int SDAPin = CPU.pin6;
   *  final public static int SCLPin = CPU.pin7;
   *  public static I2C i2cbus = new I2C(SDAPin, SCLPin);
   *
   *  // Create a Microchip24LC32 object named eeprom0 using the i2cbus object.
   *  public static Microchip24LC32 eeprom0 = new Microchip24LC32(i2cbus, 0);
   * </code><p>
   * @param i2cbus the I2C bus object that has the new 24LC32 object/chip
   * connected to it.
   * @param chipAddress the binary address value of the new 24LC32 chip.
   * This should be the binary value of A2, A1, A0.
   */
  public MC24LC32LibEx (I2C i2cbus, int chipAddress) {
    this.i2cbus = i2cbus;
    this.deviceAddress = (CONTROL_CODE | (chipAddress << 1) & 0x00FF);
  }

  /**
   * Set the 24LC32's EEPROM address pointer.
   *
   * @param eeAddress
   */
  public void setAddress(int eeAddress){
    int controlByte = deviceAddress | WRITE_BIT;
    i2cbus.start();
    i2cbus.write(controlByte);
    i2cbus.write(eeAddress >> 8);
    i2cbus.write(eeAddress);
  }

  /**
   * Write a byte value to a particular address in the 24LC32.
   *
   * @param eeAddress the address where the byte value should be stored.
   * @param dataByte the byte value to be stored at <code>eeAddress</code>.
   */
  public void writeByte(int eeAddress, int dataByte){
    setAddress(eeAddress);
    i2cbus.write(dataByte);
    i2cbus.stop();
  }

  /**
   * Read a byte value from an address in the 24LC32.
   *
   * @param eeAddress the address that contains the byte to be read.
   * @return value the byte stored at <code>eeAddress</code>.
   */
  public int readByte(int eeAddress){
    setAddress(eeAddress);
    int controlByte = deviceAddress | READ_BIT;
    i2cbus.start();
    i2cbus.write(controlByte);
    int value = i2cbus.read(false);
    i2cbus.stop();
    return value;
  }

  /**
   * Write a string of characters starting at a particular address in
   * the 24LC32.
   *
   * @param eeAddress the address where the byte value should be stored.
   * @param sb the <code>StringBuffer</code> object that contains the string
   * of characters.
   */
  public void writeStringToEeprom(int eeAddress, StringBuffer sb){
    setAddress(eeAddress);
    int controlByte = deviceAddress | WRITE_BIT;
    for(int i = 0; i < sb.length(); i++){
      if(eeAddress %32 == 0) {
        i2cbus.stop();
        do{
          i2cbus.start();
        } while(!i2cbus.write(controlByte));
        i2cbus.write(eeAddress >> 8);
        i2cbus.write(eeAddress);
      }
      i2cbus.write(sb.charAt(i));
      eeAddress ++;
    }
    i2cbus.stop();
  }

  /**
   * Read a string of characters of a specific length starting at a
   * paricular address in the 24LC32.
   *
   * @param eeAddress the starting address at the beginning of the string.
   * @param count the number of characters to read
   * @param sb the <code>StringBuffer</code> object that stores the string
   * of characters.
   */
  public void readStringIntoBuffer(int eeAddress, int count, StringBuffer sb){
    sb.clear();
    setAddress(eeAddress);
    int controlByte = deviceAddress | READ_BIT;
    i2cbus.start();
    i2cbus.write(controlByte);
    for(int i = 0; i < count; i++){
      if(i < (count - 1)){
        sb.append((char)i2cbus.read(true));
      }
      else{
        sb.append((char)i2cbus.read(false));
      }
    }
  }
}