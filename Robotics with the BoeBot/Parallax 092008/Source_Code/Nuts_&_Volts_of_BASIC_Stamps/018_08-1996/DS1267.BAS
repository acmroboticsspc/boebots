' Program Listing 18.1. DS1267 Demo Program for BS1
' Program: DS1267.BAS
' This program controls the DS1267 digital potentiometer chip.
' This chip is very versatile as a digital-to-analog converter.
' It can output a variable voltage, can adjust current (up to
' 1 mA), or it can serve as the variable resistance in a
' resistor-capacitor timing circuit such as a timer or oscillator.

' Hardware interface with the DS1267:

SYMBOL RST = 0 	' Pin number of reset connection.
SYMBOL CLK = 1 	' Pin number of clock connection.
SYMBOL DQ_n = 2 	' Pin number of data (DQ) connection.
SYMBOL DQ = pin2 	' Pin variable of data connection.

' Variables used by the program:

SYMBOL DSpot0 = b2 	' Variable for setting of pot 0.
SYMBOL DSpot1 = b3 	' Variable for setting of pot 1.
SYMBOL DSpots = w1 	' Word variable holding both pot values.
SYMBOL DSxfer = w0 	' Word variable for transferring pot values.
SYMBOL clocks = b4 	' Index variable for counting clock pulses.

let dirs = %00000111 	' Output pins 0,1,2 to DS1267.
' The loop below increments pot 1 in 10-unit steps from 0 to 255.
' by subtracting pot 1 value from 0 and writing that to pot 0,
' it makes pot 0 the inverse of pot 1. In other words, as pot 1
' increases, pot 0 decreases.
Begin:
  for DSpot1 = 0 to 255 step 10 ' Pot 1 increasing: 0 to 255.
  let DSPot0 = 0 - DSPot1 ' Pot 0 decreasing.
  let DSxfer = DSpots 	' Store data in transfer variable.
  gosub outPot 	' Send to the pots.
next 			' Next value for pots.
goto Begin 		' Repeat endlessly.

'====================DS1267 SUBROUTINE=====================
' This code shifts data out to the DS1267. Because the shift
' process causes the data to be lost, we use a copy of the
' data to perform the transfer (DSxfer). The DS1267 expects
' a total of 17 bits: first the stack-select bit, which
' selects wiper 0 or wiper 1 for connection to Sout; then
' 16 bits representing the 8-bit values of pots 1 and 0,
' most-significant bit (msb) first.
outPot:
high RST 		' Take RST high to start transfer.
low DQ_n 		' Set stack-wiper to 0.
pulsout CLK,10 	' Pulse the clock line.
for clocks = 0 to 15 	' Now send 16 data bits.
  let DQ = bit15 	' Put msb on data line.
  pulsout CLK,1 	' Pulse the clock
  let DSxfer = DSxfer * 2 ' Shift 1 bit to the left.
next 			' Repeat for all 16 bits.
low RST 		' Take RST low to finish transfer.
return 			' Return to program.
