' Program Listing 20.2. QBASIC Debug Viewer (for PCs running DOS)
' DECLARE SUB showDebug ()
' Program: DBUG_IN.BAS (QBASIC program to work with BS1 debug output)
' This program demonstrates how to capture and interpret the serial
' data output by the BS1's debug instruction _without_ use of the
' STAMP.EXE software. This capability can be very handy when you
' need one more output from the Stamp, and only require one-way
' communication with the PC (Stamp -> PC), as for data acquisition.
' The center pin of the Stamp's 3-pin programming header must be
' connected to the serial data in of COM1 through a CMOS inverter
' or RS-232 line driver, as shown in the accompanying article.

' See the text and table 1 for the debug protocol.

DIM SHARED i AS INTEGER
DIM SHARED item AS INTEGER
DIM SHARED row AS INTEGER
DIM SHARED debugData$
' Open communications through com1 serial port. Set up the following
' parameters: 4800 baud, no parity, 8 data bits, 1 stop bit.
' Disable handshaking by setting the timeout values for all of the
' handshake inputs to zero; carrier detect (CD), clear to send (CS),
' data set ready (DS). In addition, disable the timeout for OPENing
' the port itself with OP0. Finally, set the port for INPUT access
' and assign it a 1024-byte receive buffer.

CLOSE ' In case port is left open from previous run.
' Open the com port for input at 4800 baud with 4096-byte buffer.
OPEN "com1:4800,N,8,1,CD0,CS0,DS0,OP0" FOR INPUT AS #1 LEN = 4096

' Now print the labels to the screen.
CLS : PRINT " =====BS1 DEBUG VIEWER====="
LOCATE 4, 25: PRINT "ASC"
LOCATE 4, 35: PRINT "DEC"
LOCATE 4, 45: PRINT "HEX"
LOCATE 5, 10: PRINT "INs:"
LOCATE 6, 10: PRINT "OUTs:"
LOCATE 7, 10: PRINT "Dirs:"
FOR i = 0 TO 13
  LOCATE (i + 8), 10: PRINT "B"; LTRIM$(RTRIM$(STR$(i))); ": "
NEXT
' Collect the debug data in a string variable. The loop below
' synchronizes on the 64 $F0 characters (240 decimal) sent by
' the Stamp at the beginning of a debug.
start:
i = 0
DO WHILE i < 64
again:
  IF LOF(1) = 0 THEN GOTO again
 IF NOT EOF(1) THEN debugData$ = INPUT$(1, #1) ELSE GOTO again
  IF debugData$ = CHR$(240) THEN i = i + 1 ELSE i = 0
LOOP

' After the 64 sync characters, this instruction grabs the
' next 33 bytes that make up the debug output. Of these,
' only 17 hold useful information, but this is the
' easiest way to collect the data.
hold:
IF LOF(1) > 33 THEN debugData$ = INPUT$(33, #1) ELSE GOTO hold

' Show the INs register, which is located 10 items below the
' other registers in the string.
i = 0: row = 5: item = 8: showDebug

' Now show the other registers, OUTs, Dirs, and B0 through B13.
row = 6: item = 18
FOR i = 0 TO 15
  showDebug
NEXT

GOTO start ' Repeat until CTL-Break
SUB showDebug
  LOCATE (row + i), 26: PRINT MID$(debugData$, (item + i), 1); " ";
  LOCATE (row + i), 34: PRINT ASC(MID$(debugData$, (item + i), 1)); " ";
  LOCATE (row + i), 46: PRINT HEX$(ASC(MID$(debugData$, (item + i), 1))); "
";
END SUB
