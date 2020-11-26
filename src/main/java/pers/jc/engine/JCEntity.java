package pers.jc.engine;

public class JCEntity {
	public int id;
	public JCChannel channel;
	public boolean isValid;
	
	public void onLoad() {}	
	
	public void onDestroy() {}
	
	public void call(String func, Object... args) {
		if (isValid) {
			String text = new JCData("", JCData.TYPE_FUNCTION, func, args).stringify();
			channel.writeAndFlush(text);
		}
	}
}
