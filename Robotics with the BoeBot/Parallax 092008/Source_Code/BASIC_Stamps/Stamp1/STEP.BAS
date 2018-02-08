' Program STEP.BAS
' The Stamp accepts simply formatted commands and drives a four-coil
' stepper. Commands are formatted as follows: +500 20<return> means
' rotate forward 500 steps with 20 milliseconds between steps. To
' run the stepper backward, substitute - for +. 
 
Symbol	Directn = b0
Symbol	Steps = w1
Symbol 	i = w2
Symbol	Delay = b6
Symbol	Dir_cmd = b7 

	dirs = %01000011 : pins = %00000001 	' Initialize output.
	b1 = %00000001 : Directn = "+"
	goto Prompt				' Display prompt. 

' Accept a command string consisting of direction (+/-), a 16-bit number
' of steps, and an 8-bit delay (milliseconds) between steps. If longer
' step delays are required, just command 1 step at a time with long
' delays between commands. 

Cmd:	serin 7,N2400,Dir_cmd,#Steps,#Delay	' Get orders from terminal.
	if Dir_cmd = Directn then Stepit	' Same direction? Begin.
	b1 = b1^%00000011			' Else reverse. 

Stepit:	for i = 1 to Steps	' Number of steps. 
	pins = pins^b1		' XOR output with b1, then invert b1 to
	b1 = b1^%00000011	' calculate the stepping sequence. 
	pause Delay		' Wait commanded delay between steps. 
	next
	Directn = Dir_cmd	' Direction = new direction. 

Prompt:	serout 6,N2400,(10,13,"step> ")		' Show prompt, send return
	goto Cmd				' and linefeed to terminal. 
