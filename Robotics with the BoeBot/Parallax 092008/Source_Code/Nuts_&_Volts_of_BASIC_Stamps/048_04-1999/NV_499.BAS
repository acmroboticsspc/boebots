' Program Listing 50.1: NV_499.BAS Self Scaling 8 Channel A/D 
'*************************************************************************
'NV_499.BAS: Automatically scaling variable gain amplifier. This software 
'multiplexes 8 analog input values to one serial ADC0831. The multiplexing 
'is done with a single low cost MM74HC4051 analog switch. It also allows 
'for the control of a self scaling variable gain amplifier based on an 
'LM358 as well as a DS1267-010. This amplifier exhibited less than 5% 
'error when using it's maximum gain of 10. Gain settings for the amplifier 
'are stored in the BS1's EEPROM memory, and were determined in the lab 
'empirically.
' 
'*************************************************************************
'BS1 Pin Definitions:
SYMBOL		CLK	= 7	'Serial clock line
SYMBOL 		DAT	= 6	'Serial data line	
SYMBOL		DATpin	= pin6	'Symbol for data line pin
SYMBOL 		CS_AD	= 5	'Chip select for ADC0831
SYMBOL 		CS_POT	= 4	'Chip select for DS1267-010
SYMBOL		SDATA	= 3	'Pin reserved for serial data output
SYMBOL 		C	= 2	'A/D channel select MSB pin	
SYMBOL 		B	= 1	'A/D channel select pin
SYMBOL 		A	= 0	'A/D channel select LSB pin
'
'BS1 Variable Definitions 
SYMBOL		DSpots 	= W0	
'Word variable for DS1267-010 dual pot settings
SYMBOL	DSpots0 		= B0	
'Pot 0 settings variable
SYMBOL	DSpots1 		= B1	
'Pot 1 settings variable(not used in this code)
SYMBOL		AD_sel	= B3	
'A/D channel select variable	
SYMBOL		Clocks 	= B4	
'Clock bit counter variable for serial communication
SYMBOL		AD_in  	= B5	
' A/D input variable used to store A/D measurements
SYMBOL		Gain	= W3	
'Stores EEPROM address of pot settings for various gains
SYMBOL		AD_data 	= W4	
' Displays voltage in 100's of micro-volts
'
'
'*************************************************************************
START:
WRITE	8,235	'Pot 0 setting for a gain of 10 stored at EEPROM address 8
WRITE	7,214	'Pot 0 setting for a gain of 9 stored at EEPROM address 7
WRITE	6,186	'Pot 0 setting for a gain of 8 stored at EEPROM address 6
WRITE	5,158	'Pot 0 setting for a gain of 7 stored at EEPROM address 5	
WRITE	4,126	'Pot 0 setting for a gain of 6 stored at EEPROM address 4
WRITE	3,98	'Pot 0 setting for a gain of 5 stored at EEPROM address 3
WRITE	2,66	'Pot 0 setting for a gain of 4 stored at EEPROM address 2
WRITE	1,38	'Pot 0 setting for a gain of 3 stored at EEPROM address 1
WRITE	0,10	'Pot 0 setting for a gain of 2 stored at EEPROM address 0

GOSUB	ANALOG		'Load first analog measurement
AD_SEL = 0		'Pre-set A/D channel to 000
PAUSE	1000		'Allow a little time for power up
'
'
'*************************************************************************
BEGIN:
GOSUB	SELECT_AD	'Select A/D channel with pins A,B,and C
GOSUB	SET_GAIN	'Set gain and take A/D measurement
GOSUB	TRANSLATE_DATA	
'Translate data into 100's of micro-volts and display	
GOTO	BEGIN
'
'
'************************************************************************* 
'SELECT_AD: This subroutine cycles through the 8 different analog 
'channels. The pin values are reset with each pass through to ensure that 
'the correct A/D is selected. The channel being accessed in located in the 
'variable AD_sel.
'
SELECT_AD:
  PINS = %11101000	'Clear lowest three bits, pins A,B, and C
  PINS = PINS + AD_sel	'Add AD_sel(0-7) to PINS register
  DEBUG AD_sel		'Display current channel selected
  AD_Sel = AD_sel + 1		
