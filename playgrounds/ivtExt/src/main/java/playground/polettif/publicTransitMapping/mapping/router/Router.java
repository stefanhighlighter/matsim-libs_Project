/*
 * *********************************************************************** *
 * project: org.matsim.*                                                   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2015 by the members listed in the COPYING,        *
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
 * *********************************************************************** *
 */

package playground.polettif.publicTransitMapping.mapping.router;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.pt.transitSchedule.api.TransitRouteStop;
import playground.polettif.publicTransitMapping.mapping.pseudoRouter.LinkCandidate;
import playground.polettif.publicTransitMapping.mapping.v2.ArtificialLink;

/**
 * A Router interface combining travelDisUtility and TravelTime.
 *
 * @author polettif
 */
public interface Router extends TravelDisutility, TravelTime {

    /**
     *
     * @param fromNode  Node to route from...
     * @param toNode    Node to route to...
     * @return  Least cost path.
     */
    LeastCostPathCalculator.Path calcLeastCostPath(Node fromNode, Node toNode);

    Network getNetwork();

	double getMinimalTravelCost(TransitRouteStop fromStop, TransitRouteStop toStop);

	double getLinkTravelCost(Link link);

	double getArtificialLinkFreeSpeed(double maxAllowedTravelCost, LinkCandidate fromLinkCandidate, LinkCandidate toLinkCandidate);

	double getArtificialLinkLength(double maxAllowedTravelCost, LinkCandidate linkCandidateCurrent, LinkCandidate linkCandidateNext);
}
