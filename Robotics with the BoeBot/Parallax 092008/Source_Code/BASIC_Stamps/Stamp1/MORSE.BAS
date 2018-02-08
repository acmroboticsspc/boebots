Symbol	Tone = 100
Symbol	Quiet = 0
Symbol	Dit_length = 7
Symbol	Dah_length = 21
Symbol	Wrd_length = 42
Symbol	Character = b0
Symbol	Index1 = b6
Symbol	Index2 = b2
Symbol	Elements = b4


Identify:
output  0: output 1
for Index1 = 0 to 7
	lookup Index1,(100,66,67,66,68,68,66,148),Character
	gosub Morse
next
sleep 60
goto Identify

Morse:
let Elements = Character & %00000111
if Elements = 7 then Adjust1
if Elements = 6 then Adjust2
Bang_Key: 
for Index2 = 1 to Elements
	if Character >= 128 then Dah
	goto Dit
   Reenter:
	let Character = Character * 2
next
gosub char_sp
return

Adjust1:
Elements = 6
goto Bang_Key

Adjust2:
Character = Character & %11111011
goto Bang_Key
end

Dit:
high 0
sound 1,(Tone,Dit_length)
low 0
sound 1,(Quiet,Dit_length)
goto Reenter

Dah:
high 0
sound 1,(Tone,Dah_length)
low 0
sound 1,(Quiet,Dit_length)
goto Reenter

Char_sp:
sound 1,(Quiet,Dah_length)
return

Word_sp:
sound 1,(Quiet,Wrd_length)
return

