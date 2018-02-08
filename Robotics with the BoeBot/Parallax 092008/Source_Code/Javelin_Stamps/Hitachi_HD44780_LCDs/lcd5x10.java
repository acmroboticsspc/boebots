import stamp.core.*;
import stamp.peripheral.lcd.HD44780;

//  test 5x10 mode of standard 2-line LCD
//  -- LCD is initialized to one, 5x10 font line
//  -- properly descended characters are displayed

public class lcd5x10 {

  public static final char DESC_G = 0xE7;       // descender character codes
  public static final char DESC_J = 0xEA;
  public static final char DESC_P = 0xF0;
  public static final char DESC_Q = 0xF1;
  public static final char DESC_Y = 0xF9;


  public static void main() {

    // use BS2p connections
    HD44780 display = new HD44780(CPU.pin0);

    // "scrunchy" descenders
    String msg1a = new String("jolly quick");
    String msg1b = new String("good puppy");

    // proper descenders
    StringBuffer msg2a = new StringBuffer();
    msg2a.append(DESC_J);
    msg2a.append("olly ");
    msg2a.append(DESC_Q);
    msg2a.append("uick");

    StringBuffer msg2b = new StringBuffer();
    msg2b.append(DESC_G);
    msg2b.append("ood ");
    msg2b.append(DESC_P);
    msg2b.append("u");
    msg2b.append(DESC_P);
    msg2b.append(DESC_P);
    msg2b.append(DESC_Y);


    display.init5x10();

    while (true) {
      display.clearScr();
      display.lcdOut(msg1a);              // display squished characters
      CPU.delay(20000);

      display.clearScr();
      display.lcdOut(msg2a);              // corrected with extended code
      CPU.delay(20000);

      display.clearScr();
      display.lcdOut(msg1b);
      CPU.delay(20000);

      display.clearScr();
      display.lcdOut(msg2b);
      CPU.delay(20000);
    }
  }
}