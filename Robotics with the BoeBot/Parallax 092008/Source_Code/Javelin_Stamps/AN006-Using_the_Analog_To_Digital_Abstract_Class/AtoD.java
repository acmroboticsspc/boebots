/*
 * Copyright © 2002 Parallax Inc. All rights reserved.
 */

package stamp.peripheral.io.ADC;
import stamp.core.*;

/**
 * This is an abstract class encapsulating the capabilities of a generic
 * Analog to Digital converter (ADC).  This class is suitable for ADCs up to
 * 16-bits of resolution.<p>
 *
 * Includes methods for reading the RAW value from the ADC chip, as well as
 * methods for converting this RAW value to volts, millivolts, and temperature.<p><pre>
 *
 * Revision History:
 * Ver 1.0 - 11/27/02: Initial release of class submitted to Parallax Inc.
 *                     by customer Tim Constable of Boston, MA
 *                     Evaluated and modified by Steve Dill of Parallax Inc.
 * </pre>
 * @author Tim Constable of Boston, MA and Steve Dill of Parallax Inc.
 * @version 1.0 Nov 29, 2002
 */
public abstract class AtoD {

  static StringBuffer lbuf = new StringBuffer(5);

  /**
   * Get last read value and format it in volts.
   * @return voltage in a formatted string
  */
  public String lastVf() {
    lbuf.clear();
    lbuf.append(lastMV);
    if (lastMV < 1000) lbuf.insert(0,'0');
    if (lastMV < 100) lbuf.insert(0,'0');
    if (lastMV < 10) lbuf.insert(0,'0');
    lbuf.insert(1,'.');
    return lbuf.toString();
  }//lastVf

  /**
   * Get last calculated millivolt value.
   * @return voltage in millivolts
  */
  public int lastMV() {
    return lastMV;
  }//lastMV

  /**
   * Get raw DAC value from chip, value depends on resolution of chip.
   * @return raw value read from ADC
  */
  public int lastRaw() {
    return lastRaw;
  }//lastRaw

  /**
   * Each ADC has a bit value that represents the millivolt value between each
   * numeric difference in the RAW value received from the ADC.
   * This bit value needs to be calculated for each ADC chip, and then passed
   * into this method so the correct math can be performed to obtain the voltage.<p>
   *
   * Using the 8-bit ADC (ADC0831) as an example:<br>
   * Divide the range of the voltage being measured, lets say 5000 mV by the
   * resolution of the chip 256.<br>
   * <i>5000/256=19.53125</i><br>
   * This number 19.53125 is the bit value of this ADC.  Since the Javelin does
   * not have native floating point math, we will need to format this value and
   * pass it in as two values, a high bit & low bit.  The high bit (<code>bitHigh</code>),
   * is simply the whole number to the left of the decimal, in this case 19.
   * The low bit (<code>bitLow</code>), needs to follow the algorithm below:<br>
   * Shift low bit left by 16: <i>0.53125*65536=34816</i><br>
   * Then if the answer is greater than a signed integer (32767), which in our
   * example it is, you will do the following:<br>
   * Subtract the answer from 65536: <i>65536-34816=30720</i>, then negate it
   * <i>-30720</i>. These are the two values that you will pass in 19 & -30720.
   * If your ADC has the capability to begin measuring from anything but 0 volts,
   * you will need to place this amount as millivolts into the offset.
   * For example, if your ADC is set from 2 to 4 volts, then the offset will
   * be 2000 millivolts.<p>
   *
   * Known ADC values (16-bit max):<p>
   * 8-bit ADC0831 - bit value is 19.53125
   *                 <code>bitHigh</code> = 19
   *                 <code>bitLow</code> = -30720
   *                 <code>offset</code> = -Vin, 0 if tied to ground.<br>
   * 12-bit LTC1298 - bit value is 1.22073125
   *                  <code>bitHigh</code> = 1
   *                  <code>bitLow</code> = 14463
   *                  <code>offset</code> = 0<p>
   *
   * @param bitHigh High portion of the bit value
   * @param bitLow  Low portion of the bit value
   * @param offset  Offset for bit value, when -Vin is not tied to ground
   *
   */
  public void setBitValue(int bitHigh, int bitLow, int offset) {
    this.bitHigh       = bitHigh;
    this.bitLow        = bitLow;
    this.offset        = offset;
  }//setBitValue

  /**
   * Read value from A to D chip.<p>
   * This abstract method is used for a variety of A to D chips.
   * The channels are not implemented the same for each chip,
   * if your chip is not listed below, refer to the chip's datasheet as to
   * what 0, 1, 2 will be translated to.<p>
   *
   * ADC0831: Only has one channel CH0, use 0<br>
   * LTC1298: 0=CH0, 1=CH1, 2=CH0-CH1, 3=CH1-CH0<br>
   *
   * @param channel Specify 0,1,2,3 for specific ADC chip
   * @return raw value from chip
   */
  // read must be overridden
  public abstract int read(int channel);

