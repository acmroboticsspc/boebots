' Listing 22.2. BS1 race timer (BASIC for Bewginners)
' Program RACE1.BAS (Prototype three-lane race timer)
' This program shows how the BS1 (or Counterfeit) can
' be used to time a three-lane Pinewood Derby race
' without complicated IF..THEN programming. This
' program is a prototype; when the race is over it
' displays raw data on the PC screen via Debug. Later
' versions will convert the data to units (fractions of
' a second) and display them on a freestanding display.

SYMBOL 	time1 = w2 		' Word variable for lane-1 time.
SYMBOL 	time2 = w3 		' Word variable for lane-2 time.
SYMBOL 	time3 = w4 		' Word variable for lane-3 time.

SYMBOL start = pin7 		' Start-switch on pin 7; 0=start.

SYMBOL status1 = bit0 		' Status of lane 1; 1=racing, 0=done.
SYMBOL status2 = bit1 		' Status of lane 2; 1=racing, 0=done.
SYMBOL status3 = bit2 		' Status of lane 3; 1=racing, 0=done.
SYMBOL stats = b0 		' Byte variable containing status bits.

stats = %111 ' All cars in the race to begin.

hold:
  if start =1 then hold 	' Wait for start signal.
  timing: 			' Time the race.
  stats = stats & pins & %111 	' Put lowest 3 pin states into stats.
  if stats = 0 then finish 	' If all cars done, then race over.
  time1 = time1 + status1 	' If a car is in race (status=1) then
  time2 = time2 + status2 	' increment its timer. If it's done
  time3 = time3 + status3 	' (status=0) don't increment.
goto timing 			' Loop until race over.
finish:
  debug cr,"Checkered flag!",cr ' Race over.
  debug time1 			' Display results.
  debug time2
  debug time3
 
