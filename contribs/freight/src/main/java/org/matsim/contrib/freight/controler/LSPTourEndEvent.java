package org.matsim.contrib.freight.controler;

import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.freight.carrier.Carrier;
import org.matsim.contrib.freight.carrier.CarrierVehicle;
import org.matsim.contrib.freight.carrier.Tour;
import org.matsim.contrib.freight.carrier.Tour.End;

public final class LSPTourEndEvent extends Event{

	public static final String EVENT_TYPE = "freight tour ended";
	public static final String ATTRIBUTE_VEHICLE = "vehicle";
	public static final String ATTRIBUTE_LINK = "link";
	public static final String ATTRIBUTE_CARRIER = "carrier";
	public static final String ATTRIBUTE_DRIVER = "driver";
	public static final String ATTRIBUTE_TOUR = "tour";	
	
	
	private Id<Carrier> carrierId;
	private Id<Person> driverId;
	private Tour tour;
	private CarrierVehicle vehicle;
	
	public LSPTourEndEvent(Id<Carrier>  carrierId, Id<Person> driverId, Tour tour, double time, CarrierVehicle vehicle) {
		super(time);
		this.carrierId = carrierId;
		this.driverId = driverId;
		this.tour = tour;
		this.vehicle = vehicle;
	}

	@Override
	public String getEventType() {
		return EVENT_TYPE;
	}

	public Id<Carrier> getCarrierId() {
		return carrierId;
	}

	public Id<Person> getDriverId() {
		return driverId;
	}

	public Tour getTour() {
		return tour;
	}
	
	public CarrierVehicle getVehicle() {
		return vehicle;
	}

	@Override
	public Map<String, String> getAttributes() {
		Map<String, String> attr = super.getAttributes();
		attr.put(ATTRIBUTE_VEHICLE, this.vehicle.getVehicleId().toString());
		attr.put(ATTRIBUTE_LINK, this.tour.getStartLinkId().toString());
		attr.put(ATTRIBUTE_CARRIER, this.carrierId.toString());
		attr.put(ATTRIBUTE_DRIVER, this.driverId.toString());
		attr.put(ATTRIBUTE_TOUR, this.tour.toString());
		return attr;
	}
}
