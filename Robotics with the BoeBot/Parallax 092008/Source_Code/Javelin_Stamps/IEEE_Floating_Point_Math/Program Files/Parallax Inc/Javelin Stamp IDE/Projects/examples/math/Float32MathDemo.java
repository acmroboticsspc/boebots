/*
 * Copyright © Micromega Corporation 2002-2003. All rights reserved
 */
import  stamp.math.*;
import  stamp.core.*;
/**
 * Demonstration routine for the Float32Math class.
 *
 * @author Cam Thompson, Micromega Corporation
 * @version 1.1 - May 2, 2003
 * changes:
 *    V1.1 - May 2, 2003
 *      - added static working variable to prevent memory leak
 *    V1.0 - April 29, 2003
 *      - original version
 */

  public class Float32MathDemo {

  // array of floating point test values
  static int testValues[] = {
    (short)0x0000, (short)0x0000,   // 0.0
    (short)0x3F80, (short)0x0000,   // 1.0
    (short)0x3A83, (short)0x126F,   // 0.001
    (short)0x3DCC, (short)0xCCCD,   // 0.1
    (short)0x3F00, (short)0x0000,   // 0.5
    (short)0x42C8, (short)0x0000,   // 100.0
    (short)0x7F80, (short)0x0000,   // +infinity
    (short)0x7FC0, (short)0x0000    // NaN
  };
  // floating point contants
  static Float32 f10_0   = new Float32((short)0x4120, (short)0x0000); // 10.0
  static Float32 f20_0   = new Float32((short)0x41A0, (short)0x0000); // 20.0

  // temporary floating point variable
  static Float32 tmp   = new Float32();

  // string buffer
  static StringBuffer sbuf = new StringBuffer(30);
  static String underline = "---------------------";

  //------------------- main --------------------------------------------------

  public static void main() {
    Float32 angle = new Float32();
    Float32 f1 = new Float32();
    Float32 f2 = new Float32();
    Float32 f3 = new Float32();
    int     n;

    System.out.println("\u0010Float32MathDemo");

    // graph sine from 0 to 360 degrees (2*PI radians)
    System.out.println("\r\nGraph of sine");
    System.out.println("-1        0        1");
    System.out.println(underline);
    for (int i = 0; i <= 20; i++) {
      angle.setToConstant(Float32.PI);
      angle.multiply(2*i);
      angle.divide(f20_0);
      graph(Float32Math.sin(angle));
    } // end for

    // graph sine from 0 to 360 degrees (2*PI radians)
    System.out.println("\r\nGraph of cosine");
    System.out.println("-1        0        1");
    System.out.println(underline);
    for (int i = 0; i <= 20; i++) {
      angle.setToConstant(Float32.PI);
      angle.multiply(2*i);
      angle.divide(f20_0);
      graph(Float32Math.cos(angle));
    } // end for

    // The following tests call each of the math routines with various
    // values from the array of test values.

    System.out.println("\r\nAbsolute Value Test");
    System.out.println(underline);
    for (int idx = 0; idx < testValues.length; idx += 2) {
      f1.set(testValues[idx], testValues[idx+1]);
      f2.set(Float32Math.abs(f1));
      printTest("abs", f1, f2);
      f1.negate();
      f2.set(Float32Math.abs(f1));
      printTest("abs", f1, f2);
    }

    System.out.println("\r\nMinimum Test");
    System.out.println(underline);
    for (int idx = 0; idx < testValues.length; idx += 2) {
      f1.set(testValues[idx], testValues[idx+1]);
      if ((idx % 4) != 0) f1.negate();
      int idx2 = testValues.length-idx-2;
      f2.set(testValues[idx2], testValues[idx2+1]);
      f3.set(Float32Math.min(f1, f2));
      printTest("min", f1, f2, f3);
    }

    System.out.println("\r\nMaximum Test");
    System.out.println(underline);
    for (int idx = 0; idx < testValues.length; idx += 2) {
      f1.set(testValues[idx], testValues[idx+1]);
      if ((idx % 4) != 0) f1.negate();
      int idx2 = testValues.length-idx-2;
      f2.set(testValues[idx2], testValues[idx2+1]);
      f3.set(Float32Math.max(f1, f2));
      printTest("max", f1, f2, f3);
    }

    System.out.println("\r\nSquare Root");
    System.out.println(underline);
    for (int idx = 0; idx < testValues.length; idx += 2) {
      f1.set(testValues[idx], testValues[idx+1]);
      f2.set(Float32Math.sqrt(f1));
      printTest("sqrt", f1, f2);
      f1.negate();
      f2.set(Float32Math.sqrt(f1));
      printTest("sqrt", f1, f2);
    }

    System.out.println("\r\nCeiling");
    System.out.println(underline);
    for (int idx = 0; idx < testValues.length; idx += 2) {
      f1.set(testValues[idx], testValues[idx+1]);
      f2.set(Float32Math.ceil(f1));
      printTest("ceil", f1, f2);
      f1.negate();
      f2.set(Float32Math.ceil(f1));
      printTest("ceil", f1, f2);
    }

    System.out.println("\r\nFloor");
    System.out.println(underline);
    for (int idx = 0; idx < testValues.length; idx += 2) {
      f1.set(testValues[idx], testValues[idx+1]);
      f2.set(Float32Math.floor(f1));
      printTest("floor", f1, f2);
      f1.negate();
      f2.set(Float32Math.floor(f1));
      printTest("floor", f1, f2);
    }

    System.out.println("\r\nRound");
    System.out.println(underline);
    for (int idx = 0; idx < testValues.length; idx += 2) {
      f1.set(testValues[idx], testValues[idx+1]);
      n = Float32Math.round(f1);
      printTest("round", f1, n);
      f1.negate();
      n = Float32Math.round(f1);
      printTest("round", f1, n);
    }

    System.out.println("\r\nPower");
    System.out.println(underline);
    for (int idx = 0; idx < testValues.length; idx += 2) {
      f1.set(testValues[idx], testValues[idx+1]);
      f3.set(Float32Math.pow(f1, 2));
      printTest("pow", f1, 2, f3);
      f3.set(Float32Math.pow(f1, -3));
      printTest("pow", f1, -3, f3);
      f1.negate();
      f3.set(Float32Math.pow(f1, -2));
      printTest("pow", f1, -2, f3);
      f3.set(Float32Math.pow(f1, 3));
      printTest("pow", f1, 3, f3);
    }

    System.out.println("\r\ne to the power (integer)");
    System.out.println(underline);
    for (int i = -5; i <= 5; i++) {
      f1.set(Float32Math.exp(i));
      printTest("exp", i, f1);
    }

    System.out.println("\r\ne to the power (Float32)");
    System.out.println(underline);
    for (int idx = 0; idx < testValues.length; idx += 2) {
      f1.set(testValues[idx], testValues[idx+1]);
      f2.set(Float32Math.exp(f1));
      printTest("exp", f1, f2);
      f1.negate();
      f2.set(Float32Math.exp(f1));
      printTest("exp", f1, f2);
    }

    System.out.println("\r\nSine Test");
    System.out.println(underline);
    for (int idx = 0; idx < testValues.length; idx += 2) {
      f1.set(testValues[idx], testValues[idx+1]);
      f2.set(Float32Math.sin(f1));
      printTest("sin", f1, f2);
      f1.negate();
      f2.set(Float32Math.sin(f1));
      printTest("sin", f1, f2);
    }

    System.out.println("\r\nCosine Test");
    System.out.println(underline);
    for (int idx = 0; idx < testValues.length; idx += 2) {
      f1.set(testValues[idx], testValues[idx+1]);
      f2.set(Float32Math.cos(f1));
      printTest("cos", f1, f2);
      f1.negate();
      f2.set(Float32Math.cos(f1));
      printTest("cos", f1, f2);
    }

    System.out.println("\r\nTangent Test");
    System.out.println(underline);
    for (int idx = 0; idx < testValues.length; idx += 2) {
      f1.set(testValues[idx], testValues[idx+1]);
      f2.set(Float32Math.tan(f1));
      printTest("tan", f1, f2);
      f1.negate();
      f2.set(Float32Math.tan(f1));
      printTest("tan", f1, f2);
    }

    System.out.println("\r\ntoRadians/toDegrees Test");
    System.out.println(underline);
    f1.set(180);
    f2.set(Float32Math.toRadians(f1));
    printTest("toRadians", f1, f2);
    f1.setToConstant(Float32.PI);
    f2.set(Float32Math.toDegrees(f1));
    printTest("toDegrees", f1, f2);

    System.out.println("\r\nDone.");
  } // end main

  //-------------------- graph ------------------------------------------------

  // plot the value on a graph
  static void graph(Float32 fnum) {
    // calculate point on graph for value
    tmp.set(fnum);
    tmp.multiply(f10_0);
    int point = tmp.intValue();

    // plot the point on the graph
    char ch;
    for (int i = -10; i <= 10; i++) {
      if (i == point) ch = '*';
      else if (i == 0) ch = '|';
      else ch = ' ';
      System.out.print(ch);
    }
    // display the value
    System.out.print(' ');
    System.out.println(fnum.toString());
  } // end graph

  //-------------------- printTest --------------------------------------------
  // print two Float32 arguments and a Float32 test result
  static void printTest(String s, Float32 f1, Float32 f2, Float32 f3) {
    sbuf.clear();
    sbuf.append(s);
    sbuf.append('(');
    sbuf.append(f1.toString());
    sbuf.append(", ");
    sbuf.append(f2.toString());
    sbuf.append(')');
    while (sbuf.length() < 25) sbuf.append(' ');
    System.out.print(sbuf.toString());
    System.out.print(" = ");
    System.out.println(f3.toString());
  }
  // print a Float32 argument and a Float32 test result
  static void printTest(String s, Float32 f1, Float32 f2) {
    sbuf.clear();
    sbuf.append(s);
    sbuf.append('(');
    sbuf.append(f1.toString());
    sbuf.append(')');
    while (sbuf.length() < 20) sbuf.append(' ');
    System.out.print(sbuf.toString());
    System.out.print(" = ");
    System.out.println(f2.toString());
  } // end printTest

  // print a Float32 argument an integer argument and a Float32 test result
  static void printTest(String s, Float32 f1, int n, Float32 f2) {
    sbuf.clear();
    sbuf.append(s);
    sbuf.append('(');
    sbuf.append(f1.toString());
    sbuf.append(", ");
    sbuf.append(n);
    sbuf.append(')');
    while (sbuf.length() < 20) sbuf.append(' ');
    System.out.print(sbuf.toString());
    System.out.print(" = ");
    System.out.println(f2.toString());
  } // end printTest

  // print an integer argument and a Float32 test result
  static void printTest(String s, int n, Float32 f1) {
    sbuf.clear();
    sbuf.append(s);
    sbuf.append('(');
    sbuf.append(n);
    sbuf.append(')');
    while (sbuf.length() < 20) sbuf.append(' ');
    System.out.print(sbuf.toString());
    System.out.print(" = ");
    System.out.println(f1.toString());
  } // end printTest

  // print a Float32 argument and an integer test result
  static void printTest(String s, Float32 f1, int n) {
    sbuf.clear();
    sbuf.append(s);
    sbuf.append('(');
    sbuf.append(f1.toString());
    sbuf.append(')');
    while (sbuf.length() < 20) sbuf.append(' ');
    System.out.print(sbuf.toString());
    System.out.print(" = ");
    System.out.println(n);
  } // end printTest

} // end class