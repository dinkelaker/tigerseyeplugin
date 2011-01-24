package de.tud.stg.tigerseye.core;


/**
 * Indicates that the property store does not contain the attribute searched
 * for.
 * 
 * @author Leo Roos
 * 
 */
public class NoLegalPropertyFound extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final DSLKey<?> NullKey = DSLKey.NULL_KEY;
    private transient DSLKey<?> key;

    public NoLegalPropertyFound() {
	super();
    }

    public NoLegalPropertyFound(String message) {
	super(message);
    }

    public NoLegalPropertyFound setKey(DSLKey<?> key) {
	this.key = key;
	return this;
    }

    public DSLKey<?> getKey() {
	if(key == null) {
	    key = NullKey;
	}   
    return key;
    }

    @Override
    public String toString() {

	return super.toString();
    }

}
