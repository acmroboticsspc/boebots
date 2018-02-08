import stamp.peripheral.onewire.*;
import stamp.core.*;

/**
 * Class demonstrates using the DS2480B object to read the
 * ROM of a single device on a 1-Wire bus.
 * <p>
 * @version 1.2 8/28/02
 * @author Parallax, Inc.
 */

public class DS2480BReadOneDS1822ROM {

  // 0x33 = READ_ROM command.
  final static int READ_ROM = 0x33;

  // Field for storing the presence response from the reset() method.
  static boolean presence;

  // Array for storing the 1-Wire device's address.
  static int [] address = new int[8];

  // Declare I/O pins to be used by the DS2480B's Uart transmit & receive lines.
  public final static int SERIAL_TX_PIN = CPU.pin5;
  public final static int SERIAL_RX_PIN = CPU.pin4;

  // Constructor that declares a new DS2480B object and initializes it with
  // the I/O pins used for the transmit and receive Uarts.
  private static DS2480B oneWireBus = new  DS2480B(SERIAL_TX_PIN, SERIAL_RX_PIN);

  public static void main() {
    oneWireBus.reset();
    oneWireBus.message(READ_ROM);
    oneWireBus.getData(8,address,READ_ROM);
    oneWireBus.reset();
    for(int i = 0; i < 8; i++){
      System.out.println(address[i]);
    }
  }

}