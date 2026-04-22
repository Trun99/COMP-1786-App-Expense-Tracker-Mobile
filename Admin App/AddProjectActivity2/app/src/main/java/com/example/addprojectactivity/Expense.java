package com.example.addprojectactivity;

import java.io.Serializable;

public class Expense implements Serializable {
    private int PtyId;
    private int PtyProjectId;
    private String PtyExpenseId = "";
    private String PtyDate = "";
    private double PtyAmount = 0.0;
    private String PtyCurrency = "GBP";
    private String PtyType = "";
    private String PtyPaymentMethod = "";
    private String PtyClaimant = "";
    private String PtyPaymentStatus = "";
    private String PtyDescription = "";
    private String PtyLocation = "";

    public Expense() {}

    public int getId() { return PtyId; }
    public void setId(int id) { this.PtyId = id; }
    
    public int getProjectId() { return PtyProjectId; }
    public void setProjectId(int projectId) { this.PtyProjectId = projectId; }
    
    public String getExpenseId() { return PtyExpenseId == null ? "" : PtyExpenseId; }
    public void setExpenseId(String expenseId) { this.PtyExpenseId = expenseId; }
    
    public String getDate() { return PtyDate == null ? "" : PtyDate; }
    public void setDate(String date) { this.PtyDate = date; }
    
    public double getAmount() { return PtyAmount; }
    public void setAmount(double amount) { this.PtyAmount = amount; }
    
    public String getCurrency() { return PtyCurrency == null ? "GBP" : PtyCurrency; }
    public void setCurrency(String currency) { this.PtyCurrency = currency; }
    
    public String getType() { return PtyType == null ? "" : PtyType; }
    public void setType(String type) { this.PtyType = type; }
    
    public String getPaymentMethod() { return PtyPaymentMethod == null ? "" : PtyPaymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.PtyPaymentMethod = paymentMethod; }
    
    public String getClaimant() { return PtyClaimant == null ? "" : PtyClaimant; }
    public void setClaimant(String claimant) { this.PtyClaimant = claimant; }
    
    public String getPaymentStatus() { return PtyPaymentStatus == null ? "" : PtyPaymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.PtyPaymentStatus = paymentStatus; }
    
    public String getDescription() { return PtyDescription == null ? "" : PtyDescription; }
    public void setDescription(String description) { this.PtyDescription = description; }
    
    public String getLocation() { return PtyLocation == null ? "" : PtyLocation; }
    public void setLocation(String location) { this.PtyLocation = location; }
}