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

public class PlanWriterAlternative {

	public static void main(String[] args) {
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		Population existingPopulation = scenario.getPopulation();

		Map<String, Person> personMap = new HashMap<>();

		try (BufferedReader reader = new BufferedReader( new FileReader("/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/Generation/legs.csv"))) {
			String line;
			boolean isFirstLine = true;
			while ((line = reader.readLine()) != null) {
				if (isFirstLine) {
					isFirstLine = false;
					continue;
				}
				line = line.replaceAll("\"", "");
				String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

				if (values.length >= 14) {
					String personIdStr = values[0];
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

					if ("\"CAR_DRIVER\"".equals(values[11])) {
						String previousPurpose = values[1];
						Coord previousCoord = new Coord(
							Double.parseDouble(values[3]),
							Double.parseDouble(values[4])
						);

						Activity previousActivity = existingPopulation.getFactory().createActivityFromCoord(previousPurpose, previousCoord);
						previousActivity.setEndTime((Double.parseDouble(values[2]) % 1440) * 60);
						plan.addActivity(previousActivity);

						Leg leg = existingPopulation.getFactory().createLeg(TransportMode.car);
						leg.setMode(values[11]);
						plan.addLeg(leg);
					}
				} else {
					System.err.println("Skipping line due to insufficient values: " + line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading legs CSV file");
		}

		PopulationWriter writer = new PopulationWriter(existingPopulation, null);
		writer.write("/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/input/population_Alternative");
		System.out.println("Plan file generated successfully.");
	}
}
