package pers.jc.engine;

import java.util.HashMap;

public class JCEntity {
	public final int id = JCEngine.getAutoIncrementID(JCEntity.class);
	public JCChannel channel;
	public boolean isValid;
	
	public void onLoad() {}	
	
	public void onDestroy() {}
	
	public void call(String func, Object... args) {
		sendDataText(packDataText(func, args));
	}

	public void sendDataText(String text) {
		if (isValid) {
			channel.writeAndFlush(text);
		}
	}

	public static String packDataText(String func, Object... args) {
		return new JCData("", JCData.TYPE_FUNCTION, func, args).stringify();
	}

	public void syncPropertiesToClient(String... propertyNames) {
		if (isValid) {
			HashMap<String, Object> properties = new HashMap<>();
			for (String propertyName : propertyNames) {
				try {
					Object value = getClass().getField(propertyName).get(this);
					properties.put(propertyName, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			String dataText = new JCData("", JCData.TYPE_EVENT,
				"updateEntityProperties", new Object[]{properties}).stringify();
			channel.writeAndFlush(dataText);
		}
	}
}
