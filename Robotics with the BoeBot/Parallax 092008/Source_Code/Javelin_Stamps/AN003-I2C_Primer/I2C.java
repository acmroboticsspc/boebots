package stamp.protocol;

import stamp.core.*;

/**
 * This class defines a physical bus and provides essential I2C protocol
 * routines.  The bus pins should be pulled up to Vdd through 4.7K resistors.
 * <p>
 * This class can be used as-is, but will usually be referenced by a specific
 * device class that uses the I2C bus.  Direct use of this class requires the
 * programmer to have some understanding of the I2C bus and protocol.  Examples
 * on how to use this class with I2C and I2C compatible devices are published
 * in AN003 - I2C Primer available from the www.javelinstamp.com Applications
 * page.
 *
 * For more information, consult Philips documentation for details:
 * <p>
 * http://www.semiconductors.philips.com/i2c/
 * <p>
 * Creating the bus requires two available pins: SDA (serial data) and
 * SCL (serial clock).
 * <p>
 * <code>
 *   I2C ioBus = new I2C(CPU.pin0, CPU.pin1);  // SDA on pin 0; SCL on pin 1
 * </code>
 *
 * <p><pre>
 * Revision History:
 * 07/15/02: v1.1: Initial release.
 * 10/14/02: v1.2: Read method overloaded and class moved to the protocol
 * folder for release.
 * </pre><p>
 * @author Jon Williams (jwilliams@parallaxinc.com), Andy Lindsay
 * (alindsay@parallaxinc.com).
 * @version 1.2 October 30, 2002
 */

public class I2C {

  /**
   * Acknowledgement condition can be detected by the write method or
   * transmitted by the read method.
   */
  public static final int ACK = 0x00;

  /**
   * No acknowledgement condition can be detected by the write method or
   * transmitted by the read method.
   */
  public static final int NAK = 0x01;

  protected int sdaPin, sclPin;           // pins for I2C bus


  /**
   * Creates I2C bus object and intializes bus pins.
   *
   * @param sdaPin I2C serial data pin (bi-directional).
   * @param sclPin I2C serial clock pin.
   */
  public I2C(int sdaPin, int sclPin) {
    this.sdaPin = sdaPin;
    this.sclPin = sclPin;
    CPU.setInput(sdaPin);                 // release bus pins to pull-ups
    CPU.setInput(sclPin);
  }


  /**
   * Send I2C Start sequence.
   */
  public void start() {
    CPU.setInput(sdaPin);                 // allow bus pins to be pulled high
    CPU.setInput(sclPin);
    CPU.writePin(sdaPin, false);          // take SDA low first
    while (!CPU.readPin(sclPin)) {}       // wait for clock hold to release
    CPU.writePin(sclPin, false);          // setup for high-going clock pulse
  }


  /**
   * Write 8-bit value to I2C device.
   *
   * @param i2cData Value to write to I2C device.
   * @return True if device returned Ack; False if Nak.
   */
  public boolean write(int i2cData) {
    int i2cAck;

    CPU.shiftOut(sdaPin, sclPin, 8, CPU.SHIFT_MSB, (i2cData << 8));
    // get Ack bit
    i2cAck = CPU.shiftIn(sdaPin, sclPin, 1, CPU.PRE_CLOCK_MSB);

    return (i2cAck == ACK);
  }

  /**
   * Read 8-bit value from I2C device.
   *
   * @param ackBit <code>true</code> to send Ack; <code>false</code> to
   * send Nak after receipt of data.
   * @return Value from I2C device.
   */
  public int read(boolean ackBit) {
    int i2cData;

    i2cData = CPU.shiftIn(sdaPin, sclPin, 8, CPU.PRE_CLOCK_MSB);
    // send Ack (more reads) or Nak (last read)
    if(ackBit){
      CPU.shiftOut(sdaPin, sclPin, 1, CPU.SHIFT_LSB, ACK);
    }
    else{
      CPU.shiftOut(sdaPin, sclPin, 1, CPU.SHIFT_LSB, NAK);
    }
    return i2cData;
  }

  /**
   * Read 8-bit value from I2C device.
   *
   * @param ackBit 0 to send Ack; 1 to send Nak after receipt of data.
   * @return Value from I2C device.
   */
  public int read(int ackBit) {
    int i2cData;

    i2cData = CPU.shiftIn(sdaPin, sclPin, 8, CPU.PRE_CLOCK_MSB);
    // send Ack (more reads) or Nak (last read)
    CPU.shiftOut(sdaPin, sclPin, 1, CPU.SHIFT_LSB, ackBit);

    return i2cData;
  }

  /**
   * Send I2C Stop sequence.
   */
  public void stop() {
    CPU.writePin(sdaPin, false);          // make sure SDA is low
    CPU.setInput(sclPin);                 // let SCL pin go high first
    CPU.setInput(sdaPin);
  }
}