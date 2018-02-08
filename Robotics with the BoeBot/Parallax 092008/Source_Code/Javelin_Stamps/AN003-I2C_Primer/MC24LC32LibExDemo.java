package examples.protocol.i2cprimer;

/**
 * See AppNote003 I2C Primer - EEPROM Example.pdf for information
 * on this example program.
 */

import stamp.core.*;

// Import I2C and MC24LC32LibEx libraries.
import stamp.protocol.I2C;
import stamp.peripheral.memory.eeprom.MC24LC32LibEx;

public class MC24LC32LibExDemo {

  // Create an I2C bus object named i2cbus.
  final public static int SDAPin = CPU.pin6;
  final public static int SCLPin = CPU.pin7;
  public static I2C i2cbus = new I2C(SDAPin, SCLPin);

  // Create a MC24LC32LibExample object named eeprom0 using the i2cbus object.
  public static MC24LC32LibEx eeprom0 = new MC24LC32LibEx(i2cbus, 0);

  // Declare StringBuffer object and static variables.
  public static StringBuffer characters = new StringBuffer(128);
  public static int menuChoice, eeAddress, data, controlByte, numChars;

  public static void main() {

    while(true){

      // Get user input for menu choice and EEPROM address.
      menuChoice = TerminalHelper.menu(1,4);
      eeAddress = TerminalHelper.getAddress();

      // Each case interacts with the user using the TerminalHelper object
      // and reads/writes the 24LC32 using eeprom0, an MC24LC32LibEx object.
      switch (menuChoice){

        case '1':
          data = TerminalHelper.getCharacter();
          eeprom0.writeByte(eeAddress, data);
          break;

        case '2':
          data = eeprom0.readByte(eeAddress);
          TerminalHelper.announceCharacter((char)data);
          break;

        case '3':
          TerminalHelper.getString(characters);
          eeprom0.writeStringToEeprom(eeAddress,characters);
          break;

        case '4':
          numChars = TerminalHelper.getCharCount();
          characters.clear();
          eeprom0.readStringIntoBuffer(eeAddress, numChars, characters);
          TerminalHelper.displayCharacters(characters);
      }   // End switch
    }     // End while(true)
  }       // End main
}         // End class