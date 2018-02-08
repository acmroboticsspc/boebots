package stamp.peripheral.devantech;

import stamp.core.*;

/**
 * This class provides an interface to the Devantech SRF04 ultrasonic
 * range finder module.
  * <p>
 * <i>Usage:</i><br>
 * <code>
 *   SRF04 range = new SRF04(CPU.pin0, CPU.pin1); // trigger on P0; ehco on P1
 * </code>
 * <p>
 * Detailed documentation for the SRF04 can be found at: <br>
 * http://www.robot-electronics.co.uk
 * <p>
 * The SRF04 can be purchased from Acroname at: <br>
 * http://www.acroname.com/robotics/parts/R93-SRF04.html
 *
 * @author Jon Williams, Parallax Inc. (jwilliams@parallaxinc.com)
 * @version 1.0 21 July 2002
 */
public final class SRF04 {

  private int triggerPin, echoPin;

  /**
   * Creates SRF04 range finder object
   *
   * @param triggerPin SRF04 trigger input (from Javelin)
   * @param echoPin SFR04 echo output (to Javelin)
   */
  public SRF04 (int triggerPin, int echoPin) {
    this.triggerPin = triggerPin;
    this.echoPin = echoPin;
    CPU.writePin(triggerPin, false);      // setup for high-going pulse
    CPU.setInput(echoPin);                // ensure echo pin is an input
  }


  /**
   * Returns raw distance value from SRF04.  Sensor is sampled five times and
   * the average is returned if in range, otherwise zero is returned to
   * indicate out-of-range reading.
   *
   * @return Raw distance value from SRF04
   */
  public int getRaw() {
    int echoVal = 0;
    int echoRaw = 0;

    // collect five samples
    for (int i = 0; i < 5; i++) {
      // trigger ~17 uS (10 uS minimum required by SRF04)
      CPU.pulseOut(2, triggerPin);
      // capture input; timepout ~37 mS
      echoVal += CPU.pulseIn(0x10A6, echoPin, true);
      // 10 mS delay between readings -- required by SRF04
      CPU.delay(100);
    }

    // average readings
    echoRaw = echoVal / 5;

    // return average if in range; zero if out-of-range
    return (echoRaw < 2074) ? echoRaw : 0;
  }


  /*
   * The SRF04 returns a pulse width of 73.746 uS per inch.  Since the
   * Javelin pulsIn() round-trip echo time is in 8.68 uS units, this is the
   * same as a one-way trip in 4.34 uS units.  Dividing 73.746 by 4.34 we
   * get a time-per-inch conversion factor of 16.9922.
   *
   * Values to derive convesion factors are selected to prevent roll-over
   * past 15-bit positive values of Javelin integers.
   */

  /**
   * @return SRF04 distance value in inches
   */
  public int getIn() {
    return (getRaw() / 17);
  }


  /**
   * @return SRF04 distance value in tenths of inches
   */
  public int getIn10() {
    return (getRaw() * 15 / 25);  // raw / 1.6667
  }


  /*
   * The SRF04 returns a pulse width of 29.033 uS per centimeter.  Since the
   * Javelin pulsIn() round-trip echo time is in 8.68 uS units, this is the
   * same as a one-way trip in 4.34 uS units.  Dividing 29.033 by 4.34 we
   * get a time-per-centimeter conversion factor of 6.6896.
   *
   * Values to derive convesion factors are selected to prevent roll-over
   * past 15-bit positive values of Javelin integers.
   */

  /**
   * @return SRF04 distance value in centimeters
   */
  public int getCm() {
    return (getRaw() * 15 / 100); // raw / 6.6667
  }


  /**
   * @return SRF04 distance value in millimeters
   */
  public int getMm() {
    return (getRaw() * 15 / 10);  // raw / 0.6667
  }
}