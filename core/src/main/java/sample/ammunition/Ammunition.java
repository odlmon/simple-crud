package sample.ammunition;

import sample.annotation.Title;
import sample.attribute.Size;

import java.io.Serializable;
import java.util.Objects;

public abstract class Ammunition implements Serializable {

    @Title("Cost")
    private int cost;
    @Title("Size")
    private Size size;
    @Title("Model")
    private String model;
    @Title("Production date")
    private int dateOfIssue;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ammunition)) return false;
        Ammunition that = (Ammunition) o;
        return cost == that.cost &&
                dateOfIssue == that.dateOfIssue &&
                size == that.size &&
                Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cost, size, model, dateOfIssue);
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
