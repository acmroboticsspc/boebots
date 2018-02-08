' Program: Moe
' Stamp participant in a simple ring-type network. This Stamp has the
' job of starting the network up by passing an initial message before
' receiving a cue. Thereafter, Moe only transmits when cued by Curly,
' the last Stamp on the net. 

input 7					' Set pin 7 to input. 
pause 1000				' Give others time to wake up. 
serout 7,OT2400,(10,13,"Three     ")	' Say your line. 
serout 7,OT2400,("Larry",10,13)		' Cue next

' Now enter the main program loop
Loop:
	serin 7,T2400,("Moe",10,13)		' Wait for cue. 
	serout 7,OT2400,(10,13,"Three     ")	' Say your line. 
	serout 7,OT2400,("Larry",10,13)		' Cue next
goto Loop

' Program: Larry
' Stamp participant in a simple ring-type network. Only transmits 
' when cued by Moe, the first Stamp on the net. 

input 7					' Set pin 7 to input. 

' Now enter the main program loop
Loop:
	serin 7,T2400,("Larry",10,13)		' Wait for cue. 
	serout 7,OT2400,("Blind     ")	' Say your line. 
	serout 7,OT2400,("Curly",10,13)		' Cue next
goto Loop



' Program: Curly
' Stamp participant in a simple ring-type network. Only transmits 
' when cued by Larry, the middle Stamp on the net. 

input 7					' Set pin 7 to input. 

' Now enter the main program loop
Loop:
	serin 7,T2400,("Curly",10,13)		' Wait for cue. 
	serout 7,OT2400,("Mice      ")	' Say your line. 
	serout 7,OT2400,("Moe",10,13)		' Cue next
goto Loop

