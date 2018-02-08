package examples.protocol.i2cprimer;

/**
 * This class simplifies example programs in AppNote003 I2C Primer - EEPROM
 * Example by handling all user communication tasks that involve the Messages
 * from Javelin window.
 */

import stamp.core.*;

public class TerminalHelper {

  private static StringBuffer keypadText = new StringBuffer(5);
  private static String [] choices = {"1 - Write character",
                                      "2 - Read character",
                                      "3 - Write string",
                                      "4 - Read string"};

  public static int menu(int start, int end) {
    start--;
    end--;
    System.out.println("--------------------------------");
    for(int i = start; i <= end; i++){
      System.out.println(choices[i]);
    }
    System.out.println("--------------------------------");
    System.out.print("Select EEPROM Option:        ");
    int c = Terminal.getChar();
    System.out.println(" ");
    return c;
  }

  public static int getAddress(){
    char keyboardKey;
    System.out.print("Enter address (0 to 32767):  ");
    keypadText.clear();
    while ( (keyboardKey = Terminal.getChar()) != '\r' ) {
      keypadText.append(keyboardKey);
    }
    System.out.print("\n\r");
    return Integer.parseInt(keypadText);
  }

  public static char getCharacter(){
    System.out.print("Press any character key:     ");
    char c = Terminal.getChar();
    System.out.println("\n\r");
    return c;
  }

  public static void announceCharacter(char character){
    System.out.print("The characer is:             ");
    System.out.println(character);
    System.out.print("\n\r");
  }

  public static void getString(StringBuffer keyboardText){
    char keyboardKey;
    System.out.println("Enter a string of up to 128 ");
    System.out.println("characters. Then press Enter: ");
    keyboardText.clear();
    while ( (keyboardKey = Terminal.getChar()) != '\r' ) {
      keyboardText.append((char)keyboardKey);
    }
    System.out.println("\n\r");
  }

  public static int getCharCount(){
    char digit;
    System.out.println("Choose number of ");
    System.out.print("characters (up to 128):      ");
    keypadText.clear();
    while ( (digit = Terminal.getChar()) != '\r' ) {
      keypadText.append(digit);
    }
    System.out.println(" ");
    return Integer.parseInt(keypadText);
  }

  public static void displayCharacters(StringBuffer characters){
    System.out.print(characters.toString());
    System.out.println("\n\r");
  }
}