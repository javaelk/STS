package uw.star.rts.util;
import java.nio.*;
import java.nio.file.*;

/**
 * this class wraps Linux diff utility into an API
 * @author wliu
 *
 */
public class diff {
	 // diff {1} {2} |egrep -vw "\---|>|<"
	diff(Path originalFile,Path newFile){	
	}

	
	/**
	 *  e.g.8,10c14 returns 8,9,10
	 * @return a list of line numbers in original file that have changed.
	 */
	//int[] changedLinesInOriginalFile(){	}
	
	//int[] deletedLinesInOriginalFile(){	}

}
