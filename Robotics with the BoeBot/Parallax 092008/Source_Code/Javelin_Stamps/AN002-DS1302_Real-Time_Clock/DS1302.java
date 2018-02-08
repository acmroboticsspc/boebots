/*
 * Copyright © 2002 Parallax Inc. All rights reserved.
 */

package stamp.peripheral.rtc;
import stamp.core.*;

/**
 * This class encapsulates the capabilities of the Dallas DS1302 3-wire
 * real-time clock including the RAM and Super Cap charging.
 * <p>
 * This class only needs a small main class. For example add the constructor
 * and the following lines of code:<br>
 * <code><pre>
 * DS1302 t = new DS1302(CPU.pin1,CPU.pin2,CPU.pin3);  // your pins may vary
 * t.updateTimeDate();
 * System.out.print("Time: ");
 * System.out.println(t.printTime(true));
 * System.out.print("Date: ");
 * System.out.println(t.printDate());
 * </code></pre>
 * Memory overhead with the small example above you should see at least 25K free
 * <p>
 * Day of week is represented by 1-7 (Sun-Sat). Zero is not supported by the
 * DS1302. The time set method will change a 0 for the day of week to a 1
 * instead of throwing an error.
 * <p>
 * The constructor does not reset the time/date if invalid from power failure.
 * <p>
 * Use of RAM: The RAM is very useful to store rapidly changing information
 * like seconds or voltage that would otherwise wear out EEPROM either external
 * or inside the Javelin chip. The RAM routines are set up so the RAM address
 * wraps around and therefore any address can be used.  Forgiving but still only
 * 31 bytes in size.
 * <p>
 * Backup power is through pin 8 of the DS1302. By using the <code>charge(true);</code>
 * function a cap can be used instead of a battery. This feature is valuable for
 * reliability, power failure monitoring or logging in remote locations.
 * <p>
 * The clock routines whenever called check the timer t1. If t1 has elapsed, a
 * time request to the DS1302 chip is made otherwise the time values from hour,
 * minute, second... are used. This makes processing faster and keeps the clock
 * and data lines free for any other chips on the "bus".  The timer is adjusted
 * via: <code>UPDATE_PERIOD</code>.
 * <p>
 * Future revisions will be made by creating a super class that will abstract
 * individual chips such as DS1307, DS1302 or the Pocket Watch B. So the
 * same code could work with different hardware.
 *
 * <p><pre>
 * Revision History:
 * 07/29/02: Class originally submitted to Parallax Inc.
 *           by customer Tim Constable of Boston, MA
 *           Original Version 0.81
 * 08/22/02: Class modified, enhanced and tested by Steve Dill of Parallax Inc.
 *           Version 1.0 of DS1302 class approved.
 * </pre><p>
 * @author Parallax Inc.
 * @version 1.0 August 22, 2002
 */
public class DS1302  {

  Timer t1 = new Timer();

  // numeric (bin) time values updated internally. Very easy to
  // read [class name].hour  or [class name].minute
  /**
  * Numerical time value for hour.
  * Range: 0-23, 0 hour = midnight
  */
  static int hour;

  /**
  * Numerical time value for minute.
  * Range: 0-59.
  */
  static int minute;

  /**
  * Numerical time value for second.
  * Range: 0-59.
  */
  static int second;

  /**
  * Numerical date value for date of month.
  * Range: 1-31
  */
  static int date;

  /**
  * Numerical date value for month.
  * Range: 1-12
  */
  static int month;

  /**
  * Numerical date value for day of the week (Sun, Mon, Tues..).
  * Range: 1-7 (do not use 0-6)
  *        Sun = 1, Mon = 2, Tue = 3, Wed = 4, Thu = 5, Fri = 6, Sat = 7
  */
  static int dayOfWeek;

  /**
  * Numerical date value for year.
  * Range: 0-99
  */
  static int year;


