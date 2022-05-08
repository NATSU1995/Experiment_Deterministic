package GenericNetwork;


import Util.*;

import java.util.*;
import java.io.*;

/**
 * The GenericNetwork class constructs the whole structure of the network
 * by considering the information stored in the net object, node object
 * and arc object added to this network and linking them together.
 * In order to construct the network, user has to add the objects in the
 * following order :<p>
 * <nl><li>All required net objects,
 * <li>All required node objects and,
 * <li>All arc objects.<nl><p>
 * Net, node and arc objects can be added individually to the network.
 * When adding these objects, for example node object, user can use 
 * <code>add(nodeInfo)</code> directly or use <code>add(nodeKey, netKey, 
 * nodeInfo)</code> to speed up the process if the nodeKey and netKey of 
 * the node is already known (otherwise these information can be retrieved
 * from the nodeInfo object).<p>
 * On the other hand, user can setup the network by passing directly the
 * related net-list, node-list and arc-list declared as DblLinkList Structure
 * and all the net objects, node objects and arc objects in the three lists
 * are added and connected in the network.<p>
 * Besides, in this version user can construct different network objects
 * containing (pointing) to the same net, node and arc objects so as to 
 * reduce the memory used.
 * @version			2.1
 * @author			Atom Yuen
 */
public class GenericNetwork implements Serializable
{
	/** @serial */
	private DblLinkList			netList;
	/** @serial */
	private Hashtable			netHashtable;
	/** @serial */
	private Hashtable			nodeHashtable;
	/** @serial */
	private Hashtable			arcHashtable;
	/** @serial */
	private Hashtable			inArcHashtable;
	/** @serial */
	private Hashtable			outArcHashtable;

	/**
	 * Constructs a new empty network object with an empty net list.
	 * This network consists of five Hashtables for net, node,
	 * arc, in-arc and out-arc for quick access.
	 */
	public GenericNetwork()
	{
		netList = new DblLinkList();
		netHashtable = new Hashtable();
		nodeHashtable = new Hashtable();
		arcHashtable = new Hashtable();
		inArcHashtable = new Hashtable();
		outArcHashtable = new Hashtable();

	}

	/**
	 * Builds the network using the netInfo objects in the net list,
	 * the nodeInfo objects in the node list and the arcInfo objects in
	 * the arc list.
	 * @param nl		an external net list contains all netInfo objects
	 *					of the network
	 * @param nodeList	an external node list contains all nodeInfo objects
	 *					of the network
	 * @param arcList	an external arc list contains all arcInfo objects
	 *					of the network
	 */
	public void setupNetwork(DblLinkList nl, DblLinkList nodeList, DblLinkList arcList)
	{
		if (!nl.isSetFirst())
		{
			return;
		}
		do
		{
			addNet((BasicNetInfo)nl.getElement());
		} while(nl.isMoveForward());
		nl.reset();

		if (!nodeList.isSetFirst())
		{
			return;
		}
		do
		{
			addNode((BasicNodeInfo)nodeList.getElement());
		} while(nodeList.isMoveForward());
		nodeList.reset();

		if (!arcList.isSetFirst())
		{
			return;
		}
		do
		{
			addArc((BasicArcInfo)arcList.getElement());
		} while(arcList.isMoveForward());
		arcList.reset();
	}
	/**
	 * Clear the arc information by clearling the arcHashable and
	 * inArcHashtable and outArcHashtable.
	 @author Seating
	*/
	public void clearArc()
	{
	    arcHashtable.clear();
	    inArcHashtable.clear();
	    outArcHashtable.clear();
	    return;
	}
	/**
	 * Clear the node information by clearling the arcHashable and
	 * inArcHashtable and outArcHashtable.
	 @author Seating
	*/
	public void clearNode()
	{
	    nodeHashtable.clear();
	    return;
	}
	/**
	 * Clear the net information by clearling the arcHashable and
	 * inArcHashtable and outArcHashtable.
	 @author Seating
	*/
	public void clearNet()
	{
	    netHashtable.clear();
	    return;
	}

	/**
	 * Connects the netInfo object into the network.
	 * @param netInfo		the netInfo object
	 */
	public void addNet(BasicNetInfo netInfo)
	{
		String netKey = netInfo.getKey();
		addNet(netKey, netInfo);
		return;
	}

	/**
	 * Connects the netInfo object into the network with the key
	 * of the net provided.
	 * @param netKey		the key of the net.
	 * @param netInfo		the netInfo object
	 */
	public void addNet(String netKey, BasicNetInfo netInfo)
	{
		BasicNetLink netLink = new BasicNetLink(netInfo);
		netList.addBack(netLink);
		netHashtable.put(netKey, netList.getListNode());
		return;
	}

	/**
	 * Connects the nodeInfo object into the network.
	 * @param nodeInfo		the nodeInfo object
	 */
	public void addNode(BasicNodeInfo nodeInfo)
	{
		String nodeKey = nodeInfo.getKey();
		String netKey = nodeInfo.getNetKey();
		addNode(nodeKey, netKey, nodeInfo);
		return;
	}

	/**
	 * Connects the nodeInfo object into the network with the key
	 * of the node and the key of the net where the node 
	 * belongs to provided.
	 * @param nodeKey		the key of the node.
	 * @param netKey		the key of the net.
	 * @param nodeInfo		the nodeInfo object
	 */
	public void addNode(String nodeKey, String netKey, BasicNodeInfo nodeInfo)
	{
		BasicNetLink netLink = getGenericNetLinkage(netKey);
		if (netLink==null)
		{
			return;
		}
		nodeHashtable.put(nodeKey, netLink.addNode(nodeInfo));
		return;
	}

