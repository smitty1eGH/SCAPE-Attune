package com.bah.attune.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.neo4j.graphdb.Node;
import org.parboiled.common.StringUtils;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Repository;

import com.bah.attune.data.LinkNode;
import com.bah.attune.data.NameValuePair;

@Repository
public class TraceabilityDao extends BaseDao {

	/***
	 * This function gets all of the gap nodes in the database, then attempts to
	 * build a path from each of those nodes, to the root node(s). The returned
	 * tree will also include sibling nodes of each of the gap nodes.
	 */
	public LinkNode getLinkNodeGaps() {
		LinkNode node = new LinkNode(); // Link Node to hold entire tree
										// containing nodes that have gaps
		List<Node> gapNodes = getGapNodes();
		List<Node> rootNodes = getRootNodes();

		List<Node> netRootNodes = new ArrayList<Node>();

		// Filter out the root nodes that have no connections to gap nodes
		for (Node rootNode : rootNodes) {
			for (Node gapNode : gapNodes)
				if (hasConnection(rootNode, gapNode)
						&& !netRootNodes.contains(rootNode))
					netRootNodes.add(rootNode);
		}

		// If there is only 1 root node, use that as the root node in the tree,
		// otherwise if there are more than that, then create an artificial
		// root node whose name is 'All XYZ', where XYZ is the label of the
		// root nodes, and whose direct children are the root nodes.
		if (netRootNodes.size() == 1) {
			Node rootNode = netRootNodes.get(0);

			String nodeLabel = getLabels(rootNode);
			String nodeName = rootNode.getProperty(NAME).toString();

			node.setName(nodeName);
			node.addData(new NameValuePair("label", nodeLabel));
			node.setId("1");
			node.setType(" ");
			node.setChildren(getPath(rootNode, gapNodes));
		} else {
			node.setId("1");
			node.setName("All " + getRootLabel() + "(s)");

			List<LinkNode> nodes = new ArrayList<LinkNode>();
			for (Node rootNode : netRootNodes) {
				LinkNode n = new LinkNode();

				String nodeLabel = getLabels(rootNode);
				String nodeName = rootNode.getProperty(NAME).toString();

				n.setId(nodeLabel + nodeName);
				n.setName(nodeName);
				n.addData(new NameValuePair("label", nodeLabel));

				List<LinkNode> children = getPath(rootNode, gapNodes);

				if (children.size() > 0) {
					n.setChildren(children);
					n.addData(new NameValuePair("childCount", children.size()
							+ ""));

					nodes.add(n);
				}
			}

			node.setChildren(nodes);
		}

		return node;
	}

	/***
	 * This function first finds the desired node by <b>nodeName</b>, then
	 * attempts to build a path from the root node(s) to the desired node. Used
	 * when user will search for a node in the Traceability view.
	 */
	public LinkNode getNodePath(String nodeName) {
		LinkNode node = new LinkNode();

		// List<Node> rootNodes = getRootNode();
		List<Node> rootNodes = getRootNodes();
		List<Node> netRootNodes = new ArrayList<Node>();

		Node targetNode = getNodeByName(nodeName);

		// Filter out the root nodes that have no connections to gap nodes
		for (Node rootNode : rootNodes) {
			List<LinkNode> children = getPath(rootNode, targetNode);

			if (children.size() > 0)
				netRootNodes.add(rootNode);
		}

		// If there is only 1 root node, use that as the root node in the tree,
		// otherwise if there are more than that, then create an artificial
		// root node whose name is 'All XYZ', where XYZ is the label of the
		// root nodes, and whose direct children are the root nodes.
		if (netRootNodes.size() == 1) {
			Node rootNode = netRootNodes.get(0);

			String rootLabel = getLabels(rootNode);
			String rootName = rootNode.getProperty(NAME).toString();

			node.setName(rootName);
			node.addData(new NameValuePair("label", rootLabel));
			node.setId("1");
			node.setChildren(getPath(rootNode, targetNode));
		} else {
			node.setId("1");
			node.setName("All " + getRootLabel() + "(s)");

			List<LinkNode> nodes = new ArrayList<LinkNode>();
			for (Node rootNode : netRootNodes) {
				LinkNode n = new LinkNode();

				String rootLabel = getLabels(rootNode);
				String rootName = rootNode.getProperty(NAME).toString();

				n.setId(rootLabel + rootName);
				n.setName(rootName);
				n.addData(new NameValuePair("label", rootLabel));

				List<LinkNode> children = getPath(rootNode, targetNode);

				if (children.size() > 0) {
					n.setChildren(children);
					n.addData(new NameValuePair("childCount", children.size()
							+ ""));

					nodes.add(n);
				}
			}

			node.setChildren(nodes);
		}

		return node;
	}

