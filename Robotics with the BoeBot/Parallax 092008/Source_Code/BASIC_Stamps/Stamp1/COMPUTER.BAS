
' This program makes the stereotype computer sound.
' Have a ~40 ohm speaker connected to pin 0 via a 10-100uf cap.
' The other side of the speaker should be grounded.

symbol rnd = w0
symbol note = b2
symbol spkr = 0

noise:	random rnd		'Generate next random in rnd

	note = rnd & $7F	'Get lower 7-bits of rnd into note (this
				'insures a tone and not a white note)

	sound spkr,(note,7)	'Output sound (note,duration) to spkr

	goto noise		'loop to noise to do again

' This program should run for three hours to achieve maximum effect.
