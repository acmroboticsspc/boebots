' Stamp Applications - May 2000
' Listing 5

' -----[ Title ]-----------------------------------------------------------
'
' File...... XTIMER3.BAS
' Purpose... Exercise Timer - Version 3 - "Time Slicer"
' Author.... Jon Williams


' -----[ Program Description ]---------------------------------------------
'
' This program serves as a multi-stage exercise timer.  The program runs
' four cycles of four stages, with an LED and audio indication for each
' stage.
'
' The timer includes a pause switch so that a stage can be interrupted and
' restarted.  The timer begins in "Hold" mode.  Pressing the Hold switch
' starts the timer


' -----[ I/O Pins ]--------------------------------------------------------
'
SYMBOL	BprPin = 7				' beeper pin
SYMBOL	SWin = Pin6				' Hold switch input


' -----[ Constants ]-------------------------------------------------------
'
SYMBOL	LoopTm = 46				' 1/20 second loop (tuned)
SYMBOL	TixMax = 20				' 20 x 50 ms = 1 second
SYMBOL	SwMax = 5				' switch debounce value

SYMBOL	Up = 0					' Hold switch released
SYMBOL	Down = 1				' Hold switch pressed


' -----[ Variables ]-------------------------------------------------------
'
SYMBOL	cycle = B2				' 4 cycles per workout
SYMBOL	stage = B3				' 4 stages per cycle
SYMBOL	tix = B4				' timing counter
SYMBOL	secs = B5				' stage seconds counter
SYMBOL	sMax = B6				' max time for stage
SYMBOL	swtch = B7				' switch debounce


' -----[ Initialization ]--------------------------------------------------
'
Init:	Pins = %00000001			' light stage one at start
	Dirs = %10001111			' LEDs and Piezo outs

	cycle = 0				' first cycle
	stage = 0				' first stage
	tix = 0					' clear tix timer
	secs = 0				' clear seconds

	GOTO OnHold				' start in Hold mode


' -----[ Main Code ]-------------------------------------------------------
'
Main:	IF secs > 0 THEN Pad			' beep only when secs and 
	IF tix > 0 THEN Pad			'   tix are zero 
	LOOKUP stage,(%0001,%0010,%0100,%1000),Pins  ' stage LED
	GOSUB Beep
	LOOKUP stage,(120,60,60,60),sMax	' get stage timing (secs)

Pad:	PAUSE LoopTm				' pad the loop for timing

ChkSw:	swtch = swtch + SWin * SWin		' check pause switch
	IF swtch >= SwMax THEN OnHold		' debounce; call Hold if
						'   held down

Test:	tix = tix + 1 // TixMax			' increment with rollover
	IF tix > 0 THEN Main			' still in same second
	secs = secs + 1 // sMax			' increment seconds
	IF secs > 0 THEN Main			' still in stage
	stage = stage + 1 // 4			' increment stage
	IF stage > 0 THEN Main			' still in cycle
	cycle = cycle + 1 // 4			' increment cycle
	IF cycle > 0 THEN Main			' still running

Done:	Pins = %0000				' LEDs off
	SOUND BprPin,(0,12,50,12,75,12,110,12)	' sound end

	END ' of program


' -----[ Subroutines ]-----------------------------------------------------
'
Beep:	BRANCH stage,(Beep1,Beep2,Beep3,Beep4)
Beep1:	SOUND BprPin,(50,16)
	GOTO BeepX
Beep2:	SOUND BprPin,(50,16,0,8,50,16)
	GOTO BeepX
Beep3:	SOUND BprPin,(90,16)
	GOTO BeepX
Beep4:	SOUND BprPin,(90,16,0,8,90,16)
	GOTO BeepX
BeepX:	RETURN


OnHold:	IF SWin = Down THEN OnHold		' wait for release
	swtch = 0				' clear switch check
Hold1:	Dirs = %00000000			' turn of LED
	PAUSE 65
	Dirs = %10001111			' back on
	PAUSE 65
	swtch = swtch + SWin * SWin		' check switch
	IF swtch < 2 THEN Hold1			' debounce
	swtch = 0				' reset timers/counters
	tix = 0	
	secs = 0
	GOTO Main
