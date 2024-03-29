package SnNetwork;

import GenericNetwork.*;

/**
 * A class including the arc information of the network.
 * @author HKUST Transportation & Logistics Lab
 * @version SnNetwork1.0
 */

public class SnArcInfo extends BasicArcInfo
{
	/**Cost of this arc*/
	public double	cost;
	/**Upper bound of this arc*/
    public int		uBound;
    /**Flow on this arc*/
	public int		flow;
	/**Flags for various usages*/
	public int		flags;

      public int cumulatedFlow;

      public double w_ij;

      public int bounds[];

      public double reduceCosts[];

      public double c_bar =0; // used for loopless shortest paths algorithm

      public boolean flag; // indicate


	/**
	 * Creates an arc.
	 * The cost, upper bound and flow of the arc are set to zero.
	 * @param key       the name of the arc
	 * @param net       the name of the network containing the arc
	 * @param fromNode  the name of the node from which the arc starts
	 * @param toNode    the name of the node to which the arc ends
	 */

	public SnArcInfo(String key, String net, String fromNode, String toNode)
	{
		super(key, net, fromNode, toNode);
		cost = 0;
		uBound = 0;
		flow= 0;
		flags = 0;
                w_ij = 0;
                cumulatedFlow = 0;
	}
}