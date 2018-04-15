/*
 * Copyright © Micromega Corporation 2002-2003. All rights reserved
 */
package stamp.math;
import  stamp.core.*;
/**
 * This class provides low-level support for manipulating single precision
 * floating point numbers. The floating point numbers are stored and
 * manipulated in two floating point registers called A and B.  Integer
 * values are stored in register I.
 * All methods and fields are protected and only intended to be used by
 * classes that are part of the math package.
 *
 * @author Cam Thompson, Micromega Corporation
 * @version 1.1 - April 27, 2003
 * changes:
 *    V1.1 - April 27, 2003
 *      - modified convertAtoI, convertItoA, and divideI methods
 *    V1.0 - April 8, 2003
 *      - original version
 *      - code moved from Float32 class
 */

public class Fpu32 {
  // error flag (true if an error occurred during last operation)
  protected static int errorFlag;
  private static final int OVERFLOW_ERROR = 1;
  private static final int UNDERFLOW_ERROR = -1;
  private final static int PINF_HIGH = 0x7F80;
  private final static int NINF_HIGH = (short)0xFF80;

  // the exponent bias
  protected static final int BIAS = 127;
  // floating point MSB of mantissa
  protected static final int SIGN_BIT = (short)0x8000;
  // floating point MSB of mantissa
  protected static final int MSB = 0x80;
  // internal carry bit
  protected static final int CARRY_BIT = 0x100;

  // floating point register A
  protected static int expA;
  protected static boolean signA;
  protected static int highA, lowA, tailA;

  // floating point register B
  protected static int expB;
  protected static boolean signB;
  protected static int highB, lowB, tailB;

  // integer register I
  protected static int highI, lowI;
  protected static int remHigh, remLow;

  //------------------- loadA -------------------------------------------------
  /*
   * Load A (the primary floating point register).
   */
  protected static void loadA(int high, int low) {
    // clear error flag and load register
    errorFlag = 0;
    signA = high < 0;
    expA = (high >> 7) & 0xFF;
    highA = high & 0x7F;
    lowA = low;
    tailA = 0;
    if ((high & 0x7FFF) == 0 && low == 0) return;

    // adjust for bias and normalize the number
    if (expA != 0) {
      expA -= BIAS;
      highA |= MSB;
    }
    else {
      expA = -126;
      while ((highA & MSB) == 0) {
        highA = (highA << 1) + (lowA >>> 15);
        lowA <<= 1;
        expA--;
      }
    }
  } // end loadA

  //------------------- loadB -------------------------------------------------
  /*
   * Load A (the secondary floating point register).
   */
  protected static void loadB(int high, int low) {
    // load register
    signB = high < 0;
    expB = (high >> 7) & 0xFF;
    highB = high & 0x7F;
    lowB = low;
    tailB = 0;
    if ((high & 0x7FFF) == 0 && low == 0) return;

    // adjust for bias and normalize the number
    if (expB != 0) {
      expB -= BIAS;
      highB |= MSB;
    }
    else {
      expB = -126;
      while ((highB & MSB) == 0) {
        highB = (highB << 1) + (lowB >>> 15);
        lowB <<= 1;
        expB--;
      }
    }
  } // end loadB

  //------------------- storeA ------------------------------------------------
  /*
   * Store A to the Float32 object passed.
   */
  protected static void storeA(Float32 fnum) {
    // check if number needs to be denormalized
    highA &= 0xFF;
    if (expA < -126 && expA > -150) {
      shiftA(126 + expA);
      expA = -127;
      if (highA == 0 && lowA == 0) errorFlag = UNDERFLOW_ERROR;
    }
    // check for overflow
    if (errorFlag == OVERFLOW_ERROR || expA > 127) {
      fnum.high = (signA) ? NINF_HIGH : PINF_HIGH;
      fnum.low = 0;
      return;
    } // check for underflow
    else if (errorFlag == UNDERFLOW_ERROR || expA < -127) {
      fnum.high = 0;
      if (signA) fnum.high |= SIGN_BIT;
      fnum.low = 0;
    } // store the number
    else {
      // if not zero, add the bias
      if (expA != 0 || highA != 0 || lowA != 0) expA += BIAS;
      fnum.high = (expA << 7) + (highA & 0x7F);
      if (signA) fnum.high |= SIGN_BIT;
      fnum.low = lowA;
    }
  } // end storeA

