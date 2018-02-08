' Listing 2: NO_BTN.BAS (Demonstration of poor debouncing)

' This program is identical to BTN_JUKE.BAS, except that it does not
' use button commands to read the state of the switches. Contrasting
' the operation of this program to BTN_JUKE will give you a good idea
' of the benefits of button. 

' The Stamp serves as a tiny jukebox, allowing you to pick from one of 
' five musical (?) selections created with the sound command. The point 
' of the program is to demonstrate the proper way to use the button
' command. The juke has two buttons--one that lets you pick the tune
' by "scrolling" through five LEDs, and the other that plays the tune 
' you selected. 


SYMBOL Select = b2	' Variable to hold tune selection, 0-4. 

let dirs = %00111111	' Pins 6 & 7 are inputs for buttons.
let b2 = 0		' Clear the selection. 
let pins = %00000010	' Turn on the first selection LED. 

' The main program loop. Main scans the two buttons and takes the 
' appropriate action. If the play button on pin 7 is not pressed, 
' the program skips over the code that plays a tune. If the select
' button is pressed, the program goes to the routine Pick, which 
' increments the current tune selection and LED indicator. 

Main:
  if pin7 = 1 then no_play	' Don't play tune unless pin 7 button is pushed. 
  branch Select,(Tune0,Tune1,Tune2,Tune3,Tune4)
no_play:
  if pin6 = 0 then Pick		' When pin 6 button is pushed, change tune.
goto Main

' Pick increments the variable Selection, while limiting it to a maximum 
' value of 4. Note that it begins with a pause of 0.15 seconds. This 
' prevents the code from executing so fast that the LEDs become a blur. 
' However, it's no substitute for the button command. You'll find that
' it is hard to select the particular LED you want. 

Pick:
  pause 150	' Attempt to debounce by delaying .15 sec.
  let Select = Select + 1	' Increment selection. 
  if Select < 5 then skip	' If Select = 5, then Select = 0. 
  let Select = 0	' Skip this line if Select is < 3. 
skip:
  lookup Select,(2,4,8,16,32),pins	' Light appropriate LED. 
goto Main			' Return to main program loop. 

' The tunes. Not necessarily music. 

Tune0: sound 0,(100,10,110,100): goto main

Tune1: sound 0,(98,40,110,10,100,40): goto main

Tune2: sound 0,(100,10,80,100): goto main

Tune3: sound 0,(100,10,110,50,98,10): goto main

Tune4: sound 0,(98,40,100,10,110,40): goto main
