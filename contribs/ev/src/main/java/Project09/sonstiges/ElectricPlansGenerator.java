package Project09.sonstiges;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.population.io.PopulationWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.api.core.v01.Scenario;
import java.util.Random;
import org.matsim.api.core.v01.population.Population;


public class ElectricPlansGenerator {

    public static void main(String[] args) {
        // Load the existing plans from XML
        Config config = ConfigUtils.createConfig();
        Scenario scenario = ScenarioUtils.createScenario(config);
        Population population = scenario.getPopulation();

        PopulationReader populationReader = new PopulationReader(scenario);
        populationReader.readFile("src/main/java/Project09/plans_Project.xml");


        Random random = new Random();

        for (Person person : population.getPersons().values()) {
            // Check if we randomly select this person (5% chance)
            if (random.nextDouble() <= 0.05) {
                // Iterate over the selected plan of the person
                Plan selectedPlan = person.getSelectedPlan();
                if (selectedPlan != null) {
                    // Iterate over the plan elements of the selected plan
                    for (PlanElement planElement : selectedPlan.getPlanElements()) {
                        // Check if the plan element is a leg and the leg mode is "CAR_DRIVER"
                        if (planElement instanceof Leg && "CAR_DRIVER".equals(((Leg) planElement).getMode())) {
                            // Set the mode to "ELECTRIC_CAR"
                            ((Leg) planElement).setMode("ELECTRIC_CAR");
                        }
                    }
                }
            }
        }

        // Write the modified population to a new XML file
        PopulationWriter writer = new PopulationWriter(population, null);
        writer.write("src/main/java/Project09/ElectricPlans.xml");

        System.out.println("EV plans file generated successfully.");
    }
}
