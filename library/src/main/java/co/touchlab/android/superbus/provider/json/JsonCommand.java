package co.touchlab.android.superbus.provider.json;

import co.touchlab.android.superbus.provider.file.StoredCommand;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 10/11/12
 * Time: 3:35 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class JSONCommand extends StoredCommand
{
    public abstract void inflate(JSONObject json);

    public abstract void store(JSONObject json);
}
