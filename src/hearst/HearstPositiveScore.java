package hearst;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HearstPositiveScore {

	public static void main(String[] args) throws IOException {
		Map<String, Double> firstScoreMap=new HashMap<String, Double>();
		Map<String, Double> secondScoreMap=new HashMap<String, Double>();
		double firstTotal=0;
		double secondTotal=0;
		BufferedReader br=new BufferedReader(new FileReader(new File("hearst/hearstOneStat.txt")));
		String line;
		while ((line=br.readLine())!=null) {
			String[] items=line.split("\t");
			String entity=items[0];
			String cat=items[1];
			double count=Double.parseDouble(items[2]);
			firstTotal+=count;
			firstScoreMap.put(entity+"\t"+cat, count);
		}
		br.close();
		br=new BufferedReader(new FileReader(new File("hearst/hearstTwoStat.txt")));
		while ((line=br.readLine())!=null) {
			String[] items=line.split("\t");
			String entity=items[0];
			String cat=items[1];
			double count=Double.parseDouble(items[2]);
			secondTotal+=count;
			secondScoreMap.put(entity+"\t"+cat, count);
		}
		br.close();
		PrintWriter pw=new PrintWriter("hearst/positiveScore.txt");
		Set<String> totalPairSet=new HashSet<String>();
		totalPairSet.addAll(firstScoreMap.keySet());
		totalPairSet.addAll(secondScoreMap.keySet());
		for (String s:totalPairSet) {
			double firstScore=0;
			double secondScore=0;
			if (firstScoreMap.containsKey(s)) {
				firstScore=firstScoreMap.get(s)/firstTotal*100;
			}
			if (secondScoreMap.containsKey(s)) {
				secondScore=secondScoreMap.get(s)/secondTotal*100;
			}
			System.out.println(s+"\t"+firstScore+"\t"+secondScore);
			pw.println(s+"\t"+firstScore+"\t"+secondScore);
			pw.flush();
		}
		pw.close();
	}

}
