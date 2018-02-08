package examples.protocol.i2cprimer;

/**
 * See AppNote003 I2C Primer - EEPROM Example.pdf for information
 * on this example program.
 */

import stamp.core.*;
import stamp.protocol.I2C;

public class MC24LC32Demo {

  final public static int SDAPin = CPU.pin6;
  final public static int SCLPin = CPU.pin7;
  public static I2C i2cbus = new I2C(SDAPin, SCLPin);

  final public static int READ_BIT      = 0x0001;
  final public static int WRITE_BIT     = 0x0000;
  final public static int CONTROL_CODE  = 0x00A0;
  final public static int SLAVE_ADDRESS = 0x0000;

  public static StringBuffer characters = new StringBuffer(128);
  public static int menuChoice, eeAddress, data, controlByte, numChars;

  public static void main() {

    while(true){

      menuChoice = TerminalHelper.menu(1,4);
      eeAddress = TerminalHelper.getAddress();

      switch (menuChoice){

        // Uses code discussed in Example-1 section.
        case '1':
          data = TerminalHelper.getCharacter();
          controlByte = CONTROL_CODE | (SLAVE_ADDRESS << 1) | WRITE_BIT;
          i2cbus.start();
          i2cbus.write(controlByte);
          i2cbus.write(eeAddress >> 8);
          i2cbus.write(eeAddress);
          i2cbus.write(data);
          i2cbus.stop();
          break;

        // Uses code discussed in Example-2 section.
        case '2':
          i2cbus.start();
          controlByte = CONTROL_CODE | (SLAVE_ADDRESS << 1) | WRITE_BIT;
          i2cbus.write(controlByte);
          i2cbus.write(eeAddress >> 8);
          i2cbus.write(eeAddress);
          controlByte = CONTROL_CODE | (SLAVE_ADDRESS << 1) | READ_BIT;
          i2cbus.start();
          i2cbus.write(controlByte);
          data = i2cbus.read(false);
          i2cbus.stop();
          TerminalHelper.announceCharacter((char)data);
          break;

        // Uses code discussed in Example-3 section.
        case '3':
          TerminalHelper.getString(characters);
          int controlByte = CONTROL_CODE | (SLAVE_ADDRESS << 1) | WRITE_BIT;
          i2cbus.start();
          i2cbus.write(controlByte);
          i2cbus.write(eeAddress >> 8);
          i2cbus.write(eeAddress);
          for(int i = 0; i < characters.length(); i++){
            if((eeAddress % 32) == 0 ){
              i2cbus.stop();
              do{
                i2cbus.start();
              } while(!i2cbus.write(controlByte));
              i2cbus.write(eeAddress >> 8);
              i2cbus.write(eeAddress);
            }
            i2cbus.write(characters.charAt(i));
            eeAddress ++;
          }
          i2cbus.stop();
          break;

        // Uses code discussed in Example-4 section.
        case '4':
          numChars = TerminalHelper.getCharCount();
          characters.clear();
          i2cbus.start();
          controlByte = CONTROL_CODE | (SLAVE_ADDRESS << 1) | WRITE_BIT;
          i2cbus.write(controlByte);
          i2cbus.write(eeAddress >> 8);
          i2cbus.write(eeAddress);
          controlByte = CONTROL_CODE | (SLAVE_ADDRESS << 1) | READ_BIT;
          i2cbus.start();
          i2cbus.write(controlByte);
          for(int i = 0; i < numChars; i++){
            if(i < (numChars - 1)){
              characters.append((char)i2cbus.read(true));
            }
            else{
              characters.append((char)i2cbus.read(false));
            }
          }
          TerminalHelper.displayCharacters(characters);
      }   // End switch
    }     // End while(true)
  }       // End main
}         // End class