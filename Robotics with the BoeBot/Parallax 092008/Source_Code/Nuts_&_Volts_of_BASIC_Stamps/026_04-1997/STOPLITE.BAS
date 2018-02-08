' Program Listing 26.1. Stoplight control for BS1
' Program: STOPLITE.BAS (Sequence a stoplight from a lookup table.)
' This program generates proper green-yellow-red sequencing for a
' pair of traffic signals controlling an intersection. I refer
' to one street as "EW" (east-west) and the other as "NS" (north-
' south). Pins are connected to LEDs as follows:
' 	pin5 	EW/red 		pin2 	NS/red
' 	pin4 	EW/yellow 	pin1 	NS/yellow
' 	pin3 	EW/green 	pin0 	NS/green
' ====Constants===
' The program uses six 16-bit constants to represent the states
' of the lights (lower 6 bits) and the length of time to leave
' the lights in those states (upper 10 bits). The usual way
' to create such constants is to define the bit patterns
' and the times separately, then have the compiler add or
' logically OR them together. Unfortunately, the simple STAMP
' host program doesn't have this feature, so we'll have to do
' it by hand. Here's how the constants are organized:
' 		Duration (ms) Pattern of lights
' 			  \     /
' 		   |=========|====|
SYMBOL NSgo 	= %0010000000100001 	' NS green/EW red, 8192 ms.
SYMBOL NSyel 	= %0000100000100010 	' NS yellow/EW red, 2048 ms.
SYMBOL allRed 	= %0000001000100100 	' NS red/EW red, 512 ms.
SYMBOL EWgo 	= %0010000000001100 	' NS red/EW green, 8192 ms.
SYMBOL EWyel 	= %0000100000010100 	' NS red/EW yellow, 2048 ms.
' ===Variables===
SYMBOL seq = b11 		' Current state (0-5) of stoplight sequence.
SYMBOL lkup = w4 		' Number from lookup table.
' ===Program===
dirs = %00111111 		' Set lower six pins to output.
again: 				' Endless loop.
for seq = 0 to 5 		' For each of six stored patterns/times..
  lookup seq,(NSgo,NSyel,allRed,EWgo,EWyel,allRed),lkup ' Get bits.
  pins = lkup & %00111111 	' Copy lower 6 bits to pins.
  lkup = lkup & %1111111111000000 ' Strip off lower 6 bits.
  pause lkup 		' Set delay to upper 10 bits.
next 			' ..and get the next entry from the table.
goto again 		' Repeat endlessly.
