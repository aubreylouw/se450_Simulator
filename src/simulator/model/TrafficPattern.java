package simulator.model;

public enum TrafficPattern {
	SIMPLE, ALTERNATING, NOT_DEFINED;
	
	public static TrafficPattern toTrafficPattern (String valueToMatch) {
		if (valueToMatch.toUpperCase().trim() == "SIMPLE")
			return SIMPLE;
		else if (valueToMatch.toUpperCase().trim() == "ALTERNATING")
			return ALTERNATING;
		else return NOT_DEFINED;
	}
}
