package de.tud.stg.tigerseye.eclipse.core.runtime;

import java.io.File;
import java.io.IOException;
import java.util.Dictionary;

import javax.annotation.CheckForNull;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Finds the defined classpath entries for DSLs. <br>
 * DSLs can be deployed as jar file or folder. In the latter case the
 * {@code MANIFEST.MF} file is parsed and its entries added to the found
 * classpath entries. <br>
 * A DSL can also be still in development in some Eclipse instance in which case
 * the {@code .classpath} file contains the correct classpath information.
 * 
 * @author Leo Roos
 * 
 */
public class DSLClasspathResolver {

    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeDSLDefinitionsCPContainer.class);

    private final FileLocatorWrapper fileHelper;

    public DSLClasspathResolver() {
	this(new FileLocatorWrapper());
    }

    public DSLClasspathResolver(FileLocatorWrapper fileLocator) {
	this.fileHelper = fileLocator;
    }

    /**
     * Resolves the <i>probable</i> classpath for {@code bundle}.
     * <i>Probable</i> because it makes assumption regarding when the bundle is
     * still in a development location.
     * 
     * @param bundle
     *            Bundle to resolve classpath for.
     * @return the classpath of {@code bundle} which <i>should</i> not be empty.
     *         Returns <code>null</code> if no valid classpath was found.
     */
    public @CheckForNull
    File[] resolveCPEntriesForBundle(Bundle bundle) {

	File bundleFile = null;
	try {
	    bundleFile = fileHelper.getBundleFile(bundle);
	} catch (IOException e) {
	    logger.debug("No classpath for bundle {} could be resolved", bundle);
	    return null;
	}

	if (bundleFile.isFile()) {
	    if (FileHelper.isJar(bundleFile.getName())) {
		return new File[] { bundleFile };
	    } else {
		logger.debug("unknown format of file: ", bundleFile);
		return null;
	    }
	}

	// TODO check for .classpathfile instead build properties
	/*
	 * IF has .classpath and .project file get development classpath
	 */

	File buildProps = new File(bundleFile, "build.properties");
	if (buildProps.exists()) {
	    JDTClasspathResolver jdtresolver = new JDTClasspathResolver();
	    return jdtresolver.resolveClasspath(bundle);
	}


	@SuppressWarnings("unchecked")
	// guaranteed by documentation
	Dictionary<String, String> headers = bundle.getHeaders();
	String[] manifestClassPathEntries = getManifestClassPathEntries(headers);

	File[] result = getBundleLocationPrependedClasspathFiles(bundleFile,
		manifestClassPathEntries);
	return result;
    }

    private File[] getBundleLocationPrependedClasspathFiles(File bundleFile,
	    String[] manifestClassPathEntries) {
	File[] result = new File[manifestClassPathEntries.length];
	for (int i = 0; i < manifestClassPathEntries.length; i++) {
	    result[i] = new File(bundleFile, manifestClassPathEntries[i]);
	}
	return result;
    }

    private String[] getManifestClassPathEntries(
	    Dictionary<String, String> manifestEntries) {
	String cpEntries = manifestEntries.get(Constants.BUNDLE_CLASSPATH);

	String[] result;
	if (cpEntries == null)
	    // if no classpath-entry is available, the default classpath is used
	    result = new String[] { "." };
	else
	    result = cpEntries.split(",");

	return result;
    }

}
