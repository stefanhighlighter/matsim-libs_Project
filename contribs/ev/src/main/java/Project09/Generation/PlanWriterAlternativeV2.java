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
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlanWriterAlternativeV2 {

	private static final int BATCH_SIZE = 10000;

	public static void main(String[] args) {
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		Population existingPopulation = scenario.getPopulation();

		Map<String, Person> personMap = new ConcurrentHashMap<>();
		String fileName = "C:/Users/Marie/Documents/Master/OOP Java/mat-sim-ws-2324-marie/src/main/java/Project09/legs.csv";

		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			String header = reader.readLine();
			int lineCount = 0;
			StringBuilder batchLines = new StringBuilder();

			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}

				lineCount++;
				batchLines.append(line).append('\n');

				if (lineCount >= BATCH_SIZE) {
					processBatch(header, batchLines.toString(), existingPopulation, personMap);
					batchLines = new StringBuilder();
					lineCount = 0;
				}
			}

			if (lineCount > 0) {
				processBatch(header, batchLines.toString(), existingPopulation, personMap);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading CSV file");
		}

		PopulationWriter writer = new PopulationWriter(existingPopulation, null);
		writer.write("C:/Users/Marie/Documents/Master/OOP Java/mat-sim-ws-2324-marie/src/main/java/Project09/plans.xml");

		System.out.println("Plan file generated successfully.");
	}

	private static void processBatch(String header, String batch, Population existingPopulation, Map<String, Person> personMap) {
		String[] lines = batch.split("\n");
		for (String line : lines) {
			processCsvLine(header, line, existingPopulation, personMap);
		}
	}


	private static void processCsvLine(String header, String line, Population existingPopulation, Map<String, Person> personMap) {
		String[] headerArray = header.split(",");
		String[] values = line.split(",");

		int personIdIndex = 0;
		int purposeIndex = 1;
		int startTimeIndex = 2;
		int startXIndex = 3;
		int startYIndex = 4;
		int startZoneIndex = 5;
		int nextPurposeIndex = 6;
		int endTimeIndex = 7;
		int endXIndex = 8;
		int endYIndex = 9;
		int endZoneIndex = 10;
		int modeIndex = 11;
		int timeMinIndex = 12;
		int distanceMIndex = 13;

		String personIdStr = values[personIdIndex];
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

		if ("\"CAR_DRIVER\"".equals(values[modeIndex])) {
			String previousPurpose = values[purposeIndex];
			Coord previousCoord = new Coord(
				Double.parseDouble(values[startXIndex]),
				Double.parseDouble(values[startYIndex])
			);

			Activity previousActivity = existingPopulation.getFactory().createActivityFromCoord(previousPurpose, previousCoord);
			previousActivity.setStartTime((Double.parseDouble(values[startTimeIndex]) % 1440) * 60);
			previousActivity.setEndTime((Double.parseDouble(values[startTimeIndex]) % 1440) * 60);
			plan.addActivity(previousActivity);

			Leg leg = existingPopulation.getFactory().createLeg(TransportMode.car);
			leg.setDepartureTime((Double.parseDouble(values[startTimeIndex]) % 1440) * 60);
			leg.setTravelTime((Double.parseDouble(values[timeMinIndex]) % 1440) * 60); // Adjusted for the distance_m field
			leg.setMode(values[modeIndex]);
			plan.addLeg(leg);
		}
	}
}
