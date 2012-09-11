import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Client {
	
	//Assign directions to numbers
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	//Static variables
	public static CustGraph graph;
	public static String startLoc;
	public static ArrayList<String> goalLocs;
	public static boolean atGoal;
	
	//Methods
	/**
	 * Builds a graph from a board represented by an array of strings, where
	 * each string is the same size. Returns the graph.
	 * @param board
	 * @return newGraph
	 */
	private static CustGraph buildGraph(String[] board) {
		System.out.println("Building graph...");
		
		goalLocs = new ArrayList<String>();
		atGoal = false;
		ArrayList<Tile> locationList = new ArrayList<Tile>();
		HashMap<String,Integer> locToIndex = new HashMap<String,Integer>();
		
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length(); j++) {
				//Create a tile if not a wall, box, or box on goal
				char tileType = board[i].charAt(j);				
				if (tileType == '#' || tileType == '$' || tileType == '*') continue;
				String location = i+","+j;
				Tile tile = new Tile(location, tileType);
				
				if (tileType == '@') startLoc = location;
				if (tileType == '.') goalLocs.add(location);
				if (tileType == '+') atGoal = true;
				
				//Add neighbor information to tile
				//Check left tile
				if (j > 0) {
					char lTile = board[i].charAt(j-1);
					if (lTile != '#' && lTile != '$' && lTile != '*') {
						if (tile.firstNeighbor == null) {
							tile.firstNeighbor = new Neighbor(i+","+(j-1), LEFT, null);
						}
						else {
							Neighbor tmp = tile.firstNeighbor;
							tile.firstNeighbor = new Neighbor(i+","+(j-1), LEFT, tmp);
						}
					}
				}
				//Check right tile
				if (j < board[i].length()-1) {
					char rTile = board[i].charAt(j+1);
					if (rTile != '#' && rTile != '$' && rTile != '*') {
						if (tile.firstNeighbor == null) {
							tile.firstNeighbor = new Neighbor(i+","+(j+1), RIGHT, null);
						}
						else {
							Neighbor tmp = tile.firstNeighbor;
							tile.firstNeighbor = new Neighbor(i+","+(j+1), RIGHT, tmp);
						}
					}
				}
				//Check tile above
				if (i > 0) {
					char uTile = board[i-1].charAt(j);
					if (uTile != '#' && uTile != '$' && uTile != '*') {
						if (tile.firstNeighbor == null) {
							tile.firstNeighbor = new Neighbor((i-1)+","+j, UP, null);
						}
						else {
							Neighbor tmp = tile.firstNeighbor;
							tile.firstNeighbor = new Neighbor((i-1)+","+j, UP, tmp);
						}
					}
				}
				//Check tile below
				if (i < board.length-1) {
					char dTile = board[i+1].charAt(j);
					if (dTile != '#' && dTile != '$' && dTile != '*') {
						if (tile.firstNeighbor == null) {
							tile.firstNeighbor = new Neighbor((i+1)+","+j, DOWN, null);
						}
						else {
							Neighbor tmp = tile.firstNeighbor;
							tile.firstNeighbor = new Neighbor((i+1)+","+j, DOWN, tmp);
						}
					}
				}
				
				//Add complete tile to locationList and hash it <location, listIndex>
				locationList.add(tile);
				locToIndex.put(location, locationList.size()-1);
			}
		}
		CustGraph newGraph = new CustGraph(locationList, locToIndex);
		return newGraph;
	}
	
	//Almost but not quite Dijkstra's algorithm
	public static String path(Tile source, Tile goal) {
		int goalIndex, ptr;
		Tile v;
		Neighbor w;
		Queue q = new Queue();								//Fringe
		Tile[] path = new Tile[graph.adjList.size()];		//Path to goal
		int[] distances = new int[graph.adjList.size()];	//Distances from goal
		
		//Place all vertices in unseen set by initializing all distances to infinity 
		for (int i = 0; i < graph.adjList.size(); i++)
			distances[i] = Integer.MAX_VALUE;
		
		//Put source vertex v_s from unseen to done set by making distance 0
		goalIndex = graph.locToIndex.get(goal.location);
		q.enqueue(graph.adjList.get(goalIndex));
		distances[goalIndex] = 0;
		while (!q.isEmpty()) {
			v = q.dequeue(); //v moved from fringe to done set
			w = v.firstNeighbor;
			//For each neighbor of v_s, transfer it to the fringe by setting 
			//d(w) = weight(v_s, w) 
			while (w != null) {
				int neighborIndex = graph.locToIndex.get(w.location);
				//If neighbor w in unseen set, set d(w) = weight(v_s, w) and move it to fringe
				if (distances[neighborIndex] == Integer.MAX_VALUE) {
					distances[neighborIndex] = distances[graph.locToIndex.get(v.location)] + 1;
					path[neighborIndex] = v;
					//Move to fringe
					q.enqueue(graph.adjList.get(neighborIndex));
				}
				w = w.nextNeighbor;
			}
		}
		
		//Return null if there is no path between source and goal
		if (distances[graph.locToIndex.get(source.location)] == Integer.MAX_VALUE)
			return null;
		
		//Start from source
		ptr = graph.locToIndex.get(source.location);
		String solution = " ";
		while (goal.location != graph.adjList.get(ptr).location) {
			Tile temp = graph.adjList.get(ptr);
			Neighbor tempNeighbor = temp.firstNeighbor;
			while (tempNeighbor.location.compareToIgnoreCase(path[ptr].location) != 0)
				tempNeighbor = tempNeighbor.nextNeighbor;
			
			solution = solution + tempNeighbor.orientation + " ";
			ptr = graph.locToIndex.get(path[ptr].location);
		}
		
		solution.trim();
		System.out.println("solution: " + solution);
		return solution;
	}
	
	//Main
	public static void main(String[] pArgs) {
		
		if (pArgs.length < 3) {
			System.out.println("usage: java Client host port boardnum");
			return;
		}
	
		try {
			//Don't know what this stuff does
			Socket lSocket = new Socket(pArgs[0], Integer.parseInt(pArgs[1]));
			PrintWriter lOut = new PrintWriter(lSocket.getOutputStream());
			BufferedReader lIn = new BufferedReader(new InputStreamReader(lSocket.getInputStream()));
            lOut.println(pArgs[2]);
            lOut.flush();
            String lLine = lIn.readLine();

            //Read number of rows
            int boardHeight = Integer.parseInt(lLine);

            //Read each row + store board
            String[] board = new String[boardHeight];
            for (int i = 0; i < boardHeight; i++) {
                lLine=lIn.readLine();
                board[i] = lLine;
                //Print board
                System.out.println(lLine);
            }
            
            //Convert board to a graph
            graph = buildGraph(board);
            System.out.println("Graph built");
            //graph.printGraph();
            
            //Find path from player to goal
            String solution = "";
            for (int i = 0; i < goalLocs.size(); i++) {
    //--------- START AT GOAL CASE HANDLED HERE --------------------------------------------
            	if (atGoal) break;
            	int startInd = graph.locToIndex.get(startLoc);
            	int goalInd = graph.locToIndex.get(goalLocs.get(i));
            	
            	solution = path(graph.adjList.get(startInd), graph.adjList.get(goalInd));
            	if (solution != null) break;
            }
            
            //String lMySol="U R R U U L D L L U L L D R R R R L D D R U R U D L L U R";
            //these formats are also valid:
            //String lMySol="URRUULDLLULLDRRRRLDDRURUDLLUR";
            //String lMySol="0 3 3 0 0 2 1 2 2 0 2 2 1 3 3 3 3 2 1 1 3 0 3 0 1 2 2 0 3";

            //send the solution to the server
            lOut.println(solution);
            lOut.flush();
    
            //read answer from the server
            lLine=lIn.readLine();
    
            System.out.println(lLine);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
