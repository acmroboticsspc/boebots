' =========================================================================
'
'   File...... Bumper_Bot.BAS
'   Purpose... Simple bounce-off-object robot core
'   Author.... Jon Williams
'   E-mail.... jwilliams@parallax.com
'   Started...
'   Updated... 07 DEC 2003
'
'   {$STAMP BS1}
'   {$PBASIC 1.0}
'
' =========================================================================


' -----[ Program Description ]---------------------------------------------
'
' This program uses the BS1 module and a Pololu Micro Motor controller to
' form the heart of a simple robotic platform.  The small size of the pars
' allow it to be "hacked" into a motorized toy for customization.


' -----[ Revision History ]------------------------------------------------


' -----[ I/O Definitions ]-------------------------------------------------

SYMBOL  SOut            = 7                     ' serial out to Pololu
SYMBOL  Reset           = 6                     ' Pololu reset control
SYMBOL  BumpL           = PIN1                  ' left bumper input
SYMBOL  BumpR           = PIN0                  ' right bumper input


' -----[ Constants ]-------------------------------------------------------

SYMBOL  Baud            = T2400                 ' can be T1200 or T2400

SYMBOL  Hit             = 0                     ' inputs are active-high

SYMBOL  MLFwd           = %11                   ' left motor forward
SYMBOL  MLRev           = %10                   ' left motor reverse
SYMBOL  MRFwd           = %00                   ' right motor forward
SYMBOL  MRRev           = %01                   ' right motor reverse

SYMBOL  SpdMin          = 40                    ' minimum start speed
SYMBOL  SpdMax          = 125                   ' top speed
SYMBOL  SpdRamp         = 5                     ' delta for acceleration


' -----[ Variables ]-------------------------------------------------------

SYMBOL  ctrl            = B0                    ' motor control byte
SYMBOL  direction       = BIT0                  ' 0 = CW, 1 = CCW
SYMBOL  motor           = BIT1                  ' 0 = right, 1 = left
SYMBOL  speed           = B1                    ' motor speed
SYMBOL  bumpers         = B2                    ' bumper inputs
SYMBOL  state           = B3                    ' sensor state


' -----[ EEPROM Data ]-----------------------------------------------------


' -----[ Initialization ]--------------------------------------------------

Setup:
  HIGH SOut                                     ' idle state of serial line
  GOSUB Reset_SMC


' -----[ Program Code ]----------------------------------------------------

Wait_For_Start:
  GOSUB Get_Bumpers                             ' can bumper inputs
  IF bumpers = %11 THEN Wait_For_Start          ' wait until one or both
  PAUSE 1000                                    ' ... hands out!

Main:
  GOSUB Get_Bumpers                             ' scan inputs
  IF bumpers = %11 THEN Accelerate              ' if not hits, speed up
  GOSUB Reset_SMC                               ' contact -- stop motors
  speed = SpdMin                                ' reset speed
  PAUSE 10
  BRANCH bumpers, (Back_Out, Right, Left)
  GOTO Main

Back_Out:
  SEROUT SOut, Baud, ($80, 0, MLRev, speed)     ' back up
  SEROUT SOut, Baud, ($80, 0, MRRev, speed)
  PAUSE 250
  GOSUB Reset_SMC                               ' stop motors
  SEROUT SOut, Baud, ($80, 0, MLFwd, speed)     ' turn (clockwise)
  SEROUT SOut, Baud, ($80, 0, MRRev, speed)
  PAUSE 500
  SEROUT SOut, Baud, ($80, 0, MRFwd, speed)     ' both forward now
  GOTO Main

Right:
  SEROUT SOut, Baud, ($80, 0, MRRev, speed)     ' spin right
  PAUSE 200
  GOTO Main

Left:
  SEROUT SOut, Baud, ($80, 0, MLRev, speed)     ' spin left
  PAUSE 200
  GOTO Main

Accelerate:
  speed = speed + SpdRamp MAX SpdMax            ' increase speed to max
  SEROUT SOut, Baud, ($80, 0, MLFwd, speed)     ' both motors forward
  SEROUT SOut, Baud, ($80, 0, MRFwd, speed)
  PAUSE 50
  GOTO Main


' -----[ Subroutines ]-----------------------------------------------------

Reset_SMC:                                      ' reset motor controller
  LOW Reset
  PAUSE 1
  HIGH Reset
  RETURN

Get_Bumpers:
  bumpers = PINS & %11                          ' mask non-bumper pins
  RETURN