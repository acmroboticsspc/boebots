/*
 * Copyright © Micromega Corporation 2002-2003. All rights reserved
 */
package stamp.math;
import  stamp.core.*;
/**
 * This class provides support for single precision floating point numbers.
 * The floating point numbers conform to IEEE standard 754 which is the
 * representation used by standard Java.  The range of values provided is
 * plus or minus ~10e-44.85 to ~10e38.53.  The special values Not-a-Number,
 * positive infinity and negative infinity are supported.
 *
 * The floating point values are stored in two 16-bit integers in the
 * following format (the exponent has a bias of 127):
 * <pre>
 *               high                              low
 * |=|===============|=============| |===============================|
 * |S|<---Exponent-->|<------------| |-----Mantissa----------------->|
 * |=|===============|=============| |===============================|
 *  1          8            7                        16              #bits
 *
 * e.g.   1.0 = 0x3F80, 0x0000
 *       -1.0 = 0xBF80, 0x0000
 *       10.0 = 0x4120, 0x0000
 *       0.01 = 0x3C23, 0xD70A
 * </pre>Additional information regarding floating point numbers can be
 * obtained by reviewing the IEEE 754 standard or standard Java documentation.
 *
 * @author Cam Thompson, Micromega Corporation
 * @version V1.3 - May 2, 2003
 * changes:
 *    V1.3 - May 2, 2003
 *      - removed new(String) constructor, added isZero method
 *    V1.2 - April 27, 2003
 *      - modified toString, set(String) and intValue methods
 *      - added e to constants and modified setToConstant
 *    V1.1 - April 8, 2003
 *      - moved low level floating point operations to a separate class
 *        (otherwise the maximum number of classes was exceeded )
 *    V1.0 - March 27, 2003
 *      - original version
 */

public class Float32 {
  /**
   * high 16-bits of floating point number.
   * <pre>
   * |=|===============|=============|
   * |S|<---Exponent-->|<--Mantissa--|
   * |=|===============|=============|
   *  1          8            7      #bits
   * </pre>
   */
  public int high;
  /**
   * low 16-bits of floating point number.
   * <pre>
   * |===============================|
   * |-----------Mantissa----------->|
   * |===============================|
   *                 16              #bits
   * </pre>
   */
  public int low;

  // definitions for constant values used internally and by setTo method
  private static final int MIN_CONSTANT = 100;
  /**
   * An identifier for the positive infinity constant. Positive infinity is
   * equal to the 32-bit value 0x7F80000, and is obtained using the
   * <code>setToConstant(POSITIVE_INFINITY)</code> method.
   */
  public static final char POSITIVE_INFINITY = 100;
  /**
   * An identifier for the negative infinity constant. Negative infinity is
   * is equal to the 32-bit value 0xFF80000, and is obtained using the
   * <code>setToConstant(NEGATIVE_INFINITY)</code> method.
   */
  public static final char NEGATIVE_INFINITY = 101;
  /**
   * An identifier for the Not-a-Number constant (NaN). NaN is equal to the
   * 32-bit value 0x7FC0000, and is obtained using the
   * <code>setToConstant(NaN)</code> method.
   */
  public static final char NaN = 102;
  /**
   * An identifier for the maximum value constant. The maximum value is equal
   * to the 32-bit value 0x7F7FFFFF, and is obtained using the
   * <code>setToConstant(MAX_VALUE)</code> method.
   */
  public static final char MAX_VALUE = 103;
  /**
   * An identifier for the minimum value constant. The minimum value is equal
   * to the 32-bit value 0x00000001, and is obtained using the
   * <code>setToConstant(MIN_VALUE)</code> method.
   */
  public static final char MIN_VALUE = 104;
  /**
   * An identifier for the constant Pi. The value of Pi is represented to the
   * maximum resolution of a 32-bit floating number and is equal to the value
   * 3.1415927 (0x40490FDB). It is obtained using the
   * <code>setToConstant(PI)</code> method.
   */
  public static final char PI = 105;
  /**
   * An identifier for the constant e. The value of e is represented to the
   * maximum resolution of a 32-bit floating number and is equal to the value
   * 2.7182817 (0x402DF854). It is obtained using the
   * <code>setToConstant(E)</code> method.
   */
  public static final char E = 106;
  private static final int MAX_CONSTANT = 106;

