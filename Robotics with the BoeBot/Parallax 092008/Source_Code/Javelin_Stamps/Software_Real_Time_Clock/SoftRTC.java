package stamp.peripheral.rtc;

import stamp.core.*;

/**
 * This class provides a simplified software real-time clock.  The update()
 * method must be called from the main program every 100 milliseconds.  In
 * a tyical application, implementation might look like this:
 *
 * <code>
 *   public static void main() {
 *
 *     SoftRTC sysClock = new SoftRTC();
 *     Timer tics = new Timer();
 *     tics.mark();
 *
 *     while (true) {
 *       if (tics.timeout(100)) {
 *         tics.mark();
 *         sysClock.update();
 *       }
 *       // ... other main loop code
 *     }
 *   }
 * </code>
 *
 * @author Jon Williams, Parallax
 * @version 1.1 September 22, 2002
 */
public class SoftRTC {

  private int hours;
  private int mins;
  private int secs;
  private int secs10;                           // 0.1 seconds
  private int wkDay;                            // weekday; 0 [Sun] - 6 [Sat]
  private int days;                             // elapsed days since reset

  private StringBuffer tmStr = new StringBuffer();
  private static String dayNames = "SUNMONTUEWEDTHUFRISAT";


  /**
   * Creates soft RTC and initializes all fields to zero.
   */
  public SoftRTC() {
    this.hours = 0;
    this.mins = 0;
    this.secs = 0;
    this.secs10 = 0;
    this.wkDay = 0;
    this.days = 0;
  }


  /**
   * Creates soft RTC and initializes fields to user spec.
   *
   * @param hours Hours (0 .. 23)
   * @param mins Minutes (0 .. 59)
   * @param secs Seconds (0 .. 59)
   * @param wkDay Week day (0 [Sun] .. 6 [Sat])
   */
  public SoftRTC(int hours, int mins, int secs, int wkDay) {
    this.hours = hours % 24;
    this.mins = mins % 60;
    this.secs = secs % 60;
    this.secs10 = 0;
    this.wkDay = wkDay % 7;
    this.days = 0;
  }


  /**
   * Creates soft RTC and initializes all fields to user spec.
   *
   * @param hours Hours (0 .. 23)
   * @param mins Minutes (0 .. 59)
   * @param secs Seconds (0 .. 59)
   * @param wkDay Week day (0 [Sun] .. 6 [Sat])
   * @param days Days
   */
  public SoftRTC(int hours, int mins, int secs, int wkDay, int days) {
    this.hours = hours % 24;
    this.mins = mins % 60;
    this.secs = secs % 60;
    this.secs10 = 0;
    this.wkDay = wkDay % 7;
    this.days = days;
  }


  /**
   * Updates soft RTC object.  This method must be called every
   * 100 milliseconds to maintain clock.
   */
  public void update() {
    secs10 = ++secs10 % 10;
    if (secs10 == 0) {
      secs = ++secs % 60;
      if (secs == 0) {
        mins = ++ mins % 60;
        if (mins == 0) {
          hours = ++hours % 24;
          if (hours == 0) {
            ++days;
            wkDay = ++wkDay % 7;
          } // hrs
        } // mins
      } // secs
    } // secs10
  }


  /**
   * Sets the clock-oriented elements of the soft RTC
   *
   * @param hours Hours (0 .. 23)
   * @param mins Minutes (0 .. 59)
   * @param secs Seconds (0 .. 59)
   * @param wkDay Week day (0 [Sun] .. 6 [Sat])
   */
  public void setClock(int hours, int mins, int secs, int wkDay) {
    this.hours = hours % 24;
    this.mins = mins % 60;
    this.secs = secs % 60;
    this.secs10 = 0;
    this.wkDay = wkDay;
  }


  /**
   * Sets the time-oriented elements of the soft RTC
   *
   * @param hours Hours (0 .. 23)
   * @param mins Minutes (0 .. 59)
   * @param secs Seconds (0 .. 59)
   */
  public void setTime(int hours, int mins, int secs) {
    this.hours = hours % 24;
    this.mins = mins % 60;
    this.secs = secs % 60;
    this.secs10 = 0;
    this.wkDay = 0;
  }


  /**
   * Sets the time-oriented elements of the soft RTC
   *
   * @param hours Hours (0 .. 23)
   * @param mins Minutes (0 .. 59)
   */
  public void setTime(int hours, int mins) {
    this.hours = hours % 24;
    this.mins = mins % 60;
    this.secs = 0;
    this.secs10 = 0;
    this.wkDay = 0;
  }


  /**
   * Sets the hours element of the soft RTC
   *
   * @param hours Hours (0 .. 23)
   */
  public void setHours(int hours) {
    this.hours = hours % 24;
  }


  /**
   * Sets the minutes element of the soft RTC
   *
   * @param mins Minutes (0 .. 59)
   */
  public void setMins(int mins) {
    this.mins = mins % 60;
  }


  /**
   * Sets the seconds element of the soft RTC
   *
   * @param secs Seconds (0 .. 59)
   */
  public void setSecs(int secs) {
    this.secs10 = 0;
    this.secs = secs % 60;
  }


