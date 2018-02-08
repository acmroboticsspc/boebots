/*
 * Copyright © 2002 Parallax, Inc. All rights reserved.
 */

package stamp.util.text;
import stamp.core.*;

/**
 * This library provides formatted string output and scan methods based on the
 * standard C sprintf and sscanf functions.  With this class you can convert
 * byte, int, short and char types into binary, octal or hexadecimal formatted
 * strings.  You can specify the minimum and maximum number of columns to
 * display your values, and these values can be left or right justified, with
 * or without padded zeros.  Methods are included for the following C library
 * functions: itoa, atoi, printf, sprintf, and sscanf.<p>
 *
 * Most java implementations of the sprintf and sscanf functions use a linked
 * list of objects to replace the variable list of arguments of the sprintf and
 * sscanf functions while maintaining the original format string (in case of
 * porting from C). This linked list requires many of new objects and these are
 * normally only used once.<P>
 *
 * This implementation defines the functions bprintf and bscanf that take one,
 * and only one, variable parameter. The original format string must be split
 * into pieces that all have one format specifier. This normally is not a
 * problem since most format strings consists of fixed text with format
 * specifiers for values. As there are no linked lists it requires few memory
 * resources.
 *
 * @version 1.0 Feb 24th, 2003
 * @author Peter Verkaik (peterverkaik@boselectro.nl)
 */

public class Format{

  /**
   * Test if character is a digit.
   *
   * @param ch Character to be tested
   * @return True if ch is a digit
   */
  public static boolean isDigit(int ch) {
    return ch<='9' && ch>='0';
  }

  /**
   * Test if character is a space (0x20), tab (\t) or newline (\n).
   *
   * @param ch Character to be tested
   * @return True if ch is a space, tab or newline
   */
  public static boolean isSpace(int ch) {
    return ch==' ' || ch=='\t' || ch=='\n';
  }

  /**
   * Reverse string in place.
   *
   * @param s Character array to reverse
   */
  public static void reverse(char[] s) {
    int c, h, k;

    h = 0;
    k = strLen(s) - 1;
    while (h < k) {
      c = s[h];
      s[h++] = s[k];
      s[k--] = (char)c;
    }
  }

  /**
   * Get length of null terminated string in character array.
   *
   * @param s Character array that holds null terminated string
   * @return Length of null terminated string (not counting the null)
   */
  public static int strLen(char[] s) {
    int h;

    h = -1;
    while (s[++h] != 0) ;
    return h;
  }

  /**
   * Convert signed integer to signed decimal string.
   * (range is -32768 to +32767)
   *
   * @param n Integer value to convert
   * @param s Character array to hold output
   */
  public static void itoa(int n, char[] s) {
    int sign, h;

    h = 0;
    if ((sign = n) < 0)                     // record sign
      n = -n;                               // make n positive
    do {                                    // generate digits in reverse order
      s[h++] = (char)(n % 10 + '0');        // get next digit
    } while ((n = n / 10) > 0);
    if (sign < 0) s[h++] = '-';             // sign character only if negative
    s[h] = 0;                               // closing 0
    reverse(s);
  }

  /**
   * Convert signed decimal string to signed integer.
   * (range is -32768 to +32767)
   *
   * @param s Character array that holds decimal string
   * @return Value of signed decimal string
   */
  public static int atoi(char[] s) {
    int sign, n, h;

    h = 0;
    while (isSpace(s[h])) ++h;              // skip over any white space
    sign = 1;                               // assume positive
    switch (s[h]) {
      case '-': sign = -1;                  // is negative
      case '+': ++h;                        // advance h only if sign present
    }
    n = 0;
    while (isDigit(s[h])) n = 10 * n + (s[h++] - '0'); // calculate value
    return sign * n;                        // adjust for sign
  }

