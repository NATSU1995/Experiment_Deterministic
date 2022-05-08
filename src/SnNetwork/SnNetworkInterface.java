package SnNetwork;

interface SnNetworkInterface
{
	// get the first NetInfo object in the Generic Network
	public SnNetInfo getFirstNet();

	// get the last NetInfo object in the Generic Network
	public SnNetInfo getLastNet();

	// get the NetInfo object using its netKey
	public SnNetInfo getNet(String netKey);

	// get the NetInfo object next to the net with this netKey
	public SnNetInfo getNextNetFrom(String netKey);

	// get the NetInfo object previous from the net with this netKey
	public SnNetInfo getPrevNetFrom(String netKey);

	// get the first NodeInfo object in the net with this netKey
	public SnNodeInfo getFirstNodeIn(String netKey);

	// get the last NodeInfo object in the net with this netKey
	public SnNodeInfo getLastNodeIn(String netKey);

	// get the NodeInfo object using its nodeKey
	public SnNodeInfo getNode(String nodeKey);

	// get the NodeInfo object next to the node with this nodeKey
	public SnNodeInfo getNextNodeFrom(String nodeKey);

	// get the NodeInfo object previous from the node with this nodeKey
	public SnNodeInfo getPrevNodeFrom(String nodeKey);

	// get the first ArcInfo object in the net with this netKey
	public SnArcInfo getFirstArcIn(String netKey);

	// get the last ArcInfo object in the net with this netKey
	public SnArcInfo getLastArcIn(String netKey);

	// get the ArcInfo object using its arcKey
	public SnArcInfo getArc(String arcKey);

	// get the ArcInfo object next to the arc with this arcKey
	public SnArcInfo getNextArcFrom(String arcKey);

	// get the ArcInfo object previous from the arc with this arcKey
	public SnArcInfo getPrevArcFrom(String arcKey);

	// get the first Incoming ArcInfo object of the node with this nodeKey
	public SnArcInfo getFirstInArcOf(String nodeKey);

	// get the last Incoming ArcInfo object of the node with this nodeKey
	public SnArcInfo getLastInArcOf(String nodeKey);

	// get the Incoming ArcInfo object using its arcKey
	public SnArcInfo getInArc(String arcKey);

	// get the Incoming ArcInfo object next to the arc with this arcKey
	public SnArcInfo getNextInArcFrom(String arcKey);

	// get the Incoming ArcInfo object previous from the arc with this arcKey
	public SnArcInfo getPrevInArcFrom(String arcKey);

	// get the frist Outgoing ArcInfo object of the node with this nodeKey
	public SnArcInfo getFirstOutArcOf(String nodeKey);

	// get the last Outgoing ArcInfo object of the node with this nodeKey
	public SnArcInfo getLastOutArcOf(String nodeKey);

	// get the Outgoing ArcInfo object using its arcKey
	public SnArcInfo getOutArc(String arcKey);

	// get the Outgoing ArcInfo object next to the arc with this arcKey
	public SnArcInfo getNextOutArcFrom(String arcKey);

	// get the Outgoing ArcInfo object previous from the arc with this arcKey
	public SnArcInfo getPrevOutArcFrom(String arcKey);
	
	//remove a node
	public void removeSnNode(String nodeKey);
}
