package MyNetwork;

import GenericNetwork.*;
//import DataStructures.*;
import Util.*;
import java.io.*;
import java.util.*;
/**
 * Title:        Find the k shortest paths with recourse
 * Description:  Find the k shortest paths with recourse.
 * Method:  labelSetting  getTopologicalOrder
 * Copyright:    Copyright (c) 2002
 * Company:      TLL
 * @author Seating
 * @version 1.0
 */

public class Network extends GenericNetwork{
/**
         * Constructs a new empty network object with an empty net list.
         * This network consists of five Hashtables for net, node,
         * arc, in-arc and out-arc for quick access.
         */
public int N=0;
public int T=0;

        public Network()
        {
                super();

        }
   //   public void setOutputFile(String outFile){
    //     OUTPUTFILE=outFile;
   //   }

        /**
         * Returns the first net object in the net list of the network.
         * It returns null if no net in the network.
         * @return			the net object as NetInfo
         * @see				#getLastNet
         * @see				#getNet
         * @see				#getNextNetFrom
         * @see				#getPrevNetFrom
         */
        public NetInfo getFirstNet()
        {
                return (NetInfo)getGenericFirstNet();
        }

        /**
         * Returns the last net object in the net list of the network.
         * It returns null if no net in the network.
         * @return			the net object as NetInfo
         * @see				#getFirstNet
         * @see				#getNet
         * @see				#getNextNetFrom
         * @see				#getPrevNetFrom
         */
        public NetInfo getLastNet()
        {
                return (NetInfo)getGenericLastNet();
        }

        /**
         * Returns the net object with this key in the net list of the network.
         * It returns null if no net with this key.
         * @param netKey	the key of the net
         * @return			the net object as NetInfo
         * @see				#getFirstNet
         * @see				#getLastNet
         * @see				#getNextNetFrom
         * @see				#getPrevNetFrom
         */
        public NetInfo getNet(String netKey)
        {
                return (NetInfo)getGenericNet(netKey);
        }

        /**
         * Returns the net object next to the net with this key
         * in the net list of the network.
         * It returns null if no net with this key or no net next to it.
         * @param netKey	the key of the net
         * @return			the net object as NetInfo
         * @see				#getFirstNet
         * @see				#getLastNet
         * @see				#getNet
         * @see				#getPrevNetFrom
         */
        public NetInfo getNextNetFrom(String netKey)
        {
                return (NetInfo)getGenericNextNetFrom(netKey);
        }

        /**
         * Returns the net object previous from the net with this key
         * in the net list of the network.
         * It returns null if no net with this key or no net previous from it.
         * @param netKey	the key of the net
         * @return			the net object as NetInfo
         * @see				#getFirstNet
         * @see				#getLastNet
         * @see				#getNet
         * @see				#getNextNetFrom
         */
        public NetInfo getPrevNetFrom(String netKey)
        {
                return (NetInfo)getGenericPrevNetFrom(netKey);
        }

        /**
         * Returns the first node object in the net with this key
         * in the network.
         * It returns null if no net with this key or no node in this net.
         * @param netKey	the key of the net
         * @return			the node object as NodeInfo
         * @see				#getLastNodeIn
         * @see				#getNode
         * @see				#getNextNodeFrom
         * @see				#getPrevNodeFrom
         */
        public NodeInfo getFirstNodeIn(String netKey)
        {
                return (NodeInfo)getGenericFirstNodeIn(netKey);
        }

        /**
         * Returns the last node object in the net with this key
         * in the network.
         * It returns null if no net with this key or no node in this net.
         * @param netKey	the key of the net
         * @return			the node object as NodeInfo
         * @see				#getFirstNodeIn
         * @see				#getNode
         * @see				#getNextNodeFrom
         * @see				#getPrevNodeFrom
         */
        public NodeInfo getLastNodeIn(String netKey)
        {
                return (NodeInfo)getGenericLastNodeIn(netKey);
        }

        /**
         * Returns the node object with this key in the network.
         * It returns null if no node with this key.
         * @param nodeKey	the key of the node
         * @return			the node object as NodeInfo
         * @see				#getFirstNodeIn
         * @see				#getLastNodeIn
         * @see				#getNextNodeFrom
         * @see				#getPrevNodeFrom
         */
        public NodeInfo getNode(String nodeKey)
        {
                return (NodeInfo)getGenericNode(nodeKey);
        }

