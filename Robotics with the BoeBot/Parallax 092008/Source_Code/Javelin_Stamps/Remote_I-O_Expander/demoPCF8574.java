// PCF8574 demonstration program
// -- by Jon Williams
// -- Applications Engineer, Parallax
// -- jwilliams@parallaxinc.com
// -- Updated: 20 July 2002

/*  PCF8574 connections (device Addr = 0):
 *
 *  Pin  1: A0 --> Vss
 *  Pin  2: A1 --> Vss
 *  Pin  3: A2 --> Vss
 *  Pin  4: P0 --> 1.5K --> LED [cathode] --> Vdd
 *  Pin  5: P1 --> 1.5K --> LED [cathode] --> Vdd
 *  Pin  6: P2 --> 1.5K --> LED [cathode] --> Vdd
 *  Pin  7: P3 --> 1.5K --> LED [cathode] --> Vdd
 *  Pin  8: Vss
 *  Pin  9: P4 --> 10K --> Vdd; N.0. switch --> Vss
 *  Pin 10: P5 --> 10K --> Vdd; N.0. switch --> Vss
 *  Pin 11: P6 --> 10K --> Vdd; N.0. switch --> Vss
 *  Pin 12: P7 --> 10K --> Vdd; N.0. switch --> Vss
 *  Pin 13: Int\  (not used)
 *  Pin 14: SCL --> I2C bus (pulled up to Vdd through 4.7K)
 *  Pin 15: SDA --> I2C bus (pulled up to Vdd through 4.7K)
 *  Pin 16: Vdd
 */

import stamp.core.*;
import stamp.peripheral.I2C;
import stamp.peripheral.io.PCF8574;

public class demoPCF8574 {

  final static char CLR_SCR = '\u0010';
  final static char HOME    = 0x01;

  public static void main() {

    // create bus for I2C devices [SDA = pin0, SCL = pin1]
    I2C i2cBus = new I2C(CPU.pin0, CPU.pin1);
    // create PCF8574 object with mixed I/O
    // -- device address is 0
    // -- bits 0-3 are outputs; 4-7 are inputs
    PCF8574 ioPort0 = new PCF8574(i2cBus, 0x00, 0xF0);
    // create buffer for screen messages
    StringBuffer msg = new StringBuffer();
    // storage for switch inputs
    int switches;

    // start of code
    System.out.print(CLR_SCR);
    if (ioPort0.isPresent()) {
      while(true) {
        // create binary counter for LEDs
        for(int counter = 0; counter <= 15; counter++) {
          // write [inverted for active-low] counter bits to PCF8574
          ioPort0.write(~counter);
          // scan inputs between LED write cycles
          for (int scan = 0; scan < 5; scan++) {
            // get switch inputs
            // -- inputs are inverted for active low
            // -- shifted to align with bit 0 of variable
            switches = ~ioPort0.read() >> 4 & 0x0F;
            // update message screen
            msg.clear();
            msg.append(HOME);
            msg.append("LEDs = ");
            msg.append(counter);
            msg.append("  \n\r");
            msg.append("Switches = ");
            msg.append(switches);
            msg.append("  ");
            // show binary version of switches
            for (int swBit = 0x08; swBit > 0x00; swBit >>= 1) {
              if ((switches & swBit) == 0)
                msg.append("0");
              else
                msg.append("1");
            }
            msg.append(" ");
            System.out.print(msg.toString());
          }
        }
      }
    }
    else {
      System.out.print("Error: PCF8574 not found");
    }
  }
}