' Program: LTC1298 (LTC1298 analog-to-digital converter)

' The LTC1298 is a 12-bit, two-channel ADC. Its high resolution, low
' supply current, low cost, and built-in sample/hold feature make it a 
' great companion for the Stamp in sensor and data-logging applications. 
' With its 12-bit resolution, the LTC1298 can measure tiny changes in 
' input voltage; 1.22 millivolts (5-volt reference/4096). 

' ==========================================================
'                     ADC Interface Pins
' ==========================================================

' The 1298 uses a four-pin interface, consisting of chip-select, clock, 
' data input, and data output. In this application, we tie the data lines
' together with a 1k resistor and connect the Stamp pin designated DIO 
' to the data-in side of the resistor. The resistor limits the current
' flowing between DIO and the 1298's data out in case a programming error
' or other fault causes a "bus conflict." This happens when both pins are
' in output mode and in opposite states (1 vs 0). Without the resistor, 
' such a conflict would cause large currents to flow between pins, 
' possibly damaging the Stamp and/or ADC. 

SYMBOL	CS = 0 		' Chip select; 0 = active. 
SYMBOL	CLK = 1		' Clock to ADC; out on rising, in on falling edge. 
SYMBOL	DIO_n = 2	' Pin _number_ of data input/output. 
SYMBOL	DIO_p = pin2	' Variable_name_ of data input/output. 
SYMBOL  ADbits = b1	' Counter variable for serial bit reception. 
SYMBOL	AD = w1		' 12-bit ADC conversion result. 

' ==========================================================
'                     ADC Setup Bits
' ==========================================================

' The 1298 has two modes. As a single-ended ADC, it measures the 
' voltage at one of its inputs with respect to ground. As a differential
' ADC, it measures the difference in voltage between the two inputs. 
' The sglDif bit determines the mode; 1 = single-ended, 0 = differential. 
' When the 1298 is single-ended, the oddSign bit selects the active input
' channel; 0 = channel 0 (pin 2), 1 = channel 1 (pin 3). 
' When the 1298 is differential, the oddSign bit selects the polarity 
' between the two inputs; 0 = channel 0 is +, 1 = channel 1 is +. 
' The msbf bit determines whether clock cycles _after_ the 12 data bits
' have been sent will send 0s (msbf = 1) or a least-significant-bit-first
' copy of the data (msbf = 0). This program doesn't continue clocking after
' the data has been obtained, so this bit doesn't matter. 

' You probably won't need to change the basic mode (single/differential)
' or the format of the post-data bits while the program is running, so 
' these are assigned as constants. You probably will want to be able to 
' change channels, so oddSign (the channel selector) is a bit variable. 

SYMBOL	sglDif = 1	' Single-ended, two-channel mode. 
SYMBOL	msbf = 1	' Output 0s after data transfer is complete. 
SYMBOL	oddSign = bit0	' Program writes channel # to this bit. 

' ==========================================================
'                     Demo Program
' ==========================================================

' This program demonstrates the LTC1298 by alternately sampling the two
' input channels and presenting the results on the PC screen using Debug. 

high CS				' Deactivate the ADC to begin. 
Again: 				' Main loop.
  For oddSign = 0 to 1		' Toggle between input channels. 
    gosub Convert		' Get data from ADC. 
    debug "ch ",#oddSign,":",#AD,cr	' Show the data on PC screen. 
    pause 500 			' Wait a half second. 
  next				' Change input channels. 
goto Again			' Endless loop. 

' ==========================================================
'                     ADC Subroutine
' ==========================================================

' Here's where the conversion occurs. The Stamp first sends the setup 
' bits to the 1298, then clocks in one null bit (a dummy bit that always 
' reads 0) followed by the conversion data. 

Convert: 
  low CLK			' Low clock--output on rising edge. 
  high DIO_n			' Switch DIO to output high (start bit). 
  low CS			' Activate the 1298. 
  pulsout CLK,5			' Send start bit. 
  let DIO_p = sglDif		' First setup bit. 
  pulsout CLK,5			' Send bit. 
  let DIO_p = oddSign		' Second setup bit. 
  pulsout CLK,5			' Send bit. 
  let DIO_p = msbf		' Final setup bit. 
  pulsout CLK,5			' Send bit. 
  input DIO_n			' Get ready for input from DIO. 
  let AD = 0			' Clear old ADC result. 
  for ADbits = 1 to 13		' Get null bit + 12 data bits. 
    let AD = AD*2+DIO_p		' Shift AD left, add new data bit. 
    pulsout CLK,5		' Clock next data bit in. 
  next				' Get next data bit.  
  high CS			' Turn off the ADC
return				' Return to program. 
  
  


  
