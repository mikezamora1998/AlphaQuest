package org.alphaquest.java.delegate;

public interface LoadingDelegate {

	public void setup();
	public void setFinishedLoading(boolean b);
	public boolean isFinishedLoading();
}
