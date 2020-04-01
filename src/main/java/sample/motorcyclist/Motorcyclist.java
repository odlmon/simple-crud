package sample.motorcyclist;

import sample.Entity;
import sample.Title;
import sample.ammunition.Ammunition;
import sample.attribute.Size;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Motorcyclist {

    @Title("equipment")
    private List<Ammunition> equipment = new LinkedList<>();

    public List<Ammunition> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Ammunition> equipment) {
        this.equipment = equipment;
    }

    public Motorcyclist addItem(Ammunition ammunition) {
        this.equipment.add(ammunition);
        return this;
    }

    public void resetEquipment() {
        equipment.clear();
    }

    public int getTotalCost() {
        int totalCost = 0;
        for (Ammunition ammunition : equipment) {
            totalCost += ammunition.getCost();
        }
        return totalCost;
    }

    public void sortEquipmentByCost() {
        equipment.sort(Comparator.comparingInt(Ammunition::getCost));
    }

    public int getYearOfOldestItem() {
        return Collections.min(equipment, Comparator.comparingInt(Ammunition::getDateOfIssue)).getDateOfIssue();
    }

    public int getCostOfDearestItem() {
        return Collections.max(equipment, Comparator.comparingInt(Ammunition::getCost)).getCost();
    }

    public List<Ammunition> getListOfItemsInRangeBySize(Size from, Size to) {
        List<Ammunition> sizeRange = new LinkedList<>();
        for (Ammunition ammunition : equipment) {
            if ((ammunition.getSize().ordinal() >= from.ordinal())
                    && (ammunition.getSize().ordinal() <= to.ordinal())) {
                sizeRange.add(ammunition);
            }

        }
        return sizeRange;
    }
}
