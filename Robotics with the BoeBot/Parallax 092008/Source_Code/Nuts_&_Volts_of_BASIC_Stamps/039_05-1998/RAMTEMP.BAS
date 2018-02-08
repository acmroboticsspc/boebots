' Program Listing 39.1
' Nuts & Volts: Stamp Applications, May 1998

' ----[ Title ]-----------------------------------------------------------
'
' File...... RAMTEMP.BAS
' Purpose... Measure, store and display 90 temperature readings
' Author.... Jon Williams
' E-mail.... jonwms@aol.com
' WWW....... http://members.aol.com/jonwms
' Started... 28 MAR 98
' Updated...  5 APR 98

' ----[ Program Description ]---------------------------------------------
'
' Digital thermometer displays current, high and low temperatures, and
' a graph of the last 90 temperature readings (~one per minute)
'
' Temperature read from a Dallas Semiconduction DS1620
' Temperatures stored in a Solutions Cubed RAMPack B
' Temperatures and graph displayed on a SEETRON G12854 graphic LCD
'
' 
' G12864 Configuration Switch Settings:
' 1 : Off	(Run)
' 2 : Off	(2400 baud)
' 3 : On	(BL On) - do not power from Stamp (use seperate supply)
' 4 : Off	(Esc)
' 5 : Off	(Protect EE - must be On to download custom graphics)
' 6 : On	(Screen 2)

' ----[ Revision History ]------------------------------------------------
'
'  5 MAR 98 : Version 1.0 complete

' ----[ Constants ]-------------------------------------------------------
'
'  I/O pins
'
SYMBOL 	DQ      = Pin0        		' needs to match DQPin definition
SYMBOL	DQpin   = 0       		' DS1620 data I/O
SYMBOL	CLK     = 1          		' DS1620 Clock input
SYMBOL	RST     = 2        		' DS1620 Reset input
SYMBOL	LCDpin	= 6			' serial connection to G12864
SYMBOL	RAMpin	= 7			' serial connection to RAMPack B

' DS1620 commands
'
SYMBOL  TEMPR   = $AA       		' Read temperature
SYMBOL  THW     = $01     		' Write TH (high temp register)
SYMBOL  TLW     = $02  		' Write TL (low temp register)
SYMBOL  THR     = $A1           	' Read TH
SYMBOL  TLR     = $A2            	' Read TL
SYMBOL  START   = $EE           	' Start temperature conversion
SYMBOL  STOP    = $22          	' Stop temperature conversion
SYMBOL  CFGW    = $0C          	' Write configuration register
SYMBOL  CFGR    = $AC             	' Read configuration register

' RAMPack commands
'
SYMBOL	WrByte	= $00
SYMBOL	RdByte	= $01

' G12864 controls
'
SYMBOL	Esc	=  27
SYMBOL	ClrLCD	=  12			' Clear LCD text screen
SYMBOL	PosCmd	=  16			' Position cursor
SYMBOL	TposC	=  65			' temperature locations in G12864
SYMBOL	TPosH	=  97
SYMBOL	TPosL	= 113
SYMBOL	X1	= 101			' X1 = 37
SYMBOL	X2	= 190			' X2 = 126
SYMBOL	White	=  64			' Ink colors
SYMBOL	Black	=  65

SYMBOL	OneSec	= 895			' *tuned* PAUSE value


' ----[ Variables ]-------------------------------------------------------
'
SYMBOL  cmdByte = B0              	' command sent to DS1620
SYMBOL  loTmpC  = B0              	' lo byte of temperature
SYMBOL  hiTmpC  = B1            	' high byte (sign) of temperature
SYMBOL  tempC   = W0               	' temperature (needs 9 bits)
SYMBOL  tempF   = B2             	' temperature in Fahrenheit
SYMBOL	tHigh	= B4			' highest temperature reading
SYMBOL	tLow	= B5			' lowest temperature reading
SYMBOL	secs	= B6			' seconds counter
SYMBOL	mins	= B7			' minutes counter
SYMBOL	shift	= B3			' loop counter
SYMBOL	x	= B3
SYMBOL	addrLo	= B8			' RAM address
SYMBOL	addrHi	= B9
SYMBOL	y	= B10			' graphing coordinate

