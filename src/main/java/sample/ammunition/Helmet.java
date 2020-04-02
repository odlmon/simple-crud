package sample.ammunition;

import sample.Entity;
import sample.Title;
import sample.attribute.HelmetType;
import sample.attribute.Size;

@Entity
public class Helmet extends Ammunition {

    @Title("Helmet type")
    private HelmetType type;

    @Title("Number of shells")
    private int numberOfShells;

    public HelmetType getType() {
        return type;
    }

    public void setType(HelmetType type) {
        this.type = type;
    }

    public int getNumberOfShells() {
        return numberOfShells;
    }

    public void setNumberOfShells(int numberOfShells) {
        this.numberOfShells = numberOfShells;
    }

    public Helmet(int cost, Size size, String model, int dateOfIssue, HelmetType type, int numberOfShells) {
        super(cost, size, model, dateOfIssue);
        this.type = type;
        this.numberOfShells = numberOfShells;
    }

    @Override
    public String toString() {
        return "Helmet{" +
                "type='" + type + '\'' +
                ", numberOfShells=" + numberOfShells +
                "} " + super.toString();
    }
}
