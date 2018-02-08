' Program Listing 31.3
' Nuts & Volts: Stamp Applications, September 1997

' ----[ Title ]-----------------------------------------------------------
'
' File...... LCDDEMO2.BAS
' Purpose... Stamp 1 -> Multi-line LCD (4-bit interface)
' Author.... Jon Williams
' E-mail.... jonwms@aol.com
' Started... 16 JUL 1994
' Updated... 25 JUL 1997


' ----[ Program Description ]---------------------------------------------
'
' This program demonstrates the various standard features of a 1x16 LCD
' display that uses the Hitachi HD44780 controller. 
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


' ----[ Revision History ]------------------------------------------------
'
' 16 JUL 94 : Version 1.0 - compilation of code from last 3 months
' 08 AUG 96 : Trimmed code to save space -- no performance changes!
' 25 JUL 97 : Updated for Nuts & Volts


' ----[ Constants ]-------------------------------------------------------
'
SYMBOL  RS      = 4                     ' Register Select (1 = char)
SYMBOL  E       = 5                     ' LCD enable pin (1 = enabled)

' LCD control characters
'
SYMBOL  ClrLCD  = $01                   ' clear the LCD
SYMBOL  CrsrHm  = $02                   ' move cursor to home position
SYMBOL  CrsrLf  = $10                   ' move cursor left
SYMBOL  CrsrRt  = $14                   ' move cursor right
SYMBOL  DispLf  = $18                   ' shift displayed chars left
SYMBOL  DispRt  = $1C                   ' shift displayed chars right
SYMBOL  DDRam   = $80			' Display Data RAM control
SYMBOL	Line1	= $00			' starting address of line 1
SYMBOL	Line2	= $40			' starting address of line 2
 

' ----[ Variables ]-------------------------------------------------------
'
SYMBOL  char    = B1                    ' character sent to LCD
SYMBOL  index   = B2                    ' loop counter


' ----[ EEPROM Data ]-----------------------------------------------------
'
	EEPROM ("This is Line 1")	' preload EEPROM with messages
	EEPROM ("This is Line 2")

' ----[ Initialization ]--------------------------------------------------
'
Init:	Dirs = %00111111                ' set 0-5 as outputs
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
	char = %00101000                ' 2-line mode
	GOSUB LCDcmd
	char = %00001100                ' disp on, crsr off, blink off
	GOSUB LCDcmd                     
	char = %00000110                ' inc crsr, no disp shift
	GOSUB LCDcmd


' ----[ Main Code ]-------------------------------------------------------
'
Start:	char = ClrLCD			' clear LCD, home cursor
	GOSUB LCDcmd
	PAUSE 1000

L1:	FOR index = 0 TO 13
	  READ index, char		' get character from EEPROM
	  GOSUB LCDwr                   ' write it
	  PAUSE 50                      ' delay between chars
	NEXT
	PAUSE 2000                      ' wait 2 seconds

	char = DDRam + Line2		' move to line 2
	GOSUB LCDcmd

L2:	FOR index = 14 TO 27
	  READ index, char
	  GOSUB LCDwr
	  PAUSE 50
	NEXT
	PAUSE 2000

	GOTO Start


' ----[ Subroutines ]-----------------------------------------------------
'

' Send command to the LCD
'
' Load char with command value, then call
'
'   Clear the LCD............. $01, %00000001
'   Home the cursor........... $02, %00000010
'   Display control........... (see below)
'   Entry mode................ (see below)
'   Cursor left............... $10, %00010000
'   Cursor right.............. $14, %00010100
'   Scroll display left....... $18, %00011000
'   Scroll display right...... $1C, %00011100
'   Set CG RAM address........      %01aaaaaa (Character Generator)
'   Set DD RAM address........      %1aaaaaaa (Display Data)
'
' Display control byte:
'
'   % 0 0 0 0 1 D C B
'               | | +-- blink character under cursor (1=blink)
'               | +---- cursor on/off (1=on)
'               +------ display on/off (1=on)
'
' Entry mode byte:
'
'   % 0 0 0 0 0 1 X S
'                 | +-- shift display (S=1), left (X=1), right (X=0)
'                 +---- cursor move: right (X=1), left (X=0)
'
'
LCDcmd: LOW RS                          ' enter command mode
					'  then write the character

' Write ASCII char to LCD 
'
LCDwr:	Pins = Pins & %11010000         ' save 7, 6 and RS; clear bus
	Pins = char / 16 | Pins         ' output high nibble
	PULSOUT E, 1			' strobe the Enable line
	Pins = Pins & %11010000
	Pins = char & $0F | Pins        ' output low nibble
	PULSOUT E, 1
	HIGH RS                         ' return to character mode
	RETURN