        /**
         * Returns the node object next to the node with this key
         * in the node list of the network.
         * It returns null if no node with this key or no node next to it.
         * @param nodeKey	the key of the node
         * @return			the node object as NodeInfo
         * @see				#getFirstNodeIn
         * @see				#getLastNodeIn
         * @see				#getNode
         * @see				#getPrevNodeFrom
         */
        public NodeInfo getNextNodeFrom(String nodeKey)
        {
                return (NodeInfo)getGenericNextNodeFrom(nodeKey);
        }

        /**
         * Returns the node object previous from the node with this key
         * in the node list of the network.
         * It returns null if no node with this key or no node previous from it.
         * @param nodeKey	the key of the node
         * @return			the node object as NodeInfo
         * @see				#getFirstNodeIn
         * @see				#getLastNodeIn
         * @see				#getNode
         * @see				#getNextNodeFrom
         */
        public NodeInfo getPrevNodeFrom(String nodeKey)
        {
                return (NodeInfo)getGenericPrevNodeFrom(nodeKey);
        }

        /**
         * Returns the first arc object in the net with this key
         * in the network.
         * It returns null if no net with this key or no arc in this net.
         * @param netKey	the key of the arc
         * @return			the arc object as ArcInfo
         * @see				#getLastArcIn
         * @see				#getArc
         * @see				#getNextArcFrom
         * @see				#getPrevArcFrom
         */
        public ArcInfo getFirstArcIn(String netKey)
        {
                return (ArcInfo)getGenericFirstArcIn(netKey);
        }

        /**
         * Returns the last arc object in the net with this key
         * in the network.
         * It returns null if no net with this key or no arc in this net.
         * @param netKey	the key of the arc
         * @return			the arc object as ArcInfo
         * @see				#getFirstArcIn
         * @see				#getArc
         * @see				#getNextArcFrom
         * @see				#getPrevArcFrom
         */
        public ArcInfo getLastArcIn(String netKey)
        {
                return (ArcInfo)getGenericLastArcIn(netKey);
        }

        /**
         * Returns the arc object with this key in the network.
         * It returns null if no arc with this key.
         * @param arcKey	the key of the arc
         * @return			the arc object as ArcInfo
         * @see				#getFirstArcIn
         * @see				#getLastArcIn
         * @see				#getNextArcFrom
         * @see				#getPrevArcFrom
         */
        public ArcInfo getArc(String arcKey)
        {
                return (ArcInfo)getGenericArc(arcKey);
        }

        /**
         * Returns the arc object next to the arc with this key
         * in the arc list of the network.
         * It returns null if no arc with this key or no arc next to it.
         * @param arcKey	the key of the arc
         * @return			the arc object as ArcInfo
         * @see				#getFirstArcIn
         * @see				#getLastArcIn
         * @see				#getArc
         * @see				#getPrevArcFrom
         */
        public ArcInfo getNextArcFrom(String arcKey)
        {
                return (ArcInfo)getGenericNextArcFrom(arcKey);
        }

        /**
         * Returns the arc object previous from the arc with this key
         * in the arc list of the network.
         * It returns null if no arc with this key or no arc previous from it.
         * @param arcKey	the key of the arc
         * @return			the arc object as ArcInfo
         * @see				#getFirstArcIn
         * @see				#getLastArcIn
         * @see				#getArc
         * @see				#getNextArcFrom
         */
        public ArcInfo getPrevArcFrom(String arcKey)
        {
                return (ArcInfo)getGenericPrevArcFrom(arcKey);
        }

        /**
         * Returns the first in-arc object of the node with this key
         * in the network.
         * It returns null if no node with this key or no arc entering into it.
         * @param nodeKey	the key of the node
         * @return			the arc object as ArcInfo
         * @see				#getLastInArcOf
         * @see				#getInArc
         * @see				#getNextInArcFrom
         * @see				#getPrevInArcFrom
         */
        public ArcInfo getFirstInArcOf(String nodeKey)
        {
                return (ArcInfo)getGenericFirstInArcOf(nodeKey);
        }

