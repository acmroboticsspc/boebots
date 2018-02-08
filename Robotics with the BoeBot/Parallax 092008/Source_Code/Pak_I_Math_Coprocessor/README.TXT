README for the PAK-I V1.0
by AWC
=============================
The files on this disk are:

FUTILITY.BS2 - Basic Stamp Library for the PAK-I
FCONVERT.EXE - Program to convert floating point number formats
COMCTL32.DLL - You may or may not need this file to run FCONVERT
COMDLG32.DLL - You may or may not need this file to run FCONVERT
MSVCRT40.DLL - You may or may not need this file to run FCONVERT

Copy FCONVERT to your hard drive and try executing it. If it won't run,
copy the DLL files to the same directory or your Windows System directory
and try again.

The FUTILITY.BS2 file contains an example of printing a table of square roots while
blinking an LED connected to the PAK-I. The table takes a while to generate since
the program has to compute the square root, compute the error, and print two floating
point numbers on each pass (a total of 1000 passes).

