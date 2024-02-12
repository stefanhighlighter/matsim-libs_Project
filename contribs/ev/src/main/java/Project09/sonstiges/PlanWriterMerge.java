package Project09.sonstiges;

import org.matsim.api.core.v01.*;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationWriter;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PlanWriterMerge {
	private static final String LEGS_FILE = "/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/Generation/legs10.csv";
	private static final String OUTPUT_FILE = "/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/Generation/populationMerged.xml";

	public static void main(String[] args) {
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);

		Population existingPopulation = scenario.getPopulation();
		Map<String, Person> personMap = new HashMap<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(LEGS_FILE))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				String personIdStr = values[0];
				String mode = values[11];

				if (!mode.equals("\"CAR_DRIVER\"")) {
					continue;
				}

				double startX = Double.parseDouble(values[3]);
				double startY = Double.parseDouble(values[4]);
				Coord homeCoord = new Coord(startX, startY);
				String homeCoordKey = getHomeCoordKey(homeCoord);

				Person person = personMap.get(homeCoordKey);
				if (person == null) {
					Id<Person> personId = Id.createPersonId(personIdStr);
					person = existingPopulation.getFactory().createPerson(personId);
					existingPopulation.addPerson(person);
					personMap.put(homeCoordKey, person);
				} else {
					personMap.put(homeCoordKey, person);
				}

				Plan plan = person.getSelectedPlan();
				if (plan == null) {
					plan = existingPopulation.getFactory().createPlan();
					person.addPlan(plan);
				}

				String previousPurpose = values[1].replace("\"", "");
				Coord startCoord = new Coord(startX, startY);

				Activity activity = existingPopulation.getFactory().createActivityFromCoord(previousPurpose, startCoord);
				activity.setEndTime((Double.parseDouble(values[2]) * 60));
				plan.addActivity(activity);

				String nextPurpose = values[6].replace("\"", "");
				if (nextPurpose.equals("HOME")) {
					double endX = Double.parseDouble(values[8]);
					double endY = Double.parseDouble(values[9]);
					Coord endCoord = new Coord(endX, endY);

					Activity finalActivity = existingPopulation.getFactory().createActivityFromCoord(nextPurpose, endCoord);
					finalActivity.setEndTime((Double.parseDouble(values[7]) * 60));
					plan.addActivity(finalActivity);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading CSV file: " + LEGS_FILE);
		}

		PopulationWriter writer = new PopulationWriter(existingPopulation, null);
		writer.write(OUTPUT_FILE);
		System.out.println("Plan file generated successfully.");
	}

	private static String getHomeCoordKey(Coord homeCoord) {
		return homeCoord.getX() + "_" + homeCoord.getY();
	}
}
