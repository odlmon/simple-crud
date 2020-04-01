package sample.ammunition;

import sample.Title;
import sample.attribute.Size;

public abstract class Ammunition {

    @Title("Cost")
    private int cost = 100;
    @Title("Size")
    private Size size = Size.XL;
    @Title("Model")
    private String model = "undetermined";
    @Title("Production date")
    private int dateOfIssue = 2018;

    public int getCost() {
        return cost;
    }
    public Size getSize() {
        return size;
    }
    public String getModel() {
        return model;
    }
    public int getDateOfIssue() {
        return dateOfIssue;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    public void setSize(Size size) {
        this.size = size;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public void setDateOfIssue(int dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public Ammunition(int cost, Size size, String model, int dateOfIssue) {
        this.cost = cost;
        this.size = size;
        this.model = model;
        this.dateOfIssue = dateOfIssue;
    }

    @Override
    public String toString() {
        return "Ammunition{" +
                "cost=" + cost +
                ", size=" + size +
                ", model='" + model + '\'' +
                ", dateOfIssue=" + dateOfIssue +
                '}';
    }
}