  /**
   * Sets the week day element of the soft RTC
   *
   * @param wkDay Week day (0 [Sun] .. 6 [Sat])
   */
  public void setWkDay(int wkDay) {
    this.wkDay = wkDay;
  }


  /**
   * Sets the days counter element of the soft RTC
   *
   * @param days Day count
   */
  public void setDays(int days) {
    this.days = days;
  }


  /**
   * Returns the hours count
   *
   * @return Hours (0 .. 23)
   */
  public int hours() {
    return hours;
  }

  /**
   * Returns the hours count
   *
   * @return Hours (0 .. 23)
   */
  public int hours12() {

    int h;

    h = (hours == 0) ? 12 : hours % 12;

    return h;
  }



  /**
   * Returns the minutes count
   *
   * @return Minutes (0 .. 59)
   */
  public int mins() {
    return mins;
  }


  /**
   * Returns the seconds count
   *
   * @return Seconds (0 .. 59)
   */
  public int secs() {
    return secs;
  }


  /**
   * Returns the 0.1 seconds count
   *
   * @return Tenths of seconds (0 .. 9)
   */
  public int secs10() {
    return secs10;
  }


  /**
   * Returns the week day
   *
   * @return Week day (0 [Sun] .. 6 [Sat])
   */
  public int wkDay() {
    return wkDay;
  }


  /**
   * Returns a week day as a 3-character string
   *
   * @return Week day as string ("SUN" (0) ... "SAT" (6))
   */
  public StringBuffer wkDayStr() {
    tmStr.clear();
    for (int i = 0; i < 3; i++) {
      tmStr.append(dayNames.charAt(wkDay * 3 + i));
    }

    return tmStr;
  }


  /**
   * Returns the days counter
   *
   * @return Days
   */
  public int days() {
    return days;
  }


  /**
   * Returns the time in minutes past midnight
   *
   * @return Minutes past midnight (0 .. 1439)
   */
  public int rawTime() {
    return (hours * 60 + mins);
  }


  /**
   * Returns a time string in format hh:mm:ss.n
   *
   * @return Time string (hh:mm:ss.n)
   */
  public StringBuffer timeStr() {
    tmStr.clear();
    tmStr.append(hours / 10);
    tmStr.append(hours % 10);
    tmStr.append(":");
    tmStr.append(mins / 10);
    tmStr.append(mins % 10);
    tmStr.append(":");
    tmStr.append(secs / 10);
    tmStr.append(secs % 10);
    tmStr.append(".");
    tmStr.append(secs10);

    return tmStr;
  }


  /**
   * Returns a short time (minutes and seconds) in format mm:ss.n
   *
   * @return Time string (mm:ss.n)
   */
  public StringBuffer timeStrShort() {
    tmStr.clear();
    tmStr.append(mins / 10);
    tmStr.append(mins % 10);
    tmStr.append(":");
    tmStr.append(secs / 10);
    tmStr.append(secs % 10);
    tmStr.append(".");
    tmStr.append(secs10);

    return tmStr;
  }


  /**
   * Returns a clock string in 12-hour short format hh:mm xM
   *
   * @return Clock string (hh:mm xM)
   */
  public StringBuffer clockStr12() {

    int h;

    h = (hours == 0) ? 12 : hours % 12;

    tmStr.clear();
    tmStr.append(h / 10);
    tmStr.append(h % 10);
    tmStr.append(":");
    tmStr.append(mins / 10);
    tmStr.append(mins % 10);
    tmStr.append(" ");
    if (hours < 12) {
      tmStr.append("AM");
    }
    else {
      tmStr.append("PM");
    }

    return tmStr;
  }


  /**
   * Returns a clock string in short format ddd hh:mm xM
   *
   * @return Clock string (ddd hh:mm xM)
   */
  public StringBuffer clockStr12Long() {

    int h;

    h = (hours == 0) ? 12 : hours % 12;

    tmStr.clear();
    tmStr = wkDayStr();
    tmStr.append(" ");
    tmStr.append(h / 10);
    tmStr.append(h % 10);
    tmStr.append(":");
    tmStr.append(mins / 10);
    tmStr.append(mins % 10);
    tmStr.append(" ");
    if (hours < 12) {
      tmStr.append("AM");
    }
    else {
      tmStr.append("PM");
    }

    return tmStr;
  }


  /**
   * Returns a clock string in 24-hour short format hh:mm
   *
   * @return Clock string (hh:mm)
   */
  public StringBuffer clockStr24() {
    tmStr.clear();
    tmStr.append(hours / 10);
    tmStr.append(hours % 10);
    tmStr.append(":");
    tmStr.append(mins / 10);
    tmStr.append(mins % 10);

    return tmStr;
  }


  /**
   * Returns a clock string in short format ddd hh:mm xM
   *
   * @return Clock string (ddd hh:mm)
   */
  public StringBuffer clockStr24Long() {
    tmStr.clear();
    tmStr = wkDayStr();
    tmStr.append(" ");
    tmStr.append(hours / 10);
    tmStr.append(hours % 10);
    tmStr.append(":");
    tmStr.append(mins / 10);
    tmStr.append(mins % 10);

    return tmStr;
  }
}