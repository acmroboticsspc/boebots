' =========================================================================
'
'   File...... Robot_Eyes.BAS
'   Purpose... Demonstrates Photovore logic
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
' Demonstrates photovore (light seeking) logic that can be applied to a
' a Stamp controlled robot.


' -----[ Revision History ]------------------------------------------------


' -----[ I/O Definitions ]-------------------------------------------------

SYMBOL  EyeR            = 0                     ' right eye RC input
SYMBOL  EyeL            = 1                     ' left eye RC input


' -----[ Constants ]-------------------------------------------------------

SYMBOL  Threshold       = 20                    ' difference threshold


' -----[ Variables ]-------------------------------------------------------

SYMBOL  rtEye           = B2                    ' right eye value
SYMBOL  lfEye           = B3                    ' left eye value
SYMBOL  move            = B4                    ' calculated move


' -----[ EEPROM Data ]-----------------------------------------------------


' -----[ Initialization ]--------------------------------------------------

Setup:


' -----[ Program Code ]----------------------------------------------------

Main:
  GOSUB Read_Eyes
  BRANCH move, (Go_Straight, Go_Right, Go_Left, Stay_Still)

Go_Straight:
  DEBUG "Straight"
  GOTO Main

Go_Right:
  DEBUG "Right"
  GOTO Main

Go_Left:
  DEBUG "Left"
  GOTO Main

Stay_Still:
  DEBUG "Holding..."
  GOTO Main


' -----[ Subroutines ]-----------------------------------------------------

Read_Eyes:
  POT EyeL, 145, lfEye                          ' read robot "eyes"
  POT EyeR, 175, rtEye
  DEBUG CLS, lfEye, CR, rtEye, CR               ' display eye values

Check_Eyes:
  IF lfEye = 0 AND rtEye = 0 THEN Is_Dark       ' too dark or eyes swamped
  IF rtEye < lfEye THEN Is_Right                ' look for dominant side

Is_Left:
  move = %10                                    ' indicate left
  IF lfEye = 0 THEN Eyes_Done                   ' eye is saturated
  lfEye = rtEye - lfEye                         ' get difference
  IF lfEye >= Threshold THEN Eyes_Done          ' check threshold
  move = %00                                    ' near center -- straight
  GOTO Eyes_Done

Is_Right:
  move = %01
  IF rtEye = 0 THEN Eyes_Done
  rtEye = lfEye - rtEye
  IF rtEye >= Threshold THEN Eyes_Done
  move = %00
  GOTO Eyes_Done

Is_Dark:
  move = %11

Eyes_Done:
  RETURN