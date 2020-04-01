package sample.ammunition;

import sample.Entity;
import sample.Title;
import sample.attribute.BootsType;
import sample.attribute.Size;

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
    public String toString() {
        return "Boots{" +
                "bootsType=" + bootsType +
                "} " + super.toString();
    }
}
