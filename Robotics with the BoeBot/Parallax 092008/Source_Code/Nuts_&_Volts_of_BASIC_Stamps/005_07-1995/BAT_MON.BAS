' Listing 5.1. BASIC Stamp Battery Monitor
' CHEAP AND SIMPLE LOW BATTERY WARNING
' Guy Marsden, March 1995. tekart@well.com
'
' Uses a Cds opto coupler with LED input such as: CLM6000.
' Put a 33 or 47K resistor across cell and a .1uF cap to gnd.
' Use a high value resistor in series with the LED wired directly
' to the battery. I used 100k for a 12volt system. This value is
' enough to produce a resistance of 20k at nominal battery voltage.
'
' When the voltage drops, the Cds resistance increases. By setting
' the SCALE of the POT function under a low battery condition, you
' will then have a range to work with. Determine your setpoint
' using a variable bench supply and a DVM.
symbol Batt = b2
symbol LoBatt = 220
CheckBatt:
  pot 0,76,Batt 		' check battery voltage
  if Batt > LoBatt Alarm 	' if less than established value
goto CheckBatt
Alarm:
  sound 1,100,100 		' beep piezo alarm
  pause 100
goto Alarm
