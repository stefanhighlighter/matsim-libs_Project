package Project09.sonstiges;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.population.io.PopulationWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.api.core.v01.Scenario;
import java.util.Random;
import org.matsim.api.core.v01.population.Population;


public class ElectricPlansGenerator {

    public static void main(String[] args) {
        Config config = ConfigUtils.createConfig();
        Scenario scenario = ScenarioUtils.createScenario(config);
        Population population = scenario.getPopulation();

        PopulationReader populationReader = new PopulationReader(scenario);
        populationReader.readFile("/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/input/plans_Project.xml");

        Random random = new Random();

        for (Person person : population.getPersons().values()) {
            if (random.nextDouble() <= 0.05) {
                Plan selectedPlan = person.getSelectedPlan();
                if (selectedPlan != null) {
                    for (PlanElement planElement : selectedPlan.getPlanElements()) {
                        if (planElement instanceof Leg && "car".equals(((Leg) planElement).getMode())) {
                            ((Leg) planElement).setMode("car");
                        }
                    }
                }
            }
        }

        // Write the modified population to a new XML file
        PopulationWriter writer = new PopulationWriter(population, null);
        writer.write("/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/input/ev_population_Project.xml");

        System.out.println("EV plans file generated successfully.");
    }
}
