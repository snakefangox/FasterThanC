package net.snakefangox.fasterthanc.tools;

import java.util.*;

public class Graph<T> implements Iterable<T> {
	Map<T, Set<T>> verticesMap;

	int edgesCount;

	public Graph() {
		verticesMap = new HashMap<>();
	}

	public int getNumVertices() {
		return verticesMap.size();
	}

	public int getNumEdges() {
		return edgesCount;
	}


	private void validateVertex(T v) {
		if (!hasVertex(v)) throw new IllegalArgumentException(v.toString() + " is not a vertex");
	}

	public int degree(T v) {
		validateVertex(v);
		return verticesMap.get(v).size();
	}

	public void addEdge(T v, T w) {
		if (!hasVertex(v)) addVertex(v);
		if (!hasVertex(w)) addVertex(w);
		if (!hasEdge(v, w)) edgesCount++;
		verticesMap.get(v).add(w);
		verticesMap.get(w).add(v);
	}

	public void addVertex(T v) {
		if (!hasVertex(v)) verticesMap.put(v, new HashSet<T>());
	}

	public void removeVertex(T v) {
		verticesMap.get(v).forEach(w -> {
			verticesMap.get(w).remove(v);
		});
		verticesMap.remove(v);
	}

	public boolean hasEdge(T v, T w) {
		validateVertex(v);
		validateVertex(w);
		return verticesMap.get(v).contains(w);
	}

	public boolean hasVertex(T v) {
		return verticesMap.containsKey(v);
	}

	@Override
	public Iterator<T> iterator() {
		return verticesMap.keySet().iterator();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (T v : verticesMap.keySet()) {
			builder.append(v.toString() + ": ");
			for (T w : verticesMap.get(v)) {
				builder.append(w.toString() + " ");
			}
			builder.append("\n");
		}

		return builder.toString();
	}
}