	/***
	 * Returns the path to get from <b>startNode</b> to <b>endNode</b>. It will
	 * include any subpaths to get to that node (i.e. if a path forks, then
	 * joins back together at some point).
	 */
	public List<LinkNode> getPath(Node startNode, Node endNode) {
		if (isParent(startNode, endNode)) {
			List<LinkNode> nodes = new ArrayList<LinkNode>();
			LinkNode node = new LinkNode();

			String name = endNode.getProperty(NAME).toString();
			String label = getLabels(endNode);

			node.setName(name);
			node.setId(label + name);
			node.addData(new NameValuePair("label", label));

			List<LinkNode> children = getChildren(node, 1, 0);
			node.addData(new NameValuePair("childCount", children.size() + ""));
			node.setChildren(new ArrayList<LinkNode>(0));

			nodes.add(node);

			return nodes;
		} else {
			long startNodeID = startNode.getId();
			long endNodeID = endNode.getId();

			List<LinkNode> subPaths = new ArrayList<LinkNode>();

			String query = "match (n)-->(nextChild)-[*]->(m) where id(n)="
					+ startNodeID + " and id(m)=" + endNodeID
					+ " return nextChild";

			int childCount = 0;
			for (Map<String, Object> row : runQuery(query)) {
				Node child = (Node) row.get("nextChild");

				String name = child.getProperty(NAME).toString();
				String label = getLabels(child);

				LinkNode node = new LinkNode();
				node.setName(name);
				node.addData(new NameValuePair("label", label));
				node.setId(label + name);

				List<LinkNode> children = getPath(child, endNode);

				node.setChildren(children);
				node.addData(new NameValuePair("childCount", children.size()
						+ ""));

				subPaths.add(node);
				childCount++;
			}

			if (childCount == 0)
				return new ArrayList<LinkNode>(0);

			return subPaths;
		}
	}

	/***
	 * This returns the tree of nodes that is <b>levels</b> deep, with the top
	 * of the tree being the requested node with a label and name. An id is
	 * passed through, because the id of the node returned, must match the one
	 * that was requested. In the case where a label or name is null, the root
	 * level node tree is returned, <b>levels</b> deep.
	 */
	public LinkNode getLinkNodeData(String id, String label, String name,
			Integer levels) {
		if (levels <= 0)
			return null;
		else {
			if (!StringUtils.isEmpty(label) && !StringUtils.isEmpty(name)) {
				LinkNode startNode = new LinkNode();

				startNode.setId(id);
				startNode.setName(name);
				startNode.addData(new NameValuePair("label", label));
				startNode.addData(new NameValuePair("childCount",
						getChildCount(label, name)));
				startNode.setChildren(getChildren(startNode, levels, 1));

				return startNode;
			} else {
				List<LinkNode> rootNodes = getRootLinkNodes();

				LinkNode rootNode = new LinkNode();

				if (rootNodes.size() == 1) {
					rootNode = rootNodes.get(0);
					rootNode.setChildren(getChildren(rootNode, levels, 0));
				} else if (rootNodes.size() > 1) {
					List<LinkNode> children = new ArrayList<LinkNode>();

					rootNode.setId("1");
					rootNode.setName("All root entities");

					for (LinkNode root : rootNodes) {
						root.setChildren(getChildren(root, levels, 1));
						children.add(root);
					}

					// Sort the nodes by name
					Collections.sort(children, new Comparator<LinkNode>() {
						@Override
						public int compare(LinkNode one, LinkNode two) {
							return one.getName().compareTo(two.getName());
						}
					});

					rootNode.setChildren(children);
				}

				return rootNode;
			}
		}
	}

