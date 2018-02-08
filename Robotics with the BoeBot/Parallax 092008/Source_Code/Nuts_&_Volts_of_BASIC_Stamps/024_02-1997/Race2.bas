' Program Listing 24.2. BASIC for Beginners Race Timer with Display
' Program RACE2.BAS (Three-lane race timer with display)
' This program shows how the BS1 (or Counterfeit) can
' be used to time a three-lane Pinewood Derby race.
' It converts a raw count of program loops into
' units of 1/100th of a second and presents them on
' a serial LCD display.
SYMBOL time1 = w2 	' Word variable for lane-1 time.
SYMBOL time2 = w3 	' Word variable for lane-2 time.
SYMBOL time3 = w4 	' Word variable for lane-3 time.
SYMBOL start = pin7 	' Start-switch on pin 7; 0=start.
SYMBOL status1 = bit0 	' Status of lane 1; 1=racing, 0=done.
SYMBOL status2 = bit1 	' Status of lane 2; 1=racing, 0=done.
SYMBOL status3 = bit2 	' Status of lane 3; 1=racing, 0=done.
SYMBOL win = bit3 	' Flag to indicate race winner.
SYMBOL stats = b0 	' Byte variable containing status bits.
SYMBOL pos = b11 	' Printing location.
SYMBOL digits = b10 	' Digits to display.
SYMBOL timeDat = w1 	' Timing data to convert/display.
SYMBOL iPre = 254 	' Instruction prefix for LCD.
SYMBOL clrLCD = 1 	' Clear LCD screen.
SYMBOL blank = 8 	' Blank the LCD (but retain data).
SYMBOL restore = 12 	' Restore LCD.
SYMBOL topLft = 128 	' Move to top-left of LCD screen.
SYMBOL topRt = 136 	' Move to top-right of LCD screen.
SYMBOL btmLft = 192 	' Move to bottom left of LCD screen.
SYMBOL btmRt = 200 	' Move to bottom right of LCD screen.

begin:
  stats = %111 	' All cars in the race to begin.
  time1=0:time2=0:time3=0 ' Clear timers.
  serout 3,n2400,(iPre,clrLCD) ' Clear the display.
  pause 5
' The line below is sneaky--it prints "Race in Progress" to the
' LCD, then blanks the LCD so that the message is hidden. That
' way, the program can display the whole 16-byte message by just
' sending a 2-byte 'unblank display' instruction.
serout 3,n2400,("Race in Progress",iPre,blank)

hold:
  if start =1 then hold 	' Wait for start signal.
  serout 3,n2400,(iPre,restore) ' Restore "Race in Progress" to LCD.

timing: 			' Time the race.
  stats = stats & pins & %111 ' Put lowest 3 pin states into stats.
  if stats = 0 then finish 	' If all cars done, then race over.
  time1 = time1 + status1 	' If a car is in race (status=1) then
  time2 = time2 + status2 	' increment its timer. If it's done
  time3 = time3 + status3 	' (status=0) don't increment.
goto timing 			' Loop until race over.

finish:
  serout 3,n2400,(iPre,clrLCD) ' Clear display.
  pause 5
  serout 3,n2400,(iPre,btmRt,"-FINAL-") ' Print FINAL.
  timeDat=time1: pos=topLft:gosub Display ' Display race times.
  timeDat=time2: pos=topRt:gosub Display
  timeDat=time3: pos=btmLft:gosub Display
END 				' End program--reset to time another race.

' This subroutine converts the loop count in the variable timeDat
' into a number of hundredths of a second, then prints that value
' (as seconds, with decimal point) at the screen location specified
' by the variable pos.

Display:
  serout 3,n2400,(iPre,pos) 	' Move to display location.
  if timeDat > time1 OR timeDat > time2 OR timeDat > time3 then noWin
  serout 3,n2400,("*") 	' Put * by winner (or winners, if tie)
goto skip1

noWin:
  serout 3,n2400,(" ") 	' Put space by non-winners.
skip1:
  timeDat = timeDat*8/27 	' Convert to 100ths of a second.
  digits = timeDat/100 	' Print hundreds place followed
  serout 3,n2400,(#digits,".")' ..by decimal point.
  digits = timeDat//100 	' Now print remainder.
  if digits > 9 then skip0 	' If remainder is less than 10,
  serout 3,n2400,("0") 	' ..print "0" (i.e., convert

skip0: 				' "6" to "06" for correct display.
  serout 3,n2400,(#digits)
return 				' Return to program.
