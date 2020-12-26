package org.jjazz.propertiesencoding;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Logger;
import org.netbeans.api.progress.BaseProgressUtils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.*;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.util.NbBundle.Messages;

/**
 * A plugin that adds an action (Edit menu) to force the use of project encoding
 * for all Bundle*.properties of the selected project.
 * <p>
 * Because by default .properties are loaded/saved as ISO, not UTF8 as for the
 * rest of the project. This causes problems when reusing translation platforms
 * which exports files in UTF8. The setting can be manually changed in Netbeans
 * file by file in the Properties, but it's very tedious, hence this plugin.
 *
 * @todo Add a way to set the attribute when a new
 */
@ActionID(
        category = "Edit",
        id = "org.jjazz.propertiesencoding.SetPropertiesFilesEncoding"
)
@ActionRegistration(
        displayName = "#CTL_SetPropertiesFilesEncoding"
)
@ActionReference(path = "Menu/Edit", position = 20100, separatorBefore = 20050)
@Messages("CTL_SetPropertiesFilesEncoding=Use project encoding for all .properties files")
public final class SetPropertiesFilesEncoding implements ActionListener
{

    static final String PROPERTY_ENCODING = "projectEncoding";  // Copied from PropertiesDataNote because not public
    private static final Logger LOGGER = Logger.getLogger(SetPropertiesFilesEncoding.class.getName());
    private final Project project;

    public SetPropertiesFilesEncoding(Project context)
    {
        this.project = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev)
    {

        Runnable r = () ->
        {
            LOGGER.info("=====================================================================");
            LOGGER.info("PROJECT=" + ProjectUtils.getInformation(project).getName() + ": force use of Project Encoding for all Bundle*.properties files");

            Sources sources = ProjectUtils.getSources(project);

            int total = 0;
            int updated = 0;
            int error = 0;

            for (SourceGroup srcGroup : sources.getSourceGroups(Sources.TYPE_GENERIC))
            {
                FileObject rootFolder = srcGroup.getRootFolder();
                LOGGER.info("- srcGroup: name=" + srcGroup.getName() + " rootFolder=" + rootFolder.getPath());
                DataFolder dataFolder = DataFolder.findFolder(rootFolder);
                if (dataFolder == null)
                {
                    continue;
                }

                var allChildren = dataFolder.children(true);
                while (allChildren.hasMoreElements())
                {
                    FileObject file = allChildren.nextElement().getPrimaryFile();
                    if (file == null)
                    {
                        continue;
                    }
                    FileObject parent = file.getParent();
                    if ((parent == null || !file.getParent().getPath().contains("/build/")) && file.getName().startsWith("Bundle") && file.getExt().equals("properties"))
                    {
                        total++;
                        Object attr = file.getAttribute(PROPERTY_ENCODING);
                        if (attr == null || ((Boolean) attr == false))
                        {
                            try
                            {
                                file.setAttribute(PROPERTY_ENCODING, Boolean.TRUE);
                                LOGGER.info("  Updated " + file.getPath());
                                updated++;
                            } catch (IOException ex)
                            {
                                LOGGER.warning("  Unable to update " + file.getPath() + ". Error=" + ex.getLocalizedMessage());
                                error++;
                            }
                        }
                    }
                }

            }
            String msg = "Force UseProjectEncoding on all Bundle*.properties files: total processed=" + total + "  updated=" + updated + "  errors=" + error;
            LOGGER.info("  " + msg);
            LOGGER.info("=====================================================================");
            NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
        };
        
        BaseProgressUtils.showProgressDialogAndRun(r, "Setting 'Use project encoding' for project Bundle*.properties files...");
    }
}
