/*
 * Copyright © 2002 Parallax Inc. All rights reserved.
 */

package stamp.peripheral.display.lcd.serial;
import stamp.core.*;

/**
 * This class is for the Scott Edwards BPI-216 Serial LCD Module.  The BPI-216
 * is a 2-line by 16-character LCD with a serial interface for easy use.
 * The PBI-216 has a switch selectable baud rate that must be set to 9600 baud
 * for communication with this class.<p>
 * <pre>
 * Revision History:
 * Ver 1.0 - 09/21/02: Initial release by Jon Williams and Andy Lindsay of
 *                     Parallax Inc.
 * Ver 1.1 - 12/24/02: Modified by Steve Dill of Parallax Inc.
 *                     Moved file location:
 *                       from: stamp.peripheral.lcd
 *                       to: stamp.peripheral.display.lcd.serial
 *                     All public constants now private.
 *                     Renamed cursorOn() to displayOnUL()
 *                     Renamed blinkOn() to displayOnBL()
 *                     Renamed cursorOff() to displayOn()
 *                     Renamed clearScr() to CLS()
 *                     Created scrollRT() - Scrolls display image to the right
 *                     Created scrollLF() - Scrolls display image to the left
 *                     Renamed moveTo() to cursorMove()
 *                       - row now accepts "1" for line1 or "2" for line2
 *                     Created cursorRT() - Move cursor to the right
 *                     Created cursorLF() - Move cursor to the left
 * </pre>
 * @author Jon Williams, Andy Lindsay and Steve Dill of Parallax Inc.
 * @version 1.1 December 24, 2002
 */
public class BPI216 {


  /**
   * Creates new serial LCD display object
   *
   * @param lcdUart TX uart object setup for LCD control
   */
  public BPI216(Uart lcdUart) {
    this.lcdUart = lcdUart;
  }


  /**
   * Sends BPI-216 command byte to LCD directly:<br>
   * 0x01 clear the LCD<br>
   * 0x02 move the cursor home<br>
   * 0x10 move the cursor left<br>
   * 0x14 move the cursor right<br>
   * 0x18 scroll left<br>
   * 0x1C scroll right<br>
   * 0x08 blank display without clearing<br>
   * 0x0E display on with cursor underline<br>
   * 0x0C display on with no cursor<br>
   * 0x0D display on with cursor blinking-block<br>
   * 0x80 DDRAM starting address<br>
   * 0x40 character Generator (CG) RAM<br>
   *
   * @param cmd Command to send to LCD
   */
  public void command(int cmd) {
    lcdUart.sendByte(LCD_CTRL);                  // set command mode
    lcdUart.sendByte(cmd);                       // send the command
  }


  /**
   * Writes specified CGRAM location on LCD at cursor position
   *
   * @param CG CGRAM location (0-7) to write on LCD
   */
  public void write(int CG) {
    lcdUart.sendByte(CG);                        // send the character
  }


  /**
   * Writes string on LCD at cursor position
   *
   * @param s String to write on LCD
   */
  public void write(String s) {
    for (int i = 0; i < s.length(); i++)
      write(s.charAt(i));
  }


  /**
   * Writes string buffer on LCD at cursor position
   *
   * @param sb StringBuffer to write on LCD
   */
  public void write(StringBuffer sb) {
    for (int i = 0; i < sb.length(); i++)
      write(sb.charAt(i));
  }


  /**
   * Clears LCD and returns cursor to line 1, position 0
   */
  public void CLS() {
    command(CLR_LCD);
    CPU.delay(100);
  }


  /**
   * Moves cursor to home position (line 1, column 0) -- DDRAM unchanged
   */
  public void home() {
    command(CRSR_HOME);
    CPU.delay(100);
  }


  /**
   * Moves LCD cursor to specified row and column position.
   *
   * @param row Row number (1 or 2)
   * @param column Position on line (0-15 on screen, 16-39 off screen)
   */
  public void cursorMove(int row, int column) {
    command((DDRAM+(row-1)*ROW) + column);      // row 1 = 0x80, row 2 = 0xC0
  }


  /**
   * Moves cursor right
   */
  public void cursorRT() {
    command(CRSR_RT);
  }


  /**
   * Moves cursor left
   */
  public void cursorLF() {
    command(CRSR_LF);
  }


  /**
   * Sends custom character data to LCD
   *
   * @param CG CGRAM number (0 - 7)
   * @param array[] Custom character data (8 byte char array)
   */
  public void createChar5x8(int CG, char array[]) {
    command(CGRAM + (8 * CG));                  // point to character RAM
    for (int i = 0; i < 8; i++) {
      write(array[i]);                           // download character data
    }
    command(DDRAM);                              // move cursor back to screen
  }


  /**
   * Turns display on, no cursor
   */
  public void displayOn() {
    command(DISP_ON);
  }


  /**
   * Turns display off without changing contents of RAM
   */
  public void displayOff() {
    command(DISP_OFF);
  }


  /**
   * Turn display on with an underline cursor
   */
  public void displayOnUL() {
    command(DISP_ON_UL);
  }


  /**
   * Turns display on with a blinking-block cursor
   */
  public void displayOnBL() {
    command(DISP_ON_BLK);
  }


  /**
   * Scroll display image to the right.
   */
  public void scrollRT() {
    command(SCROLL_RT);
  }


  /**
   * Scroll display image to the left.
   */
  public void scrollLF() {
    command(SCROLL_LF);
  }


//============================================================================
// Methods and fields below this point are private.
//============================================================================

  private static final int CLR_LCD     = 0x01;   // clear the LCD
  private static final int CRSR_HOME   = 0x02;   // move the cursor home
  private static final int CRSR_LF     = 0x10;   // move the cursor left
  private static final int CRSR_RT     = 0x14;   // move the cursor right
  private static final int SCROLL_LF   = 0x18;   // scroll left
  private static final int SCROLL_RT   = 0x1C;   // scroll right
  private static final int DISP_OFF    = 0x08;   // blank display without clearing
  private static final int DISP_ON_UL  = 0x0E;   // display on, cursor underline
  private static final int DISP_ON     = 0x0C;   // display on, no cursor
  private static final int DISP_ON_BLK = 0x0D;   // display on, cursor blinking-block
  private static final int DDRAM       = 0x80;   // DDRAM starting address
  private static final int CGRAM       = 0x40;   // character Generator (CG) RAM
  private static final int ROW         = 0x40;   // offset for line 2

  private static final char LCD_CTRL   = 0xFE;  // control byte indicator
  private Uart lcdUart;                         // uart control object

}// end class: BPI216