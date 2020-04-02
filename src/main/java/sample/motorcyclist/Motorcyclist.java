package sample.motorcyclist;

import sample.Entity;
import sample.Title;
import sample.ammunition.*;
import sample.attribute.Size;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Motorcyclist {

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
}
