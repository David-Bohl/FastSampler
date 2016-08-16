package org.turntotech.audiocapture;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.FileDescriptor;
import java.util.HashMap;

/**
 *  From Raghav Sood on Stack Overflow.  Modified.
 */
public class SoundPoolPlayer {
    private SoundPool mShortPlayer= null;
    private HashMap mSounds = new HashMap();


    public SoundPoolPlayer(Context pContext)
    {
        // setup Soundpool
        this.mShortPlayer = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);

        mSounds.put(R.raw.metronome, this.mShortPlayer.load(pContext, R.raw.metronome, 1));
        mSounds.put(R.raw.piano_c, this.mShortPlayer.load(pContext, R.raw.piano_c, 2));

    }

    public void playPresetResource(int piResource, float pitch) {
        int iSoundId = (Integer) mSounds.get(piResource);
        this.mShortPlayer.play(iSoundId, 0.40f, 0.40f, 0, 0, pitch);
    }

    public void playShortResource(int mySoundID, float pitch) {
        this.mShortPlayer.play(mySoundID, 0.99f, 0.99f, 0, 0, pitch);
    }

    public int loadShortResource(String path){
        int soundID = mShortPlayer.load(path, 1);
        return soundID;
    }

    public void unload (int soundID){
        mShortPlayer.unload(soundID);
    }

    // Cleanup
    public void release() {
        // Cleanup
        this.mShortPlayer.release();
        this.mShortPlayer = null;
    }
}
