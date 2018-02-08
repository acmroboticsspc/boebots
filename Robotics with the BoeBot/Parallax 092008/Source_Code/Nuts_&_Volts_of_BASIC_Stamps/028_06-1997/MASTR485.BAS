' Program Listing 28.1. BS1 RS-485 Master
' Program: MASTR485.BAS (RS-485 net master)
' This program demonstrates some basic principles of using an RS-485
' transceiver chip (LTC1487 used in our setup). The program sends
' a byte to each of three 'slave' units, which write that bit
' pattern to their 6 output pins not used for RS-485 communication
' and control. To confirm receipt of the message, slaves reply with
' their node id, in this case "A", "B", or "C".

SYMBOL id = b11 		' ID number of net node.
SYMBOL reply = b10 		' Response from node.
SYMBOL pinSet = b9 		' Pin setting for node.
SYMBOL xm_rcv = 0 		' Pin 0 sets transmit (1) or receive (0).
SYMBOL serIO = 1 		' Pin 1 is used for serial input/output.

' For the purpose of the demo, the pinSet value that the master will
' tell the slaves to write to their outputs will be an easy-to-
' recognize sequence: 000001 000010 000100 001000 010000 100000 000001...
' You can connect LEDs to the slaves' pins 2-7 to watch the sequence,
' or take my word for it. Since the lowest two bits are reserved for
' use by the RS-485 transceiver, the starting pattern is 00000100.
begin:
  pinSet = %00000100 		' Starting bit pattern.
again:
  pause 1000 			' Run slowly for demo purposes.
   for id = "A" to "C" 	' Cycle through ids A, B, C.
    high xm_rcv: pause 1 	' Turn on 485 transmitter; wait briefly.
    serout serIO,T2400,(id,#pinSet,".") 'Send id, bit pattern, and ".".
    low xm_rcv 		' Switch to receive mode.
    serin serIO,t2400,reply 	' Receive the slave's response.
    debug "Unit ", #@reply," OK.",cr ' Display on debug screen.
   next 			' Next unit.
   debug cr,cr 		' Scroll the screen.
   pinSet = pinSet * 2 	' Shift bit pattern left.
   if pinSet = 0 then begin 	' If bitpattern is 0, reload %00000100.
goto again 			' Else continue.