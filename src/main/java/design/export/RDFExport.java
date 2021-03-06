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
package design.export;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resources;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.mxgraph.view.mxGraph;

import design.E3Graph;
import design.Utils;
import design.info.Actor;
import design.info.Base;
import design.info.ConnectionElement;
import design.info.EndSignal;
import design.info.Info;
import design.info.LogicBase;
import design.info.MarketSegment;
import design.info.StartSignal;
import design.info.ValueActivity;
import design.info.ValueExchange;
import design.info.ValueInterface;
import design.info.ValuePort;
import e3fraud.vocabulary.E3value;

public class RDFExport {
	
	public final E3Graph graph;
	private String result;
	
	Map<Long, Resource> offeringIn = new HashMap<>();
	Map<Long, Resource> offeringOut = new HashMap<>();
	Map<String, Resource> valueObject = new HashMap<>();
	public Model model;
	private Resource modelRes;
	private Resource diagramRes;
	private String base;
	private boolean unsafe;
	
	public RDFExport(mxGraph graph, boolean unsafe) {
		this.unsafe = unsafe;
		this.graph = (E3Graph) graph;
		
		convertToRdf();
	}
	
	public Resource getResource(long suid) {
		Resource res = model.createResource(base + suid);
		
		res.addProperty(E3value.e3_has_uid, "" + suid);
		res.addProperty(E3value.mc_in_mo, modelRes);
		res.addProperty(E3value.mc_in_di, diagramRes);
		modelRes.addProperty(E3value.mo_has_mc, res);
		diagramRes.addProperty(E3value.di_has_mc, res);
		
		return res;
	}
	
	public Resource getOfferingIn(long suid) {
		if (!offeringIn.containsKey(suid)) {
			offeringIn.put(suid, getResource(Info.getSUID()));
			Resource of = offeringIn.get(suid);
			of.addProperty(E3value.e3_has_name, "in");
			of.addProperty(RDF.type, E3value.value_offering);

			of.addProperty(E3value.vo_in_vi, getResource(suid));
			getResource(suid).addProperty(E3value.vi_consists_of_of, of);
		}
		
		return offeringIn.get(suid);
	}
	
	public Resource getOfferingOut(long suid) {
		if (!offeringOut.containsKey(suid)) {
			offeringOut.put(suid, getResource(Info.getSUID()));
			Resource of = offeringOut.get(suid);
			of.addProperty(E3value.e3_has_name, "out");
			of.addProperty(RDF.type, E3value.value_offering);

			of.addProperty(E3value.vo_in_vi, getResource(suid));
			getResource(suid).addProperty(E3value.vi_consists_of_of, of);
		}
		
		return offeringOut.get(suid);
	}

	public Resource getValueObject(String obj) {
		if (!valueObject.containsKey(obj)) {
			valueObject.put(obj, getResource(Info.getSUID()));
			Resource reObj = valueObject.get(obj);
			reObj.addProperty(E3value.e3_has_name, obj);
			reObj.addProperty(RDF.type, E3value.value_object);
		}
		
		return valueObject.get(obj);
	}
	
