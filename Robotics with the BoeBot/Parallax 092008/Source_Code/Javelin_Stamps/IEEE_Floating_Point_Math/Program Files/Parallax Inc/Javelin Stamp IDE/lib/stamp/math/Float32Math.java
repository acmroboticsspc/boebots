/*
 * Copyright © Micromega Corporation 2002-2003. All rights reserved
 */
package stamp.math;
import  stamp.core.*;
/**
 * This class provides support for single precision floating point operations.
 * @author Cam Thompson, Micromega Corporation
 * @version 1.1 - May 2, 2003
 * changes:
 *    V1.1 - May 2, 2003
 *      - isZero method moved to Float32
 *      - added static working variables to prevent memory leaks
 *    V1.0 - April 28, 2003
 *      - original version
 */

public class Float32Math {

  // table of factorials for Maclaurin series calculation
  private static int factorials[] = {
    (short)0x3F00, (short)0x0000,   // 1 / 2!
    (short)0x3E2A, (short)0xAAAB,   // 1 / 3!
    (short)0x3D2A, (short)0xAAAB,   // 1 / 4!
    (short)0x3C08, (short)0x8889,   // 1 / 5!
    (short)0x3AB6, (short)0x0B61,   // 1 / 6!
    (short)0x3950, (short)0x0D01,   // 1 / 7!
    (short)0x37D0, (short)0x0D01,   // 1 / 8!
    (short)0x3638, (short)0xEF1D    // 1 / 9!
  };
  // floating point constants
  private static Float32 f0_001  = new Float32(0x3A83, (short)0x126F); // 0.001
  private static Float32 f0_5    = new Float32(0x3F00, (short)0x0000); // 0.5
  private static Float32 f1_0    = new Float32(0x3F80, (short)0x0000); // 1.0
  private static Float32 f2_0    = new Float32(0x4000, (short)0x0000); // 2.0
  private static Float32 f45_0   = new Float32(0x4234, (short)0x0000); // 45.0
  private static Float32 eLimit  = new Float32(0x42B1, (short)0x6FEC); // 88.7186
  private static Float32 f90_0   = new Float32(0x42B4, (short)0x0000); // 90.0
  private static Float32 f360_0  = new Float32(0x43B4, (short)0x0000); // 360.0
  private static Float32 f9fact  = new Float32(0x48B1, (short)0x3000); // 9!
  private static Float32 piOver180 = new Float32(0x3C8E, (short)0xFA35); // pi / 180.0
  // variable used to hold return values
  private static Float32 result  = new Float32();
  // temporary variables
  private static Float32 temp    = new Float32();
  private static Float32 xval    = new Float32();
  // angle in radians
  private static Float32 radians = new Float32();
  // power term (e.g x^3)
  private static Float32 power   = new Float32();
  // sine/cosine flag
  private static boolean sine;
  // quadrant that the angle is in
  private static int quadrant;

  //-------------------- abs --------------------------------------------------
  /**
   * Returns the absolute value of a <code>Float32</code> value.
   * Special cases:
   * <ul><li>If the argument is +0 or -0, the result is +0.
   * <li>If the argument is infinite, the result is +infinity.
   * <li>If the argument is NaN, the result is NaN.
   *</ul>
   * @param fnum <code>Float32</code> value
   * @return <code>Float32</code> reference to absolute value
   */
  public static Float32 abs(Float32 fnum) {
    result.set(fnum);
    result.high &= 0x7FFF;
    return(result);
  }
  //-------------------- min --------------------------------------------------
  /**
   * Returns the smaller of two <code>Float32</code> values. That is, the result
   * is the value closer to -infinity. If the arguments have the same value,
   * the result is that same value. Special cases:
   * <ul><li>If either value is NaN, then the result is NaN.
   * <li>This method considers -0 to be strictly smaller than +0.
   * If one argument is +0 and the other is -0, the result is -0.
   * </ul>
   * @param fnum1 <code>Float32</code> value
   * @param fnum2 <code>Float32</code> value
   * @return <code>Float32</code> reference to minimum value.
   */
  public static Float32 min(Float32 fnum1, Float32 fnum2) {
    // check for NaN or different signs (to catch signed zero case)
    if (fnum1.isNaN() || (fnum1.high < 0 && fnum2.high >= 0))
      result.set(fnum1);
    else if (fnum2.isNaN() || (fnum1.high >= 0 && fnum2.high < 0))
      result.set(fnum2);
    // compare the numbers and choose minimum
    else if (fnum1.compare(fnum2) < 0)
      result.set(fnum1);
    else
      result.set(fnum2);
    return(result);
  }

