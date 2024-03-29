package SnNetwork;

import GenericNetwork.*;
import java.applet.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
public class SnDisplay extends Applet implements ItemListener, WindowListener
{
    private SnNetwork           snNetwork;
    private final int			WINDOWWIDTH = 900;
	private final int			WINDOWHEIGHT = 400;
	private final int			TEXTAREAWIDTH = 15;
	private final int			TEXTAREAHEIGHT = 5;

	private Choice				netChoice;
	private TextArea			netResultText;
	private TextArea			netNodeText;
	private TextArea			netArcText;
	private Choice				nodeChoice;
	private TextArea			nodeDetailText;
	private TextArea			nodeInArcText;
	private TextArea			nodeOutArcText;
	private Choice				arcChoice;
	private TextArea			arcDetailText;
	private TextField			arcFromNodeText;
	private TextField			arcToNodeText;
	private TextField			arcPrevInText;
	private TextField			arcNextInText;
	private TextField			arcPrevOutText;
	private TextField			arcNextOutText;

	private Frame				itsFrame;

    private final String		NETFILE = "Net_info.txt";
	private final String		NODEFILE = "Node_info.txt";
	private final String		ARCFILE = "Arc_info.txt";
	private final int           CLISTSIZE=50;
	private final int           CYCLELEN=10;
	private final int           BIGM=32600;