	private void convertToRdf() {
		// Check if no non-colludable actors are colluded
		{
			boolean noBadColluders = Utils.getAllCells(graph).stream()
					// Only keep the actors that are colluding
					.filter(o -> {
						Object val = graph.getModel().getValue(o);
						if (val instanceof Actor) {
							Actor actor = (Actor) val;
							return actor.colluded;
						}
						return false;
					})
					// Get the children of each colluding actor
					.map(o -> Utils.getChildren(graph, o))
					// The list of children has to be either empty
					// Or consist of only ValueActivities
					.allMatch(children -> {
						return children.stream()
								// Get the value of each child
								.map(child -> graph.getModel().getValue(child))
								// Only keep actors, market segments, value activities
								.filter(value -> value instanceof ValueActivity
										|| value instanceof MarketSegment
										|| value instanceof Actor)
								// Either it is empty (returning true)
								// Or it is not empty, and then the values should all be value activities
								.allMatch(child -> graph.getModel().getValue(child) instanceof ValueActivity);
					});
			
			if (!noBadColluders) {
				// TODO: Actual error handling
				System.out.println("An actor is colluding which has something else than just value activities as children. Error, aborting!");
				return;
			}
		}
		
		model = ModelFactory.createDefaultModel();
		model.setNsPrefix("a", E3value.getURI());
		base = "http://www.cs.vu.nl/~gordijn/TestModel#";
		
		// Create model resource
		long modelSUID = Info.getSUID();
		modelRes = model.createResource(base + modelSUID, E3value.model);
		modelRes.addProperty(E3value.e3_has_name, "model" + modelSUID);
		modelRes.addProperty(E3value.e3_has_uid, "" + modelSUID);
		
		// Create diagram resource
		long diagramSUID = Info.getSUID();
		diagramRes = model.createResource(base + diagramSUID, E3value.diagram);
		diagramRes.addProperty(E3value.e3_has_name, "diagram" + diagramSUID);
		diagramRes.addProperty(E3value.e3_has_uid, "" + diagramSUID);
		
		for (String valueObject : graph.valueObjects) {
			getValueObject(valueObject);
		}

		// TODO: Make sure there's a E3Graph.getNewSUID() method as well,
		// and get rid of this whole global counter thing.
		long colludedID = Info.getSUID();
		Map<String, String> colludedFormulas = new HashMap<>();
		
		for (Object cell : Utils.getAllCells(graph)) {
			Object cellValue = graph.getModel().getValue(cell);
			
			if (!(
					cellValue instanceof Actor
					|| cellValue instanceof MarketSegment
					|| cellValue instanceof ValueInterface
					|| cellValue instanceof ValueExchange
					|| cellValue instanceof StartSignal
					|| cellValue instanceof EndSignal
					|| cellValue instanceof ConnectionElement
					|| cellValue instanceof ValueActivity
					|| cellValue instanceof ValuePort
					|| cellValue instanceof LogicBase
			)) continue;
			
			Base value = (Base) cellValue;
			
			//System.out.println("Considering: \"" + value.name + "\"");
			
			Resource res = null;
			
			if (value instanceof Actor) {
				Actor acInfo = (Actor) value;
				
				if (acInfo.colluded) {
					res = getResource(colludedID);
					
					if (res.hasProperty(E3value.e3_has_name)) {
						String newName = res.getProperty(E3value.e3_has_name).getString() + " + " + acInfo.name;
						res.removeAll(E3value.e3_has_name);
						res.addProperty(E3value.e3_has_name, newName);
					} else {
						res.addProperty(E3value.e3_has_name, acInfo.name);
					}
					
					for (String key : value.formulas.keySet()) {
						if (colludedFormulas.containsKey(key)) {
							if (unsafe) {
								int original = Integer.parseInt(colludedFormulas.get(key));
								int delta = Integer.parseInt(value.formulas.get(key));
								colludedFormulas.put(key, (original + delta) + "");
							} else {
								colludedFormulas.put(
									key, 
									colludedFormulas.get(key) + "+" + value.formulas.get(key));
							}
						} else {
							colludedFormulas.put(key, value.formulas.get(key));
						}
					}
				}
			}
			
			if (res == null) {
				res = getResource(value.getSUID());

				// Add name
				if (value.name != null && !value.name.isEmpty()) {
					res.addProperty(E3value.e3_has_name, value.name);
				} else {
					String name = "";

					if (value instanceof Actor) {
						name += "ac";
					} else if (value instanceof MarketSegment) {
						name += "ms";
					} else if (value instanceof ValueActivity) {
						name += "va";
					} else if (value instanceof ValueInterface) {
						name += "vi";
					} else if (value instanceof ValuePort) {
						name += "vp";
					} else if (value instanceof ValueExchange) {
						name += "ve";
					} else if (value instanceof ConnectionElement) {
						name += "ce";
					} else if (value instanceof StartSignal) {
						name += "ss";
					} else if (value instanceof EndSignal) {
						name += "es";
					} else {
						name += "uknown";
					}
					
					name += value.getSUID();

					res.addProperty(E3value.e3_has_name, name);
				}
			
				// Add formulas
				// TODO: What if the value part of the formula is empty? Put zero there or just leave it empty?
				// I guess for now we'll just leave it "empty", the parser on the other side can deal with it
				for (String key : value.formulas.keySet()) {
					res.addProperty(E3value.e3_has_formula, key + "=" + value.formulas.get(key));
				}
			}
			
			if (value instanceof Actor) {
				{
					List<Object> vaChildren = Utils.getChildrenWithValue(graph, cell, ValueActivity.class);
					List<Object> msChildren = Utils.getChildrenWithValue(graph, cell, MarketSegment.class);
					List<Object> acChildren = Utils.getChildrenWithValue(graph, cell, Actor.class);
					
					boolean isElementary = vaChildren.size() >= 0
							&& msChildren.size() == 0
							&& acChildren.size() == 0;
					
					if (isElementary) {
						res.addProperty(RDF.type, E3value.elementary_actor);
					} else {
						res.addProperty(RDF.type, E3value.composite_actor);
					}
				}
				
				List<Resource> viResources = new ArrayList<>();
				for (Object child : Utils.getChildrenWithValue(graph, cell, ValueInterface.class)) {
					Base childInfo = (Base) graph.getModel().getValue(child);
					Resource childRes = getResource(childInfo.getSUID());
					res.addProperty(E3value.ac_has_vi, childRes);
					childRes.addProperty(E3value.vi_assigned_to_ac, res);
					
					viResources.add(childRes);
				}
				
				for (Object child : Utils.getChildrenWithValue(graph, cell, ValueActivity.class)) {
					ValueActivity vaInfo = (ValueActivity) graph.getModel().getValue(child);
					Resource vaRes = getResource(vaInfo.getSUID());
					res.addProperty(E3value.el_performs_va, vaRes);
					vaRes.addProperty(E3value.va_performed_by_el, res);
				}
				
				Object parent = graph.getModel().getParent(cell);
				while (parent != null && parent != graph.getDefaultParent()) {
					Base parentValue = (Base) graph.getModel().getValue(parent);
					Resource parentRes = getResource(parentValue.getSUID());
					for (Resource viRes : viResources) {
						parentRes.addProperty(E3value.ca_consists_of_vi, viRes);
					}
					
					parent = graph.getModel().getParent(parent);
				}
			} else if (value instanceof MarketSegment) {
				res.addProperty(RDF.type, E3value.market_segment);

				for (Object child : Utils.getChildrenWithValue(graph, cell, ValueInterface.class)) {
					Base childInfo = (Base) graph.getModel().getValue(child);
					res.addProperty(E3value.ms_has_vi, getResource(childInfo.getSUID()));
				}
				
				for (Object child : Utils.getChildrenWithValue(graph, cell, ValueActivity.class)) {
					ValueActivity vaInfo = (ValueActivity) graph.getModel().getValue(child);
					Resource vaRes = getResource(vaInfo.getSUID());
					res.addProperty(E3value.ms_performs_va, vaRes);
					vaRes.addProperty(E3value.va_performed_by_ms, res);
				}
			} else if (value instanceof ValueActivity) {
				res.addProperty(RDF.type, E3value.value_activity);

				for (Object child : Utils.getChildrenWithValue(graph, cell, ValueInterface.class)) {
					Base childInfo = (Base) graph.getModel().getValue(child);
					res.addProperty(E3value.va_has_vi, getResource(childInfo.getSUID()));
				}
			} else if (value instanceof ValueInterface) {
				ValueInterface viInfo = (ValueInterface) value;
				res.addProperty(RDF.type, E3value.value_interface);
				
				Base parentValue = (Base) graph.getModel().getValue(graph.getModel().getParent(cell));
				Resource parentRes = getResource(parentValue.getSUID());

				if (parentValue instanceof Actor) {
					Actor acInfo = (Actor) parentValue;
					
					if (acInfo.colluded) {
						res.addProperty(E3value.vi_assigned_to_ac, getResource(colludedID));
					} else {
						res.addProperty(E3value.vi_assigned_to_ac, parentRes);
					}
				} else if (parentValue instanceof MarketSegment) {
					res.addProperty(E3value.vi_assigned_to_ms, parentRes);
				} else if (parentValue instanceof ValueActivity) {
					res.addProperty(E3value.vi_assigned_to_va, parentRes);
				}
				
				getOfferingIn(viInfo.getSUID());
				getOfferingOut(viInfo.getSUID());
			} else if (value instanceof ValuePort) {
				res.addProperty(RDF.type, E3value.value_port);
				ValuePort vpInfo = (ValuePort) value;
				
				Object viCell = graph.getModel().getParent(cell);
				ValueInterface viInfo = (ValueInterface) graph.getModel().getValue(viCell);

				if (((ValuePort) value).incoming) {
					Resource offIn = getOfferingIn(viInfo.getSUID());
					offIn.addProperty(E3value.vo_consists_of_vp, res);					
					res.addProperty(E3value.vp_in_vo, offIn);
				} else {
					Resource offOut = getOfferingOut(viInfo.getSUID());
					offOut.addProperty(E3value.vo_consists_of_vp, res);					
					res.addProperty(E3value.vp_in_vo, offOut);
				}
				
				// False = in
				// True = out
				res.addProperty(E3value.vp_has_dir, (!vpInfo.incoming) + "");
				
				assert(graph.getModel().getEdgeCount(cell) < 2);
				if (graph.getModel().getEdgeCount(cell) == 1) {
					Object valueExchange = graph.getModel().getEdgeAt(cell, 0);
					ValueExchange veInfo = (ValueExchange) graph.getModel().getValue(valueExchange);
					Resource veRes = getResource(veInfo.getSUID());
					
					if (vpInfo.incoming) {
						res.addProperty(E3value.vp_in_connects_ve, veRes);
						veRes.addProperty(E3value.ve_has_in_po, res);
					} else {
						res.addProperty(E3value.vp_out_connects_ve, veRes);
						veRes.addProperty(E3value.ve_has_out_po, res);
					}
					
					if (veInfo.valueObject != null) {
						Resource valueObjectRes = getValueObject(veInfo.valueObject);
						res.addProperty(E3value.vp_requests_offers_vo, valueObjectRes);
						valueObjectRes.addProperty(E3value.vo_offered_requested_by_vp, res);
					}
					
					// Propagate valuation from edge if vp valuation == 0
					if (value.formulas.getOrDefault("VALUATION", "0").equals("0")) {
						value.formulas.put("VALUATION", veInfo.formulas.getOrDefault("VALUATION", "0"));
						res.addProperty(E3value.e3_has_formula, "VALUATION" + "=" + value.formulas.get("VALUATION"));
					}
				}
			} else if (value instanceof ValueExchange) {
				res.addProperty(RDF.type, E3value.value_exchange);
			} else if (value instanceof StartSignal) {
				res.addProperty(RDF.type, E3value.start_stimulus);
				
				ConnectionVisitor cv = new ConnectionVisitor(this);
				try {
					//System.out.println("Starting visitor");
					cv.accept(cell);
				} catch (MalformedFlowException e) {
					System.out.println("Malformed flow! Cause: " + e.subject);
					e.printStackTrace();
				}
			} else if (value instanceof EndSignal) {
				res.addProperty(RDF.type, E3value.end_stimulus);
			} else if (value instanceof ConnectionElement) {
				res.addProperty(RDF.type, E3value.connection_element);
				//System.out.println("Connection element with SUID: " + value.getSUID() + " and name: " + value.name);
			} else if (value instanceof LogicBase) {
				//System.out.println("Adding LogicBase");
				LogicBase lbInfo = (LogicBase) value;
				if (((LogicBase) value).isOr) {
					res.addProperty(RDF.type, E3value.OR_node);
				} else {
					res.addProperty(RDF.type, E3value.AND_node);
				}
			}
		}
		
		// Commit the formuals for the colluded actor
		Resource res = getResource(colludedID);
		for (Entry<String, String> entry : colludedFormulas.entrySet()) {
			res.addProperty(E3value.e3_has_formula, entry.getKey() + "=" + entry.getValue());
		}
		
		// Convert to RDF
		StringWriter out = new StringWriter();
		model.write(out, "RDF/XML");
		result = out.toString();
		
		System.out.println(result);
	}
	
	@Override
	public String toString() {
		return result;
	}
}
