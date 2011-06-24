/**
 * 
 */
package de.tud.stg.popart.eclipse.wizards.legacygroovy.ui;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;

import org.eclipse.jdt.core.IJavaProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This generator is responsible for the generation of artifacts like
 * new Groovy Classes, new Test Classes, ...
 * 
 * @author Thorsten Kamann <thorsten.kamann@googlemail.com>
 */
/**
 * @author thorsten
 * 
 */
public class ArtifactCodeGenerator {
    private StringBuffer buffer = new StringBuffer();
    private final IJavaProject project;
    private int indentationCount = 0;

    private static final Logger logger = LoggerFactory
	    .getLogger(ArtifactCodeGenerator.class);
    /**
     * Defines the possible directions for the identation
     */
    public static class IndentationDirection {
	/**
	 * Constant for the indentation direction left
	 */
	public static int INDENT_LEFT = -1;

	/**
	 * Constant for the indentation direction none (no identation)
	 */
	public static int INDENT_NONE = 0;

	/**
	 * Constant for the indentation direction right
	 */
	public static int INDENT_RIGHT = 1;
    }

    /**
     * @param project
     *            The IJavaProject
     */
    public ArtifactCodeGenerator(IJavaProject project) {
	this.project = project;
    }

    /**
     * Clears the underlying buffer.
     * 
     * @return The current instance of ArtifactGenerator
     */
    public ArtifactCodeGenerator clear() {
	buffer = new StringBuffer();
	return this;
    }

    /**
     * Adds only a linebreak. This is a convinience method. You can use
     * <code>addCode("")</code>, too.
     * 
     * @return The current instance of ArtifactGenerator
     */
    public ArtifactCodeGenerator addLineBreak() {
	return addCode("");
    }

    /**
     * Adds a code fragment to the current buffer of code lines
     * 
     * @param code
     *            The code fragment to add
     * @return The current instance of ArtifactGenerator
     */
    public ArtifactCodeGenerator addCode(String code) {
	return addCode(IndentationDirection.INDENT_NONE, code, true);
    }

	/**
     * Adds a code fragment to the current buffer of code lines
     * 
     * @param indentationDirection
     *            The IndentationDirection to use
     * @param code
     *            The code fragment to add
     * @return The current instance of ArtifactGenerator
     */
    public ArtifactCodeGenerator addCode(int indentationDirection, String code) {
	return addCode(indentationDirection, code, true);
    }

    /**
     * Adds a code fragment to the current buffer of code lines
     * 
     * @param code
     *            The code fragment to add
     * @param addLineBreak
     *            Should a linebreak added to the code fragment?
     * @return The current instance of ArtifactGenerator
     */
    public ArtifactCodeGenerator addCode(String code, boolean addLineBreak) {
	return addCode(IndentationDirection.INDENT_NONE, code, addLineBreak);
    }

	/**
     * Adds a code fragment to the current buffer of code lines
     * 
     * @param indentationDirection
     *            The IndentationDirection to use
     * @param code
     *            The code fragment to add
     * @param addLineBreak
     *            Should a linebreak added to the code fragment?
     * @return The current instance of ArtifactGenerator
     */
    public ArtifactCodeGenerator addCode(int indentationDirection, String code,
	    boolean addLineBreak) {
	String indentation = "";

	setIndentationCount(indentationDirection);
	for (int i = 0; i < indentationCount; i++) {
	    indentation += CodeGeneration.getIndentation(project);
	}

	buffer.append(indentation);
	buffer.append(fixCodeWithPrefix(indentation, code));
	if (addLineBreak) {
	    buffer.append(CodeGeneration.getLineDelimiter(project));
	}
	return this;
    }

    /**
     * Returns the content of the buffer of codefragments as String
     */
    @Override
    public String toString() {
	return buffer.toString();
    }

	private void setIndentationCount(int indentationDirection) {
	indentationCount += indentationDirection;
	if (indentationCount < 0) {
	    indentationCount = 0;
	}
    }

    private String fixCodeWithPrefix(String prefix, String code) {
	LineNumberReader reader;
	StringBuffer buffer = new StringBuffer();

	reader = new LineNumberReader(new StringReader(code));
	String line;
	try {
	    line = reader.readLine();
	    boolean firstLinePassed = false;
	    while (line != null) {
		if (firstLinePassed) {
		    buffer.append(prefix);

		} else {
		    firstLinePassed = true;
		}
		buffer.append(line);
		line = reader.readLine();
		if (line != null) {
		    buffer.append(CodeGeneration.getLineDelimiter(project));
		}
	    }
	} catch (IOException e) {
	    logger.warn(e.getLocalizedMessage());
	    return code;
	}
	return buffer.toString();
    }
}
