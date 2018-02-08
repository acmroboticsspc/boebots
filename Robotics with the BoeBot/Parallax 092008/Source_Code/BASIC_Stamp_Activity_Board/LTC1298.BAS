symbol	CS	= 3		' ADC Chip select (Low True)
symbol	CLK	= 0		' ADC Clock
symbol	Din 	= pin1		' Data input pin number
symbol	DatIn	= 1		' ADC Data Input
symbol	Dout	= pin2		' Data output pin number 
symbol  DatOut	= 2		' ADc Data Output
symbol	ADbits	= b1		' Loop counter used to receive bits
symbol	ADRes	= w1		' 12-bit ADC conversion result
symbol	sglDif	= 1		' 1 -> Single-ended, two-channel mode. 
symbol	msbf	= 1		' Output 0s after data transfer is complete
symbol	oddSign	= bit0		' Channel number
Init:
	input DatIn
	high CS			' Deactivate the ADC to begin. 
Start: 				' Main loop.
	For oddSign = 0 to 1	' Toggle between input channels. 
	gosub Convert		' Get data from ADC and display it.
	debug "Ch: ",#oddSign,":",#ADRes,cr
	pause 500 		' Wait a half second. 
	next			' Change input channels. 
	goto Start		' Do forever
'*************************************************************************
Convert: 
	low CLK			' Low clock--output on rising edge. 
	high DatOut		' Switch DIO to output high (start bit). 
	low CS			' Activate the 1298. 
	pulsout CLK,5		' Send start bit. 
	let Dout = sglDif	' First setup bit. 
	pulsout CLK,5		' Send bit. 
	let Dout = oddSign	' Second setup bit. 
	pulsout CLK,5		' Send bit. 
	let pin2 = msbf		' Final setup bit. 
	pulsout CLK,5		' Send bit. 
	let ADRes = 0		' Clear old ADC result. 
	for ADbits = 1 to 13	' Get null bit + 12 data bits. 
	let ADRes = ADRes*2+Din	' Shift AD left, add new data bit. 
	pulsout CLK,5		' Clock next data bit in. 
	next			' Get next data bit.  
	high CS			' Turn off the ADC
	return			' Return to program.