' Program Listing 43.1
' Stamp Applications: Nuts & Volts, September 1998

' ----[ Title ]-----------------------------------------------------------
'
' File...... DIGIO.BAS
' Purpose... 74HC165/74HC595 Demo
' Author.... Jon Williams
' E-mail.... jonwms@aol.com
' WWW....... http://members.aol.com/jonwms
' Started... 02 AUG 98
' Updated... 02 AUG 98


' ----[ Program Description ]---------------------------------------------
'
' Demonstrates digital I/O expansion using readily available shift
' registers.


' ----[ Revision History ]------------------------------------------------
'
' 02 AUG 98 : Rev 1 complete


' ----[ Constants ]-------------------------------------------------------
'
SYMBOL	Clk	= 0			' clock
SYMBOL	DPin	= 1			' data pin
SYMBOL	Data	= Pin1			' data bit
SYMBOL	_165	= 6			' HC165 strobe
SYMBOL	_595	= 7			' HC595 strobe


' ----[ Variables ]-------------------------------------------------------
'
SYMBOL	temp	= B0			' using for shift in/out
SYMBOL	shift	= B2			' shift counter


' ----[ EEPROM Data ]-----------------------------------------------------
'


' ----[ Initialization ]--------------------------------------------------
'
Init:	Pins = %11000000		' _595 & _165 = H, Clk = L
	Dirs = %11000001


' ----[ Main Code ]-------------------------------------------------------
'
Main:	GOSUB DigIn			' get HC165 inputs
	DEBUG #%temp,cr		' display
	PAUSE 100
	GOTO Main
	END


' ----[ Subroutines ]-----------------------------------------------------
'

DigIn:	PULSOUT _165, 5		' load the inputs
	INPUT DPin			' prepare for data in
	FOR shift = 1 TO 8		' shift 8 bits
	  temp = temp * 2		' rotate left (MSB first)
	  Bit0 = Data			' get a bit
	  PULSOUT Clk, 5		' clock out the next bit
	NEXT
	RETURN


DigOut:	OUTPUT DPin			' prepare for data out
	FOR shift = 1 TO 8		' shift 8 bits
	  Data = Bit7			' MSB first
	  PULSOUT Clk, 5		' clock the bit
	  temp = temp * 2		' get next bit
	NEXT
	PULSOUT _595, 5		' bits -> outputs
	RETURN 
