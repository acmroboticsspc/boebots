' Program DICE.BAS
' An electronic dice game that uses two sets of seven LEDs
' to represent the pips on a pair of dice. 

Symbol	die1 = b0	' Store number (1-6) for first die.
Symbol	die2 = b1	' Store number (1-6) for ssecond die.
Symbol	shake = w3	' Random word variable
Symbol	pippat = b2	' Pattern of "pips" (dots) on dice. 
Symbol	Select = 7	' Pin number of die-select transistors.

high Select
let dirs = 255   	' All pins initially outputs. 
let die1 = 1     	' Set lucky starting value for dice (7). 
let die2 = 4     	' (Face value of dice = die1+1, die2+1.)

Repeat:            	' Main program loop. 
let pippat = die1	' Dice values are 1-6; lookup values, 0-5. 
gosub Display      	' Display die 1 pattern. 
let pippat = die2	' Now die 2. 
gosub Display
input Select         	' Change the Select line to input. 
if pin7 = 1 then Roll 	' Switch closed? Roll the dice. 
let w3 = w3+1        	' Else stir up w3 for random results. 
Reenter:             	' Return from Roll subroutine. 
output Select        	' Restore Select to output. 
goto Repeat

Display:                	' Look up pip pattern for die value. 
lookup pippat,(64,18,82,27,91,63),pippat
let pins = pins&%10000000	' Clear all pins except Select.
toggle Select           	' Invert Select.
let pins = pins|pippat   	' OR pippat into pins (Select unchanged). 
pause 4                  	' Leave on 4 milliseconds. 
return

Roll:
random shake            	' Put random number into 16-bit shake. 
let die1 = b6&%00000111 	' Use lower 3 bits of each byte. 
let die2 = b7&%00000111
if die1 > 5 then Roll    	' Throw back numbers over 5 (dice>6). 
if die2 > 5 then Roll
goto Reenter            	' Back to the main loop. 

