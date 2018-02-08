// Devantech SRF04 Sonic Rangefinder demonstration program
// -- by Jon Williams
// -- Applications Engineer, Parallax
// -- jwilliams@parallaxinc.com
// -- Updated: 21 July 2002

/*  SRF04 connections:
 *
 *  Trigger --> Javelin.P0
 *  Echo    --> Javelin.P1
 */

import stamp.core.*;
import stamp.peripheral.devantech.SRF04;

public class demoSRF04 {

  final static char CLR_SCR = '\u0010';

  public static void main() {

    // create SRF04 object (trigger on P0, echo on P1)
    SRF04 range = new SRF04(CPU.pin0, CPU.pin1);
    // create message buffer for screen display
    StringBuffer msg = new StringBuffer();

    int distance;

    while (true) {
      // create and display measurement message
      msg.clear();
      msg.append(CLR_SCR);
      msg.append("SRF04 Demo\n\n");

      // display raw return
      distance = range.getRaw();
      msg.append("Raw  = ");
      if (distance > 0)
        msg.append(distance);
      else
        msg.append("Out of Range");
      msg.append("\n");

      // display whole inches
      distance = range.getIn();
      msg.append("In   = ");
      if (distance > 0)
        msg.append(distance);
      else
        msg.append("Out of Range");
      msg.append("\n");

      // display fractional inches
      distance = range.getIn10();
      msg.append("In10 = ");
      if (distance > 0) {
        msg.append(distance / 10);    // whole part
        msg.append( ".");
        msg.append(distance % 10);    // fractional part
      }
      else
        msg.append("Out of Range");
      msg.append("\n");

      // display centimeters
      distance = range.getCm();
      msg.append("cm   = ");
      if (distance > 0)
        msg.append(distance);
      else
        msg.append("Out of Range");
      msg.append("\n");

      // display millimeters
      distance = range.getMm();
      msg.append("mm   = ");
      if (distance > 0)
        msg.append(distance);
      else
        msg.append("Out of Range");
      msg.append("\n");

      System.out.print(msg.toString());

      // wait 0.5 seconds between readings
      CPU.delay(5000);
    }
  }
}