' Listing 4: VCO.BAS
' The BASIC Stamp uses input from the VCO of a 4046 phase-locked loop 
' as a logarithmic A-to-D converter. Input on pin 7; 2400-baud serial 
' output on pin 0. 
	input 7
	output 0
VCO:	pulsin 7,1,w2	' Put the width of pulse on pin 7 into w2. 
	let w2 = w2-45	' Allow a near-zero min value without underflow. 
	serout 0,N2400,(#w2,10,13)	 
	pause 1000	' Wait 1 second between measurements. 
	goto VCO
	
