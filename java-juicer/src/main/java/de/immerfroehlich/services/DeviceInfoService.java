package de.immerfroehlich.services;

import java.util.List;
import java.util.stream.Collectors;

import de.immerfroehlich.deviceinfo.BlockDevice;
import de.immerfroehlich.deviceinfo.DeviceInfo;

public class DeviceInfoService {
	
	public List<String> getDeviceList() {
		DeviceInfo deviceInfo = new DeviceInfo();
		List<BlockDevice> devices = deviceInfo.getRomDevices();
		List<String> deviceNames = devices.stream()
				.map(x -> "/dev/" + x.name)
				.collect(Collectors.toList());
		return deviceNames;
	}

}
