package de.immerfroehlich.services;

public class LibDiscIdServiceTest {

	public static void main(String[] args) {
		
		String drive = "/dev/cdrom";
		
		LibDiscIdService service = new LibDiscIdService();
		
		String discId = service.calculateDiscIdByDevicePath(drive).get();
		System.out.println(discId);
		
		String toc = service.getDiscToc(drive).get();
		System.out.println(toc);
	}

}
