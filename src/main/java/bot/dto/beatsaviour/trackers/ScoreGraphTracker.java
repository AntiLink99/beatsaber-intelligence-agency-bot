package bot.dto.beatsaviour.trackers;

import org.apache.commons.collections4.map.LinkedMap;

public class ScoreGraphTracker {
	private LinkedMap<String,Double> graph;

	public LinkedMap<String,Double> getGraph() {
		return graph;
	}

	public void setGraph(LinkedMap<String,Double> graph) {
		this.graph = graph;
	}
}
