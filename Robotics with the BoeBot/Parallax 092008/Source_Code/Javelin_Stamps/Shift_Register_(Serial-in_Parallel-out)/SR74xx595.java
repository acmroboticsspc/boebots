package stamp.peripheral.io;

import stamp.core.*;

/**
 * This class provides an interface to the 74xx595 serial-in/parallel-out
 * shift register.
 * <p>
 * 74xx595 Pin Connections: <BR>
 *   (1) Qb output - bit 1 of output data <BR>
 *   (2) Qc output - bit 2 of output data <BR>
 *   (3) Qd output - bit 3 of output data <BR>
 *   (4) Qe output - bit 4 of output data <BR>
 *   (5) Qg output - bit 5 of output data <BR>
 *   (6) Qg output - bit 6 of output data <BR>
 *   (7) Qh output - bit 7 of output data <BR>
 *   (8) Ground (Vss) <BR>
 *   (9) SQh (serial output to next 74xx595) <BR>
 *   (10) Reset (normally connected to Vdd) <BR>
 *   (11) Shift clock (connects to defined clock output from Javelin) <BR>
 *   (12) Latch clock(connects to defined latch outp from Javelin) <BR>
 *   (13) Output enable (normally connected to Vss to allow outputs) <BR>
 *   (14) Serial data input (connects to defined data output from Javelin) <BR>
 *   (15) Qa output - bit 0 of output data <BR>
 *   (16) Vdd (+5 volts) <BR>
 * <p>
 * <b>Design Note:</b> Latch pin (74xx595.12) should be pulled low to Vss through
 * a 10K resistor to prevent false output latching during Javelin start-up.
 * <p>
 * Usage:
 * <p>
 * <code>
 *   SR74xx595 leds = new SR74xx595(CPU.pin0, CPU.pin1, CPU.pin2);
 * </code>
 * <p>
 * Note that the data pin is an output; be careful when sharing this pin
 * with other devices that expect it to be an input (i.e., 74xx165).
 *
 * @author Jon Williams, Parallax Inc. (jwilliams@parallaxinc.com)
 * @version 1.0 17 July 2002
 */
public class SR74xx595 {

  private int dataPin, clockPin, latchPin;

  /**
   * Creates 75xx595 serial-in/parallel-out shift register object.
   *
   * @param dataPin 74xx595 serial data (74xx595.pin14)
   * @param clockPin 74xx595 shift clock (74xx595.pin11)
   * @param latchPin 74xx595 output latch (74xx595.pin12)
   */
  public SR74xx595 (int dataPin, int clockPin, int latchPin) {
    this.dataPin = dataPin;
    this.clockPin = clockPin;
    this.latchPin = latchPin;
    // initialize pins -- make low for high-going pulses
    CPU.writePin(dataPin, false);
    CPU.writePin(clockPin, false);
    CPU.writePin(latchPin, false);
  }


  /**
   * Sends and latches data to 74xx595.
   *
   * @param value 8-bit value to send
   */
  public void out(int value) {
    outNoLatch(value);                    // shift data to device
    CPU.pulseOut(1, latchPin);            // latch new data to outputs
  }


  /**
   * Sends data to 74xx595 without latching to outputs.
   * <p>
   * This method is used when multiple 74xx595s are daisy-chained together.
   * When using multiple devices, the first data out is for the device furthest
   * from the Javelin.
   *
   * @param value 8-bit value to send
   */
  public void outNoLatch(int value) {
    CPU.shiftOut(dataPin, clockPin, 8, CPU.SHIFT_MSB, (value << 8));
  }
}