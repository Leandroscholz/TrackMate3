package org.mastodon.revised.ui;

public interface ProgressListener
{

	public void out( String msg );

	public void err( String msg );

	public void setProgress( double progressCompletion );

}