  // floating point sign bit
  private static final int MSB = 0x80;
  // sign bit
  private static final int SIGN_BIT = (short)0x8000;
  // internal carry bit
  private static final int CARRY_BIT = 0x100;

  // string buffer
  private static StringBuffer sbuf = new StringBuffer(20);
  // special constants (+infinity, -infinity, NaN, Max, Min, Pi)
  private final static int NAN_HIGH = 0x7FC0;
  private final static int PINF_HIGH = 0x7F80;
  private final static int NINF_HIGH = (short)0xFF80;
  private final static int constants[] = {
    PINF_HIGH, 0,
    NINF_HIGH, 0,
    NAN_HIGH, 0,
    (short)0x7F7F, (short)0xFFFF,
    (short)0x0000, (short)0x0001,
    (short)0x4049, (short)0x0FDB,
    (short)0x402D, (short)0xF854,
  };

  private static final int intPower10[]  = { // integer powers of ten
    0, 1,                       // 1
    0, 10,                      // 10
    0, 100,                     // 100
    0, 1000,                    // 1,000
    0, 10000,                   // 10,000
    0x0001, (short)0x86A0,      // 100,000
    0x000F, (short)0x4240,      // 1,000,000
    0x0098, (short)0x9680,      // 10,000,000
  };

  private static final int power10[] = {     // floating point powers of 10
    0x3F80, 0x0000,             // 1.0
    0x4120, 0x0000,             // 10.0
    0x42C8, 0x0000,             // 100.0
    0x447A, 0x0000,             // 1,000.0
    0x461C, 0x4000,             // 10,000.0
    0x47C3, 0x5000,             // 100,000.0
    0x4974, 0x2400,             // 1,000,000.0
    0x4B18, (short) 0x9680,     // 10,000,000.0
    0x4CBE, (short) 0xBC20      // 100,000,000.0
  };

  // temporary value used with integer or string arguments
  private static Float32 temp = new Float32();
  //
  //-------------------- constructors -----------------------------------------
  /**
   * Create a new floating point number initialized to zero.
   */
  public Float32() {
    high = 0;
    low = 0;
  }
  /**
   * Create a new floating point number and set to the value of another
   * floating point number.
   */
  public Float32(Float32 fnum) {
    set(fnum);
  }
  /**
   * Create a new floating point number and set to the value of an integer.
   */
  public Float32(int num) {
    set(num);
  }
  /**
   * Create a new floating point number and set to the binary floating point
   * value (internal representation contained in two 16-bit integers).
   * @param high high 16-bits of binary floating point value
   * @param low low 16-bits of binary floating point value
   */
  public Float32 (int high, int low) {
    set(high, low);
  }

