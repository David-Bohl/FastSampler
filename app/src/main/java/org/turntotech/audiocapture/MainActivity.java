// Mostly a combination of the AudioCapture and Receiving Data from other Apps Modules

package org.turntotech.audiocapture;


import android.database.Cursor;

import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import android.widget.LinearLayout;
import com.twobard.pianoview.*;
import com.twobard.pianoview.Piano.PianoKeyListener;



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


		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(outputFile);


		Intent receivedIntent = getIntent();
		String receivedAction = receivedIntent.getAction();


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

	private PianoKeyListener onPianoKeyPress=
			new PianoKeyListener() {

				@Override
				public void keyPressed(int id, int action) {
					//Log.i(DEBUG_TAG,"Key pressed: " + id);
					//Log.i("ID: " + Integer.toString(id)," " + Integer.toString(action) );

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


	public void start(View view) {
		Log.i("start button", "pressed");
		try {

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

		myAudioRecorder.stop();

		stop.setEnabled(false);
		reset.setEnabled(true);
		soundRecorded = true;
		Toast.makeText(getApplicationContext(), "Sample recorded successfully",
				Toast.LENGTH_SHORT).show();

		sound.unload(globalSoundID);
		globalSoundID = sound.loadShortResource(outputFile);

	}


	public void reset(View view){

		myAudioRecorder.reset();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

		outputFile = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/myrecording.3gp";

		myAudioRecorder.setOutputFile(outputFile);

		start.setEnabled(true);
		reset.setEnabled(false);
		soundRecorded = false;

		Toast.makeText(getApplicationContext(), "Ready to record another sample",
				Toast.LENGTH_SHORT).show();

	}

}