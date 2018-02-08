/*
 * Copyright © 2002 Parallax Inc. All rights reserved.
 */

package stamp.peripheral.io.ADC;
import stamp.core.*;

/**
 * This class encapsulates the capabilities of the 12-bit LTC1298 3-wire A to D.<p>
 * For more information on this class and the circuit please see Parallax
 * Application Note #008.
 *
 * There are 2 types of setup's this class will handle:<br><pre>
 * Setup A: Reading the measurement taken directly from:
 *          Channel 0 (CH0) use <code>read(0)</code>
 *          Channel 1 (CH1) use <code>read(1)</code>
 *
 * Setup B: Read the difference between Channel 0 and Channel 1.
 *          To subtract CH1 from CH0 use <code>read(2)</code>
 *          To subtract CH0 from CH1 use <code>read(3)</code>
 * <p>
 * Revision History:
 * Ver 1.0 - 11/29/02: Initial release of class submitted to Parallax Inc.
 *                     by customer Tim Constable of Boston, MA
 *                     Evaluated and modified by Steve Dill of Parallax Inc.
 * </pre>
 * @author Tim Constable of Boston, MA
 * @version 1.0 Nov 27, 2002
 */
public class LTC1298 extends AtoD {

  // LTC1298 Commands some are shifted 11 bits
  final static int read0    = 0x1a;       // read(0) Read CH0 on pin2
  final static int read1    = 0x1e;       // read(1) Read CH1 on pin3
  final static int readDif0 = 0x12;       // read(2) Read Dif of CH1 from CH0
  final static int readDif1 = 0x16;       // read(3) Read Dif of CH0 from CH1
  final static int bitHigh  = 1;          // High bit of 1.220703125
  final static int bitLow   = 14464;      // Low bit of 1.220703125

  /**
   * Initialize the LTC1298 A to D Chip and bus.
   *
   * @param DataPin   - LTC1298 data pin #5 (tied to pin #1 with 1K resistor)
   * @param ClockPin  - LTC1298 clock pin #7
   * @param EnablePin - LTC1298 enable pin #1
   */
  public LTC1298(int dataPin, int clockPin, int enablePin){

    // update protected variables
    this.dataPin   = dataPin;
    this.clockPin  = clockPin;
    this.enablePin = enablePin;

    readSize   = 12;                        // Bytes to Read on the LTC1298
    resolution = 4096;                      // Range of the LTC1298
    super.setBitValue(bitHigh,bitLow,0);    // Set bit value size in super class

    // initialize pins -- make low for high-going pulses
    CPU.writePin(enablePin,false);          // init the bus
    CPU.writePin(clockPin, false);
    CPU.delay(100);                         // settle down
    CPU.writePin(enablePin,false);
    CPU.writePin(enablePin,true);
  }// LTC1298(int,int,int)

  /**
   * Change <code>offset</code> value.<p>
   *
   * This offset is added to each voltage computed from the raw value of the
   * ADC chip.  The LTC1298 maximum voltage measured is 4.998.  To adjust this
   * to +5.000 V use <code>setOffset(2)</code>.  This setting can also be
   * useful if you set Vref to 4.5 V.  Use this value to fine tune your settings
   * without manipulating the high/low bits.
   *
   * @param offset, in millivolts, to add to calculation of voltage.
   *
   */
  public void setOffset(int offset) {
    super.setBitValue(bitHigh,bitLow,offset);
  }// setOffset

  /**
   * Read value from A to D chip.
   * The LTC1298 chip must be given a command as to which Port it is to read from.<br>
   * Channel 0 (CH0) use <code>read(0)</code><br>
   * Channel 1 (CH1) use <code>read(1)</code><br>
   * To subtract CH1 from CH0 use <code>read(2)</code><br>
   * To subtract CH0 from CH1 use <code>read(3)</code><br>
   *
   * @param command 0,1,2,3
   * @return raw value from chip
   */
  public int read(int command){
    int clockIn;
    // change command variable from 0,1,2,3 to 0x1a, 0x1e, 0x12, 0x16
    switch(command) {
      case 0:
        command = read0;               // change 0 to '0x1a'
        break;
      case 1:
        command = read1;               // change 1 to '0x1e'
        break;
      case 2:
        command = readDif0;            // change 2 to '0x12'
        break;
      case 3:
        command = readDif1;            // change 3 to '0x16'
        break;
      default:
        command = read0;               // Default to read CH0
        break;
    }// switch

    CPU.writePin(enablePin,false);
    // set port
    CPU.shiftOut(dataPin,clockPin,5,CPU.SHIFT_MSB,(command << 11));
    // read the value
    clockIn = CPU.shiftIn(dataPin,clockPin,readSize,CPU.PRE_CLOCK_MSB);
    CPU.writePin(enablePin,true);
    CPU.delay(100);
    lastRaw = clockIn;                 // lastRaw is protected
    super.calcMV(clockIn);
    return clockIn;
  }// clockIn

//============================================================================
// Methods and fields below this point are private.
//============================================================================

  // Pin Constants unique to chip
  private int dataPin;   // LTC1298 data pin #5 (tied to pin #1 with 1K resistor)
  private int clockPin;  // LTC1298 clock pin #7
  private int enablePin; // LTC1298 enable pin #1

}// LTC1298