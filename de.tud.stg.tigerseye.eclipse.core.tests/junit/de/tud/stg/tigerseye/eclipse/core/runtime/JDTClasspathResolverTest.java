package de.tud.stg.tigerseye.eclipse.core.runtime;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.ClassPathDetector;
import org.eclipse.jdt.launching.JavaRuntime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.tigerseye.test.TransformationUtils;

import utilities.GlobalResourceCollection;

public class JDTClasspathResolverTest {
	
	
	private static final Logger logger = LoggerFactory.getLogger(JDTClasspathResolverTest.class);

	@Mock(answer = Answers.RETURNS_SMART_NULLS)
	private Bundle bundleMock;
	@Mock(answer = Answers.RETURNS_SMART_NULLS)
	private IProject projectMock;
	@Mock(answer = Answers.RETURNS_SMART_NULLS)
	private IProjectDescription descriptionMock;
	@Mock(answer = Answers.RETURNS_SMART_NULLS)
	private FileLocatorWrapper fileLocator;
	@Mock(answer = Answers.RETURNS_SMART_NULLS)
	private ClassPathDetectorWrapper detector;
	@Mock(answer = Answers.RETURNS_SMART_NULLS)
	private ProjectLinker linker;

	JDTClasspathResolver cut = new JDTClasspathResolver();

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
	}

	@Ignore("JUnit Plug-in Test")
	@Test
	public void investigateActualExternalProjectResolution() throws Exception {

		URI testproj;
		// testproj =
		// TestResource.DSLDefinitionsDevelopmentProjectRoot.getFile().toURI();
		//
		// URL fileURL = FileLocator.toFileURL(testproj.toURL());
		//
		// testproj = fileURL.toURI();

		testproj = new File(
				"/home/leo/wss/runtime-New_configuration/DSLDefinitions/")
				.toURI();

		String projectName = "test.some.project.name";
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName);

		IProjectDescription projDesc = ResourcesPlugin.getWorkspace()
				.newProjectDescription(projectName);
		projDesc.setLocationURI(testproj);

		project.create(projDesc, null);

		project.open(null);
		// URI rawLocationURI = project.getRawLocationURI();
		//
		// logger.info(rawLocationURI);

		IProjectDescription description = project.getDescription();

		logger.info(ToStringBuilder.reflectionToString(description));

		IJavaProject newJavProj = JavaCore.create(project);

		IClasspathEntry[] rawClasspath = newJavProj.getRawClasspath();

		logger.info("Raw cp");
		for (IClasspathEntry iClasspathEntry : rawClasspath) {
			logger.info(ToStringBuilder.reflectionToString(
					iClasspathEntry, ToStringStyle.SIMPLE_STYLE));
		}

		@SuppressWarnings("restriction")
		ClassPathDetector detector = new ClassPathDetector(project, null);
		IClasspathEntry[] classpath = detector.getClasspath();
		logger.info("Detected cp");
		for (IClasspathEntry iClasspathEntry : classpath) {
			logger.info(ToStringBuilder.reflectionToString(
					iClasspathEntry, ToStringStyle.SIMPLE_STYLE));
		}
		logger.info("just change sth");
		// new ClassPathDetector(project, monitor)

		// Try that project
		// /home/leo/wss/runtime-New_configuration/DSLDefinitions/
	}
	
	@Ignore("JUnit Plug-in test")
	@Test
	public void testResolveClasspathAsPluginTest() throws Exception {
		Bundle bundle = Platform.getBundle("de.tud.stg.tigerseye.examples.DSLDefinitions");
//		File[] resolveClasspath = cut.resolveClasspathAndLinkProject(bundle);
//		for (File file : resolveClasspath) {
//			logger.info(file);
//		}
	}


	@Ignore("TODO")
	@Test
	public void testResolveClasspath() throws Exception {
		File someJavaProjectRoot = GlobalResourceCollection.DSLDefinitionsDevelopmentProjectRoot
				.getFile();
		// when(bundleMock.get)

		when(descriptionMock.getLocationURI()).thenReturn(
				someJavaProjectRoot.toURI());
		when(projectMock.getName()).thenReturn("some.project.name");
		when(projectMock.getDescription()).thenReturn(descriptionMock);
		mockResolverForBundleResource(someJavaProjectRoot);

//		File[] classpathEntries = cut.resolveClasspathAndLinkProject(bundleMock);
//
//		File[] expectedFiles = getFilesFor(someJavaProjectRoot, "bin");
//
//		assertThat(Arrays.asList(classpathEntries), hasItems(expectedFiles));

	}

	@Ignore("TODO")
	@Test
	public void testResolveClasspathForWrongFormattedProject() throws Exception {
		GlobalResourceCollection.LogoDSLClasspath.getFile();
		fail();
	}

	private File[] getFilesFor(File eclipseJavaProjectRoot, String... expected) {
		return TransformationUtils.getFilesRelativeToRoot(eclipseJavaProjectRoot,
				expected);
	}

	private void mockResolverForBundleResource(File resource) throws Exception {
		when(fileLocator.getBundleFile(Mockito.any(Bundle.class))).thenReturn(
				resource);
		cut = new JDTClasspathResolver(fileLocator, linker);
	}

}
