package GenericNetwork;


import Util.*;

import java.io.*;

/**
 * The BasicArcInfo class defines the simplest arc class assigned with
 * the minimum attributes and served as a data class.
 * Other arc classes contain with additional attributes or data should be
 * extended from this.
 * @version			2.1
 * @author			Atom Yuen
 */
public class BasicArcInfo implements Serializable, KeyedObject
{
	/** @serial */
	private String			key;
	/** @serial */
	private String			netKey;
	/** @serial */
	private String			fromNodeKey;
	/** @serial */
	private String			toNodeKey;

	/**
	 * Constructs the arc object with the arcKey, netKey, fromNode and toNode
	 * @param k			the key of this arc
	 * @param net		the key of the net this arc belongs to
	 * @param fromNode	the key of the node this arc going out from
	 * @param toNode	the key of the node this arc entering into
	 */
	public BasicArcInfo(String k, String net, String fromNode, String toNode)
	{
		key = k;
		netKey = net;
		fromNodeKey = fromNode;
		toNodeKey = toNode;
	}

	/**
	 * Returns the key of this arc.
	 * @return		the key of this arc as String
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * Returns the key of the net this arc belongs to.
	 * @return		the key of the net as String
	 */
	public String getNetKey()
	{
		return netKey;
	}

	/**
	 * Returns the key of the node this arc going out from
	 * @return		the key of the node as String
	 */
	public String getFromNodeKey()
	{
		return fromNodeKey;
	}

	/**
	 * Returns the key of the node this arc entering into
	 * @return		the key of the node as String
	 */
	public String getToNodeKey()
	{
		return toNodeKey;
	}
}