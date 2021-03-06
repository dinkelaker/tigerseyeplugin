package de.tud.stg.tigerseye.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import de.tud.stg.tigerseye.eclipse.core.TigerseyeCore;

public class TigerseyeUIPreferenceInitializer {

    private static final RGB DEFAULT_RGB_VALUE = new RGB(0, 0, 255);

    public void initialize() {
	IPreferenceStore store = TigerseyeCore.getPreferences();

	store.setDefault(
		TigerseyeUIPreferenceConstants.TIGERSEYE_EDITOR_HIGHLIGHT_KEYWORDS_ENABLED,
		true);
	PreferenceConverter.setDefault(store,
		TigerseyeUIPreferenceConstants.DEFAULT_COLOR_VALUE,
		DEFAULT_RGB_VALUE);

    }

}
