' Listing 1: BTN_JUKE.BAS (Demonstration of the Button command)

' The Stamp serves as a tiny jukebox, allowing you to pick from one of 
' five musical (?) selections created with the sound command. The point 
' of the program is to demonstrate the proper way to use the button
' command. The juke has two buttons--one that lets you pick the tune
' by "scrolling" through five LEDs, and the other that plays the tune 
' you selected. The selection button uses the debounce and autorepeat 
' features of button, while the play button is set up for immediate
' response without delay or autorepeat. 

SYMBOL Select = b2	' Variable to hold tune selection, 0-4. 

let dirs = %00111111	' Pins 6 & 7 are inputs for buttons.
let w0 = 0		' Initialize all variables to zero
let w1 = 0		' (includes clearing the button variables b0,b1)
let pins = %00000010	' Turn on the first selection LED. 

' The main program loop. Main scans the two buttons and branches to 
' no_play or Pick, depending on which button was pressed. Note the two
' different ways the button command is used. In the first case, 
' button skips over the branch instruction that jumps to the 
' appropriate tune routine _unless_ the button is pushed. 
' The tunes are fairly long, so no debounce is needed, and 
' autorepeat isn't appropriate (the next trip through main will 
' play the tune again, anyway). The second button command, which 
' scrolls through the selection LEDs, uses both debounce and auto-
' repeat. Switch bounce could cause the display to seem to skip 
' over selections, and autorepeat is a nice, professional touch
' for rapidly scrolling through the display. 

Main:
  button 7,0,0,0,b0,0,no_play	' Don't play tune unless button is pushed. 
  branch Select,(Tune0,Tune1,Tune2,Tune3,Tune4)
no_play:
  button 6,0,200,60,b1,1,Pick	' When button is pushed, change selection.
goto Main

' Pick increments the variable Selection, while limiting it to a maximum 
' value of 4. If Selection exceeds 4, the code resets it to 0. 

Pick:
  let Select = Select + 1	' Increment selection. 
  if Select < 5 then skip	' If Select = 5, then Select = 0. 
  let Select = 0		' Skip this line if Select is < 5. 
skip:
  lookup Select,(2,4,8,16,32),pins	' Light appropriate LED. 
goto Main				' Return to main program loop. 

' The tunes. Not necessarily music. 

Tune0: sound 0,(100,10,110,100): goto main

Tune1: sound 0,(98,40,110,10,100,40): goto main

Tune2: sound 0,(100,10,80,100): goto main

Tune3: sound 0,(100,10,110,50,98,10): goto main

Tune4: sound 0,(98,40,100,10,110,40): goto main