  /**
   * Print formatted string to message window (stdout).
   * (Javelin version of C <code>printf</code>, limited to 1 parameter max.)
   *
   * <p>format specifier for byte, char, short and int:
   * <code>%c,%d,%i,%b,%o,%u,%x</code>
   * <br>format specifier for string: <code>%s</code>
   * <br>field specification (example)<pre>
   *   %-05.7d where - for left justify, default right justify
   *                 0 for padding with zeroes, default spaces
   *                 5 minimum field width
   *                 . field separator
   *                 7 maximum field width</pre>
   *
   * <i>Use %% to print a percentage sign.</i>
   * @param str Character array holding formatted output
   * @param si Index of str to start writing to
   * @param format String defining format
   * @param data Value or string to print
   * @return the number of characters printed to stdout (message window)
   */
  public static int printf(char[] format) {
    return _printf(null, 0, format, INTEGER|STDOUT, 0, null);
  }

  public static int printf(String format) {
    return _printf(null, 0, format.toCharArray(), INTEGER|STDOUT, 0, null);
  }

  public static int printf(String format, int data) {
    return _printf(null, 0, format.toCharArray(), INTEGER|STDOUT, data, null);
  }

  public static int printf(char[] format, int data) {
    return _printf(null, 0, format, INTEGER|STDOUT, data, null);
  }

  public static int printf(String format, String data) {
    return _printf(null, 0, format.toCharArray(), STRING|STDOUT, 0, data.toCharArray());
  }

  public static int printf(char[] format, String data) {
    return _printf(null, 0, format, STRING|STDOUT, 0, data.toCharArray());
  }

  public static int printf(String format, char[] data) {
    return _printf(null, 0, format.toCharArray(), STRING|STDOUT, 0, data);
  }

  public static int printf(char[] format, char[] data) {
    return _printf(null, 0, format, STRING|STDOUT, 0, data);
  }

  /**
   * Print formatted string to character buffer.
   * (Javelin version of C <code>sprintf</code>, limited to 1 argument max.)
   * for format specifiers see <code>printf</code><br>
   * for other parameters see <code>printf</code>
   *
   * @param str Character buffer holding formatted output
   * @return Index of str pointing at trailing null
   */
  public static int sprintf(char[] str, char[] format) {
    return _printf(str, 0, format, INTEGER, 0, null);
  }

  public static int sprintf(char[] str, String format) {
    return _printf(str, 0, format.toCharArray(), INTEGER, 0, null);
  }

  public static int sprintf(char[] str, String format, int data) {
    return _printf(str, 0, format.toCharArray(), INTEGER, data, null);
  }

  public static int sprintf(char[] str, char[] format, int data) {
    return _printf(str, 0, format, INTEGER, data, null);
  }

  public static int sprintf(char[] str, String format, String data) {
    return _printf(str, 0, format.toCharArray(), STRING, 0, data.toCharArray());
  }

  public static int sprintf(char[] str, char[] format, String data) {
    return _printf(str, 0, format, STRING, 0, data.toCharArray());
  }

  public static int sprintf(char[] str, String format, char[] data) {
    return _printf(str, 0, format.toCharArray(), STRING, 0, data);
  }

  public static int sprintf(char[] str, char[] format, char[] data) {
    return _printf(str, 0, format, STRING, 0, data);
  }

