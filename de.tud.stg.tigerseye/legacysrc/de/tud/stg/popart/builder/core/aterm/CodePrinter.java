package de.tud.stg.popart.builder.core.aterm;

import java.io.OutputStream;

import aterm.Visitor;

/**
 * A {@link CodePrinter} is capable of visiting an AST and printing flat code in some concrete language syntax.
 * 
 * @author Kamil Erhard
 * 
 */
public interface CodePrinter extends Visitor {

	public void write(OutputStream out);

}