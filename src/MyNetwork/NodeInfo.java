package MyNetwork;
import GenericNetwork.BasicNodeInfo;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class NodeInfo extends BasicNodeInfo {

  public double supply=0;
  public double demand=0;
  public double shortage_cost = 0;
  public double overage_cost =0;
  public NodeInfo(String nodeKey,String netKey){
    super(nodeKey,netKey);

  }
}