' ----[ EEPROM Data ]-----------------------------------------------------
'


' ----[ Initialization ]--------------------------------------------------
'
Init:   Dirs = %10000111        	' setup I/O pins
	Pins = %10000000

	' initialize the DS1620
	'
	HIGH RST              		' alert the DS1620
	cmdByte = CFGW       		' prepare to write config
	GOSUB CmdOut  
	cmdByte = %00000010       	' use with CPU; free run mode
	GOSUB CmdOut
	LOW RST
	PAUSE 10                	' wait 10 ms for EEPROM write
	HIGH RST               	' alert the DS1620
	cmdByte = START
	GOSUB CmdOut               	' start continuous conversion
	LOW RST                  	' end DS1620 commo

	' clear RamPack storage area
	PAUSE 500
	FOR addrLo = 0 TO 89
	  SEROUT RAMpin,T2400,($55,WrByte,$00,addrLo,$00)
	  PAUSE 10
	NEXT addrLo

	tLow = 255			' initialize low temp


' ----[ Main Code ]-------------------------------------------------------
'
Main: 	HIGH RST             		' alert the DS1620
	cmdByte = TEMPR       		' prep for temperature read
	GOSUB CmdOut
	GOSUB TempIn            	' get temp from the DS1620
	LOW RST                 	' end DS1620 commo

	' convert to Fahrenheit
	' (32 to 120 deg F)
	tempF = loTmpC * 9 + 325 / 10 MAX 120

	IF tempF <= tHigh THEN ChkLow
	tHigh = tempF			' update new high temp
ChkLow:	IF tempF >= tLow THEN ShoTmp
	tLow = tempF			' update new low temp

ShoTmp:	SEROUT LCDpin,N2400,(PosCmd,TPosC,#tempF)
	SEROUT LCDpin,N2400,(PosCmd,tPosH,#tHigh,PosCmd,tPosL,#tLow)

	PAUSE OneSec

	secs = secs + 1 // 60		' update seconds
	IF secs > 0 THEN Main		' check for minutes update

Main2:	mins = mins + 1 // 90		' update minutes
	' record the latest temp
	SEROUT RAMpin,T2400,($55,WrByte,$00,mins,tempF)
	PAUSE 10	
	GOSUB ShGrph			' update temperature graph
	secs = secs + 10		' make-up for graphing delay
	GOTO Main


' ----[ Subroutines ]-----------------------------------------------------
'

' Send command to DS1620
' 8-bit command is sent LSB -> MSB
'
CmdOut: OUTPUT DQpin             	' make DQ an output
	FOR shift = 1 TO 8    		' shift eight bits
	  LOW CLK
	  DQ = Bit0              	' output LSB of cmdByte
	  HIGH CLK
	  cmdByte = cmdByte / 2    	' shift cmd for next bit
	NEXT shift
	RETURN


' Retrieve temperature from DS1620
' Data comes in LSB -> MSB in 9 bits
' - 1 bit for sign
' - 8 bits for temp (in 2's compliment format if negative)
'
TempIn: INPUT DQpin              	' make DQ an input
	FOR shift = 1 TO 9
	  tempC = tempC / 2      	' shift temp bits
	  LOW CLK
	  Bit8 = DQ              	' get data bit
	  HIGH CLK
	NEXT shift
	RETURN

' Get last 90 readings from RamPack
' Draw temperature graph on G12864
' 
ShGrph:	addrLo = mins			' current reading on far right
	FOR x = X2 TO X1 STEP -1

	  ' clear the current display line
	  SEROUT LCDpin,N2400,(Esc,"I",White,Esc,"L",x,125,x,66)

	  ' get reading from RAMPack
	  SEROUT RAMpin,T2400,($55,RdByte,$00,addrLo)
	  SERIN  RAMpin,T2400,tempF

	  IF tempF = 0 THEN nxtAddr	
	  ' calculate top of line and graph the temperature
	  y = 122 - tempF / 2 + 64
	  SEROUT LCDpin,N2400,(Esc,"I",Black,Esc,"L",x,125,x,y)

	  ' update RAM address pointer (with wrap-around)
nxtAddr:  addrLo = addrLo + 89 // 90
	NEXT x
	RETURN 
