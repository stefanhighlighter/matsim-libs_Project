/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.matsim.core.config.groups;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.matsim.core.utils.misc.Time;

interface ConfigKey {}

/**
 * config group for experimental parameters. this group and its parameters should not be used outside of vsp.
 * @author dgrether
 * @author nagel
 */
public class VspExperimentalConfigGroup extends org.matsim.core.config.Module {

	public static enum VspExperimentalConfigKey implements ConfigKey {
		//			activityDurationInterpretation,
		vspDefaultsCheckingLevel,
		logitScaleParamForPlansRemoval,
		scoreMSAStartsAtIteration,
		isGeneratingBoardingDeniedEvent,
		isAbleToOverwritePtInteractionParams,
		isUsingOpportunityCostOfTimeForLocationChoice
	}

	private final Map<ConfigKey,String> typedParam = new TreeMap<ConfigKey,String>();

	public void addParam( final ConfigKey key, final String value ) {
		String retVal = this.typedParam.put( key,value );
		if ( retVal != null ) {
			Logger.getLogger(this.getClass()).info(key + ": replacing >" + retVal + "< (old) with >" + value + "< (new)") ;
		}
	}

	public String getValue( final ConfigKey key ) {
		return this.typedParam.get(key) ;
	}

	// === testing area end ===

	private final static Logger log = Logger.getLogger(VspExperimentalConfigGroup.class);

	public static final String GROUP_NAME = "vspExperimental";

	// ---
	private static final String REMOVING_UNNECESSARY_PLAN_ATTRIBUTES = "removingUnnecessaryPlanAttributes" ;
	private boolean removingUnneccessaryPlanAttributes = false ;

	// ---

	@Deprecated
	private static final String USE_ACTIVITY_DURATIONS = "useActivityDurations";
	private static final String ACTIVITY_DURATION_INTERPRETATION="activityDurationInterpretation" ;

	public static enum ActivityDurationInterpretation { minOfDurationAndEndTime, tryEndTimeThenDuration, @Deprecated endTimeOnly } 

	private ActivityDurationInterpretation activityDurationInterpretation = ActivityDurationInterpretation.minOfDurationAndEndTime ;

	// ---

	private static final String INPUT_MZ05_FILE = "inputMZ05File";
	private String inputMZ05File = "";

	// ---

	private static final String MODES_FOR_SUBTOURMODECHOICE = "modes";
	private static final String CHAIN_BASED_MODES = "chainBasedModes";

	private String modesForSubTourModeChoice = "car, pt";
	private String chainBasedModes = "car";

	// ---

	@Deprecated
	private static final String USING_OPPORTUNITY_COST_OF_TIME_FOR_PT_ROUTING =
		"usingOpportunityCostOfTimeForPtRouting" ;
	@Deprecated
	private boolean isUsingOpportunityCostOfTimeInPtRouting = true ;

	// ---

	//	private static final String VSP_DEFAULTS_CHECKING_LEVEL = "vspDefaultsCheckingLevel" ;

	public static final String IGNORE = "ignore" ;
	public static final String WARN = "warn" ;
	public static final String ABORT = "abort" ;

	//	private String vspDefaultsCheckingLevel = IGNORE ;

	// ---

	private static final String EMISSION_ROADTYPE_MAPPING_FILE = "emissionRoadTypeMappingFile";
	private String emissionRoadTypeMappingFile = null;

	private static final String EMISSION_VEHICLE_FILE = "emissionVehicleFile";
	private String emissionVehicleFile = null;

	private static final String EMISSION_FACTORS_WARM_FILE_AVERAGE = "averageFleetWarmEmissionFactorsFile";
	private String averageFleetWarmEmissionFactorsFile = null;

	private static final String EMISSION_FACTORS_COLD_FILE_AVERAGE = "averageFleetColdEmissionFactorsFile";
	private String averageFleetColdEmissionFactorsFile = null;

	private static final String USING_DETAILED_EMISSION_CALCULATION = "usingDetailedEmissionCalculation";
	private boolean isUsingDetailedEmissionCalculation = false;

	private static final String EMISSION_FACTORS_WARM_FILE_DETAILED = "detailedWarmEmissionFactorsFile" ;
	private String detailedWarmEmissionFactorsFile = null;

