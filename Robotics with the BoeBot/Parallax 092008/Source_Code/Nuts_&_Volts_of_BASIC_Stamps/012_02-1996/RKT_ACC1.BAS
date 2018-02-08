' Listing 12.2, Model Rocket Acceleration Probe, by Dave Bodnar
REM Rocket telemetry program MINIMUM memory implementation
REM uses internal STAMP memory for data storage
' RKT_ACC1.BAS (was RKT_MIN5.BAS) D. Bodnar 7-6-95 6:27
Symbol Accel = 1 	'pin 1 for acceleration
Symbol LED1 = 2 	'pin to show ready for launch
Symbol LED2 = 3 	'pin (another) to show ready for launch
Symbol Ser_out = 7 	'pin for serial output of data (orange wire)
Symbol Delay = 8000 	' better at about 8000 - 1000 for testing only
realstart:
  read 255,b11 	'memory end location for writing
  b10=b11-1 		'make copy for later
  high LED2:high LED1 	'Both on FIRST
  pause Delay 		'RED only on (LED1)
  low LED2 		'GREEN only on (LED2)
  pause Delay 		'LAUNCH ready
  high LED2:low LED1
  pause Delay
  Low LED2
  gosub GETnWRITEit:
  b6=b2-3
waitforlaunch: 	'stay here till LAUNCH detected
  high LED1 		'flash RED while waiting
  pot accel,170,b2
  low LED1
  if b2>b6 then waitforlaunch:
High LED2
start:
  gosub GETnWRITEit:
  low LED2:pause 20:high LED2:pause 5
  if b11 >1 then start: 'loop till RAM full
  Low LED2
doneloop:
  b9=b10
  serout ser_out,n2400,("S",#b9,13,10) 'send "Start" & amount of RAM
  pause 2000
loop:
  read b9, b2:
  serout ser_out, n2400,(#b2,13,10)
  high LED1:Pause 20:low LED1:pause 20
  b9=b9-1
  if b9 > 1 then loop
  pause 5000
  goto doneloop:
GETnWRITEit:
  pot accel,170,b2 			'take reading
  Write b11,b2:b11=b11-1
Return
