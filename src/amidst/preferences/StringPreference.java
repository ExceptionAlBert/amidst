package amidst.preferences;

import java.util.prefs.Preferences;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class StringPreference extends PrefModelBase<String> {
	public StringPreference(Preferences preferences, String key,
			String defaultValue) {
		super(preferences, key, defaultValue);
	}

	@Override
	protected String getInitialValue(String defaultValue) {
		return preferences.get(key, defaultValue);
	}

	@Override
	protected void update(String value) {
		preferences.put(key, value);
	}
}
