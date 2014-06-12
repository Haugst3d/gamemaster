package dk.livingcode.android.gamemaster.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dk.livingcode.android.gamemaster.R;
import dk.livingcode.android.gamemaster.database.GameCovers;
import dk.livingcode.android.gamemaster.model.Game;
import dk.livingcode.android.gamemaster.model.GameFilter;
import dk.livingcode.android.gamemaster.model.Release;
import dk.livingcode.android.gamemaster.utility.Strings;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameListActivityAdapter extends ArrayAdapter<Game> {
	private ArrayList<Game> items;
	private ArrayList<Game> filtered;
	private IGameSelectedListener selector;
	private ArrayList<Integer> selections;
	private GamePropertyFilter filter;
	private GameFilter currentFilterSettings;
	private String currentQuery = "title";

	@SuppressWarnings("unchecked")
	public GameListActivityAdapter(Context context, IGameSelectedListener selector, int textViewResourceId, ArrayList<Game> items) {
		super(context, textViewResourceId, items);

		this.selector = selector;
		this.items = (ArrayList<Game>)items.clone();
		this.filtered = this.items;
		this.filter = new GamePropertyFilter();
		this.selections = new ArrayList<Integer>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.game_list_activity_row, null);

			holder = new ViewHolder();
			holder.toplayout = (RelativeLayout)v.findViewById(R.id.game_list_activity_row_layout_front);
			holder.icon = (ImageView)v.findViewById(R.id.icon);
			holder.toptext = (TextView)v.findViewById(R.id.toptext);
			holder.middletext = (TextView)v.findViewById(R.id.middletext);
			holder.txtPublisher = (TextView)v.findViewById(R.id.txtPublisher);
			holder.bottomtext = (TextView)v.findViewById(R.id.bottomtext);
			holder.backname = (TextView)v.findViewById(R.id.game_list_activity_row_layout_back_name);
			holder.collect = (Button) v.findViewById(R.id.game_list_activity_row_btnCollect);
			holder.details = (Button) v.findViewById(R.id.game_list_activity_row_btnDetails);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		final Game g = this.getItem(position);
		if (g != null) {
			if (selections != null && selections.contains(g.getId())) {
				holder.toplayout.setBackgroundColor(0xFF333333);
			} else {
				holder.toplayout.setBackgroundColor(0xFFeeeeee);
			}

			Release r = null;
			if (currentFilterSettings.getRegion() == null) {
				r = g.getDefaultRelease();
			} else {
				r = g.getRelease(currentFilterSettings.getRegion().getId());
			}

			if (r == null)
				return v;

			int drawableID = GameCovers.getCoverResourceId(r.getCoverIdentifier());
			holder.icon.setImageResource(drawableID);
			holder.backname.setText(r.getTitle());
			holder.toptext.setText(r.getTitle());
			holder.middletext.setText("Developer: " + (g.getDeveloper() != null ? g.getDeveloper().getName() : " - "));

			if (g.getReleases().size() > 1) {
				holder.txtPublisher.setText("Publisher: - ");
			} else {
				holder.txtPublisher.setText("Publisher: " + (r.getPublisher() != null ? r.getPublisher().getName() : " - "));	
			}

			if (g.getReleases().size() > 1 && currentFilterSettings.getRegion() == null) {
				String regionCodes = Strings.Empty;
				for (Release rel : g.getReleases()) {
					regionCodes += rel.getRegion().getCode() + ", ";
				}

				if (regionCodes.endsWith(", ")) {
					regionCodes = regionCodes.substring(0, regionCodes.length() - 2);
				}

				holder.bottomtext.setText("Regions: " + regionCodes);	
			} else {
				int flags = 0;
				flags |= android.text.format.DateUtils.FORMAT_NO_MONTH_DAY;
				flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
				flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

				final String releaseDate =  DateUtils.formatDateTime(getContext(), r.getReleased().getTime(), flags);
				holder.bottomtext.setText("Region: " + r.getRegion().getCode() + ", Released: " + releaseDate);	
			}

			holder.collect.setTag(String.valueOf(g.getId()));
			holder.collect.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Get real game id from the button view
					int gameId = Integer.parseInt((String)v.getTag());
					Game selected = null;
					for (Game gi : items) {
						if (gi.getId() == gameId) {
							selected = gi;
							break;
						}
					}
					
					selector.onGameCollectedClicked(selected, filtered.indexOf(selected));
				}
			});

			holder.details.setTag(String.valueOf(g.getId()));
			holder.details.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Get real game id from the button view
					int gameId = Integer.parseInt((String)v.getTag());
					Game selected = null;
					for (Game gi : items) {
						if (gi.getId() == gameId) {
							selected = gi;
							break;
						}
					}
					
					selector.onGameDetailsClicked(selected, filtered.indexOf(selected));
				}
			});
		}

		return v;
	}

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new GamePropertyFilter();	
		}

		return filter;
	}

	public void setGameFilter(final GameFilter gf) {
		currentFilterSettings = gf;
	}

	public void setSelections(final ArrayList<Integer> selections) {
		this.selections = selections;
	}

	public void sortGamesList(final String fieldName) {
		currentQuery = fieldName;

		Collections.sort(filtered, new Comparator<Game>() {
			@Override
			public int compare(Game g1, Game g2) {
				Release r1 = null;
				if (currentFilterSettings.getRegion() == null) {
					r1 = g1.getDefaultRelease();
				} else {
					r1 = g1.getRelease(currentFilterSettings.getRegion().getId());
				}

				Release r2 = null;
				if (currentFilterSettings.getRegion() == null) {
					r2 = g2.getDefaultRelease();
				} else {
					r2 = g2.getRelease(currentFilterSettings.getRegion().getId());
				}

				if (fieldName.equals("title")) {
					return r1.getTitle().compareTo(r2.getTitle());	
				} else if (fieldName.equals("published")) {
					return r1.getReleased().compareTo(r2.getReleased());	
				} else if (fieldName.equals("publisher")) {
					return r1.getPublisher().getName().compareTo(r2.getPublisher().getName());	
				} else if (fieldName.equals("region")) {
					return r1.getRegion().getName().compareTo(r2.getRegion().getName());	
				} else if (fieldName.equals("genre")) {

				}

				return 0;
			}
		});

		clear();
		addAll(filtered);

		// Update the UI after sorting the list
		notifyDataSetChanged();
	}

	private class GamePropertyFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// NOTE: this function is *always* called from a background thread, and not the UI thread.
			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<Game> filt = new ArrayList<Game>();
				ArrayList<Game> lItems = new ArrayList<Game>();
				synchronized (this) {
					lItems.addAll(items);
				}

				for (int i = 0, l = lItems.size(); i < l; i++) {
					Game m = lItems.get(i);

					Release r = null;
					if (currentFilterSettings.getRegion() == null) {
						r = m.getDefaultRelease();
					} else {
						r = m.getRelease(currentFilterSettings.getRegion().getId());
					}

					if (r.getTitle().toLowerCase().contains(constraint)) {
						filt.add(m);
					}
				}

				result.count = filt.size();
				result.values = filt;
			} else {
				synchronized (this) {
					result.values = items;
					result.count = items.size();
				}
			}

			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			// NOTE: this function is *always* called from the UI thread.
			filtered = (ArrayList<Game>)results.values;

			sortGamesList(currentQuery);
		}
	}

	static class ViewHolder {
		ImageView icon;
		RelativeLayout toplayout;
		TextView toptext;
		TextView middletext;
		TextView txtPublisher;
		TextView bottomtext;
		TextView backname;
		Button collect;
		Button details;
	}
}
