// Joystick demonstration program
// -- by Jon Williams
// -- Applications Engineer, Parallax
// -- jwilliams@parallaxinc.com
// -- Updated: 22 July 2002

/* Joystick connections:
 *
 *   X Pot    --> Javelin.P0 (rcTime() circuit -- 0.1 uF connected to Vdd)
 *   Y Pot    --> Javelin.P1 (rcTime() circuit -- 0.1 uF connected to Vdd)
 *   X Switch --> Javelin.P2 (active low [pulled up through 10K])
 *   Y Switch --> Javelin.P3 (active low [pulled up through 10K])
 */

import stamp.core.*;
import stamp.peripheral.Joystick;

public class demoJoystick {

  final static char CLR_SCR = '\u0010';

  public static void main() {

    // create joystick object
    joystick controller = new joystick(CPU.pin0, CPU.pin1, CPU.pin2, CPU.pin3);
    // intermediate holder for each pot reading
    int axisVal;
    // create message buffer for screen display
    StringBuffer msg = new StringBuffer();

    while (true) {
      // create and display measurement message
      msg.clear();
      msg.append(CLR_SCR);
      msg.append("Joystick Demo\n\n");

      // X Pot
      msg.append("X position = ");
      axisVal = controller.rawX();
      if (axisVal >= 0)
        msg.append(axisVal);
      else
        msg.append("Error");
      msg.append("\n");

      // Y Pot
      msg.append("Y position = ");
      axisVal = controller.rawY();
      if (axisVal >= 0)
        msg.append(axisVal);
      else
        msg.append("Error");
      msg.append("\n\n");

      // X Button
      msg.append("X button is ");
      if (!controller.buttonX()) msg.append("not ");
      msg.append("pressed.\n");

      // Y Button
      msg.append("Y button is ");
      if (!controller.buttonY()) msg.append("not ");
      msg.append("pressed.\n\n");

      System.out.print(msg.toString());

      // wait 0.1 seconds between readings
      CPU.delay(1000);
    }
  }
}