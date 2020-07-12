package de.immerfroehlich.deviceinfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.immerfroehlich.command.Command;
import de.immerfroehlich.command.CommandExecutor;
import de.immerfroehlich.command.Result;

public class DeviceInfo {
	
	public List<BlockDevice> getRomDevices() {
		InputStream json = lsblk();
		
		ObjectMapper objectMapper = new ObjectMapper();
		BlockDeviceList blkDevList = null;
		try {
			blkDevList = objectMapper.readValue(json, BlockDeviceList.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Could not read from lsblk. Please make a bug description.");
			System.exit(1);
		}
		
		return blkDevList.blockdevices.stream()
				.filter(device -> device.type.equals("rom"))
				.collect(Collectors.toList());
	}
	
	private InputStream lsblk() {
		//lsblk --json --output name,type
		
		Command command = new Command();
		command.setCommand("lsblk");
		command.addParameter("--json");
		command.addParameter("--output");
		command.addParameter("name,type");
		command.addParameter("-a");
		
		CommandExecutor cmd = new CommandExecutor();
		Result result = cmd.execute(command);
		
		//TODO use the code from CommandExecutor
		byte[] byteResult = result.asByteArray();
		InputStream stream = new ByteArrayInputStream(byteResult);
		
		return stream;
	}

}