        /**
         * Returns the last in-arc object of the node with this key
         * in the network.
         * It returns null if no node with this key or no arc entering into it.
         * @param nodeKey	the key of the node
         * @return			the arc object as ArcInfo
         * @see				#getFirstInArcOf
         * @see				#getInArc
         * @see				#getNextInArcFrom
         * @see				#getPrevInArcFrom
         */
        public ArcInfo getLastInArcOf(String nodeKey)
        {
                return (ArcInfo)getGenericLastInArcOf(nodeKey);
        }

        /**
         * Returns the in-arc object with this key in the network.
         * It returns null if no in-arc with this key.
         * @param arcKey	the key of the arc
         * @return			the arc object as ArcInfo
         * @see				#getFirstInArcOf
         * @see				#getLastInArcOf
         * @see				#getNextInArcFrom
         * @see				#getPrevInArcFrom
         */
        public ArcInfo getInArc(String arcKey)
        {
                return (ArcInfo)getGenericInArc(arcKey);
        }

        /**
         * Returns the in-arc object next to the in-arc with this key
         * in the network.
         * It returns null if no in-arc with this key or no in-arc next to it.
         * @param arcKey	the key of the arc
         * @return			the arc object as ArcInfo
         * @see				#getLastInArcOf
         * @see				#getFirstInArcOf
         * @see				#getInArc
         * @see				#getPrevInArcFrom
         */
        public ArcInfo getNextInArcFrom(String arcKey)
        {
                return (ArcInfo)getGenericNextInArcFrom(arcKey);
        }

        /**
         * Returns the in-arc object previous from the in-arc with this key
         * in the network.
         * It returns null if no in-arc with this key or no in-arc previous from it.
         * @param arcKey	the key of the arc
         * @return			the arc object as ArcInfo
         * @see				#getLastInArcOf
         * @see				#getFirstInArcOf
         * @see				#getInArc
         * @see				#getNextInArcFrom
         */
        public ArcInfo getPrevInArcFrom(String arcKey)
        {
                return (ArcInfo)getGenericPrevInArcFrom(arcKey);
        }

        /**
         * Returns the first out-arc object of the node with this key
         * in the network.
         * It returns null if no node with this key or no arc going out from it.
         * @param nodeKey	the key of the node
         * @return			the arc object as ArcInfo
         * @see				#getLastOutArcOf
         * @see				#getOutArc
         * @see				#getNextOutArcFrom
         * @see				#getPrevOutArcFrom
         */
        public ArcInfo getFirstOutArcOf(String nodeKey)
        {
                return (ArcInfo)getGenericFirstOutArcOf(nodeKey);
        }

        /**
         * Returns the last out-arc object of the node with this key
         * in the network.
         * It returns null if no node with this key or no arc going out from it.
         * @param nodeKey	the key of the node
         * @return			the arc object as ArcInfo
         * @see				#getFirstOutArcOf
         * @see				#getOutArc
         * @see				#getNextOutArcFrom
         * @see				#getPrevOutArcFrom
         */
        public ArcInfo getLastOutArcOf(String nodeKey)
        {
                return (ArcInfo)getGenericLastOutArcOf(nodeKey);
        }

        /**
         * Returns the out-arc object with this key in the network.
         * It returns null if no out-arc with this key.
         * @param nodeKey	the key of the node
         * @return			the arc object as ArcInfo
         * @see				#getFirstOutArcOf
         * @see				#getLastOutArcOf
         * @see				#getNextOutArcFrom
         * @see				#getPrevOutArcFrom
         */
        public ArcInfo getOutArc(String arcKey)
        {
                return (ArcInfo)getGenericOutArc(arcKey);
        }

        /**
         * Returns the out-arc object next to the out-arc with this key
         * in the network.
         * It returns null if no out-arc with this key or no out-arc next to it.
         * @param arcKey	the key of the arc
         * @return			the arc object as ArcInfo
         * @see				#getFirstOutArcOf
         * @see				#getLastOutArcOf
         * @see				#getOutArc
         * @see				#getPrevOutArcFrom
         */
        public ArcInfo getNextOutArcFrom(String arcKey)
        {
                return (ArcInfo)getGenericNextOutArcFrom(arcKey);
        }

