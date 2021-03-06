package de.tud.stg.tigerseye.eclipse.core.runtime;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class TigerseyeClassPathContainerInitializer extends
	ClasspathContainerInitializer {

    public TigerseyeClassPathContainerInitializer() {
    }

    @Override
    public void initialize(IPath containerPath, IJavaProject project)
	    throws CoreException {
	IClasspathContainer tigerseyeContainer = new TigerseyeClasspathContainer(
		project.getProject());
	JavaCore.setClasspathContainer(containerPath,
		new IJavaProject[] { project },
		new IClasspathContainer[] { tigerseyeContainer }, null);
    }

    @Override
    public boolean canUpdateClasspathContainer(IPath containerPath,
	    IJavaProject project) {
	/*
	 * Can always be updated.
	 */
	return true;
    }

    @Override
    public void requestClasspathContainerUpdate(IPath containerPath,
	    IJavaProject project, IClasspathContainer containerSuggestion)
	    throws CoreException {
	this.initialize(containerPath, project);
    }

}
