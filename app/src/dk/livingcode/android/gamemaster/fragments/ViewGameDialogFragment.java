package dk.livingcode.android.gamemaster.fragments;

import dk.livingcode.android.gamemaster.R;
import dk.livingcode.android.gamemaster.database.GamesDataSource;
import dk.livingcode.android.gamemaster.model.Game;
import dk.livingcode.android.gamemaster.model.Release;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ViewGameDialogFragment extends DialogFragment {
	private Game selectedGame;
	private boolean isInCollection = false;
	
	public ViewGameDialogFragment() {
		// Empty constructor required for DialogFragment
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setUserVisibleHint(true);
		
		final Bundle extras = getArguments();
		if (extras != null) {
			final int selectedGameId = extras.getInt("SelectedGameId");
			isInCollection = extras.getBoolean("IsInCollection");
			
			GamesDataSource database = new GamesDataSource(getActivity());
			database.open();
			
			selectedGame = database.getGame(selectedGameId);
			
			database.close();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_game_list_view_game, container);
		
		getDialog().setTitle(selectedGame.getDefaultRelease().getTitle());
		
		final TextView txtDescription = (TextView) view.findViewById(R.id.dialog_game_list_view_game_description);
		if (txtDescription != null) {
			txtDescription.setText(selectedGame.getDescription());
		}
		
		final Release r = selectedGame.getDefaultRelease();
		
		final TextView txtRelease = (TextView) view.findViewById(R.id.dialog_game_list_view_game_releasedate);
		if (txtRelease != null) {
			int flags = 0;
			flags |= android.text.format.DateUtils.FORMAT_NO_MONTH_DAY;
			flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
            flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;
            
			final String releaseDate =  DateUtils.formatDateTime(getActivity(), r.getReleased().getTime(), flags);
			txtRelease.setText("Released: " + releaseDate);
		}
		
		final TextView txtGenre = (TextView) view.findViewById(R.id.dialog_game_list_view_game_genre);
		if (txtGenre != null) {
			txtGenre.setText("Genre: " + selectedGame.getGenre() + ", " + selectedGame.getSubGenre());
		}
		
		final TextView txtPlatform = (TextView) view.findViewById(R.id.dialog_game_list_view_game_platform);
		if (txtPlatform != null) {
			txtPlatform.setText("Platform: " + selectedGame.getConsole().getName());
		}
		
		final TextView txtRegion = (TextView) view.findViewById(R.id.dialog_game_list_view_game_region);
		if (txtRegion != null) {
			txtRegion.setText("Region: " + r.getRegion().getName());
		}
		
		final Button btnCollection = (Button) view.findViewById(R.id.dialog_game_list_view_game_btnCollection);
		if (btnCollection != null) {
			if (isInCollection) {
				btnCollection.setText("Remove");
			} else {
				btnCollection.setText("Add");
			}
			
			btnCollection.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ViewGameDialogListener activity = (ViewGameDialogListener) getActivity();
					
					if (isInCollection) {
						activity.onFinishViewing(selectedGame.getId(), "Remove");
					} else {
						activity.onFinishViewing(selectedGame.getId(), "Add");
					}
					
					dismiss();
				}
			});
		}

		final Button btnClose = (Button) view.findViewById(R.id.dialog_game_list_view_game_btnClose);
		if (btnClose != null) {
			btnClose.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}
		
		final Button btnDetails = (Button) view.findViewById(R.id.dialog_game_list_view_game_btnDetails);
		if (btnDetails != null) {
			btnDetails.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ViewGameDialogListener activity = (ViewGameDialogListener) getActivity();
					activity.onViewDetails(selectedGame.getId());
					
					dismiss();
				}
			});
		}
		
		return view;
	}
}