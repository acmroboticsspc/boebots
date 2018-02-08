package stamp.peripheral.io;

import stamp.core.*;

/**
 * This class provides an interface to the 74xx165 parallel-in/serial-out
 * shift register.
 * <p>
 * 74xx165 Pin Connections: <BR>
 *   (1) Shift Load input (high-to-low transition latches inputs) <BR>
 *   (2) Clock input <BR>
 *   (3) E - bit 4 of input data <BR>
 *   (4) F - bit 5 of input data <BR>
 *   (5) G - bit 6 of input data <BR>
 *   (6) H - bit 7 of input data <BR>
 *   (7) Qh\ (inverted serial output) <BR>
 *   (8) Ground (Vss) <BR>
 *   (9) Qh (non-inverted serial output) <BR>
 *   (10) Serial input from daisy-chained 74xx165 <BR>
 *   (11) A - bit 0 of input data <BR>
 *   (12) B - bit 1 of input data <BR>
 *   (13) C - bit 2 of input data <BR>
 *   (14) D - bit 3 of input data <BR>
 *   (15) Clock Inhibit (normally tied to Vss [ground]) <BR>
 *   (16) Vdd (+5 volts) <BR>
 * <p>
 * <b>Design Note:</b> Load pin (74xx165.12) should be pulled high to Vdd through
 * a 10K resistor to ensure proper non-pulse state if pin becomes an input.
 * <p>
 * <b>Connection Note:</b> If the inputs to the 74xx165 are active low, the
 * inverted output (Qh\ - pin 7) can be used to simplify code (1 = active).
 * The exception is when using multiple devices in a daisy-chain (serial output
 * of one to serial input [pin 10] of another).  In this case, only the device
 * connected to the Javelin should use the inverted output.  All other devices
 * should use the non-inverted output to maintain the data stream through the chain.
 * <p>
 * Usage:
 * <p>
 * <code>
 *   // data on pin0, clock on pin1, latch on pin2
 *   SR74xx165 buttons = new SR74xx165(CPU.pin0, CPU.pin1, CPU.pin2);
 * </code>
 * <p>
 *
 * @author Jon Williams, Parallax Inc. (jwilliams@parallaxinc.com)
 * @version 1.0 18 July 2002
 */
public class SR74xx165 {

  private int dataPin, clockPin, loadPin;
  private int data165;

  /**
   * Creates 75xx165 parallel-in/serial-out shift register object.
   *
   * @param dataPin 74xx165 serial output (74xx165.pin9)
   * @param clockPin 74xx165 shift clock (74xx165.pin2)
   * @param loadPin 74xx165 input load (74xx165.pin1)
   */
  public SR74xx165 (int dataPin, int clockPin, int loadPin) {
    this.dataPin = dataPin;
    this.clockPin = clockPin;
    this.loadPin = loadPin;
    CPU.setInput(dataPin);
    CPU.writePin(clockPin, false);        // initialize for high pulses
    CPU.writePin(loadPin, true);          // initalize for low pulse
  }


  /**
   * Loads inputs and retrieves data from 74xx165.
   *
   * @return 8-bit value from 74xx165 [chain]
   */
  public int in() {
    CPU.pulseOut(1, loadPin);             // load inputs
    return inNoLoad();                    // return loaded data
  }


  /**
   * Retrieves previously-loaded data from 74xx165. This method is used when
   * multiple 74xx165s are daisy-chained together.
   *
   * @return 8-bit value from 74xx165 [chain]
   */
  public int inNoLoad() {
    return CPU.shiftIn(dataPin, clockPin, 8, CPU.PRE_CLOCK_MSB);
  }
}