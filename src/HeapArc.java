
import DataStructures.Comparable;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class HeapArc implements Comparable{
  public String key;
  public double value;
  public double cap=0;

  public HeapArc(String k,double v) {
    key = k;
    value = v;
  }

  public int compareTo( Comparable rhs )
  {
      if( value < ((HeapArc)rhs).value ) return -1;
      if( value > ((HeapArc)rhs).value ) return  1;
      return 0;
  }
}
