package SnNetwork;
import GenericNetwork.*;
import java.util.*;
import java.applet.*;
import java.awt.*;
import java.io.*;

/**
 * A class providing all methods needed in network simplex algorithm.
 * @author HKUST Transportation & Logistics Lab
 * @version SnNetwork1.0
 */
public class SnNetwork extends GenericNetwork implements SnNetworkInterface
{
    private SnCandList cList;          /*The candidate list */
    private int pivots;    /* Number of pivots */
    private int degen;     /* Number of degenerate pivots */
	private double objfcn;    /* Objective function */
    public final int  SN_INTINFINITY=1000000; /* Infinite integer*/
	private final double SN_MARGIN=0.001;
	private   SnNodeInfo badNode;////Node;    // get the badnode of the network
    private   SnArcInfo  badArc;     // get the badarc  of the network


    final int SN_TREE_OK        =   0;
    final int SN_TREE_NO_ROOT   =   1;
    final int SN_TREE_MANY_ROOTS=   2;
    final int SN_TREE_BAD_PRED  =   3;
    final int SN_TREE_NO_TREE   =   4;

	final int SN_FLAGS_MARK_DELETE = 1;

	final int SN_FLOWS_OK         =   0;
	final int SN_FLOWS_UNBALANCED =   1;
	final int SN_FLOWS_BADBOUND   =   2;

	final int SN_DUALS_OK         =   0;
	final int SN_DUALS_NO_ROOT    =   1;

	/**
	 * A constructor, initializes the network.
	 */
	public SnNetwork()
	{
		super();
		pivots=0;
		degen=0;
		objfcn=0;
	}

	/**
	 * Gets the first NetInfo object in the Network.
	 */
	public SnNetInfo getFirstNet()
	{
		return (SnNetInfo)getGenericFirstNet();
	}

	/**
	* Gets the last NetInfo object in the Network.
	*/
	public SnNetInfo getLastNet()
	{
		return (SnNetInfo)getGenericLastNet();
	}

	/**
	 * Gets the NetInfo object using its netKey.
	 */
	public SnNetInfo getNet(String netKey)
	{
		return (SnNetInfo)getGenericNet(netKey);
	}

	/**Gets the NetInfo object next to the net with this netKey.*/
	public SnNetInfo getNextNetFrom(String netKey)
	{
		return (SnNetInfo)getGenericNextNetFrom(netKey);
	}

	/**Gets the NetInfo object previous from the net with this netKey.*/
	public SnNetInfo getPrevNetFrom(String netKey)
	{
		return (SnNetInfo)getGenericPrevNetFrom(netKey);
	}

	/**
	 * Gets the first NodeInfo object in the net with this netKey.
	 */
	public SnNodeInfo getFirstNodeIn(String netKey)
	{
		return (SnNodeInfo)getGenericFirstNodeIn(netKey);
	}

	/**
	  * Gets the last NodeInfo object in the net with this netKey.
	  */
	public SnNodeInfo getLastNodeIn(String netKey)
	{
		return (SnNodeInfo)getGenericLastNodeIn(netKey);
	}

	/**
	 * Gets the NodeInfo object using its nodeKey.
	 */
	public SnNodeInfo getNode(String nodeKey)
	{
		return (SnNodeInfo)getGenericNode(nodeKey);
	}

	/**
	 * Gets the NodeInfo object next to the node with this nodeKey.
	 */
	public SnNodeInfo getNextNodeFrom(String nodeKey)
	{
		return (SnNodeInfo)getGenericNextNodeFrom(nodeKey);
	}

	/**
	 * Gets the NodeInfo object previous from the node with this nodeKey.
	 */
	public SnNodeInfo getPrevNodeFrom(String nodeKey)
	{
		return (SnNodeInfo)getGenericPrevNodeFrom(nodeKey);
	}

	/**
	 * Gets the first ArcInfo object in the net with this netKey.
	 */
	public SnArcInfo getFirstArcIn(String netKey)
	{
		return (SnArcInfo)getGenericFirstArcIn(netKey);
	}

	/**
	 * Gets the last ArcInfo object in the net with this netKey.
	 */
	public SnArcInfo getLastArcIn(String netKey)
	{
		return (SnArcInfo)getGenericLastArcIn(netKey);
	}

	/**
	 * Gets the ArcInfo object using its arcKey.
	 */
	public SnArcInfo getArc(String arcKey)
	{
		return (SnArcInfo)getGenericArc(arcKey);
	}

	/**
	 * Gets the ArcInfo object next to the arc with this arcKey.
	 */
	public SnArcInfo getNextArcFrom(String arcKey)
	{
		return (SnArcInfo)getGenericNextArcFrom(arcKey);
	}

	/**
	 * Gets the ArcInfo object previous from the arc with this arcKey.
	 */
	public SnArcInfo getPrevArcFrom(String arcKey)
	{
		return (SnArcInfo)getGenericPrevArcFrom(arcKey);
	}

	/**
	 * Gets the first Incoming ArcInfo object of the node with this nodeKey.
	 */
	public SnArcInfo getFirstInArcOf(String nodeKey)
	{
		return (SnArcInfo)getGenericFirstInArcOf(nodeKey);
	}

	/**
	 * Gets the last Incoming ArcInfo object of the node with this nodeKey.
	 */
	public SnArcInfo getLastInArcOf(String nodeKey)
	{
		return (SnArcInfo)getGenericLastInArcOf(nodeKey);
	}

	/**
	 * Gets the Incoming ArcInfo object using its arcKey.
	 */
	public SnArcInfo getInArc(String arcKey)
	{
		return (SnArcInfo)getGenericInArc(arcKey);
	}

	/**
	 * Gets the Incoming ArcInfo object next to the arc with this arcKey.
	 */
	public SnArcInfo getNextInArcFrom(String arcKey)
	{
		return (SnArcInfo)getGenericNextInArcFrom(arcKey);
	}

	/**
	 * Gets the Incoming ArcInfo object previous from the arc with this arcKey.
	 */
	public SnArcInfo getPrevInArcFrom(String arcKey)
	{
		return (SnArcInfo)getGenericPrevInArcFrom(arcKey);
	}

	/**
	 * Gets the frist Outgoing ArcInfo object of the node with this nodeKey.
	 */
	public SnArcInfo getFirstOutArcOf(String nodeKey)
	{
		return (SnArcInfo)getGenericFirstOutArcOf(nodeKey);
	}

