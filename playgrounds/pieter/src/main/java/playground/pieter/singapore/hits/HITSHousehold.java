package playground.pieter.singapore.hits;

import java.io.Serializable;
import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;

public class HITSHousehold implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// int msno_main;
	String h1_hhid;
	int h1_pcode;
	String h2_dwell;
	String h3_ethnic;
	String h3a_other;
	int h4_totpax;
	// String h4b_u4yrs;
	String h4c_eligible;
	String h4d_nereason;
	String h5_vehavail;
	int xqty_carncom;
	int xqty_carnind;
	int xqty_carnrent;
	int xqty_caropcom;
	int xqty_caropind;
	int xqty_caroprent;
	int xqty_buscoach;
	int xqty_hgv;
	int xqty_lgvgoods;
	int xqty_lgvpsgr;
	int xqty_minibus;
	int xqty_motocom;
	int xqty_motoind;
	int xqty_taxi;
	// String h7_bike;
	int h8_bikeqty;

	private ArrayList<HITSPerson> persons;
	private ArrayList<HITSTrip> trips;	
	private ArrayList<HITSStage> stages;
		
	public HITSHousehold(){
	}
	public HITSHousehold(ResultSet hrs, Connection sqlConn, DateFormat dfm) {

		try {
			this.h1_hhid = hrs.getString("h1_hhid");
			this.h1_pcode = hrs.getInt("h1_pcode");
			this.h2_dwell = hrs.getString("h2_dwell");
			this.h3_ethnic = hrs.getString("h3_ethnic");
			this.h3a_other = hrs.getString("h3a_other");
			this.h4_totpax = hrs.getInt("h4_totpax");
			this.h4c_eligible = hrs.getString("h4c_eligible");
			this.h4d_nereason = hrs.getString("h4d_nereason");
			this.h5_vehavail = hrs.getString("h5_vehavail");
			this.xqty_carncom = hrs.getInt("xqty_carncom");
			this.xqty_carnind = hrs.getInt("xqty_carnind");
			this.xqty_carnrent = hrs.getInt("xqty_carnrent");
			this.xqty_caropcom = hrs.getInt("xqty_caropcom");
			this.xqty_caropind = hrs.getInt("xqty_caropind");
			this.xqty_caroprent = hrs.getInt("xqty_caroprent");
			this.xqty_buscoach = hrs.getInt("xqty_buscoach");
			this.xqty_hgv = hrs.getInt("xqty_hgv");
			this.xqty_lgvgoods = hrs.getInt("xqty_lgvgoods");
			this.xqty_lgvpsgr = hrs.getInt("xqty_lgvpsgr");
			this.xqty_minibus = hrs.getInt("xqty_minibus");
			this.xqty_motocom = hrs.getInt("xqty_motocom");
			this.xqty_motoind = hrs.getInt("xqty_motoind");
			this.xqty_taxi = hrs.getInt("xqty_taxi");
			this.h8_bikeqty = hrs.getInt("h8_bikeqty");
			setPersons(sqlConn, dfm);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		getTripsFromPersons();
		getStagesFromPersons();
	}

	public void setPersons(Connection conn, DateFormat dfm) {
		ArrayList<HITSPerson> p = new ArrayList<HITSPerson>();
		Statement ps;
		String sqlQuery;
		try {
			ps = conn.createStatement();
			sqlQuery = "select " +
			"h1_hhid,"+
			"pax_id,"+
			"p1_age,"+
			"p2_gender,"+
			"p3c_nolic,"+
			"p3_car_lic,"+
			"p3a_moto_lic,"+
			"p3b_vanbus_lic,"+
			"p4_mobility,"+
			"p4a_aids,"+
			"p4b_aidsoth,"+
			"p5a_edu,"+
			"p5_econactivity,"+
			"p5i_econactoth,"+
			"p6_occup,"+
			"p6i_occupoth,"+
			"p6a_fixedwkpl,"+
			"p6c_fwkplpcode,"+
			"p7_workhrs,"+
			"p8_income,"+
			"p9_day,"+
			"p9_date,"+
			"p10_maketrip,"+
			"p11_notripreason,"+
			"p11a_notripreasoth,"+
			"p12_lasttravelday,"+
			"p13_1sttriporig_home,"+
			"p13b_1storigpcode,"+
			"p14_1sttripstarttime,"+
			"p29_futrptuse,"+
			"p30_spendpt,"+
			"p31_spendschshtlbus,"+
			"p32_ptreimb "+

			"from hits.hitsshort where h1_hhid = '"+ this.h1_hhid +"' group by h1_hhid, pax_id;";
			ps.executeQuery(sqlQuery );
			ResultSet prs = ps.getResultSet();
			while (prs.next()) {
				p.add(new HITSPerson(prs, conn,dfm,this));
			}
			prs.close();
			ps.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		p.trimToSize();
		this.persons = p;
	}

	public ArrayList<HITSPerson> getPersons() {
		return persons;
	}
	public ArrayList<HITSTrip> getTrips() {
		return trips;
	}
	public ArrayList<HITSStage> getStages() {
		return stages;
	}

	private void getTripsFromPersons() {
		this.trips = new ArrayList<HITSTrip>();
		for(HITSPerson person:this.persons){
			this.trips.addAll(person.getTrips());
		}
		this.trips.trimToSize();
	}
	private void getStagesFromPersons() {
		this.stages = new ArrayList<HITSStage>();
		for(HITSPerson person:this.persons){
			this.stages.addAll(person.getStages());
		}
		this.stages.trimToSize();
	}
	
	public String toString(){
	     String format = "|%1$10s|%2$10s|%3$10s|%4$2d|%5$1s|\n";
	     return String.format(format, h1_hhid, h2_dwell, h3_ethnic, h4_totpax,h5_vehavail);
	}
}
