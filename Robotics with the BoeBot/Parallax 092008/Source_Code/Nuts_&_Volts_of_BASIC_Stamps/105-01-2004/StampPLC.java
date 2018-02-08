import stamp.core.*;

/**
 * Stamp PLC template
 *
 * @version 0.3 -- 11 NOV 2003
 * @author Jon Williams, Parallax (jwilliams@parallax.com)
 */

public class StampPLC {

  // ---------------------------------------------------------------------------
  // Stamp PLC constants
  // ---------------------------------------------------------------------------

  static final int CLOCK     = CPU.pin0;      // shared clock
  static final int LD_165    = CPU.pin1;      // 74HC165 shift/load
  static final int DI_165    = CPU.pin2;      // 74HC165 data in (from)
  static final int ADC_CS    = CPU.pin3;      // ADC chip select
  static final int ADC_DO    = CPU.pin4;      // ADC data out (to)
  static final int ADC_DI    = CPU.pin5;      // ADC data in (from)

  static final int DI9       = CPU.pin6;      // direct digital inputs
  static final int DI10      = CPU.pin7;

  static final int DO1       = CPU.pin8;      // direct digital outputs
  static final int DO2       = CPU.pin9;
  static final int DO3       = CPU.pin10;
  static final int DO4       = CPU.pin11;
  static final int DO5       = CPU.pin12;
  static final int DO6       = CPU.pin13;
  static final int DO7       = CPU.pin14;
  static final int DO8       = CPU.pin15;

  static final int DOUTS[]   = { DO1, DO2, DO3, DO4, DO5, DO6, DO7, DO8 };

  static final int DIN1      = 0;             // for readDigIn() method
  static final int DIN2      = 1;
  static final int DIN3      = 2;
  static final int DIN4      = 3;
  static final int DIN5      = 4;
  static final int DIN6      = 5;
  static final int DIN7      = 6;
  static final int DIN8      = 7;
  static final int DIN9      = 8;
  static final int DIN10     = 9;

  static final int DOUT1     = 0;             // for writeDigOut() method
  static final int DOUT2     = 1;
  static final int DOUT3     = 2;
  static final int DOUT4     = 3;
  static final int DOUT5     = 4;
  static final int DOUT6     = 5;
  static final int DOUT7     = 6;
  static final int DOUT8     = 7;

  static final boolean ON    = true;          // for IO methods
  static final boolean OFF   = false;

  static final int AIN1      = 0;             // for readAnalog() method
  static final int AIN2      = 1;
  static final int AIN3      = 2;
  static final int AIN4      = 3;

  static final int ADC_UP5   = 0;             // unipolar, 0-5 volts
  static final int ADC_BP5   = 1;             // bipolar, +/- 5 volts
  static final int ADC_BP10  = 2;             // bipolar, +/- 10 volts
  static final int ADC_UP10  = 3;             // unipolar, 0-10 volts
  static final int ADC_420   = 4;             // 4-20 mA input

  static final int ADC_CFG[] = { 0xF0, 0xE0, 0xD0, 0xC0,
                                 0xF4, 0xE4, 0xD4, 0xC4,
                                 0xF8, 0xE8, 0xD8, 0xC8,
                                 0xFC, 0xEC, 0xDC, 0xCC,
                                 0xF0, 0xE0, 0xD0, 0xC0 };

  // ---------------------------------------------------------------------------
  // IDE Terminal constants
  // ---------------------------------------------------------------------------

  final static char HOME      = 0x01;


  // ---------------------------------------------------------------------------
  // Stamp PLC methods
  // ---------------------------------------------------------------------------


  // Initialize PLC
  // -- setup internal hardware connections
  // -- force digital outputs off

  static void initPLC() {

    CPU.writePin(CLOCK, false);               // clock = output low
    CPU.writePin(LD_165, true);               // load = output high
    CPU.writePin(ADC_CS, true);               // adc select = output high
    CPU.writePin(ADC_DO, false);              // prep for high pulses
    CPU.writePin(CPU.PORTB, true);            // digital outputs off

  }


  // Read digital inputs
  // -- returns 10-bit value
  // -- "1" = input active

  static int readDigInputs() {

    int inBits;

    // read indirect inputs
    CPU.pulseOut(1, LD_165);
    inBits = CPU.shiftIn(DI_165, CLOCK, 8, CPU.PRE_CLOCK_MSB);

    // read direct (active-low) inputs
    if (CPU.readPin(DI9) == false) {
      inBits |= 0x100;
    }
    if (CPU.readPin(DI10) == false) {
      inBits |= 0x200;
    }

    return inBits;

  }


  // Read single digital input (0 - 9)
  // -- returns ON (true) if active

  static boolean readDigIn(int digIn) {

    int mask;
    boolean active;

    if (digIn <= DIN10) {
      mask = 0x001 << digIn;
      active = ((readDigInputs() & mask) == mask);
    }
    else {
      active = OFF;                           // digIn out of range
    }

    return active;

  }


  // Write new value to digital outputs
  // -- updates all digital outputs (Dout1 - Dout8)
  // -- Note: outputs are active-low