	private static final String EMISSION_FACTORS_COLD_FILE_DETAILED = "detailedColdEmissionFactorsFile";
	private String detailedColdEmissionFactorsFile;

	// ---

	private static final String WRITING_OUTPUT_EVENTS = "writingOutputEvents" ;
	private boolean writingOutputEvents = false ;

	// ---

	private static final String MATSIM_GLOBAL_TIME_FORMAT = "matsimGlobalTimeformat" ;
	private String matsimGlobalTimeFormat = Time.TIMEFORMAT_HHMMSS;

	// ---

	public VspExperimentalConfigGroup() {
		super(GROUP_NAME);

		// the following somewhat curious syntax is so that both the compiler and the runtime system notice if an entry
		// is missing
		for ( VspExperimentalConfigKey key : VspExperimentalConfigKey.values() ) {
			switch(key) {
			case vspDefaultsCheckingLevel:
				this.addParam( key, IGNORE ) ;
				break ;
			case logitScaleParamForPlansRemoval:
				this.addParam( key, "1." ) ; 
				break;
			case scoreMSAStartsAtIteration:
				this.addParam( key, "null") ;
				break;
			case isGeneratingBoardingDeniedEvent:
				this.addParam( key, "false" ) ; // default is that this event is NOT generated.  kai, oct'12 
				break;
			case isAbleToOverwritePtInteractionParams:
				this.addParam( key, "false" ) ; // default is that this NOT allowed.  kai, nov'12 
				break;
			case isUsingOpportunityCostOfTimeForLocationChoice:
				this.addParam( key, "true" ) ; 
				break;
			}
		}
	}

