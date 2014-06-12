package dk.livingcode.android.gamemaster.adapters;

import java.util.ArrayList;

import dk.livingcode.android.gamemaster.R;
import dk.livingcode.android.gamemaster.model.Console;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ConsoleListActivityAdapter extends ArrayAdapter<Console> {
	private ArrayList<Console> items;
	
	public ConsoleListActivityAdapter(Context context, int textViewResourceId, ArrayList<Console> items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		// Use the ViewHolder pattern, to make the ui generation faster
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.console_list_activity_row, null);
		}

		holder = new ViewHolder();
		holder.layout = (RelativeLayout)convertView.findViewById(R.id.console_list_activity_row_layout);
		holder.icon = (ImageView)convertView.findViewById(R.id.icon);
		holder.toptext = (TextView)convertView.findViewById(R.id.toptext);
		holder.bottomtext = (TextView)convertView.findViewById(R.id.bottomtext);

		if (holder.layout != null) {
		/*	if (position % 2 != 0) {
				holder.layout.setBackgroundColor(0xFF000000);
			} else {
				holder.layout.setBackgroundColor(0xFF111111);
			}   */
		}

		Console o = items.get(position);
		if (o != null) {
			if (holder.icon != null) {
				//holder.icon.setBackgroundResource(R.drawable.projectentry_note_black);
			}

			if (holder.toptext != null) {
				holder.toptext.setText(o.getName());
			}

			if (holder.bottomtext != null) {
				holder.bottomtext.setText("Type: " + o.getType());
			}
		}

		return convertView;
	}

	static class ViewHolder {
		RelativeLayout layout;
		ImageView icon;
		TextView toptext;
		TextView bottomtext;
	}
}