  //-------------------- max --------------------------------------------------
  /**
   * Returns the larger of two <code>Float32</code> values. That is, the result
   * is the value closer to +infinity. If the arguments have the same value,
   * the result is that same value.
   * Special cases:
   * <ul><li>If either value is NaN, then the result is NaN.
   * <li>This method considers -0 to be strictly smaller than +0.
   * If one argument is +0 and the other is -0, the result is +0.
   * </ul>
   * @param fnum1 <code>Float32</code> value
   * @param fnum2 <code>Float32</code> value
   * @return <code>Float32</code> reference to maximum value
   */
  public static Float32 max(Float32 fnum1, Float32 fnum2) {
    // check for NaN or different signs (to catch signed zero case)
    if (fnum1.isNaN() || (fnum1.high >= 0 && fnum2.high < 0))
      result.set(fnum1);
    else if (fnum2.isNaN() || (fnum1.high < 0 && fnum2.high >= 0))
      result.set(fnum2);
    // compare the numbers and choose maximum
    else if (fnum1.compare(fnum2) > 0)
      result.set(fnum1);
    else
      result.set(fnum2);
    return(result);
  }

  //-------------------- sin --------------------------------------------------
  /**
   * Returns the trigonometric sine of an angle.
   * Special cases:
   * <ul><li>If the argument is NaN or an infinity, then the result is NaN.
   * <li>If the argument is zero, then the result is a zero with the same
   * sign as the argument.
   * </ul>
   * @param angle angle in radians
   * @return <code>Float32</code> reference to sine of the angle
   */
  public static Float32 sin(Float32 angle) {
    // check for NaN, infinity or zero
    if (angle.isNaN() || angle.isInfinite()) {
      result.setToConstant(Float32.NaN);
      return(result);
    }
    else if (angle.isZero()) {
      result.set(angle);
      return(result);
    }

    // calculate the sine value and return
    reduceAngle(angle);
    sineCosine();
    if (quadrant == 3 || quadrant == 4) result.negate();
    return(result);
  } // end sin

  //-------------------- cos --------------------------------------------------
  /**
   * Returns the trigonometric cosine of an angle.
   * Special cases:
   * <ul><li>If the argument is NaN or an infinity, then the result is NaN.
   * </ul>
   * @param angle angle in radians
   * @return <code>Float32</code> reference to cosine of the angle
   */
  public static Float32 cos(Float32 angle) {
    // check for NaN, infinity or zero
    if (angle.isNaN() || angle.isInfinite()) {
      result.setToConstant(Float32.NaN);
      return(result);
    }
    else if (angle.isZero()) return(setResult(0));

    // calculate the cosine value and return
    reduceAngle(angle);
    sine = !sine;
    sineCosine();
    if (quadrant == 2 || quadrant == 3) result.negate();
    return(result);
  } // end cos

  //-------------------- tan --------------------------------------------------
  /**
   * Returns the trigonometric tangent of an angle.
   * Special cases:
   * <ul><li>If the argument is NaN or an infinity, then the result is NaN.
   * <li>If the argument is zero, then the result is a zero with the same
   * sign as the argument.
   * </ul>
   * @param angle angle in radians
   * @return <code>Float32</code> reference to tangent of the angle
   */
  public static Float32 tan(Float32 angle) {
    // check for NaN, infinity or zero
    if (angle.isNaN() || angle.isInfinite()) {
      result.setToConstant(Float32.NaN);
      return(result);
    }
    else if (angle.isZero()) {
      result.set(angle);
      return(result);
    }

    // calculate tan = sin / cos
    xval.set(cos(angle));
    sin(angle);
    result.divide(xval);
    return(result);
  } // end tan

