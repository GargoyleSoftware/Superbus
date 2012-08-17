package co.touchlab.android.superbus.provider;

/**
 * User: William Sanville
 * Date: 8/16/12
 * Time: 2:27 PM
 * An interface which we expect the Application class to implement. This assumes the Application class is responsible
 * for maintaining a singleton or some kind of scheme for maintaining an instance of a PersistenceProvider.
 */
public interface PersistedApplication
{
    PersistenceProvider getProvider();
}
