package Project09.sonstiges;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanMergerNew {
	private static final String INPUT_FILE = "/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/Generation/population10.xml";
	private static final String OUTPUT_FILE = "/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/Generation/populationMerged.xml";

	public static void main(String[] args) {
		try {
			File inputFile = new File(INPUT_FILE);
			File outputFile = new File(OUTPUT_FILE);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(inputFile);

			Map<String, List<Element>> existingPersonsMap = new HashMap<>();

			Element populationElement = (Element) document.getElementsByTagName("population").item(0);

			NodeList personList = populationElement.getElementsByTagName("person");
			for (int i = 0; i < personList.getLength(); i++) {
				Node personNode = personList.item(i);

				if (personNode.getNodeType() == Node.ELEMENT_NODE) {
					Element personElement = (Element) personNode;

					String homeX = personElement.getElementsByTagName("activity").item(0).getAttributes().getNamedItem("x").getTextContent();
					String homeY = personElement.getElementsByTagName("activity").item(0).getAttributes().getNamedItem("y").getTextContent();

					String homeCoordinates = homeX + "," + homeY;
					if (existingPersonsMap.containsKey(homeCoordinates)) {
						List<Element> existingPersonPlans = existingPersonsMap.get(homeCoordinates);
						existingPersonPlans.add((Element) personElement.getElementsByTagName("plan").item(0));
					} else {
						List<Element> plans = new ArrayList<>();
						plans.add((Element) personElement.getElementsByTagName("plan").item(0));
						existingPersonsMap.put(homeCoordinates, plans);
					}
				}
			}

			updateXML(document, existingPersonsMap);
			saveToXML(document, outputFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void updateXML(Document doc, Map<String, List<Element>> homeCoordinatesMap) {
		for (Map.Entry<String, List<Element>> entry : homeCoordinatesMap.entrySet()) {
			List<Element> plans = entry.getValue();
			if (plans.size() > 1) {
				Element mergedPlan = mergeAndSortPlans(doc, plans);

				Element firstPlan = plans.get(0);

				Node personNode = firstPlan.getParentNode();
				if (personNode != null) {
					personNode.replaceChild(mergedPlan.cloneNode(true), firstPlan);
				}

				// Entferne überschüssige Person-Knoten
				for (int i = 1; i < plans.size(); i++) {
					Node redundantPersonNode = plans.get(i).getParentNode();
					if (redundantPersonNode != null) {
						redundantPersonNode.getParentNode().removeChild(redundantPersonNode);
					}
				}

				// Füge HOME END nur einmal am Ende des gesamten Plans hinzu
				addHomeEndActivity(doc, personNode, entry.getKey());
			}
		}
	}

	private static void addHomeEndActivity(Document document, Node personNode, String homeCoordinates) {
		String[] coordinates = homeCoordinates.split(",");
		String homeX = coordinates[0];
		String homeY = coordinates[1];

		// Entferne vorhandene HOME END-Aktivitäten
		NodeList existingActivities = personNode.getChildNodes();
		for (int i = existingActivities.getLength() - 1; i >= 0; i--) {
			Node activityNode = existingActivities.item(i);
			if (activityNode.getNodeType() == Node.ELEMENT_NODE) {
				Element activity = (Element) activityNode;
				if ("activity".equals(activity.getTagName()) && "HOME END".equals(activity.getAttribute("type"))) {
					personNode.removeChild(activity);
				}
			}
		}

		// Füge HOME END am Ende des gesamten Plans hinzu
		Element homeEndActivity = document.createElement("activity");
		homeEndActivity.setAttribute("type", "HOME END");
		homeEndActivity.setAttribute("x", homeX);
		homeEndActivity.setAttribute("y", homeY);

		personNode.appendChild(homeEndActivity);
	}

	private static Element mergeAndSortPlans(Document document, List<Element> plans) {
		Element mergedPlan = document.createElement("plan");
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		List<Element> allActivities = new ArrayList<>();
		List<Element> allLegs = new ArrayList<>();

		for (Element plan : plans) {
			NodeList activitiesAndLegs = plan.getChildNodes();
			for (int i = 0; i < activitiesAndLegs.getLength(); i++) {
				Node node = activitiesAndLegs.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node.cloneNode(true);
					if (element.getTagName().equals("leg")) {
						// Make sure legs have no time attribute
						element.removeAttribute("end_time");
						allLegs.add(element);
					} else {
						allActivities.add(element);
					}
				}
			}
		}

		allActivities.sort((a1, a2) -> {
			String endTime1 = a1.getAttribute("end_time");
			String endTime2 = a2.getAttribute("end_time");

			if (endTime1.isEmpty() && endTime2.isEmpty()) {
				return 0;
			} else if (endTime1.isEmpty()) {
				return 1;
			} else if (endTime2.isEmpty()) {
				return -1;
			}

			try {
				Date time1 = sdf.parse(endTime1);
				Date time2 = sdf.parse(endTime2);
				return time1.compareTo(time2);
			} catch (ParseException e) {
				e.printStackTrace();
				return 0;
			}
		});

		boolean homeEndAdded = false;
		for (int i = 0; i < allActivities.size(); i++) {
			Element activity = allActivities.get(i);
			if (activity.getAttribute("type").equals("HOME END") && !homeEndAdded) {
				homeEndAdded = true;
				mergedPlan.appendChild(activity.cloneNode(true));
			} else {
				mergedPlan.appendChild(activity.cloneNode(true));

				// Append leg for all activities except the last "home end"
				if (i < allActivities.size() - 1) {
					// Ensure there is a leg available before appending
					if (!allLegs.isEmpty()) {
						Element leg = allLegs.remove(0);
						mergedPlan.appendChild(leg.cloneNode(true));
					}
				}
			}
		}


		mergedPlan.setAttribute("selected", plans.get(0).getAttribute("selected"));

		return mergedPlan;
	}


	private static void saveToXML(Document doc, File outputFile) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "yes");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(outputFile);

			transformer.transform(source, result);

			System.out.println("Output XML file saved successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
