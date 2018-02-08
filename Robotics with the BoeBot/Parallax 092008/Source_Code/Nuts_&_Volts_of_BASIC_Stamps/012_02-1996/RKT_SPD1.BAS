' Listing 12.1, Model Rocket Velocity Probe, by Dave Bodnar
REM Rocket telemetry program MINIMUM memory implementation
REM for use with PROPELLER/SPEED sensor only
REM uses internal STAMP memory for data storage
' RKT_SPD1.BAS (was RKT_MIN5.BAS) D. Bodnar 7-6-95 6:53
Symbol Prop = 0 	'pin 0 for propellor
Symbol LED1 = 2 	'pin to show ready for launch
Symbol LED2 = 3 	'pin (another) to show ready for launch
Symbol Buzz = 5 	'pin for Piezo buzzer
Symbol Ser_out = 7 	'pin for serial output of data (orange wire)
Symbol Delay = 8000 	'better at about 8000 - 1000 for testing only
Symbol STOPmem=2
realstart:
  read 255,b11 	'memory end location for writing
'debug b11,cr
  b11=b11-1
  b10=b11 		'make copy for later
  LOW buzz
  high LED2:high LED1 	'Both on FIRST
  pause Delay 		'RED only on (LED1)
  HIGH buzz
  low LED2 		'GREEN only on (LED2)
  pause Delay 		'LAUNCH ready
  LOW buzz
  high LED2:low LED1
  pause Delay
  Low LED2
  HIGH buzz

waitforlaunch: 	'stay here till LAUNCH detected
  high LED1 		'flash RED while waiting
  pulsin prop,1,w2
  low LED1
  if w2=0 then waitforlaunch:
  High LED2
  LOW buzz
start:
gosub GETnWRITEit:
  if b11 >STOPmem then start: 'loop till RAM full
  Low LED2
  HIGH buzz
doneloop:
  b9=b10		'debug "start of end",cr
  LOW buzz
  serout ser_out,n2400,("START",#b9,13,10) 'send "Start" & amount of RAM
  pause 2000
loop:
  HIGH buzz
  read b9, b7:b9=b9-1:read b9,b8
' debug #b9,"+1 hi=",#b7," lo=",#b8
  w2=b7*256 + b8
' debug " ",#w2,cr
  LOW buzz
  serout ser_out, n2400,(#w2,13,10)
' high LED1:Pause 20:low LED1:pause 20
  b9=b9-1
  if b9 > STOPmem then loop
   pause 5000
goto doneloop:
GETnWRITEit:
  HIGH buzz:PAUSE 40
  pulsin prop, 1,w2 'take reading
  b7=w2/256
  w1= b7*256
  b8=w2-w1
' debug #b11,"hi=",#b7," lo=",#b8, " 16bit=",#w2,cr
  Write b11,b7:b11=b11-1:write b11,b8:b11=b11-1
  LOW buzz:PAUSE 40
Return
