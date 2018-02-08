' PROGRAM: Sonar.bas
' The Stamp runs a sonar transceiver to measure distances up to 10 feet. 

Symbol	echo_time = w2		' Variable to hold echo delay

setup:	let pins = 0		' All pins low
	output 0		' Controls sonar xmitter
	input 1			' Listens to sonar receiver

ping:	pulsout 0,50		' Send a 1-ms ping
	pulsin 1,1,echo_time	' Listen for return
	debug echo_time		' Display time measurement
	pause 500		' Wait 1/2 second
	goto ping		' Do it again. 	