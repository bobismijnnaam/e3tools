/*******************************************************************************
 * Copyright (C) 2016 Bob Rubbens
 *  
 *  
 * This file is part of e3tool.
 *  
 * e3tool is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * e3tool is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with e3tool.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package design;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.ScrollPaneConstants;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxGraphHandler;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;

import design.info.Actor;
import design.info.Base;
import design.info.EndSignal;
import design.info.LogicBase;
import design.info.LogicDot;
import design.info.MarketSegment;
import design.info.StartSignal;
import design.info.ValueActivity;
import design.info.ValueExchange;
import design.info.ValueInterface;
import design.info.ValuePort;
import design.listeners.KeyboardHandler;
import design.listeners.ProxySelection;

public class E3GraphComponent extends mxGraphComponent {
	JPopupMenu defaultMenu = new JPopupMenu();
	JPopupMenu logicMenu = new JPopupMenu();
	JPopupMenu partDotMenu = new JPopupMenu();
	JPopupMenu unitDotMenu = new JPopupMenu();
	JPopupMenu valueInterfaceMenu = new JPopupMenu();
	JPopupMenu valuePortMenu = new JPopupMenu();
	JPopupMenu valueExchangeMenu = new JPopupMenu();
	JPopupMenu actorMenu = new JPopupMenu();
	JPopupMenu valueActivityMenu = new JPopupMenu();
	JPopupMenu marketSegmentMenu = new JPopupMenu();
	JPopupMenu startSignalMenu = new JPopupMenu();
	JPopupMenu endSignalMenu = new JPopupMenu();

	public mxUndoManager undoManager;
	
	private boolean popupTriggerEnabled = true;
	
	public E3GraphComponent makeShowcase(E3Graph graph) {
		E3GraphComponent component = new E3GraphComponent(graph);
		
		component.setEnabled(false);
    	component.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    	component.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    	
    	// TODO: Do some scaling and translation here? See MainWindow.java
    	// TODO: Make it so the user can scroll around and such
		
		return component;
	}

	public E3GraphComponent(mxGraph graph) {
		super(graph);
		
		ContextMenus.addDefaultMenu(defaultMenu, graph);
		ContextMenus.addLogicMenus(logicMenu, graph);
		ContextMenus.addPartDotMenu(partDotMenu, graph);
		ContextMenus.addProportionMenu(unitDotMenu, graph);
		ContextMenus.addValueInterfaceMenu(valueInterfaceMenu, graph);
		
		ContextMenus.addE3PropertiesMenu(valuePortMenu, graph);
		ContextMenus.addValuePortMenu(valuePortMenu, graph);
		
		ContextMenus.addE3PropertiesMenu(valueExchangeMenu, graph);
		ContextMenus.addValueExchangeMenu(valueExchangeMenu, graph);
		
		ContextMenus.addE3PropertiesMenu(actorMenu, graph);
		ContextMenus.addActorMenu(actorMenu, graph);
		
		ContextMenus.addE3PropertiesMenu(valueActivityMenu, graph);
		ContextMenus.addE3PropertiesMenu(marketSegmentMenu, graph);
		
		ContextMenus.addE3PropertiesMenu(startSignalMenu, graph);
		ContextMenus.addStartSignalMenu(startSignalMenu, graph);
		
		ContextMenus.addE3PropertiesMenu(endSignalMenu, graph);
		ContextMenus.addEndSignalMenu(endSignalMenu, graph);
		
		// Some debug menus
		if (Main.DEBUG) {
			ContextMenus.addE3PropertiesMenu(valueInterfaceMenu, graph);
		}
		
		getConnectionHandler().setCreateTarget(false);
		graph.setAllowDanglingEdges(false);
		graph.setPortsEnabled(false);
		graph.setCellsDisconnectable(false);
		getGraphHandler().setRemoveCellsFromParent(true);
		// This makes drag and drop behave properly
		// If you turn these on a drag-shadow that is sometimes offset improperly
		// is shown. Once they fix it in mxGraph we can turn it back on but it's not really needed.
		getGraphHandler().setImagePreview(false);
		getGraphHandler().setCenterPreview(false);
//		getGraphHandler().setCloneEnabled(false);
		graph.getSelectionModel().setSingleSelection(true);
		setConnectable(true);
		
		undoManager = new mxUndoManager();
		
		// This handler propagates all the edits to the undo manager
		mxIEventListener undoHandler = new mxIEventListener() {
			@Override
			public void invoke(Object sender, mxEventObject evt) {
				undoManager.undoableEditHappened((mxUndoableEdit) evt
						.getProperty("edit"));
			}
		};
		
		// This handler should keep the selection in sync with the command history
		// (Although I'm not sure if it actually does anything)
		mxIEventListener undoSelectionHandler = new mxIEventListener() {
			@Override
			public void invoke(Object sender, mxEventObject evt) {
				List<mxUndoableChange> changes = ((mxUndoableEdit) evt
						.getProperty("edit")).getChanges();
				graph.setSelectionCells(graph
						.getSelectionCellsForChanges(changes));
			}
		};
		
		graph.getModel().addListener(mxEvent.UNDO, undoHandler);
		graph.getView().addListener(mxEvent.UNDO, undoHandler);
		undoManager.addListener(mxEvent.UNDO, undoSelectionHandler);
		undoManager.addListener(mxEvent.REDO, undoSelectionHandler);

		// Enable delete key et. al.
		// Pass the undoManager as well so ctrl+z/ctrl+y can trigger edits
		new KeyboardHandler(this, undoManager);
		
		// Set styling of nodes, background color, etc.
		E3Style.styleGraphComponent(this);
		
		graph.getSelectionModel().addListener(mxEvent.CHANGE, new ProxySelection(graph));		

		getGraphControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Object cell = graph.getSelectionCell();
				mxGeometry gm = graph.getCellGeometry(cell);
				
				if (graph.getModel().getValue(cell) instanceof Base) {
					Base base = (Base) graph.getModel().getValue(cell);
					System.out.println(base.SUID);
				}

				if (e.isPopupTrigger()) {
					triggerContextMenu(e);
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					triggerContextMenu(e);
				}
			}
		});
	}
	
	public boolean isPopupTriggerEnabled() {
		return popupTriggerEnabled;
	}
	
	public void setPopupTriggerEnabled(boolean b) {
		popupTriggerEnabled = b;
	}
		
	public void triggerContextMenu(MouseEvent e) {
		Object obj = getCellAt(e.getX(), e.getY());
		String style = graph.getModel().getStyle(obj);
		JPopupMenu menu = null;
		
		Main.contextTarget = obj;
		Main.contextPos = new mxPoint(e.getX(), e.getY());
		
		if (obj == null) {
			obj = graph.getDefaultParent();
			menu = defaultMenu;
			Main.contextTarget = new mxPoint(e.getX(), e.getY());
		} else if (style != null) {
			if (style.endsWith("Triangle")) {
				Main.contextTarget = graph.getModel().getParent(Main.contextTarget);
			}
			if (style.equals("Bar")) {
				Main.contextTarget = graph.getModel().getParent(Main.contextTarget);
			}
			
			Object val = graph.getModel().getValue(Main.contextTarget);
			// If it does not have a base object as value
			// there's probably no use in checking for a right click menu
			if (!(val instanceof Base)) return;
			Base value = (Base) val;
			
			if (value instanceof LogicBase) menu = logicMenu;
			if (value instanceof ValueInterface) menu = valueInterfaceMenu;
			if (value instanceof ValuePort) menu = valuePortMenu;
			if (value instanceof ValueExchange) menu = valueExchangeMenu;
			if (value instanceof Actor) menu = actorMenu;
			if (value instanceof MarketSegment) menu = marketSegmentMenu;
			if (value instanceof ValueActivity) menu = valueActivityMenu;
			if (value instanceof StartSignal) menu = startSignalMenu;
			if (value instanceof EndSignal) menu = endSignalMenu;
			if (value instanceof LogicDot) {
				if (((LogicDot) value).isUnit) menu = unitDotMenu;
				else menu = partDotMenu;
			}
		}
			
		if (e.isPopupTrigger() && menu != null && popupTriggerEnabled) {
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	/**
	 * Makes sure that only actors can be moved out of parents.
	 */
	@Override
	protected mxGraphHandler createGraphHandler() {
		return new mxGraphHandler(this) {
			@Override
			protected boolean shouldRemoveCellFromParent(Object parent, Object[] cells, MouseEvent e) {
				Base value = Utils.base(graph, cells[0]);
				
				if (value instanceof Actor
						|| value instanceof ValueActivity
						|| value instanceof MarketSegment) {
					return super.shouldRemoveCellFromParent(parent, cells, e);
				}
				
				return false;
			}
		};
	}
}