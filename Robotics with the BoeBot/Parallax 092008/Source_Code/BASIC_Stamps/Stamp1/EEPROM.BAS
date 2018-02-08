' Program: EEPROM.BAS
' This program demonstrates subroutines for storing data in a
' Microchip 93LC66 serial EEPROM. This program will not work 
' with the National Semiconductor part with the same number. 
' Its serial protocol is substantially different. 

Symbol	CS  	= 0	' Chip-select line to pin 0. 
Symbol	CLK 	= 1	' Clock line to pin 1. 
Symbol	DATA	= pin2	' Destination of Shout; input to Shin
Symbol	DATA_N	= 2	' Pin # of DATA for "input" & "output"
Symbol	ReadEE	= $C00	' EEPROM opcode for read. 
Symbol	Enable	= $980	' EEPROM opcode to enable writes. 
Symbol	Disable	= $800	' EEPROM opcode to disable writes.
Symbol	WriteEE	= $A00	' EEPROM opcode for write.  
Symbol	GetMSB 	= $800	' Divisor for getting msb of 12-bit no.
Symbol	ShifReg	= w1	' Use w1 to shift out 12-bit sequences. 
Symbol	EEaddr	= w2	' 9-bit address for reads & writes.
Symbol	EEdata	= b6	' Data for writes; data from reads. 
Symbol	i	= b7	' Index counter for EEPROM routines.
Symbol	clocks	= b10	' Number of bits to shift with Shout. 

	output DATA_N	' EEPROM combined data connection. 
	output CLK	' EEPROM clock. 
	output CS	' EEPROM chip select. 

' Demonstration program to exercise EEPROM subroutines:
' Accepts serial input at 2400 baud through pin 7. Type a 
' message up to 512 characters long. The Stamp will store
' each character in the EEPROM. When you reach 512 characters 
' or press return, the Stamp will read the message back from 
' the EEPROM and transmit it serially through pin 6
' at 2400 baud. 

	output 6	' For serial output.
	input  7	' For serial input. 
	gosub Eenabl	' Remove EEPROM write protection. 
	let EEaddr=0	' Start at 1st (0th) address. 
CharIn:	serin 7,N2400,EEdata	' Get character. 
	if EEdata<32 then Done ' If it's return, done.
	gosub Ewrite		' Otherwise, write to EEPROM. 
	let EEaddr=EEaddr+1	' Increment addr for next write.
	if EEaddr=512 then Done ' Memory full? Done. 
	goto CharIn

Done:	gosub Edisbl		' Protect EEPROM. 
	for w4 = 0 to 511	' Show all 512 bytes. 
	let EEaddr = w4	' Point to EEPROM address. 
	gosub Eread		' Retrieve the data.
	serout 6,N2400,(EEdata) ' Send it out serial port.
	next			' Next character. 
	End			' Demo over. 

' Write the data in EEdata to the address EEaddr. 
Ewrite:	let ShifReg=WriteEE	' Get the write opcode.  
	let ShifReg=ShifReg|EEaddr ' OR in the address bits.
	let clocks = 12	' Send 12 bits to EEPROM. 
	high CS			' Chip select on. 
	gosub Shout		' Send the opcode/address. 
	let ShifReg = EEdata*16 ' Move bit 7 to bit 11. 
	let clocks = 8		' Eight data bits. 
	gosub Shout		' Send the data.
	low CS			' Deselect the EEPROM. 
	return

' Read data from EEPROM address EEaddr into EEdata. 
Eread:	let ShifReg=ReadEE	' Get the read opcode.
	let ShifReg=ShifReg|EEaddr ' OR in the address bits.
	let clocks=12		' Send 12 bits to EEPROM. 
	high CS			' Chip select on. 
	gosub Shout		' Send the opcode/address. 
	gosub Shin		' Receive the byte.
	low CS			' Deselect the EEPROM. 
	return
' Enable writes to the EEPROM. Upon power-up the EEPROM is 
' write-protected, so this routine must be called before
' first writing to the EEPROM. 
Eenabl:	let ShifReg=Enable	' Get the write-enable opcode.
	high CS			' Chip select on. 
	let clocks = 12	' Send 12 bits to EEPROM. 
	gosub Shout		' Send the opcode. 
	low CS			' Deselect the EEPROM. 
	return

' Disable writes to the EEPROM. 
Edisbl:	let ShifReg=Disable	' Get the write-disable opcode. 
	high CS			' Chip select on. 
	let clocks = 12	' Send 12 bits to EEPROM. 
	gosub Shout		' Send the opcode
	low CS			' Deselect the EEPROM
	return

' Shift data into EEdata. 
Shin:	input DATA_N		' Change the data line to input.
	let EEdata=0		' Clear data byte.
	for i = 1 to 8		' Prepare to get 8 bits. 
	let EEdata=EEdata*2	' Shift EEdata to the left. 
	high CLK		' Data valid on rising edge.
	let EEdata=EEdata+DATA ' Move data to lsb of variable. 
	low CLK			' End of clock pulse. 
	next i			' Get another bit.
	output DATA_N		' Restore data line to output. 
	return

' Shift data out of ShifReg.
Shout:	for i = 1 to clocks	' Number of bits to shift out. 
	let DATA=ShifReg/GetMSB ' Get bit 12 of ShifReg.
	pulsout CLK,10		' Output a brief clock pulse. 
	let ShifReg=ShifReg*2 ' Shift register to the left. 
	next i 			' Send another bit. 
	return
