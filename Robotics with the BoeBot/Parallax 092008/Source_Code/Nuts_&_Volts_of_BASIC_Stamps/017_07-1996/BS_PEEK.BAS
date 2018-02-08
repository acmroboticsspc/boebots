Program Listing 17.1. QBASIC Browser for BS1 Object Code
' QBASIC (PC) Program: BS_PEEK.BAS
' This program allows you to view, modify, save, and download Stamp I
' (BS1) object-code files created by the BSAVE directive. It provides
' examples of techniques for reading and writing BS1 object files.
' To use the downloading capability, you must have the BSLOAD utility
' on your computer and either in the current directory, or in your
' PATH statement. This program is not meant to be the ultimate in
' object-code editing--just a suitable starting point for more elaborate
' programs.
'======================SUBROUTINES=======================
DECLARE FUNCTION screenSays% (searchString AS STRING)
DECLARE SUB acceptEntry ()
DECLARE SUB goHome ()
DECLARE SUB goEnd ()
DECLARE SUB saveFile ()
DECLARE SUB rightKey ()
DECLARE SUB upKey ()
DECLARE SUB downKey ()
DECLARE SUB leftKey ()
DECLARE SUB makeBkgd ()
DECLARE SUB readFile ()
DECLARE SUB updateCell (cell AS INTEGER)
DECLARE FUNCTION HEXX$ (NUM AS INTEGER)
DEFINT A-Z
'======================CONSTANTS=======================
CONST black = 0: CONST blue = 1
CONST green = 2: CONST cyan = 3
CONST red = 4: CONST magenta = 5
CONST brown = 6: CONST white = 7
CONST foreground = white
CONST background = blue
CONST lockout = red
enter$ = CHR$(13)
'======================SCAN CODES=======================
leftArrow$ = CHR$(0) + CHR$(75)
rightArrow$ = CHR$(0) + CHR$(77)
upArrow$ = CHR$(0) + CHR$(72)
downArrow$ = CHR$(0) + CHR$(80)
home$ = CHR$(0) + CHR$(71)
end$ = CHR$(0) + CHR$(79)
altS$ = CHR$(0) + CHR$(31)
altR$ = CHR$(0) + CHR$(19)
esc$ = CHR$(27)
'======================GLOBAL VARIABLES=======================
DIM SHARED code(255) AS INTEGER
DIM SHARED attribs(255) AS INTEGER
DIM SHARED cursPos AS INTEGER
DIM SHARED filename AS STRING
'======================MAIN PROGRAM=======================
' Load the file specified by COMMAND$ (QuickBASIC compiler only)
' or get the user to enter a file path.
start:
CLS
LOCATE 5, 1: INPUT ; "Enter pathname of BS1 object file: ", filename
IF filename = "" THEN GOTO start
continue:
CLS
makeBkgd
readFile
' Assign attributes to each screen cell (byte of data) depending on
' whether it's program or data. The last program-memory address is
' stored in location 255.
FOR i = 0 TO 255
   IF i < code(255) THEN
     attribs(i) = 0
   ELSE
     attribs(i) = 2
   END IF
NEXT
' Display the data on the screen. Place the cursor at 0.
FOR i = 0 TO 255
  updateCell (i)
NEXT
cursPos = 0: attribs(cursPos) = 1
updateCell (cursPos)
' Scan for keys pressed by the user and respond accordingly.
scanit:
k$ = INKEY$
IF k$ = "" THEN GOTO scanit
SELECT CASE k$
  CASE leftArrow$
   leftKey
  CASE rightArrow$
   rightKey
  CASE upArrow$
   upKey
  CASE downArrow$
   downKey
  CASE home$
   goHome
  CASE end$
   goEnd
  CASE enter$
   acceptEntry
  CASE altS$
   saveFile
  CASE altR$
  CLS : theShell$ = "BSLOAD " + filename
  SHELL theShell$
   IF screenSays%("ERROR") THEN
   CLS : PRINT "Download failed!": PRINT
   PRINT "Check cable for correct connection. "
   PRINT "Ensure that power supply is connected."
   INPUT ; "Press ENTER to continue.", dummy$
  GOTO continue
  ELSE
   CLS : PRINT "Download successful."
   INPUT ; "Press ENTER to continue.", dummy$
   GOTO continue
  END IF
  CASE esc$
   END
END SELECT
GOTO scanit
SUB acceptEntry
' Let the user enter a new decimal value for cell that the
' cursor is now on. If it's in program memory, beep and
' ignore the entry.
IF cursPos >= code(255) THEN
  BEEP: EXIT SUB
ELSE
  LOCATE 22, 22: INPUT ; "new value (decimal): ", newVaL$
  code(cursPos) = (VAL(newVaL$) AND 255)
  updateCell (cursPos)
