package dk.livingcode.android.gamemaster.utility;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import org.apache.http.conn.ConnectTimeoutException;

public class AsyncServiceTask<Parameters, Progress, Result> extends AsyncTask<Parameters, Progress, Result> {
	public static final String Tag = AsyncServiceTask.class.getSimpleName();
	private int connectionStatus = -1;
	
	public AsyncServiceTask() {
		connectionStatus = AsyncServiceResult.SUCCESS;
	}
	
	protected final void setConnectionStatus(final int status) {
		this.connectionStatus = status;
	}

	protected final void setConnectionStatus(final Exception exception) {
		Log.e(Tag, "Error occurred during an asynchronous service request.", exception);
		
		if (exception instanceof UnknownHostException) {
			setConnectionStatus(AsyncServiceResult.NETWORK_ERROR);
		} else if (exception instanceof ConnectTimeoutException) {
			setConnectionStatus(AsyncServiceResult.CONNECTION_TIMEOUT);
		} else if (exception instanceof SocketTimeoutException) {
			setConnectionStatus(AsyncServiceResult.SOCKET_TIMEOUT);
		} else {
			setConnectionStatus(AsyncServiceResult.UNKNOWN_ERROR);
		}
	}
	
	protected final int getConnectionStatus() {
		if (this.connectionStatus != -1) {
			return this.connectionStatus;
		}

		return AsyncServiceResult.UNKNOWN_ERROR;
	}
	
	@Override
	protected Result doInBackground(Parameters... arg0) {
		return null;
	}

	protected void handleStatus(final Context owner) {
		final int status = getConnectionStatus();
		switch (status) {
		case AsyncServiceResult.CONNECTION_TIMEOUT:
		case AsyncServiceResult.SOCKET_TIMEOUT:
			Toast.makeText(owner, "Forbindelsen til serveren timede ud. Prøv at gemme igen, eller kontrollér netværksforbindelse.", Toast.LENGTH_LONG).show();
			break;
		case AsyncServiceResult.NETWORK_ERROR:
			Toast.makeText(owner, "Der kunne ikke oprettes forbindelse til serveren. Prøv at gemme igen, eller kontrollér netværksforbindelse.", Toast.LENGTH_LONG).show();
			break;
		case AsyncServiceResult.UNKNOWN_ERROR:
			Toast.makeText(owner, "Der opstod en ukendt fejl. Prøv at gemme igen, eller kontrollér netværksforbindelse og log ind oplysninger.", Toast.LENGTH_LONG).show();
			break;
		case AsyncServiceResult.UNKNOWN_STATUS:
			Toast.makeText(owner, "Der opstod en ukendt fejl.", Toast.LENGTH_LONG).show();
			break;
		}
	}
}