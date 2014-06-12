package dk.livingcode.android.gamemaster.fragments;

public interface ViewGameDialogListener {
    void onFinishViewing(final int id, final String action);
    void onViewDetails(final int id);
}