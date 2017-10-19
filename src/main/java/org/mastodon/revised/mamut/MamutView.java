package org.mastodon.revised.mamut;

import org.mastodon.adapter.RefBimap;
import org.mastodon.app.MastodonView;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Vertex;
import org.mastodon.revised.model.mamut.Link;
import org.mastodon.revised.model.mamut.Spot;

public class MamutView< V extends Vertex< E >, E extends Edge< V > > extends MastodonView< MamutAppModel, Spot, Link, V, E >
{
	public MamutView( final MamutAppModel appModel, final RefBimap< Spot, V > vertexMap, final RefBimap< Link, E > edgeMap )
	{
		super( appModel, vertexMap, edgeMap );
	}
}