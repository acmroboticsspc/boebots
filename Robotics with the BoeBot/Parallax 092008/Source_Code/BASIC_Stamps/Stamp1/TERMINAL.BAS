' PROGRAM: TERMINAL.BAS

' Taken from Parallax Stamp Application Note: LCD User-Interface Terminal

' July 15, 1993
' The Stamp serves as a user-interface terminal. It accepts text via RS-232
' from a host, and provides a way for the user to respond to queries via four
' pushbuttons. Symbol	S_in	= 	7	'Serial data input pinSymbol	S_out	=	6	'Serial data output pinSymbol	E	=	5	'Enable pin, 1 = enabledSymbol	RS	=	4	'Register select pin, 0 = instructionSymbol  keys	=	b0	'Variable holding # of key pressed. Symbol	char	=	b3	'Character sent to LCD. Symbol	Sw_0	=	pin0	'User input switchesSymbol	Sw_1	=	pin1	'multiplexed w/LCD data lines.Symbol	Sw_2	=	pin2Symbol	Sw_3	=	pin3
' Set up the StampÕs I/O lines and initialize the LCD. 
begin:	let pins = 0		'Clear the output lines	let dirs = %01111111	'One input, 7 outputs. 	pause 200		'Wait 200 ms for LCD to reset. ' Initialize the LCD in accordance with HitachiÕs instructions for 4-bit interface. 
i_LCD:	let pins = %00000011	'Set to 8-bit operation. 	pulsout E,1		'Send data three times	pause 10		'to initialize LCD. 	pulsout E,1			pause 10			pulsout E,1			pause 10		
	let pins = %00000010	'Set to 4-bit operation. 	pulsout E,1		'Send above data three times. 	pulsout E,1	pulsout E,1
	let char = 14		'Set up LCD in accordance with	gosub wr_LCD		'Hitachi instruction manual. 
	let char = 6		'Turn on cursor and enable	gosub wr_LCD		'left-to-right printing. 
	let char = 1		'Clear the display. 	gosub wr_LCD
	high	RS		'Prepare to send characters. ' Main program loop: receive data, check for backspace, and display data on LCD
main:	serin S_in,N2400,char	'Main terminal loop. 	goto bksp
out:	gosub wr_LCD	goto main' Write the ASCII character in b3 to LCD. 
wr_LCD:	let pins = pins & %00010000	let b2 = char/16	'Put high nibble of b3 into b2. 	let pins = pins | b2	'OR the contents of b2 into pins. 	pulsout E,1		'Blip enable pin. 	let b2 = char & %00001111	'Put low nibble of b3 into b2. 	let pins = pins & %00010000	'Clear 4-bit data bus. 	let pins = pins | b2		'OR the contents of b2 into pins.	pulsout E,1		'Blip enable. 	return' Backspace, rub out character by printing a blank.  
bksp:	if char > 13 then out	'Not a bksp or cr? Output character. 	if char = 3 then clear	'Ctl-C clears LCD screen.  	if char = 13 then cret	'Carriage return.  	if char <> 8 then main	'Reject other non-printables. 	gosub back	let char = 32		'Send a blank to display	gosub wr_LCD	gosub back		'Back up to counter LCDÕs auto-increment. 	goto main		'Get ready for another transmission. back:	low RS			'Change to instruction register. 	let char = 16		'Move cursor left. 	gosub wr_LCD		'Write instruction to LCD. 	high RS			'Put RS back in character mode. 	returnclear:	low RS			'Change to instruction register. 	let b3 = 1		'Clear the display. 	gosub wr_LCD		'Write instruction to LCD. 	high RS			'Put RS back in character mode.	goto main' If a carriage return is received, wait for switch input from the user. The
' host program (on the other computer) should cooperate by waiting for a reply
' before sending more data. 
cret:	let dirs = %01110000	'Change LCD data lines to input. loop:	let keys = 0	if Sw_0 = 1 then xmit	'Add one for each skipped key. 	let keys = keys + 1	if Sw_1 = 1 then xmit	let keys = keys + 1	if Sw_2 = 1 then xmit	let keys = keys + 1	if Sw_3 = 1 then xmit	goto loopxmit:	serout S_out,N2400,(#keys,10,13)	let dirs = %01111111	'Restore I/O pins to original state.	goto main