  /**
   * Initialize DS1302 chip for r/w (write protect register)
   * clock is not checked for valid time
   * @parm dataPin, clockPin, enablePin
   * @return Nothing
   * @exception If clock chip does not exist
   */
  public DS1302(int data, int clock, int enable){
    dataPin   = data;
    clockPin  = clock;
    enablePin = enable;

    // initialize pins -- make low for high-going pulses
    CPU.writePin(enablePin,false);                   // init the bus
    CPU.writePin(clockPin, false);
    CPU.delay(SETTLE_TIME);                          // settle down
    CPU.writePin(enablePin,true);
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,CLEAR_PROTECT);
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,0);// control byte
    CPU.writePin(enablePin,false);
    try {
      int tmp = readRam(1);                          // test location 0
    }
    catch (Exception e) {
      System.out.println("DS1302 Read Exception - Check wires");
    }
  }//end constructor: DS1302


    /**
     * Takes an integer and formats it to two byte string with a leading zero
     *
     * @parm num
     * @return two character string
     *
    */
  public String numToString2(int num) {
    buf3.clear();
    if (num < 10) buf3.append("0");
    buf3.append(num);
    return (buf3.toString());                   // "0" + String.valueOf(num)
  }//end method: numToString2


  /**
   * Charge the Super Cap to allow backup power.
   * Charge at least an hour to fully charge the Super Cap.
   * This feature is very useful for keeping a valid time without external
   * power.  When using batteries instead of a Super Cap DO NOT set charge(true);
   * this feature is not ment to charge batteries.
   *
   * @parm boolean
   * @return Nothing
   *
   */
  public void charge(boolean data ){
    CPU.writePin(enablePin,true);
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,0x90);
    if (data) CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,0xA5);
    else CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,0xF0);
    CPU.writePin(enablePin,false);
    CPU.delay(SETTLE_TIME);                          // chip settle time
  }//end method: charge


  /**
   * Save one byte to RAM in the DS1302 chip.
   * Allows all integers to map to 31 bytes
   *
   * @parm location (full int range), Data to be stored
   * @return Nothing
   *
   */
  public void writeRam(int location, int data ){
    location = Math.abs(location);      // remove high order bit
    location %= 31;                     // wrap RAM address
    location = (location<<1) | 0xc0;    // shifts 1 bit and adds C0 to location
    CPU.writePin(enablePin,true);
    // tell the DS1302 chip the "location"
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,location);
    // tell the DS1302 chip the data you want to save.
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,data);
    CPU.writePin(enablePin,false);
    CPU.delay(SETTLE_TIME);             // chip settle time
  }//end method: writeRam


  /**
   * Get one RAM register from the DS1302 chip.
   * Allows all integers to map to 31 bytes.
   * This method will autowrap RAM values above 31 bytes.<br>
   * Example: RAM location 32 = Ram location 0
   *
   * @parm Location
   * @return Ram data
   *
   */
  public int readRam(int location){
    location = Math.abs(location);      // remove high order bit
    location %= 31;                     // wrap RAM address
    location = (location<<1) | 0xc1;    // shifts 1 bit and adds C1 to location
    CPU.writePin(enablePin,true);       // enable chip
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,location);
    // 8 bits are received then shifted right 8 bits
    // then the 8 high order bits are be cleared by & 0x00FF
    // then assigned to a 16 bit value
    clockIn = (CPU.shiftIn(dataPin,clockPin,8,CPU.PRE_CLOCK_LSB) >> 8) & 0x00FF;
    CPU.writePin(enablePin,false);      // disable chip
    CPU.delay(SETTLE_TIME);             // chip settle time
    return clockIn;                     // return RAM register
  }//end method: readRam


  /**
   * Read a specific DS1302 chip register contents in the RAW, unformatted form.
   * See DS1302 docs for more detail.
   * getRawTime will not generally be used except to further explore how the
   * DS1302 works.<p>
   * <pre>
   * Sample DS1302 chip Commands:<code>
   * READ_YEAR   = 0x8d;
   * READ_MONTH  = 0x89;
   * READ_DAY    = 0x8b;
   * READ_DATE   = 0x87;
   * READ_HOUR   = 0x85;
   * READ_MINUTE = 0x83;
   * READ_SECOND = 0x81;</code></pre>
   *
   * @parm DS1302 chip command
   * @return DS1302 chip register contents
   *
   */
  public int readRawTD(int comand){
    CPU.writePin(enablePin,true);                    // turn on access to chip
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,comand);
    // 8 bits are received then shifted right 8 bits
    // then the 8 high order bits are be cleared by & 0x00FF
    // then assigned to a 16 bit value
    clockIn = (CPU.shiftIn(dataPin,clockPin,8,CPU.PRE_CLOCK_LSB) >> 8) & 0x00FF;
    CPU.writePin(enablePin,false);                   // turn off access to chip
    // all variables must be converted from BCD to binary
    clockIn = (((clockIn & 0xf0)>>4)*10)+(clockIn & 0x0f);
    CPU.delay(SETTLE_TIME);                          // chip settle time
    return clockIn;
  }//end method: readRawTD


  /**
   * Read a DS1302 chips' time/date register, for sake of speed, this data is
   * unformatted.  The RAW data's format follows the DS1302 data sheet.<p><pre><code>
   *   array[0] = second;      // 0-59
   *   array[1] = minute;      // 0-59
   *   array[2] = hour;        // 0-23
   *   array[3] = date;        // of Month 1-31
   *   array[4] = month;       // 1-12
   *   array[5] = dayOfWeek;   // 1-7 (Sun-Sat)
   *   array[6] = year;        // 0-99</code></pre>
   *
   * @parm Index of time array 0-7
   * @return Contents of time array
   *
   */
  public int readTD(int index){
    updateTimeDate();                       // update internal class variables
    return byteArray[index];                // return requested value
  }//end method: getTD


  /**
   * Read all time/date variables from DS1302 chip.<p><pre><code>
   *   0 = hour;        // 0-23
   *   1 = minute;      // 0-59
   *   2 = second;      // 0-59
   *   3 = month;       // 1-12
   *   4 = date;        // of Month 1-31
   *   5 = year;        // 0-99
   *   6 = dayOfWeek;   // 1-7 (Sun-Sat)</code></pre>
   *
   * @return array size 7
   *
   */
  public int[] readTimeDate(){
    int array[] = new int[7];
    updateTimeDate();                              // update Time/Date variables
    array[0] = hour;
    array[1] = minute;
    array[2] = second;
    array[3] = month;
    array[4] = date;
    array[5] = year;
    array[6] = dayOfWeek;
    return array;                                  // return array
  }//end method: readTimeDate


    /**
     * Forces the public time variables to be updated.
     * This method is also called from within the DS1302 class
     *
     * @parm Nothing
     * @return Nothing
     *
    */
  public void updateTimeDate(){
    if (t1.timeout(UPDATE_PERIOD)) {

      CPU.writePin(enablePin,true);                             // enable chip
      // transfer 7 bytes of data all at once (bulk transfer)
      CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,BULK_READ); // bulk transfer
      // read and convert each of the 7 bytes as they come in off the DS1302 chip
      for (int i=0; i<=7; i++) {
        // 8 bits are received then shifted right 8 bits
        // then the 8 high order bits are be cleared by & 0x00FF
        // then assigned to a int array[i]
        byteArray[i] = (CPU.shiftIn(dataPin,clockPin,8,CPU.PRE_CLOCK_LSB) >> 8) & 0x00FF;
        if (i==0) byteArray[0]&=~0x80;  // If clock is halted, filter halt bit
        // all variables must be converted from BCD to binary
        byteArray[i] = (((byteArray[i] & 0xf0)>>4)*10)+(byteArray[i] & 0x0f);
      }//end for
      CPU.writePin(enablePin,false);
      t1.mark();
    }//end if

    second    = byteArray[0];     // 0-59
    minute    = byteArray[1];     // 0-59
    hour      = byteArray[2];     // 0-23
    date      = byteArray[3];     // 1-31
    month     = byteArray[4];     // 1-12
    dayOfWeek = byteArray[5];     // 1-7 = Sun-Sat
    year      = byteArray[6];     // 0-99
                                  // control byte, byteArray[7], not used
  }//end method: updateTimeDate


  /**
   * Format the time (12hr/24hr).  Useful for a preformatted time stamp.
   *
   * @parm TRUE for 12hr (am/pm) format
   * @parm FALSE for 24 hour (military) format
   * @return Time String
   *
   */
  public String readTime(boolean ap) {
    boolean pmFlag = false;                   // create flag for pm in 12hr mode
    updateTimeDate();                         // update internal class variables
    buf10.clear();                            // clear buffer
    if (ap && (hour > 12)) {                  // if 12hr clock and time > 12
      pmFlag = true;                          // set flag to pm
      hour -= 12;                             // adjust time from 24hr clock
    }//end if
    if (ap && hour == 0) hour = 12;           // adjust for 12am

   // create string
   buf10.append(numToString2(hour));
   buf10.append(":");
   buf10.append(numToString2(minute));
   buf10.append(":");
   buf10.append(numToString2(second));

   // add am/pm character to return string if displaying 12hr time
   if (ap) {
     if (pmFlag) buf10.append("p");
     else buf10.append("a");
   }//end if

   return (buf10.toString());
  }//end method: readTime


  /**
   * Format the date to US style, useful for date stamp on screen
   *
   * @parm Nothing
   * @return Date String
   *
   */
  public String readDate() {
   updateTimeDate();                        // update internal class variables
   buf10.clear();
   buf10.append(numToString2(month));
   buf10.append("/");
   buf10.append(numToString2(date));
   buf10.append("/");
   buf10.append(numToString2(year));
   return (buf10.toString());               // return formatted string
  }//end method: readDate


  /**
   * Returns the day of the week.
   *   TRUE  - returns long format
   *   FALSE - returns short format
   *
   * @parm true for long version, false for short 3 character name
   * @return string
   *
   */
  public String readDay(boolean b) {
    updateTimeDate();                       // update internal class variables
    if (b) return (DAY_OF_WEEK_LONG[dayOfWeek]);      // return long format
    else return (DAY_OF_WEEK_SHORT[dayOfWeek]);       // return short format
  }//end method: readDay


  /**
   * Set all the time variables in the DS1302 chip.<p><pre><code>
   * hour      - hr (0-23)
   * minute    - min (0-59)
   * seconds   - sec (0-59)
   * month     - mo (1-12)
   * date      - date (1-31)
   * year      - yr (0-99)
   * dayOfWeek - dayOfWeek (1-7), Sun=1 - Sat=7</code></pre><p>
   *
   * Force any Zero values for dayOfWeek, date, and month(mo) to a '1'
   * Values exceeding maximums will be changed to minimum values<br>
   * Example: if sec > 60 then sec=0.
   * @parm sec, min, hr, date, mo, dayOfWeek, yr
   * @return Nothing
   *
   */
  public void writeTime(int hr, int min, int sec, int mo, int date, int yr, int dayOfWeek){
    // Force any Zero values for DayOfWeek, date, and Month to a 1
    if (mo == 0) {mo = 1;}
    if (date == 0) {date = 1;}
    if (dayOfWeek == 0) {dayOfWeek = 1;}
    // Time setting routines for auto rollover (no carry)
    if (sec > 59) {sec = 0;}
    if (min > 59) {min = 0;}
    if (hr > 23) {hr = 0;}
    if (date > 31) {date = 1;}
    if (mo > 12) {mo = 1;}
    if (dayOfWeek > 7) {dayOfWeek = 1;}
    if (yr > 99) {yr = 0;}

    CPU.writePin(enablePin,true);                                 // enable chip
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,BULK_WRITE);    // load command
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,btoBCD(sec));   // seconds
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,btoBCD(min));   // min
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,btoBCD(hr));    // hour
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,btoBCD(date));  // date of Month
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,btoBCD(mo));    // month
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,btoBCD(dayOfWeek));// day
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,btoBCD(yr));    // year
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,0);             // control !!
    CPU.writePin(enablePin,false);                                // disable chip
    CPU.delay(UPDATE_PERIOD * 10);                                // wait for update
  }//end method: writeTime


  /**
   * Halts the dsa1302 chip
   * @parm True to halt clock, False to resume.
   * @return nothing
   *
   */
  public void halt(boolean b) {
    updateTimeDate();                                      // Update variables
    int sec=second;
    sec=btoBCD(sec);                                       // Convert to BCD
    if (b) sec |= 0x80;                                    // Set halt bit
    else sec &= ~0x80;                                     // Clear halt bit

    CPU.writePin(enablePin,true);                          // enable chip
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,128);    // Send command
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,sec);    // Send halt bit
    CPU.writePin(enablePin,false);                         // disable chip
  }//end method: halt


  /**
   * Protects the ds1302 chip from accidential writes
   * @param TRUE turns on protection (cannot write)
   * @param FALSE turns off protection (allow writes)
   * @return nothing
   *
   */
  public void protect(boolean b) {
    CPU.writePin(enablePin,true);                       // enable chip
    CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,0x8E);// Send command
    if (b) CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,0x80);// Set writeProtect bit
    else CPU.shiftOut(dataPin,clockPin,8,CPU.SHIFT_LSB,0);     // Set writeProtect bit
    CPU.writePin(enablePin,false);                      // disable chip
  }//end method: protect