  /**
   * Print formatted string to character buffer, maintaining buffer index.
   * (special version of <code>sprintf</code>, to ease porting <code>printf</code>
   * and <code>sprintf</code> with multiple arguments)<br>
   * for format specifiers see <code>printf</code>
   *
   * <p>Example:
   * <br>An original <code>printf</code> statement like:<br>
   * <code>printf("outside temperature %d %s inside temperature %d %s",12,"fahrenheit",24,"celsius");</code>
   * <br>would be written for the Javelin as:<code><pre>
   *   printf("outside temperature %d ",12);
   *   printf("%s ","fahrenheit");
   *   printf("inside temperature %d ",24);
   *   printf("%s","celsius");</code></pre>
   * The original <code>printf</code> is simply split up in smaller <code>printf</code>
   * statements that take only 1 argument.  The same should apply for <code>sprintf.</code>
   * However, <code>sprintf</code> outputs characters starting at the first position
   * of the supplied character array. To get a single output string while having
   * multiple arguments, <code>bprintf</code> outputs characters starting at a
   * supplied position.
   * <p>Example:
   * <br>An original <code>sprintf</code> statement like<br>
   * <code>sprintf(buffer,"outside temperature %d %s inside temperature %d %s",12,"fahrenheit",24,"celsius");</code>
   * <br>would be written for the Javelin as:<code><pre>
   *   int k=0;
   *   k=bprintf(buffer,k,"outside temperature %d ",12);
   *   k=bprintf(buffer,k,"%s ","fahrenheit");
   *   k=bprintf(buffer,k,"inside temperature %d ",24);
   *   k=bprintf(buffer,k,"%s","celsius");</code></pre>
   * The original <code>sprintf</code> is simply split up in smaller <code>bprintf
   * </code> statements that take only 1 argument. The end result is the same:
   * a single output string in buffer. Obviously, if there is only 1 argument in
   * an original <code>sprintf</code>, then use <code>sprintf</code>.
   *
   * @param str Character buffer holding formatted output
   * @param si Start index in str for formatted output
   * for other parameters see printf
   * @return Index of str pointing at closing 0
   */
  public static int bprintf(char[] str, int si, char[] format) {
    return _printf(str, si, format, INTEGER, 0, null);
  }

  public static int bprintf(char[] str, int si, String format) {
    return _printf(str, si, format.toCharArray(), INTEGER, 0, null);
  }

  public static int bprintf(char[] str, int si, String format, int data) {
    return _printf(str, si, format.toCharArray(), INTEGER, data, null);
  }

  public static int bprintf(char[] str, int si, char[] format, int data) {
    return _printf(str, si, format, INTEGER, data, null);
  }

  public static int bprintf(char[] str, int si, String format, String data) {
    return _printf(str, si, format.toCharArray(), STRING, 0, data.toCharArray());
  }

  public static int bprintf(char[] str, int si, char[] format, String data) {
    return _printf(str, si, format, STRING, 0, data.toCharArray());
  }

  public static int bprintf(char[] str, int si, String format, char[] data) {
    return _printf(str, si, format.toCharArray(), STRING, 0, data);
  }

  public static int bprintf(char[] str, int si, char[] format, char[] data) {
    return _printf(str, si, format, STRING, 0, data);
  }

  /**
   * Scan formatted string into variable.
   * (Javelin version of C <code>sscanf</code>, single argument)
   *
   * <p>format specifier for byte, char, short and int:
   * <code>%c,%d,%i,%b,%o,%u,%x</code>
   * <br>format specifier for string: <code>%s</code>
   * <br>field specification (example)<pre>
   *   %*4d where * specifies to scan but not assign scanned value
   *              4 (maximum) field width to scan</pre>
   *
   * @param str Character array holding formatted string
   * @param format String defining format
   * @param data pointer of variable to hold scanned value
   * @return Index in str pointing at next position to read
   */
  public static int sscanf(char[] str, char[] format, char[] data) {
    return _scanf(str, 0, format, STRING, null, data);
  }

  public static int sscanf(String str, String format, int[] data) {
    return _scanf(str.toCharArray(), 0, format.toCharArray(), INTEGER, data, null);
  }

  public static int sscanf(String str, char[] format, int[] data) {
    return _scanf(str.toCharArray(), 0, format, INTEGER, data, null);
  }

  public static int sscanf(char[] str, String format, int[] data) {
    return _scanf(str, 0, format.toCharArray(), INTEGER, data, null);
  }

  public static int sscanf(char[] str, char[] format, int[] data) {
    return _scanf(str, 0, format, INTEGER, data, null);
  }

