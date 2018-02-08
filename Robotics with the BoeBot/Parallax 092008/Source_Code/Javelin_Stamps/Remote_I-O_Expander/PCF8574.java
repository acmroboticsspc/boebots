package stamp.peripheral.io;

import stamp.core.*;
import stamp.peripheral.I2C;

/**
 * This class provides an interface to the Philips PCF8574 (8-bit bi-
 * directional port expander).  PCF8574 inputs and outputs are active low;
 * pins used as inputs should be pulled-up to Vdd through 10K.
 * <p>
 * To create a PCF8574 object, the programmer will usually define a [shared]
 * I2C bus object first and pass this object to the PCF8574 constructor.
 * <p>
 * <code>
 *   I2C ioBus = new I2C(CPU.pin0, CPU.pin1);
 *   PCF8574 ioPort = new PCF8574(ioBus, 0x00, 0xF0);
 * </code>
 * <p>
 * If a single PCF8574 is the only I2C device connected to the Javelin, an
 * alternate constructor may be used that creates the necessary I2C bus.
 * Note that this bus is private to the PCF8574 and cannot be shared with any
 * other device.
 * <p>
 * <code>
 *   PCF8574 ioPort = new PCF8574(CPU.pin0, CPU.pin1, 0x00, 0xF0);
 * </code>
 * <p>
 * For additonal operational details, consult Philips documentation for the
 * PCF8574.
 *
 * @author Jon Williams (jwilliams@parallaxinc.com)
 * @version 1.1 15 July 2002
 */
public class PCF8574 {

  private I2C bus;                        // I2C bus pins
  private int addr = 0x40;                // base control address for PCF8574
  private int dirs = 0xFF;                // assume all pins are inputs


  /**
   * Create PCF8574 object
   *
   * @param bus I2C bus object
   * @param devAddr Device address (0 .. 7)
   * @param dirs PCF8574 pin directions (1 = input, 0 = output)
   */
  public PCF8574 (I2C bus, int devAddr, int dirs) {
    this.bus = bus;
    this.addr = this.addr | ((devAddr & 0x07) << 1);
    this.dirs = dirs;
  }


  /**
   * Create PCF8574 object with private I2C bus.  This method is not
   * recommended unless the PCF8574 is the only I2C device connected to
   * the Javelin.
   *
   * @param sdaPin I2C serial data pin
   * @param sclPin I2C serial clock pin
   * @param devAddr Device address (0 .. 7)
   * @param dirs PCF8574 pin directions (1 = input, 0 = output)
   */
  public PCF8574 (int sdaPin, int sclPin, int devAddr, int dirs) {
    this.bus = new I2C(sdaPin, sclPin);
    this.addr = this.addr | ((devAddr & 0x07) << 1);
    this.dirs = dirs;
  }


  /**
   * Check for presence of specific PCF8574 device.
   *
   * @return True if device exists on bus
   */
  public boolean isPresent() {
    boolean deviceAck;

    bus.start();
    deviceAck = bus.write(addr);
    bus.stop();

    return deviceAck;
  }


  /**
   * Set PCF8574 outputs (pins masked with dirs value).
   *
   * @param outData Data to write to PCF8574 port
   */
   public void write(int outData) {
    bus.start();
    bus.write(addr);                      // send write addres
    bus.write(outData | dirs);            // write new outputs to buffer
    bus.write(outData | dirs);            // force data to output pins
    bus.stop();
  }


  /**
   * Get PCF8574 inputs (pins masked with dirs value)
   *
   * @return Data from PCF8574 inputs
   */
  public int read() {
    int dataIn;

    bus.start();
    bus.write(addr | 1);                  // send read address (bit zero is set)
    bus.read(bus.ACK);                    // read buffer
    dataIn = bus.read(bus.NAK);           // read current inputs
    bus.stop();

    return (dataIn & dirs);
  }
}