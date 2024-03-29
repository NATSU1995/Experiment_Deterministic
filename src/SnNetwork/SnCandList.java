package SnNetwork;
import Util.*;
import GenericNetwork.*;
import java.awt.*;

/**
 * List of candidate entering arcs.
 * @author HKUST Transportation & Logistics Lab
 * @version SnNetwork1.0
 */
public class SnCandList extends DblLinkList
{
    SnNodeInfo lastNode; //Address of last node tested
    int cSize;   //Size of candidate list
    int maxcSize; //Maximum size of list
    int maxGive;  //Max number of calls before rebuild
    int numGiven; //Number of arcs given since rebuild

    /**
     * Creates a Candidate List.
     * @param clistsize the length of the list
     * @param cyclelen  the length of the circle for pricing
     */
    public SnCandList(int clistsize, int cyclelen)
    {
       super();
       lastNode=null;
       cSize=0;
       maxcSize=cyclelen;
       numGiven=cyclelen;

     }


}