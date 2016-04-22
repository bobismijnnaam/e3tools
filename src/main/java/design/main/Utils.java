package design.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

import design.main.Info.Base;
import design.main.Info.LogicDot;
import design.main.Info.SignalDot;
import design.main.Info.ValueExchange;
import design.main.Info.ValueExchangeLabel;
import design.main.Info.ValueInterface;
import design.main.Info.ValuePort;

public class Utils {
	public static boolean overlap(mxRectangle a, mxRectangle b) {
		if (a.getX() > b.getX()) {
			mxRectangle dummy = a;
			a = b;
			b = dummy;
		}
		
		if (a.getX() + a.getWidth() > b.getX()) {
			// Horizontal overlap
			if (a.getY() > b.getY()) {
				mxRectangle dummy = a;
				a = b;
				b = dummy;
			}
			
			if (a.getY() + a.getHeight() > b.getY()) {
				// And vertical overlap as well
				return true;
			}
		} else {
			return false;
		}
		
		return false;
	}
	
	/**
	 * Returns true if inner is within outer
	 * @param inner
	 * @param outer
	 * @return
	 */
	public static boolean within(mxRectangle inner, mxRectangle outer) {
		if (inner.getX() < outer.getX())
			return false;
		
		if (inner.getX() + inner.getWidth() > outer.getX() + outer.getWidth())
			return false;
		
		if (inner.getY() < outer.getY())
			return false;
		
		if (inner.getY() + inner.getHeight() > outer.getY() + outer.getHeight())
			return false;
		
		return true;
	}
	
	public static mxRectangle rect(mxGeometry gm) {
		return new mxRectangle(gm.getX(), gm.getY(), gm.getWidth(), gm.getHeight());
	}
	
	/**
	 * Returns true if the given value interface or value port is situated on a top-level actor.
	 * That is, it is not nested.
	 * @param cell
	 * @return
	 */
	public static boolean isToplevelValueInterface(mxGraph graph, mxICell cell) {
		Base value = Utils.base(graph, cell);

		if (value instanceof ValuePort) {
			return isToplevelValueInterface(graph, cell.getParent());
		} else if (value instanceof ValueInterface) {
			mxICell parent = cell.getParent();

			if (parent == null) return false;

			return parent.getParent() == graph.getDefaultParent();
		}
		
		return false;
	}
	
	/**
	 * Gets the geometry object from a cell, copies it, and returns it.
	 * @param graph The graph the cell resides in
	 * @param obj The cell the geometry should be copied from
	 * @return A copy of the geometry object
	 */
	public static mxGeometry geometry(mxGraph graph, Object obj) {
		mxGeometry gm = (mxGeometry) graph.getCellGeometry(obj);
		if (gm != null) return (mxGeometry) gm.clone();
		else return null;
	}
	
	/**
	 * Gets the base object from a cell, copies it, and returns it.
	 * @param graph The graph the cell resides in
	 * @param obj The cell the Base should be copied from
	 * @return A deep copy of the Base object
	 */
	public static Base base(mxGraph graph, Object obj) {
		Object value = graph.getModel().getValue(obj);
		if (value instanceof Base) return ((Base) value).getCopy();
		return null;
	}
	
	public static List<Object> getAllCells(mxGraph graph) {
		return getAllCells(graph, graph.getDefaultParent());
	}
	
	public static List<Object> getAllCells(mxGraph graph, Object parent) {
		List<Object> result = new ArrayList<>(Arrays.asList(mxGraphModel.getChildCells(graph.getModel(), parent, true, true)));
		List<Object> aggr = new ArrayList<>();
		
		for (Object cell : result) {
			aggr.addAll(getAllCells(graph, cell));
		}
		
		result.addAll(aggr);
		
		return result;
	}
	
	/**
	 * Returns a copy of the Info.ValueExchange value of cell
	 * @param cell The cell of which to get the value
	 * @return
	 */
	public static ValueExchange getValueExchange(Object cell) {
		return getValueExchange(cell, true);
	}
	
	/**
	 * Returns the Info.ValueExchange value of the cell.
	 * @param cell The cell of which to get the value
	 * @param clone If true, a copy of the value is returned.
	 * @return
	 */
	public static ValueExchange getValueExchange(Object cell, boolean clone) {
		mxICell actualCell = (mxICell) cell;
		ValueExchange value = (ValueExchange) (actualCell.getValue());
		if (clone) {
			value = (ValueExchange) value.getCopy();
		}
		return value;
	}
	
	public static boolean isDotValue(Base value) {
		return value instanceof SignalDot || value instanceof LogicDot;
	}
	
