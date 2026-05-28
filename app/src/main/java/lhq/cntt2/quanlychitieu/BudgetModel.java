package lhq.cntt2.quanlychitieu;

public class BudgetModel {
    private String category;
    private double limitAmount;

    public BudgetModel(String category, double limitAmount) {
        this.category = category;
        this.limitAmount = limitAmount;
    }

    public String getCategory() { return category; }
    public double getLimitAmount() { return limitAmount; }
}