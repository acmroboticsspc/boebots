/*
 * Copyright © 2002 Parallax Inc. All rights reserved.
 */

package stamp.peripheral.io.ADC;
import stamp.core.*;

/**
 * This class encapsulates the capabilities of the 8 bit ADC0831 3-wire A to D.<p><pre>
 *
 * There are 4 types of setup's this class will handle:
 * Setup A: The simplest, tie vRef to +5 V and -Vin to ground.
 *          Use default bit values 19 & -30720
 * Setup B: Set vRef to the max voltage of the device being measured and -Vin to
 *          ground.  You will need to calculate new bit values, since the range
 *          of measurement will be smaller. This will make the bit value smaller
 *          and more accurate.
 * Setup C: Set vRef to max voltage of the device being measured and -Vin to the
 *          negative terminal of the device being measured. The ADC0831 will
 *          measure the voltage between -Vin and +Vin.
 *          You will need to calulate a new bit value.
 * Setup D: Tie vRef to +5 and -Vin to the negative terminal of the device being
 *          measured the ADC0831 will measure the voltage between -Vin and +Vin.
 *          You will need to calulate a new bit value as well as a new offset.
 * <p>
 * Revision History:
 * Ver 1.0 - 12/12/02: Initial release of class submitted to Parallax Inc.
 *                     by customer Tim Constable of Boston, MA
 *                     Evaluated and modified by Steve Dill of Parallax Inc.
 * </pre>
 * @author Tim Constable  Boston, MA
 * @version 1.0 Dec 12, 2002
 */

public class ADC0831 extends AtoD {

  final static int bitHigh  = 19;           // High bit of 19.53125
  final static int bitLow   = -30720;       // Low bit of 19.53125

  /**
   * Initialize the bus and A to D Chip.
   *
   * @param enablePin ADC0831 pin#1
   * @param dataPin   ADC0831 pin#6
   * @param clockPin  ADC0831 pin#7
   */
  public ADC0831(int dataPin, int clockPin, int enablePin){

    // update protected variables
    this.dataPin   = dataPin;
    this.clockPin  = clockPin;
    this.enablePin = enablePin;
    readSize   = 9;                         // Bits to Read on the ADC0831
    resolution = 256;                       // Range of the ADC0831

    super.setBitValue(bitHigh,bitLow,0);    // Set bit value size in super class

    // initialize pins -- make low for high-going pulses
    CPU.writePin(enablePin,false);          // init the bus
    CPU.writePin(clockPin, false);
    CPU.delay(100);                         // settle down
    CPU.writePin(enablePin,false);
    CPU.writePin(enablePin,true);
  }// end constructor: ADC0831

  /**
   * Set/change bit and offset value.<p>
   * Use this method when Vref is not tied to +5.<br>
   * Offset is needed when -Vin is not tied to ground.<p>
   *
   * For specific information on how to calculate the bit value see the
   * AtoD abstract class's method setBitValue, or read Parallax Inc's
   * application note #006.
   * @param bitHigh High portion of the bit value
   * @param bitLow  Low portion of the bit value
   * @param offset  Offset for bit value, when -Vin is not tied to ground
   */
  public void setBitValue(int bitHigh, int bitLow, int offset) {
    super.setBitValue(bitHigh,bitLow,offset);
  }//end method: setBitValue

  /**
   * Read value from A to D chip.<p>
   * Only one channel is available on the ADC0831
   * @param channel dummy value for ADC0831 to conform to base class
   * @return raw value from chip
   */
  public int read(int channel){
    int clockIn;
    CPU.writePin(enablePin,false);
    clockIn = CPU.shiftIn(dataPin,clockPin,readSize,CPU.POST_CLOCK_MSB);
    CPU.writePin(enablePin,true);
    CPU.delay(100);
    lastRaw = clockIn;                               // raw is protected
    super.calcMV(clockIn);
    return clockIn;
  }// end method: read

//============================================================================
// Methods and fields below this point are private.
//============================================================================

  // Pin Constants unique to chip
  private int enablePin;                             // ADC0831 enable pin #1
  private int dataPin;                               // ADC0831 data pin #6
  private int clockPin;                              // ADC0831 clock pin #7

}// end class: ADC0831