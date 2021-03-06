/*
 * Copyright (C) 2015, 2016 Dan Ionita 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package e3fraud.vocabulary;
import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions from e3value.rdfs 
 * @author Auto-generated by schemagen on 04 Jun 2014 19:02 
 */
public class E3value {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static final Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.cs.vu.nl/~gordijn/e3value#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final Property DEFAULT_ROOT_RELATION = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#DEFAULT_ROOT_RELATION" );
    
    public static final Property ac_has_vi = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ac-has-vi" );
    
    public static final Property ac_in_ms = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ac-in-ms" );
    
    public static final Property bc_has_mo = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#bc-has-mo" );
    
    public static final Property ca_consists_of_vi = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ca-consists-of-vi" );
    
    public static final Property ce_with_down_de = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ce-with-down-de" );
    
    public static final Property ce_with_up_de = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ce-with-up-de" );
    
    public static final Property de_down_ce = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#de-down-ce" );
    
    public static final Property de_up_ce = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#de-up-ce" );
    
    public static final Property di_has_mc = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#di-has-mc" );
    
    public static final Property e3_has_formula = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#e3-has-formula" );
    
    public static final Property e3_has_name = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#e3-has-name" );
    
    public static final Property e3_has_uid = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#e3-has-uid" );
    
    public static final Property el_performs_va = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#el-performs-va" );
    
    public static final Property up_fraction = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#up-fraction" );
    
    public static final Property down_fraction = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#edown-fraction" );
    
    public static final Property mc_in_di = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#mc-in-di" );
    
    public static final Property mc_in_mo = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#mc-in-mo" );
    
    public static final Property mo_has_end_period = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#mo-has-end-period" );
    
    public static final Property mo_has_mc = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#mo-has-mc" );
    
    public static final Property mo_has_start_period = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#mo-has-start-period" );
    
    public static final Property mo_in_bc = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#mo-in-bc" );
    
    public static final Property ms_consists_of_ac = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ms-consists-of-ac" );
    
    public static final Property ms_has_vi = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ms-has-vi" );
    
    // The next two properties (ms_performs_va and va_performed_by_ms) were added by Dan & Bob at 2016-8-16
    // Since we deemed them needed to properly generate RDF models (also the old editor output them just fine)
    public static final Property ms_performs_va = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ms-performs-va" ); 

    public static final Property va_performed_by_ms = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ms-performs-va" ); 
    
    public static final Property va_has_vi = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#va-has-vi" );
    
    public static final Property va_performed_by_el = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#va-performed-by-el" );
    
    public static final Property ve_has_first_percentage = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ve-has-first-percentage" );
    
    public static final Property ve_has_first_vp = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ve-has-first-vp" );
    
    public static final Property ve_has_in_percentage = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ve-has-in-percentage" );
    
    public static final Property ve_has_in_po = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ve-has-in-po" );
    
    public static final Property ve_has_out_percentage = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ve-has-out-percentage" );
    
    public static final Property ve_has_out_po = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ve-has-out-po" );
    
    public static final Property ve_has_second_percentage = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ve-has-second-percentage" );
    
    public static final Property ve_has_second_vp = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ve-has-second-vp" );
    
    public static final Property ve_in_vt = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ve-in-vt" );
    
    public static final Property ve_valuation = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#ve-valuation" );
    
    public static final Property vi_assigned_to_ac = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vi-assigned-to-ac" );
    
    public static final Property vi_assigned_to_ms = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vi-assigned-to-ms" );
    
    public static final Property vi_assigned_to_va = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vi-assigned-to-va" );
    
    public static final Property vi_consists_of_of = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vi-consists-of-of" );
    
    public static final Property vi_in_ca = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vi-in-ca" );
    
    public static final Property vo_consists_of_vp = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vo-consists-of-vp" );
    
    public static final Property vo_in_vi = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vo-in-vi" );
    
    public static final Property vo_offered_requested_by_vp = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vo-offered-requested-by-vp" );
    
    public static final Property vp_first_connects_ve = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vp-first-connects-ve" );
    
    public static final Property vp_has_dir = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vp-has-dir" );
    
    public static final Property vp_in_connects_ve = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vp-in-connects-ve" );
    
    public static final Property vp_in_vo = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vp-in-vo" );
    
    public static final Property vp_out_connects_ve = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vp-out-connects-ve" );
    
    public static final Property vp_requests_offers_vo = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vp-requests-offers-vo" );
    
    public static final Property vp_second_connects_ve = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vp-second-connects-ve" );
    
    public static final Property vp_valuation = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vp-valuation" );
    
    public static final Property vt_consists_of_ve = m_model.createProperty( "http://www.cs.vu.nl/~gordijn/e3value#vt-consists-of-ve" );
    
    public static final Resource AND_fork = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#AND-fork" );
    
    public static final Resource AND_join = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#AND-join" );
    
    public static final Resource DEFAULT_ROOT_CONCEPT = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#DEFAULT_ROOT_CONCEPT" );
    
    public static final Resource OR_fork = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#OR-fork" );
    
    public static final Resource OR_join = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#OR-join" );
        
    public static final Resource OR_node = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#e3value-OR" );
    
    public static final Resource AND_node= m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#e3value-AND" );
    
    public static final Resource actor = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#actor" );
    
    public static final Resource business_case = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#business-case" );
    
    public static final Resource composite_actor = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#composite-actor" );
    
    public static final Resource concept_11705501_7_f09b989266 = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#concept_11705501_7_f09b989266" );
    
    public static final Resource concept_9873235_0_f09ba1c05d = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#concept_9873235_0_f09ba1c05d" );
    
    public static final Resource connection_element = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#connection-element" );
    
    public static final Resource dependency_element = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#dependency-element" );
    
    public static final Resource diagram = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#diagram" );
    
    public static final Resource e3value_object = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#e3value-object" );
    
    public static final Resource elementary_actor = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#elementary-actor" );
    
    public static final Resource end_stimulus = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#end-stimulus" );
    
    public static final Resource market_segment = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#market-segment" );
    
    public static final Resource model = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#model" );
    
    public static final Resource model_concept = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#model-concept" );
    
    public static final Resource start_stimulus = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#start-stimulus" );
    
    public static final Resource value_activity = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#value-activity" );
    
    public static final Resource value_exchange = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#value-exchange" );
    
    public static final Resource value_interface = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#value-interface" );
    
    public static final Resource value_object = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#value-object" );
    
    public static final Resource value_offering = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#value-offering" );
    
    public static final Resource value_port = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#value-port" );
    
    public static final Resource value_transaction = m_model.createResource( "http://www.cs.vu.nl/~gordijn/e3value#value-transaction" );
    
    
    
}
