package com.example.andsds;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {
	private List<Music> musicsList;
	private ListView lvMusic;
	private ImageButton ibPlayOrPause;
	private ImageButton ibNext;
	private ImageButton ibPrevious;
	private MediaPlayer player;
	private int currentMusicIndex;
	private MusicAdapter adapter;
	private int pausePosition;
	private TextView tvMusicTitle;
	
	private TextView tvMusicCurrentPosition;
	private TextView tvMusicDuration;
	private SeekBar sbProgress;
	private boolean isRunning;
	private UpdataProgressThread t;
	
	//播放器是否播放
	private boolean isPlayerWork;
 
	private Button btnSingleCycle;
	private Button btnAllCycle;
	private Button btnRandom;
	
	
	
	
	public void setRunning(boolean isRunning){
		this.isRunning=isRunning;
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 初始化控件
		setView();

		// 获取歌曲数据
		getMuiscData();

		// 设置监听器
		setListeners();

		// 初始化播放工具
		initPlay();
		
	
		
		
		
		adapter = new MusicAdapter(MainActivity.this, musicsList);
		Log.d("edu", "kjhhd");
		lvMusic.setAdapter(adapter);
	}

	private void initPlay() {
		player = new MediaPlayer();
		player.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				next();
			}
		});
		
	}

	private void setListeners() {
		OnClickListener listeners = new InnerOnClickListener();
		ibPlayOrPause.setOnClickListener(listeners);
		ibNext.setOnClickListener(listeners);
		ibPrevious.setOnClickListener(listeners);
		btnSingleCycle.setOnClickListener(listeners);
		btnAllCycle.setOnClickListener(listeners);
		btnRandom.setOnClickListener(listeners);

		OnItemClickListener listener = new InnerOnItemClickListener();
		lvMusic.setOnItemClickListener(listener);
		
		OnSeekBarChangeListener sbListener = new InnerOnSeekBarChangeListener();
		sbProgress.setOnSeekBarChangeListener(sbListener );

	}
	private boolean isTrackingTouch;
	private class InnerOnSeekBarChangeListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// 进度正在改变时调用
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// 开始拖拽时调用
			isTrackingTouch=true;
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// 停止拖拽时调用
			
			//播放器开始工作时。可以拖拽
			if(isPlayerWork){
				int duration = player.getDuration();
				int percent = sbProgress.getProgress();
				pausePosition = duration*percent/100;
				play();
			}
			
			isTrackingTouch=false;
			
		}
		
	}

	private class InnerOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			currentMusicIndex = position;
			play();
		}

	}

	private class InnerOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ib_music_play_or_pause:
				if (player.isPlaying()) {
					pause();
				}else{
					
					play();
				}
				break;
			case R.id.ib_music_next:
				next();
				break;
				
			case R.id.ib_music_previous:
				previous();
				break;
				
			case R.id.btn_single_cycle:
				singleCycle();
				break;
			case R.id.btn_all_cycle:
				allCycle();
				break;
			case R.id.btn_random:
				Random();
				break;
			}
		}

		
	
	}

	private void setView() {
		lvMusic = (ListView) findViewById(R.id.lv_music);
		ibPlayOrPause = (ImageButton) findViewById(R.id.ib_music_play_or_pause);
		ibNext = (ImageButton) findViewById(R.id.ib_music_next);
		ibPrevious = (ImageButton) findViewById(R.id.ib_music_previous);
		ibPrevious = (ImageButton) findViewById(R.id.ib_music_previous);
		ibPrevious = (ImageButton) findViewById(R.id.ib_music_previous);
		tvMusicTitle=(TextView) findViewById(R.id.tv_music_title);
		tvMusicCurrentPosition=(TextView) findViewById(R.id.tv_music_current_position);
		tvMusicDuration=(TextView) findViewById(R.id.tv_music_duration);
		sbProgress=(SeekBar) findViewById(R.id.sb_progress);
		
		btnSingleCycle=(Button) findViewById(R.id.btn_single_cycle);
		btnAllCycle=(Button) findViewById(R.id.btn_all_cycle);
		btnRandom=(Button) findViewById(R.id.btn_random);
		
	}

	private void getMuiscData() {
		musicsList = new ArrayList<Music>();
		String s = Environment.getExternalStorageState();
		if (s.equals(Environment.MEDIA_MOUNTED)) {
			File musicDir = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
			if (musicDir.exists()) {
				File[] files = musicDir.listFiles();

				if (files.length != 0) {
					for (int i = 0; i < files.length; i++) {
						File file = files[i];
						if (file.isFile()) {
							String fileName = file.getName();
							if (fileName.toLowerCase(Locale.CHINA).endsWith(
									".mp3")) {
								Music music = new Music();
								music.setTitle(fileName.substring(0,
										fileName.length() - 4));
								music.setPath(file.getAbsolutePath());
								musicsList.add(music);
							}
						}
					}
				}
			}

		}
	}

	private class UpdataProgressThread extends Thread {
		
		private int percent;
		private int currentPosition;
		@Override
		public void run() {
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					//更新进度条
					if(!isTrackingTouch){
						sbProgress.setProgress(percent);
					}
					//更新播放时间
					tvMusicCurrentPosition.setText(DateFomartedTimer(currentPosition));
				}
			};
			while(isRunning){
				currentPosition = player.getCurrentPosition();
				int duration= player.getDuration();
				percent = duration == 0? 0:currentPosition*100/duration;
				runOnUiThread(runnable);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void pause(){
		player.pause();
		pausePosition = player.getCurrentPosition();
		ibPlayOrPause
				.setImageResource(android.R.drawable.ic_media_play);
		if(t != null){
			setRunning(false);
			t=null;
		}
	}
	
	public void play() {

		try {
			player.reset();
			player.setDataSource(musicsList.get(currentMusicIndex).getPath());
			player.prepare();
			player.seekTo(pausePosition);
			player.start();
			ibPlayOrPause.setImageResource(android.R.drawable.ic_media_pause);
			tvMusicTitle.setText("正在播放："+musicsList.get(currentMusicIndex).getTitle());
			
			//开始播放状态
			isPlayerWork=true;
			
			//显示总时长
			tvMusicDuration.setText(DateFomartedTimer(player.getDuration()));
			
			//开启线程
			if(t==null){
				t=new UpdataProgressThread();
				setRunning(true);
				t.start();
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void mySetEnable(Button btn){
		btnSingleCycle.setEnabled(true);
		btnAllCycle.setEnabled(true);
		btnRandom.setEnabled(true);
		btn.setEnabled(false);
	}
	
	private void singleCycle() {
		mySetEnable(btnSingleCycle);
		
	}

	private void allCycle() {
		mySetEnable(btnAllCycle);
		
	}

	private void Random() {
		mySetEnable(btnRandom);
	}

	private void next() {
		if(!btnSingleCycle.isEnabled()){
			pausePosition=0;
			play();
		}else if(!btnAllCycle.isEnabled()){
			currentMusicIndex++;
			if (currentMusicIndex >= musicsList.size()) {
				currentMusicIndex = 0;
			}
			pausePosition = 0;
			play();
		}else if(!btnRandom.isEnabled()){
			currentMusicIndex= new Random().nextInt(musicsList.size());
			pausePosition=0;
			play();
		}
	}

	private void previous() {
		if(!btnSingleCycle.isEnabled()){
			pausePosition=0;
			play();
		}else if(!btnAllCycle.isEnabled()){
			currentMusicIndex--;
			if (currentMusicIndex < 0) {
				currentMusicIndex = musicsList.size() - 1;
			}
			pausePosition = 0;
			play();
		}else if(!btnRandom.isEnabled()){
			currentMusicIndex= new Random().nextInt(musicsList.size());
		pausePosition=0;
			play();
		}
		
		
	}
	
	private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss",Locale.CHINA);
	private Date date=new Date();
	
	private String DateFomartedTimer(int timeMillis){
		date.setTime(timeMillis);
		String s=sdf.format(date);
		return s;
	}

}
