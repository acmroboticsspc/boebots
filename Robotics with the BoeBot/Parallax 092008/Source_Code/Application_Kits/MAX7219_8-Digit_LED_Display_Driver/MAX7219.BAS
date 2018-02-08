' Program: MAX7219.BAS
' This program controls the MAX7219 LED display driver. It demonstrates
' the basics of communicating with the 7219, and shows a convenient
' method for storing setup data in tables. To demonstrate practical 
' application of the 7219, the program drives a 5-digit display to 
' show the current value of a 16-bit counter (0-65535). The subroutines
' are not specialized for counting; they can display _any_ 16-bit 
' value on the LCDs. (A specialized counting routine would be faster, 
' since it would only update the digits necessary to maintain the 
' count; however, it wouldn't be usable for displaying arbitrary 
' 16-bit values, like the results of Pot, Pulsin, or an A-to-D
' conversion). 

' Hardware interface with the 7219:
SYMBOL DATA_n  = 7	' Bits are shifted out this pin # to 7219.
SYMBOL DATA_p  = pin7	' "    "   "       "        ". 
SYMBOL CLK     = 5	' Data valid on rising edge of this clock pin.
SYMBOL Load    = 6	' Tells 7219 to transfer data to LEDs.

' Register addresses for the MAX7219. To control a given attribute
' of the display, for instance its brightness or the number shown 
' in a particular digit, you write the register address followed 
' by the data. For example, to set the brightness, you'd send 
' 'brite' followed by a number from 0 (off) to 15 (100% bright). 
SYMBOL dcd 	= 9	' Decode register; a 1 turns on BCD decoding. 
SYMBOL brite	= 10	' "       "   " intensity register.
SYMBOL scan	= 11	' "       "   " scan-limit register.
SYMBOL switch	= 12	' "       "   " on/off register.
SYMBOL test	= 15	' Activates test mode (all digits on, 100% bright)

' Variables used in the program. 
SYMBOL max_dat = b11	' Byte to be sent to MAX7219. 
SYMBOL index   = b2	' Index into setup table. 
SYMBOL nonZ    = bit1	' Flag used in blanking leading zeros. 
SYMBOL clocks  = b3	' Bit counter used in Max_out. 
SYMBOL dispVal = w2	' Value to be displayed on the LEDs. 
SYMBOL decade  = w3	' Power-of-10 divisor used to get decimal digits.
SYMBOL counter = w4	' The value to be displayed by the demo. 

' The program begins by setting up all pins to output low, matching
' the state established by the pulldown resistors. 
let port = $FF00	' Dirs = $FF (all outputs) and Pins = 0 (low). 

' Next, it initializes the MAX7219. A lookup table is convenient way 
' to organize the setup data; each register address is paired with 
' its setting data. The table sets the scan limit to 4 (5 digits, 
' numbered 0-4); brightness to 3; BCD decoding to the lower 5 digits
' (the only ones we're displaying), and switches the display on. The 
' MAX7219 expects data in 16-bit packets, but our lookup table holds
' a series of 8-bit values. That's why the loop below is designed to 
' pulse the Load line _every_other_ byte transmitted. 
for index = 0 to 7		' Retrieve 8 items from table. 
  lookup index,(scan,4,brite,3,dcd,$1F,switch,1),max_dat
  gosub Max_out
  let bit0 = index & 1		' Look at lowest bit of index. 
  if bit0 = 0  then noLoad
  pulsout Load,1		' If it's 1, pulse Load line. 
NoLoad:				' Else, don't pulse. 
next				' Get next item from table. 

' ====================== MAIN PROGRAM LOOP ==========================
' Now that the MAX7219 is properly initialized, we're ready to send it
' data. The loop below increments a 16-bit counter and displays it on 
' the LEDs connected to the MAX. Subroutines below handle the details
' of converting binary values to binary-coded decimal (BCD) digits and 
' sending them to the MAX. 
Loop: 
  let dispVal = counter
  gosub MaxDisplay
  let counter = counter+1
goto loop

' ========================= SUBROUTINES ============================
' The MAX7219 won't accept a number like "2742" and display it on 
' the LEDs. Instead, it expects the program to send it individual 
' digits preceded by their position on the display. For example, 
' "2742" on a five-digit display would be expressed as: 
' "digit 5: blank; digit 4: 2; digit 3: 7; digit 2: 4; digit 1: 2"
' The routine MaxDisplay below does just that, separating a value
' into individual digits and sending them to the MAX7219. If the
' lefthand digits are zero (as in a number like "102") the 
' routine sends blanks, not zeros until it encounters the first 
' non-zero digit. This is called "leading-zero blanking."
MaxDisplay:
let decade = 10000		' Start with highest digit first. 
let nonZ = 0			' Reset non-zero digit flag. 
for index = 5 to 1 step -1	' Work from digit 5 to digit 1. 
  let max_dat = index		' Send the digit address. 
  gosub Max_out
  let max_dat = dispVal/decade	' Get the digit value (0-9). 
  if max_dat = 0 then skip	' If digit <> 0 then nonZ = 1. 
  let nonZ = 1			' If a non-zero digit has already 
skip: 				' ..come, or the current digit is not 
  if nonZ = 1 OR max_dat <> 0 OR index = 1 then skip2	'..0, or the 
  let max_dat = 15		'.._only_ digit is 0, send the digit,
skip2:				'..else send a blank. 
  gosub Max_out			' Send the data in max_dat and 
  pulsout Load,1		' ..pulse the Load line. 
  let dispVal = dispVal//decade	' Get the remainder of value/decade. 
  let decade = decade/10	' And go to the next smaller digit. 
 next				' Continue for all 5 digits. 
return				' Done? Return. 

' Here's the code responsible for sending data to the MAX7219. It
' sends one byte at a time of the 16 bits that the MAX expects. The 
' program that uses this routine is responsible for pulsing the 
' Load line when all 16 bits have been sent. To talk to the MAX7219, 
' Max_out places the high bit (msb) of max_dat on DATA_p, the data pin,
' then pulses the clock line. It shifts the next bit into position by
' multiplying max_dat by 2. It repeats this process eight times. 
' In order to avoid hogging the bit-addressable space of w0, the 
' routine uses a roundabout way to read the high bit of max_dat: if 
' max_dat < $80 (%10000000) then the high bit must be 0, so a 0 
' appears on DATA_p. If max_dat >= to $80, then a 1 appears on DATA_p. 
Max_out:
for clocks = 1 to 8		' Send eight bits. 
  let DATA_p = 0		' If msb of max_dat = 1, then let 
  IF max_dat < $80 then skip3	'..DATA_p = 1, else DATA_p = 0. 
  let DATA_p = 1
skip3:
  pulsout CLK,1			' Pulse the clock line. 
  let max_dat = max_dat * 2	' Shift max_dat one bit to the left. 
next				' Continue for eight bits. 
return				' Done? Return. 
