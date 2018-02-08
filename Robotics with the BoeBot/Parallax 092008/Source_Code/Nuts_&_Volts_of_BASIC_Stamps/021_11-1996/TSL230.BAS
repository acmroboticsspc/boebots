' Program Listing 21.1. BS1 Program to Demonstrate TSL230
' Program: TSL230.BAS (Interface with TSL230 light sensor)
' This program demonstrates the light-to-frequency conversion
' capability of the TSL230 sensor from Texas Instruments.
' BS1 pins 0 and 1 control the sensitivity of the '230 through
' its "electronic aperture" feature. The higher the sensitivity,
' the higher the frequency output for a given light intensity,
' as shown below:
' 	bit1 	bit0 	Sensitivity
' 	---- 	---- 	-----------
' 	0 	0 	sensor OFF
' 	0 	1 	x1
' 	1 	0 	x10
' 	1 	1 	x100
' Since the BS1 measures pulse width rather than frequency, its
' response is reciprocal; larger numbers mean less light. The program
' reverses this by dividing the light-dependent value into 65535.
' The program displays its readings on a 2x16 serial LCD module.

SYMBOL sens = b2 	' Sensitivity setting.
SYMBOL mult = b1 	' Multiplier for a given sensitivity.
SYMBOL light = w2 	' Light-intensity reading.
SYMBOL I = 254 	' Instruction prefix for LCD.
SYMBOL one = 128 	' Address of 1st LCD line.
SYMBOL two = 192 	' Address of 2nd LCD line.
dirs = %00000011 	' Make pins 0 and 1 outputs.

again: 			' Main program loop.
for sens = 1 to 3 	' Walk through sensitivity settings.
  pins = sens 		' Write sensitivity setting to pins.
  lookup sens,(0,1,10,100),mult ' Get the sensitivity multiplier.
  pulsin 2,1,light 	' Take a light reading.
  light = 65535/light 	' Compute reciprocal.
  serout 7,n2400,(I,one,"multiplier: x",#mult," ") ' Display.
  serout 7,n2400,(I,two,"light: ",#light," ")
  pause 1000 		' Wait a second between readings.
next 			' Next sensitivity.
goto again 		' Repeat forever.
