package dev.asa.spicetrader.items;

import java.util.ArrayList;

public class Stats {
    public int maxSpeed = 0;
    public int accel = 0;
    public int turning = 0;
    public int hull = 0;
    public int cargo = 0;
    public int damage = 0;
    public int range = 0;

    public void add(Stats b) {
        maxSpeed += b.maxSpeed;
        accel += b.accel;
        turning += b.turning;
        hull += b.hull;
        cargo += b.cargo;
        damage += b.damage;
        range += b.range;
    }

    public void subtract(Stats b) {
        maxSpeed -= b.maxSpeed;
        accel -= b.accel;
        turning -= b.turning;
        hull -= b.hull;
        cargo -= b.cargo;
        damage -= b.damage;
        range -= b.range;
    }

    public ArrayList<String> toStringList() {
        ArrayList<String> stats = new ArrayList<>();

        if(maxSpeed > 0)
            stats.add("+" + String.valueOf(maxSpeed) + " max speed");
        else if(maxSpeed < 0)
            stats.add(String.valueOf(maxSpeed) + " max speed");

        if(accel > 0)
            stats.add("+" + String.valueOf(accel) + " acceleration");
        else if(accel < 0)
            stats.add(String.valueOf(accel) + " acceleration");

        if(turning > 0)
            stats.add("+" + String.valueOf(turning) + " turning");
        else if(turning < 0)
            stats.add(String.valueOf(turning) + " turning");

        if(hull > 0)
            stats.add("+" + String.valueOf(hull) + " hull");
        else if(hull < 0)
            stats.add(String.valueOf(hull) + " hull");

        if(cargo > 0)
            stats.add("+" + String.valueOf(cargo) + " cargo space");
        else if(cargo < 0)
            stats.add(String.valueOf(cargo) + " cargo space");

        if(damage > 0)
            stats.add("+" + String.valueOf(damage) + " damage");
        else if(damage < 0)
            stats.add(String.valueOf(damage) + " damage");

        if(range > 0)
            stats.add("+" + String.valueOf(range) + " range");
        else if(range < 0)
            stats.add(String.valueOf(range) + " range");

        return stats;
    }
}