	/**
	 * Gets the last Outgoing ArcInfo object of the node with this nodeKey.
	 */
	public SnArcInfo getLastOutArcOf(String nodeKey)
	{
		return (SnArcInfo)getGenericLastOutArcOf(nodeKey);
	}

	/**
	 * Gets the Outgoing ArcInfo object using its arcKey.
	 */
	public SnArcInfo getOutArc(String arcKey)
	{
		return (SnArcInfo)getGenericOutArc(arcKey);
	}

	/**
	 * Gets the Outgoing ArcInfo object next to the arc with this arcKey.
	 */
	public SnArcInfo getNextOutArcFrom(String arcKey)
	{
		return (SnArcInfo)getGenericNextOutArcFrom(arcKey);
	}

	/**
	 * Gets the Outgoing ArcInfo object previous from the arc with this arcKey.
	 */
	public SnArcInfo getPrevOutArcFrom(String arcKey)
	{
		return (SnArcInfo)getGenericPrevOutArcFrom(arcKey);
	}

	/**
	 * Remove a node in the net.
	 */
	public void removeSnNode(String nodeKey)
	{
	    removeNode(nodeKey);
	}


/**
 * Sets the flags field to zero for all the nodes in the net.
 */
public void snUnmarkAllNodes (String netKey)
{
SnNodeInfo  tmpnode;    /* Temporary node */
tmpnode =getFirstNodeIn(netKey);
do
    {
    tmpnode.flags = 0;
    tmpnode = getNextNodeFrom(tmpnode.getKey());
    }
while (tmpnode != null);
}


/**
 * Marks a node for deletion by setting its flags value to SN_FLAGS_MARK_DELETE.
 */
public void snMarkDeleteNode(
  String  nodekey    /* key of an individual node */
)

{
getNode(nodekey).flags = SN_FLAGS_MARK_DELETE;
}

/**
 * Deletes all of the marked nodes from the net
 * Note that all arcs leading from the node are deleted, but
 * arcs that point to the node have to be deleted separately.
 * Also note that the basis tree structure is untouched and may be
 * invalid after these deletions.
 * If the node at the beginning of the net is marked for deletion,
 * the routine returns the next available node in the list, or NULL, if
 * there all nodes were deleted.  If the first node is not deleted, then
 * the routine returns a pointer to that node.
 */
public SnNodeInfo snDeleteMarkedNodes(
	String netKey     /* Pointer to net */
)

{
SnNodeInfo  tmpnode;    /* For looping through the nodes */
SnNodeInfo  nextnode;   /* Next node in list */
SnArcInfo   tmparc;     /* For looping through arc lists */
SnArcInfo   nextarc;    /* Next arc in list */
SnNodeInfo  result;     /* Result of this routine */
SnNodeInfo  lastnode;   /* Last node to examine */

result = getFirstNodeIn(netKey);
nextnode = getFirstNodeIn(netKey);
do
    {
    tmpnode = nextnode;
    if (tmpnode.flags == SN_FLAGS_MARK_DELETE)
        {
        /* First delete all of the arcs */
        tmparc = getFirstOutArcOf(tmpnode.getKey());
        while (tmparc != null)
            {
            nextarc = getNextOutArcFrom(tmparc.getKey());
            tmparc = nextarc;
            }
        /* Now remove the node from the linked list */
        nextnode = getNextNodeFrom(tmpnode.getKey());
        if (tmpnode == result) /* If deleting first node, set result */
            result = nextnode;
        if (nextnode == tmpnode)    /* If only one node */
            {
            result = null;  /* Then no result, and no linking */
            }
        else
            {
            SnNodeInfo tmpnodepre=getPrevNodeFrom(tmpnode.getKey());
            SnNodeInfo tmpnodeprenex=getNextNodeFrom(tmpnodepre.getKey());
            tmpnodeprenex= nextnode;

            SnNodeInfo nextnodepre=getPrevNodeFrom(nextnode.getKey());
            tmpnodepre=getPrevNodeFrom(tmpnode.getKey());
            nextnodepre = tmpnodepre;
            }
        }
    else    /* Node not deleted */

        nextnode = getNextNodeFrom(tmpnode.getKey());
    }
while (nextnode != null);

return(result);
}

/**
 * Sets the flags field to zero for all of the arcs in the net
 */
public void snUnmarkAllArcs(
	String netkey    /* Pointer to linked list of nodes */
)

{
SnNodeInfo  tmpnode;    /* Temporary node */
SnArcInfo   tmparc;     /* Temporary arc */

tmpnode = getFirstNodeIn(netkey);
do
    {
    tmparc = getFirstOutArcOf(tmpnode.getKey());
    while (tmparc != null)  /* Loop for each arc */
        {
        tmparc.flags = 0;
        tmparc = getNextOutArcFrom(tmparc.getKey());
        }
    tmpnode = getNextNodeFrom(tmpnode.getKey());
    }
while (tmpnode != null);
}

/**
 * Marks an arc for deletion by setting its flags value to SN_FLAGS_MARK_DELETE
 */
public void snMarkDeleteArc(
	SnArcInfo   somearc    /* Pointer to an individual arc */
)

{
somearc.flags = SN_FLAGS_MARK_DELETE;
}

/**
 * Deletes all arcs that have their flags field set to SN_FLAGS_MARK_DELETE.
 * Each arc is removed from its net.
 */
public void snDeleteMarkedArcs(
	String netKey /* Pointer to linked list of nodes */
)

{
SnNodeInfo  tmpnode;    /* For looping through the nodes */
SnArcInfo   tmparc;     /* For looping through the arcs */
SnArcInfo   nextarc;    /* Next arc to look at */
SnArcInfo   arcparent;  /* Parent of arc being deleted */
SnNodeInfo  tonode;     /* ptr to tonode of arc to be deleted */
SnArcInfo   toarc;      /* ptr to arc into tonode */
SnArcInfo   barcparent; /* ptr to parent arc */

tmpnode = getFirstNodeIn(netKey);
do
    {
    tmparc = getFirstOutArcOf(tmpnode.getKey());

    while (tmparc != null)
        {
        nextarc=getNextOutArcFrom(tmparc.getKey());
        if (tmparc.flags == SN_FLAGS_MARK_DELETE)
            {
            removeArc(tmparc.getKey());
                }

        tmparc = nextarc;   /* Go to next arc in list */
        }
    tmpnode = getNextNodeFrom(tmpnode.getKey());    /* Go to next node in list */
    }
while (tmpnode != null);
}

/************************************************************************/

public void snDeleteNetwork(
	String netKey /* Pointer to linked list of nodes */
)
{
removeNet(netKey);
}


/**
 * Checks to see that all of the nodes in the net
 * are in the spanning tree specified by the fields pred and predlink.
 * In addition, a consistency check is made to see that pred and
 * predlink correspond correctly.
 * The routine returns an error code, and possibly sets badNode to
 * point to a node with a problem.  Here are the possible returned
 * values:
 * SN_TREE_OK,SN_TREE_NO_ROOT, SN_TREE_MANY_ROOTS, SN_TREE_BAD_PRED, and SN_TREE_NO_TREE.
 * The value of the corresponding badNode is null, null, pointer to one of the roots,
 * pointer to node with bad predecessor node/link, and  pointer to a node that is in a cycle,respecitively.
 */


public SnNodeInfo snFindRoot(String netKey)

/* This routine finds the root of the basis tree.  Note that the search
 * is done backwards to make things fast, since there is a high
 * probability that the last node in the list is the root node.
 */

{
SnNodeInfo  rootnode;   /* Root node returned */
SnNodeInfo  tmpnode;    /* For looping through the list */

rootnode = null;
tmpnode = getFirstNodeIn(netKey);
do
    {
    if (tmpnode.pred == null)
        rootnode = tmpnode;
    tmpnode = getNextNodeFrom(tmpnode.getKey());
    }
while ((rootnode == null) && (tmpnode != null));

return(rootnode);
}


public int snCheckTree(String netKey)
{
SnNodeInfo  tmpnode;    /* Temporary node pointer */
int         count;      /* Which branch of the tree is being followed */
SnNodeInfo  treenode;   /* A node in the tree */
SnNodeInfo  rootnode;   /* The root node */
int         result;     /* Result of this routine */
/* First initialize flags to be zero */

tmpnode = getFirstNodeIn(netKey);
rootnode = null;
result   = SN_TREE_OK;
badNode = null;

do
    {
    tmpnode.flags = 0;
    if (tmpnode.pred == null)
        if (rootnode == null)
            rootnode = tmpnode;
        else
            {
            rootnode = tmpnode;
            result   = SN_TREE_MANY_ROOTS;
            badNode = rootnode;
            }
    tmpnode = getNextNodeFrom(tmpnode.getKey());
    }
while ((tmpnode != null) && (result == SN_TREE_OK));

/* Now check for no root node */
if (rootnode == null)
    result = SN_TREE_NO_ROOT;

if (result == SN_TREE_OK)   /* Still OK, so proceed */
    {
    /*Check that the root node has no predecessor link */
    if (rootnode.predLink != null)
        {
        result = SN_TREE_BAD_PRED;
        badNode = rootnode;
        }
    }

if (result == SN_TREE_OK)   /* Still OK, so proceed */
    /* Now check predecessors and predecessor links, making sure
     * that we don't hit a cycle.
     */
    {
    tmpnode = getFirstNodeIn(netKey);
    rootnode.flags = 1;    /* Mark rootnode as visited */
    count = 2;
    do
        {
        treenode = tmpnode;
        /* Loop over each node in a branch,
         * until hit a node already visited
         */
        while ((result == SN_TREE_OK) &&
               (treenode.flags == 0))
            {/* We know that treenode->pred is not null because the
              * first loop in this routine checked for the presence of
              * more than one node with pred equal to null
              */

              SnNodeInfo fromnode = getNode(treenode.predLink.getFromNodeKey());
              SnNodeInfo tonode   = getNode(treenode.predLink.getToNodeKey());

            if ((treenode.predLink == null) || /* if null, then error */
                /* OR not the case that either
                 *      fromnode is treenode, tonode is pred
                 *  OR  tonode   is treenode, fromnodes is pred
                 */
                (!(((fromnode == treenode) &&
                    (tonode == treenode.pred)) ||
                    ((tonode   == treenode) &&
                    (fromnode == treenode.pred)))))
                {
                result = SN_TREE_BAD_PRED;
                badNode = treenode;
                }
            else    /* Predecessor seems to be OK */
                {
                if (treenode.flags == count)   /* Visited on this branch */
                    {
                    result = SN_TREE_NO_TREE;
                    badNode = treenode;
                    }
                treenode.flags = count;
                }
            treenode = treenode.pred;  /* Go to the predecessor */
            }
        count++;
        tmpnode = getNextNodeFrom(tmpnode.getKey()); /* Check branch starting at next node */
        }
    while ((result == SN_TREE_OK) && (tmpnode != null));
    }

/* At this point, either everything OK, or result indicates error */
return(result);
}


public int snCheckFlows( String netKey )

/**
 * Checks that the net flow into and out of each node balances with the supply and demand fields.
 * In addition, it verifies that the flow on each arc is between 0 and its upper bound (inclusive).
 */
/* The possible return values are:
 *  Returned value          *badnode            *badarc
 *  --------------          --------            -------
 *  SN_FLOWS_OK             NULL                NULL
 *  SN_FLOWS_UNBALANCED     pointer to node     NULL
 *                          out of balance
 *  SN_FLOWS_BADBOUND       NULL                pointer to arc
 *                                              violating bounds
 *
 * Usage note:  The routine makes use of the field "flags" in each node,
 *              so that any prior value is destroyed.
 */

{
SnNodeInfo  tmpnode;    /* Pointer to node in linked list */
int         result;     /* Result of this routine */
SnArcInfo   tmparc;     /* Pointer to arc in linked list */
int         tmpflow;    /* Holder for temporary flow */

tmpnode = getFirstNodeIn(netKey);
result  = SN_FLOWS_OK;
badNode = null;
badArc  = null;

/* Initialize flags field to zero */
do
    {
    tmpnode.flags = 0;
    tmpnode = getNextNodeFrom(tmpnode.getKey());
    }
while (tmpnode != null);

/* Now loop through each nodes' arclist, checking each arc for validity,
 * and adding it to the running total.
 */

tmpnode = getFirstNodeIn(netKey);
do
    {
    tmparc = getFirstOutArcOf(tmpnode.getKey());
    while ((tmparc != null) && (result == SN_FLOWS_OK))
        {
        tmpflow = tmparc.flow;
        if ((tmpflow >= 0) && (tmpflow <= tmparc.uBound))
            {
            SnNodeInfo tmparc_fromnode =getNode(tmparc.getFromNodeKey());
            tmparc_fromnode.flags += tmpflow;
            SnNodeInfo tmparc_tonode = getNode(tmparc.getToNodeKey());
            tmparc_tonode.flags -= tmpflow;
            }
        else
            {
            result = SN_FLOWS_BADBOUND;
            badArc = tmparc;
            }
        tmparc = getNextOutArcFrom(tmparc.getKey());
        }
    tmpnode = getNextNodeFrom(tmpnode.getKey());
    }
while ((result == SN_FLOWS_OK) && (tmpnode != null));

if (result == SN_FLOWS_OK)  /* If still OK, check supply and demand. */
    {
        tmpnode = getFirstNodeIn(netKey);
        do
        {
            if (tmpnode.flags != tmpnode.supply)
            {
                result = SN_FLOWS_UNBALANCED;
                badNode = tmpnode;
            }
            tmpnode = getNextNodeFrom(tmpnode.getKey());
        }
        while ((result == SN_FLOWS_OK) && (tmpnode != null));
    }

return(result);
}


/**
 * Computes the dual values for the current basis tree.
 * It checks to see that the tree has a root, and returns an error
 * code if not.  It is assumed that the thread is correct and traverses
 * all of the nodes, ending at the root node.  The
 * possible return codes are: SN_DUALS_OK ( Everything OK, all duals calculated),
 * and SN_DUALS_NO_ROOT(Root node could not be found).
 */

public double snCalculateDuals(
	String netKey            /* Pointer to linked list of nodes */
)


{
SnNodeInfo  rootnode;   /* Root node */
SnNodeInfo  topnode;    /* Top node of link  */
double      result;     /* Result of this routine */
SnNodeInfo  botnode;    /* Bottom node of link */
SnArcInfo   predlink;   /* Predecessor link */
double         cost;       /* Cost of this link */

result = SN_DUALS_OK;
rootnode = snFindRoot(netKey);
if (rootnode == null)
    {
    result = SN_DUALS_NO_ROOT;
    }

if (result == SN_DUALS_OK)
    {
    rootnode.dualVal = 0;      /* Set dual value of root node */
    botnode = rootnode.thread; /* Start at thread */
    while (botnode != rootnode) /* Keep going until reach rootnode */
        {
        topnode  = botnode.pred;
        predlink = botnode.predLink;
        cost     = predlink.cost;
        if (botnode.downPtr)
            {
            botnode.dualVal = topnode.dualVal + cost;
            }
        else
            {
            botnode.dualVal = topnode.dualVal - cost;
            }
        botnode = botnode.thread;
        }
    }
return(result);
}


/**
 * Sets the field downptr for each of the nodes.
 * It is assumed that the predecessor nodes and links correspond.
 */
public void snSetDownPtr(
	String netKey             /* Linked list of nodes */
)

{
SnNodeInfo  tmpnode;    /* Temporary node */
SnNodeInfo  prednode;   /* Predecessor node */

tmpnode = getFirstNodeIn(netKey);
do
    {
    prednode = tmpnode.pred;
    if (prednode != null)   /* If not the root node */
        {

         SnNodeInfo correstmpnode=getNode(tmpnode.predLink.getFromNodeKey());

        if (tmpnode == correstmpnode) /* Pointing up */
            {
            tmpnode.downPtr = false;
            }
        else /* Must be pointing down */
            {
            tmpnode.downPtr = true;
            }
        }
    tmpnode = getNextNodeFrom(tmpnode.getKey());
    }
while (tmpnode != null);
}

/************************************************************************/

public void snCalculateDepth(
	String netKey   /* Pointer to linked list of nodes */
)

/**
 * Calculates the depth of each node in the basis tree.
 * It is assumed that the predecessor nodes and links have been
 * certified to be correct.  It is also assumed that there is a root
 * node.  It is also assumed that the thread has been properly set.
 */

{
SnNodeInfo  tmpnode;    /* Node we're calculating the depth of */
SnNodeInfo  rootnode;   /* The root node of the tree */

rootnode = snFindRoot(netKey);

/* Note -- assume that rootnode has been found */

rootnode.depth = 0;
tmpnode = rootnode.thread;
while (tmpnode != rootnode) /* Stop when returned to root */
    {
    tmpnode.depth = tmpnode.pred.depth + 1;
    tmpnode = tmpnode.thread;  /* Follow the thread */
    }
}

/**
 * Builds the thread from a previously constructed predecessor node and predecessor link structure.
 * It is assumed that the predecessor structure is correct and that there is exactly one root node.
 * First, the root node is found and all threads are set to NULL.
 * Then we start moving around the linked list of nodes at the node that follows the root node.
 * For each node that does not have the thread already set, we move up the branch towards the
 * root, setting the thread as we go up the tree.  If we find
 * a node with the thread already set, then  we set the thread
 * of the node we started at to point to the node which had the
 * thread set.
 */

public void snBuildThread(
	String  netKey   /* Pointer to linked list of nodes */
)


{
SnNodeInfo  rootnode;   /* Root node */
SnNodeInfo  tmpnode;    /* For looping over the nodes */
SnNodeInfo  parent;     /* Parent node */
SnNodeInfo  child;      /* Child node */
SnNodeInfo  nextnode;   /* Next node in the thread */

rootnode = null;
tmpnode = getFirstNodeIn(netKey);
do  /* Loop through all nodes, looking for root and setting thread to NULL */
    {
    if (tmpnode.pred == null)  /* If root, set it! */
        {
        rootnode = tmpnode;
        }
    tmpnode.thread = null;
    tmpnode = getNextNodeFrom(tmpnode.getKey());
    }
while (tmpnode != null);

/* Now we assume that we have the root node */

rootnode.thread = rootnode;
rootnode.revThrd = rootnode;

tmpnode = getNextNodeFrom(rootnode.getKey());
while (tmpnode != rootnode)
    {
    if (tmpnode.thread == null)    /* If thread not set */
        {
        child = tmpnode;
        parent = child.pred;   /* Get ready to move up the tree */
        while (parent.thread == null)  /* Parent has no thread */
            {
            parent.thread = child;     /* Parent point down to child */
            child.revThrd = parent;    /* Child point up to parent */
            child = parent;             /* Set the next child and then */
            parent = parent.pred;      /* move up the tree */
            }
        nextnode = parent.thread;
        tmpnode.thread = nextnode;     /* tmpnode will point to nextnode */
        nextnode.revThrd = tmpnode;    /* nextnode up to tmpnode */
        parent.thread = child;         /* Parent goes to child */
        child.revThrd = parent;        /* Child reverses to parent */
        }
    tmpnode = getNextNodeFrom(tmpnode.getKey()); /* Go to next node in list */
    if (tmpnode==null)
       {
        tmpnode=getFirstNodeIn(netKey);
       }
    }
}


