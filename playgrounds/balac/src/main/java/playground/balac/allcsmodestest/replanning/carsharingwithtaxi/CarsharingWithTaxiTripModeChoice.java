package playground.balac.allcsmodestest.replanning.carsharingwithtaxi;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.replanning.modules.AbstractMultithreadedModule;
import org.matsim.core.router.TripRouter;
import org.matsim.population.algorithms.PlanAlgorithm;
/**
 * @author balacm
 */
public class CarsharingWithTaxiTripModeChoice extends AbstractMultithreadedModule{

	private String[] availableModes = null;
	private boolean ignoreCarAvailability = true;
	
	private final Scenario scenario;

	public CarsharingWithTaxiTripModeChoice(final Scenario scenario) {
		super(scenario.getConfig().global().getNumberOfThreads());

		// try to get the modes from the "changeLegMode" module of the config file

		this.scenario = scenario;
		
		if (Boolean.parseBoolean(scenario.getConfig().getModule("OneWayCarsharing").getValue("useOneWayCarsharing") )) {
			
			this.availableModes = new String[2];
			this.availableModes[0] = "taxi";
			this.availableModes[1] = "onewaycarsharing";
		}
		if (Boolean.parseBoolean(scenario.getConfig().getModule("FreeFloating").getValue("useFreeFloating") )) {
			if (this.availableModes == null) {
				this.availableModes = new String[2];
				this.availableModes[0] = "taxi";
				this.availableModes[1] = "freefloating";
			}
			else {
				this.availableModes = new String[3];
				this.availableModes[0] = "taxi";
				this.availableModes[1] = "onewaycarsharing";
				this.availableModes[2] = "freefloating";
			}
		}
		
		if (!Boolean.parseBoolean(scenario.getConfig().getModule("FreeFloating").getValue("useFreeFloating") ) 
				&& !Boolean.parseBoolean(scenario.getConfig().getModule("OneWayCarsharing").getValue("useOneWayCarsharing") )) {
			this.availableModes = new String[1];
			this.availableModes[0] = "taxi";
		}
			
	}
	
	
	@Override
	public PlanAlgorithm getPlanAlgoInstance() {
		final TripRouter tripRouter = getReplanningContext().getTripRouter();
		ChooseRandomTripModeWithTaxi algo = new ChooseRandomTripModeWithTaxi(this.scenario, this.availableModes, MatsimRandom.getLocalInstance(), tripRouter.getStageActivityTypes());
		algo.setIgnoreCarAvailability(this.ignoreCarAvailability);
		return algo;
	}

}
