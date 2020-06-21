package com.fumi.mybroadcast.statemachine;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fumi.mybroadcast.client.MediaBrowserHelper;
import com.fumi.mybroadcast.service.contentcatalogs.MusicLibrary;
import com.fumi.mybroadcast.statemachine.Action.FSMAction;
import com.fumi.mybroadcast.statemachine.FSM.FSM;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

public class FSMTask extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = FSMTask.class.getSimpleName();

    Context mContext;
    MediaBrowserHelper mMediaBrowserHelper;

    public FSMTask(Context context, MediaBrowserHelper mediaBrowserHelper){
        mContext = context;
        mMediaBrowserHelper = mediaBrowserHelper;
    }

    @Override
    protected Integer doInBackground(String... args) {
        String fsmConfigFileName = args[0];

        startFSM(fsmConfigFileName);

        return 1;
    }

    public void startFSM(String fsmConfigFileName) {
        try {
            InputStream inputStream = mContext.getAssets().open(fsmConfigFileName);
            FSM f = new FSM(inputStream, new FSMAction() {
                @Override
                public boolean action(String curState, String message, String nextState, Object args) {
                    /*
                     * Here we can implement our login of how we wish to handle
                     * an action
                     */
                    StringBuffer sb = new StringBuffer();
                    sb.append("FSMAction: ");
                    sb.append(curState).append(",").append(message).append(",").append(nextState).append(args!=null?args.toString() : "");
                    Log.d(TAG, sb.toString());

                    final MediaPlayer mediaPlayer = new MediaPlayer();

                    //mMediaBrowserHelper.getTransportControls().playFromMediaId(message, null);
                    //String url = "https://firebasestorage.googleapis.com/v0/b/mybroadcast-280201.appspot.com/o/Jazz_In_Paris%2Fjazz_in_paris.mp3?alt=media&token=230bd312-ab33-4689-8e7c-f0c02f738741";
                    StorageReference ref = MusicLibrary.getMusicStorageRef(message);
                    ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri  uri = task.getResult();
                            try {
                                playMusic(uri.toString(), mediaPlayer);
                            } catch (IOException ex) {
                                Log.d(TAG, ex.getMessage(), ex);
                            }
                        }
                    });

                    return true;
                }
            });

            Log.d(TAG, f.getCurrentState());
            f.ProcessFSM("出発");
            Log.d(TAG, f.getCurrentState());

            Thread.sleep(30000);
            f.ProcessFSM("北綾瀬駅");
            Log.d(TAG, f.getCurrentState());

            Thread.sleep(30000);
            f.ProcessFSM("代々木公園駅");
            Log.d(TAG, f.getCurrentState());

            Thread.sleep(30000);
            f.ProcessFSM("NHKニュースセンター");
            Log.d(TAG, f.getCurrentState());
        } catch (ParserConfigurationException ex) {
            Log.d(TAG, ex.getMessage(), ex);
        } catch (SAXException ex) {
            Log.d(TAG, ex.getMessage(), ex);
        } catch (IOException ex) {
            Log.d(TAG, ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            Log.d(TAG, ex.getMessage(), ex);
        }
    }

    private void playMusic(String url, MediaPlayer mediaPlayer) throws IOException {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }
}