  //------------------- normalizeA ---------------------------------------------
  /*
   * normalize A (also checks for carry from last operation).
   */
  protected static int normalizeA() {

    // check for carry bit and normalize by right shift
    if ((highA & CARRY_BIT) != 0) {
      shiftA(-1);
      if(++expA > 127) return(overflow());

      // round the number
      if ((lowA & 1) != 0 && tailA < 0) {
        lowA++;
        if (lowA == 0) highA++;
        if ((highA & CARRY_BIT) != 0) {
          shiftA(-1);
          if (++expA > 127) return(overflow());
        }
      } // end if
      return (0);
    } // end if (right shift)

    // check for zero
    if (highA == 0 && lowA == 0 && tailA == 0) {
      signA = false;
      expA = 0;
      return(0);
    }

    // normalize by shifting left
    while ((highA & MSB) == 0) {
      if (highA == 0) {
        shiftA(8);
        expA -= 8;
      }
      else {
        shiftA(1);
        expA--;
      } // end if
    } // end while

    // round the number
    if (tailA < 0 && ((lowA & 1) != 0 || (tailA & 0x7FFF) != 0)) {
      lowA++;
      if (lowA == 0) highA++;
      if ((highA & CARRY_BIT) != 0) {
        shiftA(-1);
        if (++expA > 127) return(overflow());
      }
    }

    // check for underflow or zero
    if (expA <= -150) return(underflow());
    else if (highA == 0 && lowA == 0) {
      expA = 0;
      tailA = 0;
    }

    return(0);
  } // end normalizeA

 //------------------- negateA -----------------------------------------------
 /*
  * Negate A (A = -A).
  * note: only operates on mantissa and does not normalize.
  */
  protected static void negateA() {
    highA = ~highA & 0xFF;
    lowA = ~lowA;
    tailA = -tailA;
    if (tailA == 0) lowA++;
    if (lowA == 0) highA++;
  } // end negateA

  //------------------- negateB -----------------------------------------------
 /*
  * Negate B (B = -B).
  * note: only operates on mantissa and does not normalize.
  */
  protected static void negateB() {
      highB = ~highB & 0xFF;
      lowB = ~lowB;
      tailB = -tailB;
      if (tailB == 0) lowB++;
      if (lowB == 0) highB++;
  } // end negateB

  //------------------- addAB -----------------------------------------------
  /*
  * Add A and B (A = A + B).
  * note: only operates on mantissa and does not normalize.
  */
  protected static void addAB() {
    highA &= 0xFF;
    tailA += tailB;
    if (CPU.carry()) lowA++;
    if (CPU.carry()) highA++;
    lowA += lowB;
    if (CPU.carry()) highA++;
    highA += (highB & 0xFF);
  } // end addAB

  //------------------- subtractAB --------------------------------------------
 /*
  * Subtract B from A (A = A - B).
  * note: only operates on mantissa and does not normalize. (preserves B)
  */
  protected static void subtractAB() {
    int tmpHigh = highA & 0xFF;
    int tmpLow = lowA;
    int tmpTail = tailA;
    highA = ~highB & 0xFF;
    lowA = ~lowB;
    tailA = -tailB;
    if (tailA == 0) lowA++;
    if (lowA == 0) highA++;
    tailA += tmpTail;
    if (CPU.carry()) lowA++;
    lowA += tmpLow;
    if (CPU.carry()) highA++;
    highA += tmpHigh;
  } // end subtractAB

  //------------------- shiftA ------------------------------------------------
  /*
   * Shift A left if cnt is positive or right if cnt is negative.
   * note: only operates on mantissa and does not normalize.
   */
  protected static void shiftA(int cnt) {
    // check for left shift by 1
    if (cnt == 1) {
      highA = ((highA & 0xFF) << 1) + (lowA >>> 15);
      lowA = (lowA << 1) + (tailA >>> 15);
      tailA <<= 1;
      return;
    }

    // check for right shift by 1
    if (cnt ==  -1) {
      tailA = (tailA >>> 1) + ((lowA & 1) << 15);
      lowA = (lowA >>> 1) + ((highA & 1) << 15);
      highA >>>= 1;
      return;
    }
    highA &= 0xFF;

    // shift left by multiple bits
    if (cnt > 0) {
      if (cnt > 15) {
        lowA = tailA;
        tailA = 0;
        cnt -= 16;
      }
      // shift remaining bits left
      if (cnt > 0) {
        int mask = ~((short)0xFFFF >>> cnt);
        highA = (highA << cnt) + ((lowA & mask) >>> (16-cnt));
        lowA = (lowA << cnt) + ((tailA & mask) >>> (16-cnt));
        tailA <<= cnt;
      }
    }
    // shift right by multiple bits
    else if (cnt < 0) {
      cnt = -cnt;
      if (cnt > 15) {
        tailA = lowA;
        lowA = highA;
        highA = 0;
        cnt -= 16;
      }
      // shift remaining bits right
      if (cnt > 0) {
        int mask = (short)0xFFFF >>> (16-cnt);
        tailA = (tailA >>> cnt) + ((lowA & mask) << (16-cnt));
        lowA = (lowA >>> cnt) + ((highA & mask) << (16-cnt));
        highA >>>= cnt;
      } // end if
    } // end if
  } // end shiftA

