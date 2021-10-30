package de.immerfroehlich.javajuicer.model;

public enum ReleaseType {
	
	SingleArtistAlbumOrSingle("Single Artist Album or Single (Pop)"), //Pop
	InterpretedMusic("Interpreted Music (e.g. classical, orchestral)"), //Classical
	Compilation("Compilation (multiple artists)"), //Various artists
	Audio("Audio (e.g. book, play, feature)");
	
	private String type;
	
	private ReleaseType(String type) {
		this.type = type;
	}
	
	public static ReleaseType parse(String type) {
	    for (ReleaseType paymentType : ReleaseType.values()) {
	        if (paymentType.type.equals(type)) {
	            return paymentType;
	        }
	    }
	    
	    throw new RuntimeException("Please only ever call this method with type strings from this class!");
	}

	public String getString() {
		return type;
	}
	
}
