/* Simple test program to demonstrate a few formatting examples.
 *
 * For more information see AppNote012 from Parallax Inc.
 * Version 1.0 Feb. 24th, 2003 (an012)
 */

import stamp.util.text.*;
public class FormatTest{

  public static void main() {
    int var = -36;

    System.out.println("Testing printf");
    Format.printf("Print variables like %d, within a message.\n",var);
    Format.printf("Pad fixed output with %05d zeroes.\n",4);
    Format.printf("Place a single character %c, in the message.\n",'A');
    Format.printf("Place a %s in the message.\n","string");
  }
}