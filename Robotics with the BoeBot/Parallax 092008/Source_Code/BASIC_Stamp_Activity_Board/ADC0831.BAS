'************************************************************************
'*  Basic Stamp Activity Board Sample Program                  ADC0831  *
'*  9 September, 1997                                           (BS-1)  *
'*                                                                      *
'*  This program demonstrates how to use the ADC0831 single-channel,    *
'*  8-bit A/D converter with the Basic Stamp Activity Board.  Place     *
'*  the jumpers on 'X4' in accordance with the drawing depicted on      *
'*  the Basic Stamp Activity Board Notes. The aforementioned notes      *
'*  also indicate where to apply the 0-5 Vdc input. Hit 'Alt-R'to       *
'*  download and view the results in the debug window.                  *
'************************************************************************

Symbol	CS	=	3
Symbol	AD	=	pin1
Symbol	CLK	=	0
Symbol	data	=	b0
Symbol	i	=	b2

setup:	let pins = 255		'Pins high (deselect ADC).
	let dirs = %11111101	'S_out, CLK, CS outputs; AD input.
loop:	gosub conv		'Get the data and display it.
	debug "A/D Result: ",#b0,cr
	pause 100		'Wait 0.1 seconds.
	goto loop		'Do it forever.
'************************************************************************
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
'************************************************************************