	// Counts the amount of children a node has.
	private String getChildCount(String label, String name) {
		String query = "match (n:`" + label + "` {name:'" + name
				+ "'})-->(m) return count(distinct m)";

		Map<String, Object> result = runQuery(query).single();

		return result.get("count(distinct m)").toString();
	}

	// A root node is considered such if it has no parents in the Metadata
	// model.
	private List<LinkNode> getRootLinkNodes() {
		List<LinkNode> rootNodes = new ArrayList<LinkNode>();
		List<String> rootLabels = getRootLabels();

		if (!rootLabels.isEmpty()) {
			for (String rootLabel : rootLabels) {
				String query = "match (n:`" + rootLabel + "`) return n";

				for (Map<String, Object> result : runQuery(query)) {
					Node node = (Node) result.get("n");

					LinkNode n = new LinkNode();
					n.setId(String.valueOf(node.getId()));
					n.setName(node.getProperty(NAME).toString());
					n.addData(new NameValuePair("label", rootLabel));
					n.addData(new NameValuePair("childCount",
							getChildCount(getLabels(node),
							node.getProperty(NAME).toString())));
					rootNodes.add(n);
				}
			}
		}

		return rootNodes;
	}

	// private List<Node> getRootNodes() {
	// List<Node> rootNodes = new ArrayList<Node>();
	// String rootLabel = getRootLabel();
	//
	// String query = "match (n:`" + rootLabel + "`) return n";
	//
	// for (Map<String, Object> result : runQuery(query))
	// rootNodes.add((Node) result.get("n"));
	//
	// return rootNodes;
	// }

	private List<Node> getRootNodes() {
		List<Node> rootNodes = new ArrayList<Node>();
		List<String> rootLabels = getRootLabels();

		for (String rootLabel : rootLabels) {
			String query = "match (n:`" + rootLabel + "`) return n";

			for (Map<String, Object> result : runQuery(query))
				rootNodes.add((Node) result.get("n"));
		}

		return rootNodes;
	}

	private String getRootLabel() {
		String isRootQuery = "match (n:Metadata {isRoot: true}) return n";

		Map<String, Object> result = runQuery(isRootQuery).singleOrNull();

		// If there is a metadata node that has the property isRoot, then we'll
		// use that
		if (result != null)
			return ((Node) result.get("n")).getProperty("name").toString();

		// Find a root node otherwise
		String rootNodeQuery = "match (n:Metadata) where (n)-->() and not (n)<--() return distinct n";

		Map<String, Object> rootNode = runQuery(rootNodeQuery).singleOrNull();

		return ((Node) rootNode.get("n")).getProperty("name").toString();
	}

	private List<String> getRootLabels() {
		String isRootQuery = "match (n:Metadata {isRoot: true}) return n";

		List<String> rootLabels = new ArrayList<String>();
		Result<Map<String, Object>> results = runQuery(isRootQuery);

		for (Map<String, Object> result : results) {
			String name = ((Node) result.get("n")).getProperty("name")
					.toString();
			rootLabels.add(name);
		}

		if (rootLabels.isEmpty()) {
			// Find a root node otherwise
			String rootNodeQuery = "match (n:Metadata) where (n)-->() and not (n)<--() return distinct n";

			Map<String, Object> rootNode = runQuery(rootNodeQuery)
					.singleOrNull();

			rootLabels.add(((Node) rootNode.get("n")).getProperty("name")
					.toString());
		}

		return rootLabels;
	}

