' Program Listing 19.1: Using the TSM6755 with BS1
' Program: IIIV_LED.BAS
' This program controls TSM6x55 LED displays. It demonstrates the
' basics of communicating with the TSM6x55s by displaying the
' value of a counter on the LEDs.

' Hardware interface with the TSM6x55:
SYMBOL DATA_n 	= 1 	' Bits are shifted out this pin # to display.
SYMBOL DATA_p 	= pin1 	' " " " " ".
SYMBOL CLK 	= 2 	' Data valid on rising edge of this clock pin.
SYMBOL Enable 	= 0 	' Activates TSM6x55 to accept data.

' Variables used in the program.
SYMBOL initLED = bit0 	' Flag to trigger initialization of display.
SYMBOL colon 	= bit1 	' Flag to turn on colon in the middle of display.
SYMBOL bitPat 	= b1 	' Byte to be sent to the display.
SYMBOL clocks 	= b2 	' Bit counter used to clock out bits.
SYMBOL digit 	= b3 	' Loop counter for clocking out bytes.
SYMBOL DP 	= b4 	' Position of decimal point.
SYMBOL dispVal = w3 	' Value to be displayed on the LEDs.
SYMBOL counter = w4 	' Counter for demo.

' The program begins by clearing all pins except the TSM6x55 enable
' to 0, and setting their I/O direction to output. It then writes 0s
' to the TSM6x55's internal registers to clear out any garbage that
' may be left from a previous write to the display. If this were not
' done, any 1s left in the display registers would cause the display
' to load newly arrived data before it should, garbling the display.
let port = $FF01 	' Dirs = $FF (all outputs) and Pins = 1 (low).
let initLED = 1 	' Set up to initialize the display.
gosub IIIV_write 	' Clear display registers.
let initLED = 0 	' Switch to normal operation.
let colon = 0 		' Turn off the colon.

' The decimal-point variable serves three functions: (1) If it's
' in the range of 0 to 3, it turns on the decimal point of the
' corresponding digit (numbered right to left). (2) If it's
' in the range of 1 to 3, it also turns off leading-zero blanking
' of the display, allowing numbers like "0.123" to be displayed.
' (3) If DP is 0 or greater than 3, leading-zero blanking is on,
' so that numbers like "26" don't display as "0026". When DP is 0,
' the rightmost decimal point is on _with_ leading-zero blanking,
' so that single-digit numbers display like so: "4." For our demo,
' we'll turn off the decimal points and turn on leading-zero blanking.

let DP = 255 ' No decimal point; no leading zeros.

' ====================== MAIN PROGRAM LOOP ==========================
' Now that the display is properly initialized, we're ready to send it
' data. The loop below increments a 16-bit counter and displays the
' lower 4 digits on the TSM6x55. A subroutine takes care of converting
' the value into digits, suppressing leading zeros, and sending data
' to the display.
 
Loop:
  let dispVal = counter 	' Copy counter into dispVal.
  gosub IIIV_write 		' Write it to the display.
  let counter = counter+1 	' Increment the counter.
  pause 200 			' Slow things down a bit.
goto loop 			' Do it again.

' ===================== TSM 6x55 SUBROUTINE ===========================
' This routine converts the value in dispVal into a series of bit
' patterns that light up the appropriate LEDs on the display to
' show individual digits. It sends these bytes to the display via
' clocked (synchronous) output in which a bit is placed on the data
' line, then the clock line is pulsed to shift that bit into the display.
IIIV_write:
  low enable 			' Activate the display.
  high DATA_n 			' Send a start bit (1) to the display.
  pulsout CLK,1 		' Clock out the start bit.
  for digit = 0 to 3 	      	' Convert & send digits 0 - 3 (1s to 1000s).
  bitPat = dispVal//10 	' Get the ones digit of dispVal.
  if DP < 4 AND DP > 0 then noZblank ' No 0 blanking if 0 < DP < 4.

' The line below blanks 0s in the leftmost positions of the display.
' A digit is defined as a leading zero by these three rules:
' (1) The digit must be a 0 (bitPat = 0), and
' (2) It must be the leftmost digit of the number (dispVal = 0), and
' (3) It must not be the only digit of the number (digit<>0).
' If a digit meets these three rules, then it is changed from 0 to blank.
  if bitPat = 0 AND dispVal = 0 AND digit <> 0 then blank

noZblank: 			' Look up LED pattern matching digit value.
  lookup bitPat,(111,40,93,124,58,118,119,44,127,62),bitPat

' To turn on the decimal point for a particular digit, its bit pattern
' must be ORed with 128 (%10000000). So the two lines below are meant
' to work as: "If DP = digit then bitPat = bitPat OR 128." The backwards
' logic is required because PBASIC can only go to a label as the
' outcome of if/then. Below are several more examples of negative
' logic to work around this limitation.
  if DP <> digit then skip0
  bitPat = bitPat | 128 	' Turn on bit 7 of LED (decimal point).

skip0:
  if initLED = 0 then skip1 	' "If initLED = 1 then bitPat = 0"
blank:
  bitPat = 0 			' Send all zeros to display.
skip1:
  for clocks = 1 to 8 		' Send eight bits.
   let DATA_p = 0 		' If msb of bitPat = 1, then let
   IF bitPat < $80 then skip2 '..DATA_p = 1, else DATA_p = 0.
   let DATA_p = 1
skip2:
   pulsout CLK,1 		' Pulse the clock line.
   let bitPat = bitPat * 2 	' Shift bitPat one bit to the left.
  next clocks 			' Continue for eight bits.
dispVal = dispVal/10 		' Divide dispVal by 10 to get next digit.
next digit
DATA_p = colon 		' Now put the colon bit on the data line.
pulsout CLK,1 			' And clock it out to the display.
pulsout CLK,1 			' This takes 3 clocks; 2 for the LEDs of
pulsout CLK,1 			' the colon, plus 1 to load the display.
high enable 			' Disable the TSM6x55.
return 				' Return to program.