  public static int sscanf(String str, String format, char[] data) {
    return _scanf(str.toCharArray(), 0, format.toCharArray(), STRING, null, data);
  }

  public static int sscanf(String str, char[] format, char[] data) {
    return _scanf(str.toCharArray(), 0, format, STRING, null, data);
  }

  public static int sscanf(char[] str, String format, char[] data) {
    return _scanf(str, 0, format.toCharArray(), STRING, null, data);
  }


  /**
   * Scan formatted string into variable, maintaining formatted string index
   * (special version of <code>sscanf</code>, to ease porting <code>scanf</code>
   * and <code>sscanf</code> with multiple arguments) for format specifiers
   * see <code>sscanf</code><p>
   *
   * Example:<br>
   * An original <code>sscanf</code> statement like<code>
   * sscanf(buffer,"outside temperature %d %s inside temperature %d %s",outtemp,
   *        outunit,intemp,inunit);</code><br>
   * would be written for the Javelin as:<pre><code>
   *   int k=0;
   *   k=bscanf(buffer,k,"outside temperature %d ",outtemp);
   *   k=bscanf(buffer,k,"%s ",outunit);
   *   k=bscanf(buffer,k,"inside temperature %d ",intemp);
   *   k=bscanf(buffer,k,"%s",inunit);</code></pre><p>
   *
   * The original <code>sscanf</code> is simply split up in smaller bscanf
   * statements that take only 1 argument. The variable <code>k</code> keeps
   * track of the parsing position.<br>
   * Note:<br>
   * The one missing is method from the original C library is <code>scanf</code>.
   * It would use the Terminal.getChar() method to read parameters from the
   * Javelin message window. The problem with <code>scanf</code> is that wrong
   * user input may cause <code>scanf</code> to never return.  Better to read in
   * a complete string (terminated by user input carriage return) and then parse
   * the string using <code>bscanf</code>.
   *
   * @param str Character array holding formatted string
   * @param si Start index in <code>str</code> to read from
   * for other parameters see </code>sscanf<code>
   * @return Index in <code>str</code> pointing at next position to read
   */
  public static int bscanf(char[] str, int si, char[] format, char[] data) {
    return _scanf(str, si, format, STRING, null, data);
  }

  public static int bscanf(String str, int si, String format, char[] data) {
    return _scanf(str.toCharArray(), si, format.toCharArray(), STRING, null, data);
  }

  public static int bscanf(String str, int si, String format, int[] data) {
    return _scanf(str.toCharArray(), si, format.toCharArray(), INTEGER, data, null);
  }

  public static int bscanf(String str, int si, char[] format, int[] data) {
    return _scanf(str.toCharArray(), si, format, INTEGER, data, null);
  }

  public static int bscanf(char[] str, int si, String format, int[] data) {
    return _scanf(str, si, format.toCharArray(), INTEGER, data, null);
  }

  public static int bscanf(char[] str, int si, char[] format, int[] data) {
    return _scanf(str, si, format, INTEGER, data, null);
  }

  public static int bscanf(String str, int si, char[] format, char[] data) {
    return _scanf(str.toCharArray(), si, format, STRING, null, data);
  }

  public static int bscanf(char[] str, int si, String format, char[] data) {
    return _scanf(str, si, format.toCharArray(), STRING, null, data);
  }


//============================================================================
// Methods and fields below this point are private.
//============================================================================

  private static final int INTEGER = 1;
  private static final int STRING  = 2;
  private static final int STDOUT  = 8192;
  private static final int STDIN   = 16384;

  private static char[] buf = new char[17]; // for integer conversions

