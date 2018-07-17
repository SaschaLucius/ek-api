package com.github.saschawiegleb.ek.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.github.saschawiegleb.ek.entity.Ad;
import com.github.saschawiegleb.ek.entity.Category;

import javaslang.collection.List;

public class EkServiceTest {

    private EkService service = null;

    @Before
    public void setUp() {
        service = new EkService();
    }

    @Test
    public void testGetAllAds() {
        Category category = service.getCategory(245);
        List<Ad> ads = service.getAds(category);
        assertThat(ads.length()).isGreaterThan(25 * 50);
    }

    @Ignore
    public void testGetCategory() {
        Category category = service.getCategory("Foto");
        assertThat(category.id()).isEqualTo(245);
        assertThat(service.getCategory(245)).isEqualTo(category);
    }

    @Test
    public void testGetInvalidPageAds() {
        Category category = service.getCategory(245);
        List<Ad> ads = service.getAds(category, 51); // will return page 50
        assertThat(ads.length()).isEqualTo(27);
    }

    // @Test
    public void testGetMaxMinAds() {
        Category category = service.getCategory(245);
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (int i = 1; i < 50; i++) {
            long startTime = System.currentTimeMillis();
            List<Ad> ads = service.getAds(category, i);
            long estimatedTime = System.currentTimeMillis() - startTime;
            System.out.println("Page: " + i + " Ads: " + ads.length() + " Time: " + estimatedTime);
            if (max < ads.length()) {
                max = ads.length();
            }
            if (min > ads.length()) {
                min = ads.length();
            }
        }

        System.out.println("Min: " + min + " Max: " + max);
        assertThat(min).isLessThan(max);
    }

    @Test
    public void testGetPageOneAds() {
        Category category = service.getCategory(245);
        List<Ad> ads = service.getAds(category, 1);
        assertThat(ads.length()).isEqualTo(27);
    }

    @Test
    public void testGetSomePageAds() {
        Category category = service.getCategory(245);
        List<Ad> ads = service.getAds(category, 1, 25, 50);
        assertThat(ads.length()).isLessThanOrEqualTo(27 * 3).isGreaterThanOrEqualTo(25 * 3);
    }

    @Test
    public void testLimit() {
        Category category = service.getCategory(245);
        List<Ad> ads = service.getLatestAds(category, Integer.MAX_VALUE);
        assertThat(ads.length()).isEqualTo(0);
    }

    @Test
    public void testNoLimit() {
        Category category = service.getCategory(245);
        List<Ad> ads = service.getLatestAds(category, 0);
        assertThat(ads.length()).isEqualTo(27);
    }
}
