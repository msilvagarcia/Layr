package layr.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ListenableCall implements Runnable {

	Callable<? extends Object> callable;
	List<Listener<Object>> onSuccessListeners;
	List<Listener<Exception>> onFailListeners;

	public ListenableCall( Callable<? extends Object> callable ) {
		this.callable = callable;
		this.onSuccessListeners = new ArrayList<Listener<Object>>();
		this.onFailListeners = new ArrayList<Listener<Exception>>();
	}
	
	public static ListenableCall listenable(Callable<? extends Object> callable) {
		return new ListenableCall( callable );
	}

	public void run() {
		try {
			Object result = callable.call();
			dispatchOnSuccessListeners(result);
		} catch (Exception e) {
			dispatchOnFailListeners(e);
		}
	}

	private void dispatchOnSuccessListeners(Object result) {
		for ( Listener<Object> listener : onSuccessListeners )
			listener.listen(result);
	}

	public void onSuccess( Listener<Object> onSuccess ){
		if ( onSuccess != null )
			this.onSuccessListeners.add(onSuccess);
	}

	private void dispatchOnFailListeners(Exception e) {
		if ( onFailListeners.size() > 0 )
			for ( Listener<Exception> listener : onFailListeners )
				listener.listen(e);
		else
			e.printStackTrace();
	}

	public void onFail( Listener<Exception> onFail ){
		if ( onFail != null )
			this.onFailListeners.add(onFail);
	}

}

