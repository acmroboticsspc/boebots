/*
 * Copyright © 2002 Parallax Inc., all rights reserved.
 */

package stamp.math;

/** Companion methods for the FloatLite6 library which will find the
  * minimum, maximum & average of FloatLite6 objects, including FloatLite6 Arrays.
  *
  * @author Gray Cole
  * @version 1.0 March 20, 2003
  */
public class FloatLite6Math
  {

  /** Calculate the minimum value from an array of FloatLite6 values.
    *
    * @param list array of FloatLite6 objects
    * @return FloatLite6 object with the minimum value
    */
  public static FloatLite6 min(FloatLite6[] list)
   {
   answer.setValue(list[0]);
   for (int i=1; i<list.length; i++)
     if (answer.compare(list[i]) > 0)
       answer.setValue(list[i]);

   return answer;
   }


  /** Calculate the minimum value from an array of FloatLite6 values.
    *
    * @param list array of FloatLite6 objects
    * @param minval FloatLite6 object to hold the minimum value
    */
  public static void min(FloatLite6[] list, FloatLite6 minval)
   {
   for (int i=1; i<list.length; i++)
     if (minval.compare(list[i]) > 0)
       minval.setValue(list[i]);
   }


  /** Calculate the maximum value from an array of FloatLite6 values.
    *
    * @param list array of FloatLite6 objects
    * @return FloatLite6 object with the maximum value
    */
  public static FloatLite6 max(FloatLite6[] list)
   {
   answer.setValue(list[0]);
   for (int i=1; i<list.length; i++)
     if (answer.compare(list[i]) < 0)
       answer.setValue(list[i]);

   return answer;
   }


  /** Calculate the maximum value from an array of FloatLite6 values
    * returning result in given object.
    *
    * @param list array of FloatLite6 objects
    * @param maxval FloatLite6 object where result is placed
    */
  public static void max(FloatLite6[] list, FloatLite6 maxval)
   {
   for (int i=1; i<list.length; i++)
     if (maxval.compare(list[i]) < 0)
       maxval.setValue(list[i]);
   }


  /** Calculate the average value of an array of fixed point numbers.
    *
    * @param list array of FloatLite6 values
    * @return FloatLite6 object with the average value
    */
  public static FloatLite6 average(FloatLite6[] list)
   {
   int count = list.length;
   int remains, placehldr;
   char numhold;
   answer.setValue(list[0]);
   for (int i=1; i<list.length; i++)
     answer.add(list[i]);

   placehldr = answer.getInteger();
   answer.setInteger(placehldr / count);
   remains = placehldr % count;
   for (int i = 0; i < 3; i++)
     {
     // get the char value
     numhold =  answer.getNumerator(i);
     placehldr = remains * 100;
     placehldr += (int) numhold;
     numhold = (char) (placehldr / count);
     remains = placehldr % count;
     answer.setNumerator(i,numhold);
    }
   return answer;
   }//end method: average


  /** Calculate the average value of an array of fixed point numbers.
    *
    * @param list array of FloatLite6 values
    * @param avgval FloatLite6 object with the average value
    */
  public static void average(FloatLite6[] list, FloatLite6 avgval)
    {
    int count = list.length;
    int remains, placehldr;
    char numhold;
    avgval.zero();
    for (int i=0; i<list.length; i++)
      avgval.add(list[i]);

    placehldr = avgval.getInteger();
    avgval.setInteger(placehldr / count);
    remains = placehldr % count;
    for (int i = 0; i < 3; i++)
      {
      // get the char value
      numhold =  avgval.getNumerator(i);
      placehldr = remains * 100;
      placehldr += (int) numhold;
      numhold = (char) (placehldr / count);
      remains = placehldr % count;
      avgval.setNumerator(i,numhold);
      }
    }


  /** Add an integer value to a passed FloatLite6 Object.
    * @param FloatLite6 object to hold result
    * @param integer to be added
    * @return FloatLite6 object with the result
    */
  public static FloatLite6 addInt(FloatLite6 orig, int val)
    {
    answer.setValue(val);
    orig.add(answer);
    return orig;
    }


  /** Add a value in a String object to a passed FloatLite6 Object.
    * @param FloatLite6 object to hold result
    * @param String object holding the value to be added
    * @return FloatLite6 object with the result
    */
  public static FloatLite6 addString(FloatLite6 orig, String val)
    {
    answer.setValue(val);
    orig.add(answer);
    return orig;
    }


  /** Subtract an integer value to a passed FloatLite6 Object.
    * @param FloatLite6 object to hold result
    * @param integer to be subtracted
    * @return FloatLite6 object with the result
    */
  public static FloatLite6 subInt(FloatLite6 orig, int val)
    {
    answer.setValue(val);
    orig.subtract(answer);
    return orig;
    }


  /** Subtract a value in a String object to a passed FloatLite6 Object.
    * @param FloatLite6 object to hold result
    * @param String object holding the value to be subtracted
    * @return FloatLite6 object with the result
    */
  public static FloatLite6 subString(FloatLite6 orig, String val)
    {
    answer.setValue(val);
    orig.subtract(answer);
    return orig;
    }

    private static FloatLite6 answer = new FloatLite6("0");
  }//end class: FL6Math