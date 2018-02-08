' Program Listing 25.4. Total-Shutdown Voltage Regulator for BS1
' Program: TURNOFF.BAS
' (BS1 controls LP2951 regulator for system shutdown)
' This program demonstrates how the Stamp can use a shutdown-
' capable power supply to provide push-on/auto-off power
' control. In this type of operation, the user presses a button,
' putting a low on the LP2951 shutdown pin and supplying 5 volts
' to the system. This starts up the Stamp, which immediately
' puts a low on the shutdown pin itself. This latches the power
' supply on after the user releases the button. The process takes
' only 20 ms, so even a brief button press will do. When the
' Stamp's work is done, it shuts itself (and everything else
' on the same power supply) off by putting a high on the shutdown
' pin. The supply remains off until the button is pressed again.
' If you have the Stamp powered by the LP2951, remember that you
' will have to hold the ON button down throughout downloading.
' Also, you may find that this program will not start up normally
' unless the Stamp programming cable is removed.

SYMBOL 	powerControl = 0 	' LP2951 shutdown pin.
SYMBOL 	LEDoutput = 7 		' LED output for demo.

PowerOn:
  low powerControl 		' Latch power supply ON.

'=======================================================
' Substitute your own code for the LED flasher below.
  for b2 = 1 to 20 		' Flash LED 10 on/off cycles.
  toggle LEDoutput 		' Toggle the LED.
  w2 = 700/b2 			' W2 sets delay that decreases..
  pause w2 			' ..with each cycle.
Next

'=======================================================
PowerOff:
high powerControl 		' Turn power (and Stamp) OFF.
