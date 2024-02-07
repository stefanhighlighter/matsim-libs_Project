package Project09;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.ev.EvConfigGroup;
import org.matsim.contrib.ev.EvModule;
import org.matsim.contrib.ev.routing.EvNetworkRoutingProvider;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class RunEv_Project {
	static final String DEFAULT_CONFIG_FILE = "/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/input/config_Project.xml";
	private static final Logger log = LogManager.getLogger(RunEv_Project.class);

	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			log.info("Starting simulation run with the following arguments:");
			log.info("args=" + Arrays.toString( args ) );
		} else {
			File localConfigFile = new File(DEFAULT_CONFIG_FILE);
			if (localConfigFile.exists()) {
				log.info("Starting simulation run with the local config file");
				args = new String[] {DEFAULT_CONFIG_FILE};
			} else {
				log.info("Starting simulation run with the example config file from GitHub repository");
				args = new String[] {"https://raw.githubusercontent.com/matsim-org/matsim/master/contribs/ev/"
						+ DEFAULT_CONFIG_FILE};
			}
		}
		new RunEv_Project().run(args);
	}

	public void run( String[] args ) {
		Config config = ConfigUtils.loadConfig(args, new EvConfigGroup());
		config.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
		Scenario scenario = ScenarioUtils.loadScenario(config);
		Controler controler = new Controler(scenario);
		controler.addOverridingModule( new AbstractModule(){
			@Override public void install(){
				install( new EvModule() );

				addRoutingModuleBinding( TransportMode.car ).toProvider(new EvNetworkRoutingProvider(TransportMode.car) );
				// a router that inserts charging activities INTO THE ROUTE when the battery is run empty.  This assumes that a full
				// charge at the start of the route is not sufficient to drive the route.   There are other settings where the
				// situation is different, e.g. urban, where there may be a CHAIN of activities, and charging in general is done in
				// parallel with some of these activities.   That second situation is adressed by some "ev" code in the vsp contrib.
				// kai, dec'22
			}
		} );

		controler.run();
	}
}
