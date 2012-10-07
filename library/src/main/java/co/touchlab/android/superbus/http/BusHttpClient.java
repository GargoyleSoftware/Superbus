package co.touchlab.android.superbus.http;

import android.os.Build;
import co.touchlab.android.superbus.PermanentException;
import co.touchlab.android.superbus.TransientException;
import com.turbomanage.httpclient.*;

/**
 * Use this http client wrapper to make server calls.  It should understand the difference between transient and permanent exception
 * User: kgalligan
 * Date: 10/6/12
 * Time: 11:53 PM
 * To change this template use File | Settings | File Templates.
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
            /*if (logger.isLoggingEnabled()) {
                logger.log("BasicRequestHandler.onError got");
                e.printStackTrace();
            }*/
            if (res != null)
            {
                int status = res.getStatus();

                if (status >= 400) {
                    // Perhaps a 404, 501, or something that will be fixed later
                    throw new PermanentException(cause);
                }
                else
                {
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