  //-------------------- sqrt -------------------------------------------------
  /**
   * Returns the positive square root of a <code>Float32</code> value.
   * Special cases:
   * <ul><li>If the argument is NaN or less than zero, then the result is NaN.
   * <li>If the argument is +infinity, then the result is +infinity.
   * <li>If the argument is +0 or -0, then the result is the same as the argument.
   * </ul>
   * @param fnum <code>Float32</code> value
   * @return <code>Float32</code> reference to the square root of the value
   */
  public static Float32 sqrt(Float32 fnum) {
    // check for zero, infinity, NaN or negative number
    if (fnum.isZero() || fnum.isInfinite()) {
      result.set(fnum);
      return(result);
    }
    else if (fnum.high < 0 || fnum.isNaN()) {
      result.setToConstant(Float32.NaN);
      return(result);
    }

    // make an initial estimate of result
    Fpu32.loadA(fnum.high, fnum.low);
    Fpu32.expA = (Fpu32.expA + 1) / 2;
    Fpu32.storeA(result);

    // iterate to close in on an accurate answer
    for (int i = 0; i < 4; i++) {
      temp.set(fnum);
      temp.divide(result);
      temp.add(result);
      result.set(temp);
      result.divide(f2_0);
    }
    return(result);
  } // end sqrt

  //-------------------- pow --------------------------------------------------
  /**
   * Returns the value of a <code>Float32</code> raised to the power of an integer.
   * Special cases:
   * <ul><li>If the exponent is +0 or -0, then the result is 1.0.
   * <li>If the exponent is 1.0, then the result is the same as the base.
   * <li>If the base is NaN and the exponent is nonzero, then the result is NaN.
   * <li>If the base is +0 and the exponent is positive, or the base is
   * +infinity and the exponent is negative, then the result is +0.
   * <li>If the base is +0 and the exponent is negative, or the base is
   * +infinity and the exponent is positive, then the result is +infinity.
   * <li>If the base is -0 and the exponent is a positive even integer,
   * or the base is -infinity and the exponent is a negative even integer,
   * then the result is +0.
   * <li>If the base is -0 and the exponent is a positive odd integer,
   * or the base is -infinity and the exponent is a negative odd integer,
   * then the result is -0.
   * <li>If the base is -0 and the exponent is a negative even integer,
   * or the base is -infinity and the exponent is a positive even integer,
   * then the result is +infinity.
   * <li>If the base is -0 and the exponent is a negative odd integer,
   * or the base is -infinity and the exponent is a positive odd integer,
   * then the result is -infinity.
   * <li>If the base is negative and the exponent is an even integer, then the
   * result is equal to the result of raising the |base| to the power of the
   * exponent.
   * <li>If the base is negative and the exponent is an odd integer, then the
   * result is equal to the negative of the result of raising the |base| to
   * the power of the exponent.
   * </ul>
   * @param fnum <code>Float32</code> base
   * @param exp integer exponent
   * @return <code>Float32</code> reference to the resulting value
   */
  public static Float32 pow(Float32 fnum, int exp) {
    // check for exponent of zero or NaN
    result.set(fnum);
    if (exp == 0) return(setResult(0));
    else if (fnum.isNaN()) return(result);

    // check for base of zero or base of infinity
    else if (fnum.isZero() || fnum.isInfinite()) {
      if (fnum.high < 0) setResult(exp*2);
      else setResult(exp);
    }

    // set the power value and get the first result
    if (exp > 0)
      power.set(fnum);
    else {
      power.set(f1_0);
      power.divide(fnum);
      exp = -exp;
    }
    if ((exp & 1) == 0)
      result.set(f1_0);
    else
      result.set(power);

    // check for exponent of 1
    if (exp == 1) return(result);
    // set sign of result based on sign of base and odd or even exponent
    boolean sign = (fnum.high < 0) && ((exp & 1) != 0);

    // compute the base raised to the power of the exponent
    do {
      exp >>>= 1;
      power.multiply(power);
      if ((exp & 1) != 0) result.multiply(power);
    } while (exp != 0);

    // return the result
    return(result);
  } // end pow

