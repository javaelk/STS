package uw.star.rts.goal;

import java.nio.file.*;
import java.util.*;
/**
 * Select a set of goals from all available goals
 * @author wliu
 *
 */
public class STSGoalSelection {
	
	STSGoal availableGoals;
	
	public STSGoalSelection(STSGoal availableGoals){
		this.availableGoals = availableGoals;
	}
	
	public List<STSGoal> select(Path userSelection){
//TODO: perform user selection based on user input, for now , assume user selects all goals
		List<STSGoal> result = new ArrayList<>();
		result.add(availableGoals);
		return result;
	}
}