	//calculate the objective function
	private double calcObjective(String netkey)
	{
	    double result;         /* The answer */
        SnNodeInfo  tmpnode;    /* Temporary node */
        SnArcInfo   tmparc;     /* Temporary arc */
        result = 0;
        tmpnode = getFirstNodeIn(netkey);
        do
        {
             tmparc = (SnArcInfo)getFirstOutArcOf(tmpnode.getKey());
             while (tmparc != null)
             {
                result += (tmparc.cost)*(tmparc.flow);
                tmparc = getNextOutArcFrom(tmparc.getKey());
             }
            tmpnode = getNextNodeFrom(tmpnode.getKey());
        }
        while (tmpnode != null);
        return(result);
    }



   /**
    * Sets up the bigM method.
    * A new node is added to the linked list of nodes.  This will precede the node nodes in
    * the list.  Links are created that connect each node in the network
    * to the artificial node.  These links have a cost of bigM.  An initial
    * feasible basis is created by moving all flow from supply nodes to demand
    * nodes through the new node using the artificial links.
    * The routine returns the address of the newly created node.
    */
    public SnNodeInfo bigMStart(String netkey, int bigM)


    {
    SnNodeInfo  artnode;    /* Artificial node */
    SnNodeInfo  tmpnode;    /* Pointer to node working on */
    SnArcInfo   tmparc;     /* New arc created from artificial */
    SnNodeInfo   tmpnextnode;/*Next node of the temp node*/
    SnNodeInfo   tmprevnode;/*Previous node of the temp node*/
    int         supdem;     /* Supply and demand of a node */
    String   artnodekey;  /*Key of artificial node*/
    String      tmparckey;  /* Key of temporary arc*/
    artnodekey="artificial";
    artnode = new SnNodeInfo(artnodekey,netkey);
    artnode.depth = 0;
    artnode.pred  = null;
    artnode.revThrd = null;
    artnode.predLink = null;
    artnode.dualVal  = 0;
    artnode.supply   = 0;
    artnode.downPtr  = false;
    addNode(artnodekey,netkey,artnode);
    tmpnode = getNextNodeFrom(artnode.getKey());
    if(tmpnode==null) tmpnode=getFirstNodeIn(netkey);
    artnode.thread = tmpnode;
    while (tmpnode != artnode)
             {
                //  if at the tail of the node list, next node is the first one
                if((tmpnextnode=getNextNodeFrom(tmpnode.getKey()))==null)  tmpnextnode= getFirstNodeIn(netkey);
                //if at the head of the node list, prev node is the last one
                if( (tmprevnode =getPrevNodeFrom(tmpnode.getKey()))==null) tmprevnode=getLastNodeIn(netkey);


                /* Initialize flow on existing links to zero. */
                 tmparc = (SnArcInfo)getFirstOutArcOf(tmpnode.getKey());
                while (tmparc != null)
                {
                    tmparc.flow = 0;
                    tmparc = getNextOutArcFrom(tmparc.getKey());
                }

                /* Create artificial link from artnode to each node and set up the new
                * links correctly for the basis.
                */
                tmpnode.depth = 0;
                tmpnode.pred = tmpnode.revThrd = tmpnode.thread = null;
                tmpnode.predLink = null;
                tmpnode.dualVal = 0;
                tmpnode.downPtr = false;
                supdem = tmpnode.supply;

                /* Now create the link, setting its dual, direction and flow */

                if (supdem > 0)    /* If a supply node */
                    {  /* Make link from tmpnode to artnode */
                        tmpnode.dualVal = -bigM;
                        tmpnode.downPtr = false;
                        tmparckey="art"+tmpnode.getKey();
                        tmparc = new SnArcInfo(tmparckey,netkey,tmpnode.getKey(),artnode.getKey());
                        addArc(tmparc);
                        tmparc.flow     = supdem;
                        tmparc.cost=bigM;
                        tmparc.uBound=SN_INTINFINITY;
                    }
                else /* (supdem <= 0) and hence a demand node */
                    { /* Make link from artnode to tmpnode */
                        tmpnode.dualVal = bigM;
                        tmpnode.downPtr = true;
                        tmparckey="art"+tmpnode.getKey();
                        tmparc = new SnArcInfo(tmparckey,netkey,artnode.getKey(),tmpnode.getKey());
                        addArc(tmparc);
                        tmparc.flow     = -supdem;
                        tmparc.cost=bigM;
                        tmparc.uBound=SN_INTINFINITY;

                    }
                tmpnode.pred    = artnode;
                tmpnode.revThrd =tmprevnode;
                tmpnode.thread  = tmpnextnode;
                tmpnode.depth   = 1;
                tmpnode.predLink=tmparc;
                tmpnode = tmpnextnode;
        }
        return(artnode);
   }
public void removeArtNode()
{
    removeSnNode("artificial");
}

