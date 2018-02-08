' Program: MANY_SW.BAS (Read switches with POT command)

' This program illustrates a method for reading eight switches using 
' one I/O pin by using the POT command. The switches are wired as 
' shown in the accompanying application note to cut out portions of 
' a network of series-connected 1k resistors. The POT command reads
' the resulting resistance value. The subroutine ID_sw compares the
' value to a table of previously determined values to determine 
' which switch was pushed. 

Clear:			' Clear counter that determines how many
let b0 = 0		' readings are taken before switch is ID'ed. 

Again:
	pot 0,148,b2	' Take the resistance reading. 
	if b2 >= 231 then Clear	' Higher than 230 means no switch pushed. 
	goto ID_sw	' Value in range: identify the switch. 
Display: 
	debug b3	' Show the switch number on PC screen. 
goto Clear		' Repeat. 


' ID_sw starts with the lowest switch-value entry in the table (the 0th
' entry) and compares the POT value to it. If the POT value is less than
' or equal, then that's the switch that was pushed. If it's not
' lower, the routine checks the next switch-value entry. 

' There's nothing magical about the switch values in the table below. They
' were obtained by pressing the switches and recording their POT 
' values, then adding an arbitrary amount--in this case 10. The idea 
' was to select numbers that would always be higher than the highest 
' POT value returned when the corresponding switch was pressed, but 
' lower than the lowest value returned by the next switch. This keeps
' the comparison/search required to identify the switch as simple as 
' possible. 

ID_sw:
	if b0 > 8 then skip	' Take 8 readings before trying to 
	b0 = b0+1		' identify the switch. 
	goto Again
skip:
	for b3 = 0 to 7		' Compare table entries to the current reading. 
		lookup b3,(10,45,80,114,146,175,205,230),b4
		if b2 <= b4 then done	' Match? Then done. 
	next
done:	goto Display		' Switch identified; display its number. 
