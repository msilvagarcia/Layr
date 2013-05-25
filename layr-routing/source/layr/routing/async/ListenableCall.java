package layr.routing.async;

import java.util.concurrent.Callable;

public class ListenableCall<T> implements Runnable {

	Callable<T> callable;
	Listener<T> onSuccess;
	Listener<Exception> onFail;
	
	public ListenableCall( Callable<T> callable ) {
		this.callable = callable;
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
		if ( onSuccess != null )
			onSuccess.listen(result);
	}

	public void onSuccess( Listener<T> onSuccess ){
		this.onSuccess = onSuccess;
	}

	private void onFail(Exception e) {
		if ( onFail != null )
			onFail.listen(e);
		else
			e.printStackTrace();
	}

	public void onFail( Listener<Exception> onFail ){
		this.onFail = onFail;
	}

}

