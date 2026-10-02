package com.hrflow.service;

import com.hrflow.model.Candidate;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileImageServiceTest {

    @Test
    public void testGenderClassification() {
        assertTrue(ProfileImageService.isFemale("Aditi Verma"));
        assertTrue(ProfileImageService.isFemale("Deepika Nair"));
        assertTrue(ProfileImageService.isFemale("Isha Patel"));
        assertTrue(ProfileImageService.isFemale("Pooja Iyer"));
        assertTrue(ProfileImageService.isFemale("Zoya Khan"));
        assertTrue(ProfileImageService.isFemale("Sarah Jenkins"));

        assertFalse(ProfileImageService.isFemale("Aarav Sharma"));
        assertFalse(ProfileImageService.isFemale("Arjun Reddy"));
        assertFalse(ProfileImageService.isFemale("Rohan Das"));
        assertFalse(ProfileImageService.isFemale("Tarun Gupta"));
        assertFalse(ProfileImageService.isFemale("Varun Mehta"));
    }

    @Test
    public void testProfileImageResourcesExist() {
        String[] sampleMalePaths = {
                "/com/hrflow/images/profiles/male/v3_0003963.jpg",
                "/com/hrflow/images/profiles/male/v3_0977431.jpg"
        };
        for (String path : sampleMalePaths) {
            try (InputStream is = getClass().getResourceAsStream(path)) {
                assertNotNull(is, "Male profile image should exist at: " + path);
            } catch (Exception e) {
                fail("Failed to load resource: " + path);
            }
        }

        String[] sampleFemalePaths = {
                "/com/hrflow/images/profiles/female/v3_0056235.jpg",
                "/com/hrflow/images/profiles/female/v3_0935103.jpg"
        };
        for (String path : sampleFemalePaths) {
            try (InputStream is = getClass().getResourceAsStream(path)) {
                assertNotNull(is, "Female profile image should exist at: " + path);
            } catch (Exception e) {
                fail("Failed to load resource: " + path);
            }
        }
    }
}
