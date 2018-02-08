' Listing 3
' Nuts & Volts: Stamp Applications, October 1997


' -----[ Title ]-----------------------------------------------------------
'
' File...... LCD_5x10.BAS
' Purpose... Stamp 1 -> LCD (4-bit interface)
' Author.... Jon Williams
' E-mail.... jonwms@aol.com
' WWW....... http://members.aol.com/jonwms
' Started... 16 JUL 1994
' Updated... 28 AUG 1997


' -----[ Program Description ]---------------------------------------------
'
' This program demonstrates the various standard features of a 1x16 LCD
' display that uses the Hitachi HD44780 controller. The display is con-
' figured to use the 5x10 character set, thus allowing proper decenders on
' the characters "g", "j", "q", "p" and "y". 
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
' 25 JUL 97 : Updated for Nuts & Volts
' 28 AUG 97 : Added ASCII translation routine of decender characters


' -----[ Constants ]-------------------------------------------------------
'
SYMBOL  E       = 5                     ' LCD enable pin (1 = enabled)
SYMBOL  RS      = 4                     ' Register Select (1 = char)

' LCD control characters
'
SYMBOL  ClrLCD  = $01                   ' clear the LCD
SYMBOL  CrsrHm  = $02                   ' move cursor to home position
SYMBOL  CrsrLf  = $10                   ' move cursor left
SYMBOL  CrsrRt  = $14                   ' move cursor right
SYMBOL  DispLf  = $18                   ' shift displayed chars left
SYMBOL  DispRt  = $1C                   ' shift displayed chars right
SYMBOL	DDRam	= $80			' Display Data Ram control
SYMBOL	CGRam	= $40			' Char Gen Ram control

' DDROM codes for characters with decenders
'
SYMBOL	_g	= $E7
SYMBOL	_j	= $EA
SYMBOL	_p	= $F0
SYMBOL	_q	= $F1
SYMBOL	_y	= $F9

 
' -----[ Variables ]-------------------------------------------------------
'
SYMBOL  char    = B1                    ' character sent to LCD
SYMBOL  index   = B2                    ' loop counter


' -----[ EEPROM Data ]-----------------------------------------------------
'
	' test messages with decenders

	EEPROM ("Heeeere's Jonn",_y,"!")
	EEPROM ("Jolly good!")		' needs ASCII translation
	EEPROM ("quick... jump!")	'   "     "        "


' -----[ Initialization ]--------------------------------------------------
'

Init:   Pins = %00000000                ' clear the pins
	Dirs = %00111111                ' set 0-5 as outputs


' Initialize the LCD (Hitatchi HD44780 controller)
'
LCDini: PAUSE 500                       ' let the LCD settle
	Pins = %0011                    ' 8-bit mode
	PULSOUT E, 1
	PAUSE 5
	PULSOUT E, 1
	PULSOUT E, 1
	Pins = %0010                    ' 4-bit mode
	PULSOUT E, 1
	char = %00100100                ' 5x10 font
	GOSUB LCDcmd
	char = %00001100                ' disp on, crsr off, blink off
	GOSUB LCDcmd                     
	char = %00000110                ' inc crsr, no disp shift
	GOSUB LCDcmd


' -----[ Main Code ]-------------------------------------------------------
'
Start:	char = ClrLCD
	GOSUB LCDcmd

	' message 1

	FOR index = 0 TO 15
	  READ index,char		' get char from EEPROM
	  GOSUB LCDwr			' write it
	NEXT index

	PAUSE 1500

	char = ClrLCD
	GOSUB LCDcmd

	' message 2

	FOR index = 16 TO 26
	  READ index,char
	  GOSUB XLate			' translate from ASCII
	  GOSUB LCDwr
	NEXT index

	PAUSE 1500

	char = ClrLCD
	GOSUB LCDcmd

	' message 3

	FOR index = 27 TO 40
	  READ index,char
	  GOSUB XLate			' translate from ASCII
	  GOSUB LCDwr
	NEXT index

	PAUSE 1500

	GOTO Start                      ' do it all over


' -----[ Subroutines ]-----------------------------------------------------
'
' Translate ASCII code for characters with decenders
' -- has no affect on other characters
'
Xlate:	LOOKDOWN char, ("g","j","q","p","y"), char
	LOOKUP char, (_g,_j,_q,_p,_y), char
	RETURN

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

