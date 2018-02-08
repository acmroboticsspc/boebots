' Listing 38.3
' Nuts & Volts: Stamp Applications, April 1998
' -----[ Title ]-----------------------------------------------------
'
' File...... BRANCH.BAS
' Purpose... Demonstrates the use of BRANCH
' Author.... Jon Williams
' E-mail.... jonwms@aol.com
' WWW....... http://members.aol.com/jonwms
' Started... 07 MAR 98
' Updated... 07 MAR 98
' -----[ Program Description ]---------------------------------------
'
' This program demonstrates the use of BRANCH as a means of replacing
' confusing IF-THENs and GOTOs in a dynamic program
' -----[ Revision History ]------------------------------------------
'
' -----[ Constants ]-------------------------------------------------
'
' -----[ Variables ]-------------------------------------------------
'
SYMBOL state = B1
' -----[ EEPROM Data ]-----------------------------------------------
'
' -----[ Initialization ]--------------------------------------------
'
Init: state = 0
' -----[ Main Code ]-------------------------------------------------
'
Main: DEBUG "Checking the Input",CR ' check the input (sim)
' do other important things here
BRANCH state,(ProcA, ProcB, ProcC) ' do the next process

ProcA: DEBUG "In Process A",CR
PAUSE 1000 ' simulate timing of process
state = 1 ' point to next process
Goto Main

ProcB: DEBUG "In Process B",CR
PAUSE 1000
state = 2
Goto Main
 
ProcC: 	DEBUG "In Process C",CR
PAUSE 1000
state = 0
Goto Main
END
' -----[ Subroutines ]-----------------------------------------------
'
 