  //------------------- shiftB ------------------------------------------------
  /*
   * Shift B left if cnt is positive or right if cnt is negative.
   * note: only operates on mantissa and does not normalize.
   */
  protected static void shiftB(int cnt) {
    // check for left shift by 1
    if (cnt == 1) {
      highB = ((highB & 0xFF) << 1) + (lowB >>> 15);
      lowB = (lowB << 1) + (tailB >>> 15);
      tailB <<= 1;
      return;
    }

    // check for right shift by 1
    if (cnt == -1) {
      tailB = (tailB >>> 1) + ((lowB & 1) << 15);
      lowB = (lowB >>> 1) + ((highB & 1) << 15);
      highB >>>= 1;
      return;
    }
    highB &= 0xFF;

    // shift left by multiple bits
    if (cnt > 0) {
      if (cnt > 15) {
        highB = lowB;
        lowB = tailB;
        tailB = 0;
        cnt -= 16;
      }
      // shift remaining bits left
      if (cnt > 0) {
        int mask = ~((short)0xFFFF >>> cnt);
        highB = (highB << cnt) + ((lowB & mask) >>> (16-cnt));
        lowB = (lowB << cnt) + ((tailB & mask) >>> (16-cnt));
        tailB <<= cnt;
      }
    }
    // shift right by multiple bits
    else if (cnt < 0) {
      cnt = -cnt;
      if (cnt > 15) {
        tailB = lowB;
        lowB = highB;
        highB = 0;
        cnt -= 16;
      }
      // shift remaining bits right
      if (cnt > 0) {
        int mask = (short)0xFFFF >>> (16-cnt);
        tailB = (tailB >>> cnt) + ((lowB & mask) << (16-cnt));
        lowB = (lowB >>> cnt) + ((highB & mask) << (16-cnt));
        highB >>>= cnt;
      } // end if
    } // end if
  } // end shiftB

  //------------------- multiplyAB --------------------------------------------
  /*
   * Load B and Multiply A times B.
   */
  protected static void multiplyAB(int high, int low) {
    loadB(high, low);
    multiplyAB();
  }
  /*
   * Multiply A times B (A = A * B)
   */
  protected static int multiplyAB() {

    // get the sign of the result
    signA = signA ^ signB;

    // check for multiply by 1
    if (expB == 0 && highB == MSB && lowB == 0) return(0);
    else if (expA == 0 && highA == MSB && lowA == 0) {
      expA = expB;
      highA = highB;
      lowA = lowB;
      return (0);
    }

    // get exponent and check for underflow or overflow
    expA += (expB + 1);
    if (expA < -150) return(underflow());
    else if (expA > 127) return(overflow());

    // get multiplicand and clear product
    int multHigh = highA;
    int multLow = lowA;
    highA = 0;
    lowA = 0;
    tailA = 0;

    // multiply the number
    for (int i = 0; i < 24; i++) {
      // if LSB of multiplicand is set, add multiplier to product
      if ((multLow & 1) != 0)
        addAB();
      else
        highA &= 0xFF; //clear carry bit

      // shift product right by 1 (carry from add will enter MSB)
      shiftA(-1);

      // shift multiplicand right by 1
      multLow = (multLow >>> 1) + ((multHigh & 1) << 15);
      multHigh >>>= 1;
    }

    normalizeA();
    return(0);
  } // end multiplyAB

