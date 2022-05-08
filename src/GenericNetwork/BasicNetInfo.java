package GenericNetwork;


import Util.*;

import java.io.*;

/**
 * The BasicNetInfo class defines the simplest net class assigned with
 * the minimum attributes and served as a data class.
 * Other net classes contain with additional attributes or data should be
 * extended from this.
 * @version			2.1
 * @author			Atom Yuen
 */
public class BasicNetInfo implements Serializable, KeyedObject
{
	/** @serial */
	private String			key;

	/**
	 * Constructs the net object with the netKey.
	 * @param k			the key of this net
	 */
	public BasicNetInfo(String k)
	{
		key = k;
	}

	/**
	 * Returns the key of this net.
	 * @return		the key of this net as String
	 */
	public String getKey()
	{
		return key;
	}
}