	private boolean containsLinkNode(List<LinkNode> list, LinkNode that) {
		for (LinkNode node : list)
			if (node.getId().equals(that.getId()))
				return true;
		return false;
	}

	private boolean isParent(Node parent, Node child) {
		long parentID = parent.getId();
		long childID = child.getId();

		String query = "match (n)-->(m) where id(n)=" + parentID
				+ " and id(m)=" + childID + " return count(n)";
		Map<String, Object> result = runQuery(query).single();

		return result.get("count(n)").equals(1);
	}

	private boolean hasConnection(Node node1, Node node2) {
		long node1ID = node1.getId();
		long node2ID = node2.getId();

		String query = "match (n)-[*]->(m) where id(n)=" + node1ID
				+ " and id(m)=" + node2ID + " return count(n)";
		Map<String, Object> result = runQuery(query).single();

		return result.get("count(n)").equals(1);
	}

	private List<Node> getGapNodes() {
		List<Node> gapNodes = new ArrayList<Node>();
		String metadataGapQuery = "match (n:Metadata)-[{required: true}]->(m:Metadata) return n, m";

		// Grab all of the nodes that have gaps, and include the siblings of
		// those nodes
		for (Map<String, Object> row : runQuery(metadataGapQuery)) {
			Node parent = (Node) row.get("n");
			Node child = (Node) row.get("m");

			String parentLabel = parent.getProperty("name").toString();
			String childLabel = child.getProperty("name").toString();

			String gapQuery = "match (n:`" + parentLabel + "`) where not (n:`"
					+ parentLabel + "`)-->(:`" + childLabel + "`) return n";

			for (Map<String, Object> gap : runQuery(gapQuery)) {
				Node gapNode = (Node) gap.get("n");

				long gapNodeID = gapNode.getId();

				String siblingQuery = "match (sibling:`" + parentLabel
						+ "`)<--()-->(gapNode) where id(gapNode)=" + gapNodeID
						+ " return sibling";

				gapNodes.add(gapNode);

				// Add all of the siblings of the gap node
				for (Map<String, Object> sibling : runQuery(siblingQuery)) {
					Node siblingNode = (Node) sibling.get("sibling");

					if (!gapNodes.contains(siblingNode))
						gapNodes.add(siblingNode);
				}
			}
		}

		return gapNodes;
	}

	/***
	 * Returns a path from the start node, to each (but not necessarily all) of
	 * the end nodes. The key in this function is that many ancestors of the end
	 * nodes that are descendants of the start node, are repeated, so this
	 * function correctly ignores repeats. Additionally, there may be multiple
	 * ways to get to an end node, and so those paths are included. <br>
	 * <br>
	 * The function returns the list of children nodes, whose parent is the
	 * <b>startNode</b>. Therefore this function should be called when setting
	 * the children of the <b>startNode</b>.
	 */
	private List<LinkNode> getPath(Node startNode, List<Node> endNodes) {
		if (endNodes.size() == 0)
			return new ArrayList<LinkNode>(0);

		List<LinkNode> children = new ArrayList<LinkNode>();
		List<Node> childrenLeft = new ArrayList<Node>(endNodes);

		for (Iterator<Node> iter = endNodes.iterator(); iter.hasNext();) {
			Node next = iter.next();
			if (isParent(startNode, next)) {
				String name = next.getProperty(NAME).toString();
				String label = getLabels(next);

				LinkNode node = new LinkNode();

				node.setId(label + name);
				node.setName(name);
				node.addData(new NameValuePair("label", label));
				node.addData(new NameValuePair("childCount", getChildCount(
						label, name)));
				node.addData(new NameValuePair("isGap", getIsGap(next)));
				node.setChildren(new ArrayList<LinkNode>(0));

				children.add(node);
				childrenLeft.remove(next);
			} else if (!hasConnection(startNode, next)) {
				childrenLeft.remove(next);
			} else {
				long startID = startNode.getId();
				long endID = next.getId();

				String query = "match (n)-->(childNode)-->(m) where id(n)="
						+ startID + " and id(m)=" + endID + " return childNode";

				for (Map<String, Object> row : runQuery(query)) {
					Node n = (Node) row.get("childNode");

					String name = n.getProperty(NAME).toString();
					String label = getLabels(n);

					LinkNode childNode = new LinkNode();
					childNode.setId(label + name);

					if (!containsLinkNode(children, childNode)) {
						childNode.setName(name);
						childNode.addData(new NameValuePair("label", label));
						childNode.addData(new NameValuePair("childCount",
								getChildCount(label, name)));
						childNode.setChildren(getPath(n, childrenLeft));

						children.add(childNode);
					}
				}
			}
		}

		return children;

	}

