package cn.edu.sdu.online.util;

import cn.edu.sdu.online.entity.Place;

public class PublishDestinatePlace {
	private static Place place = new Place();

	public static void setPlace(String name, String addr, double longitude,
			double latitude) {
		place.setName(name);
		place.setAddr(addr);
		place.setLongitude(longitude);
		place.setLatitude(latitude);

	}

	public static Place getPlace() {
		return place;
	}

}
