package layr.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ListenableCall<T> implements Runnable {

	Callable<T> callable;
	List<Listener<T>> onSuccessListeners;
	List<Listener<Exception>> onFailListeners;
	
	public ListenableCall( Callable<T> callable ) {
		this.callable = callable;
		this.onSuccessListeners = new ArrayList<Listener<T>>();
		this.onFailListeners = new ArrayList<Listener<Exception>>();
	}
	
	public static <T> ListenableCall<T> listenable(Callable<T> callable) {
		return new ListenableCall<T>( callable );
	}

	public void run() {
		try {
			T result = callable.call();
			onSuccess(result);
		} catch (Exception e) {
			onFail(e);
		}
	}

	private void onSuccess(T result) {
		for ( Listener<T> listener : onSuccessListeners )
			listener.listen(result);
	}

	public void onSuccess( Listener<T> onSuccess ){
		if ( onSuccess != null )
			this.onSuccessListeners.add(onSuccess);
	}

	private void onFail(Exception e) {
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

