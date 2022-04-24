package dev.asa.spicetrader;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.Random;

import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;

public class AudioManager {
    private Music ambientOcean;
    private Music ambientVillage;

    private Music currSong;

    private Sound cannon;
    private Sound outOfAmmo;
    private Sound sale;

    private final int ambientFadeFrames = 60; // 1 sec fade
    private final float ambientMaxVol = 0.3f;
    private final float ambientFadeInc = ambientMaxVol / ambientFadeFrames;

    private final int soundtrackFadeFrames = 180;
    private final float soundtrackMaxVol = 0.65f;
    private final float soundtrackMinVol = 0.1f;
    private final float soundtrackFadeInc = (soundtrackMaxVol - soundtrackMinVol) / soundtrackFadeFrames;
    private final int framesBetweenSongs = 270;
    private int songBreakCounter;
    private boolean pauseBetweenSongs;

    private int numSongs = 6;

    public boolean dockedAtVillage;

    Random rand;

    public AudioManager() {
        ambientOcean = audio.newMusic(files.internal("audio/ambient/ocean.ogg"));
        ambientOcean.setVolume(ambientMaxVol);
        ambientOcean.setLooping(true);
        ambientOcean.play();

        ambientVillage = audio.newMusic(files.internal("audio/ambient/market.ogg"));
        ambientVillage.setVolume(0);
        ambientVillage.setLooping(true);
        ambientVillage.play();

        cannon = audio.newSound(files.internal("audio/soundeffects/cannon.wav"));
        outOfAmmo = audio.newSound(files.internal("audio/soundeffects/outofammotick.wav"));
        sale = audio.newSound(files.internal("audio/soundeffects/purchase.wav"));

        rand = new Random();
        songBreakCounter = framesBetweenSongs;
        pauseBetweenSongs = true;

        dockedAtVillage = false;
    }

    public void cannon(boolean ammo) {
        if(ammo)cannon.play(0.4f);
        else outOfAmmo.play(0.4f);
    }

    public void sale() {
        sale.play(0.4f);
    }

    public void tick() {
        System.out.println(ambientVillage.getVolume());
        // fade village to max, and ocean to zero, soundtrack to zero
        if(dockedAtVillage) {
            if(ambientVillage.getVolume() < ambientMaxVol)
                ambientVillage.setVolume(ambientVillage.getVolume() + ambientFadeInc);
            if(ambientOcean.getVolume() >= (0 + ambientFadeInc))
                ambientOcean.setVolume(ambientOcean.getVolume() - ambientFadeInc);
            if(currSong != null && currSong.getVolume() >= (soundtrackMinVol + soundtrackFadeInc))
                currSong.setVolume(currSong.getVolume() - soundtrackFadeInc);
        }

        // fade ocean to max, and village to zero, soundtrack to max
        else {
            if(ambientOcean.getVolume() < ambientMaxVol)
                ambientOcean.setVolume(ambientOcean.getVolume() + ambientFadeInc);
            if(currSong != null && currSong.getVolume() < soundtrackMaxVol)
                currSong.setVolume(currSong.getVolume() + soundtrackFadeInc);
            if(ambientVillage.getVolume() >= (0 + ambientFadeInc))
                ambientVillage.setVolume(ambientVillage.getVolume() - ambientFadeInc);
        }

        if(pauseBetweenSongs) {
            if(songBreakCounter > 0)
                songBreakCounter--;
            else {
                songBreakCounter = framesBetweenSongs;
                pauseBetweenSongs = false;
                loadRandomSong();
            }
        }

        if(currSong != null && !currSong.isPlaying()) {
            pauseBetweenSongs = true;
        }
    }

    private void loadRandomSong() {
        int songNum = rand.nextInt(numSongs) + 1;
        currSong = audio.newMusic(files.internal("audio/soundtrack/spice" + songNum + ".wav"));
        currSong.setVolume(0);
        currSong.play();
    }

    public void dispose() {
        cannon.dispose();
        outOfAmmo.dispose();
        sale.dispose();
        ambientOcean.dispose();
        ambientVillage.dispose();
        if(currSong != null) currSong.dispose();
    }
}
