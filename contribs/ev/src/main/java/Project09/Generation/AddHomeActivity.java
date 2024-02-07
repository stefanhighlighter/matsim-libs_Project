package Project09.Generation;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.scenario.ScenarioUtils;

public class AddHomeActivity {

	public static void main(String[] args) {
		// Hier sollte der Pfad zur ursprünglichen Population XML-Datei angegeben werden
		String inputFilePath = "/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/input/evPopulation_5percent.xml";
		// Hier sollte der Pfad zur aktualisierten Population XML-Datei angegeben werden
		String outputFilePath = "/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/input/evPopulation_Project.xml";

		Config config = ConfigUtils.createConfig();
		// Szenario erstellen
		Scenario scenario = ScenarioUtils.createScenario(config);
		// Population einlesen
		PopulationReader populationReader = new PopulationReader(scenario);
		populationReader.readFile(inputFilePath);

		// Gewünschte Änderungen vornehmen
		for (Person person : scenario.getPopulation().getPersons().values()) {
			Plan plan = person.getSelectedPlan();

			// Falls der Plan nicht mit HOME beginnt, füge eine HOME-Aktivität am Anfang hinzu
			if (!(plan.getPlanElements().get(0) instanceof Activity && "HOME".equals(((Activity) plan.getPlanElements().get(0)).getType()))) {
				Coord firstHomeCoord = plan.getPlanElements().get(0) instanceof Activity ?
					((Activity) plan.getPlanElements().get(0)).getCoord() : new Coord(0, 0);
				Activity firstHomeActivity = scenario.getPopulation().getFactory().createActivityFromCoord("HOME", firstHomeCoord);
				firstHomeActivity.setEndTime(0.0);
				plan.getPlanElements().add(0, firstHomeActivity);
			}

			// Falls der Plan nicht mit HOME endet, füge eine HOME-Aktivität am Ende hinzu
			int lastIndex = plan.getPlanElements().size() - 1;
			if (!(plan.getPlanElements().get(lastIndex) instanceof Activity && "HOME".equals(((Activity) plan.getPlanElements().get(lastIndex)).getType()))) {
				Coord lastHomeCoord = plan.getPlanElements().get(lastIndex) instanceof Activity ?
					((Activity) plan.getPlanElements().get(lastIndex)).getCoord() : new Coord(0, 0);
				Activity lastHomeActivity = scenario.getPopulation().getFactory().createActivityFromCoord("HOME", lastHomeCoord);

				double lastActivityEndTime = plan.getPlanElements().get(lastIndex - 1) instanceof Activity ?
					((Activity) plan.getPlanElements().get(lastIndex - 1)).getEndTime().seconds() : 0.0;

				lastHomeActivity.setEndTime(lastActivityEndTime + 3600);
				plan.getPlanElements().add(lastHomeActivity);
			}

			// Setze die Koordinaten des letzten HOME auf die des ersten HOME
			Activity firstHomeActivity = (Activity) plan.getPlanElements().get(0);
			Activity lastHomeActivity = (Activity) plan.getPlanElements().get(plan.getPlanElements().size() - 1);
			lastHomeActivity.setCoord(firstHomeActivity.getCoord());
		}

		// Aktualisiertes Szenario speichern
		PopulationWriter populationWriter = new PopulationWriter(scenario.getPopulation());
		populationWriter.write(outputFilePath);

		System.out.println("Aktualisierte Population wurde erfolgreich gespeichert.");
	}
}
