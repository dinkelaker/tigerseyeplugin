package de.tud.stg.tigerseye.eclipse.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import de.tud.stg.popart.builder.core.annotations.DSL;

/**
 * General interface to handle registered DSLs between plug-in components <br>
 * Only unmodifiable values which every DSL definition has are directly
 * accessible <br>
 * Further DSL attributes can be retrieved using the {@link #getValue(DSLKey)}
 * method and set using the {@link #setValue(DSLKey, Object)}. This method
 * provides a preference store key which can be used to directly accessing the
 * preference store for requested attribute through {@link #getKeyFor(DSLKey)}.
 * But the use of the specialized getValue and setValue methods is recommended.<br>
 * 
 * @see {@link DSLKey}
 * 
 * @author Leo Roos
 * 
 */
@Nonnull
public interface DSLDefinition {

    /**
     * DSL with no behavior or state.
     */
    public static final DSLDefinition NULL_DSL = new NULLDSL();

    /**
     * Whether the language is set as active by the User.
     * 
     * @return <code>true</code> if language is active <code>false</code>
     *         otherwise.
     */
    public abstract boolean isActive();

    /**
     * The class path of the class implementing the {@link DSL} interface, which
     * represents the evaluation class of this DSL.
     * 
     * @return
     */
    public abstract String getClassPath();

    /**
     * The identifier of the plug-in providing this DSL
     * 
     * @return
     */
    public abstract String getContributorSymbolicName();


    /**
     * The user friendly name of this DSL.
     * 
     * @return
     */
    public String getDslName();

    /**
     * The unique identifier preference store key for this DSL.
     * 
     * @return
     */
    public abstract String getLanguageKey();

    /**
     * Returns the preference store key of this DSL for the attribute specified
     * by {@code key}.
     * 
     * @param key
     *            the identifier for the required DSL attribute
     * @return the final preference store key to access the value of the
     *         attribute defined through {@code key} for this DSL.
     */
    public abstract String getKeyFor(DSLKey<?> key);

    /**
     * A convenience method to associate further values with this DSL object.
     * 
     * @param key
     * @param value
     */
    public abstract void setData(DSLKey<?> key, Object value);

    /**
     * @return the object associated with the key or <code>null</code> if none
     *         is found.
     * @see {@link DSLDefinition#setData(Object, Object)}
     */
    public abstract @CheckForNull
    Object getData(DSLKey<?> key);

    /**
     * Sets the attribute of this DSL described by {@code key} to its default.
     * 
     * @param key
     */
    public void setToDefault(DSLKey<?> key);

    /**
     * Set attribute of type {@code T} identified through {@code key} for this
     * DSL.
     * 
     * @param <T>
     *            Type of attribute to set.
     * @param key
     *            describing what preference to set for this DSL
     * @param value
     *            the value to set for key {@code key} of this DSL.
     */
    public <T> void setValue(DSLKey<T> key, T value);

    /**
     * Gets value of type {@code T} from the preference store.
     * 
     * @param <T>
     *            Type of return value.
     * @param key
     *            describing what preference to get for this DSL
     * @return the attribute of type {@code T} for key {@code key} for this DSL.
     * @throws NoLegalPropertyFound
     */
    public <T> T getValue(DSLKey<T> key) throws NoLegalPropertyFound;

    static class NULLDSL implements
            DSLDefinition {
	private NULLDSL() {
	}

        @Override
        public void setData(DSLKey<?> key, Object value) {
        }
    
        @Override
        public String getLanguageKey() {
	    return "";
        }
    
        @Override
        public String getKeyFor(DSLKey<?> key) {
            return "";
        }
    
        @Override
        public String getDslName() {
            return "";
        }
    
        @Override
        public Object getData(DSLKey<?> key) {
	    return new Object();
        }
    
        @Override
        public String getContributorSymbolicName() {
            return "";
        }
    
        @Override
        public String getClassPath() {
            return "";
        }
    
        @Override
        public void setToDefault(DSLKey<?> key) {
        }
    
        @Override
        public <T> void setValue(DSLKey<T> key, T value) {
        }
    
        @Override
        public <T> T getValue(DSLKey<T> key) throws NoLegalPropertyFound {
            return null;
        }

	@Override
	public boolean isActive() {
	    return false;
	}
    }

}