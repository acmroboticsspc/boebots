/* Format Example to demonstrate justification within a fixed width.
 *
 * For more information see AppNote012 from Parallax Inc.
 * Version 1.0 Feb. 24th, 2003 (an012)
 */

import stamp.util.text.*;

public class FormatExample{

  public static void main() {

    // Integer formatting of positive integer value.
    Format.printf("| %-15s |","Integer");
    Format.printf("\n| %15i |",32434);           // right justified
    Format.printf("\n| %-15i |",32434);          // left justified
    Format.printf("\n| %015i |",32434);          // with leading zeros
    Format.printf("\n| %-015i |",32434);         // with trailing zeros

    // Integer formatting of negative integer value.
    Format.printf("\n| %15i |",-120);
    Format.printf("\n| %-15i |",-120);
    Format.printf("\n| %015i |",-120);
    Format.printf("\n| %-015i |",-120);

    // Integer formatting of short integer value (output will be signed).
    Format.printf("\n| %15i |",(short)45321);
    Format.printf("\n| %-15i |",(short)45321);
    Format.printf("\n| %015i |",(short)45321);
    Format.printf("\n| %-015i |",(short)45321);

    // Integer formatting of character (will be converted to ASCII).
    Format.printf("\n| %15i |",'A');
    Format.printf("\n| %-15i |",'B');
    Format.printf("\n| %015i |",'C');
    Format.printf("\n| %-015i |",'D');

    // Character formatting of character.
    Format.printf("\n\n| %-15s |","Character");
    Format.printf("\n| %15c |",'A');
    Format.printf("\n| %-15c |",'B');
    Format.printf("\n| %015c |",'C');
    Format.printf("\n| %-015c |",'D');

    // String formatting of string.
    Format.printf("\n\n| %-15s |","String");
    Format.printf("\n| %15s |","Sammy Sam");
    Format.printf("\n| %-15s |","Sammy Sam");
    Format.printf("\n| %015s |","Sammy Sam");
    Format.printf("\n| %-015s |","Sammy Sam");

    // Hexadecimal formatting of integer.
    Format.printf("\n\n| %-15s |","Hexadecimal");
    Format.printf("\n| %15x |",14990);
    Format.printf("\n| %-15x |",14990);
    Format.printf("\n| %015x |",14990);
    Format.printf("\n| %-015x |",14990);

    // Unsinged formatting of short.
    Format.printf("\n\n| %-15s |","Unsigned");
    Format.printf("\n| %15u |",(short)54937);
    Format.printf("\n| %-15u |",(short)54937);
    Format.printf("\n| %015u |",(short)54937);
    Format.printf("\n| %-015u |",(short)54937);

    // Octal formatting of short.
    Format.printf("\n\n| %-15s |","Octal");
    Format.printf("\n| %15o |",(short)54937);
    Format.printf("\n| %-15o |",(short)54937);
    Format.printf("\n| %015o |",(short)54937);
    Format.printf("\n| %-015o |",(short)54937);

    // Binary formatting of integer.
    Format.printf("\n\n| %-25s |","Binary");
    Format.printf("\n| %25b |",30433);
    Format.printf("\n| %-25b |",30433);
    Format.printf("\n| %025b |",30433);
    Format.printf("\n| %-025b |",30433);

    // Binary formatting of short.
    Format.printf("\n\n| %25b |",(short)45321);
    Format.printf("\n| %-25b |",(short)45321);
    Format.printf("\n| %025b |",(short)45321);
    Format.printf("\n| %-025b |",(short)45321);

    // Integer field formatting
    Format.printf("\n\n| %-21s |","Signed Formatting");
    Format.printf("\n| %-21s |\n","Before        After");

    int [] x = {-12, 145, 1356, -31356, -10112};

    for(int j=0;j<5;j++){
      int i=x[j];
      Format.printf("|%7d",i);                   // fixed min width of 7
      Format.printf(" --> %c",i<0?'-':'+');      // determine sign
      Format.printf("%-9d |\n",i<0?-i:i);        // fixed min width of 9
    }

    for(int j=0;j<5;j++){
      int i=x[j];
      Format.printf("|%7d",i);
      Format.printf(" --> %c",i<0?'-':'+');
      Format.printf("%-09d |\n",i<0?-i:i);       // pad with zeros
    }

  }
}