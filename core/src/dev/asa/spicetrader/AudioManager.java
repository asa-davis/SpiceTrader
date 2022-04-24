package dev.asa.spicetrader;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.Random;

import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;

public class AudioManager {
    private final Sound cannon;
    private final Sound outOfAmmo;
    private final Sound sale;

    private final Music ambientOcean;
    private final Music ambientVillage;

    private final int ambientFadeFrames = 60; // 1 sec fade
    private final float ambientOceanMaxVol = 0.3f;
    private final float ambientVillageMaxVol = 0.2f;
    private final float ambientOceanFadeInc = ambientOceanMaxVol / ambientFadeFrames;
    private final float ambientVillageFadeInc = ambientVillageMaxVol / ambientFadeFrames;

    private Music currSongDummy;
    private Sound currSong;
    private long currSongId;
    private float currPitch;

    private final float soundtrackVol = 0.5f;
    private final int framesBetweenSongs = 120; // 270 good
    private int songBreakCounter;
    private boolean pauseBetweenSongs;

    private final int numSongs = 6;

    private boolean dockedAtVillage;

    Random rand;

    private static AudioManager instance = null;

    public static AudioManager getInstance() {
        if(instance == null)
            instance = new AudioManager();
        return instance;
    }

    private AudioManager() {
        ambientOcean = audio.newMusic(files.internal("audio/ambient/ocean.ogg"));
        ambientOcean.setVolume(ambientOceanMaxVol);
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
        sale.play(0.3f);
    }

    public void enterVillage() {
        dockedAtVillage = true;
        currSongDummy.pause();
        currSong.pause();
    }

    public void leaveVillage() {
        dockedAtVillage = false;
        currSongDummy.play();
        currSong.resume();
    }

    public void tick() {
        // fade village to max, and ocean to zero
        if(dockedAtVillage) {
            if(ambientVillage.getVolume() < ambientVillageMaxVol)
                ambientVillage.setVolume(ambientVillage.getVolume() + ambientVillageFadeInc);
            if(ambientOcean.getVolume() >= (0 + ambientOceanFadeInc))
                ambientOcean.setVolume(ambientOcean.getVolume() - ambientOceanFadeInc);
        }

        // fade ocean to max, and village to zero
        else {
            if(ambientOcean.getVolume() < ambientOceanMaxVol)
                ambientOcean.setVolume(ambientOcean.getVolume() + ambientOceanFadeInc);
            if(ambientVillage.getVolume() >= (0 + ambientVillageFadeInc))
                ambientVillage.setVolume(ambientVillage.getVolume() - ambientVillageFadeInc);
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

        if(currSongDummy != null && (!currSongDummy.isPlaying() && !dockedAtVillage)) {
            pauseBetweenSongs = true;
        }
    }

    private void loadRandomSong() {
        int songNum = rand.nextInt(numSongs) + 1;
        currSong = audio.newSound(files.internal("audio/soundtrack/spice" + songNum + ".wav"));
        currSongDummy = audio.newMusic(files.internal("audio/soundtrack/spice" + songNum + ".wav"));
        currSongId = currSong.play(soundtrackVol);
        currSongDummy.play();
        currSongDummy.setVolume(0);
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
