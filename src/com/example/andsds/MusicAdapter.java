package com.example.andsds;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter{
	private List<Music> musicList;
	private Context context;
	
	public MusicAdapter(Context context, List<Music> musicList){
		super();
		this.context=context;
		this.musicList=musicList;
	}

	@Override
	public int getCount() {
		
		return musicList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Music music = musicList.get(position);
		
		if(convertView==null){
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.music_item, null);
			
		}
		
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
		TextView tvPath = (TextView) convertView.findViewById(R.id.tv_path);
		
		tvTitle.setText(music.getTitle());
		tvPath.setText(music.getPath());
		return convertView;
	}
	
	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
