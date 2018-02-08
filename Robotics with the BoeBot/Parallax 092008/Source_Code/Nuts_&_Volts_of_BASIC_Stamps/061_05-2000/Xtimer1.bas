' Stamp Applications - May 2000
' Listing 1


' -----[ Title ]-----------------------------------------------------------
'
' File...... XTIMER1.BAS
' Purpose... Exercise Timer - Hardware test code
' Author.... Jon Williams


' -----[ Program Description ]---------------------------------------------
'
' Hardware test code for exercise timer project


' -----[ I/O Pins ]--------------------------------------------------------
'
SYMBOL	BprPin = 7


' -----[ Constants ]-------------------------------------------------------
'
SYMBOL	BprTone = 75
SYMBOL	BprLen = 16				' 0.192 secs


' -----[ Variables ]-------------------------------------------------------
'
SYMBOL	secs = B2
SYMBOL	loops = B3


' -----[ Initialization ]--------------------------------------------------
'
Init:	Pins = %00000000			' LEDs off to start
	Dirs = %10001111			' LEDs and Piezo outs


' -----[ Main Code ]-------------------------------------------------------
'
Main:	Pins = %0001				' stage 1 LED on
	SOUND BprPin,(BprTone, BprLen)		' sound start of stage
	secs = 120				' 2-minute stage
	GOSUB DlySec

	Pins = %0010				' stage 2
	SOUND BprPin,(BprTone, BprLen)
	secs = 60
	GOSUB DlySec

	Pins = %0100				' stage 3
	SOUND BprPin,(BprTone, BprLen)
	GOSUB DlySec

	Pins = %1000				' stage 4
	SOUND BprPin,(BprTone, BprLen)
	GOSUB DlySec

Done:	Pins = %0000				' LEDs off
	SOUND BprPin,(0,12,50,12,75,12,110,12)	' sound end

	END ' of program


' -----[ Subroutines ]-----------------------------------------------------
'
DlySec:	FOR loops = 1 TO secs
	  ' PAUSE 1000				' pause 1 second
	  PAUSE 10				' quick pause for testing
	NEXT
	RETURN
