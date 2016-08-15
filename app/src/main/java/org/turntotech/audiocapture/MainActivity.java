/*
 * This is a sample application where we can record audio and play audio.
 * Android has a built in microphone through which you can capture and store audio.
 * Android provides the MediaRecorder class to record audio.
 * */

package org.turntotech.audiocapture;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
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


public class MainActivity extends Activity {

	private MediaRecorder myAudioRecorder;
	private String outputFile = null;
	private Button start, stop, play, playHi, reset;
	private int globalSoundID;

	SoundPoolPlayer sound;

	private static final String DEBUG_TAG = "PianoView";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		Programmatic setup

		LinearLayout main = (LinearLayout)findViewById(R.id.main_content);
		Piano piano;
		piano = new Piano(this);
		piano.setPianoKeyListener(onPianoKeyPress);
		main.addView(piano);



		start = (Button) findViewById(R.id.button1);
		stop = (Button) findViewById(R.id.button2);
		play = (Button) findViewById(R.id.button3);
		playHi = (Button) findViewById(R.id.playHiButton);
		reset = (Button) findViewById(R.id.resetButton);

		sound = new SoundPoolPlayer(this);

		play.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN ) {

					//sound.playPresetResource(R.raw.metronome, 1);
					sound.playShortResource(globalSoundID, 1);


					return true;
				}
				return false;
			}
		});

		playHi.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN ) {

					//sound.playPresetResource(R.raw.metronome, 2);
					sound.playShortResource(globalSoundID, 2);


					return true;
				}
				return false;
			}
		});






		stop.setEnabled(false);
		reset.setEnabled(false);
		play.setEnabled(false);
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

	}

	private PianoKeyListener onPianoKeyPress=
			new PianoKeyListener() {

				@Override
				public void keyPressed(int id, int action) {
					Log.i(DEBUG_TAG,"Key pressed: " + id);
					Log.i("action", Integer.toString(action) );

					if(action == 0){
						sound.playPresetResource(R.raw.piano_c, 1);
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
				Toast.LENGTH_LONG).show();

	}

	public void stop(View view) {
		/*This method stops the recording process.*/
		myAudioRecorder.stop();
		
		/*This method should be called when the recorder instance is needed.*/
		//myAudioRecorder.release();
		//myAudioRecorder = null;
		stop.setEnabled(false);
		play.setEnabled(true);
		reset.setEnabled(true);
		Toast.makeText(getApplicationContext(), "Audio recorded successfully",
				Toast.LENGTH_LONG).show();


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

		//5. Choose the file to save audio
		myAudioRecorder.setOutputFile(outputFile);


		start.setEnabled(true);
		play.setEnabled(false);
		reset.setEnabled(false);

	}




	public void play(View view) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {

//		MediaPlayer m = new MediaPlayer();
//		m.setDataSource(outputFile);
//		m.prepare();
//		m.start();

		//sound.playShortResource(R.raw.metronome, 1);
		//this line plays preloaded sound

		//sound.playShortResource(Integer.parseInt(outputFile), 1);




		Toast.makeText(getApplicationContext(), "Playing audio",
				Toast.LENGTH_LONG).show();

	}

}