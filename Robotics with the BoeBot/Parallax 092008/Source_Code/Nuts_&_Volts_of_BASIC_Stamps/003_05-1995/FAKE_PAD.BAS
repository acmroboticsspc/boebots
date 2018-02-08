' Listing 3.2: FAKE_PAD.BAS (imitate the ORTEK keypad codes)
' This program mimics the codes produced by the ORTEK keypad,
' allowing a PC running the ORTEK software to receive Stamp data
' to its keyboard buffer. The Stamp "types" directly into programs
' that are incapable of normal serial input. To demonstrate this
' capability, the Stamp will count upward by 1 and type the result
' of each calculation to the PC. Remember that the keypad software
' must be installed on the PC for this to work. Before running
' this program, make sure that this program is saved, since the
' Stamp may begin typing numbers into it, if the keypad software
' is active.
SYMBOL nonZero = bit0 		' Leading-zero suppression flag.
SYMBOL code = b1 		' Key code to send.
SYMBOL decade = w2 		' Power-of-10 divisor for conversion.
SYMBOL count = w3 		' Counter for demo.
' Main program loop.
Loop:
  let w1 = count 		' Transfer value of copy to w1.
  gosub typeData 		' "Type" the data to PC.
  pause 100 			' Wait briefly
  let count = count + 1 	' Increment counter.
goto Loop 			' Do it forever.
' Subroutine to convert the value stored in w1 to ORTEK keypad codes.
typeData:
  let nonZero = 0 		' Clear flag indicates first non-0 digit.
  let decade = 10000 		' Start with highest digit of w1.
nextDigit:
  let code = w1/decade 	' Get value of current digit.
  let w1 = w1//decade 		' Leave remainder in w1.
  if code=0 AND nonZero=0 AND decade <> 1 then skip3 ' No leading 0s.
  if code=0 then skip1
  let nonZero = 1
goto skip2
skip1:
  let code = 16 		' Code for 0, minus 95.
skip2:
  let code = code + 95
  serout 0,N1200,(code) 	' Send key-down code of digit.
  let bit13 = 0 		' Clear bit5 of b1 (bit13) for key-up code.
  serout 0,N1200,(code) 	' Send key-up code.
skip3:
  let decade = decade/10 	' Get ready for next lower digit
  if decade > 0 then nextDigit
  serout 0,N1200,("tT") 	' Done. Send <Enter> key.
Return
