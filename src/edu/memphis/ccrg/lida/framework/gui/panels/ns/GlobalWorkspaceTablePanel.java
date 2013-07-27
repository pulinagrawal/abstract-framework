/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.gui.panels.ns;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.gui.panels.GuiPanelImpl;
import edu.memphis.ccrg.lida.framework.shared.ns.NodeStructure;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.globalworkspace.BroadcastListener;
import edu.memphis.ccrg.lida.globalworkspace.Coalition;
import edu.memphis.ccrg.lida.globalworkspace.GlobalWorkspace;
import edu.memphis.ccrg.lida.globalworkspace.triggers.BroadcastTrigger;

/**
 * This is a Panel which shows all current coalitions in Global Workspace and
 * also the most recent broadcast.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 * @author Siminder Kaur
 */
public class GlobalWorkspaceTablePanel extends GuiPanelImpl implements
		BroadcastListener {

	private static final Logger logger = Logger
			.getLogger(GlobalWorkspaceTablePanel.class.getCanonicalName());
	private Collection<Coalition> coalitions;
	private Coalition[] coalitionArray = new Coalition[0];
	private GlobalWorkspace module;
	private LinkedList<BroadcastDetail> recentBbroadcasts = new LinkedList<BroadcastDetail>();
	private int recentBroadcastsSize;
	private final static int DEFAULT_RECENT_BROADCAST_SIZE = 20;

	/** Creates new form NodeStructureTable */
	public GlobalWorkspaceTablePanel() {
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {
		jToolBar1 = new javax.swing.JToolBar();
		refreshButton = new javax.swing.JButton();
		jSplitPane1 = new javax.swing.JSplitPane();
		winnersPane = new javax.swing.JScrollPane();
		winnersTable = new javax.swing.JTable();
		coalitionsPane1 = new javax.swing.JScrollPane();
		coalitionsTable = new javax.swing.JTable();

		setPreferredSize(new java.awt.Dimension(500, 291));

		jToolBar1.setRollover(true);

		refreshButton.setText("Refresh");
		refreshButton.setFocusable(false);
		refreshButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		refreshButton
				.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		refreshButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				refreshButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(refreshButton);

		jSplitPane1.setDividerLocation(150);
		jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

		winnersTable.setModel(new WinnerCoalitionsTableModel());
		winnersPane.setViewportView(winnersTable);

		jSplitPane1.setRightComponent(winnersPane);

		coalitionsTable.setModel(new CoalitionsTableModel());
		coalitionsPane1.setViewportView(coalitionsTable);

		jSplitPane1.setLeftComponent(coalitionsPane1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addComponent(jToolBar1,
						javax.swing.GroupLayout.DEFAULT_SIZE, 1132,
						Short.MAX_VALUE).addContainerGap()).addComponent(
				jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING,
				javax.swing.GroupLayout.DEFAULT_SIZE, 1142, Short.MAX_VALUE));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addComponent(
												jToolBar1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												25,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jSplitPane1,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												260, Short.MAX_VALUE)));
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JScrollPane coalitionsPane1;
	private javax.swing.JTable coalitionsTable;
	private javax.swing.JSplitPane jSplitPane1;
	private javax.swing.JToolBar jToolBar1;
	private javax.swing.JButton refreshButton;
	private javax.swing.JScrollPane winnersPane;
	private javax.swing.JTable winnersTable;

	// End of variables declaration//GEN-END:variables

	private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_refreshButtonActionPerformed
		refresh();
	}// GEN-LAST:event_refreshButtonActionPerformed

	@Override
	public void initPanel(String[] param) {
		module = (GlobalWorkspace) agent
				.getSubmodule(ModuleName.GlobalWorkspace);
		if (module == null) {
			logger
					.log(
							Level.WARNING,
							"Error initializing NodeStructure Panel, Module does not exist in agent.",
							0L);
			return;
		}
		module.addListener(this);

		recentBroadcastsSize = DEFAULT_RECENT_BROADCAST_SIZE;

		if (param.length > 0) {
			try {
				recentBroadcastsSize = Integer.parseInt(param[0]);
			} catch (NumberFormatException e) {
				logger.log(Level.WARNING,
						"parse error, using default recent broadcast size");
			}
		} else {
			logger.log(Level.INFO, "using default recent broadcast size");
		}
	}

	@Override
	public void refresh() {
		display(module.getModuleContent("coalitions"));
	}

	private class CoalitionsTableModel extends AbstractTableModel {

		private String[] columNames = { "Coalition ID", "Activation",
				"Coalition NodeStructure", "Creating AttentionCodelet",
				"Sought Content" };
		private DecimalFormat df = new DecimalFormat("0.0000");

		@Override
		public int getColumnCount() {
			return columNames.length;
		}

		@Override
		public int getRowCount() {
			return coalitionArray.length;
		}

		@Override
		public String getColumnName(int column) {
			if (column < columNames.length) {
				return columNames[column];
			}
			return "";
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {

			if (rowIndex > coalitionArray.length
					|| columnIndex > columNames.length || rowIndex < 0
					|| columnIndex < 0) {
				return null;
			}
			Coalition coal = coalitionArray[rowIndex];

			switch (columnIndex) {
			case 0:
				return coal.hashCode();
			case 1:
				return df.format(coal.getActivation());
			case 2:
				return coal.getContent();
			case 3:
				return coal.getCreatingAttentionCodelet();
			case 4:
				return coal.getCreatingAttentionCodelet().getSoughtContent();
			default:
				return "";
			}

		}
	}

	private class WinnerCoalitionsTableModel extends AbstractTableModel {

		private String[] columNames = { "Tick at broadcast", "Broadcast count",
				"Coalition Activation", "Broadcast NodeStructure",
				"Broadcast Trigger" };
		private DecimalFormat df = new DecimalFormat("0.0000");

		@Override
		public int getColumnCount() {
			return columNames.length;
		}

		@Override
		public int getRowCount() {
			return recentBbroadcasts.size();
		}

		@Override
		public String getColumnName(int column) {
			if (column < columNames.length) {
				return columNames[column];
			}
			return "";
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex > recentBbroadcasts.size()
					|| columnIndex > columNames.length || rowIndex < 0
					|| columnIndex < 0) {
				return null;
			}
			BroadcastDetail bd = recentBbroadcasts.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return bd.getTickAtBroadcast();
			case 1:
				return bd.getBroadcastSentCount();
			case 2:
				return df.format(bd.getWinnerCoalActivation());
			case 3:
				return bd.getBroadcastContent();
			case 4:
				BroadcastTrigger trigger = bd.getLastBroadcastTrigger();
				if (trigger != null) {
					return trigger.getClass().getSimpleName();
				}
				return "";
			default:
				return "";
			}

		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void display(Object o) {
		// Collections.unmodifiableCollection(coalitions)
		coalitions = (Collection<Coalition>) o;
		coalitionArray = coalitions.toArray(new Coalition[0]);

		((AbstractTableModel) winnersTable.getModel())
				.fireTableStructureChanged();

		((AbstractTableModel) coalitionsTable.getModel())
				.fireTableStructureChanged();
	}

	@Override
	public void learn(Coalition coalition) {
		// No learning in panel
	}

	@Override
	public void receiveBroadcast(Coalition coalition) {
		BroadcastDetail bd = new BroadcastDetail((NodeStructure) coalition
				.getContent(), coalition.getActivation(),
				(BroadcastTrigger) module
						.getModuleContent("lastBroadcastTrigger"), TaskManager
						.getCurrentTick(), module.getBroadcastSentCount());
		synchronized (this) {
			recentBbroadcasts.addFirst(bd);
			if (recentBbroadcasts.size() > recentBroadcastsSize) {
				recentBbroadcasts.pollLast();
			}
		}
	}

	private class BroadcastDetail {

		private final NodeStructure broadcastContent;
		private final double winnerCoalActivation;
		private final BroadcastTrigger lastBroadcastTrigger;
		private final long tickAtBroadcast;
		private final long broadcastSentCount;

		public BroadcastDetail(NodeStructure broadcastContent,
				double winnerCoalActivation,
				BroadcastTrigger lastBroadcastTrigger, long tickAtBroadcast,
				long broadcastSentCount) {
			this.broadcastContent = broadcastContent;
			this.winnerCoalActivation = winnerCoalActivation;
			this.lastBroadcastTrigger = lastBroadcastTrigger;
			this.tickAtBroadcast = tickAtBroadcast;
			this.broadcastSentCount = broadcastSentCount;
		}

		/**
		 * @return the broadcastContent
		 */
		public NodeStructure getBroadcastContent() {
			return broadcastContent;
		}

		/**
		 * @return the winnerCoalActivation
		 */
		public double getWinnerCoalActivation() {
			return winnerCoalActivation;
		}

		/**
		 * @return the lastBroadcastTrigger
		 */
		public BroadcastTrigger getLastBroadcastTrigger() {
			return lastBroadcastTrigger;
		}

		/**
		 * @return the tickAtBroadcast
		 */
		public long getTickAtBroadcast() {
			return tickAtBroadcast;
		}

		/**
		 * @return the broadcastSentCount
		 */
		public long getBroadcastSentCount() {
			return broadcastSentCount;
		}
	}
}
