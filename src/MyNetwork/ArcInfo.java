package MyNetwork;
import GenericNetwork.BasicArcInfo;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ArcInfo extends BasicArcInfo{
  public double cost = 0;
  public double cap_low = 0;
  public double cap_up = 0;
  public double cap_rest = 0;
  public double flow =0;
  public double pre_flow =0;
  public ArcInfo(String arcKey,String netKey, String fromNodeKey,String toNodeKey){
      super(arcKey,netKey,fromNodeKey,toNodeKey);

  }


}