	@Override
	public Map<String, String> getComments() {
		Map<String,String> map = super.getComments();

		for ( VspExperimentalConfigKey key : VspExperimentalConfigKey.values() ) {
			switch(key) {
			case logitScaleParamForPlansRemoval:
				//				map.put(key.toString(), "comment") ;
				break;
			case scoreMSAStartsAtIteration:
				map.put(key.toString(), "first iteration of MSA score averaging. The matsim theory department " +
				"suggests to use this together with switching of choice set innovation, but it has not been tested yet.") ;
				break;
			case vspDefaultsCheckingLevel:
				break;
			case isGeneratingBoardingDeniedEvent:
				break;
			case isAbleToOverwritePtInteractionParams:
				map.put(key.toString(), "(do not use except of you have to) There was a problem with pt interaction scoring.  Some people solved it by overwriting the " +
						"parameters of the pt interaction activity type.  Doing this now throws an Exception.  If you still insist on doing this, " +
				"set the following to true.") ;
				break;
			case isUsingOpportunityCostOfTimeForLocationChoice:
				map.put(key.toString(), "if an approximation of the opportunity cost of time is included into the radius calculation for location choice." +
				"`true' will be faster, but it is an approximation.  Default is `true'; `false' is available for backwards compatibility.") ;
				break;
			}
		}
		map.put(MATSIM_GLOBAL_TIME_FORMAT, "changes MATSim's global time format used in output files. Can be used to enforce writing fractional seconds e.g. in output_plans.  " +
		"default is `hh:mm:ss' (because of backwards compatibility). see Time.java for possible formats");

		map.put(WRITING_OUTPUT_EVENTS, "if true then writes output_events in output directory.  default is `false'." +
		" Will only work when lastIteration is multiple of events writing interval" ) ;

		map.put(EMISSION_ROADTYPE_MAPPING_FILE, "REQUIRED: mapping from input road types to HBEFA 3.1 road type strings");

		map.put(EMISSION_VEHICLE_FILE, "definition of a vehicle for every person (who is allowed to choose a vehicle in the simulation):" + "\n" +
				" - REQUIRED: vehicle type Id must start with the respective HbefaVehicleCategory followed by `;'" + "\n" +
				" - OPTIONAL: if detailed emission calculation is switched on, vehicle type Id should aditionally contain" +
				" HbefaVehicleAttributes (`Technology;SizeClasse;EmConcept'), corresponding to the strings in " + EMISSION_FACTORS_WARM_FILE_DETAILED);

		map.put(EMISSION_FACTORS_WARM_FILE_AVERAGE, "REQUIRED: file with HBEFA 3.1 fleet average warm emission factors");

		map.put(EMISSION_FACTORS_COLD_FILE_AVERAGE, "REQUIRED: file with HBEFA 3.1 fleet average cold emission factors");

		map.put(USING_DETAILED_EMISSION_CALCULATION, "if true then detailed emission factor files must be provided!");

		map.put(EMISSION_FACTORS_WARM_FILE_DETAILED, "OPTIONAL: file with HBEFA 3.1 detailed warm emission factors") ;

		map.put(EMISSION_FACTORS_COLD_FILE_DETAILED, "OPTIONAL: file with HBEFA 3.1 detailed cold emission factors");

		map.put(VspExperimentalConfigKey.vspDefaultsCheckingLevel.toString(), 
				"Options: `"+IGNORE+"', `"+WARN+"', `"+ABORT+"'.  Default: either `"+IGNORE+"' or `"
				+WARN+"'.\n\t\t" +
				"When violating VSP defaults, this results in " +
		"nothing, warnings, or aborts.  Members of VSP should use `abort' or talk to kai.") ;

		//		map.put(USE_ACTIVITY_DURATIONS, "(deprecated, use " + ACTIVITY_DURATION_INTERPRETATION
		//				+ " instead) Set this flag to false if the duration attribute of the activity should not be considered in QueueSimulation");

		StringBuilder str = new StringBuilder() ;
		for ( ActivityDurationInterpretation itp : ActivityDurationInterpretation.values() ) {
			str.append(" ").append(itp.toString());
		}
		map.put(ACTIVITY_DURATION_INTERPRETATION, "String:" + str + ". Anything besides " 
				+ ActivityDurationInterpretation.minOfDurationAndEndTime + " will internally use a different " +
		"(simpler) version of the TimeAllocationMutator.") ;

		map.put(REMOVING_UNNECESSARY_PLAN_ATTRIBUTES, "(not tested) will remove plan attributes that are presumably not used, such as " +
		"activityStartTime. default=false") ;

		map.put(INPUT_MZ05_FILE, "(do not use) Set this filename of MZ05 daily analysis");

		map.put(MODES_FOR_SUBTOURMODECHOICE, "(do not use) set the traffic mode option for subTourModeChoice by Yu");
		map.put(CHAIN_BASED_MODES, "(do not use) set chainBasedModes for subTourModeChoice by Yu. E.g. \"car,bike\", \"car\"");

		//		map.put(OFFSET_WALK, "(deprecated, use corresponding option in planCalcScore) " +
		//		"set offset for mode \"walk\" in leg scoring function");

		//		map.put(COLORING, "coloring scheme for otfvis.  Currently (2010) allowed values: ``standard'', ``bvg''") ;
		map.put(USING_OPPORTUNITY_COST_OF_TIME_FOR_PT_ROUTING,
				"indicates if, for routing, the opportunity cost of time should be added to the mode-specific marginal " +
				"utilities of time.\n\t\t" +
				"Default is true; false is possible only for backwards compatibility.\n\t\t" +
				"This is only a suggestion since there is (by matsim design) no way to enforce that mental modules " +
		"obey this." ) ;

		return map;
	}

	@Override
	@Deprecated
	public String getValue(final String key) {
		throw new RuntimeException(" use direct getter or getValue( ...Key key) (depending on which one is implemented for " +
		"your variable); aborting ... " ) ;
	}

