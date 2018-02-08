' -----[ Title ]-----------------------------------------------------------
'
' File...... LCD_595.BAS
' Purpose... Stamp 1 -> 74HC595 -> LCD (4-bit interface)
' Author.... Jon Williams
' E-mail.... jonwms@aol.com
' WWW....... http://members.aol.com/jonwms
' Started... 16 JUL 1994
' Updated... 29 AUG 1997


' -----[ Program Description ]---------------------------------------------
'
' This program demonstrates the the connection of a standard LCD to the
' Stamp through a 74HC595 shift register. Note that this code chews up
' nearly all of the BS1 GOSUB stack.
'
' Connections:
'   NC = Not Connected
'   PU = Pulled Up to +5V through 10K resistor
'
' Stamp 1   74HC595   LCD       Notes
' -------   -------   -------   ---------------- 
'            1 NC
'            2         6 (E)
'            3         4 (RS)
'            4        11 (D4)
'            5        12 (D5)
'            6        13 (D6)
'            7        14 (D7)
'            8 GND 
'            9 NC 
'           10 PU
' Pin 1     11                  Shift clock
' Pin 2     12                  Output latch
'           13 GND 
' Pin 0     14                  Shift data 
'           15 NC
'           16 Vcc


' -----[ Revision History ]------------------------------------------------
'
' 25 AUG 97 : Modified standard LCD demo to use 74HC595
' 27 AUG 97 : Added custom character
' 29 AUG 97 : Ported from BS2 to BS1


' -----[ Constants ]-------------------------------------------------------
'
SYMBOL	SData	= Pin0			' 74HC595 serial data  (14)
SYMBOL	Clk	= 1			' 74HC595 shift clock  (11)
SYMBOL	Latch	= 2			' 74HC595 output latch (12) 

' LCD control characters
'
SYMBOL  ClrLCD  = $01                   ' clear the LCD
SYMBOL  CrsrHm  = $02                   ' move cursor to home position
SYMBOL  CrsrLf  = $10                   ' move cursor left
SYMBOL  CrsrRt  = $14                   ' move cursor right
SYMBOL  DispLf  = $18                   ' shift displayed chars left
SYMBOL  DispRt  = $1C                   ' shift displayed chars right
SYMBOL  DDRam   = $80                   ' Display Data RAM control
SYMBOL  CGRam   = $40                   ' Char Gen RAM control

 
' -----[ Variables ]-------------------------------------------------------
'
SYMBOL	temp	= B0			' work variable for LCD routines
SYMBOL	char	= B2                    ' character sent to LCD
SYMBOL	index	= B3			' loop counter
SYMBOL	shift	= B4			' loop counter for SOut

SYMBOL	lcd_E	= Bit2			' LCD Enable pin
SYMBOL	lcd_RS	= Bit3			' Register Select (1 = char)
SYMBOL	RS	= Bit8			' holds our RS value


' -----[ EEPROM Data ]-----------------------------------------------------
'
	EEPROM (0," NUTS & VOLTS ",0)	' preload EEPROM with message

	' custom character map
	' -- character code will be 0

	EEPROM (%00000000)		' . . . . . . . .
	EEPROM (%00001010)		' . . . . * . * .
	EEPROM (%00001010)		' . . . . * . * .
	EEPROM (%00000000)		' . . . . . . . .
	EEPROM (%00010001)		' . . . * . . . *
	EEPROM (%00001110)		' . . . . * * * .
	EEPROM (%00000110)		' . . . . . * * .
	EEPROM (%00000000)		' . . . . . . . .


' -----[ Initialization ]--------------------------------------------------
'
Init:	Dirs = %00000111
	Pins = %00000000

' Initialize the LCD (Hitachi HD44780 controller)
'
LCDini: PAUSE 500                       ' let the LCD settle
	char = %0011                    ' 8-bit mode
	GOSUB LCDcmd
	PAUSE 5
	GOSUB LCDcmd
	GOSUB LCDcmd
	char = %0010                    ' put in 4-bit mode
	GOSUB LCDcmd
	char = %00001100                ' disp on, crsr off, blink off
	GOSUB LCDcmd                     
	char = %00000110                ' inc crsr, no disp shift
	GOSUB LCDcmd

	' download custom character map to LCD

	char = CGRam			' point to CG RAM
	GOSUB LCDcmd                    ' prepare to write CG data
	FOR index = 16 TO 23
	  READ index, char		' get byte from EEPROM
	  GOSUB LCDwr			' put into LCD CG RAM
	NEXT


' -----[ Main Code ]-------------------------------------------------------
'
Start:  char = ClrLCD
	GOSUB LCDcmd

	FOR index = 0 TO 15		' put message on LCD
	  READ index, char		' get character from EEPROM
	  GOSUB LCDwr                   ' write it
	NEXT
	PAUSE 1000

	FOR index = 1 TO 16		' shift message off screen
	  char = DispRt
	  GOSUB LCDcmd
	NEXT
	PAUSE 500

	GOTO Start


' -----[ Subroutines ]-----------------------------------------------------
'
' Send command to the LCD
'
LCDcmd: RS = 0				' command mode
	GOTO LCDout

' Write ASCII char to LCD 
'
LCDwr:	RS = 1				' character mode

LCDout:	temp = char & $F0		' char.HIGHNIB -> temp.HIGHNIB
	lcd_RS = RS			' set RS
	lcd_E = 1
	GOSUB SOut
	lcd_E = 0			' blip Enable line
	GOSUB SOut
	temp = char * 16		' char.LOWNIB -> temp.HIGHNIB
	lcd_RS = RS			' set RS
	lcd_E = 1
	GOSUB SOut
	lcd_E = 0
	GOSUB SOut
	RETURN

SOut:	FOR shift = 1 TO 8		' shift 8 bits
	  sData = Bit7			' MSB first
	  PULSOUT Clk, 1		' clock the bit
	  temp = temp * 2		' get next bit
	NEXT
	PULSOUT Latch, 1
	RETURN
