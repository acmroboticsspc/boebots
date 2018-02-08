' Listing 38.2
' Nuts & Volts: Stamp Applications, April 1998
' -----[ Title ]-----------------------------------------------------
'
' File...... SSDEMO.BAS
' Purpose... STMPSIZE Demo
' Author.... Jon Williams
' E-mail.... jonwms@aol.com
' WWW....... http://members.aol.com/jonwms
' Started... 07 MAR 1998
' Updated... 07 MAR 1998
' -----[ Program Description ]---------------------------------------
'
' LCD Connections:
'
' LCD (Function) Stamp
' --------------------- -----
' pin 1 Vss Gnd
' pin 2 Vdd +5
' pin 3 Vo Gnd (or wiper of 10K pot)
' pin 4 RS Pin 4
' pin 5 R/W Gnd
' pin 6 E Pin 5
' pin 7 DB0 Gnd
' pin 8 DB1 Gnd
' pin 9 DB2 Gnd
' pin 10 DB3 Gnd
' pin 11 DB4 Pin 0
' pin 12 DB5 Pin 1
' pin 13 DB6 Pin 2
' pin 14 DB7 Pin 4
' -----[ Revision History ]------------------------------------------
'
' -----[ Constants ]-------------------------------------------------
'
SYMBOL RS = 4 ' Register Select (1 = char)
SYMBOL E = 5 ' LCD enable pin (1 = enabled)
' LCD control characters
'
SYMBOL ClrLCD = $01 	' clear the LCD
SYMBOL CrsrHm = $02 	' move cursor to home position
SYMBOL CrsrLf = $10 	' move cursor left
SYMBOL CrsrRt = $14 	' move cursor right
SYMBOL DispLf = $18 	' shift displayed chars left
SYMBOL DispRt = $1C 	' shift displayed chars right
SYMBOL DDRam = $80 	' Display Data RAM control
' -----[ Variables ]-------------------------------------------------
'
SYMBOL char = B1 	' character sent to LCD
SYMBOL index = B2 ' loop counter
' -----[ EEPROM Data ]-----------------------------------------------
'
' -----[ Initialization ]--------------------------------------------
'
Init: Dirs = %00111111 	' set 0-5 as outputs
Pins = %00000000 		' clear the pins
' Initialize the LCD (Hitachi HD44780 controller)
'
LCDini: PAUSE 500 		' let the LCD settle
Pins = %0011 			' 8-bit mode
PULSOUT E, 1
PAUSE 5
PULSOUT E, 1
PULSOUT E, 1
Pins = %0010 			' 4-bit mode
PULSOUT E, 1
char = %00001100 		' disp on, crsr off, blink off
GOSUB LCDcmd
char = %00000110 		' inc crsr, no disp shift
GOSUB LCDcmd
char = ClrLCD
GOSUB LCDcmd
' -----[ Main Code ]-------------------------------------------------
'
Start: FOR index = 0 TO 15
LOOKUP index,("< NUTS & VOLTS >"),char
GOSUB LCDwr
NEXT
END
' -----[ Subroutines ]-----------------------------------------------
'
LCDcmd: LOW RS ' enter command mode
' then write the character
' Write ASCII char to LCD
'
LCDwr: Pins = Pins & %11010000' save 7, 6 and RS; clear bus
Pins = char / 16 | Pins 	' output high nibble
PULSOUT E, 1 			' strobe the Enable line
Pins = Pins & %11010000
Pins = char & $0F | Pins 	' output low nibble
PULSOUT E, 1
HIGH RS ' return to character mode
RETURN
' Listing 2b
' Nuts & Volts: Stamp Applications, April 1998
' Replace sections of Listing 2a with this code
' -----[ EEPROM Data ]----------------------------
'
EEPROM ("< NUTS & VOLTS >")
' -----[ Main Code ]------------------------------
'
Start: FOR index = 0 TO 15
READ index, char
GOSUB LCDwr
NEXT
END
