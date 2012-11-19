package co.touchlab.android.superbus.http;

import android.os.Build;
import co.touchlab.android.superbus.PermanentException;
import co.touchlab.android.superbus.TransientException;
import com.turbomanage.httpclient.AbstractHttpClient;
import com.turbomanage.httpclient.BasicRequestHandler;
import com.turbomanage.httpclient.HttpRequestException;
import com.turbomanage.httpclient.HttpResponse;

/**
 * Use this http client wrapper to make server calls.  It should understand the difference between transient and permanent exceptions.
 *
 * For best practice network calls, you should generally use http://code.google.com/p/basic-http-client/
 *
 * It wraps http network calls.  Works nice.
 *
 * User: kgalligan
 * Date: 10/6/12
 * Time: 11:53 PM
 */
public class BusHttpClient extends AbstractHttpClient
{
    static
    {
        disableConnectionReuseIfNecessary();
    }

    public BusHttpClient(String baseUrl)
    {
        super(baseUrl, new BusRequestHandler());
    }

    private static class BusRequestHandler extends BasicRequestHandler
    {
        HttpRequestException requestException;

        @Override
        public boolean onError(HttpRequestException e)
        {
            boolean ret = super.onError(e);
            requestException = e;
            return ret;
        }
    }

    public void checkAndThrowError() throws PermanentException, TransientException
    {
        BusRequestHandler handler = (BusRequestHandler) requestHandler;
        HttpRequestException requestException = handler.requestException;
        if(requestException != null)
        {
            Throwable cause = requestException.getCause();
            if(cause instanceof SecurityException)
                throw new PermanentException(cause);

            HttpResponse res = requestException.getHttpResponse();

            if (res != null)
            {
                int status = res.getStatus();

                if (status >= 400) {
                    // Perhaps a 404, 501, or something that will be fixed later
                    throw new PermanentException(cause);
                }
                else
                {
                    //Not sure where this would happen...
                    throw new TransientException(cause);
                }
            }

            // Connection refused, host unreachable, etc.
            throw new PermanentException(cause);
        }
    }

    /**
     * There's a bug prior to Ginerbread with connection pooling.  Bummer.
     */
    private static void disableConnectionReuseIfNecessary()
    {
        // HTTP connection reuse which was buggy pre-froyo
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }
}
