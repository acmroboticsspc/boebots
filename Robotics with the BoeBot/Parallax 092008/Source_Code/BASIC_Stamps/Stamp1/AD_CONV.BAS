' PROGRAM: AD_CONV.BAS

' Taken from Parallax Stamp Application Note: Interfacing an A/D Converter

' July 15, 1993

' BASIC Stamp program that uses the National ADC0831 to acquire 
' analog data and output it via RS-232. 

Symbol	CS	=	0
Symbol	AD	=	pin1
Symbol	CLK	=	2
Symbol	S_out	= 	3
Symbol	data	=	b0
Symbol	i	=	b2

setup:	let pins = 255		'Pins high (deselect ADC). 
	let dirs = %11111101	'S_out, CLK, CS outputs; AD input. 

loop:	gosub conv		'Get the data. 
	serout S_out,N2400,(#b0,13,10) 'Send data, CR, LF. 
	pause 2000		'Wait 2 seconds
	goto loop		'Do it forever. 

conv:	low CLK			'Put clock line in starting state. 
	low CS			'Select ADC. 
	pulsout CLK, 1		'10 us clock pulse. 
	let data = 0		'Clear data. 
	for i = 1 to 8		'Eight data bits. 
	let data = data * 2	'Perform shift left. 
	pulsout CLK, 1		'10 us clock pulse. 
	let data = data + AD	'Put bit in LSB of data. 
	next			'Do it again. 
	high CS			'Deselect ADC when done. 
	return	
