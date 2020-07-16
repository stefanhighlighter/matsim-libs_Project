package org.matsim.contrib.freight.controler;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.freight.carrier.Carrier;
import org.matsim.contrib.freight.carrier.CarrierService;
import org.matsim.contrib.freight.carrier.CarrierVehicle;

public final class LSPServiceStartEvent extends Event{

	public static final String ATTRIBUTE_PERSON = "driver";
	public static final String EVENT_TYPE = "service ends";
	public static final String ATTRIBUTE_LINK = "link";
	public static final String ATTRIBUTE_ACTTYPE = "actType";
	public static final String ATTRIBUTE_SERVICE = "service";
	public static final String ATTRIBUTE_VEHICLE = "vehicle";
	public static final String ATTRIBUTE_CARRIER = "carrier";
	
	
	private CarrierService service;
	private Id<Carrier> carrierId;
	private Id<Person> driverId; 
	private CarrierVehicle vehicle;	
	
	public LSPServiceStartEvent(ActivityStartEvent event, Id<Carrier> carrierId, Id<Person> driverId, CarrierService service, double time, CarrierVehicle vehicle) {
		super(time);
		this.carrierId = carrierId;
		this.service = service;
		this.driverId = driverId;
		this.vehicle = vehicle;
	}

	@Override
	public String getEventType() {
		return "service";
	}

	public CarrierService getService() {
		return service;
	}

	public Id<Carrier> getCarrierId() {
		return carrierId;
	}

	public Id<Person> getDriverId() {
		return driverId;
	}

	public CarrierVehicle getVehicle() {
		return vehicle;
	}
	
}
