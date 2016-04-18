package com.bestfare.pack.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestfare.pack.R;

public class CustomListAdapter extends ArrayAdapter<String> {

	private final Activity context;
	private final String[] itemname;
	private final Integer[] imgid;
	private String fromto[]=new String[]{"From: ","To: "};
	public CustomListAdapter(Activity context, String[] itemname, Integer[] imgid) {
		super(context, R.layout.list, itemname);
		// TODO Auto-generated constructor stub
		
		this.context=context;
		this.itemname=itemname;
		this.imgid=imgid;
	}
	
	public View getView(int position,View view,ViewGroup parent) {
		LayoutInflater inflater=context.getLayoutInflater();
		View rowView=inflater.inflate(R.layout.list, null, true);

		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.label1);

		TextView extratxt = (TextView) rowView.findViewById(R.id.label2);
		
		txtTitle.setText(fromto[position]);
		imageView.setImageResource(imgid[position]);
		extratxt.setText(itemname[position]);
		return rowView;
		
	};
}