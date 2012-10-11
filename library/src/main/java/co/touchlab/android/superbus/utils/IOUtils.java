package co.touchlab.android.superbus.utils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/11/12
 * Time: 3:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class IOUtils
{
    public static String toString(Reader reader) throws IOException
    {
        char[] buf = new char[1024];
        StringBuilder sb = new StringBuilder();

        int read;
        while ((read = reader.read(buf)) > 0)
        {
            sb.append(buf, 0, read);
        }

        return sb.toString();
    }

    public static Reader toString(File commandFile)
    {
        return null;
    }
}