	/**
	 * Connects the arcInfo object into the network.
	 * @param arcInfo		the arcInfo object
	 */
	public void addArc(BasicArcInfo arcInfo)
	{
		String arcKey = arcInfo.getKey();
		String netKey = arcInfo.getNetKey();
		String fromNodeKey = arcInfo.getFromNodeKey();
		String toNodeKey = arcInfo.getToNodeKey();
		addArc(arcKey, netKey, fromNodeKey, toNodeKey, arcInfo);
		return;
	}

	/**
	 * Connects the arcInfo object into the network with the key
	 * of the node and the key of the net where the node 
	 * belongs to provided.
	 * @param arcKey		the key of the arc
	 * @param netKey		the key of the net
	 * @param fromNodeKey	the key of the from-node
	 * @param toNodeKey		the key of the to-node
	 * @param arcInfo		the arcInfo object
	 */
	public void addArc(String arcKey, String netKey, String fromNodeKey, String toNodeKey, BasicArcInfo arcInfo)
	{
		BasicNetLink netLink = getGenericNetLinkage(netKey);
		BasicNodeLink fromNodeLink = getGenericNodeLinkage(fromNodeKey);
		BasicNodeLink toNodeLink = getGenericNodeLinkage(toNodeKey);
		if (netLink==null || fromNodeLink==null || toNodeLink==null)
		{
			return;
		}
		arcHashtable.put(arcKey, netLink.addArc(arcInfo));
		outArcHashtable.put(arcKey, fromNodeLink.addOutArc(arcInfo));
		inArcHashtable.put(arcKey, toNodeLink.addInArc(arcInfo));
		return;
	}

	/**
	 * Returns the first netInfo object in the net list of the network.
	 * It returns null if no net in the network.
	 * @return			the netInfo object as BasicNetInfo
	 * @see				#getGenericLastNet
	 * @see				#getGenericNet
	 * @see				#getGenericNextNetFrom
	 * @see				#getGenericPrevNetFrom
	 */
	public BasicNetInfo getGenericFirstNet()
	{
		BasicNetLink netLink = (BasicNetLink)netList.getFirstElement();
		if (netLink==null)
		{
			return null;
		}
		return netLink.getNetInfo();
	}

	/**
	 * Returns the last netInfo object in the net list of the network.
	 * It returns null if no net in the network.
	 * @return			the netInfo object as BasicNetInfo
	 * @see				#getGenericFirstNet
	 * @see				#getGenericNet
	 * @see				#getGenericNextNetFrom
	 * @see				#getGenericPrevNetFrom
	 */
	public BasicNetInfo getGenericLastNet()
	{
		BasicNetLink netLink = (BasicNetLink)netList.getLastElement();
		if (netLink==null)
		{
			return null;
		}
		return netLink.getNetInfo();
	}

	/**
	 * Returns the netInfo object with this key in the net list of the network.
	 * It returns null if no net with this key.
	 * @param netKey	the key of the net
	 * @return			the netInfo object as BasicNetInfo
	 * @see				#getGenericFirstNet
	 * @see				#getGenericLastNet
	 * @see				#getGenericNextNetFrom
	 * @see				#getGenericPrevNetFrom
	 */
	public BasicNetInfo getGenericNet(String netKey)
	{
		ListNode netListNode = (ListNode)netHashtable.get(netKey);
		if (netListNode==null)
		{
			return null;
		}
		return ((BasicNetLink)netListNode.getElement()).getNetInfo();
	}

	/**
	 * Returns the netInfo object next to the net with this key
	 * in the net list of the network.
	 * It returns null if no net with this key or no net next to it.
	 * @param netKey	the key of the net
	 * @return			the netInfo object as BasicNetInfo
	 * @see				#getGenericFirstNet
	 * @see				#getGenericLastNet
	 * @see				#getGenericNet
	 * @see				#getGenericPrevNetFrom
	 */
	public BasicNetInfo getGenericNextNetFrom(String netKey)
	{
		ListNode netListNode = (ListNode)netHashtable.get(netKey);
		if (netListNode==null)
		{
			return null;
		}
		BasicNetLink netLink = (BasicNetLink)netListNode.getNextElement();
		if (netLink==null)
		{
			return null;
		}
		return netLink.getNetInfo();
	}

	/**
	 * Returns the netInfo object previous from the net with this key
	 * in the net list of the network.
	 * It returns null if no net with this key or no net previous from it.
	 * @param netKey	the key of the net
	 * @return			the netInfo object as BasicNetInfo
	 * @see				#getGenericFirstNet
	 * @see				#getGenericLastNet
	 * @see				#getGenericNet
	 * @see				#getGenericNextNetFrom
	 */
	public BasicNetInfo getGenericPrevNetFrom(String netKey)
	{
		ListNode netListNode = (ListNode)netHashtable.get(netKey);
		if (netListNode==null)
		{
			return null;
		}
		BasicNetLink netLink = (BasicNetLink)netListNode.getPrevElement();
		if (netLink==null)
		{
			return null;
		}
		return netLink.getNetInfo();
	}

