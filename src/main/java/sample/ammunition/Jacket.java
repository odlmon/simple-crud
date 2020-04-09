package sample.ammunition;

import sample.annotation.Entity;
import sample.annotation.Title;
import sample.attribute.Material;
import sample.attribute.Size;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Jacket)) return false;
        if (!super.equals(o)) return false;
        Jacket jacket = (Jacket) o;
        return material == jacket.material;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), material);
    }

    @Override
    public String toString() {
        return "Jacket{" +
                "material=" + material +
                "} " + super.toString();
    }
}