        /**
         * Returns the out-arc object next to the out-arc with this key
         * in the network.
         * It returns null if no out-arc with this key or no out-arc previous from it.
         * @param arcKey	the key of the arc
         * @return			the arc object as ArcInfo
         * @see				#getFirstOutArcOf
         * @see				#getLastOutArcOf
         * @see				#getOutArc
         * @see				#getPrevOutArcFrom
         */
        public ArcInfo getPrevOutArcFrom(String arcKey)
        {
                return (ArcInfo)getGenericPrevOutArcFrom(arcKey);
        }

        /**
         *
         */
        public void buildNetworkFrom(String netFile,String nodeFile,String arcFile) throws FileNotFoundException{
            readNetFile(netFile);
            readNodeFile(nodeFile);
            readArcFile(arcFile);
        }
          /**
         * Reads the information in the netFile and creates
         * the net objects to store the data. The net objects
         * are also connected in the network structure.
         * @param netFile		the file name stored in String
         * @see					#readNodeFile
         * @see					#readArcFile
         */
  private void readNetFile(String netFile){
     try
        {
         InputStream ins = new FileInputStream(netFile);
         DataInputStream inFile = new DataInputStream(ins);
         String s = inFile.readLine();
         int numNet = Integer.parseInt(s);
         // read the Field line
         s = inFile.readLine();
         for (int i=0; i<numNet; i++){
                s = inFile.readLine();
                StringTokenizer st = new StringTokenizer(s);
                String netKey = st.nextToken();
                NetInfo netInfo = new NetInfo(netKey);
                addNet(netInfo);
             }
         ins.close();
        }
        catch(IOException e)
        {
        }
  }
  /**
         * Reads the information in the nodeFile and creates
         * the node objects to store the data. The node objects
         * are also connected in the network structure.
         * @param nodeFile		the file name stored in String
         * @see					#readNetFile
         * @see					#readArcFile
         */
  private void readNodeFile(String nodeFile){
     try
        {
        InputStream ins = new FileInputStream(nodeFile);
        DataInputStream inFile = new DataInputStream(ins);
        String s = inFile.readLine();
        StringTokenizer st = new StringTokenizer(s);
        int numNode = Integer.parseInt(st.nextToken());
        this.N = Integer.parseInt(st.nextToken());
        this.T = Integer.parseInt(st.nextToken());
        // read the Field line
        int index =0;
        s = inFile.readLine();
        for (int i=0; i<numNode; i++){
                s = inFile.readLine();
                st = new StringTokenizer(s);
                String nodeKey = st.nextToken();
                String netKey = st.nextToken();
                double supply =Double.parseDouble(st.nextToken());
                double demand =Double.parseDouble(st.nextToken());
                double shortage =Double.parseDouble(st.nextToken());
                double overage =Double.parseDouble(st.nextToken());
                NodeInfo nodeInfo = new NodeInfo(nodeKey, netKey);
                nodeInfo.supply = supply;
                nodeInfo.demand = demand;
                nodeInfo.shortage_cost = shortage;
                nodeInfo.overage_cost = overage;
                addNode(nodeKey, netKey, nodeInfo);
          }
        ins.close();
      }
        catch(IOException e)
        {
        }
  }
  /**
         * Reads the information in the arcFile and creates
         * the arc objects to store the data. The arc objects
         * are also connected in the network structure.
         * @param arcFile		the file name stored in String
         * @see					#readNetFile
         * @see					#readNodeFile
         */
  private void readArcFile(String arcFile){
     try
        {
        InputStream ins = new FileInputStream(arcFile);
        DataInputStream inFile = new DataInputStream(ins);
        String s = inFile.readLine();
        s = inFile.readLine();
        while(s != null){
                StringTokenizer st = new StringTokenizer(s,"\t");
                String arcKey = st.nextToken();
                String netKey = st.nextToken();
                String fromNodeKey = st.nextToken();
                String toNodeKey = st.nextToken();
                double cost =Double.parseDouble(st.nextToken());
                double low =Double.parseDouble(st.nextToken());
                double up =Double.parseDouble(st.nextToken());
                ArcInfo arcInfo = new ArcInfo(arcKey, netKey, fromNodeKey, toNodeKey);
                arcInfo.cost = cost;
                arcInfo.cap_low = low;
                arcInfo.cap_up = up;
                addArc(arcKey, netKey, fromNodeKey, toNodeKey, arcInfo);
                s = inFile.readLine();
           }
        ins.close();
    }
    catch(IOException e)
    {
    }
  }
}