	/**
	 * Returns the net object with this key in the net list of the network.
	 * It returns null if no net with this key.
	 * @param netKey	the key of the net
	 * @return			the net object as BasicNetInfo
	 * @see				#getGenericFirstNet
	 * @see				#getGenericLastNet
	 * @see				#getGenericNextNetFrom
	 * @see				#getGenericPrevNetFrom
	 */
	private BasicNetLink getGenericNetLinkage(String netKey)
	{
		ListNode netListNode = (ListNode)netHashtable.get(netKey);
		if (netListNode==null)
		{
			return null;
		}
		return (BasicNetLink)netListNode.getElement();
	}

	/**
	 * Returns the first nodeInfo object in the net with this key
	 * in the network.
	 * It returns null if no net with this key or no node in this net.
	 * @param netKey	the key of the net
	 * @return			the nodeInfo object as BasicNodeInfo
	 * @see				#getGenericLastNodeIn
	 * @see				#getGenericNode
	 * @see				#getGenericNextNodeFrom
	 * @see				#getGenericPrevNodeFrom
	 */
	public BasicNodeInfo getGenericFirstNodeIn(String netKey)
	{
		BasicNetLink netLink = getGenericNetLinkage(netKey);
		if (netLink==null)
		{
			return null;
		}
		return netLink.getFirstNode();
	}

	/**
	 * Returns the last nodeInfo object in the net with this key
	 * in the network.
	 * It returns null if no net with this key or no node in this net.
	 * @param netKey	the key of the net
	 * @return			the nodeInfo object as BasicNodeInfo
	 * @see				#getGenericFirstNodeIn
	 * @see				#getGenericNode
	 * @see				#getGenericNextNodeFrom
	 * @see				#getGenericPrevNodeFrom
	 */
	public BasicNodeInfo getGenericLastNodeIn(String netKey)
	{
		BasicNetLink netLink = getGenericNetLinkage(netKey);
		if (netLink==null)
		{
			return null;
		}
		return netLink.getLastNode();
	}

	/**
	 * Returns the nodeInfo object with this key in the network.
	 * It returns null if no node with this key.
	 * @param nodeKey	the key of the node
	 * @return			the nodeInfo object as BasicNodeInfo
	 * @see				#getGenericFirstNodeIn
	 * @see				#getGenericLastNodeIn
	 * @see				#getGenericNextNodeFrom
	 * @see				#getGenericPrevNodeFrom
	 */
	public BasicNodeInfo getGenericNode(String nodeKey)
	{
		ListNode nodeListNode = (ListNode)nodeHashtable.get(nodeKey);
		if (nodeListNode==null)
		{
			return null;
		}
		return ((BasicNodeLink)nodeListNode.getElement()).getNodeInfo();
	}

	/**
	 * Returns the nodeInfo object next to the node with this key
	 * in the node list of the network.
	 * It returns null if no node with this key or no node next to it.
	 * @param nodeKey	the key of the node
	 * @return			the nodeInfo object as BasicNodeInfo
	 * @see				#getGenericFirstNodeIn
	 * @see				#getGenericLastNodeIn
	 * @see				#getGenericNode
	 * @see				#getGenericPrevNodeFrom
	 */
	public BasicNodeInfo getGenericNextNodeFrom(String nodeKey)
	{
		ListNode nodeListNode = (ListNode)nodeHashtable.get(nodeKey);
		if (nodeListNode==null)
		{
			return null;
		}
		BasicNodeLink nodeLink = (BasicNodeLink)nodeListNode.getNextElement();
		if (nodeLink==null)
		{
			return null;
		}
		return nodeLink.getNodeInfo();
	}

	/**
	 * Returns the nodeInfo object previous from the node with this key
	 * in the node list of the network.
	 * It returns null if no node with this key or no node previous from it.
	 * @param nodeKey	the key of the node
	 * @return			the nodeInfo object as BasicNodeInfo
	 * @see				#getGenericFirstNodeIn
	 * @see				#getGenericLastNodeIn
	 * @see				#getGenericNode
	 * @see				#getGenericNextNodeFrom
	 */
	public BasicNodeInfo getGenericPrevNodeFrom(String nodeKey)
	{
		ListNode nodeListNode = (ListNode)nodeHashtable.get(nodeKey);
		if (nodeListNode==null)
		{
			return null;
		}
		BasicNodeLink nodeLink = (BasicNodeLink)nodeListNode.getPrevElement();
		if (nodeLink==null)
		{
			return null;
		}
		return nodeLink.getNodeInfo();
	}

	/**
	 * Returns the node object with this key in the network.
	 * It returns null if no node with this key.
	 * @param nodeKey	the key of the node
	 * @return			the node object as BasicNodeInfo
	 * @see				#getGenericFirstNodeIn
	 * @see				#getGenericLastNodeIn
	 * @see				#getGenericNextNodeFrom
	 * @see				#getGenericPrevNodeFrom
	 */
	private BasicNodeLink getGenericNodeLinkage(String nodeKey)
	{
		ListNode nodeListNode = (ListNode)nodeHashtable.get(nodeKey);
		if (nodeListNode==null)
		{
			return null;
		}
		return (BasicNodeLink)nodeListNode.getElement();
	}

	/**
	 * Returns the first arcInfo object in the net with this key
	 * in the network.
	 * It returns null if no net with this key or no arc in this net.
	 * @param netKey	the key of the arc
	 * @return			the arcInfo object as BasicArcInfo
	 * @see				#getGenericLastArcIn
	 * @see				#getGenericArc
	 * @see				#getGenericNextArcFrom
	 * @see				#getGenericPrevArcFrom
	 */
	public BasicArcInfo getGenericFirstArcIn(String netKey)
	{
		BasicNetLink netLink = getGenericNetLinkage(netKey);
		if (netLink==null)
		{
			return null;
		}
		return netLink.getFirstArc();
	}