	@Override
	public void addParam(final String key, final String value) {
		for ( VspExperimentalConfigKey keyTmp : VspExperimentalConfigKey.values() ) {
			if ( keyTmp.toString().equals(key) ) {
				this.addParam(keyTmp, value ) ;
				return ;
			}
			// the above feels really odd.  Problem is that we can convert keys to strings, but not the other way round.
			// alternative might be some lookup table.  kai, oct'12
		}

		if (USE_ACTIVITY_DURATIONS.equalsIgnoreCase(key)) {
			//			this.setUseActivityDurations(Boolean.parseBoolean(value));
			if ( Boolean.parseBoolean(value) ) {
				setActivityDurationInterpretation( ActivityDurationInterpretation.minOfDurationAndEndTime ) ;
			} else {
				setActivityDurationInterpretation( ActivityDurationInterpretation.endTimeOnly ) ;
			}
			log.warn("Config parameter " + USE_ACTIVITY_DURATIONS + " is deprecated; use " 
					+ ACTIVITY_DURATION_INTERPRETATION + " instead. kai, jan'13") ;
		} else if ( ACTIVITY_DURATION_INTERPRETATION.equalsIgnoreCase(key)) {
			setActivityDurationInterpretation(ActivityDurationInterpretation.valueOf(value)) ;
		} else if ( REMOVING_UNNECESSARY_PLAN_ATTRIBUTES.equalsIgnoreCase(key)) {
			setRemovingUnneccessaryPlanAttributes(Boolean.parseBoolean(value)) ;
		} else if ( "coloring".equalsIgnoreCase(key) ) {
			throw new RuntimeException("coloring in vspExperimentalConfigGroup is no longer allowed; use the corresponding " +
			"config option in the otfvis config group (or do not use at all).  jul'12") ;
		} else if (INPUT_MZ05_FILE.equalsIgnoreCase(key)) {
			setInputMZ05File(value);
		} else if (MODES_FOR_SUBTOURMODECHOICE.equalsIgnoreCase(key)) {
			setModesForSubTourModeChoice(value);
		} else if ( USING_OPPORTUNITY_COST_OF_TIME_FOR_PT_ROUTING.equals(key) ) {
			this.setUsingOpportunityCostOfTimeInPtRouting(Boolean.parseBoolean(value)) ;
		} else if (CHAIN_BASED_MODES.equalsIgnoreCase(key)) {
			setChainBasedModes(value);
		} else if ("offsetWalk".equalsIgnoreCase(key)) {
			throw new RuntimeException( "offsetWalk in vspExperimentalConfigGroup is no longer; use the (alternative-specific) " +
			"constants in planCalcScore.  Aborting since you need to fix this ..." ) ;
			//		} else if ( VspExperimentalConfigKey.vspDefaultsCheckingLevel.toString().equals(key) ) {
			//			this.addParam( VspExperimentalConfigKey.vspDefaultsCheckingLevel, value) ;
			//		} else if ( VspExperimentalConfigKey.logitScaleParamForPlansRemoval.toString().equals(key) ) {
			//			this.addParam( VspExperimentalConfigKey.logitScaleParamForPlansRemoval, value) ;
		} else if ( EMISSION_ROADTYPE_MAPPING_FILE.equals(key)){
			this.setEmissionRoadTypeMappingFile(value);
		} else if ( EMISSION_VEHICLE_FILE.equals(key)){
			this.setEmissionVehicleFile(value);
		} else if ( EMISSION_FACTORS_WARM_FILE_AVERAGE.equals(key)){
			this.setAverageWarmEmissionFactorsFile(value);
		} else if ( EMISSION_FACTORS_COLD_FILE_AVERAGE.equals(key)){
			this.setAverageColdEmissionFactorsFile(value);
		} else if ( USING_DETAILED_EMISSION_CALCULATION.equals(key)){
			this.setIsUsingDetailedEmissionCalculation(Boolean.parseBoolean(value));
		} else if ( EMISSION_FACTORS_WARM_FILE_DETAILED.equals(key)) {
			this.setDetailedWarmEmissionFactorsFile(value);
		} else if (EMISSION_FACTORS_COLD_FILE_DETAILED.equals(key)){
			this.setDetailedColdEmissionFactorsFile(value);
		} else if ( WRITING_OUTPUT_EVENTS.equals(key) ) {
			this.setWritingOutputEvents(Boolean.parseBoolean(value) ) ;
		} else if ( MATSIM_GLOBAL_TIME_FORMAT.equals(key) ) {
			this.setMatsimGlobalTimeFormat(value) ;
		}
		else {
			throw new IllegalArgumentException(key);
		}
	}