  static void writeDigOutputs(int newOuts) {

    CPU.writePort(CPU.PORTB, (byte)(newOuts ^ 0xFF));

  }


  // Write new status to single output (0 - 7)
  // -- pass ON in newState to activate output; OFF to deactivate
  // -- Note: outputs are active-low
  // -- returns false if digOut was out of range

  static boolean writeDigOut(int digOut, boolean newState) {

    boolean status;

    if (digOut <= DOUT8) {
      CPU.writePin(DOUTS[digOut], !newState);
      status = true;
    }
    else {
      status = false;                         // digOut out of range
    }

    return status;

  }


  // Read analog channel
  // -- pass channel and mode
  // -- returns raw ADC count

  static int readAnalog(int channel, int mode) {

    int config;
    int adcRaw = 0;

    config = ADC_CFG[mode * 4 + channel];
    CPU.writePin(ADC_CS, false);
    CPU.shiftOut(ADC_DO, CLOCK, 8, CPU.SHIFT_MSB, (config << 8));
    CPU.writePin(ADC_CS, true);
    CPU.delay(1);
    CPU.writePin(ADC_CS, false);
    adcRaw = CPU.shiftIn(ADC_DI, CLOCK, 12, CPU.PRE_CLOCK_MSB);
    CPU.writePin(ADC_CS, true);

    // adjust counts for input divider (protection) circuity
    // -- cal factor = (input on terminal) / (input to MAX1270)
    // -- 1.05243

    adcRaw = adcRaw + (adcRaw / 20) + (adcRaw / 412);
    // limit to 4095
    adcRaw = (adcRaw <= 4095) ? adcRaw : 4095;

    return adcRaw;

  }


  // Converts ADC counts to signed millivolts (based on mode)

  static int millivolts(int counts, int mode) {

    int c, mV = 0;

    c = counts;

    switch (mode) {

      case ADC_UP5:
        // x 1.2207
        mV = c + (c / 5) + (c / 50) + (c / 1429);
        break;

      case ADC_BP5:
        if (counts < 2048) {
          // x 2.4414
          mV = 2 * c + (2 * c / 5) + (c / 25) + (c / 714);
        }
        else {
          c = 4095 - c;
          mV = 0 - (2 * c + (2 * c / 5) + (c / 25) + (c / 714));
        }
        break;

      case ADC_UP10:
        // x 2.4414
        mV = 2 * c + (2 * c / 5) + (c / 25) + (c / 714);
        break;

      case ADC_BP10:
        if (counts < 2048) {
          // x 4.8828
          mV = 4 * c + (4 * c / 5) + (2 * c / 25) + (c / 357);
        }
        else {
          c = 4095 - c;
          mV = 0 - (4 * c + (4 * c / 5) + (2 * c / 25) + (c / 357));
        }
        break;

      case ADC_420:
        // returns 4000 - 20000
        // x 5.0875
        mV = 5 * c + (2 * c / 25) + (c / 133);
        break;
    }

    return mV;

  }


  // ---------------------------------------------------------------------------
  // Put your custom methods here
  // ---------------------------------------------------------------------------




  // ---------------------------------------------------------------------------
  // Stamp PLC program
  // ---------------------------------------------------------------------------

  public static void main() {

    int inputs;                               // input scan
    int outputs;                              // data for outputs
    int adc;                                  // adc count
    int mV;                                   // adc in millivolts

    StringBuffer msg = new StringBuffer(128); // buffer for messages
    Timer scanTimer = new Timer();            // timer for scans


    // program code

    initPLC();
    scanTimer.mark();                         // reset timer


    // ***************************************************************
    // *                                                             *
    // *   Replace the demo code below with your application code.   *
    // *                                                             *
    // ***************************************************************


    // main scan
    while (true) {

      if (scanTimer.timeout(100)) {           // scan every 100 ms
        scanTimer.mark();                     // reset timer

        msg.clear();
        msg.append(HOME);

        msg.append("Inputs = ");
        inputs = readDigInputs();             // collect ins
        writeDigOutputs(inputs);              // copy ins to outs

        // cause DOUT1 to follow DIN9
        writeDigOut(DOUT1, ((inputs & 0x100) == 0x100));
        // cause DOUT2 to follow DIN10
        writeDigOut(DOUT2, ((inputs & 0x200) == 0x200));

        // create binary image of digital inputs
        for (int mask = 0x200; mask > 0; mask >>= 1) {
          if ((mask & inputs) == 0)
            msg.append('0');
          else
            msg.append('1');
        }
        msg.append("\n\n");

        // read and display four single-ended analog channels
        for (int chan = AIN1; chan <= AIN4; chan++) {
          msg.append("Ain");
          msg.append(chan + 1);
          msg.append(" = ");
          adc = readAnalog(chan, ADC_UP5);
          mV = millivolts(adc, ADC_UP5);
          msg.append(mV / 1000);
          msg.append(".");
          msg.append(mV % 1000);
          msg.append("   \n");
        }
        System.out.print(msg.toString());
      }
    }
  }
}