  //-------------------- exp --------------------------------------------------
  /**
   * Returns Euler's number e raised to the power of an integer value.
   * @param exp integer exponent
   * @return <code>Float32</code> reference to the resulting value
   */
  public static Float32 exp(int exp) {
    temp.setToConstant(Float32.E);
    return(pow(temp, exp));
  }

  /**
   * Returns Euler's number e raised to the power of a Float32 value.
   * Special cases:
   * <li>If the exponent is NaN, the result is NaN.
   * <li>If the exponent is +infinity, then the result is +infinity.
   * If the exponent is -infinity, then the result is +0.
   * </ul>
   * <br>Note: this routine currently uses an iterative algorithm to find
   * convergence that can be quite time consuming. If the exponent is an
   * integer value the <code>exp(int)</code> method is much faster.
   * @param fnum <code>Float32</code> value
   * @return <code>Float32</code> reference to the value e raised to the power
   * <code>fnum</code> (where e is the base of the natural logarithms)
   */
  public static Float32 exp(Float32 fnum) {
    // check for NaN, infinity or zero
    result.set(fnum);
    if (fnum.isNaN()) return(result);
    else if (fnum.isZero()) return(setResult(0));
    else if (fnum.isInfinite()) return(setResult((fnum.high < 0) ? 2 : -2));

    // if negative exponent, make positive and invert result
    boolean sign = (fnum.high < 0);
    xval.set(fnum);
    if (sign) xval.negate();

    // check for limit of exponent (88.4186)
    if (xval.compare(eLimit) > 0) {
      if (sign)
        result.set(0, 0);
      else
        result.setToConstant(Float32.POSITIVE_INFINITY);
      return(result);
    }

    // e^x = 1 + x + x^2/2! + x^3/3! + x^4/4! ... x^9/9!
    // get the first two terms of the series
    result.set(f1_0);
    result.add(xval);
    power.set(xval);

    // get the next four terms
    for (int idx = 0; idx < 16; idx += 2) {
      power.multiply(xval);
      temp.set(power);
      temp.multiply(factorials[idx], factorials[idx+1]);
      result.add(temp);
    }

    // if no convergence, then iterate (+ x^n/n!)
    if (temp.compare(f0_001) > 0) {
      Float32 fact = new Float32(f9fact);
      for (int n = 10; n < 34; n++) {
        power.multiply(xval);
        if (power.isInfinite()) break;
        fact.multiply(n);
        temp.set(power);
        temp.divide(fact);
        result.add(temp);
        if (temp.compare(f0_001) < 0) break;
      } // end for
    } // end if

    // if negative exponent, invert result
    if (sign) {
      if (result.isInfinite())
        result.set(0, 0);
      else {
        temp.set(result);
        result.set(f1_0);
        result.divide(temp);
      }
    }
    return(result);
  } // end exp