  //-------------------- set --------------------------------------------------
  /**
   * Set the floating point value to the value of another floating point number.
   * @param fnum floating point number
   * @return Float32 reference to floating point number
   */
  public Float32 set(Float32 fnum) {
    return(set(fnum.high, fnum.low));
  }
  /**
   * Set the floating point value to the binary floating point value
   * (internal representation contained in two 16-bit integers).
   * @param high high 16-bits of binary floating point value
   * @param low low 16-bits of binary floating point value
   * @return Float32 reference to floating point number
   */
  public Float32 set(int high, int low) {
    this.high = high;
    this.low = low;
    return(this);
  }
  /**
   * Set the floating point value to the value of an integer.
   * @param num integer value
   * @return Float32 reference to floating point number
   */
  public Float32 set(int num) {
    Fpu32.intToA(num);
    Fpu32.storeA(this);
    return(this);
  }
  /**
   * Set the floating point value to the value of the converted string.
   * @param s String containing floating point number
   * @return Float32 reference to floating point number
   */
  public Float32 set(StringBuffer sb) {
    return(set(sb.toString()));
  }
  /**
   * Set the floating point value to the value of the converted string.
   * @param s String containing floating point number
   * @return Float32 reference to floating point number
   */
  public Float32 set(String s) {
    // get the length of the string and check for empty string
    int len = s.length();
    if (len == 0) return(set(0));

    // convert the string to a character array
    char sa[] = s.toCharArray();
    char ch = sa[0];
    int i = 1;

    // skip whitespace
    while (ch == ' ' || ch == '\t' || ch == '\n') {
      if (i < len) ch = sa[i++];
    }
    // clear value and check for sign
    boolean sign = false;
    if (ch == '+' || ch == '-') {
      if (ch == '-') sign = true;
      if (i < len) ch = sa[i++];
    }

    // load scaled integer value of significant digits into I register
    Fpu32.highI = 0;
    Fpu32.lowI = 0;
    int scale = 0;
    boolean dp = false;
    boolean maxDigits = false;

    // get floating point digits
    while ((ch >= '0' && ch <= '9') || ch == '.') {
      // check for decimal point
      if (ch == '.') {
        if (dp) break;
        dp = true;
      }
      // check for maximum digits (scale up integers, ignore fractions)
      else {
        if (maxDigits) {
          if (!dp) scale++;
        }
        // build the number and check if maximum precision  exceeded
        else {
          Fpu32.multiplyI10();
          Fpu32.addI(0, ch - '0');
          if ((Fpu32.highI & (short)0xFF00) != 0) maxDigits = true;
          if (dp) scale--;
        }
      } // end if

      if (i >= len) break;
      ch = sa[i++];
    } // end while

    // check for exponent
    boolean esign = false;
    int exp = 0;
    if (ch == 'e' || ch == 'E') {
      if (i < len) ch = sa[i++];
      if (ch == '+' || ch == '-') {
        if (ch == '-') esign = true;
        if (i < len) ch = sa[i++];
      }
      exp = 0;
      while (ch >= '0' && ch <= '9') {
        exp = (exp * 10) + (ch - '0');
        if (i >= len || exp > 99) break;
        ch = sa[i++];
      } // end while
    } // end if
    if (esign) exp = -exp;
    exp += scale;

    // if exponent < -45 set to zero
    if (exp < -45) return(setZero((sign)? -1 : 1));

    // if exponent > 38 set to infinity
    if (exp > 38) return(setInfinity((sign)? -1 : 1));

    // convert to floating point and scale by decimal exponent
    Fpu32.convertItoA();
    if (exp > 0) {
      while (exp > 7) {
        Fpu32.multiplyAB(power10[14], power10[15]);
        exp -= 7;
      }
      if (exp > 0) Fpu32.multiplyAB(power10[exp*2], power10[exp*2+1]);
    } else if (exp < 0) {
      while (exp < -7) {
        Fpu32.divideAB(power10[14], power10[15]);
        exp += 7;
      }
      if (exp < 0) Fpu32.divideAB(power10[-exp*2], power10[-exp*2+1]);
    }

    // store the converted value
    Fpu32.signA = sign;
    Fpu32.storeA(this);
    return(this);
  } // end set

  //-------------------- setToConstant ----------------------------------------
  /**
   * Sets the floating point number to one of the following constants:
   * <code>POSITIVE_INFINITY, NEGATIVE_INFINITY, NaN, MAX_VALUE, MIN_VALUE,
   * PI </code>.
   * A public field is defined for each constant identifier (see the field
   * descriptions for more detail).  If the identifier is negative, the
   * constant value will be negative. If the identifier is no valid, the value
   * is set to NaN.
   * @param id contant identifier
   * @return Float32 reference to result
   */
  public Float32 setToConstant(int id) {
    boolean sign = (id < 0);
    if (sign) id = -id;
    if (id < MIN_CONSTANT || id > MAX_CONSTANT) id = NaN;
    id  = (id - MIN_CONSTANT) * 2;
    set(constants[id], constants[id+1]);
    if (sign) negate();
    return(this);
  } //end setTo

  //-------------------- add --------------------------------------------------
  /**
   * Add a floating point number to this floating point number.
   * @param fnum floating point number
   * @return Float32 reference to result
   */
  public Float32 add(Float32 fnum) {
    return(add(fnum.high, fnum.low));
  }
  /**
   * Add an integer value to this floating point number.
   * @param num integer value
   * @return Float32 reference to floating point number
   */
  public Float32 add(int num) {
    return(add(temp.set(num)));
  }

  /**
   * Add the value of the converted string to this floating point number.
   * @param s string containing floating point number
   * @return Float32 reference to floating point number
   */

