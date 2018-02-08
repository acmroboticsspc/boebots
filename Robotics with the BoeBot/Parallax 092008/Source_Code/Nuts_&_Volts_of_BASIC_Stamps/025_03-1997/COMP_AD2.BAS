' Listing 25.2. Improved Comparator ADC for BS1
' Program: COMP_AD2.BAS
' (Binary-search ADC with the BS1 and a comparator)
' This program implements an ADC using a comparator.
' The unknown voltage goes to the comparator's + input and the
' Stamp's PWM output to the - input. The Stamp systematically
' searches for the unknown voltage by splitting the possible
' range of voltages in half, seeing whether the unknown is
' higher or lower, then splitting that range in half...
' This approach assures that the conversion is always
' finished in the shortest time possible. The conversion
' subroutine presented here runs at about 13 conversions/second.

SYMBOL refOut = 0 		' Comparator reference.
SYMBOL comp_p = pin1 		' Comparator output (pin).
SYMBOL ADCres = b2 		' Analog-to-digital result.
SYMBOL pwrTwo = b3 		' Power-of-2 to add to ADCres.

' Demonstration loop: take a conversion, display it, and loop.

again:
  gosub ADconvert 		' Perform conversion.
  debug ADCres 		' Display it.
goto again 			' Do it again.

' ADC conversion routine.

ADconvert:
  ADCres = 0: pwrTwo = 128

convLoop:
  ADCres = ADCres + pwrTwo 	' Add current power-of-2 to ADCres
  PWM refOut,ADCRes,1 		' ..and output that voltage via PWM.
  if comp_p = 1 then skip1 	' If unknown voltage is lower, then
  ADCRes = ADCres-pwrTwo 	' subtract power-of-2 from ADCres.

skip1:
  pwrTwo = pwrTwo/2 		' Try next lower power-of-2..
  if pwrTwo <> 0 then convLoop ' ..until power-of-2 = 0
return