package Project09.sonstiges;

import org.apache.commons.lang3.RandomUtils;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.population.io.PopulationWriter;
import org.matsim.core.scenario.ScenarioUtils;
import java.util.Random;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.core.utils.misc.OptionalTime;




public class ModifiedPlans {
    public static void main(String[] args) {
        // Load the existing plans from XML
        Config config = ConfigUtils.createConfig();
        Scenario scenario = ScenarioUtils.createScenario(config);
        Population population = scenario.getPopulation();

        PopulationReader populationReader = new PopulationReader(scenario);
        populationReader.readFile("src/main/java/Project09/plans_Project.xml");

        // Create a new scenario with a new population
        Scenario modifiedScenario = ScenarioUtils.createScenario(config);
        Population modifiedPopulation = modifiedScenario.getPopulation();

        Random random = new Random();

        for (Person person : population.getPersons().values()) {
            // Check if we randomly selected this person (50% chance)
            if (random.nextDouble() <= 0.5) {
                // Modify the existing person in the population
                modifyPerson(person, modifiedPopulation);
            }
        }

        // Write the modified population to a new XML file
        PopulationWriter populationWriter = new PopulationWriter(modifiedPopulation, modifiedScenario.getNetwork());
        populationWriter.write("src/main/java/Project09/ModifiedPlans.xml");

        System.out.println("Modified plans file generated successfully.");
    }

    // Helper method to modify a person's plan elements
    private static void modifyPerson(Person person, Population modifiedPopulation) {
        Plan selectedPlan = person.getSelectedPlan();
        if (selectedPlan != null) {
            Person modifiedPerson = modifiedPopulation.getFactory().createPerson(person.getId());
            Plan modifiedPlan = modifiedPopulation.getFactory().createPlan();

            for (PlanElement planElement : selectedPlan.getPlanElements()) {
                if (planElement instanceof Leg) {
                    // Modify the leg mode to "ELECTRIC_CAR"
                    Leg modifiedLeg = modifiedPopulation.getFactory().createLeg("ELECTRIC_CAR");
                    OptionalTime departureTime = ((Leg) planElement).getDepartureTime();
                    OptionalTime travelTime = ((Leg) planElement).getTravelTime();

                    if (departureTime.isDefined()) {
                        modifiedLeg.setDepartureTime(departureTime.seconds());
                    }

                    if (travelTime.isDefined()) {
                        modifiedLeg.setTravelTime(travelTime.seconds());
                    }

                    modifiedPlan.addLeg(modifiedLeg);
                } else if (planElement instanceof Activity) {
                    // Clone and add activity to the modified plan
                    Activity originalActivity = (Activity) planElement;
                    Activity modifiedActivity = modifiedPopulation.getFactory().createActivityFromCoord(originalActivity.getType(), originalActivity.getCoord());

                    OptionalTime startTime = originalActivity.getStartTime();
                    OptionalTime endTime = originalActivity.getEndTime();

                    if (startTime.isDefined()) {
                        modifiedActivity.setStartTime(startTime.seconds());
                    }

                    if (endTime.isDefined()) {
                        modifiedActivity.setEndTime(endTime.seconds());
                    }

                    modifiedPlan.addActivity(modifiedActivity);
                }
            }

            modifiedPerson.addPlan(modifiedPlan);
            modifiedPopulation.addPerson(modifiedPerson);
        }
    }
}
