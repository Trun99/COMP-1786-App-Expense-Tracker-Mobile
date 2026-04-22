package com.example.addprojectactivity;

import java.io.Serializable;

public class Project implements Serializable {
    private int PtyId;
    private String PtyCode = "";
    private String PtyName = "";
    private String PtyDescription = "";
    private String PtyStartDate = "";
    private String PtyEndDate = "";
    private String PtyManager = "";
    private String PtyStatus = "";
    private double PtyBudget = 0.0;
    private String PtyRequirements = "";
    private String PtyClient = "";

    public Project() {}

    public int getId() { return PtyId; }
    public void setId(int id) { this.PtyId = id; }
    
    public String getCode() { return PtyCode == null ? "" : PtyCode; }
    public void setCode(String code) { this.PtyCode = code; }
    
    public String getName() { return PtyName == null ? "" : PtyName; }
    public void setName(String name) { this.PtyName = name; }
    
    public String getDescription() { return PtyDescription == null ? "" : PtyDescription; }
    public void setDescription(String description) { this.PtyDescription = description; }
    
    public String getStartDate() { return PtyStartDate == null ? "" : PtyStartDate; }
    public void setStartDate(String startDate) { this.PtyStartDate = startDate; }
    
    public String getEndDate() { return PtyEndDate == null ? "" : PtyEndDate; }
    public void setEndDate(String endDate) { this.PtyEndDate = endDate; }
    
    public String getManager() { return PtyManager == null ? "" : PtyManager; }
    public void setManager(String manager) { this.PtyManager = manager; }
    
    public String getStatus() { return PtyStatus == null ? "" : PtyStatus; }
    public void setStatus(String status) { this.PtyStatus = status; }
    
    public double getBudget() { return PtyBudget; }
    public void setBudget(double budget) { this.PtyBudget = budget; }
    
    public String getRequirements() { return PtyRequirements == null ? "" : PtyRequirements; }
    public void setRequirements(String requirements) { this.PtyRequirements = requirements; }
    
    public String getClient() { return PtyClient == null ? "" : PtyClient; }
    public void setClient(String client) { this.PtyClient = client; }
}