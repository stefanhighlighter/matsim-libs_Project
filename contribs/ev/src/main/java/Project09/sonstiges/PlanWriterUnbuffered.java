package Project09.sonstiges;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationWriter;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PlanWriterUnbuffered {
	public static void main(String[] args) {
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);

		Population existingPopulation = scenario.getPopulation();

		Map<String, Person> personMap = new HashMap<>();
		List<CSVRecord> legs = readCSV("/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/sonstiges/legs_sample.csv");

		for (CSVRecord legRecord : legs) {
			String personIdStr = legRecord.get("person_id");
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

			if (legRecord.get("mode").equals("CAR_DRIVER")) {
				String legType = legRecord.get("previous_purpose");
				Coord coord = new Coord(Double.parseDouble(legRecord.get("start_x")), Double.parseDouble(legRecord.get("start_y")));

				Activity activity = existingPopulation.getFactory().createActivityFromCoord(legType, coord);
				activity.setEndTime((Double.parseDouble(legRecord.get("start_time_min")) % 1440) * 60);
				plan.addActivity(activity);

				Leg leg = existingPopulation.getFactory().createLeg(TransportMode.car);
				leg.setMode("car");
				plan.addLeg(leg);
			}
		}

		PopulationWriter writer = new PopulationWriter(existingPopulation, null);
		writer.write("/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/sonstiges/population_Unbuffered.xml");

		System.out.println("Plan file generated successfully.");
	}

	private static List<CSVRecord> readCSV(String filePath) {
		try (CSVParser parser = new CSVParser(new FileReader(filePath), CSVFormat.DEFAULT.withHeader())) {
			return parser.getRecords();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading CSV file: " + filePath);
		}
	}
}

// buffer reader
