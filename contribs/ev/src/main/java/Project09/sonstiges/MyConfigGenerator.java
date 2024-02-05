/*
package Project09.sonstiges;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.ev.EvModule;
import org.matsim.contrib.ev.charging.VehicleChargingHandler;
import org.matsim.contrib.ev.fleet.ElectricFleetSpecification;
import org.matsim.contrib.ev.fleet.ElectricFleetSpecificationImpl;
import org.matsim.contrib.ev.infrastructure.ChargingInfrastructure;
import org.matsim.contrib.ev.infrastructure.ChargingInfrastructureSpecification;
import org.matsim.contrib.ev.infrastructure.ChargingInfrastructureSpecificationImpl;
import org.matsim.contrib.ev.routing.EvNetworkRoutingProvider;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.NetworkConfigGroup;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.config.groups.VehiclesConfigGroup;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.mobsim.qsim.AbstractQSimModule;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.contrib.ev.EvConfigGroup;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class MyConfigGenerator {
    public static void main(String[] args) throws URISyntaxException, MalformedURLException {

        Config myConfig = ConfigUtils.createConfig();

        // global setting

        myConfig.qsim().setStartTime(0);
        myConfig.qsim().setEndTime(24*60*60-1);
        myConfig.qsim().setSnapshotPeriod(0);
        myConfig.qsim().setStorageCapFactor(0.02);
        myConfig.qsim().setFlowCapFactor(0.02);

        myConfig.planCalcScore().setLearningRate(1);
        myConfig.planCalcScore().setBrainExpBeta(2);
        myConfig.planCalcScore().setLateArrival_utils_hr(-18);
        myConfig.planCalcScore().setEarlyDeparture_utils_hr(0);
        myConfig.planCalcScore().setPerforming_utils_hr(6);
        myConfig.planCalcScore().setMarginalUtlOfWaiting_utils_hr(0);

        PlanCalcScoreConfigGroup.ActivityParams charging = new PlanCalcScoreConfigGroup.ActivityParams("car charging interaction");
        charging.setActivityType("car charging interaction");
        charging.setOpeningTime(0);
        charging.setPriority(1);
        charging.setClosingTime(24*60*60);
        charging.setScoringThisActivityAtAll(false);
        charging.setTypicalDuration(1.5*60*60);
        charging.setTypicalDurationScoreComputation(PlanCalcScoreConfigGroup.TypicalDurationScoreComputation.relative);
        myConfig.planCalcScore().addActivityParams(charging);


        PlanCalcScoreConfigGroup.ActivityParams home = new PlanCalcScoreConfigGroup.ActivityParams("HOME");
        home.setActivityType("HOME");
        home.setPriority(1);
        home.setTypicalDuration(16 * 60 * 60);
        myConfig.planCalcScore().addActivityParams(home);

        //work
        PlanCalcScoreConfigGroup.ActivityParams work = new PlanCalcScoreConfigGroup.ActivityParams("WORK");
        work.setActivityType("WORK");
        work.setPriority(1);
        work.setTypicalDuration(8 * 60 * 60);
        work.setOpeningTime(7 * 60 * 60);
        work.setLatestStartTime(9 * 60 * 60);
        work.setEarliestEndTime(0);
        work.setClosingTime(18 * 60 * 60);
        myConfig.planCalcScore().addActivityParams(work);

        //education
        PlanCalcScoreConfigGroup.ActivityParams education = new PlanCalcScoreConfigGroup.ActivityParams("EDUCATION");
        education.setActivityType("EDUCATION");
        education.setPriority(1);
        education.setTypicalDuration(8 * 60 * 60);
        education.setOpeningTime(7 * 60 * 60);
        education.setLatestStartTime(9 * 60 * 60);
        education.setEarliestEndTime(0);
        education.setClosingTime(18 * 60 * 60);
        myConfig.planCalcScore().addActivityParams(education);

        // other
        PlanCalcScoreConfigGroup.ActivityParams other = new PlanCalcScoreConfigGroup.ActivityParams("OTHER");
        other.setActivityType("OTHER");
        other.setTypicalDuration(2*60*60);
        myConfig.planCalcScore().addActivityParams(other);

        // subtour
        PlanCalcScoreConfigGroup.ActivityParams subtour = new PlanCalcScoreConfigGroup.ActivityParams("SUBTOUR");
        subtour.setActivityType("SUBTOUR");
        subtour.setTypicalDuration(2*60*60);
        myConfig.planCalcScore().addActivityParams(subtour);

        // accompany
        PlanCalcScoreConfigGroup.ActivityParams accompany = new PlanCalcScoreConfigGroup.ActivityParams("ACCOMPANY");
        accompany.setActivityType("ACCOMPANY");
        accompany.setTypicalDuration(8*60*60);
        myConfig.planCalcScore().addActivityParams(accompany);

        // recreation
        PlanCalcScoreConfigGroup.ActivityParams recreation = new PlanCalcScoreConfigGroup.ActivityParams("RECREATION");
        recreation.setActivityType("RECREATION");
        recreation.setTypicalDuration(2*60*60);
        myConfig.planCalcScore().addActivityParams(recreation);

        // shopping
        PlanCalcScoreConfigGroup.ActivityParams shopping = new PlanCalcScoreConfigGroup.ActivityParams("SHOPPING");
        shopping.setActivityType("SHOPPING");
        shopping.setTypicalDuration(2*60*60);
        myConfig.planCalcScore().addActivityParams(shopping);

        // Strategy
        myConfig.strategy().setMaxAgentPlanMemorySize(5);
        myConfig.strategy().setFractionOfIterationsToDisableInnovation(0.8);


        StrategyConfigGroup.StrategySettings reRoute = new StrategyConfigGroup.StrategySettings();
        reRoute.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ReRoute);
        reRoute.setWeight(0.1);
        myConfig.strategy().addStrategySettings(reRoute);

        StrategyConfigGroup.StrategySettings bestScore = new StrategyConfigGroup.StrategySettings();
        bestScore.setStrategyName(DefaultPlanStrategiesModule.DefaultSelector.BestScore);
        bestScore.setWeight(0.9);
        myConfig.strategy().addStrategySettings(bestScore);

        myConfig.controler().setOutputDirectory("/scenarios/basicScenario");
        myConfig.controler().setFirstIteration(0);
        myConfig.controler().setLastIteration(10);
        myConfig.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.overwriteExistingFiles);




        // EV config File
        EvConfigGroup evConfigGroup = new EvConfigGroup();
        evConfigGroup.setAuxDischargeTimeStep(10);
        evConfigGroup.setTimeProfiles(true);
        evConfigGroup.setChargeTimeStep(5);

        evConfigGroup.setChargersFile("scenarios/Freising/freising_chargers.xml");
        evConfigGroup.setVehiclesFile("scenarios/Freising/evehicles_500.xml");


        // network config group
        NetworkConfigGroup networkConfigGroup = new NetworkConfigGroup();
        networkConfigGroup.setInputFile("scenarios/Freising/freising_network.xml.gz");

        myConfig.network().setInputFile("scenarios/Freising/freising_network.xml.gz");
        myConfig.plans().setInputFile("scenarios/Freising/population_500.xml");


        // vehicle config group
        VehiclesConfigGroup vehiclesConfigGroup = new VehiclesConfigGroup();
        vehiclesConfigGroup.setVehiclesFile("scenarios/Freising/evehicles_500.xml");





        myConfig.addModule(evConfigGroup);


        myConfig.controler().setOutputDirectory("scenarios/output");
        myConfig.controler().setFirstIteration(0);
        myConfig.controler().setLastIteration(10);
        myConfig.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);


        // save config file
        ConfigUtils.writeConfig(myConfig,"scenarios\\Freising\\new_config.xml");


        Scenario scenario = ScenarioUtils.loadScenario(myConfig);
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
*/