  //------------------- multiplyA10 -------------------------------------------
  /*
   * Multiply A times 10.0 (A = A * 10.0).
   */
  protected static void multiplyA10() {
    // a quicker multiply of A by 10.0
    if (highA == 0 && lowA == 0) return;
    highB = highA;
    lowB = lowA;
    tailB = tailA;
    shiftA(-2);
    addAB();
    expA += 3;
    normalizeA();
  } // end multiplyA10

  //------------------- divideAB ----------------------------------------------
  /*
   * Load B and Divide A by B.
   */
  protected static void divideAB(int high, int low) {
    loadB(high, low);
    divideAB();
  }
  /*
   * Divide A by B (A = A / B).
   */
  protected static int divideAB() {

    // get the sign of the result
    signA = signA ^ signB;

    // check for divide by 1
    if (expB == 0 && highB == MSB && lowB == 0) return(0);

    // get the new exponent
    expA -= expB;
    if (expA < -150) return(underflow());
    else if (expA > 127) return(overflow());

    // If the mantissas are equal return 1
    if (highA == highB && lowA == lowB && tailA == tailB) {
      highA = MSB;
      lowA = 0;
      return (0);
    }

    // make sure the divisor mantissa is greater than the dividend mantissa
    int tmpHigh = highA;
    int tmpLow = lowA;
    int cnt = 23;
    subtractAB();
    if ((highA & CARRY_BIT) == 0) {
      // adjust the divisor
      cnt = 24;
      highA = tmpHigh;
      lowA = tmpLow;
      if (--expA < -150) return(underflow());
    }
    shiftB(-1);
    Fpu32.tailB = 0;

    // divide the number
    int quotHigh = 0;
    int quotLow = 0;
    boolean carry;

    for (int i = 0; i < cnt; i++) {
      // subtract divisor and check if carry occurred
      tmpHigh = highA;
      tmpLow = lowA;
      subtractAB();
      carry = (highA & CARRY_BIT) == 0;

      // if carry occurred, restore the dividend
      if (carry) {
        highA = tmpHigh;
        lowA = tmpLow;
      }
      // shift partial quotient and dividend (next quotient bit enters LSB)
      quotHigh = (quotHigh << 1) + (quotLow >>> 15);
      quotLow <<= 1;
      if (carry) quotLow++;
      shiftA(1);
    } // end for

    // compliment the quotient
    quotHigh = ~quotHigh & 0xFF;
    quotLow = ~quotLow;

    // check for rounding and last shift
    subtractAB();
    if ((highA & CARRY_BIT) != 0) {
      quotLow++;
      if (quotLow == 0) quotHigh++;
    }
    highA = quotHigh & 0xFF;
    lowA = quotLow;
    if ((quotHigh & CARRY_BIT) != 0) shiftA(-1);

    normalizeA();
    return(0);
  } // end divideAB

  //------------------- compareAB ---------------------------------------------
  /*
   * Load B and Compare A and B.
   */
  protected static int compareAB(int high, int low) {
    loadB(high, low);
    return(compareAB());
  }
  /*
   * Compare A and B (A>B return 1, A=B return 0, A<B returns -1).
   */
  protected static int compareAB() {
    int r = (signA & signB) ? -1 : 1;
    if (signA != signB) return((signA) ? -r : r);
    if (expA != expB) return((expA > expB) ? r : -r);
    int tmpHigh = ~highB;
    int tmpLow = -lowB;
    if (tmpLow == 0) tmpHigh++;
    tmpHigh &= 0xFF;
    tmpLow += lowA;
    if (CPU.carry()) tmpHigh++;
    tmpHigh += (highA & 0xFF);
    if (tmpHigh == CARRY_BIT && tmpLow == 0) return (0);
    return(((tmpHigh & CARRY_BIT) != 0) ? r : -r);
  } // end compareAB

  //------------------- intToA() ----------------------------------------------
  /*
   * Convert a 16-bit signed integer to floating point and store in A.
   */
  protected static void intToA(int n) {
    expA = 23;
    highA = 0;
    if (n < 0) {
      signA = true;
      lowA = -n;
    }
    else {
      signA = false;
      lowA = n;
    }
    tailA = 0;
    normalizeA();
  } // end intToA

  //------------------- convertItoA() ------------------------------------------
  /*
   * Set A to floating point equivalent of the 24-bit integer value in I.
   */
  protected static void convertItoA() {
    // get the integer value
    expA = 23;
    signA = false;
    lowA = lowI;
    tailA = 0;
    // scale down the value to 24 bits of resolution
    highA = highI;
    while ((highA & 0xFF) != 0) {
      shiftA(-1);
      expA++;
    }
    normalizeA();
  } // end convertItoA