    //find the entering arc
    private SnArcInfo findEnterArc(String netkey)
             /** This routine does one of two things:
            *  1) If the candidate list has returned maxgive entries so
            *     far, then rebuild the candidate list of at most maxcsize
            *     entries, starting the search at lastnode.
            *  2) Otherwise, return the best candidate among the candidates
            *     in the list.  If no such candidate, try to rebuild the
            *     list.
            */
     {
        SnArcInfo  bestarc;        /* Best arc found so far */
        double     bestcost;       /* Best cost found so far */
        SnArcInfo  bestnodearc;    /* Best arc for this node */
        double     bestnodecost;   /* Best cost for this node */
        SnArcInfo  tmparcp;       /* Looping variable */
        SnArcInfo  tmparc;        /* Arc considered for pricing */
        SnNodeInfo fromnode;      /* Node tmparc comes from */
        SnNodeInfo tonode;        /* Node tmparc comes to */
        int         i;            /* Loop counter */
        boolean     check;        /*Check if at the first position of the list*/
        int         csize;          /* Candidate list size */
        double      checkcost;      /* Check reduced cost */
        int         maxcsize;       /* Max. cand. list size */
        SnNodeInfo  nodetoprice;    /* Node to price */
        SnNodeInfo  whichnode;      /* Which node to look at */
        SnNodeInfo  stopnode;       /* Node to stop at when building new list */
        double       dualval;        /* Dual value for a node */

        bestarc = null;
        bestcost = 0;
        csize = cList.cSize;
        if (cList.numGiven <cList.maxGive)
            { /* If haven't given enough arcs, then find a good one. */
            check=cList.isSetFirst();
            for (i=0; i<csize; i++)
                {
                    tmparc= (SnArcInfo)cList.getElement();
                    fromnode= getNode(tmparc.getFromNodeKey());
                    tonode= getNode(tmparc.getToNodeKey());
                    if (tmparc.flow == 0)
                        {
                        checkcost =   fromnode.dualVal
                                + tmparc.cost
                                - tonode.dualVal;
                        }
                    else if (tmparc.flow == tmparc.uBound)
                        {
                        checkcost = - fromnode.dualVal
                                - tmparc.cost
                                + tonode.dualVal;
                        }
                    else
                        {
                        checkcost =  fromnode.dualVal
                                + tmparc.cost
                                - tonode.dualVal;
                        if (checkcost > SN_MARGIN) checkcost = -checkcost;
                        }
                    if (checkcost+SN_MARGIN < bestcost)
                        {
                        bestcost = checkcost;
                        bestarc = tmparc;
                        }
                    cList.moveForward();
                }
            }


             /* Rebuild candidate list if a pivot eligible arc has not been
              * found.
             */

         if (bestarc == null)
            {
            csize = 0;
            cList.clear();

            i = 0;
            maxcsize = cList.maxcSize;
            stopnode = cList.lastNode;
            if(stopnode==null) whichnode=getFirstNodeIn(netkey);
            else whichnode =  getNextNodeFrom(stopnode.getKey());
            do /* Do until candidate list full or have just priced stopnode */
                {
                if(whichnode==null) whichnode=getFirstNodeIn(netkey);
                nodetoprice = whichnode;
                bestnodecost = 0;
                bestnodearc = null;
                tmparc = (SnArcInfo)getFirstOutArcOf(nodetoprice.getKey());
                if (tmparc!=null)tonode= getNode(tmparc.getToNodeKey());
                else tonode=nodetoprice;
                dualval = nodetoprice.dualVal;
                while (tmparc != null)
                    {
                     tonode= getNode(tmparc.getToNodeKey());
                    if (tmparc.flow == 0)
                        {
                        checkcost =   dualval
                                    + tmparc.cost
                                    - tonode.dualVal;
                        }
                    else if (tmparc.flow == tmparc.uBound)
                        {
                        checkcost = - dualval
                                - tmparc.cost
                                + tonode.dualVal;
                        }
                    else
                        {
                        checkcost =   dualval
                                + tmparc.cost
                                - tonode.dualVal;
                        if (checkcost >SN_MARGIN) checkcost = -checkcost;
                        }
                    if (checkcost+SN_MARGIN < bestnodecost)
                        {
                        bestnodecost = checkcost;
                        bestnodearc = tmparc;
                        }
                    tmparc = getNextOutArcFrom(tmparc.getKey());//tmparc=tmparc.nextarc
                    }
                    if (bestnodearc != null)
                        {
                        cList.addBack(bestnodearc);
                        csize++;
                    if (bestnodecost < bestcost)
                        {
                        bestcost = bestnodecost;
                        bestarc  = bestnodearc;
                        }
                    }
                 whichnode = getNextNodeFrom(whichnode.getKey());
                }
            while ((csize<maxcsize) && (whichnode != stopnode));
            cList.lastNode = whichnode;
            cList.cSize = csize;
            cList.numGiven = 0;
            }
         cList.numGiven++;
         return(bestarc);
     }