	@Override
	public final TreeMap<String, String> getParams() {
		TreeMap<String, String> map = new TreeMap<String, String>();

		map.put(ACTIVITY_DURATION_INTERPRETATION, getActivityDurationInterpretation().toString()) ;
		map.put(REMOVING_UNNECESSARY_PLAN_ATTRIBUTES, Boolean.toString(isRemovingUnneccessaryPlanAttributes()) ) ;
		map.put(INPUT_MZ05_FILE, getInputMZ05File() ) ;
		map.put(MODES_FOR_SUBTOURMODECHOICE, getModesForSubTourModeChoice() ) ;
		map.put(CHAIN_BASED_MODES, getChainBasedModes() );
		map.put(USING_OPPORTUNITY_COST_OF_TIME_FOR_PT_ROUTING,
				Boolean.toString( this.isUsingOpportunityCostOfTimeInPtRouting()) ) ;
		map.put(EMISSION_ROADTYPE_MAPPING_FILE, this.getEmissionRoadTypeMappingFile());
		map.put(EMISSION_VEHICLE_FILE, this.getEmissionVehicleFile());
		map.put(EMISSION_FACTORS_WARM_FILE_AVERAGE, this.getAverageWarmEmissionFactorsFile());
		map.put(EMISSION_FACTORS_COLD_FILE_AVERAGE, this.getAverageColdEmissionFactorsFile());
		map.put(USING_DETAILED_EMISSION_CALCULATION, Boolean.toString( this.isUsingDetailedEmissionCalculation));
		map.put(EMISSION_FACTORS_WARM_FILE_DETAILED,  this.getDetailedWarmEmissionFactorsFile()) ;
		map.put(EMISSION_FACTORS_COLD_FILE_DETAILED, this.getDetailedColdEmissionFactorsFile());
		map.put( WRITING_OUTPUT_EVENTS, Boolean.toString(this.isWritingOutputEvents()) ) ;
		map.put( MATSIM_GLOBAL_TIME_FORMAT, this.getMatsimGlobalTimeFormat() );
		for ( VspExperimentalConfigKey key : VspExperimentalConfigKey.values() ) {
			map.put( key.toString(), this.getValue(key) ) ;
		}

		return map;
	}

	//	@Override
	//	protected void checkConsistency() throws RuntimeException {
	//		log.info( "entering checkVspDefaults ...") ;
	//
	//		// begin vsp default definitions
	//		final boolean usingOpportunityCostOfTimeInPtRoutingDefault = true ;
	//		// end vsp default definitions
	//
	//		boolean problem = false ;
	//		if ( this.getVspDefaultsCheckingLevel().equals( VspExperimentalConfigGroup.WARN )
	//				|| this.getVspDefaultsCheckingLevel().equals( VspExperimentalConfigGroup.ABORT ) ) {
	//
	//			if ( this.isUsingOpportunityCostOfTimeInPtRouting() != usingOpportunityCostOfTimeInPtRoutingDefault ) {
	//				log.warn( "violating VSP defaults; "+USING_OPPORTUNITY_COST_OF_TIME_FOR_PT_ROUTING + "  should be set to: " +
	//						usingOpportunityCostOfTimeInPtRoutingDefault + " in module: " + GROUP_NAME) ;
	//				problem = true ;
	//			}
	//
	//		}
	//
	//		if ( this.getVspDefaultsCheckingLevel().equals(VspExperimentalConfigGroup.ABORT) && problem ) {
	//			String str = "violating VSP defaults and vspDefaultsCheckingLevel set to `abort', thus aborting ..." ;
	//			log.fatal( str ) ;
	//			throw new RuntimeException( str ) ;
	//		}
	//
	//		log.info( "leaving checkVspDefaults ...") ;
	//	}

	public String getInputMZ05File() {
		return this.inputMZ05File;
	}

	public void setInputMZ05File(final String inputMZ05File) {
		this.inputMZ05File = inputMZ05File;
	}

	public String getModesForSubTourModeChoice() {
		return this.modesForSubTourModeChoice;
	}

	public void setModesForSubTourModeChoice(final String modesForSubTourModeChoice) {
		this.modesForSubTourModeChoice = modesForSubTourModeChoice;
	}

	public String getChainBasedModes() {
		return this.chainBasedModes;
	}

	public void setChainBasedModes(final String chainBasedModes) {
		this.chainBasedModes = chainBasedModes;
	}

	public ActivityDurationInterpretation getActivityDurationInterpretation() {
		return this.activityDurationInterpretation;
	}

	public void setActivityDurationInterpretation(final ActivityDurationInterpretation activityDurationInterpretation) {
		if ( ActivityDurationInterpretation.endTimeOnly.equals(activityDurationInterpretation) ){
			/*
			 * I don't think this is the correct place for consistency checks but this bug is so hard to find that the user should be warned in any case. dg 08-2012
			 */
			log.warn("You are using " + activityDurationInterpretation + " as activityDurationInterpretation. " +
			"This is not working in conjunction with the pt module as pt interaction activities then will never end!");
			log.warn("ActivityDurationInterpreation " + activityDurationInterpretation + " is deprecated; use " 
					+ ActivityDurationInterpretation.minOfDurationAndEndTime + " instead. kai, jan'13") ;
		}
		this.activityDurationInterpretation = activityDurationInterpretation;
	}

