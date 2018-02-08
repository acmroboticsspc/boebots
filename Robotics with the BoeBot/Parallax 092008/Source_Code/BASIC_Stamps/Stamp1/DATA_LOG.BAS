' Program DATA_LOG.BAS
' This is a QBASIC program to display and record the data
' from the Stamp program SOLAR.BAS. To quit this program,
' either press control-break, or press any key and wait
' for the next Stamp transmission.

DEFINT A-Z
OPEN "com1:1200,N,8,1,CD0,CS0,DS0,OP0" FOR INPUT AS #1
OPEN "c:\data.log" FOR OUTPUT AS #2
CLS
Again:
        temp$ = INPUT$(1, 1)
        PRINT ASC(temp$); CHR$(9); TIME$
        PRINT #2, ASC(temp$); CHR$(9); TIME$
IF INKEY$ = "" THEN GOTO Again
CLOSE
END

