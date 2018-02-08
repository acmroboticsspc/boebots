/* Format Demo which demonstrates all of the methods available to you in the
 * Format library.  For overloaded methods please refer to the JavaDoc.
 *
 * For more information see AppNote012 from Parallax Inc.
 * Version 1.0 Feb. 24th, 2003 (an012)
 */

import stamp.util.text.*;
import stamp.core.*;

public class FormatDemo{

  public static void main() {

    int intX=32455;          //dec +32455, hex 7EC7, octal 077307, bin 0111111011000111
    byte byteX=-23;          //dec -00023, hex FFE9, octal 177751, bin 1111111111101001
    char charC='X';          //dec +00088, hex 0058, octal 000130, bin 0000000001011000
    int shortX=(short)42566; //dec -22970, hex A646, octal 123106, bin 1010011001000110
    char[] cBuffer = new char[150];
    int cIndex = 0;
    int[] data = new int[1];
    char[] cBuf = new char[20];


    // Acceptable format specifiers for printf, bprintf, and sprintf are:
    // %c,%d,%i,%b,%o,%u,%x and will work with: byte, int, char & short
    // %s will work with String or char[]

    // Acceptable format specifiers for sscanf, and bscanf are:
    // %c,%d,%i,%b,%o,%u,%x and will work with: int
    // %s will work with char[]

    // Method: printf
    // Print a formatted string directly to the mesage window.
    System.out.println("TEST FOR: printf");
    System.out.println("\nintX=32455, dec +32455, hex 7EC7, unsigned 32455," +
    " octal 077307, bin 0111111011000111"); // using + between constants is OK
    Format.printf("Character format specifier %%c: %c for int\n",intX);
    Format.printf("Decimal format specifier %%d  : %d for int\n",intX);
    Format.printf("Integer format specifier %%i  : %i for int\n",intX);
    Format.printf("Binary format specifier %%b   : %b for int\n",intX);
    Format.printf("Octal format specifier %%o    : %o for int\n",intX);
    Format.printf("Unsigned format specifier %%u : %u for int\n",intX);
    Format.printf("Hex format specifier %%x      : %x for int\n",intX);

    System.out.println("\nbyteX=-23, dec -00023, hex FFE9, unsigned 65513," +
    " octal 177751, bin 1111111111101001"); // using + between constants is OK
    Format.printf("Character format specifier %%c: %c for byte\n",byteX);
    Format.printf("Decimal format specifier %%d  : %d for byte\n",byteX);
    Format.printf("Integer format specifier %%i  : %i for byte\n",byteX);
    Format.printf("Binary format specifier %%b   : %b for byte\n",byteX);
    Format.printf("Octal format specifier %%o    : %o for byte\n",byteX);
    Format.printf("Unsigned format specifier %%u : %u for byte\n",byteX);
    Format.printf("Hex format specifier %%x      : %x for byte\n",byteX);

    System.out.println("\ncharC='X', dec +00088, hex 0058, unsigned 00088," +
    " octal 000130, bin 0000000001011000"); // using + between constants is OK
    Format.printf("Character format specifier %%c: %c for char\n",charC);
    Format.printf("Decimal format specifier %%d  : %d for char\n",charC);
    Format.printf("Integer format specifier %%i  : %i for char\n",charC);
    Format.printf("Binary format specifier %%b   : %b for char\n",charC);
    Format.printf("Octal format specifier %%o    : %o for char\n",charC);
    Format.printf("Unsigned format specifier %%u : %u for char\n",charC);
    Format.printf("Hex format specifier %%x      : %x for char\n",charC);

    System.out.println("\nshortX=42566, dec -22970, hex A646, unsigned 42566," +
    " octal 123106, bin 1010011001000110"); // using + between constants is OK
    Format.printf("Character format specifier %%c: %c for short\n",shortX);
    Format.printf("Decimal format specifier %%d  : %d for short\n",shortX);
    Format.printf("Integer format specifier %%i  : %i for short\n",shortX);
    Format.printf("Binary format specifier %%b   : %b for short\n",shortX);
    Format.printf("Octal format specifier %%o    : %o for short\n",shortX);
    Format.printf("Unsigned format specifier %%u : %u for short\n",shortX);
    Format.printf("Hex format specifier %%x      : %x for short\n",shortX);

    System.out.println("\nstring STRING and character array CHAR[]");
    Format.printf("String format specifier %%s: %s for string\n","STRING");
    Format.printf("String format specifier %%s: %s for char[]\n","CHAR[]".toCharArray());

    // %d specifier allows you to format an integer.
    Format.printf("\nPad with zeros, left justify %-08d \n",32434);
    Format.printf("Pad with zeros, right justify %08d \n",32434);
    Format.printf("Set Max width to 4: %-02.4d \n",32434);

    // Method: sprintf
    // The sprintf method will print a formatted string to a character buffer.
    // Character buffer will be reset on each sprintf call.
    // Acceptable format specifiers for:
    // byte, int, char & short are %c,%d,%i,%b,%o,%u,%x.
    // String is %s.
    System.out.println("\nTEST FOR: sprintf");
    Format.sprintf(cBuffer,"Decimal format specifier: %d for int\n",intX);
    CPU.message(cBuffer,150);
    Format.sprintf(cBuffer,"Decimal format specifier: %d for byte\n",byteX);
    CPU.message(cBuffer,150);
    Format.sprintf(cBuffer,"Decimal format specifier: %d for char\n",charC);
    CPU.message(cBuffer,150);
    Format.sprintf(cBuffer,"Decimal format specifier: %d for short\n",shortX);
    CPU.message(cBuffer,150);

    // Method: sscanf
    // The sscanf method will scan a sprintf buffer (character array)
    // and extract the matching format specifier (%c,%d,%i,%b,%o,%u,%x,%s).
    // The information must be typed exactly as used in the sprintf command.
    System.out.println("\nTEST FOR: sscanf");
    Format.sprintf(cBuffer,"padded with %05d zeroes\n",4);  // Fill array
    Format.sscanf(cBuffer,"padded with %5d zeroes\n",data); // Scan for %5d field
    System.out.println(data[0]);

    // Method:bprintf
    // The bprintf method will print a formatted string to a character buffer
    // like sprintf while maintaining the buffer index.
    System.out.println("\nTEST FOR: bprintf");
    cIndex = 0;
    cIndex = Format.bprintf(cBuffer,cIndex,"this is string %d of many\n",-34);
    cIndex = Format.bprintf(cBuffer,cIndex,"padded with %05d zeroes\n",4);
    cIndex = Format.bprintf(cBuffer,cIndex,"single character '%c' in place\n",'A');
    cIndex = Format.bprintf(cBuffer,cIndex,"get a %s to print\n","STRING");
    // Print this buffer to the terminal window
    CPU.message(cBuffer,cIndex);

    // Method: bscanf
    // The bscanf is a (buffered) version of scanf, it will scan a formatted
    // string while maintaining a buffer index.
    System.out.println("\nTEST FOR: bscanf");
    cIndex = 0;
    cIndex = Format.bscanf(cBuffer,cIndex,"this is string %d of many\n",data);
    System.out.println(data[0]);
    cIndex = Format.bscanf(cBuffer,cIndex,"padded with %5d zeroes\n",data);
    System.out.println(data[0]);
    cIndex = Format.bscanf(cBuffer,cIndex,"single character '%c' in place\n",data);
    System.out.println((char)data[0]);
    cIndex = Format.bscanf(cBuffer,cIndex,"get a %s to print\n",cBuf);
    CPU.message(cBuf,cBuf.length);

    //Useful conversion methods

    // Method: itoa
    // Convert signed integer to signed decimal string.
    System.out.println("\n\nTEST FOR: itoa");
    Format.itoa(-32453,cBuf);
    CPU.message(cBuf,cBuf.length);

    // Method: atoi
    // Convert signed decimal string to signed integer.
    System.out.println("\n\nTEST FOR: atoi");
    intX=Format.atoi(cBuf);
    System.out.println(intX);

    // Helpful methods

    // Method: strLen
    // Get length of null terminated string in character array.
    System.out.print("\nLength of null terminated character array for cBuf: ");
    System.out.println(Format.strLen(cBuf));
    System.out.print("Value of cBuf: ");
    CPU.message(cBuf,cBuf.length);

    // Method: reverse
    // Reverse a character array in place
    System.out.print("\nReverse the characters in cBuf: ");
    Format.reverse(cBuf);
    CPU.message(cBuf,cBuf.length);

    // Method: isDigit
    // Test if character is an ASCII digit
    intX=52;
    if (Format.isDigit(intX)) System.out.println("\nYes, intX is an ASCII digit");

    System.out.println("\nEnd of Demo");

  }
}