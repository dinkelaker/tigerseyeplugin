package de.tud.stg.tigerseye.examples.simplesqldsl;

import groovy.lang.Closure;

import java.util.HashMap;

import de.tud.stg.tigerseye.dslsupport.annotations.DSLParameter;
import de.tud.stg.tigerseye.dslsupport.annotations.DSLMethod;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

/**
 * {@link SimpleSqlDSL} is a small DSL modelling a very simple subset of SQL operations
 * 
 * @author Kamil Erhard
 * 
 */
public class SimpleSqlDSL implements de.tud.stg.tigerseye.dslsupport.DSL {

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	@DSLMethod(production = "SELECT__p0__FROM__p1")
	
	public void selectFrom(String[] columns, String[] tables) {
		
	}

	@DSLMethod(production = "SELECT__p0__FROM__p1__WHERE__p2")
	
	public void selectFromWhere(String[] columns, String[] tables, @DSL(arrayDelimiter = "AND") String[] checks) {
		
	}
}