    public SnDisplay(SnNetwork network)
    {
        snNetwork=  network;
        netChoice = new Choice();
		netChoice.addItemListener(this);
		nodeChoice = new Choice();
		nodeChoice.addItemListener(this);
		arcChoice = new Choice();
		arcChoice.addItemListener(this);


		Label netTitle = new Label("Net Key :");
		Label netResultTitle = new Label("Result :");
		Label netNodeTitle = new Label("Node :");
		Label netArcTitle = new Label("Solution: ");
		netResultText = new TextArea(TEXTAREAHEIGHT, TEXTAREAWIDTH*2);
		netNodeText = new TextArea(TEXTAREAHEIGHT, TEXTAREAWIDTH);
		netArcText = new TextArea(TEXTAREAHEIGHT, TEXTAREAWIDTH);

		Panel netPanel = new Panel();
		GridBagLayout netPanelGBL = new GridBagLayout();
		GridBagConstraints netPanelGBC = new GridBagConstraints();
		netPanel.setBackground(Color.white);
		netPanel.setLayout(netPanelGBL);

		netPanelGBC.fill = GridBagConstraints.HORIZONTAL;
		netPanelGBC.anchor = GridBagConstraints.EAST;
		netPanelGBC.gridwidth = 2;
/*		netPanelGBC.gridx = 1;
		netPanelGBC.gridy = 1;
		netPanelGBL.setConstraints(netTitle, netPanelGBC);
		netPanel.add(netTitle);
		netPanelGBC.gridy = 2;
		netPanelGBL.setConstraints(netChoice, netPanelGBC);
		netPanel.add(netChoice);
		netPanelGBC.gridy = 3;
		netPanelGBL.setConstraints(netNodeTitle, netPanelGBC);
		netPanel.add(netNodeTitle);
		netPanelGBC.gridy = 4;
		netPanelGBL.setConstraints(netNodeText, netPanelGBC);
		netPanel.add(netNodeText);
		netPanelGBC.gridwidth = GridBagConstraints.REMAINDER;
	*/	netPanelGBC.gridx = 1;
		netPanelGBC.gridy = 1;
		netPanelGBL.setConstraints(netResultTitle, netPanelGBC);
		netPanel.add(netResultTitle);
		netPanelGBC.gridy = 2;
		netPanelGBL.setConstraints(netResultText, netPanelGBC);
		netPanel.add(netResultText);
		netPanelGBC.gridy = 3;
		netPanelGBL.setConstraints(netArcTitle, netPanelGBC);
		netPanel.add(netArcTitle);
		netPanelGBC.gridy = 4;
		netPanelGBL.setConstraints(netArcText, netPanelGBC);
		netPanel.add(netArcText);


		Label nodeTitle = new Label("Node Key :");
		Label nodeDetailTitle = new Label("Detail :");
		Label nodeInArcTitle = new Label("In Arc :");
		Label nodeOutArcTitle = new Label("Out Arc : ");
		nodeDetailText = new TextArea(TEXTAREAHEIGHT, TEXTAREAWIDTH);
		nodeInArcText = new TextArea(TEXTAREAHEIGHT, TEXTAREAWIDTH);
		nodeOutArcText = new TextArea(TEXTAREAHEIGHT, TEXTAREAWIDTH);

		Panel nodePanel = new Panel();
		GridBagLayout nodePanelGBL = new GridBagLayout();
		GridBagConstraints nodePanelGBC = new GridBagConstraints();
		nodePanel.setBackground(Color.white);
		nodePanel.setLayout(nodePanelGBL);

		nodePanelGBC.fill = GridBagConstraints.HORIZONTAL;
		nodePanelGBC.anchor = GridBagConstraints.EAST;
		nodePanelGBC.gridwidth = 1;
		nodePanelGBC.gridx = 1;
		nodePanelGBC.gridy = 1;
		nodePanelGBL.setConstraints(nodeTitle, nodePanelGBC);
		nodePanel.add(nodeTitle);
		nodePanelGBC.gridy = 2;
		nodePanelGBL.setConstraints(nodeChoice, nodePanelGBC);
		nodePanel.add(nodeChoice);
		nodePanelGBC.gridy = 3;
		nodePanelGBL.setConstraints(nodeInArcTitle, nodePanelGBC);
		nodePanel.add(nodeInArcTitle);
		nodePanelGBC.gridy = 4;
		nodePanelGBL.setConstraints(nodeInArcText, nodePanelGBC);
		nodePanel.add(nodeInArcText);
		nodePanelGBC.gridwidth = GridBagConstraints.REMAINDER;
		nodePanelGBC.gridx = 2;
		nodePanelGBC.gridy = 1;
		nodePanelGBL.setConstraints(nodeDetailTitle, nodePanelGBC);
		nodePanel.add(nodeDetailTitle);
		nodePanelGBC.gridy = 2;
		nodePanelGBL.setConstraints(nodeDetailText, nodePanelGBC);
		nodePanel.add(nodeDetailText);
		nodePanelGBC.gridy = 3;
		nodePanelGBL.setConstraints(nodeOutArcTitle, nodePanelGBC);
		nodePanel.add(nodeOutArcTitle);
		nodePanelGBC.gridy = 4;
		nodePanelGBL.setConstraints(nodeOutArcText, nodePanelGBC);
		nodePanel.add(nodeOutArcText);


		Label arcTitle = new Label("Arc Key :");
		Label arcDetailTitle = new Label("Detail :");
		Label arcFromNodeTitle = new Label("FromNode :");
		Label arcToNodeTitle = new Label("ToNode :");
		Label arcPrevInTitle = new Label("PrevInArc :");
		Label arcNextInTitle = new Label("NextInArc :");
		Label arcPrevOutTitle = new Label("PrevOutArc :");
		Label arcNextOutTitle = new Label("NextOutArc :");
		arcDetailText = new TextArea(TEXTAREAHEIGHT, TEXTAREAWIDTH);
		arcFromNodeText = new TextField(TEXTAREAWIDTH);
		arcToNodeText = new TextField(TEXTAREAWIDTH);
		arcPrevInText = new TextField(TEXTAREAWIDTH);
		arcNextInText = new TextField(TEXTAREAWIDTH);
		arcPrevOutText = new TextField(TEXTAREAWIDTH);
		arcNextOutText = new TextField(TEXTAREAWIDTH);

		Panel arcPanel = new Panel();
		GridBagLayout arcPanelGBL = new GridBagLayout();
		GridBagConstraints arcPanelGBC = new GridBagConstraints();
		arcPanel.setBackground(Color.white);
		arcPanel.setLayout(arcPanelGBL);

		arcPanelGBC.fill = GridBagConstraints.HORIZONTAL;
		arcPanelGBC.anchor = GridBagConstraints.EAST;
		arcPanelGBC.gridwidth = 1;
		arcPanelGBC.gridx = 1;
		arcPanelGBC.gridy = 1;
		arcPanelGBL.setConstraints(arcTitle, arcPanelGBC);
		arcPanel.add(arcTitle);
		arcPanelGBC.gridy = 2;
		arcPanelGBL.setConstraints(arcChoice, arcPanelGBC);
		arcPanel.add(arcChoice);
		arcPanelGBC.gridy = 3;
		arcPanelGBL.setConstraints(arcFromNodeTitle, arcPanelGBC);
		arcPanel.add(arcFromNodeTitle);
		arcPanelGBC.gridy = 4;
		arcPanelGBL.setConstraints(arcFromNodeText, arcPanelGBC);
		arcPanel.add(arcFromNodeText);
		arcPanelGBC.gridy = 5;
		arcPanelGBL.setConstraints(arcPrevInTitle, arcPanelGBC);
		arcPanel.add(arcPrevInTitle);
		arcPanelGBC.gridy = 6;
		arcPanelGBL.setConstraints(arcPrevInText, arcPanelGBC);
		arcPanel.add(arcPrevInText);
		arcPanelGBC.gridy = 7;
		arcPanelGBL.setConstraints(arcNextInTitle, arcPanelGBC);
		arcPanel.add(arcNextInTitle);
		arcPanelGBC.gridy = 8;
		arcPanelGBL.setConstraints(arcNextInText, arcPanelGBC);
		arcPanel.add(arcNextInText);
		arcPanelGBC.gridwidth = GridBagConstraints.REMAINDER;
		arcPanelGBC.gridx = 2;
		arcPanelGBC.gridy = 1;
		arcPanelGBL.setConstraints(arcDetailTitle, arcPanelGBC);
		arcPanel.add(arcDetailTitle);
		arcPanelGBC.gridy = 2;
		arcPanelGBL.setConstraints(arcDetailText, arcPanelGBC);
		arcPanel.add(arcDetailText);
		arcPanelGBC.gridy = 3;
		arcPanelGBL.setConstraints(arcToNodeTitle, arcPanelGBC);
		arcPanel.add(arcToNodeTitle);
		arcPanelGBC.gridy = 4;
		arcPanelGBL.setConstraints(arcToNodeText, arcPanelGBC);
		arcPanel.add(arcToNodeText);
		arcPanelGBC.gridy = 5;
		arcPanelGBL.setConstraints(arcPrevOutTitle, arcPanelGBC);
		arcPanel.add(arcPrevOutTitle);
		arcPanelGBC.gridy = 6;
		arcPanelGBL.setConstraints(arcPrevOutText, arcPanelGBC);
		arcPanel.add(arcPrevOutText);
		arcPanelGBC.gridy = 7;
		arcPanelGBL.setConstraints(arcNextOutTitle, arcPanelGBC);
		arcPanel.add(arcNextOutTitle);
		arcPanelGBC.gridy = 8;
		arcPanelGBL.setConstraints(arcNextOutText, arcPanelGBC);
		arcPanel.add(arcNextOutText);

		setLayout(new GridLayout(1,3));
		add(netPanel);
		add(nodePanel);
		add(arcPanel);

		itsFrame = new Frame("My Test Network");
		itsFrame.addWindowListener(this);
		itsFrame.add("Center", this);
		itsFrame.show();
		itsFrame.hide();
		itsFrame.resize(itsFrame.insets().left + itsFrame.insets().right  + WINDOWWIDTH,
						itsFrame.insets().top  + itsFrame.insets().bottom + WINDOWHEIGHT);
		itsFrame.setResizable(false);
		displayInit();


    }

