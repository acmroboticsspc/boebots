/*
 * Copyright © 2002 Parallax Inc., all rights reserved.
 */

package stamp.math;

/** The FloatLite6 library can perform addition and subtraction of fixed decimal
  * point numbers up to 6 decimal places.  Comparison and equality methods are
  * included.<p>
  * FloatLite6 objects support values from -32768.999999 to 32767.999999.
  *
  * @author Gray Cole
  * @version 1.0 March 20, 2003
  */
public class FloatLite6
  {


  // CONSTRUCTORS
  /** Constructor when provided the value as a string.
    *
    * @param num the string representation of the fixed point number
    */
  public FloatLite6(String num)
    {
    sbtemp.clear();
    sbtemp.append(num);
    buildFLObject(sbtemp);
    }


  /** Constructor when provided the value in a StringBuffer object.
    *
    * @param num the StringBuffer value of the fixed point number
    */
  public FloatLite6(StringBuffer num)
    {
    sbtemp.clear();
    sbtemp.append(num.toString());
    buildFLObject(sbtemp);
    }


  /** Constructor when provided the value in a char array.
    *
    * @param num the char array value of the fixed point number
    */
  public FloatLite6(char[] num)
    {
    sbtemp.clear();
    int len = num.length;
    for (int i = 0; i <  len; i++)
      sbtemp.append(num[i]);
    buildFLObject(sbtemp);
    }


  /** Constructor when provided the value of another FloatLite object.
    *
    * @param another FloatLite object
    */
  public FloatLite6(FloatLite6 other)
    {
    this.integer = other.getInteger();
    for (int i = 0; i < 3; i++)
      this.decimal[i] = other.getNumerator(i);
    this.positive = other.getSign();
    }


  // PUBLIC METHODS
  /** Zeros the value of the FloatLite object.
    */
  public void zero()
    {
    this.setInteger(0);
    this.setSign(true);
    for (int i = 0; i < 3; i++)
      this.decimal[i] = (char) 0;
    }


  /** Add a FloatLite object to the current one.
    * The resulting value is held in the calling object.
    *
    * @param a FloatLite object to be added
    */
  public FloatLite6 add(FloatLite6 other)
    {
    // some temporary variables to keep from creating a new FL Object
    int othint;
    char oth0,oth1,oth2;
    // local flag to take care of recursive call of add
    boolean signFlag = false;
    if (positive == other.getSign())
      {
      integer = integer+other.getInteger();
      addDigits(other);
      }
    else
      {
      if (this.absCompare(other) < 0)
        {
        // save orig values from other
        othint = other.getInteger();
        oth0 = other.decimal[0];
        oth1 = other.decimal[1];
        oth2 = other.decimal[2];

        // set the local signFlag so that recursive call does not reset signChanged
        if (signChanged)
          signFlag = true;
        // do the add in the other FL object
        this.assign(other.add(this));
        // reassign original values to other
        other.setInteger(othint);
        other.decimal[0] = oth0;
        other.decimal[1] = oth1;
        other.decimal[2] = oth2;
        if (signFlag)
          signChanged = true;
        // test for sign change
        if (signChanged)
          {
          signFlag = false;
          signChanged = false;
          other.setSign(!other.getSign());
          }
        // integer variable in FloatLite object can never be negative
        if (integer < 0)
          this.zero();

        return this;
        }
      //at this point, abs(this) must be larger
      subDigits(other);
      integer = integer - other.getInteger();

      }

    // check for sign changes
    if (signChanged)
       {
       signChanged = false;
       other.setSign(!other.getSign());
       }
    // integer variable in FloatLite object can never be negative
    if (integer < 0)
      this.zero();
    return this;
    }//end method: add


  /** Add an integer to the current FloatLite object.
    * The resulting value is held in the calling object.
    *
    * @param integer value to be added
    */
  public FloatLite6 add(int num)
    {
    FloatLite6Math.addInt(this,num);
    return this;
    }


  /** Add a String object to current FloatLite object.
    * The resulting value is held in the calling object.
    *
    * @param String object holding a valid floatlite6 value to be added
    */
  public FloatLite6 add(String num)
    {
    FloatLite6Math.addString(this,num);
    return this;
    }


  /** Subtract a FloatLite object from the current one.
    * The resulting value is held in the calling object.
    *
    * @param a FloatLite object to be subtracted
    */
  public FloatLite6 subtract(FloatLite6 other)
    {
    if (this.equals(other))
      {
      if (this.positive == other.getSign())
        this.zero();
      return this;
      }
    other.setSign(!other.getSign());
    signChanged = true;
    return add(other);
    }


  /** Subtract an int from a FloatLite6 object.
    * The resulting value is held in the calling object.
    *
    * @param int to be subtracted
    */
  public FloatLite6 subtract(int num)
    {
    FloatLite6Math.subInt(this,num);
    return this;
    }


  /** Subtract a string object from a FloatLite6 object.
    * The resulting value is held in the calling object
    *
    * @param string holding a valid floatlite6 value to be subtracted
    */
  public FloatLite6 subtract(String num)
    {
    FloatLite6Math.subString(this,num);
    return this;
    }


  /** Test equality with another FloatLite object.
    *
    * @param FloatLite object for comparison
    * @return boolean [i]true[/i] if objects hold same value [i]false[/i] otherwise
    */
  public boolean equals(FloatLite6 other)
    {
    if (positive==other.getSign() && integer==other.getInteger())
      {
      char[] otherDec = other.getNumerator();
      if (decimal[0]==otherDec[0] && decimal[1]==otherDec[1] && decimal[2]==otherDec[2])
        return true;
      }
    return false;
    }


  /** Compare two FloatLite objects.
    *
    * @param FloatLite object for comparison
    * @return 0 if the objects are equal;
    *        -1 if other object is greater than current object;
    *         1 if current object is greater
    */
  public int compare(FloatLite6 other)
    {
    if (this.equals(other))
      return 0;

    char[] otherDec = other.getNumerator();

    if (positive && !other.getSign())
      return 1;
    else if (!positive && other.getSign())
      return -1;
    else if (!positive && integer<other.getInteger())
      return 1;
    else if (!positive && integer==other.getInteger())
      {
      if (decimal[0] < otherDec[0])
        return 1;
      else if (decimal[0]==otherDec[0] && decimal[1]<otherDec[1])
        return 1;
      else if (decimal[0]==otherDec[0] && decimal[1]==otherDec[1] && decimal[2]<otherDec[2])
        return 1;
      }
    else if (positive && integer>other.getInteger())
      return 1;
    else if (positive && integer==other.getInteger())
      {
      if (decimal[0] > otherDec[0])
        return 1;
      else if (decimal[0]==otherDec[0] && decimal[1]>otherDec[1])
        return 1;
      else if (decimal[0]==otherDec[0] && decimal[1]==otherDec[1] && decimal[2]>otherDec[2])
        return 1;
      }

    return -1;
    }//end method: compare


  /** Compare two FloatLite objects.
    *
    * @param FloatLite object for comparison
    * @return 0 if the objects are equal;
    *        -1 if other object is greater than current object;
    *         1 if current object is greater
    */
  public int absCompare(FloatLite6 other)
    {
    if (this.equals(other))
      return 0;

    char[] otherDec = other.getNumerator();


    if (integer>other.getInteger())
      return 1;
    else if (integer==other.getInteger())
      {
      if (decimal[0] > otherDec[0])
        return 1;
      else if (decimal[0]==otherDec[0] && decimal[1]>otherDec[1])
        return 1;
      else if (decimal[0]==otherDec[0] && decimal[1]==otherDec[1] && decimal[2]>otherDec[2])
        return 1;
      }

    return -1;
    }


  /** Assign a value from a FloatLite object to another FloatLite object.
    *
    * @param FloatLite value to be used
    */
  public void setValue(FloatLite6 other)
    {
    this.assign(other);
    }


  /** Assign a value from a stringbuffer object that contains a valid
    * float number.
    *
    * @param StringBuffer value to be used
    */
  public void setValue(StringBuffer sbnum)
    {
    buildFLObject(sbnum);
    }


  /** Assign a value from a string object that contains a valid float number.
    *
    * @param String value to be used
    */
  public void setValue(String num)
    {
    sbtemp.clear();
    sbtemp.append(num);
    buildFLObject(sbtemp);
    }


  /** Assign a value from a char array that contains a valid float number.
    *
    * @param char array value to be used
    */
  public void setValue(char[] num)
    {
    sbtemp.clear();
    int len = num.length;
    for (int i = 0; i <  len; i++)
      sbtemp.append(num[i]);
    buildFLObject(sbtemp);
    }


  /** Assign a value from an integer.
    *
    * @ param integer value to be used
    */
  public void setValue(int num)
    {
    sbtemp.clear();
    sbtemp.append(num);
    buildFLObject(sbtemp);
    }


  /** Return the current FloatLite object as a string.
    */
  public String toString()
    {
    sbtemp.clear();
    if (!positive)
      sbtemp.append("-");
    // use a cheap way to get this integer value into the string
    sbtemp.append(integer);
    sbtemp.append(".");

    for (int i = 0; i < 3; i++)
      {
      if ((int)decimal[i] < 10)
        {
        sbtemp.append("0");
        sbtemp.append((int) decimal[i]);
        }
      else
        {
        sbtemp.append(((int)decimal[i]/10));
        sbtemp.append(((int)decimal[i]%10));
        }
      }

    int end= sbtemp.length();

    // remove trailing zeros before returning
    for (int i = end-1; i > 0; i--)
      {
      if (sbtemp.charAt(i) != '0')
        break;
      sbtemp.delete(i,i+1);
      }
    // add back one zero if decimal point is last character
    if (sbtemp.charAt(sbtemp.length() -1) == '.')
      sbtemp.append("0");
    return sbtemp.toString();
    }//end method: toString


  /**
  * Get the sign of the FloatLite6 object.
  * @return boolean [i]true[/i] if positive, [i]false[/i] if negative
  */
  public boolean getSign()
    {return positive;}


  /** Find the numerator of the fraction element.
    *
    * @return integer representation of numerator value -- denominator
    *   is always 10000
    */
  public char[] getNumerator()
    {return decimal;}


  /** Find the numerator of the fraction element.
    *
    * @return integer representation of numerator value -- denominator
    *   is always 10000
    */
  public char getNumerator(int digit)
    {return decimal[digit];}


  /** Find the whole number portion of the object.
    *
    * @return integer representation of whole number value
    */
  public int getInteger()
    {return integer;}


  /** Protected method to set the whole number portion of the object
    * available to class and package members.
    *
    * @param new integer value of whole number portion
    */
  protected void setInteger(int newInt)
    {integer = newInt;}


  /** Protected method to set the numerator of the fraction element
    * available to class and package members.
    *
    * @param new integer representation of numerator value -- denominator
    *   is always 10000
    */
  protected void setNumerator(char[] newInt)
    {decimal = newInt;}


  /** Protected method to set the numerator element available to class
    * and package members.
    *
    * @param new integer representation of numerator value -- denominator
    *   is always 10000
    */
  protected void setNumerator(int digit,char newval)
    {decimal[digit] = newval;}


//============================================================================
// Methods and fields below this point are private.
//============================================================================


  /* Private method to set the sign of the current FloatLite6 object.
   *
   * @param boolean value true for positive, false for negative
   */
  private void setSign(boolean newSign)
    {positive = newSign;}


  /* Private method to assign a FloatLite value to the current object.
   *
   * @param FloatLite object whose value is to be assigned to current object
   */
  private void assign(FloatLite6 other)
    {
    integer = other.getInteger();
    for (int i = 0; i < 3; i++)
      this.decimal[i] = other.getNumerator(i);
    positive = other.getSign();
    }


  /* Private method used in addition function.
   */
  private void addDigits(FloatLite6 other)
    {
    char[] otherDec = other.getNumerator();
    int temp;
    temp = decimal[2] + otherDec[2];
    if (temp<0)
      temp += 256;
    if (temp >= 100)
      {
      decimal[1]++;
      temp-=100;
      }
    decimal[2] = (char)temp;
    temp = decimal[1] + otherDec[1];
    if (temp<0)
      temp += 256;
    if (temp >= 100)
      {
      decimal[0]++;
      temp-=100;
      }
    decimal[1] = (char)temp;
    temp = decimal[0] + otherDec[0];
    if (temp<0)
      temp += 256;
    if (temp >= 100)
      {
      integer++;
      temp-=100;
      }
    decimal[0] = (char)temp;
    }//end method: addDigits


  /* Private method used in subtraction function.
   */
  private void subDigits(FloatLite6 other)
    {
    char[] otherDec = other.getNumerator();
    int temp;
    for (int i = 2; i > -1; i--)
      {
      temp = decimal[i] - otherDec[i];
      if (temp<0)
        {
        // keep test in index range
        if (i > 0)
          {
          // borrow
          this.decimal[i-1]--;
          }
        else
          integer--;
        testOverflow();
        temp+=100;
         }
      this.decimal[i] = (char)temp;
      }
    }//end method: subDigits


  /* Private method used internally in subtracting decimal digits.
   */
  private void testOverflow()
    {
    if (decimal[1] < 0)
      {
      this.decimal[1] += 100;
      this.decimal[0]--;
      testOverflow();
      }
    if (decimal[0] < 0)
      {
      this.decimal[0] += 100;
      this.integer--;
      }
    }


  /* Private method used in constructor to trim excess places.
   */
  private StringBuffer round(StringBuffer num)
    {
    StringBuffer answer = new StringBuffer(10);
    int place = indexOf(num, '.')+6;
    for (int i=0; i<place; i++)
      answer.append(num.charAt(i));

    if (num.charAt(place+1) < '5')
      answer.append(num.charAt(place));
    else
      answer.append((char)((int)num.charAt(place)+1));

    return answer;
    }


  /* Private method used in constructor.
   */
  private int indexOf(StringBuffer temp, char ch)
    {
    for (int i=0; i<temp.length(); i++)
      if (temp.charAt(i) == ch)
        return i;
    return -1;
    }


  /* Private method used in calculations.
   */
  private String substring(StringBuffer temp, int start, int end)
    {
    StringBuffer answer = new StringBuffer(temp.length());
    for (int i=start; i<end; i++)
      answer.append(temp.charAt(i));
    return answer.toString();
    }


  /* Private method used in calculations.
   */
  private String substring(StringBuffer temp, int start)
    {
    return substring(temp, start, temp.length());
    }


  /* Private method used in constructors.
   *
   * @param StringBuffer num SB representation of float value
   */
  private void buildFLObject(StringBuffer num)
    {
    // call method to test for valid values
    testVals(num);
    positive = num.charAt(0)!='-';
    char hldr = '0';

    if (indexOf(num, '.') == -1)
      num.append(".000000");
    int exp = num.length() - indexOf(num, '.') - 1;
    while (exp<6)
      {
      num.append("0");
      exp++;
      }
    int digits = indexOf(num, '.');
    if (!positive)
      {
      digits--;
      // remove the minus sign
      num.delete(0,1);
      }

    if (exp > 6)
      num = round(num);

    //integer = Integer.parseInt(substring(num, 0, indexOf(num, '.')));
    integer = 0;
    for (int i = 0; i < digits; i++)
      {
      // create next decimal place on all but 1st
      if (i > 0)
        integer *= 10;

      hldr = num.charAt(i);
      hldr -= '0';
      integer += hldr;
      }

    if (digits > 5 || integer<0)
      {
      integer=0;
      decimal[0] = 0;
      decimal[1] = 0;
      decimal[2] = 0;
      }
    else
      {
      // get the integer and . out of the sb
      num.delete(0,indexOf(num, '.') + 1);
      // use a cheap (heap) way to get the values into decimal
      for (int i = 0; i < 3; i++)
        {
        hldr = num.charAt(i*2);
        hldr -= '0';
        decimal[i] = hldr;
        decimal[i] *= 10;
        hldr =  num.charAt(i*2 +1);
        decimal[i] += (hldr  - '0');
        }

      }

    }//end method: buildFLObject


  /* Private method used to validate input.
   *
   * @param StringBuffer num SB representation of float value
   */
  private void testVals(StringBuffer num)
    {
    int len = num.length();
    boolean decFnd = false;
    boolean badVal = false;
    char tstVal = num.charAt(0);

    if (tstVal != '-' && tstVal != '.')
      if (tstVal < '0' || tstVal > '9')
        badVal = true;
    for (int i = 1; i < len; i++)
      {
      if (badVal)
        break;
      tstVal = num.charAt(i);
      if (tstVal == '.')
        if (decFnd)
          badVal = true;
        else
          decFnd = true;
      else if (tstVal < '0' || tstVal > '9')
        badVal = true;
      }
    if (badVal)
      {
      // set the string to 0.0
      num.clear();
      num.append("0.0");
      }
    }//end method: testVals


  /* Private integer variable to hold the whole number portion of the
   * FloatLite6 value.
   */
  private int integer;


  /* Private boolean variable to hold the sign portion of the FloatLite6 value.
   */
  private boolean positive;


  /* Private char array variable to hold the decimal portion of the
   * FloatLite6 value.
   */
  private char[] decimal = new char[3];


  /* Private static StringBuffer variable available to all FloatLite6
   * instances for internal calculations.
   */
  private static StringBuffer sbtemp = new StringBuffer(13);


  /* Private static boolean variable to for internal use by FloatLite6 methods
   */
  private static boolean signChanged = false;
  }// end class: FloatLite6