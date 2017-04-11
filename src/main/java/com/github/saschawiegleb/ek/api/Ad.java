package com.github.saschawiegleb.ek.api;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Ad {
	public static Ad byElement(String id, Element backupElement) {
		Ad element = new Ad();
		element.setId(id);

		return element;
	}

	public static Ad byId(Long id, Element element) {
		return byId(id.toString(), element);
	}

	public static Ad byId(String id, Element backupElement) {
		id = id.split("/")[id.split("/").length - 1]; // works for "id" and
														// "user-xxx-id"
		Ad element = new Ad();
		element.setId(id);
		Document doc = Rest.get(linkById(id));

		if (doc.getElementById("viewad-adexpired") != null || doc.getElementById("home") != null) {
			System.out.println("already expired");
			if (backupElement != null) {
				return byElement(id, backupElement);
			} else {
				element.setHeadline("Artikel bereits verkauft.");
				return element;
			}
		}

		Element category = doc.getElementById("vap-brdcrmb");
		Element title = doc.getElementById("viewad-title");
		if (title != null) {
			element.setHeadline(title.ownText());
		} else {
			System.err.println("no title: " + id);
		}

		Element price = doc.getElementById("viewad-price");
		if (price != null) {
			element.setPrice(price.ownText());
		} else {
			System.err.println("no price: " + id);
		}

		if (doc.getElementById("viewad-thumbnails") != null) {
			Elements elements = doc.getElementById("viewad-thumbnails").getElementsByTag("img");
			List<String> images = new ArrayList<String>();
			for (Element img : elements) {
				String link = img.attr("data-imgsrc").replaceFirst("_72", "_57");
				images.add(link);
				// try(InputStream in = new URL(link).openStream()){
				// Files.copy(in,
				// Paths.get("a.jpg"),StandardCopyOption.REPLACE_EXISTING);
				// }catch(Exception e){
				// System.out.println(link);
				// }
			}
			element.setImages(images);
		}

		Element details = doc.getElementById("viewad-details");
		element.setDescription(doc.getElementById("viewad-description-text").ownText());
		// NullPointerException
		element.setVendorId(doc.getElementById("viewad-contact").getElementsByClass("iconlist-icon-big").first()
				.getElementsByTag("a").first().attr("href").replaceAll("/s-bestandsliste\\.html\\?userId=", ""));

		return element;
	}

	public static String linkById(String id) {
		return Key.decrypt() + "s-anzeige/" + id;
	}

	// leads to approx 1kb per entry -> 1 million entries per GB RAM
	private String category;
	private String description = "";
	private String headline = "";
	private String id;
	private List<String> images;
	private String location;
	private String price;
	private boolean printed = false;

	private String subcategory;

	private String time;

	private String vendorId;

	private String vendorNumber;

	private Ad() {
	}

	public String getLink() {
		return Key.decrypt() + "s-anzeige/" + id;
	}

	public boolean isPrinted() {
		return printed;
	}

	public String searchString() {
		return headline.toLowerCase() + " " + description.toLowerCase();
	}

	private void setCategory(String category) {
		this.category = category;
	}

	private void setDescription(String description) {
		this.description = description;
	}

	private void setHeadline(String title) {
		this.headline = title;
	}

	private void setId(String id) {
		this.id = id;
	}

	private void setImages(List<String> images) {
		this.images = images;
	}

	private void setLocation(String location) {
		this.location = location;
	}

	private void setPrice(String price) {
		this.price = price;
	}

	public void setPrinted() {
		this.printed = true;
	}

	private void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}

	private void setTime(String time) {
		this.time = time;
	}

	private void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	private void setVendorNumber(String vendorNumber) {
		this.vendorNumber = vendorNumber;
	}

	@Override
	public String toString() {
		return "Ad [headline=" + headline + ", id=" + id + ", getLink()=" + getLink() + "]";
	}
}
