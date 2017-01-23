package org.mastodon.revised.model.tagset;

import java.awt.Color;

public class Tag
{

	private final int index;

	private String label;

	private Color color;

	Tag( final int index, final String label, final Color color )
	{
		this.index = index;
		this.label = label;
		this.color = color;
	}

	int index()
	{
		return index;
	}

	public void setColor( final Color color )
	{
		this.color = color;
	}

	public void setLabel( final String label )
	{
		this.label = label;
	}

	public Color color()
	{
		return color;
	}

	public String label()
	{
		return label;
	}
}
