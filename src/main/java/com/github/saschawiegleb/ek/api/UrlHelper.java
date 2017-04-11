package com.github.saschawiegleb.ek.api;

public class UrlHelper {
	private static final int PAGE_LIMIT = 50;

	public static String getGlobalSearchURL(String search, int seite) {
		return Key.decrypt() + "/s-seite:" + Integer.toString(seite) + "/" + search + "/k0";
	}

	public static String getPageURL(Category category, int pageNumber, String searchString) {
		StringBuilder builder = new StringBuilder();
		builder.append(Key.decrypt());
		String add = "";
		if (pageNumber > 1) {
			if (pageNumber > PAGE_LIMIT) {
				pageNumber = PAGE_LIMIT;
			}
			add += "seite:" + Integer.toString(pageNumber) + "/";
		}
		if (searchString != null && !searchString.isEmpty()) {
			add += searchString + "/";
		}

		builder.append(add).append("c").append(category.getId());
		return builder.toString();
	}

	public static String getTopPageURL(Category category, int seite) {
		StringBuilder builder = new StringBuilder();
		builder.append(Key.decrypt());
		String add = "topAds/";
		if (seite > 1) {
			if (seite > PAGE_LIMIT) {
				seite = PAGE_LIMIT;
			}
			add += "seite:" + Integer.toString(seite) + "/";
		}
		builder.append(add).append("c").append(category.getId());
		return builder.toString();
	}
}