//============================================================================
// Private methods and fields below this point.
//============================================================================

  private int dataPin;                      // DS1302 pin used for data
  private int clockPin;                     // DS1302 pin used for clocking
  private int enablePin;                    // DS1302 pin used to enable chip
  private int clockIn;                      // storage for clocked data
  private final static int BULK_READ     = 0xbf;  // DS1302 chip command
  private final static int BULK_WRITE    = 0xbe;  // DS1302 chip command
  private final static int CLEAR_PROTECT = 0x8e;  // DS1302 chip command
  private final static int SETTLE_TIME   = 100;   // delay time, can be tweaked

  private int[] byteArray = new int[9];         // transfer Time/Date array
  private static StringBuffer buf3  = new StringBuffer(3);   // formatted output
  private static StringBuffer buf10 = new StringBuffer(10);  // formatted output

  // Day of week should always be 1-7. Zero is not supported by the DS1302 chip
  private final static String[] DAY_OF_WEEK_LONG = {"BAD", "Sunday", "Monday",
    "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
  private final static String[] DAY_OF_WEEK_SHORT = {"BAD", "Sun", "Mon", "Tue",
    "Wed", "Thr", "Fri", "Sat"};

  // Considerations on setting the UPDATE_PERIOD:
  // A smaller number will update the time variables faster.  Updating
  // frequently will slow overall processing when making frequent requests.
  // A value between 100 - 500 is best to keep seconds running smooth.
  // A higher value will keep the Clock/Data buss free of traffic but also
  // make updating seconds occur much slower.
  private final static int UPDATE_PERIOD = 100;


  // Private binary to BCD used to load data into the DS1302 chips' time registers
  private int btoBCD(int data) {
    data = ((data / 10)<<4) + (data % 10);           // Convert binary to BCD
    return data;
  }//end method: btoBCD

}//end class: DS1302