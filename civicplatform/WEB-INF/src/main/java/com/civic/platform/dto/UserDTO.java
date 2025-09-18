package com.civic.platform.dto;

import java.util.Date;

public class UserDTO {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String location;
    private Date joinDate;
    private int issuesReported;
    private int issuesResolved;
    private int communityValidations;
    private int commentsPosted;
    private int totalViews;
    private int upvotesReceived;
    private String accountLevel;
    private int experiencePoints;
    private int nextLevelPoints;

    // Constructors
    public UserDTO() {}

    public UserDTO(int id, String name, String email, String phone, String location, 
                  Date joinDate, int issuesReported, int issuesResolved, 
                  int communityValidations, int commentsPosted, int totalViews, 
                  int upvotesReceived, String accountLevel, int experiencePoints, 
                  int nextLevelPoints) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.joinDate = joinDate;
        this.issuesReported = issuesReported;
        this.issuesResolved = issuesResolved;
        this.communityValidations = communityValidations;
        this.commentsPosted = commentsPosted;
        this.totalViews = totalViews;
        this.upvotesReceived = upvotesReceived;
        this.accountLevel = accountLevel;
        this.experiencePoints = experiencePoints;
        this.nextLevelPoints = nextLevelPoints;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Date getJoinDate() { return joinDate; }
    public void setJoinDate(Date joinDate) { this.joinDate = joinDate; }

    public int getIssuesReported() { return issuesReported; }
    public void setIssuesReported(int issuesReported) { this.issuesReported = issuesReported; }

    public int getIssuesResolved() { return issuesResolved; }
    public void setIssuesResolved(int issuesResolved) { this.issuesResolved = issuesResolved; }

    public int getCommunityValidations() { return communityValidations; }
    public void setCommunityValidations(int communityValidations) { this.communityValidations = communityValidations; }

    public int getCommentsPosted() { return commentsPosted; }
    public void setCommentsPosted(int commentsPosted) { this.commentsPosted = commentsPosted; }

    public int getTotalViews() { return totalViews; }
    public void setTotalViews(int totalViews) { this.totalViews = totalViews; }

    public int getUpvotesReceived() { return upvotesReceived; }
    public void setUpvotesReceived(int upvotesReceived) { this.upvotesReceived = upvotesReceived; }

    public String getAccountLevel() { return accountLevel; }
    public void setAccountLevel(String accountLevel) { this.accountLevel = accountLevel; }

    public int getExperiencePoints() { return experiencePoints; }
    public void setExperiencePoints(int experiencePoints) { this.experiencePoints = experiencePoints; }

    public int getNextLevelPoints() { return nextLevelPoints; }
    public void setNextLevelPoints(int nextLevelPoints) { this.nextLevelPoints = nextLevelPoints; }
}