	/**
	 * Recursive function to find all children within the scope of the data set
	 */
	private List<LinkNode> getChildren(LinkNode node) {
		List<LinkNode> children = new ArrayList<LinkNode>();

		String label = node.getDataValueByName("label");
		String name = node.getName();

		String query = "match (n:`" + label + "` {name:'" + name
				+ "'})-->(m) return distinct m";
		for (Map<String, Object> result : runQuery(query)) {
			Node childNode = (Node) result.get("m");
			LinkNode child = new LinkNode();

			child.setId(UUID.randomUUID().toString());
			child.setName(childNode.getProperty(NAME).toString());
			child.addData(new NameValuePair("label", getLabels(childNode)));
			child.setChildren(getChildren(child));

			children.add(child);
		}

		return children;
	}

	/***
	 * Get children function that takes into account how many levels deep you
	 * wish to traverse. The topmost node counts as a level.
	 */
	private List<LinkNode> getChildren(LinkNode node, int levels,
			int currentLevel) {
		if (currentLevel >= levels)
			return new ArrayList<LinkNode>();
		else {
			List<LinkNode> children = new ArrayList<LinkNode>();

			String label = node.getDataValueByName("label");
			String name = node.getName();

			String query = "match (n:`" + label + "` {name: '" + name
					+ "'})-->(m) return distinct m";
			for (Map<String, Object> result : runQuery(query)) {
				Node childNode = (Node) result.get("m");
				LinkNode child = new LinkNode();
				int nextLevel = currentLevel + 1;

				String childLabel = getLabels(childNode);
				String childName = childNode.getProperty(NAME).toString();

				child.setId(childLabel + childName);
				child.setName(childName);
				child.addData(new NameValuePair("label", childLabel));
				child.addData(new NameValuePair("childCount", getChildCount(
						getLabels(childNode),
						childNode.getProperty(NAME).toString())));
				child.setChildren(getChildren(child, levels, nextLevel));

				children.add(child);
			}

			// Sort the children by name
			Collections.sort(children, new Comparator<LinkNode>() {
				@Override
				public int compare(LinkNode one, LinkNode two) {
					return one.getName().compareTo(two.getName());
				}
			});

			return children;
		}
	}

	private String getIsGap(Node node) {
		String label = getLabels(node);
		String metadataGapQuery = "match (n:Metadata {name:'" + label
				+ "'})-[{required: true}]->(m:Metadata) return n, m";

		for (Map<String, Object> row : runQuery(metadataGapQuery)) {
			Node requiredNode = (Node) row.get("m");

			String requiredNodeLabel = requiredNode.getProperty("name")
					.toString();
			String requiredQuery = "match (n)-->(m:`" + requiredNodeLabel
					+ "`) where id(n)=" + node.getId() + " return count(m)";

			if (runQuery(requiredQuery).single().get("count(m)").equals(0))
				return "true";
		}

		return "false";
	}

	private Node getNodeByName(String nodeName) {
		String query = "match (n) where n.name='" + nodeName + "' return n";
		Map<String, Object> result = runQuery(query).singleOrNull();

		if (result != null)
			return (Node) result.get("n");

		return null;
	}
}
