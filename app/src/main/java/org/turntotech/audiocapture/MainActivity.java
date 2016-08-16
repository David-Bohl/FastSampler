// Mostly a combination of the AudioCapture and Receiving Data from other Apps Modules

package org.turntotech.audiocapture;

import java.io.IOException;

import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.media.SoundPool;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import com.twobard.pianoview.*;
import com.twobard.pianoview.Piano.PianoKeyListener;

import java.io.IOException;


public class MainActivity extends Activity {


	private MediaRecorder myAudioRecorder;
	private String outputFile = null;
	private Button start, stop, reset;
	private int globalSoundID;
	private boolean soundRecorded = false;

	SoundPoolPlayer sound;

	private static final String DEBUG_TAG = "PianoView";

	public String getPath(Uri uri)
	{
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
		return cursor.getString(idx);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LinearLayout main = (LinearLayout)findViewById(R.id.main_content);
		Piano piano;
		piano = new Piano(this);
		piano.setPianoKeyListener(onPianoKeyPress);
		main.addView(piano);

		start = (Button) findViewById(R.id.button1);
		stop = (Button) findViewById(R.id.button2);
		reset = (Button) findViewById(R.id.resetButton);

		sound = new SoundPoolPlayer(this);

		stop.setEnabled(false);
		reset.setEnabled(false);

		outputFile = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/myrecording.3gp";

		// 1. Create a MediaRecorder object
		myAudioRecorder = new MediaRecorder();
		
		/*set the source , output and encoding format and output file.*/
		
		//2. This method specifies the source of audio to be recorded
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		
		//3. This method specifies the audio format in which audio to be stored
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		
		//4. This method specifies the audio encoder to be used
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

		//5. Choose the file to save audio
		myAudioRecorder.setOutputFile(outputFile);

		//get the received intent
		Intent receivedIntent = getIntent();
		//get the action
		String receivedAction = receivedIntent.getAction();
		//find out what we are dealing with
		//String receivedType = receivedIntent.getType();

		if(receivedAction.equals(Intent.ACTION_SEND)) {

			Uri receivedUri = (Uri)receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);

			if (receivedUri != null) {

				outputFile = getPath(receivedUri);

				soundRecorded = true;
				start.setEnabled(false);
				reset.setEnabled(true);

				sound.unload(globalSoundID);
				globalSoundID = sound.loadShortResource(outputFile);
			}

		}




	}//end on create

//	@Override
//	public void onStop() {
//		finish();
//		super.onStop();
//
//	}




	private PianoKeyListener onPianoKeyPress=
			new PianoKeyListener() {

				@Override
				public void keyPressed(int id, int action) {
					Log.i(DEBUG_TAG,"Key pressed: " + id);
					Log.i("action", Integer.toString(action) );

					if(action == 0){

						float pitch = (float) Math.pow( Math.pow(2.0, 1/12.0), (float) id-12 );
						if(soundRecorded){
							sound.playShortResource(globalSoundID, pitch);
						}
						else{
							sound.playPresetResource(R.raw.piano_c, pitch);
						}

					}
				}
			};




	//6. Methods in the mediaRecorder allow you to to start and stop recording
	public void start(View view) {
		Log.i("start button", "pressed");
		try {
			/*Two basic methods perpare and start to start recording the audio.*/
			myAudioRecorder.prepare();
			Log.i("recorder", "prepared");
			myAudioRecorder.start();
			Log.i("recorder", "started");
		} catch (Exception e) {
			e.printStackTrace();
		}
		start.setEnabled(false);
		stop.setEnabled(true);
		Toast.makeText(getApplicationContext(), "Recording started",
				Toast.LENGTH_SHORT).show();

	}

	public void stop(View view) {
		/*This method stops the recording process.*/
		myAudioRecorder.stop();
		
		/*This method should be called when the recorder instance is needed.*/
		stop.setEnabled(false);
		reset.setEnabled(true);
		soundRecorded = true;
		Toast.makeText(getApplicationContext(), "Audio recorded successfully",
				Toast.LENGTH_SHORT).show();


		sound.unload(globalSoundID);
		globalSoundID = sound.loadShortResource(outputFile);
		///////////////////////////////////////////////////
	}

	//7. After the recording is done, we create a MediaPlayer object which gives us methods to play the audio.

	public void reset(View view){

		myAudioRecorder.reset();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

		//4. This method specifies the audio encoder to be used
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

		outputFile = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/myrecording.3gp";

		//5. Choose the file to save audio
		myAudioRecorder.setOutputFile(outputFile);

		start.setEnabled(true);
		reset.setEnabled(false);
		soundRecorded = false;


	}

}