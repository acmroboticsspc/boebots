import stamp.core.*;
import stamp.peripheral.lcd.HD44780;

// Two-line LCD test using BS2p connection spec and custom characters

public class lcdTest {

  static final char[] MOUTH0 = {0x0E,0x1F,0x1F,0x1F,0x1F,0x1F,0x0E,0x00};
  static final char[] MOUTH1 = {0x0E,0x1F,0x1F,0x18,0x1F,0x1F,0x0E,0x00};
  static final char[] MOUTH2 = {0x0E,0x1F,0x1C,0x18,0x1C,0x1F,0x0E,0x00};
  static final char[] SMILEY = {0x00,0x0A,0x0A,0x00,0x11,0x0E,0x06,0x00};

  static final int[] CELLS = {2, 1, 0, 1};  // animation sequence

  public static void main() {

    HD44780 myLCD = new HD44780(CPU.pin0);  // same as BS2p LCD connections
    String msg = new String(" IS VERY COOL! \003");  // must be 16 chars

    myLCD.initMultiLine();
    myLCD.createChar5x8(0, MOUTH0);
    myLCD.createChar5x8(1, MOUTH1);
    myLCD.createChar5x8(2, MOUTH2);
    myLCD.createChar5x8(3, SMILEY);

    while (true) {
      myLCD.clearScr();
      CPU.delay(5000);
      myLCD.lcdOut("  The Javelin");
      CPU.delay(20000);

      // animation cycle
      for (int addr = 0; addr <= 15; addr++) {
        for (int cycle = 0; cycle <= 4; cycle++) {
          myLCD.moveTo(1, addr);
          if (cycle < 4) {
            myLCD.lcdOut(CELLS[cycle]);
          }
          else {
            myLCD.lcdOut(msg.charAt(addr));
          }
          CPU.delay(750);
        }
      }

      // flash the display
      for (int cycle = 0; cycle <= 4; cycle++) {
        myLCD.lcdCmd(myLCD.DISP_CTRL | myLCD.DISP_OFF);
        CPU.delay(2500);
        myLCD.lcdCmd(myLCD.DISP_CTRL | myLCD.DISP_ON);
        CPU.delay(2500);
      }
      CPU.delay(20000);
    }
  }
}