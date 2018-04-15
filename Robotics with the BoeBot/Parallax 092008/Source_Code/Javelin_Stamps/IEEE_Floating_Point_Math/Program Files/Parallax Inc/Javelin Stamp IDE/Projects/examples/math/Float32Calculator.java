/*
 * Copyright © Micromega Corporation 2002-2003. All rights reserved
 */
import  stamp.math.*;
import  stamp.core.*;
/**
 * This is a demonstration program for the FloatSingle class that
 * implements a simple four function calculator which also provides
 * support for entering and displaying IEEE754 32-bit single precision
 * numbers.
 *
 * Input is entered in the input area of the message window and the
 * results are displayed in the message window.
 *
 * The input characters supported are:
 *  clear the calculator:                   C
 *  standard math operators:                +, -, *, /
 *  display result in decimal format:       =, <CR>
 *  display result in IEEE 754 hex format:  H, I
 *  the value of PI:                        P
 *  exit from program:                      Q, <ESC>
 *
 * Numbers can be entered in normal or exponential format:
 *    e.g. 123.45, 2.0e5, -20.543e-2, .000001
 *
 * IEEE 754 values can be entered as hexadecimal numbers:
 *    e.g. 0x3F80000
 *
 * Note you can use this calculator to easily determine the IEEE 754
 * value for a particular number by entering the number and pressing
 * the H key.  The last answer is always retained by the calculator
 * so you can easily view the result in either format.
 *    e.g.
 *    entering: PH          (P-value of Pi, H-display IEEE 754 hex)
 *    displays: 0x40490FDB  (the IEEE 754 value of Pi)
 *    entering: = or <CR>   (display result in decimal)
 *    displays: 3.1415927   (value of Pi)
 *
 * The input is simply processed from left to right with no operator
 * precedence.
 *    e.g.
 *    entering: 2.5+3*4=
 *    displays: 22.0        (2.5 is added to 3 then multiplied by 4)
 *
 * @author Cam Thompson, Micromega Corporation
 * @version 1.1 - April 9, 2003
 * changes:
 *    V1.1 - April 30, 2003
 *      - modified Float32 package location
 *    V1.0 - April 9, 2003
 *      - original version
 */

public class Float32Calculator {

  final static char ESC = 0x1B; // ESC character

  // allocate floating point objects
  static Float32 f1 = new Float32();
  static Float32 f2 = new Float32();

  //------------------- main --------------------------------------------------

  public static void main() {
    System.out.println("\u0010Float32 Calculator");
    System.out.println("-----------------------------------");
    System.out.println("Special characters:");
    System.out.println("C           clear the calculator");
    System.out.println("+, -, *, /  standard math operators");
    System.out.println("=, <CR>     display result in decimal format");
    System.out.println("H, I        display result in IEEE 754 hex format");
    System.out.println("P           the value of PI");
    System.out.println("Q, <ESC>    exit from program");
    System.out.println("\r\nNumbers can be entered in normal or exponential format");
    System.out.println("    e.g. 123.45, 2.0e5, -20.543e-2, .000001");
    System.out.println("\r\nIEEE 754 values can be entered as hexadecimal numbers");
    System.out.println("    e.g. 0x3F80000");
    System.out.println("-----------------------------------\r\n");

    boolean numberEntered = false;           // initialize variables
    char lastOperator = ' ';
    char ch = ' ';

    Float32 pi = new Float32();              // get the value of pi
    pi.setToConstant(Float32.PI);

    // main input loop
    while (ch != ESC) {

      ch = getCharacter();                   // get next character
      numberEntered = false;

      if (ch == '.' || (ch >= '0' && ch <= '9')) {
        ch = getNumber(ch);                  // check for numeric input
        numberEntered = true;
      }

      if (ch == '-' && !numberEntered) {     // check for unary minus
        ch = getNumber(ch);
        numberEntered = true;
      }

      switch (ch) {                          // process the current operator
        case 'C':
          f1.set(0);                         // clear the current number
          f2.set(0);                         // clear previous number
          break;

        case '+':
        case '-':
        case '*':
        case '/':
          doOperation(lastOperator);         // perform operation
          break;

        case '\n':                           // display answer at end of line
        case '\r':                           // unless '=' already entered
          if (lastOperator == '=') break;

        case '=':
          doOperation(lastOperator);         // perform operation
          System.out.print(' ');
          System.out.println(f2.toString()); // display floating point value
          f1.set(f2);                        // store answer as current number
          f2.set(0);                         // clear previous number
          break;

        case 'H':
        case 'I':
          doOperation(lastOperator);         // perform operation
          System.out.print(" = ");
          printHex();                        // display IEEE 754 format in hex
          f1.set(f2);                        // store answer as current number
          f2.set(0);                         // clear previous number
          break;

        case 'P':
         if (numberEntered)                  // check for multiplier
            f1.multiply(pi);
          else                               // load value of Pi
            f1.set(pi);
            doOperation(lastOperator);       // perform operation
          break;

        case ESC:
        case 'Q':
          ch = ESC;                          // quit the program
          break;

        default:                             // unrecognized character
          System.out.println("???");
          break;
      } // end switch

      lastOperator = ch;                     // set the last operator
    } // end while

    System.out.println("\r\nDone.");
  } // end main