    public void itemInit()
    {
        String netKey=snNetwork.getFirstNet().getKey();
        //add net item
        netChoice.addItem(netKey);

        //add node items
        SnNodeInfo tmpnode=snNetwork.getFirstNodeIn(netKey);
        while(tmpnode!=null)
        {
            String nodeKey=tmpnode.getKey();
            if(nodeKey!="artificial") nodeChoice.addItem(nodeKey);
            tmpnode=snNetwork.getNextNodeFrom(nodeKey);
        }

        //add arc items
        SnArcInfo tmparc=snNetwork.getFirstArcIn(netKey);
        while(tmparc!=null)
        {
            String arcKey=tmparc.getKey();
            if((tmparc.getToNodeKey()!="artificial")&&(tmparc.getFromNodeKey()!="artificial")) arcChoice.addItem(arcKey);
            //System.out.println(arcKey.substring(0,3));
            tmparc=snNetwork.getNextArcFrom(arcKey);
        }


	}

	public void displayInit()
	{
	    itemInit();
	    refreshNetPanel(snNetwork.getFirstNet().getKey());
		refreshNodePanel(nodeChoice.getSelectedItem());
		refreshArcPanel(arcChoice.getSelectedItem());
		itsFrame.show();
	}

	public void itemStateChanged(ItemEvent e)
	{
		Choice changedChoice = (Choice)e.getSource();
		if (changedChoice==netChoice)
		{
			refreshNetPanel(netChoice.getSelectedItem());
		}
		if (changedChoice==nodeChoice)
		{
			refreshNodePanel(nodeChoice.getSelectedItem());
		}
		if (changedChoice==arcChoice)
		{
			refreshArcPanel(arcChoice.getSelectedItem());
		}
	}


/**
	 * Refreshes of the net panel to show the details of
	 * the selected netKey
	 * @param netKey		the key of the net
	 */
	public void refreshNetPanel(String netKey)
	{
		SnNetInfo net = snNetwork.getNet(netKey);
		netResultText.appendText("Total cost:  " + snNetwork.getObjfcn()
		                     +"\nPivots:    "+snNetwork.getPivots()
		                     +"\nDegeneracy:   "+snNetwork.getDegen());

		SnNodeInfo node = snNetwork.getFirstNodeIn(netKey);
		netNodeText.setText("");
		while (node!=null)
		{
			String nodeKey = node.getKey();
			netNodeText.appendText(nodeKey + "\n");
			node = snNetwork.getNextNodeFrom(nodeKey);
		}

		SnArcInfo arc = snNetwork.getFirstArcIn(netKey);
		netArcText.setText("");
		netArcText.appendText("Arc" + "    "+"Flow"+"\n");
		while (arc!=null)
		{
			String arcKey = arc.getKey();
			netArcText.appendText(arcKey + "    "+arc.flow+"\n");
			arc = snNetwork.getNextArcFrom(arcKey);
		}
	}