    //find the leaving arc and update the tree;
    private void updateTree(String netkey, SnArcInfo enterArc)
    {
        SnNodeInfo  enterfrom;  /* From node of entering arc*/
        SnNodeInfo  enterto;    /* To node of entering arc*/
        int         fmin0;      /* The minimum flow over both sides */
        int         fmin1;      /* Min flow along one side */
        int         fmin2;      /* Min flow along second side */
        double      entercost;  /* Cost of entering arc*/
        double      enteredco;  /* Reduced cost of entering arc*/
        boolean     moreflag;   /* Flag to indicate direction */
        SnNodeInfo  k1node;     /* For direction of arc coming in */
        SnNodeInfo  k2node;     /* For direction of arc coming in */
        int         deep;       /* Difference of depth between two nodes */
        SnNodeInfo  tmpnode;    /* Temporary node */
        SnArcInfo   tmparc;     /* Temporary arc */
        int         i;          /* Loop counter */
        SnNodeInfo  blocknode0; /* Blocking node overall */
        SnArcInfo   blockarc0;  /* Blocking arc overall */
        SnNodeInfo  blocknode1=null; /* Blocking node side 1 */
        SnArcInfo   blockarc1=null;  /* Blocking arc side 1 */
        SnNodeInfo  blocknode2=null; /* Blocking node side 2 */
        SnArcInfo   blockarc2=null;  /* Blocking arc side 2 */
        double      dualadj;	/* Dual adjustment */
        boolean     side1flag;  /* Side one flag */
        boolean     treeflag;   /* Indicates if tree needs to be updated */
        int         tmpint;     /* Temporary integer */
        SnNodeInfo  blfrnode;   /* From node of link blocking */
        SnNodeInfo  bltonode;   /* To node of link blocking */
        boolean     down1flag;  /* Down1 flag */
        boolean     down2flag;  /* Down2 flag */
        int         depthb;     /* Depth of bottom */
        int         depthc;     /* Depth of top less bottom */
        SnNodeInfo  nlastnode;  /* For tracing up the tree */


        pivots++;

        /* Found pivot eligible link.  Now find the loop connecting
        * enterfrom (the from node) and enterto (the to node)
        */
        enterfrom = getNode(enterArc.getFromNodeKey());
        enterto   = getNode(enterArc.getToNodeKey());
        fmin1     = SN_INTINFINITY;
        fmin2     = SN_INTINFINITY;
        entercost = enterArc.cost;
        enteredco = enterfrom.dualVal + entercost - enterto.dualVal;

        /* If arc is at upper bound, flip direction of arc (as represented by
        * k1node and k2node.
        */
        if (!(moreflag = (enteredco > 0)))
            {
            k2node = enterto;
            k1node = enterfrom;
            }
        else
            {
            k1node = enterto;
            k2node = enterfrom;
            }

        /* Branch according to relative depth of the two nodes */

        if ((deep = (k1node.depth - k2node.depth)) <= 0)
            /* K2node deepest.  Bring K2 to same level as K1.
            * Also keep track of minimum restriction of flow, as long as
            * it's not a degenerate pivot.
            */
            {
            deep = -deep;
            tmpnode = k2node;
            for (i=1; (i<=deep) && (fmin2 > 0); i++)
                {
                tmparc = tmpnode.predLink;
                if (tmpnode.downPtr)
                    {
                    if (tmparc.flow < fmin2)
                        {
                        blockarc2 = tmparc;
                        blocknode2 = tmpnode;
                        fmin2 = tmparc.flow;
                        }
                    }
                else
                    {
                    tmpint = tmparc.uBound - tmparc.flow;
                    if (tmpint <= fmin2)
                        {
                        blockarc2 = tmparc;
                        blocknode2 = tmpnode;
                        fmin2 = tmpint;
                        }
                    }
                tmpnode = tmpnode.pred;    /* Move up the tree */
                }
            blfrnode = k1node;      /* Record restricting link */
            bltonode = tmpnode;
            }
        else  /* Deep is greater than 0 no matter what */
            { /* K1 deepest.  Bring K1 to same depth as K2
              * Also determine restriction of flow, but stop if degenerate
              * pivot.
              */
            tmpnode = k1node;
            for (i=1; (i<=deep) && (fmin1 > 0); i++)
                {
                tmparc = tmpnode.predLink;
                if (!(tmpnode.downPtr))
                    {
                    if (tmparc.flow <= fmin1)
                        {
                        blockarc1 = tmparc;
                        blocknode1 = tmpnode;
                        fmin1 = tmparc.flow;
                        }
                    }
                else
                    {
                    tmpint = tmparc.uBound - tmparc.flow;
                    if (tmpint < fmin1)
                        {
                        blockarc1 = tmparc;
                        blocknode1 = tmpnode;
                        fmin1 = tmpint;
                        }
                    }
                tmpnode = tmpnode.pred;    /* Move up the tree */
                }
            blfrnode = tmpnode;     /* Record restricting link */
            bltonode = k2node;
            }
        /* Move up in parallel branches until reach the same node.
        * Provided these are not parallel branches.
        */
        while ((blfrnode != bltonode) && (fmin1 > 0) && (fmin2 > 0))
            { /* Check side 2 */
            tmparc = bltonode.predLink;
            tmpint = tmparc.flow;
            if (!(bltonode.downPtr)) tmpint = tmparc.uBound - tmpint;
            if (tmpint < fmin2)
                {
                blockarc2 = tmparc;
                blocknode2 = bltonode;
                fmin2 = tmpint;
                }
            /* Update side 1 */
            bltonode = bltonode.pred;
            tmparc = blfrnode.predLink;
            tmpint = tmparc.flow;
            if (blfrnode.downPtr) tmpint = tmparc.uBound - tmpint;
            if (tmpint <= fmin1)
                {
                blockarc1 = tmparc;
                blocknode1 = blfrnode;
                fmin1 = tmpint;
                }
            blfrnode = blfrnode.pred;  /* Move up the tree */
            }
        /* Compare min flow over each branch.  Upward point arc (fmin2)
        *  wins in case of tie over downward point arc (fmin1)
        */
        if (side1flag = (fmin1 < fmin2))
            {   /* Side 1 is constricting */
            fmin0 = fmin1;
            blocknode0 = blocknode1;
            blockarc0 = blockarc1;
            }
        else
            {   /* Side 2 is constricting */
            fmin0 = fmin2;
            blocknode0 = blocknode2;
            blockarc0 = blockarc2;
            }
        /* Check incoming link for possible non-pivot.  Flow can't be larger
        * than the upper bound of the incoming link less it's current
        * flow if we're increasing the flow.  If decreasing the flow, then
        * it can't be larger than the flow itself.  If link goes to one of
        * its bounds, then don't change the basis tree.
        */
        if (moreflag)
            tmpint = enterArc.flow;
        else
            tmpint = enterArc.uBound - enterArc.flow;
        treeflag = true;
        if (fmin0 > tmpint)
            {
            fmin0 = tmpint;
            treeflag = false;
            }
        /* Move flow around cycle by pushing flow from bltonode to enterfrom.
        * (Side 1)
        */
        if (fmin0 > 0)
            {
            tmpnode = k1node;
            while (tmpnode != bltonode)
                {
                tmparc = tmpnode.predLink;
                if (tmpnode.downPtr)   /* If link points down, add flow. */
                    tmparc.flow += fmin0;
                else                    /* else link points up, so subtract */
                    tmparc.flow -= fmin0;
                tmpnode = tmpnode.pred;
            }
            /* Now do side 2 flow from enterto to bltonode */
            tmpnode = k2node;
            while (tmpnode != bltonode)
                {
                tmparc = tmpnode.predLink;
                if (tmpnode.downPtr)   /* If link points down, subtract flow */
                    tmparc.flow -= fmin0;
                else                    /* else link points up, so add flow */
                    tmparc.flow += fmin0;
                tmpnode = tmpnode.pred;
                }

            /* Now add flow to new arc and update the objective function */
            if (moreflag) fmin0 = -fmin0;
            enterArc.flow += fmin0;
            }
        else
            degen++;    /* Degenerate pivot */
        /* If tree is false, don't change the tree.  If true, change the tree.
        */
        if (treeflag)
            {
            /* Begin updating of predecessor and thread.
            * First determine which node (of enterfrom and enterto)
            * will become the predecessor.  Call this one blocknode1 and the
            * other blocknode2.
            */
            if (!side1flag)
                {   /* Node k1node is top */
                    blocknode1 = k1node;
                    blocknode2 = k2node;
                    down1flag = blocknode2.downPtr;
                    blocknode2.downPtr = true;
                }
            else
                {   /* Node k2node is top */
                blocknode2 = k1node;
                blocknode1 = k2node;
                down1flag = blocknode2.downPtr;
                blocknode2.downPtr = false;
                entercost = -entercost;
                }

            /* Now calculate dual across the link */
            if (moreflag)
                {
                entercost = -entercost;
                blocknode2.downPtr = !(blocknode2.downPtr);
                }
            dualadj = blocknode1.dualVal + entercost - blocknode2.dualVal;
            while (blocknode2 != null)
                {
                depthb = blocknode2.depth;
                depthc = blocknode1.depth + 1 - depthb;

                /* Now update all successors of blocknode2 */
                tmpnode = blocknode2;
                do
                    {
                    nlastnode = tmpnode;
                    tmpnode.dualVal += dualadj;
                    tmpnode.depth += depthc;
                    tmpnode = tmpnode.thread;
                    }
                while (tmpnode.depth > depthb);
                /* Update thread from blocknode1 */
                k1node = nlastnode.thread;
                k2node = blocknode2.revThrd;
                k2node.thread = k1node;
                k1node.revThrd = k2node;
                nlastnode.thread = blocknode1.thread;
                blocknode1.thread = blocknode2;
                nlastnode.thread.revThrd = nlastnode;
                blocknode2.revThrd = blocknode1;
                /* Store predecessor of blocknode2 and update predecessor */
                blockarc1 = blocknode2.predLink;
                k1node = blocknode2.pred;
                blocknode2.pred = blocknode1;
                blocknode2.predLink = enterArc;
                /* Check if link into blocknode2 is to be cut */
                if ((blocknode2 != blocknode0) && (blocknode1 != blocknode0))
                    {
                    blocknode1 = blocknode2;
                    blocknode2 = k1node;
                    enterArc  = blockarc1;
                    /* Flip orientation of link */
                    down2flag  = blocknode2.downPtr;
                    blocknode2.downPtr = !down1flag ;
                    down1flag  = down2flag;
                    }
                else    /* Finished so cut the last link */
                    blocknode2 = null;
                }
            }
        /* End of main loop so find the next pivot eligible arc */
        }

