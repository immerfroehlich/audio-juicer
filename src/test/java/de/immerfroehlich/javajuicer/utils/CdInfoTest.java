package de.immerfroehlich.javajuicer.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import de.immerfroehlich.services.CdInfo;
import de.immerfroehlich.services.CdParanoiaService;

public class CdInfoTest {

	@Test
	public void test() {
		CdParanoiaService service = new CdParanoiaService();
		CdInfo cdInfo = service.getCDInfo();
	}

}