	/**
	 * Finds the child of edge valueExchangeEdge that is the "name" label. Returns it if
	 * it's found, otherwise it returns null
	 * @param graph
	 * @param valueExchangeEdge
	 * @return
	 */
	public static Object getValueExchangeNameLabel(mxGraph graph, Object valueExchangeEdge) {
		for (Object cell : Utils.getAllCells(graph, valueExchangeEdge)) {
			if (graph.getModel().getValue(cell) instanceof ValueExchangeLabel) {
				ValueExchangeLabel labelValue = (ValueExchangeLabel) graph.getModel().getValue(cell);
				if (!labelValue.isValueObjectLabel) {
					return cell;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Finds the child of the edge valueExchangeEdge that is the "valueObject" label. Returns it
	 * if it's found, otherwise it returns null
	 * @param graph
	 * @param valueExchangeEdge
	 * @return
	 */
	public static Object getValueExchangeValueObjectLabel(mxGraph graph, Object valueExchangeEdge) {
		for (Object cell : Utils.getAllCells(graph, valueExchangeEdge)) {
			if (graph.getModel().getValue(cell) instanceof ValueExchangeLabel) {
				ValueExchangeLabel labelValue = (ValueExchangeLabel) graph.getModel().getValue(cell);
				if (labelValue.isValueObjectLabel) {
					return cell;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Sets the visibility of a value exchange edge's name label, based on the value
	 * exchange's value.labelHidden. If the name label is empty it is not visible,
	 * and otherwise if it is hidden it is not visible.
	 * @param graph
	 * @param valueExchangeEdge
	 */
	public static void setValueExchangeNameLabelVisibility(mxGraph graph, Object valueExchangeEdge) {
		Object nameCell = getValueExchangeNameLabel(graph, valueExchangeEdge);
		
		ValueExchange veValue = (ValueExchange) Utils.base(graph, valueExchangeEdge);
		ValueExchangeLabel nameLabelValue = (ValueExchangeLabel) Utils.base(graph, nameCell);
		
		graph.getModel().beginUpdate();
		try {
			if (nameLabelValue.name == null || nameLabelValue.name.trim().isEmpty()) {
				graph.getModel().setVisible(nameCell, false);
			} else {
				graph.getModel().setVisible(nameCell, !veValue.labelHidden);
			}
		} finally {
			graph.getModel().endUpdate();
		}
	}
	
	/**
	 * Sets the visibility of a value exchange edge's valueobject label, based on the value
	 * exchange's value.valueObjectHidden. If the valueObject label is empty it is not visible,
	 * and otherwise if it is hidden it is not visible.
	 * @param graph
	 * @param valueExchangeEdge
	 */
	public static void setValueExchangeValueObjectLabelVisibility(mxGraph graph, Object valueExchangeEdge) {
		Object valueObjectCell = getValueExchangeValueObjectLabel(graph, valueExchangeEdge);
		
		ValueExchange veValue = (ValueExchange) Utils.base(graph, valueExchangeEdge);
		ValueExchangeLabel valueObjectValue = (ValueExchangeLabel) Utils.base(graph, valueObjectCell);
		
		graph.getModel().beginUpdate();
		try {
			if (valueObjectValue.name == null || valueObjectValue.name.trim().isEmpty()) {
				graph.getModel().setVisible(valueObjectCell, false);
			} else {
				graph.getModel().setVisible(valueObjectCell, !veValue.valueObjectHidden);
			}
		} finally {
			graph.getModel().endUpdate();
		}
	}
	
	/**
	 * Sets the name label of the valueExchangeEdge to the right text and gives it the correct
	 * visibility based on labelHidden of valueExchangeEdge's value
	 * @param graph
	 * @param valueExchangeEdge
	 */
	public static void updateValueExchangeNameLabel(mxGraph graph, Object valueExchangeEdge) {
		graph.getModel().beginUpdate();
		try {
			Object labelCell = Utils.getValueExchangeNameLabel(graph, valueExchangeEdge);

			assert (labelCell != null);

			ValueExchange veValue = (ValueExchange) Utils.base(graph, valueExchangeEdge);
			ValueExchangeLabel labelValue = (ValueExchangeLabel) Utils.base(graph, labelCell);

			labelValue.name = veValue.name;

			graph.getModel().setValue(labelCell, labelValue);

			Utils.setValueExchangeNameLabelVisibility(graph, valueExchangeEdge);
		} finally {
			graph.getModel().endUpdate();
		}
	}
	
	/**
	 * Sets the valueObject label of the valueExchangeEdge to the right text and gives it the correct
	 * visibility based on valueObjectHidden of valueExchangeEdge's value
	 * @param graph
	 * @param valueExchangeEdge
	 */
	public static void updateValueExchangeValueObjectLabel(mxGraph graph, Object valueExchangeEdge) {
		graph.getModel().beginUpdate();
		try {
			Object valueObjectCell = Utils.getValueExchangeValueObjectLabel(graph, valueExchangeEdge);

			assert (valueObjectCell != null);

			ValueExchange veValue = (ValueExchange) Utils.base(graph, valueExchangeEdge);
			ValueExchangeLabel valueObjectValue = (ValueExchangeLabel) Utils.base(graph, valueObjectCell);

			valueObjectValue.name = veValue.valueObject;

			graph.getModel().setValue(valueObjectCell, valueObjectValue);

			Utils.setValueExchangeValueObjectLabelVisibility(graph, valueExchangeEdge);
		} finally {
			graph.getModel().endUpdate();
		}
	}
}
