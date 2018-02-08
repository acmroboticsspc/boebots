'example of StampMEM data storage I/O
'setup for BS1 @2400 baud (must short solder jumper on StampMEM board)
'b1=0 or 1 for I/O, b2=high byte, b3=low byte, b4=number bytes
'2/20/99
 
b0=0					'2400 baud command
pause 500				'powerup delay
b1=0:b2=31:b3=24:b4=8			'indicate data parameters to output
serout 0,b0,(b1,b2,b3,b4)		'transmit data parameters
serout 0,b0,("PARALLAX")			'transmit write data 
b1=1					'indicate data parameters to input
pause 5					'allow time to write data and read data
serout 0,b0,(b1,b2,b3,b4)		'transmit data parameters
serin 0,b0,b5,b6,b7,b8,b9,b10,b11,b12	'receive read data
debug #@b5,#@b6,#@b7,#@b8,#@b9,#@b10,#@b11,#@b12,cr	'read data on PC screen

