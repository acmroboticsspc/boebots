' Program Listing 43.2
' Stamp Applications: Nuts & Volts, September 1998

' ----[ Title ]-----------------------------------------------------------
'
' File...... LTC1257.BAS
' Purpose... LTC1257 DAC Demo
' Author.... Jon Williams
' E-mail.... jonwms@aol.com
' WWW....... http://members.aol.com/jonwms
' Started... 02 AUG 98
' Updated... 02 AUG 98


' ----[ Program Description ]---------------------------------------------
'
' Demonstation program for the Linear Technologies LTC1257 12-bit DAC
' (digital to analog converter). Note that Vcc must be 2.7 volts greater
' than Vref in order to take the analog output all the way up to Vref.
' For conveniece, the 9v raw supply to the Stamp is used for Vcc while the
' regulated outpuIt from the Stamp is used for Vref.
'
' Vout = Vref / 4096 * dacValue
' Output resolution at 12 bits is 1.2 millivolts


' ----[ Revision History ]------------------------------------------------
'
' 02 AUG 98 : Rev 1 complete


' ----[ Constants ]-------------------------------------------------------
'
SYMBOL	Clk	= 0			' clock
SYMBOL	DPin	= 1			' data pin
SYMBOL	Data	= Pin1			' data bit
SYMBOL	_DAC	= 5			' LTC1257 Load\


' ----[ Variables ]-------------------------------------------------------
'
SYMBOL	temp	= W0			' using for shift in/out
SYMBOL	tempL	= B0
SYMBOL	tempH	= B1
SYMBOL	dac1	= W1			' output data
SYMBOL	dac2	= W2			' output data
SYMBOL	shift	= B6			' shift counter


' ----[ EEPROM Data ]-----------------------------------------------------
'


' ----[ Initialization ]--------------------------------------------------
'
Init:	Pins = %00100000
	Dirs = %00100011


' ----[ Main Code ]-------------------------------------------------------
'
Main:	dac1 = $07FF			' about half
	dac2 = $0FFF			' max output
	
	' output to one DAC
	temp = dac1
	GOSUB AnOut
	PULSOUT _DAC, 5		' output the voltage

	END				' remove for two units

	' output to multiple DACs
	temp = dac2			' last DAC first
	GOSUB AnOut
	temp = dac1			' closest DAC last
	GOSUB AnOut
	PULSOUT _DAC, 5		' output the voltage(s)

	END


' ----[ Subroutines ]-----------------------------------------------------
'

AnOut:	OUTPUT DPin			' prepare for output
	FOR shift = 1 TO 12		' shift 12 bits
	  Data = Bit11			' output the bit (MSB first)
	  PULSOUT Clk, 5		' clock bit into LTC1257
	  temp = temp * 2		' get next bit
	NEXT
	RETURN
