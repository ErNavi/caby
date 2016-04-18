package com.bestfare.pack;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

class PopupAdapter implements GoogleMap.InfoWindowAdapter {
  LayoutInflater inflater=null;

  PopupAdapter(LayoutInflater inflater) {
    this.inflater=inflater;
  }

  @Override
  public View getInfoWindow(Marker marker) {
    View popup=inflater.inflate(R.layout.markeroption_layout, null);
    TextView tv=(TextView)popup.findViewById(R.id.title);

    tv.setText(marker.getTitle());
    tv=(TextView)popup.findViewById(R.id.snippet);
    tv.setText(marker.getSnippet());

    return(popup);
  }

  @Override
  public View getInfoContents(Marker marker) {

    View popup=inflater.inflate(R.layout.markeroption_layout, null);
    TextView tv=(TextView)popup.findViewById(R.id.title);

    tv.setText(marker.getTitle());
    tv=(TextView)popup.findViewById(R.id.snippet);
    tv.setText(marker.getSnippet());

    return(null);
  }
}