  //------------------- convertAtoI -------------------------------------------
  /*
   * set I to the integer portion of A, and set A to the fractional portion.
   */
  protected static void convertAtoI() {

    // check for integer value of zero
    if (expA < 0) {
      highI = 0;
      lowI = 0;
    }
    else if (expA < 8) {
      highI = 0;
      lowI = (highA >>> (7 - expA));
      highA &= (~((short)0xFF80 >> expA));
    }
    else if (expA < 23) {
      highI = highA >>> (23 - expA);
      lowI = (highA << (expA - 7)) + (lowA >>> (23 - expA));
      highA = 0;
      lowA &= (~(SIGN_BIT >> (expA - 8)));
    }
    else if (expA < 30) {
      highI = highA & 0xFF;
      lowI = lowA;
      if (expA > 23) {
        highI = (highI << (expA - 23)) +
                ((lowI & (~((short)0xFFFF >>> (expA - 23)))) >>> (39 - expA));
        lowI <<= (expA - 23);
      }
      highA = 0;
      lowA = 0;
    }
    else {
      highI = 0x7FFF;
      lowI = (short)0xFFFF;
      highA = 0;
      lowA = 0;
    }
    // normalize the fraction
    tailA = 0;
    normalizeA();

  } // end convertAtoI

  //------------------- addI -------------------------------------------------
  /*
   * Add a 32-bit integer value to I.
   */
  protected static void addI(int high, int low) {
    lowI += low;
    if (CPU.carry()) highI++;
    highI += high;
  } // end addI

  //------------------- multiplyI10 -------------------------------------------
  /*
   * multiply I by 10.
   */
  protected static void multiplyI10() {
    if (highI == 0 && lowI == 0) return;
    int tmpHigh = (highI << 1) + (lowI >>> 15);
    int tmpLow = lowI << 1;
    highI = (tmpHigh << 2) + (tmpLow >>> 14);
    lowI = tmpLow << 2;
    addI(tmpHigh, tmpLow);
  } // end multiplyI10

  //------------------- divideI -----------------------------------------------
  /*
   * divide I by a 24-bit integer value and return quotient.
   */

  protected static void divideI(int high, int low) {
    int tmpHigh, tmpLow, carry;

    // check for fast integer divide
    if (highI == 0 && high == 0 && lowI > 0 && low > 0) {
      remHigh = 0;
      remLow = lowI % low;
      lowI = lowI / low;
      return;
    }

    // left justify the dividend
    int cnt = 32;
    if (highI == 0) {
      highI = lowI;
      cnt = 16;
    }
    while (highI > 0) {
      highI = (highI << 1) + (lowI >>> 15);
      lowI = lowI << 1;
      cnt--;
    }

    // divide the number
    int quotHigh = 0;
    int quotLow = 0;
    remHigh = 0;
    remLow = 0;
    for (int i = 0; i < cnt; i++) {
      // rotate dividend left
      carry = highI >>> 15;
      highI = (highI << 1) + (lowI >>> 15);
      lowI = lowI << 1;

      // rotate remainder left with carry
      remHigh = (remHigh << 1) + (remLow >>> 15);
      remLow = (remLow << 1) + carry;

      // subtract divisor from remainder
      tmpHigh = remHigh - high;
      tmpLow = remLow - low;
      if (!CPU.carry()) tmpHigh--;

      // if divisor < remainder update remainder
      // rotate bit into quotient
      quotHigh = (quotHigh << 1) + (quotLow >>> 15);
      if (tmpHigh >= 0) {
        remHigh = tmpHigh;
        remLow = tmpLow;
        quotLow = (quotLow << 1) + 1;
      }
      else {
        quotLow = (quotLow << 1);
      }
    } // end for

    highI = quotHigh;
    lowI = quotLow;
  } // end divideI

  //------------------- underflow -------------------------------------------

  protected static int underflow() {
    errorFlag = UNDERFLOW_ERROR;
    expA = -126;
    highA = 0;
    lowA = 0;
    return(errorFlag);
  } // end underflow

  //------------------- overflow -------------------------------------------

  protected static int overflow() {
    errorFlag = OVERFLOW_ERROR;
    expA = 127;
    highA = 0x007F;
    lowA = (short)0xFFFF;
    return(errorFlag);
  } // end overflow

} // end class