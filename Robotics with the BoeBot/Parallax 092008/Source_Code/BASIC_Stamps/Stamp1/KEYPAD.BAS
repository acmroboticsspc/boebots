' PROGRAM: Keypad.bas
' Taken from Parallax Stamp Application Note #3: Hardware Solution for Keypads

' July 28, 1993

' The Stamp accepts input from a 16-key matrix keypad with the help of ' a 74C922 keypad decoder chip. 
Symbol	E	=	5	' Enable pin, 1 = enabledSymbol	RS	=	4	' Register select pin, 0 = instructionSymbol	char	=	b1	' Character sent to LCD. Symbol	buttn	=	b3	' Workspace for button command. Symbol	lngth	=	b5	' Length of text appearing on LCD.Symbol	temp	=	b7	' Temporary holder for input character.' Set up the Stamp's I/O lines and initialize the LCD. 
begin:	let pins = 0		' Clear the output lines	let dirs = %01111111	' One input, 7 outputs. 	pause 200		' Wait 200 ms for LCD to reset. 	let buttn = 0	let lngth = 0	gosub i_LCD	gosub clearkeyin:	let dirs = %01100000		' Set up I/O directions. loop:	button 4,1,50,10,buttn,0,nokey	' Check pin 4 (data available) for
					' keypress. 	lngth = lngth + 1		' Key pressed: increment position
					' counter. 	let temp = pins & %00001111	' Strip extra bits to leave only key
					' data. 	if temp > 9 then hihex		' Convert 10 thru 15 into A thru F. 	let temp = temp + 48		' Add offset for ASCII 0.LCD:	let dirs = %01111111		' Get ready to output to LCD.	if lngth > 16 then c_LCD	' Screen full? Clear it.cont:	let char = temp		' Write character to LCD.	gosub wr_LCDnokey:	pause 10			' Short delay for nice auto-repeat
					' speed.	goto keyin			' Get ready for next key. hihex:  let temp = temp + 55		' Convert numbers 10 to 15 into A-F.	goto LCDc_LCD:	let lngth = 1		' If 16 characters are showing on LCD, 	gosub clear		' clear the screen and print at left edge. 	goto cont' Initialize the LCD in accordance with Hitachi's instructions' for 4-bit interface. 
i_LCD:	let pins = %00000011		' Set to 8-bit operation. 	pulsout E,1		' Send above data three times	pause 10			' to initialize LCD. 	pulsout E,1			pulsout E,1			let pins = %00000010		' Set to 4-bit operation. 	pulsout E,1		' Send above data three times. 	pulsout E,1	pulsout E,1	let char = 12		' Set up LCD in accordance w/	gosub wr_LCD		' Hitachi instruction manual. 	let char = 6		' Turn off cursor, enable	gosub wr_LCD		' left-to-right printing.  	high RS			' Prepare to send characters. 	return' Write the ASCII character in b3 to the LCD. 
wr_LCD:	let pins = pins & %00010000	let b2 = char/16		' Put high nibble of b3 into b2. 	let pins = pins | b2		' OR the contents of b2 into pins. 	pulsout E,1		' Blip enable pin. 	let b2 = char & %00001111	' Put low nibble of b3 into b2. 	let pins = pins & %00010000	' Clear 4-bit data bus. 	let pins = pins | b2		' OR the contents of b2 into pins.	pulsout E,1		' Blip enable. 	return' Clear the LCD screen. 
clear:	low RS			' Change to instruction register. 	let char = 1		' Clear display. 	gosub wr_LCD		' Write instruction to LCD. 	high RS			' Put RS back in character mode. 	return