'Increment channel number for next time through
  IF AD_sel = 8 THEN Zero_AD_sel	'Make sure AD_sel never exceeds 7	
  RETURN
Zero_AD_sel:
  AD_sel = 0 		'Return AD_sel to 0 
RETURN	
'
'
'************************************************************************* 
'TRANSLATE_DATA: This subroutine translates the A/D data into a value in 
'100's of micro-volts. This is done to retain maximum resolution of the 
'measurement. Simply divide the result returned in AD_data by 10,000 to 
'determine the measurements value in volts. 
'
TRANSLATE_DATA:
LET AD_Data = AD_in * 196	
'Scale up A/D measurement by 10E3*8 bit A/D step(19.6mV/step)	 
LET AD_data = AD_data / Gain	
'Divide measurement by Gain value (10 to 2)
DEBUG 	AD_data,CR	'Display result to debug screen
RETURN
'*************************************************************************
'SET_GAIN: The SET_GAIN routine cycles through the gain values from 10 to 
'2. Any measurement that is returned that is below the upper rail of the 
'op-amp is considered valid. The LM358 upper rail is 3.5V. With a minimum 
'gain of two the greatest voltage that can be measured is 1.75Vdc. By 
'changing to a rail-to-rail output op-amp such as the LMC6132 this range 
'can be increased to 2.5Vdc. A voltage divider could also scale down 
'voltages, and allow larger voltages to be measured.
'
SET_GAIN:
Gain = 9				'Start gain test at (9-1+2) or 10
Gain_test:
IF Gain = 0 THEN Done_with_gain	'Check for minimum gain setting
LET Gain = Gain - 1			
'Subtract one from exiting Gain register
READ	Gain,DSpots0		
'Use result as address pointer for EEPROM READ
GOSUB	OUTPOT			'Set potentiometer to desired gain level
GOSUB	ANALOG			'Read analog to digital converter
IF AD_in > 178 THEN Gain_test		'
Test for value greater than 3.5V(op-amp upper rail)
Done_with_gain:				
LET Gain = Gain + 2		'Actual gain is Gain reg. + 2
DEBUG	Gain,CR			'Display Gain selected
PAUSE	10
RETURN 
'*************************************************************************
'OUTPOT: This subroutine was taken directly from a Scott Edwards 
'application note. It is also similar to the analog subroutine except that 
'17 bits are transmitted.	
'
OUTPOT:
LET DIRS = %11111111		'Make all pins outputs
HIGH CS_POT			'Select DS1267-010
LOW CLK			
LOW DAT 
PULSOUT CLK,10
FOR CLOCKS = 0 TO 15
	LET DATpin = BIT15
	PULSOUT CLK,10
	LET DSpots = DSpots * 2
NEXT
LOW CS_POT			'De-select DS1267-010
HIGH CLK
PAUSE	10
RETURN
'*************************************************************************
'ANALOG: This subroutine was taken directly from the Parallax application 
'note #2 from their data sheet. Notes on this application note can be 
'found on pages 77-79 of the Parallax BASIC Stamp data book.
'
ANALOG:
LET DIRS = %10111111		'Make DAT pin an input
LOW CLK
LOW CS_AD	 		'Select ADC0831
PULSOUT CLK,10
LET AD_in = 0
FOR CLOCKS = 0 TO 7
	LET AD_in = AD_in * 2
	PULSOUT CLK,10
	LET AD_in = AD_in + DATpin
NEXT
HIGH	CS_AD			'De-select ADC0831
HIGH	CLK
RETURN
'*************************************************************************
END: 			'End of program space