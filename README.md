This Netbeans IDE plugin scans all the `Bundle*properties` of the selected project and set **use project encoding** on each file found.

# Why ?

This was created to solve `Bundle*.properties` encoding issues I encountered while developing JJazzLab.

JJazzLab `Bundle*.properties` files use UTF8 encoding:
- Translations are performed by users on the *crowdin* platform which is connected to the JJazzLab repository. Crowdin generates pull requests with updated `Bundle*.properties` using UTF-8.
- JJazzLab pom.xml specifies `<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>`

By default it seems Netbeans does not consider a properties file to be UTF-8, even if the project is configured in Maven to use UTF-8. 
You can fix that in Netbeans by selecting one or more `Bundle*properties` files in the Projects or Files tab > right-click menu > Properties >  check **"Use project encoding"**. 
Netbeans will remember this per-file setting.

If you forgot to do this and you directly open a `Bundle*properties` file in Netbeans, you usually notice the error (strange characters appear) and you can use the trick above to fix it.

However, if you use the Netbeans GUI Builder with internationalized strings, you may do changes in the GUI Builder that will corrupt all the translation files because of the wrong encoding ! 
And this can go unnoticed for a while as it only impacts the translation files `Bundle*.properties`, not `Bundle.properties`...


# Install

Clone and open the PluginUseProjectEncoding4Properties project in Netbeans. Select it, right-click menu "Install/reload in IDE".

This will add a new menu entry in the Netbeans Edit menu. Select your root project and run the command: it will set **use project encoding** on all `Bundle*.properties` files found in the selected project.

This probably should be redone when you upgrade Netbeans.


