' Program Listing 14.1 Scanning LED Display
' Program: SCANLED.BAS (Scanning LED display developed by Ralph Wallio
' and his crew at Indianola High School)
' This is a partial listing of the scanning LED display program. I've
' shortened it to conserve space, since the majority of the program
' is EEPROM data making up the message "IHS AUDITORIUM CREW." Just
' the portion "IHS" is enough to convey the principle.

DIRS=%01111111 ' Bit 7 is input for trigger; the rest outputs.

' LED bit patterns forming the letters IHS.

EEPROM 0,(65,65,127,65,65,0,127,8,8,8,127,0,49,73,73,73,69)
' --------------- ------------- --------------
' I H S
' Wait until triggered before displaying bit patterns on LEDs.

Trigger_Loop: BUTTON 7,0,0,0,B2,1,Display

GOTO Trigger_Loop
Display: FOR B3=0 TO 16 	' Get data from EEPROM.
  READ B3, PINS 		' Copy each EEPROM byte to pins.
  PINS=0 			' Blank the LEDs.
  NEXT 				' Get next byte of data.
GOTO Trigger_Loop 		' Done. Wait for next rotation.
