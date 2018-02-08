' Program Listing 34.1
' Stamp Applications: Nuts & Volts, December 1997

' ----[ Title ]-----------------------------------------------------------
'
' File...... BS1CLOCK.BAS
' Purpose... Stamp 1-based Alarm Clock
' Author.... Jon Williams
' E-mail.... jonwms@aol.com
' WWW....... http://members.aol.com/jonwms
' Started... 25 OCT 97
' Updated... 26 OCT 97


' ----[ Program Description ]---------------------------------------------
'
' This program demonstrates basic timekeeping functions using the Pocket
' Watch B real-time clock module from Solutions Cubed. A Scott Edwards'
' Electronics serial backpack is used for the display


' ----[ Revision History ]------------------------------------------------
'
' 26 OCT 97 : Completed and working


' ----[ Constants ]-------------------------------------------------------
'
' ===================
' I/O Pin Definitions
' ===================
'
SYMBOL	SetHr	= 0
SYMBOL	SetMn	= 1
SYMBOL	TmAlrm	= Pin2

' ==============
' Pocket Watch B
' ==============
'
SYMBOL	ClkIO	= 3
SYMBOL	ClkBaud	= T2400

' command set
SYMBOL	WrTime	= $00			' write time -- standard
SYMBOL	WrAlrm	= $01			' write alarm -- standard
SYMBOL	RdTime	= $02			' read time -- standard
SYMBOL	RdAlrm	= $03			' read alarm -- standard
SYMBOL	Alrm1	= $04			' alarm on
SYMBOL	Alrm0	= $05			' alarm off
SYMBOL	WrXTm	= $10			' write time -- extended
SYMBOL	WrXAlrm= $11			' write alarm -- extended
SYMBOL	RdXTm	= $12			' read time -- extended
SYMBOL	RdXAlrm	= $13			' read alarm -- extended
SYMBOL	Alrm1X	= $14			' alarm on -- extended
SYMBOL	Alrm0X	= $15			' alarm off -- extended
SYMBOL	SSAlrm	= $16			' single-shot alarm
SYMBOL	SAAlrm	= $17			' short astable alarm
SYMBOL	LAAlrm	= $18			' long astable alarm
SYMBOL	GetAlrm	= $19			' get alarm characteristics	


' =======================
' SEE Serial Backpack LCD
' =======================
'
SYMBOL	LcdIO	= 4
SYMBOL	LcdBaud	= N2400

SYMBOL  I       = $FE			' backpack instruction toggle
SYMBOL  ClrLCD  = $01			' clear the LCD
SYMBOL  CrsrHm  = $02			' move cursor to home position
SYMBOL  CrsrLf  = $10			' move cursor left
SYMBOL  CrsrRt  = $14			' move cursor right
SYMBOL  DispLf  = $18			' shift displayed chars left
SYMBOL  DispRt  = $1C			' shift displayed chars right
SYMBOL  DDRam   = $80			' Display Data Ram control
SYMBOL  CGRam   = $40			' Char Gen Ram control


SYMBOL	BtnDly	= 35			' delay for BUTTON loop


' ----[ Variables ]-------------------------------------------------------
'
SYMBOL	secs	= B0
SYMBOL	mins	= B1
SYMBOL	hrs	= B2
SYMBOL	xx	= B3			' space holder for date registers
SYMBOL	temp1	= B4
SYMBOL	temp2	= B5
SYMBOL	butn	= B6			' BUTTON workspace
SYMBOL	rawTm	= W4			' raw time (minutes past midnight)

' ----[ EEPROM Data ]-----------------------------------------------------
'


' ----[ Initialization ]--------------------------------------------------
'
Init:	Dirs = %00011000
	Pins = %00001000
	PAUSE 1000			' let clock and LCD initialize


' ----[ Main Code ]-------------------------------------------------------
'
Start:	GOSUB GetTm			' get the clock
	GOSUB ShowTm			' show clock on backpack lcd
	
	butn = 0
ChkHr:	BUTTON SetHr,0,150,10,butn,0,ChkMn	' is Set Hours pressed?
	GOSUB GetTm				' yes, get the clock
	rawTm = rawTm + 60 // 1440
	GOSUB SetTm				' set new time
	GOSUB ShowTm				' display the change
	PAUSE BtnDly				' pause between changes
	GOTO ChkHr				' still pressed?

ChkMn:	BUTTON SetMn,0,150,10,butn,0,Start	' is Set Minutes pressed?
	GOSUB GetTm				' yes, get the clock
	rawTm = rawTm + 1 // 1440
	GOSUB SetTm				' set new time
	GOSUB ShowTm				' display the change
	PAUSE BtnDly				' pause between changes
	GOTO ChkMn				' still pressed?

	GOTO Start


' ----[ Subroutines ]-----------------------------------------------------
'
GetTm:	temp1 = $02 + TmAlrm			' time ($02) or alarm ($03)
	SEROUT ClkIO,ClkBaud,($55,temp1)
	' note that we ignore day, month and year
	SERIN  ClkIO,ClkBaud,secs,mins,hrs,xx,xx,xx
	rawTm = hrs * 60 + mins		' update raw time value
	RETURN

SetTm:	hrs = rawTm / 60			' get hours from raw time
	mins = rawTm // 60			' get minutes from raw time
	' set the clock time
	' (alarm if "TmAlrm" input is high)
	temp1 = $10 + TmAlrm			' calculate command code
	SEROUT ClkIO,ClkBaud,($55,temp1,0,mins,hrs,10,18,97)
	IF TmAlrm = 0 THEN Done
	' reset the alarm output duration
	' every 24 hours for 1 minute
	' (the Pocket Watch B increments each value by 1)
	SEROUT ClkIO,ClkBaud,($55,LAAlrm,23,0)
	' enable the alarm
	SEROUT ClkIO,ClkBaud,($55,Alrm1X)
Done:	RETURN

ShowTm:	SEROUT LcdIO,LCDBaud,(I,ClrLCD)	' clear the lcd
	temp1 = hrs / 10 			' get the hours 10s digit
	temp2 = hrs // 10			' get the hours 1s digit
	SEROUT LcdIO,LCDBaud,(#temp1,#temp2,":")
	temp1 = mins / 10			' get the mins 10s digit
	temp2 = mins // 10			' get the mins 1s digit
	SEROUT LcdIO,LCDBaud,(#temp1,#temp2)
	' remove next 3 lines to conserve code space
	temp1 = secs / 10			' get the secs 10s digit
	temp2 = secs // 10			' get the secs 1s digit
	SEROUT LcdIO,LCDBaud,(":",#temp1,#temp2)
	RETURN