    /**
     * Network Simplex algorithm.
     * Finds the minimum cost network flow.
     * @param netkey    name of the net you want to find minimum cost flow.
     * @param clistsize length of the candidate list
     * @param cyclelen  length of the circle for pricing
     */
    public void snAlgorithm(String netkey,int clistsize,int cyclelen)
    {
        SnArcInfo enterarc;

        cList=new SnCandList(clistsize,cyclelen);
        while(((enterarc=findEnterArc(netkey))!=null)&&(degen<1000000))
            {
                updateTree(netkey, enterarc);
            }
        objfcn=calcObjective(netkey);
    }


   /**
    * Gets the number of pivoting.
    */
   public int getPivots()
   {
    return pivots;
   }
   /**
    * Gets the number of degenerate.
    */
   public int getDegen()
   {
    return degen;
   }
   /**
    * Gets the total cost of the network flow.
    */
   public double getObjfcn()
   {
    return objfcn;
   }
   /**
    * Gets the node with problem after check the tree.
    */
  public SnNodeInfo getBadNode()
  {
    return badNode;
  }
  /**
    * Gets the arc with problem after check the flow.
    */
  public SnArcInfo  getBadArc()
  {
    return badArc;
  }
  public void setInitial(){
      removeArtNode();
      pivots =0;
      degen =0;
      objfcn =0;
      badArc = null;
      badNode = null;
      cList = null;
  }

