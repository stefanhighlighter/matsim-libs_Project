/* *********************************************************************** *
 * project: org.matsim.*
 * Plansgenerator.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package playground.droeder.data.graph.algorithms;

import java.util.List;

import playground.droeder.data.graph.MatchingGraph;
import playground.droeder.data.graph.MatchingNode;
import playground.droeder.data.graph.algorithms.interfaces.NodeAlgorithm;


/**
 * @author droeder
 *
 */
public class NodeDistAlgo implements NodeAlgorithm{
	
	private Double deltaDist;
	private MatchingGraph candGraph;

	public NodeDistAlgo(Double deltaDist, MatchingGraph candGraph){
		this.deltaDist = deltaDist;
		this.candGraph = candGraph;
	}
	

	@Override
	public List<MatchingNode> run(MatchingNode ref, List<MatchingNode> candidates) {
		return this.candGraph.getNearestNodes(ref.getCoord().getX(), ref.getCoord().getY(), deltaDist);
	}


}
