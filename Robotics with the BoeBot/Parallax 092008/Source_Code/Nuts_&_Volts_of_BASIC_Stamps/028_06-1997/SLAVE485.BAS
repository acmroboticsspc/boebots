' Program Listing 28.2. BS1 RS-485 Slave
' Program: SLAVE485.BAS (RS-485 net slave)
' This program demonstrates some basic principles of using an RS-485
' transceiver chip (LTC1487 in our setup). The program waits for
' an ID letter matching the value myID set below. When that letter
' is received, the data that follows--a number in text format like
' "192"--is saved to the byte variable and then written to the upper
' six output pins. The program acknowledges receipt of the data
' by sending its ID back to the master.
' >>The master program is set up to expect three slaves with ids "A",
' "B" and "C". Use this program for all slaves, but change the symbol
' myId below to "B" and "C" for the other slave units.

SYMBOL myId = "A" 		' ID letter of this node.
SYMBOL pinSet = b10 		' Pin setting for node.
SYMBOL xm_rcv = 0 		' Pin 0 sets transmit (1) or receive (0).
SYMBOL serIO = 1 		' Pin 1 is used for serial input/output.
again:
  dirs = %11111100 		' Set upper 6 pins to output.
  low xm_rcv 			' Set 485 transceiver to receive mode.
  serin serIO,t2400,(myId),#pinSet ' Wait for id, then receive number.
  pinSet = pinSet & %11111100 ' Strip off two lowest bits of number.
  let pins = pinSet 		' Write that value to outputs.
  high xm_rcv: pause 10 	' Set 485 transceiver for transmit.
  serout serIO,T2400,(myID) 	' Send back my ID letter.
goto again 			' Do it all over.

' Program Listing 28.3. BS2 RS-485 Master
' Program: MASTR485.BS2 (RS-485 net master for BS2)
' This program demonstrates some basic principles of using an RS-485
' transceiver chip (LTC1487 used in our setup). The program sends
' a byte to each of three 'slave' units, which write that bit
' pattern to their 6 output pins not used for RS-485 communication
' and control. To confirm receipt of the message, slaves reply with
' their node id, in this case "A", "B", or "C". If a slave does not
' respond within 100 milliseconds, this BS2 version of the master
' program displays an error message and carries on.

id 	var byte 		' ID number of net node.
reply 	var byte 		' Response from node.
pinSet 	var byte 		' Pin setting for node.
xm_rcv 	con 0 			' Pin 0 sets transmit (1) or receive (0).
serIO 	con 1 			' Pin 1 is used for serial input/output.
T2400 	con 396 		' Noninverted 2400-baud serial baudmode.

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
  for id = "A" to "C" 		' Cycle through ids A, B, C.
  high xm_rcv: pause 1 	' Turn on 485 transmitter; wait briefly.
  serout serIO,T2400,[id,DEC pinSet,"."] 'Id, bit pattern, and ".".
  low xm_rcv 			' Switch to receive mode.
  serin serIO,T2400,100,netErr,[reply] ' Get response.
  debug "Unit ", reply," OK.",cr ' Display on debug screen.
errReturn:
  next 				' Next unit.
  debug cr,cr 			' Scroll the screen.
  pinSet = pinSet * 2 		' Shift bit pattern left.
  if pinSet = 0 then begin 	' If bitpattern is 0, reload %00000100.
goto again 			' Else continue.

' If a slave unit does not repond within 100ms, the program comes here
' to display an error message on the screen, then continues with the
' next unit.
netErr:
  debug "Unit ", id,": NO RESPONSE.",cr ' Display on debug screen.
goto errReturn