	/**
	 * Refreshes of the node panel to show the details of
	 * the selected nodeKey
	 * @param nodeKey		the key of the node
	 */
	public void refreshNodePanel(String nodeKey)
	{
		SnNodeInfo node = snNetwork.getNode(nodeKey);
		nodeDetailText.setText("Key:   " + nodeKey + "\n");
		nodeDetailText.appendText("Supply:  " + node.supply +"\n");
		if(node.pred!=null) nodeDetailText.appendText("Predecessor:  " + node.pred.getKey() +"\n");
		else nodeDetailText.appendText("Predecessor:  " + "NIL" +"\n");
		SnArcInfo arc = snNetwork.getFirstInArcOf(nodeKey);
		nodeInArcText.setText("");
		while (arc!=null)
		{
			String arcKey = arc.getKey();
			nodeInArcText.appendText(arcKey + "\n");
			arc = snNetwork.getNextInArcFrom(arcKey);
		}
		arc = snNetwork.getFirstOutArcOf(nodeKey);
		nodeOutArcText.setText("");
		while (arc!=null)
		{
			String arcKey = arc.getKey();
			nodeOutArcText.appendText(arcKey + "\n");
			arc = snNetwork.getNextOutArcFrom(arcKey);
		}
	}

	/**
	 * Refreshes of the arc panel to show the details of
	 * the selected arcKey
	 * @param arcKey		the key of the arc
	 */
	public void refreshArcPanel(String arcKey)
	{
		SnArcInfo arc = snNetwork.getArc(arcKey);
		arcDetailText.setText("Key:   " + arcKey + "\n");
		arcDetailText.appendText("Cap:   " + arc.uBound +"\n");
		arcDetailText.appendText("Cost: " + arc.cost +"\n");
		arcDetailText.appendText("Flow: " + arc.flow +"\n");

		arcFromNodeText.setText(arc.getFromNodeKey());
		arcToNodeText.setText(arc.getToNodeKey());
		arc = snNetwork.getPrevInArcFrom(arcKey);
		if (arc!=null)
		{
			arcPrevInText.setText(arc.getKey());
		}
		else
		{
			arcPrevInText.setText("Nil");
		}
		arc = snNetwork.getNextInArcFrom(arcKey);
		if (arc!=null)
		{
			arcNextInText.setText(arc.getKey());
		}
		else
		{
			arcNextInText.setText("Nil");
		}
		arc = snNetwork.getPrevOutArcFrom(arcKey);
		if (arc!=null)
		{
			arcPrevOutText.setText(arc.getKey());
		}
		else
		{
			arcPrevOutText.setText("Nil");
		}
		arc = snNetwork.getNextOutArcFrom(arcKey);
		if (arc!=null)
		{
			arcNextOutText.setText(arc.getKey());
		}
		else
		{
			arcNextOutText.setText("Nil");
		}
	}

	/**
	 * Procedures for controlling the window.
	 * Only the closing of the window is implemented.
	 * @param e			the windowEvent
	 */
	public void windowClosing(WindowEvent e)
	{
		itsFrame.dispose();

		System.exit(0);
	}

	public void windowOpened(WindowEvent e)      {} // End windowOpened.
	public void windowClosed(WindowEvent e)      {} // End windowClosed.
	public void windowIconified(WindowEvent e)   {} // End windowIconified.
	public void windowDeiconified(WindowEvent e) {} // End windowDeiconified.
	public void windowActivated(WindowEvent e)   {} // End windowActivated.
	public void windowDeactivated(WindowEvent e) {} // End windowDeactivated.

}


