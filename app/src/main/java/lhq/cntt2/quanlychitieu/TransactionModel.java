package lhq.cntt2.quanlychitieu;

import com.google.firebase.Timestamp;

public class TransactionModel {
    private String transactionId;
    private String userId;
    private double amount;
    private String type; // "INCOME" (Thu) hoặc "EXPENSE" (Chi)
    private String category;
    private Timestamp timestamp;
    private String note;

    public TransactionModel() {}

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}