package com.anantdevelopers.swipesinalpha.OptionsMenuResources.SettingsActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anantdevelopers.swipesinalpha.R;

import java.util.List;

public class customListAdapter extends ArrayAdapter<ListItem> {

     private Context context;
     private int resource;

     public customListAdapter(@NonNull Context context, int resource, @NonNull List<ListItem> objects) {
          super(context, resource, objects);
          this.context = context;
          this.resource = resource;
     }

     @NonNull
     @Override
     public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

          ListItem currentItem = getItem(position);

          String name = currentItem.getName();
          String description = currentItem.getDescription();
          int image = currentItem.getImage();

          LayoutInflater inflater = LayoutInflater.from(context);
          convertView = inflater.inflate(resource, parent, false);

          TextView nameTextView = convertView.findViewById(R.id.name_txt);
          TextView descriptionTextView = convertView.findViewById(R.id.description_txt);
          ImageView imageView = convertView.findViewById(R.id.img);

          nameTextView.setText(name);
          descriptionTextView.setText(description);
          imageView.setImageResource(image);

          return convertView;

     }
}