	/**
	 * Returns the last arcInfo object in the net with this key
	 * in the network.
	 * It returns null if no net with this key or no arc in this net.
	 * @param netKey	the key of the arc
	 * @return			the arcInfo object as BasicArcInfo
	 * @see				#getGenericFirstArcIn
	 * @see				#getGenericArc
	 * @see				#getGenericNextArcFrom
	 * @see				#getGenericPrevArcFrom
	 */
	public BasicArcInfo getGenericLastArcIn(String netKey)
	{
		BasicNetLink netLink = getGenericNetLinkage(netKey);
		if (netLink==null)
		{
			return null;
		}
		return netLink.getLastArc();
	}

	/**
	 * Returns the arcInfo object with this key in the network.
	 * It returns null if no arc with this key.
	 * @param arcKey	the key of the arc
	 * @return			the arcInfo object as BasicArcInfo
	 * @see				#getGenericFirstArcIn
	 * @see				#getGenericLastArcIn
	 * @see				#getGenericNextArcFrom
	 * @see				#getGenericPrevArcFrom
	 */
	public BasicArcInfo getGenericArc(String arcKey)
	{
		ListNode arcListNode = (ListNode)arcHashtable.get(arcKey);
		if (arcListNode==null)
		{
			return null;
		}
		return (BasicArcInfo)arcListNode.getElement();
	}

	/**
	 * Returns the arcInfo object next to the arc with this key
	 * in the arc list of the network.
	 * It returns null if no arc with this key or no arc next to it.
	 * @param arcKey	the key of the arc
	 * @return			the arcInfo object as BasicArcInfo
	 * @see				#getGenericFirstArcIn
	 * @see				#getGenericLastArcIn
	 * @see				#getGenericArc
	 * @see				#getGenericPrevArcFrom
	 */
	public BasicArcInfo getGenericNextArcFrom(String arcKey)
	{
		ListNode arcListNode = (ListNode)arcHashtable.get(arcKey);
		if (arcListNode==null)
		{
			return null;
		}
		return (BasicArcInfo)arcListNode.getNextElement();
	}

	/**
	 * Returns the arcInfo object previous from the arc with this key
	 * in the arc list of the network.
	 * It returns null if no arc with this key or no arc previous from it.
	 * @param arcKey	the key of the arc
	 * @return			the arcInfo object as BasicArcInfo
	 * @see				#getGenericFirstArcIn
	 * @see				#getGenericLastArcIn
	 * @see				#getGenericArc
	 * @see				#getGenericNextArcFrom
	 */
	public BasicArcInfo getGenericPrevArcFrom(String arcKey)
	{
		ListNode arcListNode = (ListNode)arcHashtable.get(arcKey);
		if (arcListNode==null)
		{
			return null;
		}
		return (BasicArcInfo)arcListNode.getPrevElement();
	}

	/**
	 * Returns the first in-arcInfo object of the node with this key
	 * in the network.
	 * It returns null if no node with this key or no arc entering into it.
	 * @param nodeKey	the key of the node
	 * @return			the arcInfo object as BasicArcInfo
	 * @see				#getGenericLastInArcOf
	 * @see				#getGenericInArc
	 * @see				#getGenericNextInArcFrom
	 * @see				#getGenericPrevInArcFrom
	 */
	public BasicArcInfo getGenericFirstInArcOf(String nodeKey)
	{
		BasicNodeLink nodeLink = getGenericNodeLinkage(nodeKey);
		return nodeLink.getFirstInArc();
	}

	/**
	 * Returns the last in-arcInfo object of the node with this key
	 * in the network.
	 * It returns null if no node with this key or no arc entering into it.
	 * @param nodeKey	the key of the node
	 * @return			the arcInfo object as BasicArcInfo
	 * @see				#getGenericFirstInArcOf
	 * @see				#getGenericInArc
	 * @see				#getGenericNextInArcFrom
	 * @see				#getGenericPrevInArcFrom
	 */
	public BasicArcInfo getGenericLastInArcOf(String nodeKey)
	{
		BasicNodeLink nodeLink = getGenericNodeLinkage(nodeKey);
		return nodeLink.getLastInArc();
	}

	/**
	 * Returns the in-arcInfo object with this key in the network.
	 * It returns null if no in-arc with this key.
	 * @param arcKey	the key of the arc
	 * @return			the arcInfo object as BasicArcInfo
	 * @see				#getGenericFirstInArcOf
	 * @see				#getGenericLastInArcOf
	 * @see				#getGenericNextInArcFrom
	 * @see				#getGenericPrevInArcFrom
	 */
	public BasicArcInfo getGenericInArc(String arcKey)
	{
		ListNode arcListNode = (ListNode)inArcHashtable.get(arcKey);
		if (arcListNode==null)
		{
			return null;
		}
		return (BasicArcInfo)arcListNode.getElement();
	}

