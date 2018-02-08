' Program Listing 11.1: ROT_TIME.BAS (Timer with rotary-encoder interface)
' This program implements a 60-hour countdown timer with a user-friendly
' rotary-encoder (twist-knob) interface and LCD Serial Backpack display.
' When first powered up, the display shows "00:00:00" and waits for
' the user to twist the knob to set the hours. Clockwise increases the
' setting, counter-clockwise reduces it. When the hours are set, the
' user pushes the knob in to set the minutes and seconds in the same
' way. Once the seconds are set, pushing the knob in one more time
' starts the timer. The display counts down to zero, then turns on the
' output.
' This application relies on an external timer as an accurate source of
' 2-Hz 'ticks.' Typical accuracy is within 2-3 seconds over the maximum
' timing period of 59:59:59 (almost 60 hours). Another interesting
' feature of the application is its control of the rotary-encoder power
' supply. Since the encoder's LEDs draw almost 20 mA of current, the
' program shuts them off when they're not needed and thereby conserves
' battery power.
' ================================
' Variables and constants.
' ================================
SYMBOL old = b0 	' Previous bit pattern of rotary encoder.
SYMBOL new = b1 	' Current " " " " "
SYMBOL directn = bit0 	' Direction of knob rotation.
SYMBOL count = b2 	' Number dialed in by encoder.
SYMBOL hours = b3 	' Timer hours setting.
SYMBOL minutes = b4 	' Timer minutes setting.
SYMBOL seconds = b5 	' Timer seconds setting.
SYMBOL temp = b6 	' Temporary variable used by display routine.
SYMBOL prnPos = b7 	' Printing position on LCD screen.
SYMBOL btn = b8 	' Workspace variable for Button command.
SYMBOL case = b9 	' Offset for Branch command.
SYMBOL out_pin = 3 	' Output pin controlled by timer.
SYMBOL encoder = 2 	' Power to rotary encoder LEDs.
SYMBOL I = 254 	' LCD Backpack instruction prefix (see note).
SYMBOL cls = 1 	' LCD Backpack clear-screen instruction.

' NOTE: This program is written for the rev3A Backpack firmware,
' which uses an instruction prefix, rather than a toggle. The new
' firmware makes this latest Backpack "reset proof" since the
' controller can always put the LCD into a known state by clearing
' the screen (and optionally also turning the cursor on/off).
' ================================
' Main Program Start
' ================================
Begin:
  low out_pin 		' Turn off the output pin.
  high encoder 	' Turn on power to encoder LEDs.
  pause 1000 		' Wait a sec for LCD initialization.
  serout 0,n2400,(I,cls) ' Clear the LCD screen
  let new = pins & $C0 ' Get initial state of encoder pins.
  let prnPos = 132 	' Set print position to 4 (128+4)
gosub Display 		' Put 0s on the display.
' ================================
' User Setup of Time Duration
' ================================
Setup:
  gosub rotary 	' Check the knob.
  serout 0,n2400,(I,prnPos) ' Position cursor on the display.
  gosub showDigs 	' Display digits.
  button 5,0,255,0,btn,1,pushed 
	' Check for knob push on pin 5.
goto Setup 		' Loop.
' If the knob is pushed in, causing a low on pin 5, the program
' jumps from setup to here. It checks the current printing position
' to determine whether the user has been setting hours, minutes, or
' seconds and determine what to do next.
pushed:
  let case = prnPos-132/3 ' Convert position to 0-2.
  branch case,(setHours,setMins,setSecs) ' Branch based on 0-2)
setHours:
  let hours = count 	' Put the count into hours.
goto continue 		' Continue setting timer.
setMins:
  let minutes = count 	' Put the count into minutes.
  goto continue 	' Continue setting timer.
setSecs:
  let seconds = count 	' Put the count into seconds.
  goto runTimer 	' And start the countdown.
continue:
  let count = 0 	' Continue: clear count for next.
  let prnPos = prnPos+3 ' Move to next screen position.
goto Setup 		' Get more input from user.

' ================================
' Timing Countdown
' ================================
runTimer:
  let old = 0 		' Initialize "old" to track ticks from timer.
  low encoder 		' Turn off the encoder.
' This code counts changes in state from the external timer. Every
' fourth change (transition from 0-1 or 1-0) of the 2-Hz clock means
' that a second has passed. When that happens, the program subtracts
' 1 from the seconds, minutes and hours.
DoTiming:
  if pin1 = bit0 then DoTiming' No change? Loop.
  let old = old + 1 		' Changed: increment old.
  let new = old & %11 		' Look at bottom two bits of old.
  if new <> 3 then DoTiming 	' Loop is not 3 (4th count, 0,1,2,3)..
  let seconds = seconds - 1 	' Fourth count: decrement seconds.
  if seconds <> 255 then update ' If not underflow (-1 = 255), update.
  let seconds = 59 		' Underflow: wrap around to 59 seconds.
  let minutes = minutes -1 	' Seconds underflowed: borrow 1 from mins.
  if minutes <> 255 then update ' If not underflow (-1 = 255), update.
  Let minutes = 59 		' Underflow: wrap to 59 minutes.
  let hours = hours - 1 	' Minutes underflowed: borrow 1 from hours.
update:
  gosub Display 		' Display new hours/mins/secs.
check:
  if hours <> 0 then DoTiming ' If not 00:00:00, continue timing.
  if minutes <> 0 then DoTiming
  if seconds <> 0 then DoTiming
  high out_pin 		' Time's up: turn on the output.
hold: goto hold 		' Endless loop: reset to start again.
' ================================
' Subroutines
' ================================
' Check the rotary encoder. If it has moved, determine direction and
' adjust the value of the variable "count" accordingly.
rotary:
  let old = new & $C0 		' Make old = top two bits of new.
again:
  let new = pins & $C0 	' Make new = top two bits of pins.
  if new = old then done 	' No change? Done.
  let directn = bit6 ^ bit15 	' Change: determine direction.
  if directn = 1 then CW 	' Clockwise: goto routine below.
  let count = count - 1 	' Counterclockwise: decrement count.
  if count <> 255 then skip 	' If count < 0, then count = 59.
  let count = 59
skip:
  return 			' Return to main program.
  let count = count + 1 	' Clockwise: increment count.
  if count <> 60 then done 	' If count = 60, wrap around to 0.
  let count = 0
done:
  return 			' Return to main program.
' Display the hour:minute:second digits on the LCD screen.
Display:
  serout 0,n2400,(I,132) 	' Start at hours position.
  let count = hours 		' Show hours digits.
  gosub showDigs
  serout 0,n2400,(":") 	' Colon.
  let count = minutes 		' Now minutes.
  gosub showDigs
  serout 0,n2400,(":") 	' Colon.
  let count = seconds 		' Now seconds.
  gosub showDigs
return 				' Return to main program.
' Display the two-digit value stored in count on the LCD.
showDigs:
  let temp = count/10 		' Get the tens-place digit.
  serout 0,n2400,(#temp) 	' Put it on the display.
  let temp = count//10 	' Get the ones-place digit.
  serout 0,n2400,(#temp) 	' Put it on the display.
return 				' Return to main program.
