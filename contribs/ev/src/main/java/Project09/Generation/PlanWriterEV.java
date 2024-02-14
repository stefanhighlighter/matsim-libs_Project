package Project09.Generation;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
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


public class PlanWriterEV {
	private static final String POPULATION_FILE = "/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/Generation/populationMerged.xml";
	private static final String OUTPUT_FILE = "/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/Generation/evPopulation.xml";
	private static final double EV_PERCENTAGE = 0.05;

	public static void main(String[] args) {
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		Population population = scenario.getPopulation();

		PopulationReader populationReader = new PopulationReader(scenario);
		populationReader.readFile(POPULATION_FILE);

		Scenario modifiedScenario = ScenarioUtils.createScenario(config);
		Population modifiedPopulation = modifiedScenario.getPopulation();
		Random random = new Random();

		for (Person person : population.getPersons().values()) {
			if (random.nextDouble() <= EV_PERCENTAGE) {
				modifyPerson(person, modifiedPopulation);
			}
		}

		PopulationWriter populationWriter = new PopulationWriter(modifiedPopulation, modifiedScenario.getNetwork());
		populationWriter.write(OUTPUT_FILE);

		System.out.println("Modified plans file generated successfully.");
	}

	private static void modifyPerson(Person person, Population modifiedPopulation) {
		Plan selectedPlan = person.getSelectedPlan();
		if (selectedPlan != null) {
			Person modifiedPerson = modifiedPopulation.getFactory().createPerson(person.getId());
			Plan modifiedPlan = modifiedPopulation.getFactory().createPlan();

			for (PlanElement planElement : selectedPlan.getPlanElements()) {
				if (planElement instanceof Leg) {
					Leg modifiedLeg = modifiedPopulation.getFactory().createLeg("car");
					modifiedPlan.addLeg(modifiedLeg);
				} else if (planElement instanceof Activity) {
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