	public boolean isRemovingUnneccessaryPlanAttributes() {
		return this.removingUnneccessaryPlanAttributes;
	}

	public void setRemovingUnneccessaryPlanAttributes(final boolean removingUnneccessaryPlanAttributes) {
		this.removingUnneccessaryPlanAttributes = removingUnneccessaryPlanAttributes;
	}

	@Deprecated // should always return true; switch is only there for backwards compatibility
	public boolean isUsingOpportunityCostOfTimeInPtRouting() {
		return this.isUsingOpportunityCostOfTimeInPtRouting;
	}

	@Deprecated // switch is only there for backwards compatibility
	public void setUsingOpportunityCostOfTimeInPtRouting(final boolean tmp) {
		log.warn("config parameter " + USING_OPPORTUNITY_COST_OF_TIME_FOR_PT_ROUTING + " deprecated; remove from config file; " +
		"will eventually be removed." ) ;
		this.isUsingOpportunityCostOfTimeInPtRouting = tmp;
	}

	//	public void setVspDefaultsCheckingLevel(final String vspDefaultsCheckingLevel) {
	//		this.addParam(VspExperimentalConfigKey.vspDefaultsCheckingLevel, vspDefaultsCheckingLevel) ;
	//	}

	//	public String getVspDefaultsCheckingLevel() {
	//		return this.getValue(VspExperimentalConfigKey.vspDefaultsCheckingLevel ) ;
	//	}

	public void setEmissionRoadTypeMappingFile(String roadTypeMappingFile) {
		this.emissionRoadTypeMappingFile = roadTypeMappingFile;
	}

	public String getEmissionRoadTypeMappingFile() {
		return this.emissionRoadTypeMappingFile;
	}

	public void setEmissionVehicleFile(String emissionVehicleFile) {
		this.emissionVehicleFile = emissionVehicleFile;
	}

	public String getEmissionVehicleFile() {
		return this.emissionVehicleFile;
	}

	public void setAverageWarmEmissionFactorsFile(String averageFleetWarmEmissionFactorsFile) {
		this.averageFleetWarmEmissionFactorsFile = averageFleetWarmEmissionFactorsFile;
	}

	public String getAverageWarmEmissionFactorsFile() {
		return this.averageFleetWarmEmissionFactorsFile;
	}

	public void setAverageColdEmissionFactorsFile(String averageFleetColdEmissionFactorsFile) {
		this.averageFleetColdEmissionFactorsFile = averageFleetColdEmissionFactorsFile;
	}

	public String getAverageColdEmissionFactorsFile() {
		return this.averageFleetColdEmissionFactorsFile;
	}

	public boolean isUsingDetailedEmissionCalculation(){
		return this.isUsingDetailedEmissionCalculation;
	}

	public void setIsUsingDetailedEmissionCalculation(final boolean isUsingDetailedEmissionCalculation) {
		this.isUsingDetailedEmissionCalculation = isUsingDetailedEmissionCalculation;
	}

	public void setDetailedWarmEmissionFactorsFile(String detailedWarmEmissionFactorsFile) {
		this.detailedWarmEmissionFactorsFile = detailedWarmEmissionFactorsFile;
	}

	public String getDetailedWarmEmissionFactorsFile() {
		return this.detailedWarmEmissionFactorsFile;
	}

	public void setDetailedColdEmissionFactorsFile(String detailedColdEmissionFactorsFile) {
		this.detailedColdEmissionFactorsFile = detailedColdEmissionFactorsFile;
	}

	public String getDetailedColdEmissionFactorsFile(){
		return this.detailedColdEmissionFactorsFile;
	}

	public boolean isWritingOutputEvents() {
		return this.writingOutputEvents ;
	}

	public void setWritingOutputEvents(boolean writingOutputEvents) {
		this.writingOutputEvents = writingOutputEvents;
	}

	public String getMatsimGlobalTimeFormat() {
		return this.matsimGlobalTimeFormat;
	}

	public void setMatsimGlobalTimeFormat(String format) {
		this.matsimGlobalTimeFormat = format;
		Time.setDefaultTimeFormat(format) ;
	}
}
