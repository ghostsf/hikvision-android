package com.demo.sdk6x.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Handler;

/**
 * 抓拍时的声音类
 * @author huangweifeng
 * @Data 2013-10-23
 */
public class UtilAudioPlay {

    private static SoundPool     mSoundPool     = null;
    private static UtilAudioPlay mPlayAudioTask = new UtilAudioPlay();
    private static int           mSoundId       = -1;

    private UtilAudioPlay() {
    };

    /**
     * 这里对方法做描述
     * 
     * @param context 上下文
     * @param rawFile 音频文件
     * @since V1.0
     */
    public synchronized static void playAudioFile(Context context, int rawFile) {

        if (null == mSoundPool) {
            mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);
            mSoundId = mSoundPool.load(context, rawFile, 1);

            new Handler().postDelayed(mPlayAudioTask.new PlayAudioTask1(), 100);
        } else {
            if (-1 != mSoundId)
                mPlayAudioTask.new PlayAudioTask().execute(mSoundId);
        }
    }

    private class PlayAudioTask1 implements Runnable {
        @Override
        public void run() {
            mPlayAudioTask.new PlayAudioTask().execute(mSoundId);
        }
    }

    private class PlayAudioTask extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected Void doInBackground(Integer... soundId) {
            mSoundPool.play(soundId[0], 1, 1, 1, 0, 1);
            return null;
        }
    }
}
