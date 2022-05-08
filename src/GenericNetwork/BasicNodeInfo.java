package GenericNetwork;


import Util.*;

import java.io.*;

/**
 * The BasicNodeInfo class defines the simplest node class assigned with
 * the minimum attributes and served as a data class.
 * Other node classes contain with additional attributes or data should be
 * extended from this.
 * @version			2.1
 * @author			Atom Yuen
 */
public class BasicNodeInfo implements Serializable, KeyedObject
{
	/** @serial */
	private String			key;
	/** @serial */
	private String			netKey;

	/**
	 * Constructs the node object with the nodeKey and netKey.
	 * @param k			the key of this node
	 * @param net		the key of the net this node belongs to
	 */
	public BasicNodeInfo(String k, String net)
	{
		key = k;
		netKey = net;
	}

	/**
	 * Returns the key of this node.
	 * @return		the key of this node as String
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * Returns the key of the net this node belongs to
	 * @return		the key of the net as String
	 */
	public String getNetKey()
	{
		return netKey;
	}
}
