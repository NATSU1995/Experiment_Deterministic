package SnNetwork;
import GenericNetwork.*;
import java.awt.*;

/**
 * Node information class.
 * @author HKUST Transportation & Logistics Lab
 * @version SnNetwork1.0
 */
public class SnNodeInfo extends BasicNodeInfo implements Comparable
{
	 /**Predecessor node in tree*/
	public SnNodeInfo pred;
	 /**Reverse thread of node*/
	public SnNodeInfo revThrd;
	 /**Forward thread of node*/
	public SnNodeInfo thread;
	/**Predecessor arc in tree*/
	public SnArcInfo  predLink;
	 /**TRUE if basis link in is down*/
	public boolean downPtr;
    /**Depth of node in basis tree*/
	public int    depth;
	/**Dual value at this node   */
	public double dualVal;
	/**Supply at this node   demand=-supply */
	public int    supply;
	/**Flags for various usages*/
	public int    flags;

        public double shortest_path_length =0;
        public String shortest_path_next ="";

	/**
	 * Creates a node.
	 * @param key   name of the node
	 * @param net   name of the net containing the node
	 */
	public SnNodeInfo(String k, String net)
	{
		super(k, net);
		pred =null;
	    revThrd=null;
        thread=null;
	    predLink=null;
	    downPtr=false;
	    depth=0;
	    dualVal=0;
	    supply=0;
	    flags=0;
	}
        public int compareTo( Object rhs )
        {
            if (shortest_path_length < ((SnNodeInfo)rhs).shortest_path_length) return -1;
            else if (shortest_path_length > ((SnNodeInfo)rhs).shortest_path_length) return 1;
            else return 0;
        }

}