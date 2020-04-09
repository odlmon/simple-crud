package sample.ammunition;

import sample.annotation.Entity;
import sample.annotation.Title;
import sample.attribute.BootsType;
import sample.attribute.Size;

import java.util.Objects;

@Entity
public class Boots extends Ammunition {

    @Title("Boots type")
    private BootsType bootsType;

    public BootsType getBootsType() {
        return bootsType;
    }

    public void setBootsType(BootsType bootsType) {
        this.bootsType = bootsType;
    }

    public Boots(int cost, Size size, String model, int dateOfIssue, BootsType bootsType) {
        super(cost, size, model, dateOfIssue);
        this.bootsType = bootsType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Boots)) return false;
        if (!super.equals(o)) return false;
        Boots boots = (Boots) o;
        return bootsType == boots.bootsType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bootsType);
    }

    @Override
    public String toString() {
        return "Boots{" +
                "bootsType=" + bootsType +
                "} " + super.toString();
    }
}
