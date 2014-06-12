package dk.livingcode.android.gamemaster.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import dk.livingcode.android.gamemaster.R;

public class CollectionGameViewFragment extends SherlockFragment {
	public CollectionGameViewFragment() {
	}

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
		final View view = inflater.inflate(R.layout.collection_game_editor_fragment, container, false);

		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);


	}

	@Override
	public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
		inflater.inflate(R.menu.collection_game_editor_menu, menu);
	}	

	@Override
	public void onPrepareOptionsMenu(Menu menu) {	
		final MenuItem cancelMenu = menu.findItem(R.id.collection_game_editor_menu_cancel);
		cancelMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				getActivity().finish();
				
				return false;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.collection_game_editor_menu_cancel:

			break;
		}

		return false;
	}
}