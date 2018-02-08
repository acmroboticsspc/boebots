package stamp.peripheral.io;

import stamp.core.*;

/**
 * This class encapsulates the basic operations of a standard LED.
 *
 * @author Jon Williams, Parallax Inc.
 * @version 1.0 03 April 2002
 */
public class LED {

  public static final boolean LED_OFF = false;
  public static final boolean LED_ON  = true;

  private int ioPin;
  private boolean onState;
  private boolean ledState;

  /**
   * Creates an LED object.
   *
   * @param ioPin LED control pin
   * @param onState Output state of control pin to light LED
   */
  public LED (int ioPin, boolean onState) {
    this.ioPin = ioPin;
    this.onState = onState;
    off();
  }

  /**
   * Extinguishes the LED
   */
  public void off() {
    CPU.readPin(ioPin);
    ledState = LED_OFF;
  }

  /**
   * Lights the LED
   */
  public void on() {
    CPU.writePin(ioPin, onState);
    ledState = LED_ON;
  }

  /**
   * Controls LED with external variable/condition
   *
   * @param ledState New state of LED (false or true)
   */
  public void putState(boolean ledState) {
    if (ledState)
      on();
    else
      off();
  }

  /**
   * Inverts state of LED
   */
  public void invert() {
    putState(!ledState);
  }

  /**
   * Returns LED status
   *
   * return LED status (LED_OFF, LED_ON)
   */
  public boolean getState() {
    return ledState;
  }
}