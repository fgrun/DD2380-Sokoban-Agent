import java.util.ArrayList;


public class GameState {

	//	Variables
	char[][] board;
	ArrayList<Box> boxes;
	ArrayList<Goal> goals;
	int playerX;
	int playerY;
	
	//	Methods
	/**
	 * calculates Manhattan distance
	 * 
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @return Manhattan distance as int
	 */
	int getDistance(int startX, int startY, int endX, int endY){
		
		//	To-Do
		
	}

	/**
	 * Finds the next box and returns its index in ArrayList<Box> boxes
	 * 
	 * @return The index of the next box as int
	 */
	int findNextBox(){
		
		int distance = Integer.MAX_VALUE;
		int temp = 0;
		int index = 0;
		
		for(int i = 0; i < boxes.size(); i++)
		{
			temp = getDistance(playerX, playerY, boxes.get(i).x, boxes.get(i).y);
			if(temp < distance)
			{
				index = i;
				distance = temp;
			}
		}
		
		return index;
	}

	/**
	 * Finds the next goal and returns its index in ArrayList<Goal>
	 * 
	 * @param x x-value of the starting point
	 * @param y y-value of the starting point
	 * 
	 * @return The index of the next goal as int
	 */
	int findNextGoal(int x, int y){
		
		int distance = Integer.MAX_VALUE;
		int temp = 0;
		int index = 0;
		
		for(int i = 0; i < goals.size(); i++)
		{
			temp = getDistance(x, y, goals.get(i).x, goals.get(i).y);
			if(temp < distance)
			{
				index = i;
				distance = temp;
			}
		}
		
		return index;
	}

	/**
	 * Finds the next goal and returns its index in ArrayList<Goal>
	 * 
	 * @param indexBox The index of the Box that is the starting point
	 * 
	 * @return The index of the next goal as int
	 */
	int findNextGoal(int indexBox){
		
		int x = boxes.get(indexBox).x;
		int y = boxes.get(indexBox).y;
		int distance = Integer.MAX_VALUE;
		int temp = 0;
		int index = 0;
		
		for(int i = 0; i < goals.size(); i++)
		{
			temp = getDistance(x, y, goals.get(i).x, goals.get(i).y);
			if(temp < distance)
			{
				index = i;
				distance = temp;
			}
		}
		
		return index;
	}
	
	/**
	 * Finds the next goal and returns its index in ArrayList<Goal>
	 * 
	 * @return The index of the next goal as int
	 */
	int findNextGoal(){
		
		int distance = Integer.MAX_VALUE;
		int temp = 0;
		int index = 0;
		
		for(int i = 0; i < goals.size(); i++)
		{
			temp = getDistance(playerX, playerY, goals.get(i).x, goals.get(i).y);
			if(temp < distance)
			{
				index = i;
				distance = temp;
			}
		}
		
		return index;
	}
}