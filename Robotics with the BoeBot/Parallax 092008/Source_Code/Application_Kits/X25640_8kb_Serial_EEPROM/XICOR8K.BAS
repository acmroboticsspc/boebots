' Program: XICOR8K.BAS
' This program demonstrates subroutines for storing data in a
' XICOR X25640 serial EEPROM.

Symbol	CS  	= 0	' Chip-select line to pin 0. 
Symbol	CLK 	= 1	' Clock line to pin 1. 
Symbol	DATA	= pin2	' Destination of Shout; input to Shin
Symbol	DATA_N	= 2	' Pin # of DATA for "input" & "output"
Symbol	ReadEE	= 3	' EEPROM opcode for read. 
Symbol	WREN	= 6	' EEPROM opcode to enable writes. 
Symbol	WRDI	= 4	' EEPROM opcode to disable writes.
Symbol	RDSR	= 5	' EEPROM opcode to read the status register. 
Symbol	WriteEE	= 2	' EEPROM opcode for write.  
Symbol	i	= b6	' Counter for shift loops
Symbol	EEdata	= b4	' Data byte to/from EEPROM
Symbol	EEaddr	= w1	' 16-bit EEPROM address. 
Symbol	Shreg	= b1	' Shift register used by Shin/Shout. 

' Beginning of demonstration program. We're just going to write-enable
' the EEPROM, write a value to one of its addresses, then retrieve
' that value. 

Demo:	output DATA_N		' Output to EEPROM data lines. 
	high CS			' Deactivate EEPROM. 

' This chunk of code sends the write-enable opcode to the EEPROM. 
' To disable writes, use this same code, but substitute WRDI for WREN.
	let Shreg = WREN	' Write-enable opcode. 
	low CS			' Activate the EEPROM. 
	gosub Shout		' Send the opcode. 
	high CS			' Deactivate the EEPROM. 
' ===================================================================
' Now write some data to the EEPROM. The legal range of addresses 
' is 0 to 8191. 
	let EEdata = 195	' Sample data. 
	let EEaddr = 800	' Set an address. 
	gosub EEwrite		' Write the data. 
	pause 10		' Wait for write cycle to finish. 
	gosub EEread		' Read back the data. 
	debug EEdata		' Show us. 
here:	goto here		' Endless loop to terminate program. 

' ==================================================================
' The subroutines for reading and writing the EEPROM.
' ==================================================================

' Shift data into Shreg. 
Shin:	input DATA_N		' Change the data line to input.
	let Shreg=0		' Clear data byte.
	for i = 1 to 8		' Prepare to get 8 bits. 
	low CLK			' Data valid on falling edge.
	let Shreg=Shreg*2	' Shift Shreg to the left. 
	let bit8=DATA 		' Move data to lsb of variable. 
	high CLK		' End of clock pulse. 
	next i			' Get another bit.
	output DATA_N		' Restore data line to output. 
	return

' Shift data out of Shreg. 
Shout:	for i = 1 to 8		' Shift out 8 bits. 
	low CLK			' Clear clock bit. 
	let DATA=bit15 		' Get bit 7 of Shreg.
	let Shreg=Shreg*2 	' Shift register to the left. 
	high CLK		' Data valid on rising edge of clock.
	next i 			' Send another bit. 
	return

' Write EEdata to EEaddr. 
EEwrite:
	let Shreg = WriteEE	' Set up the write opcode. 
	gosub Send_addr		' Send it and the address. 
	let Shreg = EEdata	' Now send the data. 
	gosub Shout
	high CS			' Deactivate the EEPROM. 
	return

' Read data at EEaddr and put it into EEdata. 
EEread: 
	let Shreg = ReadEE	' Set up the read opcode. 
	gosub Send_addr		' Send it and the address. 
	gosub Shin		' Now get the data. 
	let EEdata = Shreg	' Transfer from Shreg to EEdata. 
	high CS			' Deactivate the EEPROM. 
	return

' Send the opcode and address for write or read. 
Send_addr:
	low CS			' Activate the EEPROM. 
	gosub Shout		' Send opcode in Shreg. 
	let Shreg = b3		' Send upper byte of address.
	gosub Shout
	let Shreg = b2		' Send lower byte of address. 
	gosub Shout
	return