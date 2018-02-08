' Program Listing 20.1. BS1 Program to Demonstrate Debug Output
' Program: DBUG.BAS
' This program sets the various memory variables to known
' values for viewing with the PC program in listing 2.

b0 = "A": b1 = "B": b2 = "C": b3 = "D"
b4 = "E": b5 = "F": b6 = "G": b7 = "H"
b8 = "I": b9 = "J": b10 = "K": b11 = "L"
b12 = "M":b13 = "N"
dirs = %00001111: pins = 0

loop:
  pause 2000 			' Wait 2 seconds.
  debug b0 			' Debug output.
  b0 = b0 + 1 			' Increment B0.
  random w6 			' Random number in B12 and B13 (W6).
goto loop ' Repeat forever.
