package GenericNetwork;


import Util.*;

import java.io.*;

/**
 * The BasicNetLink class defines a new connection point for a net object
 * to the network.  The details of the net object will be defined in the 
 * BasicNetInfo object.
 * @version			2.1
 * @author			Atom Yuen
 */
class BasicNetLink implements Serializable
{
	/** @serial */
	private BasicNetInfo	netInfo;
	/** @serial */
	private DblLinkList		nodeList;
	/** @serial */
	private DblLinkList		arcList;

	/**
	 * Constructs the netLink object.
	 * Two empty double-linked list, the node list and arc list,
	 * are created for this net to store all nodes and arcs
	 * belonging to this net.
	 * @param k			the key of this net
	 */
	protected BasicNetLink(BasicNetInfo n)
	{
		netInfo = n;
		nodeList = new DblLinkList();
		arcList = new DblLinkList();
	}

	/**
	 * Returns the BasicNetInfo object.
	 * @return		the BasicNetInfo object containing data of the net
	 */
	protected BasicNetInfo getNetInfo()
	{
		return netInfo;
	}

	/**
	 * A ListNode pointing to a node object where the node belongs to
	 * this net is created and insert into the node list of this net.
	 * It returns the ListNode of the added node in the node list
	 * which is used by the node hashtable.
	 * @param nodeInfo	the node object
	 * @return			the node ListNode as ListNode
	 */
	protected ListNode addNode(BasicNodeInfo nodeInfo)
	{
		BasicNodeLink nodeLink = new BasicNodeLink(nodeInfo);
		nodeList.addBack(nodeLink);
		return nodeList.getListNode();
	}

	/**
	 * A ListNode pointing to an arc object where the arc belongs to
	 * this net is created and insert into the arc list of this net.
	 * It returns the ListNode of the added arc in the arc list
	 * which is used by the arc hashtable.
	 * @param arcInfo	the arc object
	 * @return			the arc ListNode as ListNode
	 */
	protected ListNode addArc(BasicArcInfo arcInfo)
	{
		arcList.addBack(arcInfo);
		return arcList.getListNode();
	}

	/**
	 * Returns the first node object in the node list of this net.
	 * @return		the node object as BasicNodeInfo
	 */
	protected BasicNodeInfo getFirstNode()
	{
		BasicNodeLink nodeLink = (BasicNodeLink)nodeList.getFirstElement();
		if (nodeLink==null)
		{
			return null;
		}
		return nodeLink.getNodeInfo();
	}

	/**
	 * Returns the last node object in the node list of this net.
	 * @return		the node object as BasicNodeInfo
	 */
	protected BasicNodeInfo getLastNode()
	{
		BasicNodeLink nodeLink = (BasicNodeLink)nodeList.getLastElement();
		if (nodeLink==null)
		{
			return null;
		}
		return nodeLink.getNodeInfo();
	}

	/**
	 * Returns the first arc object in the arc list of this net.
	 * @return		the arc object as BasicArcInfo
	 */
	protected BasicArcInfo getFirstArc()
	{
		return (BasicArcInfo)arcList.getFirstElement();
	}

	/**
	 * Returns the last arc object in the arc list of this net.
	 * @return		the arc object as BasicArcInfo
	 */
	protected BasicArcInfo getLastArc()
	{
		return (BasicArcInfo)arcList.getLastElement();
	}

	/**
	 * Removes the ListNode nodeListNode in the node list of this net.
	 * @param nodeListNode		the ListNode to be removed
	 */
	protected void removeNodeListNode(ListNode nodeListNode)
	{
		BasicNodeLink nodeLink = (BasicNodeLink)nodeListNode.getElement();
		nodeLink.dispose();
		nodeList.removeListNode(nodeListNode);
		return;
	}

	/**
	 * Removes the ListNode arcListNode in the arc list of this net.
	 * @param arcListNode		the ListNode to be removed
	 */
	protected void removeArcListNode(ListNode arcListNode)
	{
		arcList.removeListNode(arcListNode);
		return;
	}

	/**
	 * Clear the BasicNetLink object.
	 * The netInfo object is no longer pointed to and
	 * Both the node and arc list are cleared.
	 */
	protected void dispose()
	{
		netInfo = null;
		nodeList.clear();
		arcList.clear();
	}

	/**
	 * Gets the number of nodeInfo objects contained in this netLink object.
	 * @return			the number of nodeInfo objects as int
	 */
	protected int getNumNode()
	{
		return nodeList.size();
	}

	/**
	 * Gets the number of arcInfo objects contained in this netLink object.
	 * @return			the number of arcInfo objects as int
	 */
	protected int getNumArc()
	{
		return arcList.size();
	}
}
