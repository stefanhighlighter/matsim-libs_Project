package Project09.Generation;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationWriter;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlanWriter {
	private static final String LEGS_FILE = "/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/Generation/legs10.csv";
	private static final String OUTPUT_FILE = "/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/Generation/population10.xml";

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
				String mode = values[11].replace("\"", "");

				if (!mode.equals("CAR_DRIVER")) {
					continue;
				}

				Id<Person> personId = Id.createPersonId(personIdStr);
				Person person = personMap.computeIfAbsent(personIdStr, id -> {
					Person newPerson = existingPopulation.getFactory().createPerson(personId);
					existingPopulation.addPerson(newPerson);
					return newPerson;
				});

				Plan plan = person.getSelectedPlan();
				if (plan == null) {
					plan = existingPopulation.getFactory().createPlan();
					person.addPlan(plan);
				}

				String previousPurpose = values[1].replace("\"", "");
				double startX = Double.parseDouble(values[3]);
				double startY = Double.parseDouble(values[4]);
				Coord startCoord = new Coord(startX, startY);

				Activity activity = existingPopulation.getFactory().createActivityFromCoord(previousPurpose, startCoord);
				activity.setEndTime((Double.parseDouble(values[2]) * 60));
				plan.addActivity(activity);

				Leg leg = existingPopulation.getFactory().createLeg(TransportMode.car);
				leg.setMode("car");
				plan.addLeg(leg);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading CSV file: " + LEGS_FILE);
		}

		PopulationWriter writer = new PopulationWriter(existingPopulation, null);
		writer.write(OUTPUT_FILE);
		System.out.println("Plan file generated successfully.");
	}
}
