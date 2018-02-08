// LED object demonstration
//
// by Jon Williams
// jwilliams@parallaxinc.com
//
// 03 MAY 2002

import stamp.core.*;
import stamp.peripheral.io.LED;

public class HelloLEDs {

  public static void main() {

    // create LEDs
    LED green = new LED(CPU.pin0, true);
    LED yellow = new LED(CPU.pin1, true);
    LED red = new LED(CPU.pin2, true);

    // create timers for LEDs
    Timer greenTimer = new Timer();
    Timer yellowTimer = new Timer();
    Timer redTimer = new Timer();

    // demonstrate LED basics
    green.on();
    CPU.delay(5000);
    yellow.putState(true);
    CPU.delay(5000);
    red.putState(yellow.getState());
    CPU.delay(5000);

    green.off();
    CPU.delay(5000);
    yellow.putState(false);
    CPU.delay(5000);
    red.invert();
    CPU.delay(5000);

    // start the timers
    greenTimer.mark();
    yellowTimer.mark();
    redTimer.mark();

    // flash LEDs at different rates
    while (true) {
      if (greenTimer.timeout(250)) {
        greenTimer.mark();
        green.invert();
      }
      if (yellowTimer.timeout(333)) {
        yellowTimer.mark();
        yellow.invert();
      }
      if (redTimer.timeout(1000)) {
        redTimer.mark();
        red.invert();
      }
    }
  }
}