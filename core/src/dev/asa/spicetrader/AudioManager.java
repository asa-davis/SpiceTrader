package dev.asa.spicetrader;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;

public class AudioManager {
    private Music ambientOcean;
    private Music ambientVillage;

    private Music currSong;

    private Sound cannon;
    private Sound outOfAmmo;
    private Sound sale;

    private final float ambientMaxVol = 0.3f;
    private final int ambientFadeFrames = 60; // 1 sec fade
    private final float ambientFadeInc = ambientMaxVol / ambientFadeFrames;

    public boolean dockedAtVillage = false;

    public AudioManager() {
        ambientOcean = audio.newMusic(files.internal("audio/ambient/ocean.ogg"));
        ambientOcean.setVolume(ambientMaxVol);
        ambientOcean.setLooping(true);
        ambientOcean.play();

        ambientVillage = audio.newMusic(files.internal("audio/ambient/market.ogg"));
        ambientVillage.setVolume(0);
        ambientOcean.setLooping(true);
        ambientVillage.play();

        cannon = audio.newSound(files.internal("audio/soundeffects/cannon.wav"));
        outOfAmmo = audio.newSound(files.internal("audio/soundeffects/outofammotick.wav"));
        sale = audio.newSound(files.internal("audio/soundeffects/purchase.wav"));

        //currSong = audio.newMusic(files.internal("audio/soundtrack/spice1.ogg"));
        //currSong.play();
    }

    public void cannon(boolean ammo) {
        if(ammo)cannon.play(0.4f);
        else outOfAmmo.play(0.4f);
    }

    public void sale() {
        sale.play();
    }

    public void tick() {
        // fade village to max, and ocean to zero
        if(dockedAtVillage) {
            if(ambientVillage.getVolume() < ambientMaxVol)
                ambientVillage.setVolume(ambientVillage.getVolume() + ambientFadeInc);
            if(ambientOcean.getVolume() > 0)
                ambientOcean.setVolume(ambientOcean.getVolume() - ambientFadeInc);
        }

        // fade ocean to max, and village to zero
        else {
            if(ambientOcean.getVolume() < ambientMaxVol)
                ambientOcean.setVolume(ambientOcean.getVolume() + ambientFadeInc);
            if(ambientVillage.getVolume() > 0)
                ambientVillage.setVolume(ambientVillage.getVolume() - ambientFadeInc);
        }
    }
}
