' Program THERM.BAS
' This program reads a thermistor with the BASIC pot command, 
' computes the temperature using a power-series polynomial equation, 
' and reports the result to a host PC via the Stamp cable 
' using the debug command. 

' Symbol constants represent factored portions of the coefficients 
' C0, C1, and C2. "Top" and "btm" refer to the values' positions in
' the fractions; on top as a multiplier or on the bottom as a divisor. 
Symbol	co0	=       162
Symbol  co1top	=       255
Symbol  co1btm	=       2125
Symbol  co2bt1	=       25
Symbol  co2top	=       3
Symbol  co2btm	=       50

Check_temp:
	pot 0,46,w0	' 46 is the scale factor. 
	let w1 = w0*w0/co2bt1*co2top/co2btm
	let w0 = w0*co1top/co1btm+w0
	let w0 = co0+w1-w0
	debug w0
	pause 1000	' Wait 1 second for next temperature reading. 
goto Check_temp	
end
	