  public double getCostOfNode(String nodeKey){
      SnArcInfo arc = this.getFirstOutArcOf(nodeKey);
      double cost=0;
      while( arc !=null){
          cost+=arc.cost*arc.flow;
          arc = this.getNextOutArcFrom(arc.getKey());
      }
      return cost;
  }

  public double getStageOneCost(){
      double cost=0;
      SnNodeInfo node = this.getFirstNodeIn("N001");
      while(node !=null){
        SnArcInfo arc = this.getFirstInArcOf(node.getKey());
        if( arc ==null){
            cost+=this.getCostOfNode(node.getKey());
        }
        node = this.getNextNodeFrom(node.getKey());

      }

      return cost;
  }

  public double getInFlowOf(String nodeKey){
      SnArcInfo arc = this.getFirstInArcOf(nodeKey);
      double s=0;
      while( arc !=null){
          s+=arc.flow;
          arc = this.getNextInArcFrom(arc.getKey());
      }
      return s;
  }
public String getFlowInfoOf(String nodeKey,int numLocation){
      SnArcInfo arc = this.getFirstOutArcOf(nodeKey);
      String str= new String();
      while( arc !=null){
            if(arc.uBound>=SN_INTINFINITY){
                arc = arc = this.getNextOutArcFrom(arc.getKey());
                continue;
            }
            String nKey= arc.getFromNodeKey();
            double cost = precision(arc.cost,100);
            int n= Integer.parseInt(nKey.substring(1));
            int t= (n-1)/numLocation;
            int l= n-t*numLocation;
            nKey = arc.getToNodeKey();
            n= Integer.parseInt(nKey.substring(1));
            int tt= (n-1)/numLocation;
            int ll= n-tt*numLocation;
            int cap = arc.uBound;
            double flow = precision(arc.flow,100);
            str+="N_"+l+"_"+t+"_"+"0"+"\t"+"N_"+ll+"_"+tt+"_"+"0"+"\t"+cost+"\t"+cap+"\t"+flow+"\n";

            arc = this.getNextOutArcFrom(arc.getKey());
      }
      return str;
  }

public String getFlowInfoOf(String nodeKey,int numLocation,String s){
      SnArcInfo arc = this.getFirstOutArcOf(nodeKey);
      String str= new String();
      while( arc !=null){
            if(arc.uBound>=SN_INTINFINITY){
                arc = arc = this.getNextOutArcFrom(arc.getKey());
                continue;
            }
            String nKey= arc.getFromNodeKey();
            double cost = precision(arc.cost,100);
            int n= Integer.parseInt(nKey.substring(1));
            int t= (n-1)/numLocation;
            int l= n-t*numLocation;
            //nKey = arc.getToNodeKey();
            //n= Integer.parseInt(nKey.substring(1));
            //int tt= (n-1)/numLocation;
            int tt=t+1;
            //int ll= n-tt*numLocation;
            int ll=l;
            int cap = arc.uBound;
            double flow = precision(arc.flow,100);
            str+="N_"+l+"_"+t+"_"+"0"+"\t"+"N_"+ll+"_"+tt+"_"+"-1"+"\t"+cost+"\t"+cap+"\t"+flow+"\n";

            arc = this.getNextOutArcFrom(arc.getKey());
      }
      return str;
  }

      private double precision(double d,int K){
             int temp=(int)(d*K+0.4999);
             d=((double)temp)/K;
             return d;
     }
}