  public Float32 add(String s) {
    return(add(temp.set(s)));
  }
  /**
   * Add the binary floating point value (internal representation contained
   * in two 16-bit integers) to this floating point number.
   * @param high high 16-bits of binary floating point value
   * @param low low 16-bits of binary floating point value
   * @return Float32 reference to floating point number
   */
  public Float32 add(int high, int low) {
    // check for Not-a-Number
    if (nan(high, low)) return(this);

    // check for add by zero
    if ((high & 0x7FFF) == 0 && low == 0) {
      // check for -0.0 + 0.0 case
      if (this.high == (short)0x8000 && this.low == 0) this.high = high;
      return(this);
    }

    // check for add to zero
    if ((this.high & 0x7FFF) == 0 && this.low == 0) {
      this.high = high;
      this.low = low;
      return(this);
    }
    // check for infinity
    int sign = this.high ^ high;
    boolean infin1 = infinity(this.high, this.low);
    boolean infin2 = infinity(high, low);
    if (infin1) {
      if (infin2) {
        if (sign < 0) return(setNaN());
        else return(setInfinity(high));
      }
      else return(this);
    }
    else if (infin2) return(set(high, low));

    // get working copies of the numbers to add
    Fpu32.loadA(this.high, this.low);
    Fpu32.loadB(high, low);

    // ensure that A is >= B
    if (Fpu32.expA < Fpu32.expB) {
      Fpu32.loadA(high, low);
      Fpu32.loadB(this.high, this.low);
    }
    // align the numbers, skip add if one is much larger
    int cnt = Fpu32.expA - Fpu32.expB;
    if (cnt > 0) {
      if (cnt > 23) {
        Fpu32.storeA(this);
        return(this);
      }
      Fpu32.shiftB(-cnt);
    }

    // if signs are opposite, negate B
    if (Fpu32.signA ^ Fpu32.signB) Fpu32.negateB();

    // add the floating point values
    Fpu32.addAB();

    // check if signs are opposite
    if (Fpu32.signA ^ Fpu32.signB) {
      // if no carry, then negate result
      if ((Fpu32.highA & CARRY_BIT) == 0) {
        Fpu32.negateA();
        Fpu32.signA = !Fpu32.signA;
        Fpu32.tailA = 0;
      }
      Fpu32.highA &= 0xFF;  // clear carry bit
    }
    // normalize and store the result
    Fpu32.normalizeA();
    Fpu32.storeA(this);
    return(this);
  } // end add

  //-------------------- subtract ---------------------------------------------
  /**
   * Subtract a floating point number from this floating point number.
   * @param fnum floating point number
   * @return Float32 reference to result
   */
  public Float32 subtract(Float32 fnum) {
    return(subtract(fnum.high, fnum.low));
  }
  /**
   * Add an integer value to this floating point number.
   * @param num integer value
   * @return Float32 reference to floating point number
   */
  public Float32 subtract(int num) {
    return(subtract(temp.set(num)));
  }
  /**
   * Subtract the value of the converted string from this floating point number.
   * @param s string containing floating point number
   * @return Float32 reference to floating point number
   */
  public Float32 subtract(String s) {
    return(subtract(temp.set(s)));
  }
  /**
   * Subtract the binary floating point value (internal representation contained
   * in two 16-bit integers) from this floating point number.
   * @param high high 16-bits of binary floating point value
   * @param low low 16-bits of binary floating point value
   * @return Float32 reference to floating point number
   */
  public Float32 subtract(int high, int low) {
    // check for Not-a-Number
    if (nan(high, low)) return(this);

    // reverse sign and use add method
    return(add((high ^ SIGN_BIT), low));
  } // end subtract