  /**
   * Read from the ADC chip multiple times, then return the average.
   * Input values:
   * 1 = 2 reads, 2 = 4 reads, 3 = 8 reads, 4 = 16 reads, or 5 = 32 reads<br>
   *
   * This abstract method is used for a variety of A to D chips.
   * The channels are not implemented the same for each chip,
   * if your chip is not listed below, refer to the chip's datasheet as to
   * what 0, 1, 2, or 3 will be translated to.<p>
   *
   * ADC0831: Only has one channel CH0, use 0<br>
   * LTC1298: 0=CH0, 1=CH1, 2=CH0-CH1, 3=CH1-CH0<br>
   *
   * @param channel Specify 0,1,2,3 for specific ADC chip
   * @param times number of times to smooth, input 1,2,3,4,5 for 2,4,8,16,32 reads
   * @return smoothed value
   */
  public int readSmooth(int channel,int times) {
    boolean of=false;                             // Overflow
    clear(rsp);

    if ((times%32!=0)|(times>32)) times = 2;
    for (i=0;i<(1<<times);i++){
      clear(rsa);
      rsa[0]=read(channel);
      add(rsp,rsa,rsp,of);
    }//for

    clear(rsb);
    times+=48;
    rsb[0]=1;
    switch (times) {
      case '5':
        rsb[0]=rsb[0]<<11;
        break;
      case '4':
        rsb[0]=rsb[0]<<12;
        break;
      case '3':
        rsb[0]=rsb[0]<<13;
        break;
      case '2':
        rsb[0]=rsb[0]<<14;
        break;
      case '1':
        rsb[0]=rsb[0]<<15;
        break;
      default:
        rsb[0]=rsb[0]<<14;                       // Default set to 4 read
    }//switch

    clear(rsa);
    multiply(rsp,rsb,rsa);
    lastRaw=rsa[1];
    calcMV(lastRaw);
    return lastRaw;
  }

  /**
   * Convert answer into a temperature format for devices measuring the temperature as a voltage F.<p>
   * Additionally add the "F" character for printing.  This routine rounds up the tenths.
   *
   * @param sym - set to true to and add "F"
   * @return Temperature as string
   *
   */
  public String calcTemp(boolean sym) {
    lbuf.clear();
    i=lastMV/10;                              // truncate last digit
    if (lastMV%10>4) i++;                       // round up if 5 or higher
    lbuf.append(i);
    if (sym) lbuf.append("F");                // add Fahrenheit symbol
    return (lbuf.toString());
  }//calcTemp

//============================================================================
// Methods and fields below are protected.
//============================================================================

  /**
   * Bytes to read for specific ADC
   */
  protected int readSize;

  /**
   * Range of the sensor
   */
  protected int resolution;

  /**
   * last calculated raw ADC value
   */
  protected int lastRaw;

  /**
   * Calculates and stores millivolt value for later retrieval.
   * @param raw A to D value
   * @return millivolt
   */
  protected int calcMV(int raw) {
    clear(a);
    clear(b);
    a[0]=bitLow;
    a[1]=bitHigh;
    b[0]=raw;
    multiply(a,b,p);
    lastMV=p[1]+offset;
    return lastMV;
  }// end method: calcMV


//============================================================================
// Methods and fields below this point are private.
//============================================================================

  private   int bitHigh;               // high portion of bit value
  private   int bitLow;                // low portion of bit value (formatted)
  private   int offset;                // When using -Vin or correction
  private   int lastV;                 // last read volt value
  private   int lastMV;                // last millivolt value

  private int clockIn, sum_me, sum_temp, i; // general temp use declared once

  // Used for math routines not native to the Javelin
  private static int a[]   = new int[4];
  private static int b[]   = new int[4];
  private static int p[]   = new int[4];
  private static int rsa[] = new int[4];
  private static int rsb[] = new int[4];
  private static int rsp[] = new int[4];
  private static int temp[]= {0,0,0,0,0,0,0,0};

  /**
   * Clears all values in the array.
   * @param a the <code> int[]</code> array to be cleared.
   */
  private static void clear(int a []){
    for(int i = 0; i < a.length; i++)
      a[i] = 0;
  }//clear

  /**
   * Copies array b to array a.
   * @param a source <code> int[]</code> array.
   * @param b destination <code> int[]</code> array.
   */
  private static void copy(int a [], int b[]){
    for(int i = 0; i < a.length; i++)
      a[i] = b[i];
  }//copy

  /**
   * Add arrays a and b together, and add the sum to the c array.  If the
   * c array contains zeros before this method is called, c will contain
   * a + b.
   * @param a source <code> int[]</code> array.
   * @param b source <code> int[]</code> array.
   * @param c destination <code> int[]</code> array that contains the sum
   * of a + b.
   * @param overflow contains <code>true</code> if overflow occurred and
   * <code>false</code> if no overflow occurred.
   */
  private static void add(int a[], int b[], int c[], boolean overflow){
    overflow = false;
    for(int i = 0; i < c.length; i++){
      if((i==a.length)||(i==b.length)) break;
      if(overflow){
        overflow = false;
        c[i] = a[i] + b[i];
        if(CPU.carry()) overflow = true;
        c[i] = c[i] + 1;
        if(CPU.carry()) overflow = true;
      }else{
        c[i] = a[i] + b[i];
        overflow = CPU.carry();
      }
    }//for
  }//add

  /**
   * Multiply array a by array b, and place the result in array c.
   * @param a source <code> int[]</code> array.
   * @param b source <code> int[]</code> array.
   * @param c destination <code> int[]</code> array that contains the result
   * of a * b.
   * @param overflow contains <code>true</code> if overflow occurred and
   * <code>false</code> if no overflow occurred.
   */
  private static void multiply(int a [], int b[], int c[]){
    int mask = 1, shift = 0, cIndex = 0;
    boolean bool = false;
    clear(c);
    for(int aIndex = 0; aIndex < (a.length); aIndex++){
      for(int bIndex = 0; bIndex < (b.length); bIndex++){
        cIndex = aIndex + bIndex;
        for(int i = 0; i <= 15; i++){
          shift = mask << i;
          if((a[aIndex] & shift) != 0){
            temp[cIndex] = (b[bIndex] << i);
            temp[cIndex +1] = (b[bIndex] >>> (16-i));
            add(temp,c,c, bool);
          }//if
        }//for i
        temp[cIndex] = 0;
      }//for bIndex
    }//for aIndex
  }//multiply

}//end class: AtoD