package co.touchlab.android.superbus.utils;

import java.io.*;

/**
 * User: kgalligan
 * Date: 9/4/12
 * Time: 5:16 PM
 */
public class FileUtils
{
    public static String readFileAsString(File file) throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        StringWriter stringWriter = new StringWriter();
        String temp = null;

        while((temp = bufferedReader.readLine()) != null)
            stringWriter.append(temp);

        bufferedReader.close();

        return stringWriter.toString();
    }

    public static void writeStringAsFile(String data, File file) throws IOException
    {
        FileWriter fileWriter = new FileWriter(file);

        fileWriter.write(data);

        fileWriter.close();
    }
}
