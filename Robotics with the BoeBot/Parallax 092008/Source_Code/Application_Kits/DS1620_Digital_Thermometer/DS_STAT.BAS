' Program: DS_STAT.BAS
' This program interfaces the DS1620 Digital Thermometer to the 
' BASIC Stamp to configure it for thermostat operation. A PC 
' running terminal software, should be connected to the Stamp
' with data out to pin 4 (through a 22k resistor) and data in 
' to pin 3: 

'       Function       DB25 Pin       DB9 Pin         Stamp Pin 
'         GND             7              5               GND
'       Transmit          2              3                4
'       Receive           3              2                3

' Hardware handshaking must be disabled in the terminal software. 
' Communication format is 2400 baud, no parity, 8 data bits, 
' 1 stop bit. The Stamp prompts the user for the high and low 
' temperature setpoints, then copies these to the registers of the 
' DS1620. The setpoints _must_ be preceded by the appropriate sign
' (+ or -). The program does not assume that "78" means "+78." 
' Once the DS1620 is programmed, the circuit should be turned
' off, and the DS1620 removed for installation in its standalone
' thermostat application. 

' ===================== Define Pins and Variables ================
SYMBOL  ComIn = 4	' Serial communication input pin. 
SYMBOL  ComOut = 3	' Serial communication output pin. 
SYMBOL 	DQp = pin2	' Data I/O pin. 
SYMBOL  DQn = 2		' Data I/O pin _number_. 
SYMBOL  CLKn = 1	' Clock pin number. 
SYMBOL  RSTn = 0	' Reset pin number. 
SYMBOL  DSout = w0	' Use bit-addressable word for DS1620 output. 
SYMBOL  DSin = w0	' "   "   "           word "   "      input. 
SYMBOL  clocks = b2	' Counter for clock pulses. 
SYMBOL  config = b3	' Copy of the DS1620 configuration bits. 
SYMBOL  setTemp = w2	' Copy of the temperature setting. 
SYMBOL  comData = b6	' Serial input data. 
SYMBOL  index = b7	' Temporary counter used in prompts. 

' ===================== Define DS1620 Constants ===================
' >>> Constants for configuring the DS1620
SYMBOL  Rconfig = $AC	' Protocol for 'Read Configuration.'
SYMBOL  Wconfig = $0C	' Protocol for 'Write Configuration.'
SYMBOL  CPU = %10	' Config bit: serial thermometer mode. 
SYMBOL  NoCPU = %00	' Config bit: standalone thermostat mode.  
SYMBOL  OneShot = %01	' Config bit: one conversion per start request. 
SYMBOL  Cont = %00	' Config bit: continuous conversions after start.
' >>> Constants for serial thermometer applications. 
SYMBOL  StartC  = $EE	' Protocol for 'Start Conversion.'
SYMBOL  StopC   = $22	' Protocol for 'Stop Conversion.'
SYMBOL  Rtemp   = $AA	' Protocol for 'Read Temperature.'
' >>> Constants for programming thermostat functions. 
SYMBOL RhiT = $A1	' Protocol for 'Read High-Temperature Setting.'
SYMBOL  WhiT = $01	' Protocol for 'Write High-Temperature Setting.'
SYMBOL  RloT = $A2	' Protocol for 'Read Low-Temperature Setting.'
SYMBOL  WloT = $02	' Protocol for 'Write Low-Temperature Setting.'

' ===================== Begin Program ============================
' Start by setting initial conditions of I/O lines. 
Start:
low RSTn	' Deactivate the DS1620 for now. 
high CLKn	' Initially high as shown in DS specs. 

' Next, send prompts and gather data for configuration. 
promptUser:
Serout comOut,N2400,(13,"Hi:")	' Prompt for the high setpoint. 
For index = 0 to 1		' Prepare to get both setpoints. 
  let setTemp = 0		' Clear the high- and low-temp registers. 
  Serin comIn,N2400,comData	' Get the sign of the temperature (+/-).
  if comData = "+" then skip1	' Prepare for positive numbers. 
  if comData <> "-" then Err	' If not negative then what? Error..
  let setTemp = 256		' If negative, set the sign bit. 
