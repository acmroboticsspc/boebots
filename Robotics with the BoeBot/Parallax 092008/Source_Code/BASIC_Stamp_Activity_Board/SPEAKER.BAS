'************************************************************************
'*  Basic Stamp Activity Board Sample Program                  Speaker  *
'*  9 September, 1997                                           (BS-1)  *
'*                                                                      *
'*  This program demonstrates the use of the on-board speaker. All      *
'*  you need to do is hit 'Alt-R' to compile and download.  Simply      *
'*  change the position of the potentiometer to change the pitch of     *
'*  the speaker. You may modify the duration of the sound in the        *
'*  'sound' command to affect the characteristics of the sound.         *
'*  Try replacing the sound command with the pwm command if you are     *
'*  the curious type!                                                   *
'************************************************************************

START:
	pot 5,20,b0
	debug b0
	sound 4,(b0,10)
	goto START
