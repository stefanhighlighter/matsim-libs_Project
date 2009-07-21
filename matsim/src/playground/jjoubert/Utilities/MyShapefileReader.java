package playground.jjoubert.Utilities;

import java.io.IOException;

import org.geotools.data.FeatureSource;
import org.geotools.feature.Feature;
import org.matsim.core.utils.gis.ShapeFileReader;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
/**
 * A utility class to read in a multipolygon, such as a study area shapefile, given the
 * filename of the shapefile. To use the method, all associated files must be present,
 * and not only the <code>*.shp</code> file.
 * 
 * @author jwjoubert
 */
public class MyShapefileReader {
	private final String shapefileName; 
	
	/**
	 * Constructs a single instance of the <code>MyShapefileReader</code>. The class is
	 * used to read in a single study area. Should you wish to read in many shapefiles,
	 * such as the Geospatial Analsysis Platform (GAP) mesozones, rather use the class
	 * <code>playground.jjoubert.Utilities.MyGapReader</code>. 
	 * @param shapefileName the absolute path and name of the shapefile to be read.
	 */
	public MyShapefileReader(String shapefileName){
		this.shapefileName = shapefileName;
	}
	
	/**
	 * Reads the read shapefile as a multipolygon. An error message is given if the 
	 * shapefile is <b><i>not</i></b> a multipolygon.
	 * @return <code>MultiPolygon</code>
	 */
	public MultiPolygon readMultiPolygon() {
		FeatureSource fs = null;
		MultiPolygon mp = null;
		try {	
			fs = ShapeFileReader.readDataFile( this.shapefileName );
			for(Object o: fs.getFeatures() ){
				Geometry geo = ((Feature)o).getDefaultGeometry();
				if(geo instanceof MultiPolygon){
					mp = (MultiPolygon)geo;
				} else{
					System.err.println("The shapefile is not a multipolygon!!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mp;
	}
	
	public String getShapefileName() {
		return shapefileName;
	}

}
