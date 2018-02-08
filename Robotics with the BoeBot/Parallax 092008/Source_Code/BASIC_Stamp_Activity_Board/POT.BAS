'************************************************************************
'*  Basic Stamp Activity Board Sample Program            Potentiometer  *
'*  9 September, 1997                                           (BS-1)  *
'*                                                                      *
'*  This program demonstrates the use of the on-board potentiometer.    *
'*  Simply place the 'X2' jumper to the '1' position. Hit 'Alt-R to     *
'*  compile and download. By changing in the potentiometer position     *
'*  you change its resistence. The Stamp will detect the change in      *
'*  resistence and display the result.                                  *
'************************************************************************

START:
	pot 5,20,b0
	debug b0
	goto START
