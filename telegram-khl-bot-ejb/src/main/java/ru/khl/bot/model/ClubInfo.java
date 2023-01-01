package ru.khl.bot.model;

public class ClubInfo {

    private String name;
    private String id;
    private String conference;
    private String city;

    public ClubInfo(String name, String id, String conference, String city) {
        this.name = name;
        this.id = id;
        this.conference = conference;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getConference() {
        return conference;
    }

    public String getCity() {
        return city;
    }
}
