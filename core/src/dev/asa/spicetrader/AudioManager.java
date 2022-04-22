package dev.asa.spicetrader;

import com.badlogic.gdx.audio.Music;

import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;

public class AudioManager {
    private Music ambientOcean;
    private Music ambientVillage;
    private Music currSong;

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

        //currSong = audio.newMusic(files.internal("audio/soundtrack/spice1.ogg"));
        //currSong.play();
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
