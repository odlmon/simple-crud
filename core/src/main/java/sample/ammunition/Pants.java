package sample.ammunition;

import sample.annotation.Entity;
import sample.annotation.Title;
import sample.attribute.Color;
import sample.attribute.Size;
import sample.attribute.TypeOfPockets;

import java.util.Objects;

@Entity
public class Pants extends Ammunition {

    @Title("Color")
    private Color color;

    @Title("Pocket type")
    private TypeOfPockets pocketType;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public TypeOfPockets getPocketType() {
        return pocketType;
    }

    public void setPocketType(TypeOfPockets pocketType) {
        this.pocketType = pocketType;
    }

    public Pants(int cost, Size size, String model, int dateOfIssue, Color color, TypeOfPockets pocketType) {
        super(cost, size, model, dateOfIssue);
        this.color = color;
        this.pocketType = pocketType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pants)) return false;
        if (!super.equals(o)) return false;
        Pants pants = (Pants) o;
        return color == pants.color &&
                pocketType == pants.pocketType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), color, pocketType);
    }

    @Override
    public String toString() {
        return "Pants{" +
                "color=" + color +
                ", pocketType=" + pocketType +
                "} " + super.toString();
    }
}
