' Program Listing 23.1 BS1 Program to Demonstrate Mondo-Tronics H-bridge
' Program: MONDOMOT.BAS (Demonstrate Mondo-Tronics H-bridge)
' This program demonstrates the Mondo motor controller to
' control the direction and speed of a DC motor. Connect
' input A of the controller to Stamp pin 0; B to pin 1; and
' GND to GND. Run the program. The motor will slowly accelerate
' to top speed, then stop and repeat the acceleration in
' reverse. This program uses a carry-the-1 method of generating
' duty cycle control of motor speed. When you add a number to an
' "accumulator" (a memory location of fixed size), the accumulator
' will overflow if the result is bigger than it can hold. The
' larger the number added, the more likely an overflow or "carry"
' is. This program adds the desired motor duty cycle to an
' accumulator, and turns the motor on only when there's a carry.
' Higher duty cycles make the motor run faster. This method works
' well with the Stamp because it is more or less independent of
' speed.

SYMBOL motAcc = b11 		' Motor-speed "accumulator."
SYMBOL motDir = bit0 		' Motor direction: 0=fwd; 1=reverse.
SYMBOL spd = b10 		' Motor speed, 0 (off) to 15 (full on).
SYMBOL cycles = b9 		' Number of loops at a given speed.
SYMBOL A_ = pin0 		' Controller A input.
SYMBOL B_ = pin1 		' Controller B input.

dirs = %11 			' Set pins 0 and 1 to output.

again:
  for cycles = 0 to 255 	' Turn 255 cycles at each speed.
   gosub motor 		' Output to motor.
  next
  spd = spd +1 		' Increase speed.
  if spd <= 15 then again 	' If speed is > 15, then..
  spd = 0 			' ..turn motor off..
  motDir = motDir ^ 1 		' ..and reverse direction.
goto again 	' Loop forever.

motor:
  motAcc = motAcc & %1111 	' Limit motAcc to 4 bits.
  motAcc = motAcc + spd 	' Add speed.
  if motAcc >= 16 then motOn 	' If carry, then turn on motor.
  A_ = B_ 			' Otherwise, motor off.
Return

' If you look at the table accompanying the H-bridge, you'll
' see that the motor is on only when inputs A and B are opposite.
' Programming shorthand for this is to set A to the motor
' direction, and make B = NOT A. PBASIC1 does not have a NOT
' function per se (see LET in the manual), so we make do by
' XORing A with 1, which has the same effect: B = A ^ 1.

motOn:
  A_ = motDir: B_ = A_ ^ 1
return
