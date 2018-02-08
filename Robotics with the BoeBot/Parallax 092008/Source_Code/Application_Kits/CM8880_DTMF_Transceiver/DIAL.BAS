' Program: DIAL.SRC (Sends a string of DTMF tones via the 8880)
' This program demonstrates how to use the CM8880 as a DTMF tone 
' generator. All that's required is to initialize the 8880 properly, 
' then write the number of the desired DTMF tone to the 8880's 
' 4-bit bus. 

' The symbols below are the pin numbers to which the 8880's 
' control inputs are connected, and one variable used to read 
' digits out of a lookup table. 

SYMBOL	RS_p = 4		' Register-select pin (0=data). 
SYMBOL  RW_p = 5		' Read/Write pin (0=write). 
SYMBOL	CS_p = 6		' Chip-select pin (0=active). 
SYMBOL	digit = b2		' Index of digits to dial. 

' This code initializes the 8880 for dialing by writing to its
' internal control registers CRA and CRB. The write occurs when 
' CS (pin 6) is taken low, then returned high. See the accompanying
' article for an explanation of the 8880's registers. 

let pins = 255		' All pins high to deselect 8880.  
let dirs = 255		' Set up to write to 8880 (all outputs). 
let pins = %00011011	' Set up  register A, next write to register B. 
high CS_p
let pins = %00010000	' Clear register B; ready to send DTMF. 
high CS_p

' This for/next loop dials the seven digits of my fax number. For 
' simplicity, it writes the digit to be dialed directly to the output
' pins. Since valid digits are between 0 and 15, this also takes RS, 
' RW, and CS low--perfect for writing data to the 8880. To complete
' the write, the CS line is returned high. The initialization above
' sets the 8880 for tone bursts of 200 ms duration, so we pause 
' 250 ms between digits. Note: in the DTMF code as used by the phone
' system, zero is represented by ten (1010 binary) not 0. That's why
' the phone number 459-0623 is coded 4,5,9,10,6,2,3. 

for digit = 0 to 6
  lookup digit,(4,5,9,10,6,2,3),pins	' Get current digit from table. 
  high CS_p				' Done with write. 
  pause 250				' Wait to dial next digit. 
next digit

end