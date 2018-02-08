package stamp.peripheral;

import stamp.core.*;

/**
 * This class provides an interface to standard analog joystick -- the kind
 * used for PC games that plugs into a DB-15 connector (game port).
 * <p>
 * The joystick potentiometers are read using CPU.rcTime().  This code is
 * written for the rcTime() capacitor to be connected to Vdd; the joystick
 * pots connect between the pin/cap connection and Vss (ground).
 * <p>
 * <i>Usage:</i><br>
 * <code>
 *   joystick controller = new joystick(CPU.pin0, CPU.pin1, CPU.pin1, CPU.pin2);
 * </code>
 * <p>
 *
 * @author Jon Williams, Parallax Inc. (jwilliams@parallaxinc.com)
 * @version 1.0 22 July 2002
 */
public class joystick {

  public static final int BTN_X  = 0x01;  // x button pressed
  public static final int BTN_Y  = 0x02;  // y button pressed
  public static final int BTN_XY = BTN_X + BTN_Y;

  private int xPotPin, yPotPin, xSwitchPin, ySwitchPin;

  private int xMult = 1;                  // multiplier for scaling raw value
  private int xDiv = 1;                   // divisor for scaling raw value
  private int yMult = 1;
  private int yDiv = 1;


  /**
   * Creates analog joystick object.
   *
   * @param xPotPin X pot input pin (DB-15 pin 3)
   * @param yPotPin y pot input pin (DB-15 pin 6)
   * @param xSwitchPin X switch input pin (DB-15 pin 2)
   * @param ySwitchPin Y switch input pin (DB-15 pin 7)
   */
  public joystick (int xPotPin, int yPotPin, int xSwitchPin, int ySwitchPin) {
    this.xPotPin = xPotPin;
    this.yPotPin = yPotPin;
    this.xSwitchPin = xSwitchPin;
    this.ySwitchPin = ySwitchPin;
    // make switch pins inputs
    CPU.setInput(xSwitchPin);
    CPU.setInput(ySwitchPin);
  }


  /**
   * Reads raw X value from joystick.
   *
   * @return X potentiometer reading.
   */
  public int rawX() {
    // discharge cap
    CPU.writePin(xPotPin, true);
    CPU.delay(10);
    // read pot
    // -- timeout ~20 mS (200K * 0.1uF = 1TC)
    // -- cap *should* charge to 63% in 1TC
    // -- threshold is 50%
    return CPU.rcTime(2304, xPotPin, false);
  }


  /**
   * Reads raw Y value from joystick.
   *
   * @return Y potentiometer reading.
   */
  public int rawY() {
    // discharge cap
    CPU.writePin(yPotPin, true);
    CPU.delay(10);
    // read pot
    // -- timeout ~20 mS (200K * 0.1uF = 1TC)
    // -- cap *should* charge to 63% in 1TC
    // -- threshold is 50%
    return CPU.rcTime(2304, yPotPin, false);
  }


  /**
   * Sets multiplier for scaled X pot value <br>
   *  -- scaleX = rawX * multiplierX / divisorX
   *
   * @param multiplier Scale multiplier
   */
  public void setXMult(int multiplier) {
    this.xMult = multiplier;
  }


  /**
   * Sets divisor for scaled X pot value <br>
   *  -- scaleX = rawX * multiplierX / divisorX
   *
   * @param divisor Scale divisor
   */
  public void setXDiv(int divisor) {
    this.xDiv = divisor;
  }


  /**
   * Sets multiplier for scaled Y pot value <br>
   *  -- scaleY = rawY * multiplierY / divisorY
   *
   * @param multiplier Scale multiplier
   */
  public void setYMult(int multiplier) {
    this.yMult = multiplier;
  }


  /**
   * Sets divisor for scaled Y pot value <br>
   *  -- scaleY = rawY * multiplierY / divisorY
   *
   * @param divisor Scale divisor
   */
  public void setYDiv(int divisor) {
    this.yDiv = divisor;
  }


  /**
   * Sets multiplier and divisor for scaled X pot value <br>
   *  -- scaleX = rawX * multiplierX / divisorX
   *
   * @param multiplier Scale multiplier
   * @param divisor Scale divisor
   */
  public void setXScale(int multiplier, int divisor) {
    this.xMult = multiplier;
    this.xDiv = divisor;
  }


  /**
   * Sets multiplier and divisor for scaled X pot value <br>
   *  -- scaleY = rawY * multiplierY / divisorY
   *
   * @param multiplier Scale multiplier
   * @param divisor Scale divisor
   */
  public void setYScale(int multiplier, int divisor) {
    this.yMult = multiplier;
    this.yDiv = divisor;
  }


  /**
   * Reads and scales X value from joystick.
   *
   * @return Scaled X potentiometer reading.
   */
  public int scaleX() {
    int xVal;

    xVal = rawX();
    // return scaled value if valid; otherwise -1
    return (xVal >= 0) ? (xVal * xMult / xDiv) : xVal;
  }


  /**
   * Reads and scales Y value from joystick.
   *
   * @return Scaled Y potentiometer reading.
   */
  public int scaleY() {
    int yVal;

    yVal = rawY();
    // return scaled value if valid; otherwise -1
    return (yVal >= 0) ? (yVal * yMult / yDiv) : yVal;
  }

  /**
   * Reads X button on joystick.
   *
   * @return True if X button is pressed
   */
  public boolean buttonX() {
    // return inverted input [for active-low]
    return !CPU.readPin(xSwitchPin);
  }


  /**
   * Reads Y button on joystick.
   *
   * @return True if Y button is pressed
   */
  public boolean buttonY() {
    // return inverted input [for active-low]
    return !CPU.readPin(ySwitchPin);
  }


  /**
   * Reads X and Y buttons on joystick.
   *
   * @return Value of buttons pressed (BTN_X, BTN_Y, BTN_XY)
   */
  public int buttons() {
    int btnsVal = 0;

    if (buttonX()) btnsVal += BTN_X;
    if (buttonY()) btnsVal += BTN_Y;

    return btnsVal;
  }
}