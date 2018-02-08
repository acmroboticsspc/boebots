symbol track1_speed=b2				'set up variable names
symbol track1_accum=b1

symbol track2_speed=b3
symbol track2_accum=b7

symbol track3_speed=b4
symbol track3_accum=b6

symbol current_track = b5

symbol track_btn = b8
symbol up_btn = b9
symbol down_btn = b10

pause 2000					'wait for lcd to wake up

serout 6,n2400,(254,1,254," ")			'clear lcd

dirs = %00111000				'make track driver pins
						'outputs; all others are
						'inputs

reset:	w1 = 0: w2 = 0				'set track speeds and
						'current track # to 0

goto update_lcd					'update lcd

main_loop:

	if pin7 = 0 then reset			'reset everything if
						'reset button is pressed

	gosub run_trains			'update track pwm

track:	button 0,0,30,6,track_btn,0,down0	'read track select button

	current_track = current_track + 1	'increment current track #
	if current_track <> 3 then go_lcd
	current_track = 0			'reset if over 2
go_lcd:	goto update_lcd				'update lcd

down0:	button 1,0,30,1,down_btn,0,up0		'read down button

	if current_track <> 0 then down1	'check current track #
 	if track1_speed = 0 then up0
	track1_speed = track1_speed - 1		'reduce track 1 speed
 	goto update_lcd				'update lcd

down1:	if current_track <> 1 then down2	'check current track #
	if track2_speed = 0 then up0
	track2_speed = track2_speed - 1		'reduce track 2 speed
	goto update_lcd				'update lcd

down2:	if track3_speed = 0 then up0
	track3_speed = track3_speed - 1		'reduce track 3 speed
	goto update_lcd				'update lcd

up0:	button 2,0,30,1,up_btn,0,main_loop	'read up button

	if current_track <> 0 then up1		'check current track #
	if track1_speed = 99 then main_loop
	track1_speed = track1_speed + 1		'increase track 1 speed
	goto update_lcd				'update lcd

up1:	if current_track <> 1 then up2		'check current track #
	if track2_speed = 99 then main_loop
	track2_speed = track2_speed + 1		'increase track 2 speed
	goto update_lcd				'update lcd

up2:	if track3_speed = 99 then main_loop
	track3_speed = track3_speed + 1		'increase track 3 speed

update_lcd:

	serout 6,n2400,(254,130,254," ")	'move cursor and print " "
	if track1_speed > 9 then abc		'test for 1 or 2 digits
	serout 6,n2400,("0")			'print leading zero
abc:	serout 6,n2400,(#track1_speed)		'print track 1 speed

	gosub run_trains			'update track pwm

	serout 6,n2400,(254,134,254," ")	'move cursor and print " "
	if track2_speed > 9 then abc2		'test for 1 or 2 digits
	serout 6,n2400,("0")			'print leading zero
abc2:	serout 6,n2400,(#track2_speed)		'print track 2 speed

	gosub run_trains			'update track pwm

	serout 6,n2400,(254,138,254," ")	'move cursor and print " "
	if track3_speed > 9 then abc3		'test for 1 or 2 digits
	serout 6,n2400,("0")			'print leading zero
abc3:	serout 6,n2400,(#track3_speed)		'print track 3 speed

	gosub run_trains			'update track pwm

done:	b0 = current_track * 4 + 130		'print arrow pointing to
	serout 6,n2400,(254,b0,254,">")		'currently selected track

goto main_loop

run_trains:
 
	track1_accum = track1_accum + track1_speed	'update track 1 pwm
	b0 = track1_accum
	pin3 = bit7					'drive track 1
	track1_accum = track1_accum & %01111111

	track2_accum = track2_accum + track2_speed	'update track 2 pwm
	b0 = track2_accum
	pin4 = bit7					'drive track 2
	track2_accum = track2_accum & %01111111

	track3_accum = track3_accum + track3_speed	'update track 3 pwm
	b0 = track3_accum
	pin5 = bit7					'drive track 3
	track3_accum = track3_accum & %01111111
 
	return
