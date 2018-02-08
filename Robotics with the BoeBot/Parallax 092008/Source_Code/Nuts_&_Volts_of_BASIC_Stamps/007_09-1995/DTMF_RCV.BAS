' Listing 7.2. DTMF Decoder and Display
' Program: DTMF_RCV.BAS (Receives and display DTMF tones using the 8880)
' This program demonstrates how to use the 8880 as a DTMF decoder. As
' each new DTMF digit is received, it is displayed on an LCD Serial
' Backpack screen. If no tones are received within a period of time
' set by sp_time, the program prints a space (or other selected character)
' to the LCD to record the delay. When the display reaches the righthand
' edge of the screen, it clears the LCD and starts over at the left edge.
SYMBOL RS_p = 4 		' Register-select pin (0=data).
SYMBOL RW_p = 5 		' Read/Write pin (0=write).
SYMBOL CS_p = 6 		' Chip-select pin (0=active).
SYMBOL dtmf = b2 		' Received DTMF digit.
SYMBOL dt_Flag = bit0 		' DTMF-received flag.
SYMBOL home_Flag = bit1 	' Flag: 0 = cursor at left edge of LCD.
SYMBOL polls = w2 		' Number of unsuccessful polls of DTMF.
SYMBOL LCDw = 16 		' Width of LCD screen.
SYMBOL LCDcol = b3 		' Current column of LCD screen for wrap.
SYMBOL LCDcls = 1 		' LCD clear-screen command.
SYMBOL I = 254 		' LCD instruction toggle.
SYMBOL sp_time = 1000 		' Print space this # of polls w/o DTMF.
' This code initializes the 8880 for receiving by writing to its
' internal control registers CRA and CRB. The write occurs when
' CS (pin 6) is taken low, then returned high.
let pins = %01111111 		' Pin 7 (LCD) low, pins 0 through 6 high.
let dirs = %11111111 		' Set up to write to 8880 (all outputs).
let pins = %00011000 		' Set up reg. A, next write to reg. B.
high CS_p
let pins = %00010000 		' Clear register B.
high CS_p
let dirs = %11110000 		' Now make set the 4-bit bus to input.
high RW_p 			' And set RW to "read."
serout 7,n2400,(I,LCDcls,I) 	' Clear the LCD screen.
' In the loop below, the program checks the 8880's status register
' to determine whether a DTMF tone has been received (indicated by
' a '1' in bit 2). If no tone, the program loops back and checks
' again. If a tone is present, the program switches from status to
' data (RS low) and gets the value (0-15) of the tone. This
' automatically resets the 8880's status flag.
again:
  high RS_p 			' Read status register.
  Low CS_p 			' Activate the 8880.
  let dt_flag = pin2 		' Store status bit 2 into flag.
  high CS_p 			' End the read.
  if dt_Flag = 1 then skip1 	' If tone detected, continue.
 let polls = polls+1 		 ' Another poll without DTMF tone.
 if polls < sp_time then again ' If not time to print a space, poll again.
 if LCDcol = LCDw then skip2   ' Don't erase the screen to print spaces.
 let dtmf = 16 		 ' Tell display routine to print a space.
gosub Display 			 ' Print space to LCD.
skip2:
 let polls = 0 		 ' Clear the counter.
goto again 			 ' Poll some more.
skip1: 				 ' Tone detected:
 let polls = 0 		 ' Clear the poll counter.
 low RS_p 			 ' Get the DTMF data.
 low CS_p 		 	 ' Activate 8880.
 let dtmf = pins & %00001111   ' Strip off upper 4 bits using AND.
 high CS_p 			 ' Deactivate 8880.
gosub display 			 ' Display the data.
goto again 			 ' Do it all again.
Display:
 if LCDcol < LC	Dw then skip3   ' If not at end of LCD, don't clear screen.
 serout 7,n2400,(I,LCDcls,I)   ' Clear the LCD screen.
 let LCDcol = 0 		 ' And reset the column counter.
skip3: 				 ' Look up the symbol for the digit.
 if LCDcol=0 AND dtmf=16 then ret ' No spaces at first column.
 lookup dtmf,("D1234567890*#ABC-"),dtmf
 serout 7,n2400,(dtmf) 	 ' Write it to the Backpack display.
 let LCDcol = LCDcol + 1 	 ' Increment the column counter.
ret:
 return
