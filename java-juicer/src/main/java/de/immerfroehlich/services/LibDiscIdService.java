package de.immerfroehlich.services;

import java.util.Optional;

import de.immerfroehlich.discid.DiscIdCalculator;

public class LibDiscIdService {
	
	/**
	 * Calculates a discid for the given CD-ROM drivePath.
	 * @param drivePath
	 * The path to the CD-ROM device. drivePath must not be null.
	 * @return
	 * An Optional containing the discid or empty if no disc was inserted.
	 */
	public Optional<String> calculateDiscIdByDevicePath(String drivePath) {
		if(drivePath == null) {
			throw new RuntimeException("drivePath must never be null");
		}
		
    	DiscIdCalculator calculator = new DiscIdCalculator();
		String discid = calculator.calculate(drivePath);
		
		if(discid.isBlank()) return Optional.empty();		
		return Optional.of(discid);
	}
	
	/**
	 * Calculates a TOC for the given CD-ROM drivePath.
	 * @param drivePath
	 * The path to the CD-ROM device. drivePath must not be null.
	 * @return
	 * An Optional containing the TOC or empty if no disc was inserted.
	 */
	public Optional<String> getDiscToc(String drivePath) {
		if(drivePath == null) {
			throw new RuntimeException("drivePath must never be null");
		}
		
		DiscIdCalculator calculator = new DiscIdCalculator();
		String toc = calculator.getToc(drivePath);
		
		if(toc.isBlank()) return Optional.empty();
		return Optional.of(toc);
	}
}
