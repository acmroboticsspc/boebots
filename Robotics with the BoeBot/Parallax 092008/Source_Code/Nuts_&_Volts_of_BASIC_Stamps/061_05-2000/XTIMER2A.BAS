' Stamp Applications - May 2000
' Listing 2


' -----[ Title ]-----------------------------------------------------------
'
' File...... XTIMER2a.BAS
' Purpose... Exercise Timer - Version 2 (4 cycles, full time)
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

SYMBOL	cycle = B2
SYMBOL	secs= B3				' stage timing (minutes)
SYMBOL	loops = B4				' counter for long delay


' -----[ Initialization ]--------------------------------------------------
'
Init:	Pins = %00000000			' LEDs off to start
	Dirs = %10001111			' LEDs and Piezo outs


' -----[ Main Code ]-------------------------------------------------------
'
Main:	FOR cycle = 1 TO 4

	  Pins = %0001				' stage 1 LED on
	  SOUND BprPin,(BprTone, BprLen)	' sound start of stage
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