	/**
	 * Returns the in-arcInfo object next to the in-arc with this key
	 * in the network.
	 * It returns null if no in-arc with this key or no in-arc next to it.
	 * @param arcKey	the key of the arc
	 * @return			the arcInfo object as BasicArcInfo
	 * @see				#getGenericLastInArcOf
	 * @see				#getGenericFirstInArcOf
	 * @see				#getGenericInArc
	 * @see				#getGenericPrevInArcFrom
	 */
	public BasicArcInfo getGenericNextInArcFrom(String arcKey)
	{
		ListNode arcListNode = (ListNode)inArcHashtable.get(arcKey);
		if (arcListNode==null)
		{
			return null;
		}
		return (BasicArcInfo)arcListNode.getNextElement();
	}

	/**
	 * Returns the in-arcInfo object previous from the in-arc with this key
	 * in the network.
	 * It returns null if no in-arc with this key or no in-arc previous from it.
	 * @param arcKey	the key of the arc
	 * @return			the arcInfo object as BasicArcInfo
	 * @see				#getGenericLastInArcOf
	 * @see				#getGenericFirstInArcOf
	 * @see				#getGenericInArc
	 * @see				#getGenericNextInArcFrom
	 */
	public BasicArcInfo getGenericPrevInArcFrom(String arcKey)
	{
		ListNode arcListNode = (ListNode)inArcHashtable.get(arcKey);
		if (arcListNode==null)
		{
			return null;
		}
		return (BasicArcInfo)arcListNode.getPrevElement();
	}

	/**
	 * Returns the first out-arcInfo object of the node with this key
	 * in the network.
	 * It returns null if no node with this key or no arc going out from it.
	 * @param nodeKey	the key of the node
	 * @return			the arcInfo object as BasicArcInfo
	 * @see				#getGenericLastOutArcOf
	 * @see				#getGenericOutArc
	 * @see				#getGenericNextOutArcFrom
	 * @see				#getGenericPrevOutArcFrom
	 */
	public BasicArcInfo getGenericFirstOutArcOf(String nodeKey)
	{
		BasicNodeLink nodeLink = getGenericNodeLinkage(nodeKey);
		return nodeLink.getFirstOutArc();
	}

	/**
	 * Returns the last out-arcInfo object of the node with this key
	 * in the network.
	 * It returns null if no node with this key or no arc going out from it.
	 * @param nodeKey	the key of the node
	 * @return			the arcInfo object as BasicArcInfo
	 * @see				#getGenericFirstOutArcOf
	 * @see				#getGenericOutArc
	 * @see				#getGenericNextOutArcFrom
	 * @see				#getGenericPrevOutArcFrom
	 */
	public BasicArcInfo getGenericLastOutArcOf(String nodeKey)
	{
		BasicNodeLink nodeLink = getGenericNodeLinkage(nodeKey);
		return nodeLink.getLastOutArc();
	}

	/**
	 * Returns the out-arcInfo object with this key in the network.
	 * It returns null if no out-arc with this key.
	 * @param nodeKey	the key of the node
	 * @return			the arcInfo object as BasicArcInfo
	 * @see				#getGenericFirstOutArcOf
	 * @see				#getGenericLastOutArcOf
	 * @see				#getGenericNextOutArcFrom
	 * @see				#getGenericPrevOutArcFrom
	 */
	public BasicArcInfo getGenericOutArc(String arcKey)
	{
		ListNode arcListNode = (ListNode)outArcHashtable.get(arcKey);
		if (arcListNode==null)
		{
			return null;
		}
		return (BasicArcInfo)arcListNode.getElement();
	}

	/**
	 * Returns the out-arcInfo object next to the out-arc with this key
	 * in the network.
	 * It returns null if no out-arc with this key or no out-arc next to it.
	 * @param arcKey	the key of the arc
	 * @return			the arcInfo object as BasicArcInfo
	 * @see				#getGenericFirstOutArcOf
	 * @see				#getGenericLastOutArcOf
	 * @see				#getGenericOutArc
	 * @see				#getGenericPrevOutArcFrom
	 */
	public BasicArcInfo getGenericNextOutArcFrom(String arcKey)
	{
		ListNode arcListNode = (ListNode)outArcHashtable.get(arcKey);
		if (arcListNode==null)
		{
			return null;
		}
		return (BasicArcInfo)arcListNode.getNextElement();
	}

	/**
	 * Returns the out-arcInfo object next to the out-arc with this key
	 * in the network.
	 * It returns null if no out-arc with this key or no out-arc previous from it.
	 * @param arcKey	the key of the arc
	 * @return			the arcInfo object as BasicArcInfo
	 * @see				#getGenericFirstOutArcOf
	 * @see				#getGenericLastOutArcOf
	 * @see				#getGenericOutArc
	 * @see				#getGenericPrevOutArcFrom
	 */
	public BasicArcInfo getGenericPrevOutArcFrom(String arcKey)
	{
		ListNode arcListNode = (ListNode)outArcHashtable.get(arcKey);
		if (arcListNode==null)
		{
			return null;
		}
		return (BasicArcInfo)arcListNode.getPrevElement();
	}

	/**
	 * Clears the whole network.  It removes all the netInfo, 
	 * nodeInfo and arcInfo objects from the network.
	 * The network becomes empty.
	 * @see				#removeAllArcs
	 * @see				#removeAllArcsIn
	 * @see				#removeArc
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeAllNodesIn
	 * @see				#removeNode
	 * @see				#removeAllNets
	 * @see				#removeNet
	 */
	public void destroy()
	{
		removeAllNets();
	}

