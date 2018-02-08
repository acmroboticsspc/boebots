' Listing 3.1: OR_KEYS.BAS (interpret ORTEK MCK-18S/N keypad codes)
' This program accepts serial data from the ORTEK MCK-18S/N numeric
' keypad and converts it into a 16-bit value in variable w1. Users
' must be careful not to hold keys down, or they will activate the
' pad's autorepeat function, causing entry errors.
' Main program loop.
Loop:
  gosub GetKeys 		' Getkeys does all the work.
  debug w1 			' Show the result on the PC screen.
  goto Loop 			' Do it forever.
' Subroutine to receive the serial data, filter out extraneous key-up
' codes, and convert received data bytes to a 16-bit value. GetKeys
' will keep accepting and interpreting digits until the user presses
' a non-numeric key, like <Enter>. The routine takes advantage of the
' fact that the key codes for the numbers 1-9 are sequential;
' subtracting 95 from them leaves you with the number itself.
' Although0 (zero) is out of sequence, an additional IF/THEN
' statement recognizes its code ("o").
GetKeys:
  let w1 = 0 			' Clear w1 to start.
Again:
  Serin 0,N1200,b0 		' Get code from the keypad.
  if bit5 = 0 then Again 	' Bit5 is 0 in key-up codes; ignore em.
  if b0 <> "o" then skip 	' Change 0 code from "o" (111) to 95.
  let b0 = 95
skip:
  if b0 > 104 then done 	' Non-numeric key pressed; we're done.
  let b0 = b0 - 95 		' Otherwise convert to 0-9, multiply
  let w1 = w1 * 10 + b0 	' old total by 10, add new value, and
goto Again 			' get the next digit.
done:
  return 			' Done: return to main program.
