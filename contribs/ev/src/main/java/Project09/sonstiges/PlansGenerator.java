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

import java.io.Reader;
import java.util.stream.Collectors;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class PlansGenerator {
	public static void main(String[] args) {
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);

		Population existingPopulation = scenario.getPopulation();

		Map<String, Person> personMap = new HashMap<>();
		List<CSVRecord> activities = readCSV("/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/sonstiges/activities_sample.csv");
		List<CSVRecord> legs = readCSV("/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/sonstiges/legs_sample.csv");

		for (CSVRecord activityRecord : activities) {
			String personIdStr = activityRecord.get("person_id");
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

			List<CSVRecord> personLegs = legs.stream()
				.filter(legRecord -> legRecord.get("person_id").equals(personIdStr) &&
					Double.parseDouble(legRecord.get("start_time_min")) ==
						Double.parseDouble(activityRecord.get("end_time_min")) &&
					legRecord.get("mode").equals("CAR_DRIVER"))
				.collect(Collectors.toList());

			if (!personLegs.isEmpty()) {
				String activityType = activityRecord.get("purpose");
				Coord coord = new Coord(Double.parseDouble(activityRecord.get("x")), Double.parseDouble(activityRecord.get("y")));
				Activity activity = existingPopulation.getFactory().createActivityFromCoord(activityType, coord);
				activity.setStartTime((Double.parseDouble(activityRecord.get("start_time_min"))%1440)*60);
				activity.setEndTime((Double.parseDouble(activityRecord.get("end_time_min"))%1440)*60);
				plan.addActivity(activity);

				// Add corresponding legs for the activity
				for (CSVRecord legRecord : personLegs) {
					if (Double.parseDouble(legRecord.get("start_time_min")) == Double.parseDouble(activityRecord.get("end_time_min")) &&
						legRecord.get("mode").equals("CAR_DRIVER")) {
						Leg leg = existingPopulation.getFactory().createLeg(TransportMode.car);
						leg.setDepartureTime((Double.parseDouble(legRecord.get("start_time_min"))%1440)*60);
						leg.setTravelTime((Double.parseDouble(legRecord.get("time_min"))%1440)*60);
						leg.setMode("car");
						plan.addLeg(leg);
					}
				}
			}
		}


		// Write the population to XML
		PopulationWriter writer = new PopulationWriter(existingPopulation, null);
		writer.write("/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/sonstiges/plans_Wrong.xml");

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
    /*private static List<CSVRecord> readCSVInChunks(String filePath) {
        try (Reader reader = new FileReader(filePath); CSVParser parser = CSVFormat.DEFAULT.withHeader().parse(reader)) {
            return parser.getRecords().stream().collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading CSV file: " + filePath);
        }*/
}