  //------------------- doOperation -------------------------------------------

  public static void doOperation(char operator) {


    switch (operator) {                      // perform the specified operation
      case '+':
        f2.add(f1);
        break;
      case '-':
        f2.subtract(f1);
        break;
      case '*':
        f2.multiply(f1);
        break;
      case '/':
        f2.divide(f1);
        break;
      case 'P':
        break;
      default:
        f2.set(f1);
        break;
    } // end switch

  } // end doOperation

  //------------------- getNumber ---------------------------------------------

  public static char getNumber(char ch) {
    StringBuffer sbuf = new StringBuffer(20);

    sbuf.append(ch);                         // store first character
    ch = getCharacter();                     // get the next character
    if (ch == 'X') return(getHexNumber());   // check for hexadecimal prefix

    while (ch == '.' || ch == 'E' || (ch >= '0' && ch <= '9')) {
      sbuf.append(ch);                       // get numeric string
      if (ch == 'E') {                       // check for exponent part
        ch = getCharacter();
        if (ch == '+' || ch == '-') {
           sbuf.append(ch);
           ch = getCharacter();
        }
      }
      else {
        ch = getCharacter();
      } // end if
    } // end while

    f1.set(sbuf);                            // set value and return
    return(ch);
  } // end getNumber

  //------------------- getHexNumber ------------------------------------------

  public static char getHexNumber() {
    char ch;
    int high = 0;
    int low = 0;

    while (true) {                           // get IEEE value as hex number
      ch = getCharacter();
      if (ch >= 'A' && ch <= 'F')
        ch -= 7;
      else if (!(ch >= '0' && ch <= '9'))
        break;
      high = (high << 4) + (low >>> 12);
      low = (low << 4) + ((ch - '0') & 0x0F);
    }

    f1.set(high, low);                       // set value and return
    return(ch);
  } // end getHexNumber

  //------------------- getCharacter ------------------------------------------

  public static char getCharacter() {
    char ch = Terminal.getChar();            // get next character in uppercase
    return((ch >= 'a' && ch <= 'z') ? ch -= 0x20 : ch);
  } // end getCharacter

  //------------------- printHex ----------------------------------------------

  private static void printHex() {
    System.out.print("0x");
    printHex(f2.high);
    printHex(f2.low);
    System.out.print("\r\n");
  }

  private static void printHex(int n) {
    printHexDigit(n >> 12);
    printHexDigit(n >> 8);
    printHexDigit(n >> 4);
    printHexDigit(n >> 0);
  } // end printHex

  private static void printHexDigit(int n) {
    char hexDigit;
    hexDigit = (char) ('0' + (n & 0x0F));
    if (hexDigit > '9') hexDigit += 7;
    System.out.print(hexDigit);
  } // end printHexDigit

} // end class