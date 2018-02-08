' Listing 3: CAP.BAS
' The BASIC Stamp estimates the value of a capacitor by the time required 
' for it to discharge through a known resistance. 
	input 7
	output 0
Cap:	pulsin 7,1,w1
	if w1 = 0 then Cap	' If no pulse, try again. 
	if w1 > 6553 then Err	' Avoid overflows. 
	let w1 = w1*10
	let w1 = w1/14		' Apply calibration value. 
	if w1 > 999 then uF	' Use uF for larger caps. 
	serout 0,N2400,(#w1," nF",10,13) 
	goto Cap

uF:	let b4 = w1/1000	' Value left of decimal point. 
	let b6 = w1//1000	' Value right of decimal point. 
	serout 0,N2400,(#b4,".",#b6," uF",10,13)
	goto Cap

Err:	serout 0,N2400,("out of range",10,13)
	goto Cap

	
