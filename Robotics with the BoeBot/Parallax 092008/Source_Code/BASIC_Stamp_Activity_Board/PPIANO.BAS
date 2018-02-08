'************************************************************************
'*  Basic Stamp Activity Board Sample Program             Player Piano  *
'*  20 July, 1998                                               (BS-1)  *
'*                                                                      *
'*  This program demonstrates how to make a one note player piano with  *
'*  the Basic Stamp Activity Board. The X6 jumper should be connected.  *
'*  Hit the P5/P10 button to play the selected song. Hit the P6/P9      *
'*  button to select the previous song. Hit the P7/P8 button to select  *
'*  the next song. Hit 'Alt-R' to download this program and prepare to  *
'*  be amused.                                                          *
'************************************************************************

symbol  R	=	0
symbol	C	=	82
symbol  Db	=	85
symbol	D	=	87
symbol  Eb	=	89
symbol  E	=	92
symbol  F	=	94
symbol  Gb	=	95
symbol  G	=	97
symbol	Ab1	=	99
symbol  A1	=	73
symbol  Bb1	=	76
symbol  BE1	=	79
symbol	C1	=	82
symbol  Db1	=	85
symbol	D1	=	87
symbol  Eb1	=	89
symbol  E1	=	92
symbol  F1	=	94
symbol  Gb1	=	95
symbol  G1	=	97
symbol	Ab2	=	99
symbol	A2	=	101
symbol	Bb2	=	102
symbol	BE2	=	104
symbol	C2	=	105
symbol	Db2	=	106
symbol	D2	=	108
symbol	E2	=	110
symbol	F2	=	111
symbol	Gb2	=	112
symbol	G2	=	113
symbol	Bb3	=	115
symbol	Bm3	=	116
symbol	C3	=	117
symbol	D3	=	118
symbol	MaxTune	= 3
symbol	tune	= b0

INIT:	let tune = 0
DELAY:	pause 200
START:	if pin5 = 0 then PLAY
	if pin6 = 0 then PREV
	if pin7 = 0 then NXXS
	goto START
NXXS:	if tune > MaxTune then START
	let tune = tune + 1:
	goto DELAY
PREV:	if tune = 0 then START
	let tune = tune - 1
	goto DELAY
PLAY:	if tune = 4 then Song5		'Heart and Soul
	if tune = 3 then Song4		'Charge!
	if tune = 2 then Song3		'Shave and a Haircut
	if tune = 1 then Song2		'Death march
Song1:	sound 4,(G,80,D2,80,C2,20,BE2,20,A2,20,G2,80,D2,80,C2,20,BE2,20,A2,20,G2,80,D2,80,C2,20,BE2,20,C2,20,A2,80)
	goto START
Song2:	sound 4,(F,80,R,2,F,70,R,2,F,10,R,2,F,80,Ab1,60,R,2,G,10,R,2,G,60,R,5,F,10,R,2,F,50,R,2,E,20,R,1,F,40)
	goto START
Song3:	sound 4,(F2,40,R,2,C2,20,R,2,C2,20,R,5,D2,50,R,3,C2,30,R,40,E2,40,F2,50)
	goto START
Song4:	sound 4,(Db2,20,Gb2,20,Bb3,15,C3,30,R,5,Bb3,20,C3,75)
	goto START
Song5:	sound 4,(C2,30,R,10,C2,30,R,10,C2,80,R,3,C2,20,BE2,30,A2,20,BE2,30,C2,20,D2,30,R,5,C2,10,E2,30,R,15,E2,30,R,15,E2,80)
	goto START
