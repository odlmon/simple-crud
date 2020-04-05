package sample.ammunition;

import sample.annotation.Entity;
import sample.annotation.Title;
import sample.attribute.Material;
import sample.attribute.Size;

@Entity
public class Jacket extends Ammunition {

    @Title("Material")
    private Material material;

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Jacket(int cost, Size size, String model, int dateOfIssue, Material material) {
        super(cost, size, model, dateOfIssue);
        this.material = material;
    }

    @Override
    public String toString() {
        return "Jacket{" +
                "material=" + material +
                "} " + super.toString();
    }
}
