' Program Listing 29.1: LCDTEMP.BAS (Measure and display the temperature)
' This program uses an ADC0831 to measure the output voltage from
' an LM34 or LM35 temperature sensor. With Vref adjusted to 2.55V,
' The value returned by the ADC is the temperature in degrees--
' no unit conversion required. The program continuously displays
' the temperature on a serial LCD module connected to pin7. The
' prototype was constructed with a BS1/LCD kit from Scott Edwards
' Electronics. The kit integrates a BS1 computer, serial LCD driver,
' and 2x16 supertwist LCD module into a single, compact package.
' The product manual, catalog, and other items of interest are
' available from ftp://ftp.nutsvolts.com/pub/nutsvolts/scott.
' View the file index.txt for a list of available files.

SYMBOL CS = 0 			' ADC chip-select (activates ADC0831).
SYMBOL AD = pin1 		' Data output of ADC.
SYMBOL CLK = 2 		' Clock input to ADC.
SYMBOL S_out = 7 		' Serial output to LCD.
SYMBOL data = b0 		' 8-bit ADC result.
SYMBOL clocks = b2 		' Counter for ADC clock cycles.
SYMBOL I = $FE 		' LCD instruction-prefix.
SYMBOL LCDcls = 1 		' LCD clear-screen instruction.
SYMBOL dg = 223 		' LCD code for degree symbol.
SYMBOL pos = 198 		' LCD position for temp reading.

' Setup: The program starts by setting the initial states of the
' pins and making all but the ADC data line outputs. It then
' clears the LCD and prints a label on the top line. Note that
' the label assumes that an LM34 (Fahrenheit) sensor is used;
' if you're using an LM35, change the F to C.

setup:
  pins = $7b 			' Pins high, except LCD and CLK.
  dirs = $FD 			' S_out, CLK, CS outputs; AD input.
  serout S_out,N2400,(I,LCDcls) ' Clear the LCD screen.
  serout S_out,N2400,(" Temperature ",dg,"F") ' Print fixed label.

' Main: This is where the program spends most of its time. It takes
' a reading via the subroutine 'convert', prints the result to the
' middle of the LCD's second line, pauses a half second, then
' repeats.
main:
  gosub convert 		' Get the data.
  pause 1000 			' Wait a second for LCD startup.
  serout S_out,N2400,(I,pos,#b0," ") ' Write it to the LCD.
  pause 500 			' Wait 1/2 second.
goto main 			' Loop continuously.

' Convert: This subroutine gets 8-bit analog readings from the ADC0831.
' With Vin- connected to ground and Vref to 2.55V, the 10mV/degree
' output of the LM34/35 directly translates to degrees.
convert:
  low CS 			' Select ADC.
  pulsout CLK, 1 		' 10 us clock pulse.
  let data = 0 		' Clear data.
  for clocks = 1 to 8 		' Eight data bits.
  let data = data * 2 		' Perform shift left.
  pulsout CLK, 1 		' 10 us clock pulse.
  let data = data + AD 	' Put bit in LSB of data.
next 				' Do it again.
high CS 			' Deselect ADC.
Return
