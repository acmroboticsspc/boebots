'Program: slave.bas (BS1 to RSS485 interface via a SN75176 chip)
'
'This program interfaces a BASIC Stamp 1 to an RS485 network
' using the SN75176 Differential Bus Tranceiver chip from
' TI.  This program is meant to operate with another Stamp 1
' connected to the same RS485 network and is running the
' control.bs1 program.
'
'Pins 2 and 3 of the SN75176 chip are connected to pin 0 of
'  the Stamp 1.  Pins 1 and 4 of the SN75176 chip are
'  connected to Pin 1 of the Stamp 1.
'
'This program expects an LED and resistor in series to be connected to
'  pin 2 of the Stamp 1.  When an "H" comes across the RS485 network
'  pin 2 is set high, turning on the LED.  When an "L" is received
'  pin 2 of the Stamp 1 is turned off.
'
'Note: Setting pin 0 on the Stamp 1 low puts the SN75176 into
'  receive mode.  So any serial data received on pin 1 of the
'  Stamp 1 will be read in with the SERIN command.
'
'============ Initialize==========
output 2                                'Make pin 2 the LED connected pin.
output 0                                'Make pin 0 an output pin.
low 0                                   'Put the SN75176 into receive mode.

'============ Begin Program ======

loop1:
  SERIN 1,N2400,b0                      'Read a byte of data comming in.

  if b0<>"H" then is_low                'If H, then ...
  high 2                                '... set pin 2 high, truning on LED
  goto loop1

is_low:                                 'If not an H, then turn off the LED
  low 2

goto loop1                              'Loop forever


