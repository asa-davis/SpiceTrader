package dev.asa.spicetrader;

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
}

