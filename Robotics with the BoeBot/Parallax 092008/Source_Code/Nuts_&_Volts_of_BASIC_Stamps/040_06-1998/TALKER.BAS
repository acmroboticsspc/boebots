' Program Listing 40.1
' Nuts & Volts: Stamp Applications, June 1998

' ----[ Title ]-----------------------------------------------------------
'
' File...... TALKER.BAS
' Purpose... BASIC Stamp -> SP0256-AL2 Allophone Speech Processor
' Author.... Jon Williams
' E-mail.... jonwms@aol.com
' WWW....... http://members.aol.com/jonwms
' Started... 03 MAY 98
' Updated... 03 MAY 98

' ----[ Program Description ]---------------------------------------------
'
' This program enables the BASIC Stamp to talk by interfacing with the GI
' SP0256-AL2 Allophone Speech Processor.
'
' A 74HC164 parallel output shift register is used to provide allophone
' address to the SP0256-AL2. Four I/O lines are used:
'
' Pin 0 (Out)	Clock to 74HC164 (can be shared)
' Pin 1	(Out)	Data to 74HC164 (can be shared)
' Pin 2	(Out)	ALD - pulsed low causes allophone address to be loaded
' Pin 3	(In)	LRQ - goes high when the SP0256-AL2 address buffer is full

' ----[ Revision History ]------------------------------------------------
'
' 03 MAY 98 : Version 1

' ----[ Constants ]-------------------------------------------------------
'
SYMBOL	Clk	= 0
SYMBOL	Dio	= Pin1
SYMBOL	ALD	= 2			' Address Load
SYMBOL	LRQ	= Pin3			' Load Request


' Copy and Paste "ALLOPHON.BAS" here


' ----[ Variables ]-------------------------------------------------------
'
SYMBOL	data	= B0			' allophone data
SYMBOL	shift	= B2			' loop counter for shift out
SYMBOL	addr	= B3			' EE address of allophone


' ----[ EEPROM Data ]-----------------------------------------------------
'
	' Hello,
	EEPROM	(HH1,EH,LL,AX,OW,PA4)
	' I am the
	EEPROM	(AA,AY,PA2,AE,MM,PA2,DH1,AX,PA2)
	' BASIC Stamp
	EEPROM	(BB2,EY,SS,IH,KK2,PA3,SS,SS,PA1,TT2,AE,AE,MM,PP,PA1,$FF)


' ----[ Initialization ]--------------------------------------------------
'
Init:	Pins = %00000100		' begin with ALD high
	Dirs = %00000111


' ----[ Main Code ]-------------------------------------------------------
'
Main:	addr = $00			' point to start of speech
	GOSUB Speak			' speak!
	PAUSE 2000			' wait for two seconds
	GOTO Main			' speak again...

	END


' ----[ Subroutines ]-----------------------------------------------------
'

Speak:	READ addr, data		' get allophone from EE table
	IF data = $FF THEN Done	' if $FF, we're done
Busy:	IF LRQ = 1 THEN Busy		' wait if SP0256-AL2 buffer full
	FOR shift = 1 TO 8		' shift out the allophone address
	  Dio = Bit7			' get a bit
	  PULSOUT Clk, 10		' clock it out
	  data = data * 2		' left-shift the byte
	NEXT
	PULSOUT ALD, 10		' load the allophone
	addr = addr + 1		' point to next allophone address
	GOTO Speak
Done:	RETURN