  /*
   * Print formatted string.
   * core printf routine
   *
   * @param str Character buffer holding formatted output
   * @param si Start index in str for formatted output
   * for other parameters see printf
   * @return Index of str pointing at closing 0
   */
  private static int _printf(char[] str, int si, char[] fmt, int flag, int idata, char[] sdata) {
    int pad=' ', len, max, min, bi, fi;
    boolean left=false, stdout, inttype, strtype;

    inttype = (flag & INTEGER) == INTEGER;
    strtype = (flag & STRING) == STRING;
    stdout = (flag & STDOUT) == STDOUT;
    fi = 0;
    len = 0;
    while (fi < fmt.length) {
      if (fmt[fi]==0) break;
      if (fmt[fi] != '%') {
        if (stdout) System.out.print(fmt[fi++]); else str[si++] = fmt[fi++];
        continue;
      } else fi++;
      if (fmt[fi] == '%') {
        if (stdout) System.out.print(fmt[fi++]); else str[si++] = fmt[fi++];
        continue;
      }
      if (fmt[fi] == '-') {left = true; ++fi;} else left = false;
      if (fmt[fi] == '0') pad = '0'; else pad = ' ';
      if (isDigit(fmt[fi])) {                    // minimum field width
        bi=0;
        while (isDigit(fmt[fi])) buf[bi++] = fmt[fi++];
        buf[bi] = 0;
        min = atoi(buf);
      }
      else min = 0;
      if (fmt[fi] == '.') {                      // maximum field width
        ++fi;
        bi = 0;
        while (isDigit(fmt[fi])) buf[bi++] = fmt[fi++];
        buf[bi] = 0;
        max = atoi(buf);
      }
      else max = 0;
      if (inttype) {                             // following code for integers
        switch (fmt[fi++]) {
          case 'c': buf[0] = (char)idata; buf[1] = 0; break;
          case 'd':
          case 'i': itoa(idata, buf); break;
          case 'b': itoab(idata, buf, 2);  break;// binary
          case 'o': itoab(idata, buf, 8);  break;// octal
          case 'u': itoab(idata, buf, 10); break;// decimal
          case 'x': itoab(idata, buf, 16); break;// hexadecimal
          default: buf[0] = 0;                   // no valid field specification
        }
        len = strLen(buf);
      }
      if (strtype) {                             // following code for strings
        if (fmt[fi++] != 's') len = 0; else len = sdata.length;
      }
      if ((max != 0) && (max < len)) len = max;
      if (min > len) min = min - len; else min = 0;
      bi = 0;
      if (!left) {
        if ((buf[bi]=='-') && (pad=='0')) {
          if (stdout) System.out.print(buf[bi++]); else str[si++] = buf[bi++];
        }
        while (min-- != 0)
          if (stdout) System.out.print((char)pad); else str[si++] = (char)pad;
      }
      while (len-- != 0) {
        if (inttype) {
          if (stdout) System.out.print(buf[bi++]); else str[si++] = buf[bi++];
        }
        if (strtype) {
          if (sdata[bi]==0) break;
          if (stdout) System.out.print(sdata[bi++]); else str[si++] = sdata[bi++];
        }
      }
      if (left) {
        while (min-- != 0)
          if (stdout) System.out.print((char)pad); else str[si++] = (char)pad;
      }
    }// end while
    if (!stdout) str[si] = 0;
    return si;
  }// end methods: _printf