skip1:
  Serin comIn,N2400,#comData	' Now get the numeric part of the entry. 
  let comData = comData * 2	' Times 2 for 1620's 0.5-degree scale. 
  if setTemp = 0 then skip2	' Skip next line if positive. 
  let comData=comData^255+1	' If negative, take two's complement. 
skip2:
  let setTemp = setTemp | comData	' Merge entry value into setTemp. 
  let DSout = WloT		' First entry is hi temp, 2nd is lo temp. 
  if index = 1 then skip3:
  let DSout = WhiT
skip3:
  gosub Shout		' Send protocol..
  let DSout = setTemp	' ..and the temperature setting.. 
  gosub Shout		' ..to the DS1620. (Two Shouts are needed to
  gosub Shout		' send 9-bit temp data to the DS1620.)
  low RSTn		' Deactivate '1620. 
  pause 100		' Let EEPROM self-program. 
  let DSout = RloT	' Read back the value written to '1620. 
  if index = 1 then skip4:
  let DSout = RhiT
skip4:
  gosub Shout		' Send read protocol to the DS1620. 
  gosub Shin 		' Get the data. 
  low RSTn		' Deactivate '1620. 
  if DSin = setTemp then skip5	' Compare to the value written. 
  goto Fail		' If they're different, print "fail" msg.
skip5:
  if index <> 0 then skip6	' Now set up to prompt for lo temp. 
  Serout comOut,N2400,(13,"Lo:")
next index  
skip6:
  let config = NoCPU+OneShot	' Temps done: configure the '1620. 
skip7: 
  let DSout = Wconfig
  gosub Shout
  let DSout = config
  gosub Shout  
  low RSTn
  pause 100
  let DSout = Rconfig		' Read back the configuration and..
  gosub Shout	
  gosub Shin 
  low RSTn	 
  let DSin = DSin & %11		' ..compare it to value sent. 
  if DSin = config then skip8
  goto Fail			' If they're different, print "fail" msg. 
skip8:
  Serout comOut,N2400,(" OK")	' Else, print "OK".
end				' Stop until power cycles. 

Err:
  Serout comOut,N2400,(" ERR")	' Signal data-entry error. 
  end

Fail: 
  Serout comOut,N2400,(13," FAIL")	' Signal 1620 write error. 
  end

' ===================== DS1620 I/O Subroutines ==================
' Subroutine: Shout
' Shift bits out to the DS1620. Sends the 8 bits stored in DSout
' (b0). Note that Shout activates the DS1620, since all trans-
' actions begin with the Stamp sending a protocol (command). It does 
' not deactivate the DS1620, though, since many transactions either 
' send additional data, or receive data after the initial protocol. 
' Note that Shout destroys the contents of DSout in the process of 
' shifting it. If you need to save this value, copy it to another 
' register. 
Shout:
high RSTn		' Activate DS1620. 
output DQn		' Set to output to send data to DS1620. 
for clocks = 1 to 8	' Send 8 data bits. 
  low CLKn		' Data is valid on rising edge of clock.  
  let DQp = bit0	' Set up the data bit. 
  high CLKn		' Raise clock. 
  let DSout=DSout/2	' Shift next data bit into position. 
next			' If less than 8 bits sent, loop. 
return			' Else return. 

' Subroutine: Shin
' Shift bits in from the DS1620. Reads 9 bits into the lsbs of DSin 
' (w0). Shin is written to get 9 bits because the DS1620's temperature
' readings are 9 bits long. If you use Shin to read the configuration
' register, just ignore the 9th bit. Note that DSin overlaps with DSout.
' If you need to save the value shifted in, copy it to another register
' before the next Shout. 
Shin:
input DQn		' Get ready for input from DQ. 
for clocks = 1 to 9	' Receive 9 data bits. 
  let DSin = DSin/2	' Shift input right. 
  low CLKn		' DQ is valid after falling edge of clock. 
  let bit8 = DQp	' Get the data bit. 
  high CLKn		' Raise the clock. 
next			' If less than 9 bits received, loop. 
return			' Else return. 
