package Project09.sonstiges;

import org.matsim.api.core.v01.*;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanMergerOld {

	private static final String INPUT_FILE = "/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/Generation/population10.xml";
	private static final String OUTPUT_FILE = "/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/Generation/populationMerged.xml";

	public static void main(String[] args) {
		Config config = ConfigUtils.createConfig();
		Population population = PopulationUtils.createPopulation(config);

		Map<String, Person> personMap = new HashMap<>();
		List<Id<Person>> personsToRemove = new ArrayList<>();
		population = PopulationUtils.readPopulation(INPUT_FILE);

		for (Person person : population.getPersons().values()) {
			Plan plan = person.getSelectedPlan();
			if (plan != null && !plan.getPlanElements().isEmpty()) {
				PlanElement firstElement = plan.getPlanElements().get(0);
				if (firstElement instanceof Activity) {
					Activity firstActivity = (Activity) firstElement;
					if (firstActivity.getType().equals("HOME") && firstActivity.getCoord() != null) {
						String homeCoordKey = getKeyFromCoord(firstActivity.getCoord());
						Person existingPerson = personMap.get(homeCoordKey);
						if (existingPerson != null) {
							combinePlans(existingPerson.getSelectedPlan(), plan);
							personsToRemove.add(person.getId());
						} else {
							personMap.put(homeCoordKey, person);
						}
					}
				}
			}
		}

		for (Id<Person> personId : personsToRemove) {
			population.removePerson(personId);
		}

		PopulationWriter populationWriter = new PopulationWriter(population);
		populationWriter.write(OUTPUT_FILE);
		System.out.println("Combined population file generated successfully.");
	}

	private static String getKeyFromCoord(Coord coord) {
		return String.format("%.6f_%.6f", coord.getX(), coord.getY());
	}

	private static void combinePlans(Plan targetPlan, Plan sourcePlan) {
		for (int i = 0; i < sourcePlan.getPlanElements().size(); i++) {
			PlanElement planElement = sourcePlan.getPlanElements().get(i);
			if (planElement instanceof Activity) {
				Activity activity = (Activity) planElement;
				if (i == sourcePlan.getPlanElements().size() - 1) {
					activity.setType("HOME END");
				}
				targetPlan.addActivity(activity);
			} else if (planElement instanceof Leg) {
				targetPlan.addLeg((Leg) planElement);
			}
		}
	}
}
