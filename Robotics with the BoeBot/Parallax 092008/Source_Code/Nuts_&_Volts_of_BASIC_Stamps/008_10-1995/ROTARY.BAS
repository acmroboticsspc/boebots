' Program Listing 8.1 Demonstrating Use Of a Rotary Encoder
' Program: Rotary.BAS (Read a rotary encoder and scroll a display)
' This program demonstrates the use of a digital rotary encoder
' as a unique user-interface control. Turning the knob scrolls a
' virtual tuning dial displayed on an LCD. To keep the code and
' hardware as simple as possible, the LCD is equipped with an
' LCD Serial Backpack, which interprets data and instructions sent
' to it serially.
SYMBOL old = b0 		' Previous state of encoder bits.
SYMBOL new = b1 		' Current state of encoder bits.
SYMBOL directn = bit0 		' Direction of encoder travel; 1=CW.
SYMBOL index1 = b2 		' For/Next counter variable.
SYMBOL index2 = b3 		' For/Next counter variable.
SYMBOL I = 254 		' Instruction-toggle for LCD.
SYMBOL LCD_cls = 1 		' Clear-screen instruction for LCD.
SYMBOL left = 24 		' Scroll-left instruction for LCD.
SYMBOL right = 28 		' Scroll-right instruction for LCD.
' The program starts by printing a scale on the LCD screen.
' The LCD's RAM can hold up to 80 characters and scroll them
' circularly across the display. The code below prints...
' 0........10........20........30........40........50..
' ...up to 70. Only the first 24 characters are initially
' visible on the display, but turning the encoder knob scrolls
' them into view, like an old-fashioned radio tuning dial.
pause 1000 ' Let LCD initialize.
serout 0,n2400,(I,LCD_cls,I) 	' Clear LCD screen.
for index1 = 0 to 70 step 10 	' Scale: 0-70 (uses 80-char LCD RAM).
serout 0,n2400,(#index1) 	' Print number on the screen.
for index2 = 1 to 8
serout 0,n2400,(".") ' Print "........" between numbers.
next
next
' Before entering the main loop, the program stores the beginning
' state of the encoder bits into the variable 'new.' It ANDs the
' pins with %11000000 in order to strip off all bits except for
' 6 and 7. (ANDing a bit with 0 always produces 0; ANDing with 1
' copies the state of the bit.)
let new = pins & %11000000 	' Mask off all but bits 6 & 7.
start:
  let old = new & %11000000 	' Mask bits and copy new into old.
again:
  let new = pins & %11000000 	' Copy encoder bits to new.
  if new = old then again 	' If no change, try again.
  let directn = bit6 ^ bit15 	' XOR right bit of new w/ left bit of old.
  if directn = 1 then CW 	' If result=1, encoder turned clockwise.
  serout 0,n2400,(I,left,I) 	' If result=0, counterclock (scroll left).
goto start ' Do it again.
CW:
  serout 0,n2400,(I,right,I) 	' Clockwise (scroll right).
goto start ' Do it again.
