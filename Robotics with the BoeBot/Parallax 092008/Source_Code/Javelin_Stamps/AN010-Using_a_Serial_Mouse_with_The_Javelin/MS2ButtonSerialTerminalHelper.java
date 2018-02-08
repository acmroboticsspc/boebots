package examples.peripheral.hid.mouse.serial;
import stamp.core.*;
import stamp.peripheral.hid.mouse.serial.MS2ButtonSerial;

/*
 * Copyright © 2002 Parallax, Inc. All rights reserved.
 */

import stamp.core.*;
import stamp.util.*;

/**
 * This is a 'Messages from Javelin' formatting library that accompanies
 * the second example program listing in <i>AppNote010-MS2ButtoNSerial</i>.
 * This example program demonstrates how a mouse can be used
 * with a small, character based display.  Instead of asking the user to
 * supply a liquid crystal display, this library's fields and methods emulate
 * a small display shown in the Messages from Javelin window.
 */
public class MS2ButtonSerialTerminalHelper {

  /**
   * Stores the cursor's x-coordinate on the display.
   */
  public static int cursorX = 0;

  /**
   * Stores the cursor's y-coordinate on the display.
   */
  public static int cursorY = 0;

  /**
   * Character that causes the cursor in the Messages from Javelin window
   * to go up by one line.
   */
  public static final char UP     =  (char)5;

  /**
   * Character that causes the cursor in the Messages from Javelin window
   * to go down by one line.
   */
  public static final char DOWN   =  (char)6;

  /**
   * Character that causes the cursor in the Messages from Javelin window
   * to go left by one space.
   */
  public static final char LEFT   =  (char)3;

  /**
   * Character that causes the cursor in the Messages from Javelin window
   * to go right by one space.
   */
  public static final char RIGHT  =  (char)4;

  /**
   * String array that defines the outline and initial content of the
   * example display.
   */
  public String monitorOutline [] = {
    "\n                            \n",
    "  --------------------------- \n",
    " |ABCDEFGHIJKLMNOPQRSTUVWXYZ |\n",
    " |abcdefghijklmnopqrstuvwxyz |\n",
    " |0123456789 ~!@#$%^&*()_-+=/|\n",
    " |.,?!                       |\n",
    " |---------------------------|\n",
    " |                           |\n",
    " |                           |\n",
    " |                           |\n",
    " |                           |\n",
    "  --------------------------- \n",
    "  Use left mouse button to    \n",
    "  drag and drop characters    \n",
    "  from top half to bottom     \n",
    "  half.                     \n\n",
    "  Right-click clears monitor.   "
  };

  /*
   * This list stores character array copies of the string array.  These
   * character arrays are updated in run-time as characters are dragged
   * and dropped from the upper half of the display into the lower half of
   * the display.
   */
  private List list = new List(monitorOutline.length);

  /*
   * Temporary object.
   */
  protected Object temp;

  /**
   * Create a list of character arrays.  Each element in the list is a copy
   * of an element in the monitorOutline string array that has been given a
   * character array reference.
   */
  public void indexInit(){
    for(int i = 0; i < monitorOutline.length; i++){
      String s = new String(monitorOutline[i].toCharArray());
      list.add(s.toCharArray());
    }
  }

  /**
   * Recopy the contents of the monitorOutline string array into the list of
   * character arrays created by the indexInit method.
   */
  public void indexReInit(){
    char c;
    for(int i = 0; i < list.size(); i++){
      temp = list.get(i);
      for(int j = 0; j < ((char[])temp).length; j++){
        ((char[])temp)[j] = monitorOutline[i].charAt(j);
      }
    }
    resetCursor();
  }

  /**
   * Get a character at a particular column (x) and row (y) in the list of
   * character arrays.
   *
   * @param x column of the character in the list.
   * @param y row of the character in the list.
   * @return the character entry in the list that located in the specified
   *         row and column.
   */
  public char index(int x, int y){
    temp = list.get(y-1);
    return ((char[])temp)[x];
  }

  /**
   * Change the value of a character at a particular column (x) and row (y)
   * in the list of character arrays.
   *
   * @param x column of the character in the list.
   * @param y row of the character in the list.
   * @param c character to be placed in the list at the specified row and
   *          column.
   */
  public void indexUpdate(int x, int y, char c){
    temp = list.get(y-1);
    ((char[])temp)[x] = c;
  }

  /**
   * Displays the contents of the list in the Messages from Javelin window.
   */
  public void indexDisplay(){
    char c;
    for(int i = 0; i < list.size(); i++){
      temp = list.get(i);
      for(int j = 0; j < ((char[])temp).length; j++){
        c = ((char[])temp)[j];
        System.out.print(c);
      }
    }
    resetCursor();
  }

  /**
   * Clears all text from the Messages from Javelin window.
   */
  public static void cls(){
    System.out.print('\u0010');
    cursorX = cursorY = 0;
  }

  /**
   * Places the cursor in the top-left row and column in the Messages
   * from Javelin window.
   */
  public static void resetCursor(){
    System.out.print('\u0001');
    cursorX = cursorY = 0;
  }

  /**
   * Prints a character at a particular location in the messages from Javelin
   * window.
   *
   * @param x column of the character on the 'Message From Javelin' window.
   * @param y row of the character on the messages from Javelin window.
   * @param c character to be placed in the list at the specified row and
   *          column.
   */
  public static void placeChar(int x, int y, char c){
    placeCursor(x,y);
    System.out.print(c);
    updateCursorPosition(x+1,y);
  }


  /**
   * Updates the cursor position for tracking purposes.
   *
   * @param x column of the character on the messages from Javelin window.
   * @param y row of the character on the messages from Javelin window.
   */
  public static void updateCursorPosition(int x, int y){
    cursorX = x;
    cursorY = y;
  }

  /**
   * Determines if the cursor has been placed in a particular region of
   * the display.  The region is defined by the upper left and lower
   * right coordinates in (x,y) implies (column, row) format.
   *
   * @param xMin column of the region's upper-left character.
   * @param yMin row of the region's upper-left character.
   * @param xMax column of the region's lower-right character.
   * @param yMax row of the region's lower-right character.
   */
  public static boolean cursorInRegion(int xMin, int yMin, int xMax, int yMax){
    if(    ((cursorX > xMin)
        &&  (cursorY > yMin))
        && ((cursorX < xMax)
        &&  (cursorY < yMax)) ) return true;
    else return false;
  }

  /**
   * Moves the cursor to a specified column (x) and row (y) in the messages
   * from Javelin window.
   *
   * @param x column of the character on the messages from Javelin window.
   * @param y row of the character on the messages from Javelin window.
   */
  public static void placeCursor(int x, int y){
    if((x==0) && (y==0)){
      System.out.println('\u0001');
      cursorX=cursorY=0;
      return;
    }
    if(y != cursorY){
      if(y > cursorY){
        for(int i = 0; i < y-cursorY; i++){
          System.out.print(DOWN);
        }
      }else{
        for(int i = 0; i < cursorY-y; i++){
          System.out.print(UP);
        }
      }
      cursorY = y;
    }
    if(x != cursorX){
      if(x > cursorX){
        for(int i = 0; i < x-cursorX; i++){
          System.out.print(RIGHT);
        }
      }else{
        for(int i = 0; i < cursorX-x; i++){
          System.out.print(LEFT);
        }
      }
      cursorX = x;
    }
  }

}