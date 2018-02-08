' Program Listing 27.1. Water-level measurement
' Program: H2OLEVEL.BAS (Measure water level via variable capacitance)
' This program works with the circuit shown in the accompanying
' article to measure the water level in a plastic-tube measuring
' column. Since water 'seeks its own level,' such a measuring
' column can be used to measure water levels in containers from
' a bucket to a a lake! The program is straightforward--most
' of the magic is in the circuit. An oscillator whose frequency
' depends in part on the capacitance of the measuring column
' is divided by 16,384 by a ripple counter. This program times
' the low-high-low (pulse) portion of the output and converts it
' to fluid ounces. The conversion is based on a series of manual
' measurements, and depends (obviously) on the characteristics of
' the measuring column and the size of the container being monitored.

'========Variables and Constants
SYMBOL level = w4 	' Water level measurement in Pulsin units.
SYMBOL oz = b6 	' Measurement converted to fluid ounces.
SYMBOL checks = b5 	' Number of out-of-range readings in a row.
SYMBOL lcd = 7 	' Serial LCD on pin 7.
SYMBOL I = 254 	' Instruction prefix for LCD.
SYMBOL clsLCD = 1 	' Clear-screen instruction for LCD.
SYMBOL line2 = 196 	' Location on 2nd line.

'========Main Program
' This code measures the incoming pulses and scales them to
' ounces based on previous manual measurements. It also keeps
' track of out-of-range readings and, if it gets 10 in a row,
' displays a message on the LCD. By waiting for more than
' one bad reading, the program is tolerant of glitches and noise
' that might otherwise cause occasional false readings.
setup:
  serout lcd,n2400,(I,clsLCD, " WATER LEVEL:" ) ' Label LCD screen.
restart:
  checks = 0 		' Reset out-of-range counter.
again:
  if checks > 10 then handleError ' > 10 bad readings? Tell user.
  checks = checks + 1 	' Increment checks.
  pulsin 0,1,level 	' Take a raw reading.
  if level < 3683 then again ' Out of range (low)? Try again.
  if level > 3850 then again ' Out of range (high)? Try again.
  oz = level - 3683 * 16 / 55 + 16 ' In range: Calc ounces.
  serout lcd,n2400,(I,line2,#oz," ounces") ' Display on LCD.
goto restart 		' Do it again.

'========Error Handler
' If 10 pulsin readings in a row are out of range, the bucket may be
' empty or overflowing, or the electronics are malfunctioning (wet?).
' These routines print appropriate messages on the LCD.
handleError:
  if level < 3683 then empty
  serout lcd,n2400,(I,line2, "OVERFLOW ")
  pause 1000
goto restart
empty:
  serout lcd,n2400,(I,line2, "-EMPTY- ")
  pause 1000
goto restart