  //-------------------- ceil -------------------------------------------------
  /**
   * Returns the smallest (closest to -infinity) <code>Float32</code> value that
   * is not less than the argument and is equal to a mathematical integer.
   * If the argument value is already equal to a mathematical integer, then the
   * result is the same as the argument.
   * Special cases:
   * <ul><li>If the argument is NaN, infinity or zero, then the result is
   * the same as the argument.
   * <li>If the argument value is less than zero but greater than -1.0,
   * then the result is -0.
   * </ul>
   * <br>Note: the value of <code>Float32Math.ceil(x) </code> is exactly the
   * value of <code>-Float32Math.floor(-x)</code>.
   * @param fnum <code>Float32</code> value
   * @return <code>Float32</code> reference to the result
   */
  public static Float32 ceil (Float32 fnum) {
    // check for NaN, infinity or zero
    result.set(fnum);
    if (fnum.isNaN() || fnum.isInfinite() || fnum.isZero()) return(result);

    // check for an integer value
    Fpu32.loadA(fnum.high, fnum.low);
    if (Fpu32.expA >= 23) return(result);
    Fpu32.convertAtoI();
    if (Fpu32.highA == 0 && Fpu32.lowA == 0) return(result);

    // if positive then round up to the next integer
    if (!Fpu32.signA) {
      Fpu32.lowI++;
      if (Fpu32.lowI == 0) Fpu32.highI++;
    }
    // convert resulting integer to floating point and return
    Fpu32.convertItoA();
    Fpu32.signA = (fnum.high < 0);
    Fpu32.storeA(result);
    return(result);
  } // end ceil

  //-------------------- floor ------------------------------------------------
  /**
   * Returns the largest (closest to +infinity) <code>Float32</code> value that
   * is not greater than the argument and is equal to a mathematical integer.
   * If the argument value is already equal to a mathematical integer, then the
   * result is the same as the argument.
   * Special case:
   * <ul><li>If the argument is NaN, infinity or zero, then the result is
   * the same as the argument.
   *</ul>
   * <br>Note: the value of <code>Float32Math.ceil(x) </code> is exactly the
   * value of <code>-Float32Math.floor(-x)</code>.

   * </ul>
   * @param fnum <code>Float32</code> value
   * @return <code>Float32</code> reference to the result
   */
  public static Float32 floor (Float32 fnum) {
    // check for NaN, infinity or zero
    result.set(fnum);
    if (fnum.isNaN() || fnum.isInfinite() || fnum.isZero()) return(result);

    // check for an integer value
    Fpu32.loadA(fnum.high, fnum.low);
    if (Fpu32.expA >= 23) return(result);
    Fpu32.convertAtoI();
    if (Fpu32.highA == 0 && Fpu32.lowA == 0) return(result);

    // if negative then round down to the next integer
    if (Fpu32.signA) {
      Fpu32.lowI++;
      if (Fpu32.lowI == 0) Fpu32.highI++;
    }
    // convert resulting integer to floating point and return
    Fpu32.convertItoA();
    Fpu32.signA = (fnum.high < 0);
    Fpu32.storeA(result);
    return(result);
  } // end floor

  //-------------------- round ------------------------------------------------
  /**
   * Returns the closest int to the <code>Float32</code>argument. The result is
   * rounded to an integer by adding 1/2, taking the floor of the result, and
   * converting to an integer.
   * Special cases:
   * <ul><li>If the argument is NaN, the result is 0.
   * <li>If the argument is -infinity or any value less than or equal to the
   * value of <code>Integer.MIN_VALUE</code>, the result is equal to the value
   * of <code>Integer.MIN_VALUE</code>.
   * <li>If the argument is +infinity or any value greater than or equal to the
   * value of <code>Integer.MAX_VALUE</code>, the result is equal to the value
   * of <code>Integer.MAX_VALUE</code>.
   * </ul>
   * @param fnum <code>Float32</code> value
   * @return the nearest integer to the <code>Float32</code> value
   */
  public static int round (Float32 fnum) {
    // check for NaN, zero or infinity
    if (fnum.isNaN() || fnum.isZero()) return(0);
    else if (fnum.isInfinite())
      return((fnum.high < 0) ? (short)Integer.MIN_VALUE : Integer.MAX_VALUE);

    // add 0.5 and get the floor
    temp.set(fnum);
    temp.add(f0_5);
    floor(temp);

    // convert to integer
    Fpu32.loadA(result.high, result.low);
    Fpu32.convertAtoI();

    // return the result (truncated to min/max values)
    if (Fpu32.highI != 0 || (fnum.high >= 0 && Fpu32.lowI < 0))
      return((fnum.high < 0) ? (short)Integer.MIN_VALUE : Integer.MAX_VALUE);
    else return((fnum.high < 0) ? -Fpu32.lowI : Fpu32.lowI);
  } // end round