  //-------------------- multiply ---------------------------------------------
  /**
   * Multiply this floating point number by another floating point number.
   * @param fnum floating point number
   * @return Float32 reference to result
   */
  public Float32 multiply(Float32 fnum) {
    return(multiply(fnum.high, fnum.low));
  }
  /**
   * Multiply this floating point number by an integer value.
   * @param num integer value
   * @return Float32 reference to floating point number
   */
  public Float32 multiply(int num) {
    return(multiply(temp.set(num)));
  }
  /**
   * Multiply this floating point number by the value of the converted string.
   * @param s string containing floating point number
   * @return Float32 reference to floating point number
   */
  public Float32 multiply(String s) {
    return(multiply(temp.set(s)));
  }
  /**
   * Multiply this floating point number by a binary floating point value
   * (internal representation contained in two 16-bit integers).
   * @param high high 16-bits of binary floating point value
   * @param low low 16-bits of binary floating point value
   * @return Float32 reference to floating point number
   */
  public Float32 multiply(int high, int low) {
    // check for Not-a-Number
    if (nan(high, low)) return(this);

    // check for multiply by zero and infinity
    int sign = this.high ^ high;
    boolean infin1 = infinity(this.high, this.low);
    boolean infin2 = infinity(high, low);
    if (((this.high & 0x7FFF) == 0 && this.low == 0) ||
        ((high & 0x7FFF) == 0 && low == 0)) {
      if (infin1 || infin2) return(setNaN());
      else return(setZero(sign));
    }
    else if (infin1 || infin2) return(setInfinity(sign));

    // multiply the numbers
    Fpu32.loadA(this.high, this.low);
    Fpu32.multiplyAB(high, low);
    Fpu32.storeA(this);
    return (this);
  } // end multiply

  //-------------------- divide -----------------------------------------------
  /**
   * Divide this floating point number by another floating point number.
   * @param fnum floating point number
   * @return Float32 reference to result
   */
  public Float32 divide(Float32 fnum) {
    return(divide(fnum.high, fnum.low));
  }
  /**
   * Divide this floating point number by an integer value.
   * @param num integer value
   * @return Float32 reference to floating point number
   */
  public Float32 divide(int num) {
    return(divide(temp.set(num)));
  }
  /**
   * Divide this floating point number by the value of the converted string.
   * @param s string containing floating point number
   * @return Float32 reference to floating point number
   */
  public Float32 divide(String s) {
    return(divide(temp.set(s)));
  }
  /**
   * Divide the floating point number by a binary floating point value
   * (internal representation contained in two 16-bit integers).
   * @param high high 16-bits of binary floating point value
   * @param low low 16-bits of binary floating point value
   * @return Float32 reference to floating point number
   */
  public Float32 divide(int high, int low) {
    // check for Not-a-Number
    if (nan(high, low)) return(this);

    // check for divisor = 0
    int sign = this.high ^ high;
    if ((high & 0x7FFF) == 0 && low == 0) {
      if ((this.high & 0x7FFF) == 0 && this.low == 0) return(setNaN());
      else return(setInfinity(sign));
    }
    // check for dividend = 0
    if ((this.high & 0x7FFF) == 0 && this.low == 0) return(setZero(sign));

    // check for infinity
    if (infinity(high, low)) {
      if (infinity(this.high, this.low)) return(setNaN());
      else return(setZero(sign));
    }
    else if (infinity(this.high, this.low)) return(setInfinity(sign));

    // divide the numbers
    Fpu32.loadA(this.high, this.low);
    Fpu32.divideAB(high, low);
    Fpu32.storeA(this);
    return (this);
  } // end divide

  //-------------------- negate -----------------------------------------------
  /**
   * Negate the floating point number.
   * @return Float32 reference to result
   */
  public Float32 negate() {
    // check for Not-a-Number
    if (nan(high, low)) return(this);

    // change the sign bit
    high ^= SIGN_BIT;
    return(this);
  } // end negate

