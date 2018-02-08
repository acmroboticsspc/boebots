Welcome to the SX-Key 48/52 Interface.

This file contains last-minute information about the SX-Key Interface.

    Contents:

    WHERE TO FIND INFORMATION
    SYSTEM REQUIREMENTS
    INSTALLATION
    KNOWN PROBLEMS
    WHAT'S NEW



WHERE TO FIND INFORMATION

Please read the KNOWN PROBLEMS section of this document.

Please visit the Parallax web site for revised documentation
and software:  http://www.parallaxinc.com



SYSTEM REQUIREMENTS

Windows 95, Windows 98 or Windows NT 4.0 (S.P. 3 recommended)
80486 (or greater) processor
16 MB RAM (24 MB Recommended)
1 MB Free Hard Drive Space
256 Color VGA Video Card (24-bit SVGA recommended)
1 Available COM port



INSTALLATION

The SX-Key Interface is available as a self extracting executable or
as a single, non-archive, executable.  Simply copy the executable to
the directory you'd like to install it in.  When it is run, ff it is
the self extrancting archive, it will prompt for a directory to install
into (defaulting to the current directory).  After extraction, run the
SXKey52.exe program.



KNOWN PROBLEMS

Bank 0 is not visible in the DEBUG windows due to lack of indirect
addressing mode for this bank.  This is intended to be fixed in a
future version.

Editor window contains no scroll bars.  This is intended to be fixed
in a future version.


WHAT'S NEW
----------
Version 1.20

Removed a testing message that appeared when selecting the Clock or
Debug features.  (This would either prevent communication with the 
SX-Key or would only allow a specific frequency to be generated with
the SX-Key's PLL circuit).


Version 1.19

Supports the SX48/52 chips with date codes of AB0004AA or later.  Do
not use this software for chips produced before this date.  It is not
recommended to continue development with the SX48/52 engineering
samples (those dated before the indicated code).
 

Added ability to disable osc2 drive output as well as internal 10 Mohm
feedback resister.  This can be done via the Device window as well as
with the DRIVEOFF and FEEDBACKOFF directives (or XTLBUFD and IFBD
respectively).  This ability allows user to set proper oscillator gain
at the same time as disabling osc2 output and feedback for oscillator
input mode (for example, when using a clock-oscillator pack connected
only to OSC1).  The following line demonstrates this:

	DEVICE  OSCHS3, DRIVEOFF, FEEDBACKOFF

would set the oscillator gain for a 50MHz+ signal (OSCHS3) and disable
the OSC2 output as well as the feedback resistor (DRIVEOFF and 
FEEDBACKOFF, respectively).  A function generator (or clock-oscillator
pack) signal set to 50 MHz and connected to OSC1 would now properly
drive the chip.  NOTE:  Turning off OSC2 drive and the feedback resistor
is only an option for the seven oscillator gain settings (OSCLP1 through
OSCHS3) and is not available otherwise.  Manually change the settings in
the Device window to see which modes allow modification of the drive and
feedback fuses (they will be greyed out if not available).


Added command-line switch to specify default COM port.
Example:

	C:\SXKey\SXKey52.exe  /2

would start software and set the COM port in the Configuration window
to COM2 (serial port 2).


Fixed Run->Program option to turn off SX-Key generated clock signal if
it was currently running.


Version 1.18 or earlier
Previous releases for engineering samples of SX48/52 chips.  It is not
recommended to continue development with this version of software or 
the engineering samples of these chips.


