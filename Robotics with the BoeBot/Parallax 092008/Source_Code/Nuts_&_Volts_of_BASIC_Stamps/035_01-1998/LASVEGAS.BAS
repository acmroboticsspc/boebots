' ----[ Title ]-----------------------------------------------------------
' Program Listing 35.1
' File...... LASVEGAS.BAS
' Purpose... BS1 Slot Machine Game
' Author.... Jon Williams
' E-mail.... jonwms@aol.com
' WWW....... http://members.aol.com/jonwms
' FTP....... ftp://members.aol.com/jonwms/stamps
' Started... 13 MAR 94
' Updated... 01 DEC 97


' ----[ Program Description ]---------------------------------------------
'
' Simple BASIC Stamp slot machine
'
' Connections:
'
' - LEDs connected to pins 0 - 5 through 510 Ohm resistors (1 = ON)
' - 45 Ohm speaker connected to pin 6 through 10 uF electrolytic cap
' - N.O. switch between pin 7 and ground -- pin 7 pull up through 10 KOhm

' ----[ Revision History ]------------------------------------------------
'
' 15 MAR 94 : Version 1 complete and working
' 01 DEC 97 : Updated and reformatted for Nuts & Volts


' ----[ Constants ]-------------------------------------------------------
'
SYMBOL  LEDs    = Pins
SYMBOL  Spkr    = 6
SYMBOL  Swtch   = 7


' ----[ Variables ]-------------------------------------------------------
'
SYMBOL  rndW    = W0
SYMBOL  delay   = W1
SYMBOL  pattern = B4
SYMBOL  tone    = B5
SYMBOL  swData  = B6
SYMBOL  spin1   = B7
SYMBOL  spin2   = B8


' ----[ EEPROM Data ]-----------------------------------------------------
'

' ----[ Initialization ]--------------------------------------------------
'
Init:   Pins = %00000000                        ' setup port
	Dirs = %00111111
	swData = 0                              ' clear switch workspace


' ----[ Main Code ]-------------------------------------------------------
'
' This section generates random patterns on the LEDs and plays a random
' tone through the speaker.  This acts as an attention getter ("play me")
' and seeds the random number generator so that the game plays differently
' each time.  Section repeats until button is pushed.
Attn:   GOSUB GetRnd			' get random number
	SOUND Spkr, (Tone, 3)		' play the random tone
	PAUSE 100
	BUTTON Swtch, 0, 255, 10, swData, 1, Spin
	GOTO Attn

' The heart of the program.  We start first by 'reseting' (all LEDs on,
' then off momentarily), then the wheel is spun.  I found that 25 turns
' of the 'wheel' seemed about right.  A 'bonk' is output through the
' speaker with each wheel spin.  Natural wheel spin decay is created by
' multiplying the the decay variable by 110% through each rev.  If all
' LEDs are lit at the end, "You Win", and the LEDs dance a bit.  If you
' lose, and you nearly always do, an obnoxious groan is output.
'
Spin:   LEDs = %00111111			' look like we're resetting
	PAUSE 750
	LEDs = %00000000
	PAUSE 500
	delay = 75				' initial delay value

	FOR spin1 = 1 TO 25			' spin wheel - 25 revs
	  GOSUB GetRnd				' get and display pattern
	  SOUND Spkr, (100, 2)			' bonk sound for each rev
	  PAUSE delay
	  delay = delay * 11 / 10		' increase delay by 10%
	NEXT spin1

	IF pattern = %00111111 THEN YouWin	' but what do you win?
	SOUND Spkr, (50, 125)			' groan!
	LEDs = %00000000			' clear the LEDs
	PAUSE 1000				' rest a bit
	GOTO Attn				' here we go again...


YouWin: FOR spin1 = 1 TO 5			' a little marquee action
	  FOR spin2 = 0 TO 3
	    LOOKUP spin2, ($00, $0C, $12, $21), LEDs
	    LOOKUP spin2, (110, 113, 116, 119), tone
	    SOUND Spkr, (Tone, 3)
	    PAUSE 65
	  NEXT spin2
	NEXT spin1
	LEDs = %00000000
	PAUSE 1000
	GOTO Attn


' ----[ Subroutines ]-----------------------------------------------------
'

GetRnd: RANDOM rndW				' get a random number
	tone = rndW & $7F			' do not allow white noise
	pattern = rndW & %00111111		' mask for 6 LEDs
	LEDs = pattern				' show new pattern
	RETURN 