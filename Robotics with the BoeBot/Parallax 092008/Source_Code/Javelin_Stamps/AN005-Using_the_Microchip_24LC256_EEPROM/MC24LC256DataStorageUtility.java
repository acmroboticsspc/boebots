import stamp.core.*;
import stamp.peripheral.memory.eeprom.*;

/*
 * This utility will allow you to read and write to various 24LC256 chips.
 * You can use this class to better understand how to interact with this chip.
 * Version 1.0
 */
public class MC24LC256DataStorageUtility{
  static char keyboardChar;
  static StringBuffer keyboardMsg = new StringBuffer(30);

  public static void main() {
    final int sdaPin = CPU.pin4;       // Javelin's I/O pin P4, used for data
    final int sclPin = CPU.pin5;       // Javelin's I/O pin P5, used for timing
    final char CLS = '\u0010';         // Clear Screen Code
    int dev = 0;                       // Device ID (default zero)
    int address = 0;                   // EEPROM address to read/write
    MC24LC256 ee = new MC24LC256(sdaPin, sclPin);    // Create MC24LC256 object
    boolean test;                                    // validate read/write
    int n=0,x=0;                                     // misc

    // Display menu
    while (true){
      System.out.print(CLS);
      System.out.print("1) Current device: ");
      System.out.println(dev);
      System.out.print("2) Current EEPROM address: ");
      System.out.println(address);
      System.out.println("\n3) Write a numeric byte");
      System.out.println("4) Read a numeric byte");
      System.out.println("5) Write a string");
      System.out.println("6) Read a string");
      System.out.print("\n\nEnter selection: ");

      // Select current device
      switch (Terminal.getChar()){
        case '1':
          System.out.print("\nEnter device you wish to select: ");
          x=Terminal.getChar();
          x-=48;
          if ((x>=0)&&(x<=7)) dev=x;
          else {
            System.out.println("\nMust select a device between 0-7, device must be currently connected");
            CPU.delay(30000);
          }//end else
          break;

        // Select current EEPROM address
        case '2':
          System.out.print("\nEnter EEPROM address (0-32767): ");
          test=true;
          x=getInt();
          if ((x>=0)&&(x<=32767)) address=x;
          else {
            System.out.println("\nAddress must be between 0-32767");
            CPU.delay(30000);
          }// end else
          break;

        // Write a byte
        case '3':
          System.out.print("\nEnter byte (0-255) to write to EEPROM: ");
          x=getInt();
          if ((x>=0)&&(x<=255)) ee.writeOne(dev,address,(byte)x);
          else {
            System.out.println("\nValue must be between 0-255");
            CPU.delay(30000);
          }// end else
          break;

        // Read a byte
        case '4':
          try {
            x = ee.readRandom(dev,address);
            System.out.print("\nData read from device ");
            System.out.print(dev);
            System.out.print(" at address ");
            System.out.print(address);
            System.out.print(" is: ");
            System.out.println(x);
          }// end try
          catch (MC24LC256BadReadException bre) {
            System.out.println("Bad Read Detected Here");
          }// end catch
          CPU.delay(30000);
          break;

        // Write a string of characters
        case '5':
          System.out.print("\nEnter message to be strored: ");
          keyboardMsg.clear();
          while ( (keyboardChar = Terminal.getChar()) != '\r' ) {
            keyboardMsg.append(keyboardChar);
          }// end while
          if (keyboardMsg.length()+address>32767) {
            System.out.println("\nThe message you have entered exceeds the end of the EEPROM");
            CPU.delay(30000);
          }// end if
          else
            for (x=0;x<keyboardMsg.length();x++){
              ee.writeOne(dev,address+x,(byte)keyboardMsg.charAt(x));
              CPU.delay(50);
            }// end for
          break;

        // Read a string of characters
        case '6':
          System.out.print("\nPlease enter the length of the message you wish to extract: ");
          x=getInt();
          System.out.println("\n");
          for (int y=0;y<x;y++){
            try {
              n = ee.readRandom(dev,address+y);
            }// end try
            catch (MC24LC256BadReadException bre) {
              System.out.println("\nBad Read Detected Here");
              CPU.delay(30000);
            }// end catch

            if ((n>31)&&(n<127))
              System.out.print((char)n);
            else System.out.print("<<NOT-PRINTABLE>>");

          }// end for
            System.out.println("\n\nPress ENTER to continue");
            Terminal.getChar();

      }// end switch
    }// end while
  }// end method: main

  public static int getInt() {
    keyboardMsg.clear();
    while ( (keyboardChar = Terminal.getChar()) != '\r' ) {
      keyboardMsg.append(keyboardChar);
    }// end while
    return Integer.parseInt(keyboardMsg);
  }// end method: getInt

}// end class: MC24LC256DataStorageUtility