LOCATE 22, 22: PRINT SPC(58);
END IF
END SUB

SUB downKey
' Respond to down-arrow key by moving the cursor to the next row.
attribs(cursPos) = attribs(cursPos) - 1
updateCell (cursPos)
cursPos = (cursPos + 16) AND 255
  attribs(cursPos) = attribs(cursPos) + 1
updateCell (cursPos)
END SUB

SUB goEnd
' Respond to the END key by going to the last location
' in data memory.
attribs(cursPos) = attribs(cursPos) - 1
updateCell (cursPos)
cursPos = (code(255) - 1) AND 255
  attribs(cursPos) = attribs(cursPos) + 1
updateCell (cursPos)
END SUB

SUB goHome
' Respond to the HOME key by going to the 0th location.
attribs(cursPos) = attribs(cursPos) - 1
updateCell (cursPos)
cursPos = 0
  attribs(cursPos) = attribs(cursPos) + 1
updateCell (cursPos)
END SUB

FUNCTION HEXX$ (NUM AS INTEGER)
' Convert 8-bit integer to 2-digit hex values with leading 0s.
  IF NUM > 15 THEN
   HEXX$ = HEX$(NUM)
  ELSE
   HEXX$ = "0" + HEX$(NUM)
  END IF
END FUNCTION

DEFINT A-Z
SUB leftKey
' Respond to left arrow by moving back one cell.
  attribs(cursPos) = attribs(cursPos) - 1
updateCell (cursPos)
cursPos = (cursPos - 1) AND 255
attribs(cursPos) = attribs(cursPos) + 1
updateCell (cursPos)
END SUB

SUB makeBkgd
' Print instructions. Draw a color rectangle on the screen and add
' program title to it.
LOCATE 21, 22: PRINT "Address: Decimal: ASCII: "
LOCATE 23, 7
PRINT "ENTER to edit | ALT-S to save | ALT-R to download | ESC to quit";
FOR i = 2 TO 19
  COLOR foreground, background
  LOCATE i, 15
  PRINT SPC(49);
NEXT
  LOCATE 2, 29: PRINT "BS1 Code Viewer/Editor"
END SUB

SUB readFile
' Read in the 256-byte BS1 object code file and store
' it in the code(n) array.
OPEN filename FOR INPUT AS #1
FOR i = 255 TO 0 STEP -1
   code(i) = ASC(INPUT$(1, 1))
NEXT i
CLOSE #1
END SUB

SUB rightKey
' Respond to right arrow key by moving forward one data cell.
attribs(cursPos) = attribs(cursPos) - 1
updateCell (cursPos)
cursPos = (cursPos + 1) AND 255
  attribs(cursPos) = attribs(cursPos) + 1
updateCell (cursPos)
END SUB

SUB saveFile
' Save the code array to current filename.
LOCATE 22, 22: PRINT "saving: "; filename;
OPEN filename FOR OUTPUT AS #1
FOR i = 255 TO 0 STEP -1
  PRINT #1, CHR$(code(i));
NEXT I
CLOSE #1
LOCATE 22, 22: PRINT SPC(58);
END SUB

FUNCTION screenSays% (searchString AS STRING)
' Load the current contents of the 25x80 DOS screen into a
' string and search that string for "searchString." Return
' an integer representing the position of the string on the
' screen, or 0 if the string isn't found.
screenText$ = ""
  FOR i% = 1 TO 25
   FOR j% = 1 TO 80
    screenText$ = screenText$ + CHR$(SCREEN(i%, j%))
   NEXT
  NEXT
screenSays% = INSTR(screenText$, searchString)
END FUNCTION

SUB updateCell (cell AS INTEGER)
' Print the specified data cell to the screen.
' Use its attributes to determine color.
cellRow = 4 + (cell \ 16)
cellCol = 16 + (3 * (cell MOD 16))
LOCATE cellRow, cellCol
SELECT CASE attribs(cell)
  CASE 0
   COLOR foreground, background
  CASE 1
   COLOR background, foreground
  CASE 2
   COLOR foreground, lockout
  CASE 3
   COLOR lockout, foreground
END SELECT
PRINT HEXX$(code(cell));
LOCATE 21, 30: COLOR white, black: PRINT USING "###"; cell;
LOCATE 21, 43: PRINT USING "###"; code(cell);
LOCATE 21, 54: PRINT " ";
LOCATE 21, 54: PRINT CHR$(code(cell));
END SUB

SUB upKey
' Respond to the up arrow by moving up a row.
attribs(cursPos) = attribs(cursPos) - 1
updateCell (cursPos)
cursPos = (cursPos - 16) AND 255
  attribs(cursPos) = attribs(cursPos) + 1
updateCell (cursPos)
END SUB
