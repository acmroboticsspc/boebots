' Stamp Applications - May 2000
' Listing 3


' -----[ Title ]-----------------------------------------------------------
'
' File...... XTIMER2b.BAS
' Purpose... Exercise Timer - Version 2 (refined main loop)
' Author.... Jon Williams


' -----[ Program Description ]---------------------------------------------
'
' This program serves as a multi-stage exercise timer.  The program runs
' four cycles of four stages, with an LED and audio indication for each
' stage.


' -----[ I/O Pins ]--------------------------------------------------------
'
SYMBOL	BprPin = 7


' -----[ Constants ]-------------------------------------------------------
'
SYMBOL	BprTone = 75
SYMBOL	BprLen = 16				' 0.192 secs


' -----[ Variables ]-------------------------------------------------------
'
SYMBOL	cycle = B2				' cycles counter
SYMBOL	stage = B3				' stage counter
SYMBOL	secs= B4				' stage timing (seconds)
SYMBOL	loops = B5				' counter for long delay


' -----[ Initialization ]--------------------------------------------------
'
Init:	Pins = %00000000			' LEDs off to start
	Dirs = %10001111			' LEDs and Piezo outs


' -----[ Main Code ]-------------------------------------------------------
'
Main:	FOR cycle = 1 TO 4
	  FOR stage = 0 TO 3
	    LOOKUP stage,(%0001,%0010,%0100,%1000),Pins
	    SOUND BprPin,(BprTone, BprLen)
	    LOOKUP stage,(120,60,60,60),secs
	    GOSUB DlySec
	  NEXT ' stage
	NEXT ' cycle

Done:	Pins = %0000				' LEDs off
	SOUND BprPin,(0,12,50,12,75,12,110,12)	' sound end

	END ' of program


' -----[ Subroutines ]-----------------------------------------------------
'
DlySec:	FOR loops = 1 TO secs
	  PAUSE 1000				' pause 1 second
	  ' PAUSE 10				' quick pause for testing
	NEXT
	RETURN
