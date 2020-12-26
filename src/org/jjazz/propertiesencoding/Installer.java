
package org.jjazz.propertiesencoding;

import java.util.logging.Logger;
import org.openide.modules.ModuleInstall;
import org.openide.windows.OnShowing;

/**
 * 
 * @author Administrateur
 */
@OnShowing
public class Installer extends ModuleInstall implements Runnable
{
    private static final Logger LOGGER = Logger.getLogger(Installer.class.getName());

    @Override
    public void run()
    {
        LOGGER.info("Installed");
    }
    
}