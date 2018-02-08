'Program places a PC text file into the STAMPMEM board
'BASIC Stamp2 must be programmed with stpmempc.bs2 file
'9/26/98
    DIM A$(20)
    CLEAR : CLS
    PRINT "Text File Name: ";
    LINE INPUT T$
    OPEN "COM1:9600,N,8,1,RS,CS,DS,CD" FOR RANDOM AS #1
    OPEN T$ FOR INPUT AS #2
    n = 1: k = 1
'determine loop time for data output
    t1 = TIMER
    FOR x = 1 TO 10000: NEXT x
    t2 = TIMER
    x = INT((10000 / (t2 - t1)) * .05)    'allow 50ms between data output
'send start command to stamp
    A$ = "START"
    FOR m = 1 TO 5: x$ = MID$(A$, m, 1): PRINT #1, x$; : NEXT m
10  IF EOF(2) THEN 50
    x$ = INPUT$(1, #2)
    PRINT x$;
    PRINT #1, x$;
    IF n = 16 THEN 20
    n = n + 1
    k = k + 1
    IF k >= 65536 THEN 60
    GOTO 10
20  n = 1
    FOR m = 1 TO x: NEXT m   'delay so data maybe placed in eeprom
    GOTO 10
50  CLOSE
    PRINT "END OF FILE........"
    END
60  CLOSE
    PRINT "FILLED UP EEPROM MEMORY"
    END

