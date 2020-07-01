package de.immerfroehlich.services;

import de.immerfroehlich.discid.DiscIdCalculator;

public class LibDiscIdService {
	
	public String calculateDiscIdByDevicePath(String drivePath) {
    	DiscIdCalculator calculator = new DiscIdCalculator();
		String discid = calculator.calculate(drivePath);
		return discid;
	}

}
