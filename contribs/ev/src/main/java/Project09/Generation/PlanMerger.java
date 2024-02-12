package Project09.Generation;

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


public class PlanMerger {


	public static void main(String[] args) {
		try {

			File inputFile = new File("/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/Generation/population10.xml");
			File outputFile = new File("/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/Generation/populationMerged10.xml");

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

				for (int i = 1; i < plans.size(); i++) {
					Node redundantPersonNode = plans.get(i).getParentNode();
					if (redundantPersonNode != null) {
						redundantPersonNode.getParentNode().removeChild(redundantPersonNode);
					}
				}
			}
		}
	}


	private static Element mergeAndSortPlans(Document document, List<Element> plans) {
		Element mergedPlan = document.createElement("plan");
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		List<Element> allActivities = new ArrayList<>();
		List<Element> allLegs = new ArrayList<>();

		// Collect all activities and legs from the plans
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

			if (endTime1.isEmpty() || endTime2.isEmpty()) {

				return 0;
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


		Iterator<Element> legIterator = allLegs.iterator();
		for (Element activity : allActivities) {
			mergedPlan.appendChild(activity);

			if (legIterator.hasNext()) {
				Element leg = legIterator.next();
				mergedPlan.appendChild(leg);
			}
		}

		mergedPlan.setAttribute("selected", plans.get(0).getAttribute("selected"));

		return mergedPlan;
	}


	private static void saveToXML(Document doc, File outputFile) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			// Set the indentation properties
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(outputFile);

			transformer.transform(source, result);

			System.out.println("Output XML file saved successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
