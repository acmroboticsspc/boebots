' Program Listing 13.1: WATCHDOG.BAS (PBASIC 1 detects motor stall)
' This program implements a watchdog timer--a device that monitors
' a pulsebeat and takes action if the pulse is absent for a
' predetermined amount of time. Applications for watchdogs include
' resetting a locked-up PC or cutting power to a stalled motor.
SYMBOL 	state = bit0 		' Trigger state for button command.
SYMBOL 	dog = w1 		' The watchdog counter.
SYMBOL 	pulse_n = 0 		' Pin number for pulsebeat input.
SYMBOL 	pulse_p = pin0 	' Pin name for pulsebeat input.
SYMBOL 	btn = b4 		' Workspace for button command.
SYMBOL 	timeout = 300 		' Max value of "dog" before alarm.

begin:
  let dog = 0 			' Clear watchdog variable to 0.
  state = pulse_p ^ 1 		' State = inverse of pulse pin.
' In the routine below, if the pulse input changes state, the OK
' routine shows us the count in variable "dog," then clears "dog"
' by looping back to the beginning of the program. Otherwise,
' it increments dog and, if dog exceeds the timeout value, shows
' the alaram message.

watchDog:
  button pulse_n,state,0,1,btn,1,OK
  let dog = dog + 1
  if dog > timeout then alarm
Goto watchDog

alarm:
  debug "alarm!",cr 		' Dog exceeded timeout.
goto begin

OK:
  debug dog,cr 		' Show us how high "dog" got.
goto begin 			' Then goto beginning to clear.