  //-------------------- toDegrees --------------------------------------------
  /**
   * Converts an angle measured in radians to an approximately equivalent
   * angle measured in degrees.
   * The conversion from degrees to radians is generally inexact.
   * @param radians angle in radians
   * @return <code>Float32</code> reference to angle in degrees
   */
  public static Float32 toDegrees(Float32 radians) {
    result.set(radians);
    result.divide(piOver180);
    return(result);
  } // end toDegrees

  //-------------------- toRadians --------------------------------------------
  /**
   * Converts an angle measured in degrees to an approximately equivalent
   * angle measured in radians.
   * The conversion from radians to degrees is generally inexact; users should
   * not expect cos(toRadians(90.0)) to exactly equal 0.0.
   * @param radians angle in degrees
   * @return <code>Float32</code> reference to angle in radians
   *
   */
  public static Float32 toRadians(Float32 degrees) {
    result.set(degrees);
    result.multiply(piOver180);
    return(result);
  } // end toRadians

  //===========================================================================
  // Private methods
  //===========================================================================

  //-------------------- reduceAngle ------------------------------------------
  /*
   * Reduce the angle to a .
   */
  private static void reduceAngle(Float32 angle) {
    // get the angle in degrees
    temp.set(toDegrees(angle));

    // make the angle positive
    boolean sign = (angle.high < 0);
    if (sign) temp.negate();

    // make sure angle is between 0 and 360 degrees
    if (temp.compare(f360_0) >= 0) {
      Fpu32.loadA(temp.high, temp.low);
      Fpu32.divideAB(f360_0.high, f360_0.low);
      Fpu32.convertAtoI();
      Fpu32.multiplyAB(f360_0.high, f360_0.low);
      Fpu32.storeA(temp);
    }
    // adjust for negative angle
    if (sign) {
      temp.negate();
      temp.add(f360_0);
    }
    // determine the quadrant and type of calculation to use
    sine = true;
    quadrant = 1;
    while (temp.compare(f90_0) >= 0) {
      temp.subtract(f90_0);
      quadrant++;
    }
    if (quadrant == 2 || quadrant == 4) {
      temp.negate();
      temp.add(f90_0);
    }
    if (temp.compare(f45_0) >= 0) {
      temp.negate();
      temp.add(f90_0);
      sine = false;
    }
    // convert the resulting angle to radians
    radians.set(toRadians(temp));
  } // end reduceAngle

  //-------------------- sineCosine -------------------------------------------
  /*
   * Calculate sine or cosine using partial Maclaurin series.
   * sin x = x - x^3/3! + x^5/5! - x^7/7! + x^9/9!
   * cos x = 1 - x^2/2! + x^4/4! - x^6/6! + x^8/8!
   */
  private static void sineCosine() {

    // get the first term of series and set the power term
    if (sine)
      power.set(radians);
    else
      power.set(f1_0);
    result.set(power);

    // compute the next four terms of the series
    for (int idx = (sine) ? 2 : 0; idx < 16; idx += 4) {
      power.multiply(radians);
      power.multiply(radians);
      power.negate();
      temp.set(power);
      temp.multiply(factorials[idx], factorials[idx+1]);
      result.add(temp);
    }
  } // end sineCosine

  //-------------------- setResult --------------------------------------------
  /*
   * Sets the appropriate special return value
   * result is 1.0 if n = 0
   * result is zero if n > 0, or infinity if n < 0
   * result is positive if n is even, or negative if n is odd
   */
  private static Float32 setResult(int n) {
    if (n == 0)
      result.set(f1_0);
    else if (n > 0)
      result.set(0, 0);
    else
      result.setToConstant(Float32.POSITIVE_INFINITY);
    if ((n & 1) != 0) result.negate();
    return(result);
  } // end setResult
} // end class