  //-------------------- compare ----------------------------------------------
  /**
   * Compare this floating point number with another floating point number.
   * @param fnum floating point number
   * @return -1 if the floating point number is less than value passed
   * <br>&nbsp 0 if the floating point number is equal to the value passed
   * <br>&nbsp 1 if the floating point number is greater than the value passed
   */
  public int compare(Float32 fnum) {
    return(compare(fnum.high, fnum.low));
  }
  /**
   * Compare this floating point number with an integer value.
   * @param num integer value
   * @return -1 if the floating point number is less than value passed
   * <br>&nbsp 0 if the floating point number is equal to the value passed
   * <br>&nbsp 1 if the floating point number is greater than the value passed
   */
  public int compare(int num) {
    return(compare(temp.set(num)));
  }
  /**
   * Compare this floating point number with the value of the converted string.
   * @param s string containing floating point number
   * @return Float32 reference to floating point number
   */
  public int compare(String s) {
    return(compare(temp.set(s)));
  }
  /**
   * Compare this floating point number with a binary floating point value
   * (internal representation contained in two 16-bit integers).
   * @param high high 16-bits of binary floating point value
   * @param high low 16-bits of binary floating point value
   * @return -1 if the floating point number is less than value passed
   * <br>&nbsp 0 if the floating point number is equal to the value passed
   * <br>&nbsp 1 if the floating point number is greater than the value passed
   */
  public int compare(int high, int low) {
    // check for zero comparison
    if ((this.high & 0x7FFF) == 0 & this.low == 0 &
        (high & 0x7FFF) == 0 & low == 0) return(0);

    // compare the numbers
    Fpu32.loadA(this.high, this.low);
    return(Fpu32.compareAB(high, low));
  } // end compare

  //-------------------- equals ----------------------------------------------
  /**
   * Checks if this floating point number is equal to another floating point number.
   * @param fnum floating point number
   * @return <code>true</code> if equal
   */
  public boolean equals(Float32 fnum) {
    if (fnum == null) return(false);
    if (high == fnum.high && low == fnum.low)
      return(true);
    else
      return(false);
  }

  //-------------------- isInfinite -------------------------------------------
  /**
   * Checks if the floating point number is infinite.
   * @return <code>true</code> if floating point number is positive
   * or negative infinity;
   *<br><code>false</code> otherwise.
   */
  public boolean isInfinite() {
    return(infinity(high, low));
  } // end isInfinite

  //-------------------- isNaN ------------------------------------------------
  /**
   * Checks if the floating point number is Not-A-Number (NaN).
   * @return <code>true</code> if floating point number is NaN;
   *<br><code>false</code> otherwise.
   */
  public boolean isNaN() {
    if (high == NAN_HIGH && low == 0) return(true);
    else return (false);
  } // end isNaN

  //-------------------- isZero -----------------------------------------------
  /**
   * Checks if the floating point number is positive or negative zero.
   * @return <code>true</code> if floating point number is +0 or -0;
   *<br><code>false</code> otherwise.
   */
  public boolean isZero() {
    if ((high & 0x7FFF) ==0 && low == 0) return(true);
    else return (false);
  } // end isZero

  //-------------------- intValue ------------------------------------------------
  /**
   * Convert a floating point number to an integer.
   * @return integer value of floating point number
   */

  public int intValue() {
    // check for Not-a-Number
    if (isNaN()) return (0);

    // get integer value of floating point number
    Fpu32.loadA(high, low);
    Fpu32.convertAtoI();

    // check for overflow and return the signed integer value
    if (Fpu32.highI != 0 || Fpu32.lowI < 0)
      return((high < 0) ? -32768 : 32767);
    else
      return((high < 0) ? -Fpu32.lowI : Fpu32.lowI);
  } // end intValue


