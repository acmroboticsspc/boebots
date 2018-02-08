' Program: DS1620.BAS
' This program interfaces the DS1620 Digital Thermometer to the 
' BASIC Stamp. Input and output subroutines can be combined to 
' set the '1620 for thermometer or thermostat operation, read 
' or write nonvolatile temperature setpoints and configuration 
' data. 

' ===================== Define Pins and Variables ================
SYMBOL  DQp = pin2      ' Data I/O pin. 
SYMBOL  DQn = 2         ' Data I/O pin _number_. 
SYMBOL  CLKn = 1        ' Clock pin number. 
SYMBOL  RSTn = 0        ' Reset pin number. 
SYMBOL  DSout = b0      ' Use bit-addressable byte for DS1620 output. 
SYMBOL  DSin = w0       ' "   "   "           word "   "      input. 
SYMBOL  clocks = b2     ' Counter for clock pulses. 

' ===================== Define DS1620 Constants ===================
' >>> Constants for configuring the DS1620
SYMBOL  Rconfig = $AC   ' Protocol for 'Read Configuration.'
SYMBOL  Wconfig = $0C   ' Protocol for 'Write Configuration.'
SYMBOL  CPU = %10       ' Config bit: serial thermometer mode. 
SYMBOL  NoCPU = %00     ' Config bit: standalone thermostat mode. 
SYMBOL  OneShot = %01   ' Config bit: one conversion per start request. 
SYMBOL  Cont = %00      ' Config bit: continuous conversions.
' >>> Constants for serial thermometer applications. 
SYMBOL  StartC = $EE    ' Protocol for 'Start Conversion.'
SYMBOL  StopC = $22     ' Protocol for 'Stop Conversion.'
SYMBOL  Rtemp = $AA     ' Protocol for 'Read Temperature.'
' >>> Constants for programming thermostat functions. 
SYMBOL  RhiT = $A1      ' Protocol for 'Read High-Temperature Setting.'
SYMBOL  WhiT = $01      ' Protocol for 'Write High-Temperature Setting.'
SYMBOL  RloT = $A2      ' Protocol for 'Read Low-Temperature Setting.'
SYMBOL  WloT = $02      ' Protocol for 'Write Low-Temperature Setting.'

' ===================== Begin Program ============================
' Start by setting initial conditions of I/O lines. 
low RSTn        ' Deactivate the DS1620 for now. 
high CLKn       ' Initially high as shown in DS specs. 
pause 100       ' Wait a bit for things to settle down. 

' Configure the DS1620 for thermometer operation. The configuration 
' register is nonvolatile EEPROM. You only need to configure 
' the DS1620 once. It will retain those configuration settings 
' until you change them--even with power removed. To conserve 
' Stamp program memory, you can preconfigure the DS1620, then 
' remove the configuration code from your final program. (You'll
' still have to issue a start-conversion command, though.)

let DSout=Wconfig       ' Put write-config command into output byte. 
gosub Shout             ' And send it to the DS1620. 
let DSout=CPU+Cont      ' Thermometer, continuous conversion. 
gosub Shout             ' Send to DS1620. 
low RSTn                ' Deactivate '1620. 
pause 50                ' Wait 50ms for EEPROM programming cycle. 
let DSout=StartC        ' Now, start the conversions by 
gosub Shout             ' sending the start protocol to DS1620. 
low RSTn                ' Deactivate '1620.

' The loop below continuously reads the latest temperature data from 
' the DS1620. The '1620 performs one temperature conversion per second. 
' If you read it more frequently than that, you'll get the result
' of the most recent conversion. The '1620 data is a 9-bit number 
' in units of 0.5 deg. C. See the ConverTemp subroutine below. 

Again: 
  pause 1000            ' Wait 1 second for conversion to finish. 
  let DSout=Rtemp       ' Send the read-temperature opcode. 
  gosub Shout
  gosub Shin            ' Get the data. 
  low RSTn              ' Deactivate the DS1620. 
  gosub ConverTemp      ' Convert temperature reading to absolute. 
  gosub DisplayF        ' Display in degrees F. 
  gosub DisplayC        ' Display in degrees C. 
goto Again

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
high RSTn               ' Activate DS1620. 
output DQn              ' Set to output to send data to DS1620. 
for clocks = 1 to 8     ' Send 8 data bits. 
  low CLKn              ' Data is valid on rising edge of clock.  
  let DQp = bit0        ' Set up the data bit. 
  high CLKn             ' Raise clock. 
  let DSout=DSout/2     ' Shift next data bit into position. 
next                    ' If less than 8 bits sent, loop. 
return                  ' Else return. 

' Subroutine: Shin
' Shift bits in from the DS1620. Reads 9 bits into the lsbs of DSin 
' (w0). Shin is written to get 9 bits because the DS1620's temperature
' readings are 9 bits long. If you use Shin to read the configuration
' register, just ignore the 9th bit. Note that DSin overlaps with DSout.
' If you need to save the value shifted in, copy it to another register
' before the next Shout. 
Shin:
input DQn               ' Get ready for input from DQ. 
for clocks = 1 to 9     ' Receive 9 data bits. 
  let DSin = DSin/2     ' Shift input right. 
  low CLKn              ' DQ is valid after falling edge of clock. 
  let bit8 = DQp        ' Get the data bit. 
  high CLKn             ' Raise the clock. 
next                    ' If less than 9 bits received, loop. 
return                  ' Else return. 

' ================= Data Conversion/Display Subroutines ===============
' Subroutine: ConverTemp
' The DS1620 has a range of -55 to +125 degrees C in increments of 1/2
' degree. It's awkward to work with negative numbers in the Stamp's 
' positive-integer math, so I've made up a temperature scale called 
' DSabs (DS1620 absolute scale) ranging from 0 (-55C) to 360 (+125C).
' Internally, your program can do its math in DSabs, then convert to 
' degrees F or C for display. 

ConverTemp:
if bit8 = 0 then skip   ' If temp > 0 skip "sign extension" procedure. 
  let w0 = w0 | $FE00   ' Make bits 9 through 15 all 1s to make a 
			' 16-bit two's complement number. 
skip:   
  let w0 = w0 + 110     ' Add 110 to reading and return. 
return

' Subroutine: DisplayF
' Convert the temperature in DSabs to degrees F and display on the 
' PC screen using debug. 

DisplayF:
let w1 = w0*9/10        ' Convert to degrees F relative to -67. 
if w1 < 67 then subzF   ' Handle negative numbers. 
  let w1 = w1-67
  Debug #w1, " F",cr
return
subzF: 
  let w1 = 67-w1        ' Calculate degrees below 0. 
  Debug "-",#w1," F",cr ' Display with minus sign. 
return

' Subroutine: DisplayC
' Convert the temperature in DSabs to degrees C and display on the 
' PC screen using debug. 

DisplayC:
let w1 = w0/2           ' Convert to degrees C relative to -55. 
if w1 < 55 then subzC   ' Handle negative numbers. 
  let w1 = w1-55
  Debug #w1, " C",cr
return
subzC: 
  let w1 = 55-w1        ' Calculate degrees below 0. 
  Debug "-",#w1," C",cr ' Display with minus sign. 
return
