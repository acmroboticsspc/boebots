'Program: control.bas (BS1 to RS485 interface via a SN75176 chip)
'
'This program interfaces a BASIC Stamp 1 to an RS485 network
' using the SN75176 Differential Bus Tranceiver chip from
' TI.  This program is meant to operate with another Stamp 1
' connected to the same RS485 network and is running the
' slave.bs1 program.
'
' Pins 2 and 3 of the SN75176 chip are connected to pin 0 of
'  the Stamp 1.  Pins 1 and 4 of the SN75176 chip are
'  connected to Pin 1 of the Stamp 1.
'
' This program expects an active-low button or switch to be 
'  connected to pin 2 of the Stamp 1. When the button or switch
'  sets pin 2 low, the character "L" is sent over the RS485
'  network.  When the button or switch sets pin 2 high then
'  the character "H" is sent over the network.
'
' Note. Setting pin 0 on the Stamp 1 high puts the SN75176 into 
'  transmit mode.  So any serial data transmitted out of pin 1 on
'  the Stamp 1 will be transmitted over the RS485 network.
'
'========== Initialize ============
input 2					'Make pin 2 the button input
output 0				'Make pin 0 an output
high 0					'Put the SN75176 into transmit mode

'========== Begin Program =========

 if pin2<>1 then loop1:			'If pin 2 is initially High, send...
  SEROUT 1,N2400,("H")			'... an "H" over the RS485 network

'========== Main Loop =============

loop1:
  BUTTON 2,0,255,0,b0,1,preloop2	'Wait till pin 2 is low
  goto loop1

preloop2:
 SEROUT 1,N2400,("L")			'Send a "L" over the RS485 network.

loop2:
  BUTTON 2,1,255,250,b0,1,loop_again    'Wait till pin 2 goes high
  goto loop2

loop_again:
  SEROUT 1,N2400,("H")                  'Send an "H" over the rs485 network.

goto loop1:				'Loop forever