  //-------------------- toString ---------------------------------------------
  /**
   * Convert a floating point number to a string.
   * @return text representation of floating point number
   */
  public String toString() {
    sbuf.clear();

    // check for Not-a-Number
    if (isNaN()) {
      sbuf.append("NaN");
      return (sbuf.toString());
    }
    // load value and get the sign
    Fpu32.loadA(high, low);
    if (Fpu32.signA) {
      sbuf.append('-');
      Fpu32.signA = false;
    }
    // check for zero value or infinity values
    if ((high & 0x7FFF) == 0 && low == 0) {
      sbuf.append("0.0");
      return (sbuf.toString());
    }
    else if (infinity(high, low)) {
      sbuf.append("Infinity");
      return (sbuf.toString());
    }
    // prescale number: 1 <= number < 100,000,000
    int idx = 16;
    int exp = 7;
    while (Fpu32.compareAB(power10[idx], power10[idx+1]) >= 0) {
      Fpu32.divideAB(power10[idx-2], power10[idx-1]);
      exp += 7;
    }
    while (Fpu32.compareAB(power10[0], power10[1]) < 0) {
      Fpu32.multiplyAB(power10[idx-2], power10[idx-1]);
      exp -= 7;
    }
    // scale number to include eight significant digits
    while (idx > 0) {
      idx -= 2;
      if (Fpu32.compareAB(power10[idx], power10[idx+1]) >= 0) break;
    }
    if (idx < 14) {
      idx = 14 - idx;
      exp -= (idx / 2);
      Fpu32.multiplyAB(power10[idx], power10[idx+1]);
    }
    // check for exponential format
    Fpu32.convertAtoI();
    boolean eFormat = (exp < -3 || exp > 7);
    boolean trailingZero = false;
    char digit;

    // if not exponential format, check for number less than zero
    if (!eFormat & exp < 0) {
      sbuf.append("0.");
      if (exp < -1) sbuf.append('0');
      if (exp < -2) sbuf.append('0');
    }

    // convert the scaled integer
    int decimalPoint = (eFormat) ? 1 : (exp + 1);
    for (idx = 14; idx >= 0; idx -= 2) {
      Fpu32.divideI(intPower10[idx], intPower10[idx+1]);
      digit = (char)('0' + Fpu32.lowI);
      Fpu32.highI = Fpu32.remHigh;
      Fpu32.lowI = Fpu32.remLow;
      sbuf.append(digit);
      if (--decimalPoint == 0) sbuf.append('.');
      if (decimalPoint < 0 && Fpu32.highI == 0 && Fpu32.lowI == 0) break;
    } // end for
    // make sure there is a trailing zero
    if (decimalPoint == 0) sbuf.append('0');

    // if exponential format, then convert the decimal exponent
    if (eFormat) {
      sbuf.append('E');
      if (exp < 0) {
        sbuf.append('-');
        exp = -exp;
      }
      digit = (char)('0' + (exp / 10));
      if (digit != '0') sbuf.append(digit);
      sbuf.append((char)('0' + (exp % 10)));
    }

    return (sbuf.toString());
  } // end toString

  //-------------------- toHexString ------------------------------------------
  /**
   * Convert a floating point number to a hexadecimal string.
   * @return text representation of floating point number
  */
  public String toHexString() {
    sbuf.clear();
    sbuf.append("0x");
    sbuf.append(toHex(high >> 12));
    sbuf.append(toHex(high >> 8));
    sbuf.append(toHex(high >> 4));
    sbuf.append(toHex(high));
    sbuf.append(toHex(low >> 12));
    sbuf.append(toHex(low >> 8));
    sbuf.append(toHex(low >> 4));
    sbuf.append(toHex(low));
    return (sbuf.toString());
  } // end toHexString

  //===========================================================================
  // Private methods
  //===========================================================================

  //-------------------- infinity ---------------------------------------------
  // return true if value passed is infinity
  private boolean infinity(int high, int low) {
    if ((high == NINF_HIGH && low == 0) ||
        (high == PINF_HIGH && low == 0)) return(true);
    else return(false);
  } // end infinity

  //-------------------- nan --------------------------------------------------
  // @return true if parameter value or object value is NaN
  // (if true, also sets object value to NaN)
  private boolean nan(int high, int low) {
    if (this.high == NAN_HIGH && this.low == 0)
      return(true);
    else if (high == NAN_HIGH && low == 0) {
      this.high = high;
      this.low = low;
      return(true);
    }
    else return(false);
  } // end nan

  //-------------------- setInfinity ------------------------------------------
  // sets value to signed infinity and returns
  private Float32 setInfinity(int n) {
    high = (n < 0) ? NINF_HIGH : PINF_HIGH;
    low = 0;
    return(this);
  } // end setInfinity

  //-------------------- setNaN -----------------------------------------------
  // sets value to Not-a-Number (NaN and returns
  private Float32 setNaN() {
    high = NAN_HIGH;
    low = 0;
    return(this);
  } // end setNaN

  //-------------------- setZero ----------------------------------------------
  // sets value to signed zero
  private Float32 setZero(int n) {
    high = (n < 0) ? SIGN_BIT : 0;
    low = 0;
    return(this);
  } // end setZero

  //-------------------- toHex ------------------------------------------------

  private char toHex(int n) {
    char hexDigit;
    hexDigit = (char) ('0' + (n & 0x0F));
    if (hexDigit > '9') hexDigit += 7;
    return(hexDigit);
  } // end toHex
} // end class