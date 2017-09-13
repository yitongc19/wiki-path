import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ArrayList;
import java.util.*;

/**
* PathFinder loads data into a graph and then finds the shortest path between 
* the two given nodes (with or without an intermediate node).
*
* @author Yitong Chen
* @author Anton Nagy
*/
public class PathFinder {
    // The graph containing all nodes and edges.
    private MysteryUnweightedGraphImplementation wikiGraph;
    
    // The list holding all the nodes.
    List<String> nodeList;
    
    // Map linking the actual name to the node ID.
    private Map<String, Integer> labelMap;
    
    // Map linking the node ID to the actual name.
    private Map<Integer, String> nodeMap;
    
    
    /**
    * Constructs a PathFinder that represents the graph with nodes (vertices) specified as in
    * nodeFile and edges specified as in edgeFile.
    * @param nodeFile name of the file with the node names
    * @param edgeFile name of the file with the edge names
    */
    public PathFinder(String nodeFile, String edgeFile) {
        wikiGraph = new MysteryUnweightedGraphImplementation();
        labelMap = new HashMap<String, Integer>();
        nodeMap = new HashMap<Integer, String>();
        nodeList = new ArrayList<String>();
        
        loadNode(nodeFile);
        loadEdge(edgeFile);
    }
    
    /**
    * Returns the length of the shortest path from node1 to node2. If no path exists,
    * returns -1. If the two nodes are the same, the path length is 0.
    * @param node1 name of the starting article node
    * @param node2 name of the ending article node
    * @return length of shortest path
    */
    public int getShortestPathLength(String node1, String node2) {
        List<String> path = getShortestPath(node1, node2);
        if (path.size() == 0) {
            return -1;
        } else {
            return (path.size() - 1);
        }
    }
    
    /**
    * Returns the length of the shortest path from node1 to node2 through node3. 
    * If no path exists, returns -1. If the two nodes are the same, the path length is 0.
    * @param node1 name of the starting article node
    * @param node2 name of the ending article node
    * @return length of shortest path
    */
    public int getShortestPathLength(String node1, String intermediateNode, String node2) {
        List<String> path = getShortestPath(node1, intermediateNode, node2);
        if (path.size() == 0) {
            return -1;
        } else {
            return (path.size() - 1);
        }
    }
    
    /**
     * Returns a shortest path from node1 to node2, represented as list that has node1 at
     * position 0, node2 in the final position, and the names of each node on the path
     * (in order) in between. If the two nodes are the same, then the "path" is just a
     * single node. If no path exists, returns an empty list.
     * @param node1 name of the starting article node
     * @param node2 name of the ending article node
     * @return list of the names of nodes on the shortest path
     */
    public List<String> getShortestPath(String node1, String node2) {
        // Getting and storing the start and finish IDs.
        int startid = labelMap.get(node1);
        int finishid = labelMap.get(node2);
        
        boolean done = false;
        
        // A Queue to hold vertices as they are visited.
        Queue<Integer> vertexQueue = new LinkedList<Integer>();
        // A List to keep track of the visited vertices.
        List<Integer> visitedList = new ArrayList<Integer>();
        // A List for the shortest path using IDs.
        List<Integer> pathInt = new ArrayList<Integer>();
        // The same list with the actual names instead of the IDs.
        List<String> path = new ArrayList<String>();
        // A Map linking the nodes with their predecessor. 
        Map<Integer, Integer> predecessorMap = new HashMap<Integer, Integer>();
        
        visitedList.add(startid);
        vertexQueue.add(startid);
        
        // If the start and end nodes are the same, add the node to the shortest path list.
        // Used breadth-first traversal to traverse the graph, storing the predecessors of
        // each node as we go through until we find the finish node or we make it through 
        // the whole graph.
        if (node1.equals(node2)) {
            path.add(node1);
        } else {
            if (!node1.equals(node2)) {
                while (!done && !vertexQueue.isEmpty()) {
                    Integer frontVertex = vertexQueue.poll();
                    Iterable<Integer> neighborsIterable = wikiGraph.getNeighbors(frontVertex);
                    Iterator<Integer> neighbors = neighborsIterable.iterator();

                    while (!done && neighbors.hasNext()) {
                        Integer nextNeighbor = neighbors.next();
                        if (!visitedList.contains(nextNeighbor)) {
                            visitedList.add(nextNeighbor);
                            predecessorMap.put(nextNeighbor, frontVertex);
                            vertexQueue.add(nextNeighbor);
                        }
                        if (nextNeighbor == finishid) {
                            done = true;
                        }
                    }
                }
            }
        }
        // Constructs the shortest path by traversing from predecessor to predecessor until
        //  we reach the start node.
        int tempID = finishid;
        while (predecessorMap.get(tempID) != null) {
            pathInt.add(0, tempID);
            tempID = predecessorMap.get(tempID);            
        }
        if (!pathInt.isEmpty()) {
            pathInt.add(0, startid);
        }
        
        // We convert the shortest path from ID format to actual name format.
        for (int i = 0; i < pathInt.size(); i ++) {
            String readableName = nodeMap.get(pathInt.get(i));
            path.add(readableName);
        }

        return path;         
    }
        
