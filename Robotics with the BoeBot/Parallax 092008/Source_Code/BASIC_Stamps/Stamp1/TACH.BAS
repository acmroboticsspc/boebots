' Listing 1: TACH.BAS
' The BASIC Stamp serves as a tachometer. It accepts pulse input through 
' pin 7, and outputs rpm measurements at 2400 baud through pin 0. 
	input 7
	output 0
Tach:	pulsin 7,1,w2      ' Read positive-going pulses on pin 7. 
	let w2 = w2/100    ' w2/100 divided into 60,000 equals 
	let w2 = 60000/w2  ' 6,000,000/w2 (60 seconds in 10-us units).

' Transmit data followed by carriage return and linefeed.
	serout 0,N2400,(#w2," rpm",10,13) 
	pause 1000	   ' Wait 1 second between readings
	goto Tach
	
