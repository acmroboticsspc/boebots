' Listing 5.2. Multiplexing Stamp I/O Lines
' Program: MULTIPOT.BAS (Multiple pots using a 4051 multiplexer)
' This program demonstrates how to connect and measure multiple
' pots using a 4051 multiplexer chip. The 4051's control inputs
' (11,10,9) connect to Stamp pins 0, 1, and 2 respectively.
' The common I/O pin (3) of the 4051 goes to Stamp pin 3.
' By writing a value between 0 and 7 to its pins, the Stamp can
' select one of eight variable-resistance inputs through the
' 4051. See the schematic for details.
SYMBOL pot_sel = b2 		' Pot number 0-7 selected through 4051.
SYMBOL pot_val = b3 		' Result of the pot measurement.
Let dirs = %0111 		' Lower 3 pins outputs to drive 4051.
Again:
  for pot_sel = 0 to 7 	' For each of the eight pots:
  let pins = pot_sel 		' Write pot number to the 4051.
  pot 3,150,pot_val 		' Perform pot measurement on selected pot.
  debug "pot #",#pot_sel," ",#pot_val,cr ' Display result.
  next pot_sel 		' Read the next pot.
pause 2000 			' Wait two seconds.
debug cr 			' Insert a carriage return on screen.
goto Again 			' Do it again (endless loop).
