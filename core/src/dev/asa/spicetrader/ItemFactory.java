package dev.asa.spicetrader;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ItemFactory {
    private TextureAtlas atlas;

    public ItemFactory(TextureAtlas atlas) {
        this.atlas = atlas;
    }

    //tier 1
    public Item getGinger() {
        return new Item("Ginger", atlas.findRegion("items/ginger"), 10, 5);
    }

    //tier 1 and 2
    public Item getPeppercorn() {
        return new Item("Peppercorn", atlas.findRegion("items/peppercorn"), 25, 10);
    }

    //tier 2
    public Item getCinnamon() {
        return new Item("Cinnamon", atlas.findRegion("items/cinnamon"), 50, 25);
    }

    //tier 2
    public Item getCloves() {
        return new Item("Cloves", atlas.findRegion("items/cloves"), 100, 50);
    }

    //tier 2 and 3
    public Item getNutmeg() {
        return new Item("Nutmeg", atlas.findRegion("items/nutmeg"), 200, 100);
    }

    public CannonBallItem getCannonball() { return new CannonBallItem(atlas.findRegion("items/Cannonball"), 5); }

    public RepairItem getRepairItem() { return new RepairItem(atlas.findRegion("items/repair"),20); }

    //tier 1
    public EquipableItem getCargoExpansion1() {
        Stats stats = new Stats();
        stats.cargo = 3;
        stats.accel = -1;
        return new EquipableItem("Cargo Expansion I", atlas.findRegion("items/upgrade"), stats, 0, 50);
    }

    public EquipableItem getHullUpgrade1() {
        Stats stats = new Stats();
        stats.hull = 3;
        stats.maxSpeed = -1;
        return new EquipableItem("Hull Upgrade I", atlas.findRegion("items/upgrade"), stats, 0, 50);
    }

    public EquipableItem getRudderUpgrade1() {
        Stats stats = new Stats();
        stats.turning = 2;
        return new EquipableItem("Rudder Upgrade I", atlas.findRegion("items/upgrade"), stats, 0, 50);
    }

    public EquipableItem getSailUpgrade1() {
        Stats stats = new Stats();
        stats.maxSpeed = 2;
        stats.accel = 2;
        return new EquipableItem("Sail Upgrade I", atlas.findRegion("items/upgrade"), stats, 0, 75);
    }

    public EquipableItem getCannonUpgrade1() {
        Stats stats = new Stats();
        stats.damage = 1;
        stats.range = 1;
        stats.accel = -1;
        stats.maxSpeed = -1;
        return new EquipableItem("Cannon Upgrade I", atlas.findRegion("items/upgrade"), stats, 0, 75);
    }

    //tier2
    public EquipableItem getCargoExpansion2() {
        Stats stats = new Stats();
        stats.cargo = 6;
        stats.accel = -1;
        return new EquipableItem("Cargo Expansion II", atlas.findRegion("items/upgrade"), stats, 0, 100);
    }

    public EquipableItem getHullUpgrade2() {
        Stats stats = new Stats();
        stats.hull = 6;
        stats.maxSpeed = -1;
        return new EquipableItem("Hull Upgrade II", atlas.findRegion("items/upgrade"), stats, 0, 100);
    }

    public EquipableItem getRudderUpgrade2() {
        Stats stats = new Stats();
        stats.turning = 4;
        return new EquipableItem("Rudder Upgrade II", atlas.findRegion("items/upgrade"), stats, 0, 100);
    }

    public EquipableItem getSailUpgrade2() {
        Stats stats = new Stats();
        stats.maxSpeed = 4;
        stats.accel = 4;
        return new EquipableItem("Sail Upgrade II", atlas.findRegion("items/upgrade"), stats, 0, 150);
    }

    public EquipableItem getCannonUpgrade2() {
        Stats stats = new Stats();
        stats.damage = 2;
        stats.range = 2;
        stats.accel = -1;
        stats.maxSpeed = -1;
        return new EquipableItem("Cannon Upgrade II", atlas.findRegion("items/upgrade"), stats, 0, 150);
    }

    //tier3
    public EquipableItem getCargoExpansion3() {
        Stats stats = new Stats();
        stats.cargo = 9;
        stats.accel = -2;
        return new EquipableItem("Cargo Expansion III", atlas.findRegion("items/upgrade"), stats, 0, 200);
    }

    public EquipableItem getHullUpgrade3() {
        Stats stats = new Stats();
        stats.hull = 9;
        stats.maxSpeed = -2;
        return new EquipableItem("Hull Upgrade III", atlas.findRegion("items/upgrade"), stats, 0, 200);
    }

    public EquipableItem getRudderUpgrade3() {
        Stats stats = new Stats();
        stats.turning = 6;
        return new EquipableItem("Rudder Upgrade III", atlas.findRegion("items/upgrade"), stats, 0, 200);
    }

    public EquipableItem getSailUpgrade3() {
        Stats stats = new Stats();
        stats.maxSpeed = 8;
        stats.accel = 8;
        return new EquipableItem("Sail Upgrade III", atlas.findRegion("items/upgrade"), stats, 0, 300);
    }

    public EquipableItem getCannonUpgrade3() {
        Stats stats = new Stats();
        stats.damage = 4;
        stats.range = 6;
        stats.accel = -2;
        stats.maxSpeed = -2;
        return new EquipableItem("Cannon Upgrade III", atlas.findRegion("items/upgrade"), stats, 0, 300);
    }

    public EquipableItem getWaukee() {
        Stats stats = new Stats();
        stats.damage = 20;
        stats.range = 20;
        stats.accel = 10;
        stats.maxSpeed = 10;
        stats.turning = 5;
        stats.hull = 12;
        return new EquipableItem("Milwaukee Special Reserve", atlas.findRegion("items/upgrade"), stats, 0, 12);
    }

    private ArrayList<EquipableItem> getTier1Equipables() {
        ArrayList<EquipableItem> items = new ArrayList<>();
        items.add(getCargoExpansion1());
        items.add(getHullUpgrade1());
        items.add(getRudderUpgrade1());
        items.add(getSailUpgrade1());
        items.add(getCannonUpgrade1());
        return items;
    }

    private ArrayList<EquipableItem> getTier2Equipables() {
        ArrayList<EquipableItem> items = new ArrayList<>();
        items.add(getCargoExpansion2());
        items.add(getHullUpgrade2());
        items.add(getRudderUpgrade2());
        items.add(getSailUpgrade2());
        items.add(getCannonUpgrade2());
        return items;
    }

    private ArrayList<EquipableItem> getTier3Equipables() {
        ArrayList<EquipableItem> items = new ArrayList<>();
        items.add(getCargoExpansion3());
        items.add(getHullUpgrade3());
        items.add(getRudderUpgrade3());
        items.add(getSailUpgrade3());
        items.add(getCannonUpgrade3());
        items.add(getWaukee());
        return items;
    }

    public EquipableItem getRandomEquipable(int tier) {
        ArrayList<EquipableItem> choices = new ArrayList<>();

        switch(tier) {
            case 1:
                choices = getTier1Equipables();
                break;
            case 2:
                choices = getTier2Equipables();
                break;
            case 3:
                choices = getTier3Equipables();
        }

        return choices.get(Utils.randInt(0, choices.size() - 1));
    }
}
