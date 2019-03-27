package hearst;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HearstNegativeScore {

	public static void main(String[] args) throws IOException {
		Map<String, Double> pairMap=new HashMap<String, Double>();
		Map<String, Double> singleMap=new HashMap<String, Double>();
		double pairTotal=0;
		double singleTotal=0;
		BufferedReader br=new BufferedReader(new FileReader(new File("hearst/hearstTwoStatSingle.txt")));
		String line;
		while ((line=br.readLine())!=null) {
			String[] items=line.split("\t");
			String entity=items[0];
			double count=Double.parseDouble(items[1]);
			singleTotal+=count;
			singleMap.put(entity, count);
		}
		br.close();
		br=new BufferedReader(new FileReader(new File("hearst/hearstTwoStatPair.txt")));
		while ((line=br.readLine())!=null) {
			String[] items=line.split("\t");
			String entity=items[0];
			String entity1=items[1];
			double count=Double.parseDouble(items[2]);
			pairTotal+=count;
			pairMap.put(entity+"\t"+entity1, count);
		}
		br.close();
		
		PrintWriter pw=new PrintWriter("hearst/negativeScore.txt");
		Set<String> pairSet=pairMap.keySet();
		for (String s:pairSet) {
			String[] items=s.split("\t");
			String entity=items[0];
			String entity1=items[1];
			double pairCount=pairMap.get(s);
			pairCount=pairCount/pairTotal;
			double e1Score=0.00001;
			double e2Score=0.00001;
			if (singleMap.containsKey(entity))
				e1Score=singleMap.get(entity);
			if (singleMap.containsKey(entity1))
				e2Score=singleMap.get(entity1);
			e1Score=e1Score/singleTotal;
			e2Score=e2Score/singleTotal;
			double pmi=Math.log(pairCount/(e1Score*e2Score));
			pw.println(s+"\t"+pmi);
			pw.flush();
		}
		pw.close();
	}

}