	/**
	 * Removes all arcInfo objects from the network.
	 * All nodeInfo and netInfo objects remain in the network.
	 * @see				#destroy
	 * @see				#removeAllArcsIn
	 * @see				#removeArc
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeAllNodesIn
	 * @see				#removeNode
	 * @see				#removeAllNets
	 * @see				#removeNet
	 */
	public void removeAllArcs()
	{
		BasicNetInfo netInfo = getGenericFirstNet();
		while (netInfo!=null)
		{
			String netKey = netInfo.getKey();
			removeAllArcsIn(netKey);
			netInfo = getGenericNextNetFrom(netKey);
		}
		return;
	}

	/**
	 * Removes all arcInfo objects in the net with this key from the network.
	 * All nodeInfo and netInfo and other arcInfo objects remain in the network.
	 * @param netKey	the key of the net
	 * @see				#destroy
	 * @see				#removeAllArcs
	 * @see				#removeArc
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeAllNodesIn
	 * @see				#removeNode
	 * @see				#removeAllNets
	 * @see				#removeNet
	 */
	public void removeAllArcsIn(String netKey)
	{
		BasicArcInfo arcInfo = getGenericFirstArcIn(netKey);
		while (arcInfo!=null)
		{
			BasicArcInfo tempArcInfo = arcInfo;
			String arcKey = arcInfo.getKey();
			arcInfo = getGenericNextArcFrom(arcKey);
			removeArc(arcKey, tempArcInfo);
		}
		return;
	}

	/**
	 * Removes one arcInfo objects with this key from the network.
	 * All nodeInfo and netInfo and other arcInfo objects remain in the network.
	 * @param arcKey	the key of the arc
	 * @see				#destroy
	 * @see				#removeAllArcs
	 * @see				#removeAllArcsIn
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeAllNodesIn
	 * @see				#removeNode
	 * @see				#removeAllNets
	 * @see				#removeNet
	 */
	public void removeArc(String arcKey)
	{
		BasicArcInfo arcInfo = getGenericArc(arcKey);
		removeArc(arcKey, arcInfo);
		return;
	}

	/**
	 * Removes this arcInfo objects from the network.
	 * All nodeInfo and netInfo and other arcInfo objects remain in the network.
	 * @param arcInfo	the arcInfo object
	 * @see				#destroy
	 * @see				#removeAllArcs
	 * @see				#removeAllArcsIn
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeAllNodesIn
	 * @see				#removeNode
	 * @see				#removeAllNets
	 * @see				#removeNet
	 */
	public void removeArc(BasicArcInfo arcInfo)
	{
		String arcKey = arcInfo.getKey();
		removeArc(arcKey, arcInfo);
		return;
	}

	/**
	 * Removes this arcInfo objects from the network where 
	 * the key of the arc provided.
	 * All nodeInfo and netInfo and other arcInfo objects remain in the network.
	 * @param arcKey	the key of the arc
	 * @param arcInfo	the arcInfo object
	 * @see				#destroy
	 * @see				#removeAllArcs
	 * @see				#removeAllArcsIn
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeAllNodesIn
	 * @see				#removeNode
	 * @see				#removeAllNets
	 * @see				#removeNet
	 */
	private void removeArc(String arcKey, BasicArcInfo arcInfo)
	{
		ListNode arcListNode = (ListNode)outArcHashtable.get(arcKey);
		if (arcListNode!=null)
		{
			BasicNodeLink fromNodeLink = getGenericNodeLinkage(arcInfo.getFromNodeKey());
			if (fromNodeLink!=null)
			{
				fromNodeLink.removeOutArcListNode(arcListNode);
			}
			outArcHashtable.remove(arcKey);
		}

		arcListNode = (ListNode)inArcHashtable.get(arcKey);
		if (arcListNode!=null)
		{
			BasicNodeLink toNodeLink = getGenericNodeLinkage(arcInfo.getToNodeKey());
			if (toNodeLink!=null)
			{
				toNodeLink.removeInArcListNode(arcListNode);
			}
			inArcHashtable.remove(arcKey);
		}

		arcListNode = (ListNode)arcHashtable.get(arcKey);
		if (arcListNode!=null)
		{
			BasicNetLink netLink = getGenericNetLinkage(arcInfo.getNetKey());
			if (netLink!=null)
			{
				netLink.removeArcListNode(arcListNode);
			}
			arcHashtable.remove(arcKey);
		}
		return;
	}

	/**
	 * Removes all arcInfo objects entering into the node with this key
	 * from the network.
	 * All nodeInfo and netInfo and other arcInfo objects remain in the network.
	 * @param nodeKey	the key of the node
	 * @see				#destroy
	 * @see				#removeAllArcs
	 * @see				#removeAllArcsIn
	 * @see				#removeArc
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeAllNodesIn
	 * @see				#removeNode
	 * @see				#removeAllNets
	 * @see				#removeNet
	 */
	public void removeAllInArcsOf(String nodeKey)
	{
		BasicArcInfo arcInfo = getGenericFirstInArcOf(nodeKey);
		while (arcInfo!=null)
		{
			BasicArcInfo tempArcInfo = arcInfo;
			String arcKey = arcInfo.getKey();
			arcInfo = getGenericNextInArcFrom(arcKey);
			removeArc(arcKey, tempArcInfo);
		}
		return;
	}

