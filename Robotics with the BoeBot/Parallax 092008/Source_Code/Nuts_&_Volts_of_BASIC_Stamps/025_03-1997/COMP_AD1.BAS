' Listing 25.1. Simple (but dumb) Comparator ADC for BS1
' Program: COMP_AD1.BAS
' (Single-slope ADC with the BS1 and a comparator)
' This program implements a single-slope ADC with a comparator.
' The unknown voltage goes to the comparator's + input and the
' Stamp's PWM output to the - input. The Stamp incrementally
' increases the PWM output to the comparator reference until
' the reference exceeds the input voltage. This is a simple
' (albeit not very intelligent) way to make an ADC. One major
' drawback of this approach is that the higher the unknown
' voltage, the longer the conversion takes. See the program
' COMP_AD2.BAS for a vastly improved version.

SYMBOL refOut = 0 		' Comparator reference.
SYMBOL compIn = pin1 		' Comparator output.
SYMBOL ADCres = b2 		' Analog-to-digital result.

' Demonstration loop: take a conversion, display it, and loop.

again:
  gosub ADconvert 		' Perform conversion.
  debug ADCres 		' Display it.
goto again 			' Do it again.

' ADC conversion routine.
ADconvert:
  ADCres = 0 			' Start at 0 volts.

convLoop:
  PWM refOut,ADCres,1 		' Output 1 PWM cycle.
  if compIn = 0 then done 	' If reference > unknown, done.
  ADCres = ADCres + 1 		' Otherwise, increase by 1.
  if ADCres <> 0 then convLoop ' If rollover from 255 to 0, quit.

done:
return 				' Return to program.
