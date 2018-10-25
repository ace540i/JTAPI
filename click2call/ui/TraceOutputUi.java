package click2call.ui;

public interface TraceOutputUi {

	public abstract void append(boolean always, String str);

	public abstract void append(String str);

	public abstract void setState(boolean state);

	public abstract void clear();

}