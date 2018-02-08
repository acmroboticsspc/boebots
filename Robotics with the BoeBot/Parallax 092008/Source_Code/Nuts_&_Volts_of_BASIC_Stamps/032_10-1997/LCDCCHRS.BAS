' Listing 1
' Nuts & Volts: Stamp Applications, October 1997


' -----[ Title ]-----------------------------------------------------------
'
' File...... LCDCCHRS.BAS
' Purpose... Stamp 1 -> LCD (4-bit interface)
' Author.... Jon Williams
' E-mail.... jonwms@aol.com
' WWW....... http://members.aol.com/jonwms
' Started... 16 JUL 1994
' Updated... 27 AUG 1997


' -----[ Program Description ]---------------------------------------------
'
' This program demonstrates the generation of custom characters for an
' LCD display that uses the Hitatchi HD44780 controller.  The LCD used 
' to test this program was the Optrex DMC-16106 (16x1).
'
' LCD Connections:
'
' LCD        (Function)         Stamp
' ---------------------         -----
' pin 1         Vss             Gnd
' pin 2         Vdd             +5
' pin 3         Vo              Gnd (or wiper of 10K pot)
' pin 4         RS              Pin 4
' pin 5         R/W             Gnd
' pin 6         E               Pin 5
' pin 7         DB0             Gnd
' pin 8         DB1             Gnd
' pin 9         DB2             Gnd
' pin 10        DB3             Gnd
' pin 11        DB4             Pin 0
' pin 12        DB5             Pin 1
' pin 13        DB6             Pin 2
' pin 14        DB7             Pin 4


' -----[ Revision History ]------------------------------------------------
'
' 16 JUL 94 : Version 1.0 - compilation of code from last 3 months
' 08 AUG 96 : Trimmed code to save space -- no performance changes!
' 27 AUG 97 : Updated for Nuts & Volts


' -----[ Constants ]-------------------------------------------------------
'
SYMBOL  RS      = 4                     ' Register Select (1 = char)
SYMBOL  E       = 5                     ' LCD enable pin  (1 = enabled)

' LCD control characters
'
SYMBOL  ClrLCD  = $01                   ' clear the LCD
SYMBOL  CrsrHm  = $02                   ' move cursor to home position
SYMBOL  CrsrLf  = $10                   ' move cursor left
SYMBOL  CrsrRt  = $14                   ' move cursor right
SYMBOL  DispLf  = $18                   ' shift displayed chars left
SYMBOL  DispRt  = $1C                   ' shift displayed chars right
SYMBOL  DDRam   = $80                   ' Display Data RAM control
SYMBOL	CGRam	= $40			' Char Gen RAM control

 
' -----[ Variables ]-------------------------------------------------------
'
SYMBOL  char    = B1                    ' character sent to LCD
SYMBOL	addr	= B2			' EEPROM address pointer
SYMBOL	newChr	= B3			' new character for animation
SYMBOL  index1  = B4                    ' loop counter
SYMBOL  index2  = B5                    ' loop counter


' -----[ EEPROM Data ]-----------------------------------------------------
'
	EEPROM ("THE BASIC STAMP ")	' preload EEPROM with messages
	EEPROM (" IS VERY COOL!  ")

	' custom character data

	EEPROM ($0E,$1F,$1C,$18,$1C,$1F,$0E,$00)	' character 0
	EEPROM ($0E,$1F,$1F,$18,$1F,$1F,$0E,$00)	' character 1
	EEPROM ($0E,$1F,$1F,$1F,$1F,$1F,$0E,$00)	' character 2


' -----[ Initialization ]--------------------------------------------------
'
Init:   Dirs = %00111111                ' set 0-5 as outputs
	Pins = %00000000                ' clear the pins

' Initialize the LCD (Hitachi HD44780 controller)
'
LCDini: PAUSE 500                       ' let the LCD settle
	Pins = %0011                    ' 8-bit mode
	PULSOUT E, 1
	PAUSE 5
	PULSOUT E, 1
	PULSOUT E, 1
	Pins = %0010                    ' 4-bit mode
	PULSOUT E, 1
	char = %00001100                ' disp on, crsr off, blink off
	GOSUB LCDcmd                     
	char = %00000110                ' inc crsr, no disp shift
	GOSUB LCDcmd

	' download custom character maps to LCD

	char = CGRam			' point to CG RAM
	GOSUB LCDcmd                    ' prepare to write CG data
	FOR index1 = 32 TO 55		' build 3 custom chars
	  READ index1, char             ' get byte from EEPROM
	  GOSUB LCDwr			' put into LCD CG RAM
	NEXT index1


' -----[ Main Code ]-------------------------------------------------------
'
Start:	char = ClrLCD
	GOSUB LCDcmd  

	FOR index1 = 0 TO 15
	  READ index1, char		' get character from EEPROM
	  GOSUB LCDwr                   ' write it
	NEXT
	PAUSE 2000                      ' wait 2 seconds

	' Animation by character replacement
	' Eat first message and expose second

	FOR index1 = 0 TO 15            ' cover 16 characters
	  addr = index1 + 16		' get new char from 2nd message
	  READ addr, newChr
	  FOR index2 = 0 TO 4           ' 5 characters in animation cycle
	    char = DDRam | index1	' set new DDRAM address
	    GOSUB LCDcmd
	    LOOKUP index2, (2,1,0,1,newChr), char
	    GOSUB LCDwr			' write animation character
	    PAUSE 50			' delay between animation chars
	  NEXT index2
	NEXT index1
	PAUSE 1000

	GOTO Start                      ' do it all over


' -----[ Subroutines ]-----------------------------------------------------
'
' Send command to the LCD
'
LCDcmd: LOW RS                          ' enter command mode
					'  then write the character

' Write ASCII char to LCD 
'
LCDwr:  Pins = Pins & %11010000         ' save 7, 6 and RS; clear bus
	Pins = char / 16 | Pins         ' output high nibble
	PULSOUT E, 1                    ' strobe the Enable line
	Pins = Pins & %11010000
	Pins = char & $0F | Pins        ' output low nibble
	PULSOUT E, 1
	HIGH RS                         ' return to character mode
	RETURN
