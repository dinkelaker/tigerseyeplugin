package de.tud.stg.tigerseye.examples.statefuldsl;

import groovy.lang.Closure;

import java.util.HashMap;
import java.util.Map;

import de.tud.stg.tigerseye.dslsupport.annotations.DSLParameter;
import de.tud.stg.popart.eclipse.core.debug.annotations.PopartType;
import de.tud.stg.popart.eclipse.core.debug.model.keywords.PopartOperationKeyword;

/**
 * {@link StatefulDSL} is a small DSL showing the possibility of setting and retrieving variables.
 * 
 * @author Kamil Erhard
 * 
 */
@DSL
public class StatefulDSL implements de.tud.stg.tigerseye.dslsupport.DSL {

	public Object eval(HashMap map, Closure cl) {
		cl.setDelegate(this);
		cl.setResolveStrategy(Closure.DELEGATE_FIRST);
		return cl.call();
	}

	private Map<String, Object> variables = new HashMap<String, Object>();

	
	public void set__p0_equals_p1(String key, Object value) {
		this.variables.put(key, value);
	}

	
	public Object get__p0(String key) {
		return this.variables.get(key);
	}

}