    /**
    * Returns a shortest path from node1 to node2 that includes the node intermediateNode.
    * This may not be the absolute shortest path between node1 and node2, but should be 
    * a shortest path given the constraint that intermediateNodeAppears in the path. If all
    * three nodes are the same, the "path" is just a single node.  If no path exists, returns
    * an empty list.
    * @param node1 name of the starting article node
    * @param node2 name of the ending article node
    * @return list that has node1 at position 0, node2 in the final position, and the names of each node 
    *      on the path (in order) in between. 
    */             
    public List<String> getShortestPath(String node1, String intermediateNode, String node2) {
        // Finds the shortest path between node1 and intermediateNode.
        List<String> firstHalf = getShortestPath(node1, intermediateNode);
        // Finds the shortest path between intermediateNode and node2.
        List<String> secondHalf = getShortestPath(intermediateNode, node2);
        // Combining the firstHalf and secondHalf together.
        List<String> finalList = null;

        if (!(firstHalf.isEmpty() || secondHalf.isEmpty())) {
            finalList = firstHalf.subList(0, firstHalf.size() - 1);
            finalList.addAll(secondHalf);
        } else {
            finalList = new ArrayList<String>();
        }

        return finalList;
    }
    
    /**
    * Loads the file of nodes and creates vertices for all of the entries.
    * @param nodeFilePath name of file of nodes.
    * @throws FileNotFoundException if the file does not exist
    * @throws UnsupportedEncodingException if a line cannot be decoded
    */
    public void loadNode(String nodeFilePath) {
        File nodeFile = null;
        Scanner scanner = null;
        try {
            nodeFile = new File(nodeFilePath);
            scanner = new Scanner(nodeFile);
        } catch (FileNotFoundException e) {
            System.out.println(e);
            System.exit(1);
        }
        
        // reads through the file line by line.
        while (scanner.hasNext()) {
            String articleName = scanner.nextLine();
            // skips the comment lines and the empty lines
            if (articleName.length() > 0 && articleName.charAt(0) != '#') {
                String readableName = null;
                try {
                    readableName = java.net.URLDecoder.decode(articleName, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    System.out.println(e);
                    System.exit(1);
                }
                int id = wikiGraph.addVertex();
                // stores information into the two instance variable maps.
                labelMap.put(readableName, id);
                nodeMap.put(id, readableName);
                nodeList.add(readableName);
            }
        }
    }
    
    /**
    * Loads the file of edges and creates edges between vertices that correspond 
    * to path between entries.
    * @param edgeFilePath name of the file of edges
    * @throws FileNotFoundException if the file does not exist
    * @throws UnsupportedEncodingException if a line cannot be decoded
    */
    public void loadEdge(String edgeFilePath) {
        File edgeFile = null;
        Scanner scanner = null;
        try {
            edgeFile = new File(edgeFilePath);
            scanner = new Scanner(edgeFile);
        } catch (FileNotFoundException e) {
            System.out.println(e);
            System.exit(1);
        }
        
        // reads through the file line by line and creates the edges.
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String sourceName = "";
            String targetName = "";
            
            // skips the comment lines and the empty lines
            if (line.length() > 0 && line.charAt(0) != '#') {
                String[] lineList = line.split("\\s");
                sourceName = lineList[0];
                targetName = lineList[1];
            
                String sourceReadable = null;
                String targetReadable = null;
                
                try {
                    sourceReadable = java.net.URLDecoder.decode(sourceName, "UTF-8");
                    targetReadable = java.net.URLDecoder.decode(targetName, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    System.out.println(e);
                    System.exit(1);
                }
                int sourceid = labelMap.get(sourceReadable);
                int targetid = labelMap.get(targetReadable);
                
                wikiGraph.addEdge(sourceid, targetid);
            }
        }
    }
    
    /**
    * Generates random node from the node file
    * @return random node
    */
    public String getRandomNode() {
        Random randomGenerator = new Random();
        int randomIndex = randomGenerator.nextInt(nodeList.size());
        String randomNode = nodeList.get(randomIndex);

        return randomNode;
    }
    
    /**
    * Takes 2 command line arguments, one for the file with the article names and one
    * for the file with the links.
    */
    public static void main(String[] args) {
        String nodeFilePath = null;
        String edgeFilePath = null;
        if (args.length <= 1) {
            System.out.println("There are not enough command line arguments!");
            System.exit(1);
        } else if (args.length <= 3) {
            nodeFilePath = args[0];
            edgeFilePath = args[1];
            PathFinder finder = new PathFinder(nodeFilePath, edgeFilePath);
            
            String node1 = finder.getRandomNode();
            String node2 = finder.getRandomNode();
            String intermediateNode = "";
            
            List<String> shortestPath = new ArrayList<String>();
            int shortestPathLength = 0;
            
            if (args.length == 2) {
                shortestPath = finder.getShortestPath(node1, node2);
                shortestPathLength = finder.getShortestPathLength(node1, node2);
            } else {
                if (args[2].equals("useIntermediateNode")) {
                    intermediateNode = finder.getRandomNode();
                    shortestPath = finder.getShortestPath(node1, intermediateNode, node2);
                    shortestPathLength = finder.getShortestPathLength(node1, intermediateNode, node2);
                }
            }
            
            // displays the result
            if (args.length == 2) {
                System.out.println("Path from " + node1 + " to " + node2 + 
                                   ", length = " + shortestPathLength);
            } else {
                System.out.println("Path from " + node1 + " to " + node2 + " through " +
                                   intermediateNode + ", length = " + shortestPathLength);
            }
            if (shortestPathLength > 0) {
                for (int i = 0 ; i < shortestPath.size() - 1; i ++) {
                    System.out.print(shortestPath.get(i) + " ---> ");
                }   
                System.out.println(shortestPath.get(shortestPath.size() - 1));
            } else if (shortestPathLength == 0) {
                System.out.println("The start node and the end node are the same!");
            } else {
                System.out.println("The path doesn't exist.");
            }
        }
    }  
}