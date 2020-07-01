package de.immerfroehlich.javajuicer.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ReflectionUtils {
	
	public static <T> T getValueOfPath(Class<T> clazz, Object object, String path) {
		String[] partPath = path.split("[.]", 2);
		
		try {
			Object obj = object.getClass().getField(partPath[0]).get(object);
			if(partPath.length == 1) {
				return (T) obj;
			}
			else {
				return getValueOfPathRec(clazz, obj, partPath[1]);
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("Programming error: the path is wrong.", e);
		}
		
	}
	
	private static <T> T getValueOfPathRec(Class<T> clazz, Object object, String path) {
		String[] partPath = path.split("[.]", 2);
		
		try {
			if(partPath.length == 1) {
				Class<? extends Object> objectClass = object.getClass();
				if(objectClass.equals(ArrayList.class)) {
					return (T) ((List) object).stream().map((e) -> {
						try {
							return e.getClass().getField(partPath[0]).get(e);
						} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
								| SecurityException e1) {
							// TODO Auto-generated catch block
							throw new RuntimeException("Programming error: the path is wrong.", e1);
						}
					}).collect(Collectors.joining(", "));
				}
				else {
					Object obj = object.getClass().getField(partPath[0]).get(object);
					return (T) obj;
				}
			}
			else {
				throw new RuntimeException("Not yet implemented.");
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("Programming error: the path is wrong.", e);
		}
	}

}
