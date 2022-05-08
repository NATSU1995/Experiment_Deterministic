package GenericNetwork;


import Util.*;

import java.io.*;

/**
 * The BasicNodeLink class defines a new connection point for a node object
 * to the network.  The details of the node object will be defined in the 
 * BasicNodeInfo object.
 * @version			2.1
 * @author			Atom Yuen
 */
class BasicNodeLink implements Serializable
{
	/** @serial */
	private BasicNodeInfo	nodeInfo;
	/** @serial */
	private DblLinkList		inArcList;
	/** @serial */
	private DblLinkList		outArcList;

	/**
	 * Constructs the nodeLink object.
	 * Two empty double-linked list, the in-arc list and out-arc list,
	 * are created for this node to store all arcs entering into
	 * and going out from this node.
	 * @param n		the BasicNodeInfo object containing data of the node
	 */
	protected BasicNodeLink(BasicNodeInfo n)
	{
		nodeInfo = n;
		inArcList = new DblLinkList();
		outArcList = new DblLinkList();
	}

	/**
	 * Returns the BasicNodeInfo object.
	 * @return		the BasicNodeInfo object containing data of the node
	 */
	protected BasicNodeInfo getNodeInfo()
	{
		return nodeInfo;
	}

	/**
	 * A ListNode pointing to an arc object where the arc is entering into
	 * this node is created and insert into the in-arc list of this node.
	 * It returns the ListNode of the added arc object in the in-arc list
	 * which is used by the in-arc hashtable.
	 * @param arcInfo	the arc object
	 * @return			the arc ListNode as ListNode
	 */
	protected ListNode addInArc(BasicArcInfo arcInfo)
	{
		inArcList.addBack(arcInfo);
		return inArcList.getListNode();
	}

	/**
	 * A ListNode pointing to an arc object where the arc is going out from
	 * this node is created and insert into the out-arc list of this node.
	 * It returns the ListNode of the added arc object in the out-arc list
	 * which is used by the out-arc hashtable.
	 * @param arcInfo	the arc object
	 * @return			the arc ListNode
	 */
	protected ListNode addOutArc(BasicArcInfo arcInfo)
	{
		outArcList.addBack(arcInfo);
		return outArcList.getListNode();
	}

	/**
	 * Returns the first arc object in the in-arc list of this node.
	 * @return		the arc object as BasicArcInfo
	 */
	protected BasicArcInfo getFirstInArc()
	{
		return (BasicArcInfo)inArcList.getFirstElement();
	}

	/**
	 * Returns the last arc object in the in-arc list of this node.
	 * @return		the arc object as BasicArcInfo
	 */
	protected BasicArcInfo getLastInArc()
	{
		return (BasicArcInfo)inArcList.getLastElement();
	}

	/**
	 * Returns the first arc object in the out-arc list of this node.
	 * @return		the arc object as BasicArcInfo
	 */
	protected BasicArcInfo getFirstOutArc()
	{
		return (BasicArcInfo)outArcList.getFirstElement();
	}

	/**
	 * Returns the last arc object in the out-arc list of this node.
	 * @return		the arc object as BasicArcInfo
	 */
	protected BasicArcInfo getLastOutArc()
	{
		return (BasicArcInfo)outArcList.getLastElement();
	}

	/**
	 * Removes the ListNode inArcListNode in the in-arc list of this node.
	 * @param inArcListNode		the ListNode to be removed
	 */
	protected void removeInArcListNode(ListNode inArcListNode)
	{
		inArcList.removeListNode(inArcListNode);
		return;
	}

	/**
	 * Removes the ListNode outArcListNode in the out-arc list of this node.
	 * @param outArcListNode		the ListNode to be removed
	 */
	protected void removeOutArcListNode(ListNode outArcListNode)
	{
		outArcList.removeListNode(outArcListNode);
		return;
	}

	/**
	 * Clear the BasicNodeLink object.
	 * The nodeInfo object is no longer pointed to and
	 * Both the in-arc and out-arc list are cleared.
	 */
	protected void dispose()
	{
		nodeInfo = null;
		inArcList.clear();
		outArcList.clear();
	}

	/**
	 * Gets the number of inArcInfo objects connected to this nodeLink object.
	 * @return			the number of inArcInfo objects as int
	 */
	protected int getNumInArc()
	{
		return inArcList.size();
	}

	/**
	 * Gets the number of outArcInfo objects connected to this nodeLink object.
	 * @return			the number of outArcInfo objects as int
	 */
	protected int getNumOutArc()
	{
		return outArcList.size();
	}
}
