/*
 * @(#)MyStockAdapter.java	 2012-5-4
 *
 * Copyright 2004-2012 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.wxxr.callhelper.qg.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.wxxr.callhelper.qg.R;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeListViewAdapter extends BaseAdapter
{

	ArrayList<File> gameSeasons;

	private LayoutInflater inflater;
	Context context;
	String n_style;
	Cursor cursor;
	ArrayList<MediaPlayer> plays;
	TextView tv_telnumber;
	TextView tv_time;
	TextView tv_duration;
	RelativeLayout play_item;

	public HomeListViewAdapter(Context context, ArrayList<File> gameSeasons, Cursor cursor, ArrayList<MediaPlayer> plays)
	{
		super();
		inflater = LayoutInflater.from(context);
		this.gameSeasons = gameSeasons;
		this.context = context;
		this.cursor = cursor;
		this.plays = plays;
	}

	public HomeListViewAdapter(Context context)
	{

		super();
		inflater = LayoutInflater.from(context);

	}

	public void addMoreDealDetails(ArrayList<File> games)
	{
		for (File game : games)
		{
			this.gameSeasons.add(game);
		}
	}

	public ArrayList<File> getList()
	{

		return this.gameSeasons;
	}

	@Override
	public int getCount()
	{
		if (gameSeasons == null)
		{
			return 0;
		}
		else
		{
			return gameSeasons.size();
		}
		// return 10;

	}

	@Override
	public Object getItem(int position)
	{
		// FIXME getItem
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		// FIXME getItemId
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = null;

		final int num = position;

		if (convertView == null)
		{

			view = inflater.inflate(R.layout.dajia_doumaisha_item, null);

		}
		else
		{
			view = convertView;

		}

		tv_telnumber = (TextView) view.findViewById(R.id.tv_number);
		tv_time = (TextView) view.findViewById(R.id.tv_time);
		tv_duration = (TextView) view.findViewById(R.id.tv_duration);
		RelativeLayout play_item = (RelativeLayout) view.findViewById(R.id.play_item);
		final ImageView iv_item = (ImageView) view.findViewById(R.id.iv_playview);

		File f = gameSeasons.get(position);

		String str = f.getName();
		int lastIndex = str.lastIndexOf(".");

		String exname = str.substring(0, lastIndex);
		String[] split = exname.split("_");

		tv_telnumber.setText(split[0]);

		tv_time.setText(split[1] + " " + split[2]);

		cursor.moveToPosition(position);

		tv_duration.setText(toTime(cursor.getInt(1)));

		play_item.setOnClickListener(new OnClickListener()
		{

			boolean flag = true;

			@Override
			public void onClick(View v)
			{

				final MediaPlayer mediaPlayer = plays.get(num);

				try
				{

					if (flag)
					{
						mediaPlayer.start();
						flag = false;
						iv_item.setBackgroundResource(R.drawable.play);
					}
					else
					{
						mediaPlayer.pause();
						flag = true;
						iv_item.setBackgroundResource(R.drawable.pause);
					}

				}

				catch (Exception e)
				{
					mediaPlayer.stop();
					mediaPlayer.release();
					e.printStackTrace();

				}

				mediaPlayer.setOnCompletionListener(new OnCompletionListener()
				{

					@Override
					public void onCompletion(MediaPlayer mp)
					{
						mp.pause();
						mp.seekTo(0);
						flag = true;
						iv_item.setBackgroundResource(R.drawable.pause);
					}
				});

			}
		});

		return view;

	}

	// static class ViewHolder
	// {
	// TextView tv_telnumber;
	// TextView tv_time;
	// TextView tv_duration;
	//
	// }

	public String toTime(int time)
	{

		time /= 1000;
		int minute = time / 60;
		int hour = minute / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

}
