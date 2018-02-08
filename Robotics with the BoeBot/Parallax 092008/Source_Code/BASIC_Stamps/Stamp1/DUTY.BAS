' Listing 2: DUTY.BAS
' The BASIC Stamp calculates the duty cycle of a repetitive pulse train. 
' Pulses in on pin 7; data out via 2400-baud serial on pin 0. 
	input 7
	output 0
Duty:	pulsin 7,1,w2		' Take positive pulse sample. 
	if w2 > 6553 then Error	' Avoid overflow when w2 is multiplied x10.
	pulsin 7,0,w3		' Take negative pulse sample. 
	let w3 = w2+w3
	let w3 = w3/10		' Distribute multiplication by 10 into two 
	let w2 = w2*10		' parts to avoid an overflow. 
	let w2 = w2/w3		' Calculate percentage. 
	serout 0,N2400,(#w2," percent",10,13)
	pause 1000		' Update once a second. 
	goto Duty

' Handle overflows by skipping calculations and telling the user. 
Error:	serout 0,N2400,("Out of range",10,13)	 
	pause 1000
	goto Duty
	