  /*
   * Scan formatted string into variable.
   * core scanf routine
   *
   * @param str Character array holding formatted string
   * @param si Start index in str to read from
   * for other parameters see sscanf
   * @return Index in str pointing at next position to read
   */
  private static int _scanf(char[] str, int si, char[] fmt, int flag, int[] idata, char[] sdata) {
    int width, bi, fi, sign, result;
    boolean skip, stdin, inttype, strtype;

    inttype = (flag & INTEGER) == INTEGER;
    strtype = (flag & STRING) == STRING;
    stdin = (flag & STDIN) == STDIN;
    fi = 0;
    while (fi < fmt.length) {
      if (isSpace(fmt[fi])) {                    // white space in format
        while (isSpace(str[si])) ++si;           // skip white space in str
        if (str[si] == 0) { si = 0; return si; }
        ++fi;
        continue;
      }
      if (fmt[fi] != '%') {            // non-white space in format but no %
        while (isSpace(str[si])) ++si;           // skip white space in str
        if (str[si] == 0) { si = 0; return si; }
        if (str[si] != fmt[fi]) return si;       // no match in str
        ++si;
        ++fi;
        continue;
      }
      ++fi;                                      // skip over %
      if (fmt[fi] == '*') {skip=true; ++fi;} else skip=false;
      if (isDigit(fmt[fi])) {                    // field width
        bi=0;
        while (isDigit(fmt[fi])) buf[bi++] = fmt[fi++];
        buf[bi] = 0;
        width = atoi(buf);
      }
      else width = 32767;
      while (isSpace(str[si])) ++si;
      if (str[si] == 0) {si = 0; break;}
      if (inttype) {                             // following code for integers
        switch (fmt[fi]) {
          case 'c':
            if (!skip) idata[0] = str[si++]; else ++si;
            break;
          case 's':
            break;
          default:
            if (str[si]=='-') {sign = -1; ++si;}
            else if (str[si]=='+') {sign = 1; ++si;} else sign = 1;
            if (isDigit(str[si])) {              // integer value
              bi=0;
              while ((width-- != 0) && isDigit(str[si])) buf[bi++] = str[si++];
              buf[bi] = 0;
            }
            else break;
            switch (fmt[fi]) {
              case 'd':
              case 'i': result = sign * atoi(buf); break;
              case 'b': result = atoib(buf, 2); break;
              case 'o': result = atoib(buf, 8); break;
              case 'u': result = atoib(buf, 10); break;
              case 'x': result = atoib(buf, 16); break;
              default:
                skip = true; result = 0;
            }
            if (!skip) idata[0] = result;
        }
      }
      if (strtype) {                             // following code for strings
        switch (fmt[fi]) {
          case 's':
            bi = 0;
            while ((width-- != 0) && !isSpace(str[si]))
              if (!skip) sdata[bi++] = str[si++]; else ++si;
            if (!skip) sdata[bi] = 0;            // closing 0
            if (str[si] == 0) {si = 0; return si;}
            break;
          default:
        }
      }
      ++fi;
    }// end while
    return si;
  }// end methods: _scanf

  /*
   * Convert unsigned integer to unsigned string using specific base.
   * (This is a non-standard function). Used by <code>printf</code>.
   *
   * @param n Integer value to convert
   * @param s Character array to hold output
   * @param b Base for output (2=binary, 8=octal, 10=decimal, 16=hexadecimal)
   */
  private static void itoab(int n, char[] s, int b) {
    int lowbit, h;

    h = 0;
    b >>= 1;
    do {                             // generate digits/letters in reverse order
      lowbit = n & 1;
      n = (n >> 1) & 0x7FFF;
      s[h] = (char)(((n % b) << 1) + lowbit);
      if (s[h] < 10) s[h] += '0'; else s[h] += ('A'-10);
      ++h;
    } while ((n /= b) != 0);
    s[h] = 0;                        // closing 0
    reverse(s);
  }

  /*
   * Convert unsigned string to unsigned integer using specific base.
   * (This is a non-standard function). Used by <code>scanf</code>.
   *
   * @param s Character array holding unsigned string
   * @param b Base for conversion (2=binary, 8=octal, 10=decimal, 16=hexadecimal)
   * @return Unsigned integer value
   */
  private static int atoib(char[] s, int b) {
    int n, digit, h;

    h = 0;
    n = 0;
    while (isSpace(s[h])) ++h;
    while ((digit = (127 & s[h++])) >= '0') {
      if (digit >= 'a')      digit -= ('a'-10);
      else if (digit >= 'A') digit -= ('A'-10);
      else                   digit -= '0';
      if (digit >= b) break;
      n = b * n + digit;
      if (h==s.length) break;
    }
    return n;
  }

}// end class: Format