import stamp.peripheral.onewire.*;
import stamp.core.*;

/**
 * Class demonstrates using the DS2480B object to acquire temperature
 * information from the scratchpad RAM of a single DS1822 device on
 * a 1-Wire bus.
 *
 * @version 1.2 11-20-02
 * @author Parallax, Inc.
 */

public class DS2480BReadOneDS1822Scratchpad {

  // Constants used for communicaiton with the DS1822 on the 1-Wire bus.
  final static int SKIP_ROM = 0x00CC;
  final static int CONVERT_T = 0x0044;
  final static int READ_SCRATCHPAD = 0x00BE;
  final static int MATCH_ROM = 0x55;

  // Field for storing the presence response from the reset() method.
  static boolean presence;

  // Field for storing the two temperature bytes sent by the DS1822.
  static int [] temperature = new int[2];

  // Declare I/O pins to be used by the DS2480B's Uart transmit and
  // receive lines.
  public final static int SERIAL_TX_PIN = CPU.pin5;
  public final static int SERIAL_RX_PIN = CPU.pin4;

  // Declare a new DS2480B object and initialize with connections
  // for Uart serial communication with a DS2480B chip.
  private static DS2480B oneWireBus = new  DS2480B(SERIAL_TX_PIN,
                                                    SERIAL_RX_PIN);

  /**
   * Measures temperature of a single DS1822 device on a 1-Wire bus.
   * IMPORTANT: This method will not work if there is more than one
   * 1-Wire device on the bus.
   */
  public static void getTempFromOneDs1822(int [] value){
    // Send reset pulse to 1-wire bus.
    oneWireBus.reset();

    // Arm strong pullup resistor.
    oneWireBus.strongPullup(oneWireBus.INFINITE_DURATION);

    // Send two characters to 1-wire bus using message method.
    oneWireBus.message(SKIP_ROM);
    oneWireBus.message(CONVERT_T);

    // Wait for a bit reading to go from false to true indicating that
    // the temperature measurement is ready.
    while(!oneWireBus.getBit()){
      CPU.delay(500);
    }

    // Disarm strong pullup resistor.
    oneWireBus.strongPullup(oneWireBus.TERMINATE_DURATION);

    // Issue another reset pulse followed by a skip ROM command.
    oneWireBus.reset();
    oneWireBus.message(SKIP_ROM);

    // Issue read scratchpad command and load the first two bytes from the DS1822's
    // scratchpad into the temperature array.  Then, deliver a final reset pulse.
    oneWireBus.message(READ_SCRATCHPAD);
    oneWireBus.getData(2,temperature,READ_SCRATCHPAD);
    oneWireBus.reset();
  }

  /**
   * Formats and displays raw temperature data that was read from
   * the DS1822's scratchpad RAM.
   */
  public static void formatAndDisplay(int [] a){
    int tempLo, tempHi, tempInt, tempDec;
    tempLo = a[0];
    tempHi = a[1];

    // Move the bits of the integer value of the measurement into
    // a single field.
    tempInt = tempLo + (tempHi << 8);

    // Convert the lowest four bits into the fractional decimal value that goes
    // to the right of the decimal point and the upper eight bits into the integer
    // decimal value to the left of the decimal point.
    tempDec = (((tempInt & 0x000F) * 100)/16);
    tempInt = tempInt >> 4;

        System.out.print(tempInt);
    System.out.print(".");

    // Maintain a leading zero if the fractional decimal measurement is less
    // than 10.
    if(tempDec < 10){
      System.out.print("0");
    }
    System.out.print(tempDec);
    System.out.println(" degrees-Celsius");
  }

  public static void main() {
    while(true){
      getTempFromOneDs1822(temperature);
      // Clear Messages from Javelin display.
      System.out.print('\u0010');
      formatAndDisplay(temperature);
    }
  }

}