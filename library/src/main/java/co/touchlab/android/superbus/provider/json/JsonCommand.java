package co.touchlab.android.superbus.provider.json;

import co.touchlab.android.superbus.provider.file.StoredCommand;
import org.json.JSONObject;

/**
 * Raw json command storage.  Gson will be simpler, but if you want full control, use this.
 *
 * Only compatible with JsonPersistenceProvider.
 *
 * User: kgalligan
 * Date: 10/11/12
 * Time: 3:35 AM
 */
public abstract class JsonCommand extends StoredCommand
{
    public abstract void inflate(JSONObject json);

    public abstract void store(JSONObject json);
}