	/**
	 * Removes all arcInfo objects going out from the node with this key
	 * from the network.
	 * All nodeInfo and netInfo and other arcInfo objects remain in the network.
	 * @param nodeKey	the key of the node
	 * @see				#destroy
	 * @see				#removeAllArcs
	 * @see				#removeAllArcsIn
	 * @see				#removeArc
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeAllNodesIn
	 * @see				#removeNode
	 * @see				#removeAllNets
	 * @see				#removeNet
	 */
	public void removeAllOutArcsOf(String nodeKey)
	{
		BasicArcInfo arcInfo = getGenericFirstOutArcOf(nodeKey);
		while (arcInfo!=null)
		{
			BasicArcInfo tempArcInfo = arcInfo;
			String arcKey = arcInfo.getKey();
			arcInfo = getGenericNextOutArcFrom(arcKey);
			removeArc(arcKey, tempArcInfo);
		}
		return;
	}

	/**
	 * Removes all nodeInfo objects from the network.
	 * All arcInfo objects are also removed from the network.
	 * All netInfo objects remain in the network.
	 * @see				#destroy
	 * @see				#removeAllArcs
	 * @see				#removeAllArcsIn
	 * @see				#removeArc
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodesIn
	 * @see				#removeNode
	 * @see				#removeAllNets
	 * @see				#removeNet
	 */
	public void removeAllNodes()
	{
		BasicNetInfo netInfo = getGenericFirstNet();
		while (netInfo!=null)
		{
			String netKey = netInfo.getKey();
			removeAllNodesIn(netKey);
			netInfo = getGenericNextNetFrom(netKey);
		}
		return;
	}

	/**
	 * Removes all nodeInfo objects in the net with this key from the network.
	 * All arcInfo objects in this net are also removed from the network.
	 * All netInfo and other nodeInfo and arcInfo objects remain in the network.
	 * @param netKey	the key of the net
	 * @see				#destroy
	 * @see				#removeAllArcs
	 * @see				#removeAllArcsIn
	 * @see				#removeArc
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeNode
	 * @see				#removeAllNets
	 * @see				#removeNet
	 */
	public void removeAllNodesIn(String netKey)
	{
		BasicNodeInfo nodeInfo = getGenericFirstNodeIn(netKey);
		while (nodeInfo!=null)
		{
			BasicNodeInfo tempNodeInfo = nodeInfo;
			String nodeKey = nodeInfo.getKey();
			nodeInfo = getGenericNextNodeFrom(nodeKey);
			removeNode(nodeKey, tempNodeInfo);
		}
		return;
	}

	/**
	 * Removes one nodeInfo objects with this key from the network.
	 * All arcInfo objects associated with this node
	 * are also removed from the network.
	 * All netInfo and other nodeInfo and arcInfo objects remain in the network.
	 * @param nodeKey	the key of the node
	 * @see				#destroy
	 * @see				#removeAllArcs
	 * @see				#removeAllArcsIn
	 * @see				#removeArc
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeAllNodesIn
	 * @see				#removeAllNets
	 * @see				#removeNet
	 */
	public void removeNode(String nodeKey)
	{
		BasicNodeInfo nodeInfo = getGenericNode(nodeKey);
		if (nodeInfo!=null)
		{
			removeNode(nodeKey, nodeInfo);
		}
		return;
	}

	/**
	 * Removes this nodeInfo objects from the network.
	 * All arcInfo objects associated with this node
	 * are also removed from the network.
	 * All netInfo and other nodeInfo and arcInfo objects remain in the network.
	 * @param nodeInfo	the nodeInfo object
	 * @see				#destroy
	 * @see				#removeAllArcs
	 * @see				#removeAllArcsIn
	 * @see				#removeArc
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeAllNodesIn
	 * @see				#removeAllNets
	 * @see				#removeNet
	 */
	public void removeNode(BasicNodeInfo nodeInfo)
	{
		String nodeKey = nodeInfo.getKey();
		removeNode(nodeKey, nodeInfo);
		return;
	}

	/**
	 * Removes this nodeInfo objects from the network where
	 * the key of the node provided.
	 * All arcInfo objects associated with this node
	 * are also removed from the network.
	 * All netInfo and other nodeInfo and arcInfo objects remain in the network.
	 * @param nodeKey	the key of the 
	 * @param nodeInfo	the nodeInfo object
	 * @see				#destroy
	 * @see				#removeAllArcs
	 * @see				#removeAllArcsIn
	 * @see				#removeArc
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeAllNodesIn
	 * @see				#removeAllNets
	 * @see				#removeNet
	 */
	private void removeNode(String nodeKey, BasicNodeInfo nodeInfo)
	{
		removeAllInArcsOf(nodeKey);
		removeAllOutArcsOf(nodeKey);
		ListNode nodeListNode = (ListNode)nodeHashtable.get(nodeKey);
		if (nodeListNode!=null)
		{
			BasicNetLink netLink = getGenericNetLinkage(nodeInfo.getNetKey());
			if (netLink!=null)
			{
				netLink.removeNodeListNode(nodeListNode);
			}
			nodeHashtable.remove(nodeKey);
		}
		return;
	}

	/**
	 * Removes all netInfo objects from the network.
	 * All nodeInfo and arcInfo objects are also removed from the network.
	 * @see				#destroy
	 * @see				#removeAllArcs
	 * @see				#removeAllArcsIn
	 * @see				#removeArc
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeAllNodesIn
	 * @see				#removeNode
	 * @see				#removeNet
	 */
	public void removeAllNets()
	{
		BasicNetInfo netInfo = getGenericFirstNet();
		while (netInfo!=null)
		{
			BasicNetInfo tempNetInfo = netInfo;
			String netKey = netInfo.getKey();
			netInfo = getGenericNextNetFrom(netKey);
			removeNet(netKey, tempNetInfo);
		}
		return;
	}

