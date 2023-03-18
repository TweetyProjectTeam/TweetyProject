package org.tweetyproject.arg.dung.serialisibility.plotter;

import org.tweetyproject.commons.Plotter;
import org.tweetyproject.graphs.util.GraphPlotter;
import org.tweetyproject.graphs.Edge;
import org.tweetyproject.graphs.Graph;

public class SerialisableExtensionPlotter extends GraphPlotter<ExtensionNode, Edge<ExtensionNode>> {

	public SerialisableExtensionPlotter(Plotter plotter, Graph<ExtensionNode> graph) {
		super(plotter, graph);
	}

	@Override
	protected String getPrettyName(ExtensionNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getPrettyName(Edge<ExtensionNode> edge) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getStyle(ExtensionNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected double getVertexWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected double getVertexHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getFontSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getVertexSpacing() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	

}
