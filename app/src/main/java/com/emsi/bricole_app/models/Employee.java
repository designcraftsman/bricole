package com.emsi.bricole_app.models;

import java.util.List;
import java.util.Map;

public class Employee {
    public String firstname;
    public String lastname;
    public int phoneNumberPrefix;
    public String phoneNumber;
    public List<String> skills;
    public String profilePictureUrl;
    public Map<String, String> availabilityMap;
    public List<String> jobPreferences;
    public List<Review> reviews;
}
