package Project09.sonstiges;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.osgeo.proj4j.*;
/*import org.locationtech.proj4j.*;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.ProjCoordinate;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;*/

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.text.DecimalFormat;

public class ChargingStations {

    private static final String WGS84 = "EPSG:4326";
    private static final String GAUSS_KRUEGER_ZONE_4 = "EPSG:31468";

    private static void writeChargerElement(XMLStreamWriter writer, int chargerId, double latitude, double longitude, double plugPower, int plugCount) throws Exception {
        writer.writeStartElement("charger");

        // Write attributes in the desired order
        writer.writeAttribute("id", "charger" + chargerId);
        writer.writeAttribute("x", formatCoordinate(latitude));
        writer.writeAttribute("y", formatCoordinate(longitude));
        writer.writeAttribute("plug_power", Double.toString(plugPower));
        writer.writeAttribute("plug_count", Integer.toString(plugCount));

        writer.writeEndElement();
        writer.writeCharacters("\n"); // Add a line break
        writer.writeCharacters("  "); // Add indentation for better readability (optional)

    }

    private static String formatCoordinate(double coordinate) {
        DecimalFormat df = new DecimalFormat("0.000000");
        return df.format(coordinate);
    }

    public static void main(String[] args) {
        try {
            Reader in = new FileReader("/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/sonstiges/ladesaeulen.csv");
            CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().withDelimiter(';').parse(in);

            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(new FileWriter("/Users/stefan/IdeaProjects/matsim-libs_Project/contribs/ev/src/main/java/Project09/input/ChargingStations.xml"));

            writer.writeStartDocument();
            writer.writeStartElement("chargers");

            int chargerId = 1;

            CRSFactory crsFactory = new CRSFactory();
            CoordinateReferenceSystem sourceCRS = crsFactory.createFromName(WGS84);
            CoordinateReferenceSystem targetCRS = crsFactory.createFromName(GAUSS_KRUEGER_ZONE_4);

            CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
            CoordinateTransform transform = ctFactory.createTransform(sourceCRS, targetCRS);

            ProjCoordinate sourceCoord = new ProjCoordinate();
            ProjCoordinate targetCoord = new ProjCoordinate();


            for (CSVRecord record : csvParser) {
                if (!"Bayern".equals(record.get("Bundesland"))) {
                    continue; // Skip non-Bayern entries
                }

                double latitude = Double.parseDouble(record.get("Breitengrad").replace(',', '.'));
                double longitude = Double.parseDouble(record.get("Laengengrad").replace(',', '.'));

                sourceCoord.x = longitude;
                sourceCoord.y = latitude;

                transform.transform(sourceCoord, targetCoord);

                double x = targetCoord.x;
                double y = targetCoord.y;


                double plugPower = Math.ceil(Double.parseDouble(record.get("Nennleistung Ladeeinrichtung [kW]").replace(',', '.')));
                int plugCount = Integer.parseInt(record.get("Anzahl Ladepunkte"));

                writeChargerElement(writer, chargerId, x, y, plugPower, plugCount);
                chargerId++;
            }

            writer.writeEndElement();
            writer.writeEndDocument();

            writer.close();
            csvParser.close();

            System.out.println("XML-Datei wurde erfolgreich erstellt.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