	/**
	 * Removes one netInfo objects with this key from the network.
	 * All nodeInfo and arcInfo objects associated with this net
	 * are also removed.
	 * All other netInfo, nodeInfo and arcInfo objects remain in the network.
	 * @param netKey	the key of the net
	 * @see				#destroy
	 * @see				#removeAllArcs
	 * @see				#removeAllArcsIn
	 * @see				#removeArc
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeAllNodesIn
	 * @see				#removeNode
	 * @see				#removeAllNets
	 */
	public void removeNet(String netKey)
	{
		BasicNetInfo netInfo = getGenericNet(netKey);
		if (netInfo!=null)
		{
			removeNet(netKey, netInfo);
		}
		return;
	}

	/**
	 * Removes this netInfo objects from the network.
	 * All nodeInfo and arcInfo objects associated with this net
	 * are also removed.
	 * All other netInfo, nodeInfo and arcInfo objects remain in the network.
	 * @param netInfo	the netInfo object
	 * @see				#destroy
	 * @see				#removeAllArcs
	 * @see				#removeAllArcsIn
	 * @see				#removeArc
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeAllNodesIn
	 * @see				#removeNode
	 * @see				#removeAllNets
	 * @see				#removeNet
	 */
	public void removeNet(BasicNetInfo netInfo)
	{
		String netKey = netInfo.getKey();
		removeNet(netKey, netInfo);
		return;
	}

	/**
	 * Removes this netInfo objects from the network where
	 * the key of the net provided.
	 * All nodeInfo and arcInfo objects associated with this net
	 * are also removed.
	 * All other netInfo, nodeInfo and arcInfo objects remain in the network.
	 * @param netKey	the key of the net
	 * @param netInfo	the netInfo object
	 * @see				#destroy
	 * @see				#removeAllArcs
	 * @see				#removeAllArcsIn
	 * @see				#removeArc
	 * @see				#removeAllInArcsOf
	 * @see				#removeAllOutArcsOf
	 * @see				#removeAllNodes
	 * @see				#removeAllNodesIn
	 * @see				#removeNode
	 * @see				#removeAllNets
	 */
	public void removeNet(String netKey, BasicNetInfo netInfo)
	{
		removeAllNodesIn(netKey);
		ListNode netListNode = (ListNode)netHashtable.get(netKey);
		if (netListNode!=null)
		{
			netList.removeListNode(netListNode);
			netHashtable.remove(netKey);
		}
		return;
	}

	/**
	 * Gets the total number of netInfo objects contained in the network.
	 * @return		the total number of netInfo objects as int
	 */
	public int getNumNet()
	{
		return netList.size();
	}

	/**
	 * Gets the total number of nodeInfo objects contained in the network.
	 * @return			the total number of nodeInfo objects as int
	 * @see				#getNumNodeIn
	 */
	public int getNumNode()
	{
		int numNode = 0;
		BasicNetInfo netInfo = getGenericFirstNet();
		while (netInfo!=null)
		{
			String netKey = netInfo.getKey();
			numNode += getNumNodeIn(netKey);
			netInfo = getGenericNextNetFrom(netKey);
		}
		return numNode;
	}

	/**
	 * Gets the number of nodeInfo objects contained in the net with this key.
	 * @param netKey	the key of the net
	 * @return			the number of nodeInfo objects as int
	 */
	public int getNumNodeIn(String netKey)
	{
		BasicNetLink netLink = getGenericNetLinkage(netKey);
		if (netLink!=null)
		{
			return netLink.getNumNode();
		}
		return 0;
	}

	/**
	 * Gets the total number of arcInfo objects contained in the network.
	 * @return			the total number of arcInfo objects as int
	 * @see				#getNumArcIn
	 */
	public int getNumArc()
	{
		int numArc = 0;
		BasicNetInfo netInfo = getGenericFirstNet();
		while (netInfo!=null)
		{
			String netKey = netInfo.getKey();
			numArc += getNumArcIn(netKey);
			netInfo = getGenericNextNetFrom(netKey);
		}
		return numArc;
	}

	/**
	 * Gets the number of arcInfo objects contained in the net with this key.
	 * @param netKey	the key of the net
	 * @return			the number of arcInfo objects as int
	 */
	public int getNumArcIn(String netKey)
	{
		BasicNetLink netLink = getGenericNetLinkage(netKey);
		if (netLink!=null)
		{
			return netLink.getNumArc();
		}
		return 0;
	}

	/**
	 * Gets the number of inArcInfo objects connected to the node with this key.
	 * @param nodeKey	the key of the node
	 * @return			the number of inArcInfo objects as int
	 */
	public int getNumInArcOf(String nodeKey)
	{
		BasicNodeLink nodeLink = getGenericNodeLinkage(nodeKey);
		if (nodeLink!=null)
		{
			return nodeLink.getNumInArc();
		}
		return 0;
	}

	/**
	 * Gets the number of outArcInfo objects connected to the node with this key.
	 * @param nodeKey	the key of the node
	 * @return			the number of outArcInfo objects as int
	 */
	public int getNumOutArcOf(String nodeKey)
	{
		BasicNodeLink nodeLink = getGenericNodeLinkage(nodeKey);
		if (nodeLink!=null)
		{
			return nodeLink.getNumOutArc();
		}
		return 0;
	}
}