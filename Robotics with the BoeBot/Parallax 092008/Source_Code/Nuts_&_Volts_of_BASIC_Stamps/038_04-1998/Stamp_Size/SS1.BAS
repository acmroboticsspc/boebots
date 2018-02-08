' -----[ Title ]-----------------------------------------------------------
'
' File...... SS1.BAS
' Purpose... Displays LCD strings with LOOKUP table
'            (demonstration program for STMPSIZE.EXE)
' Author.... Jon Williams
' E-mail.... jonwms@aol.com
' WWW....... http://members.aol.com/jonwms
' Started... 04 JUL 94
' Updated... 28 JUL 96


' -----[ Program Description ]---------------------------------------------
'
' Stamp -> LCD interface skeleton code
'
' LCD Connections:
'
' LCD        (Function)         Stamp
' ---------------------         -----
' pin 1         Vss             gnd
' pin 2         Vdd             +5
' pin 3         Vo              gnd (or wiper of 10K trimmer pot)
' pin 4         RS              pin 4
' pin 5         R/W             gnd
' pin 6         E               pin 5
' pin 7         DB0             gnd
' pin 8         DB1             gnd
' pin 9         DB2             gnd
' pin 10        DB3             gnd
' pin 11        DB4             pin 0
' pin 12        DB5             pin 1
' pin 13        DB6             pin 2
' pin 14        DB7             pin 4


' -----[ Revision History ]------------------------------------------------
'
' 28 JUL 96 : Optimized LCD output routines


' -----[ Constants ]-------------------------------------------------------
'
' control pins
'
SYMBOL  E       = 5                     ' LCD Enable pin 
SYMBOL  RS      = 4                     ' LCD Register Select
'
' LCD control characters
'
SYMBOL  ClrLCD  = $01                   ' clear the LCD
SYMBOL  CrsrHm  = $02                   ' home cursor
SYMBOL  CrsrLf  = $10                   ' move cursor left
SYMBOL  CrsrRt  = $14                   ' move cursor right
SYMBOL  DispLf  = $18                   ' shift display left
SYMBOL  DispRt  = $1C                   ' shift display right
'
' LCD addresses
'
SYMBOL  Line1   = $80                   ' start of line 1
SYMBOL  Line2   = $C0                   ' start of line 2
SYMBOL  Line3   = $94                   ' start of line 3
SYMBOL  Line4   = $D4                   ' start of line 4


' -----[ Variables ]-------------------------------------------------------
'
SYMBOL  index   = B1                    ' loop counter
SYMBOL  char    = B2                    ' character sent to LCD


' -----[ Initialization ]--------------------------------------------------
'
	BSAVE                           ' save EEPROM image for analysis

Init:   Pins = %00000000                ' clear the pins
	Dirs = %00111111                ' 0 - 5 are outputs
	PAUSE 15                        ' let the LCD settle


' Initialize the LCD (Hitatchi HD44780 controller)
'
LCDini: Pins = %00000011                ' 8-bit mode
	PULSOUT E, 1
	PAUSE 5
	PULSOUT E, 1
	PULSOUT E, 1
	Pins = %00000010                ' 4-bit mode
	PULSOUT E, 1
	' char = %00101000              ' 2-line mode
	' GOSUB LCDcmd
	char = %00001100                ' disp on, crsr off, blink off
	GOSUB LCDcmd                     
	char = %00000110                ' inc crsr, no disp shift
	GOSUB LCDcmd
	char = ClrLCD                   ' clear LCD
	GOSUB LCDcmd


' -----[ Main Code ]-------------------------------------------------------
'
Main:   FOR index = 0 TO 13
	  LOOKUP index,("INTRODUCING..."),char
	  GOSUB WrLCD
	NEXT index
	PAUSE 2000

	char = ClrLCD
	GOSUB LCDcmd

	FOR index = 0 TO 15
	  LOOKUP index,("THE BASIC STAMP!"),char
	  GOSUB WrLCD
	NEXT index
	PAUSE 2000

	char = ClrLCD
	GOSUB LCDcmd

	GOTO Main                       ' play it again, Sam


' -----[ Subroutines ]-----------------------------------------------------
'
' Send command to the LCD
'
LCDcmd: LOW RS                          ' enter command mode

' Write ASCII char to LCD 
'
WrLCD:  Pins = Pins & %11010000         ' save 7, 6 & RS, clear data
	Pins = char / 16 | Pins         ' output high nibble of char
	PULSOUT E, 1                    ' strobe the Enable line
	Pins = Pins & %11010000
	Pins = char & $0F | Pins        ' output low nibble of char
	PULSOUT E, 1
	HIGH RS                         ' return to character mode
	RETURN
