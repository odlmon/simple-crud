package sample.motorcyclist;

import sample.ammunition.Boots;
import sample.ammunition.Helmet;
import sample.ammunition.Jacket;
import sample.ammunition.Pants;
import sample.annotation.Entity;
import sample.annotation.Title;

import java.io.Serializable;

@Entity
public class Motorcyclist implements Serializable {

    @Title("Helmet")
    private Helmet helmet;

    @Title("Jacket")
    private Jacket jacket;

    @Title("Pants")
    private Pants pants;

    @Title("Boots")
    private Boots boots;

    public Helmet getHelmet() {
        return helmet;
    }

    public void setHelmet(Helmet helmet) {
        this.helmet = helmet;
    }

    public Jacket getJacket() {
        return jacket;
    }

    public void setJacket(Jacket jacket) {
        this.jacket = jacket;
    }

    public Pants getPants() {
        return pants;
    }

    public void setPants(Pants pants) {
        this.pants = pants;
    }

    public Boots getBoots() {
        return boots;
    }

    public void setBoots(Boots boots) {
        this.boots = boots;
    }

    public Motorcyclist(Helmet helmet, Jacket jacket, Pants pants, Boots boots) {
        this.helmet = helmet;
        this.jacket = jacket;
        this.pants = pants;
        this.boots = boots;
    }

    @Override
    public String toString() {
        return "Motorcyclist{" +
                "helmet=" + helmet +
                ", jacket=" + jacket +
                ", pants=" + pants +
                ", boots=" + boots +
                '}';
    }
}
