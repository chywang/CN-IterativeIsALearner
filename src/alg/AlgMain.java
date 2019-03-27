package alg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Jama.Matrix;
import weka.classifiers.functions.LinearRegression;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSink;

public class AlgMain {

	private int currentIter = 0;
	private static final double theta = 1;// threshold to select positive
											// instances
	private static final int numOfCluster = 5;
	private static final int numOfSelection = 500;// number of positive
													// instances to be selected
													// in each iteration
	private Map<String, float[]> vec = new HashMap<String, float[]>();
	private Set<String> blackList = new HashSet<String>();

	private final int numOfAttr = 50;
	private final String w2vFile = "word_vectors.txt";
	private final String positiveFile = "positive.txt";
	private final String unlabeledFile = "unlabeled.txt";
	private final String hearstPosScoreFile = "hearst/positiveScore.txt";
	private final String hearstNegScoreFile = "hearst/negativeScore.txt";

	public static void main(String[] args) throws Exception {

		if (args.length != 7) {
			System.out.println("argments input error!");
			return;
		}
		int iterationNumber = Integer.parseInt(args[0]);
		int numOfAttr = Integer.parseInt(args[1]);
		String w2vFile = args[2];
		String positiveFile = args[3];
		String unlabeledFile = args[4];
		String hearstPosScoreFile = args[5];
		String hearstNegScoreFile = args[6];

		AlgMain alg = new AlgMain(numOfAttr, w2vFile, positiveFile, unlabeledFile, hearstPosScoreFile,
				hearstNegScoreFile);
		if (iterationNumber == 1)
			alg.init();
		else if (iterationNumber > 1) {
			alg.init();
			alg.runIterate(iterationNumber - 1);
		}
	}

	public AlgMain(int numOfAttr, String w2vFile, String positiveFile, String unlabeledFile, String hearstPosScoreFile,
			String hearstNegScoreFile) throws IOException {
		numOfAttr = this.numOfAttr;
		w2vFile = this.w2vFile;
		positiveFile = this.positiveFile;
		unlabeledFile = this.unlabeledFile;
		hearstPosScoreFile = this.hearstPosScoreFile;
		hearstNegScoreFile = this.hearstNegScoreFile;

		blackList = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(new File("blacklist.txt")));
		String line;
		while ((line = br.readLine()) != null) {
			blackList.add(line);
		}
		br.close();
	}

	public void init() throws Exception {
		String initTrainDataPath = "positive.txt";
		String initUnlabeledDataPath = "unlabeled.txt";
		String initalArffPath = "iter" + currentIter + "/cluster.arff";
		String initalModelPath = "iter" + currentIter + "/cluster.model";
		String initalMatrixFolderPath = "iter" + currentIter;
		String initalCanPosDataPath = "iter" + currentIter + "/canPos.txt";
		String initalValidatePosDataPath = "iter" + currentIter + "/validatePos.txt";

		File file = new File("iter" + currentIter);
		if (!file.exists())
			file.mkdir();

		loadWord2VecModel();
		clusterTrainData(initTrainDataPath, initalArffPath, initalModelPath);
		multiLinearProjTrain(initalModelPath, initTrainDataPath, initalMatrixFolderPath);
		extractPositiveByModel(initalModelPath, initalMatrixFolderPath, initUnlabeledDataPath, initalCanPosDataPath,
				theta);
		relationSelection(numOfSelection, initalCanPosDataPath, initalValidatePosDataPath);

		String newTrainDataPath = "iter" + currentIter + "/positive.txt";
		String newUnlabeledDataPath = "iter" + currentIter + "/unlabeled.txt";
		updatePositiveDataset(initTrainDataPath, initalValidatePosDataPath, newTrainDataPath);
		updateUnlabeledDataset(initUnlabeledDataPath, initalValidatePosDataPath, newUnlabeledDataPath);

	}

	public void runIterate(int numOfIteration) throws Exception {
		int oldIter = currentIter;
		while (currentIter < numOfIteration + oldIter) {
			currentIter++;
			File file = new File("iter/iter" + currentIter);
			if (!file.exists())
				file.mkdir();

			String trainDataPath = "iter" + (currentIter - 1) + "/positive.txt";
			String unlabeledDataPath = "iter" + (currentIter - 1) + "/unlabeled.txt";
			String arffPath = "iter" + currentIter + "/cluster.arff";
			String modelPath = "iter" + currentIter + "/cluster.model";
			String matrixFolderPath = "iter" + currentIter;
			String canPosDataPath = "iter" + currentIter + "/canPos.txt";
			String validatePosDataPath = "iter" + currentIter + "/validatePos.txt";

			clusterTrainData(trainDataPath, arffPath, modelPath);
			multiLinearProjTrain(modelPath, trainDataPath, matrixFolderPath);
			extractPositiveByModel(modelPath, matrixFolderPath, unlabeledDataPath, canPosDataPath, theta);
			relationSelection(numOfSelection, canPosDataPath, validatePosDataPath);

			String newTrainDataPath = "iter" + currentIter + "/positive.txt";
			String newUnlabeledDataPath = "iter" + currentIter + "/unlabeled.txt";
			updatePositiveDataset(trainDataPath, validatePosDataPath, newTrainDataPath);
			updateUnlabeledDataset(unlabeledDataPath, validatePosDataPath, newUnlabeledDataPath);
		}
	}

	private void loadWord2VecModel() throws IOException {
		System.out.println("load word2vec begin");
		BufferedReader br = new BufferedReader(new FileReader(new File(w2vFile)));
		String line;
		while ((line = br.readLine()) != null) {
			String[] items = line.split(" ");
			String word = items[0];
			float[] vector = new float[items.length - 1];
			for (int i = 0; i < vector.length; i++)
				vector[i] = Float.parseFloat(items[i + 1]);
			vec.put(word, vector);
		}
		br.close();
		System.out.println("word2vec model load!");
	}

	private void clusterTrainData(String dataPath, String arffPath, String modelPath) throws Exception {
		System.out.println("now cluster data!");
		FastVector attributes = new FastVector();
		for (int i = 0; i < numOfAttr; i++) {
			Attribute numeric = new Attribute("attr" + String.valueOf(i));
			attributes.addElement(numeric);
		}
		Instances dataset = new Instances("cluter", attributes, 0);
		BufferedReader br = new BufferedReader(new FileReader(new File(dataPath)));
		String line;
		while ((line = br.readLine()) != null) {
			String[] items = line.split("\t");
			String downer = items[0];
			String upper = items[1];
			double[] attr = getClusterAttrs(downer, upper);
			if (Math.random() < 0.25) {
				Instance instance = new Instance(1, attr);
				dataset.add(instance);
			}
		}
		br.close();
		DataSink.write(arffPath, dataset);
		System.out.println("dataset created!");

		SimpleKMeans model = new SimpleKMeans();
		model.setNumClusters(numOfCluster);
		model.buildClusterer(dataset);
		SerializationHelper.write(modelPath, model);
		System.out.println("cluster done!");
	}

	private double[] getClusterAttrs(String word1, String word2) {
		float[] f1 = vec.get(word1);
		float[] f2 = vec.get(word2);
		if (f1 == null)
			System.out.println(word1);
		if (f2 == null)
			System.out.println(word2);
		double[] d = new double[f1.length];
		for (int i = 0; i < f1.length; i++)
			d[i] = f2[i] - f1[i];
		return d;
	}

	private void multiLinearProjTrain(String modelPath, String dataPath, String matrixFolderPath) throws Exception {
		System.out.println("now load cluster model!");
		SimpleKMeans kMeans = (SimpleKMeans) SerializationHelper.read(modelPath);
		FastVector attributes = new FastVector();
		for (int i = 0; i < numOfAttr; i++) {
			Attribute numeric = new Attribute("attr" + String.valueOf(i));
			attributes.addElement(numeric);
		}
		Instances clusterDataset = new Instances("cluter", attributes, 0);
		System.out.println("cluster model loaded!");

		Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
		BufferedReader br = new BufferedReader(new FileReader(new File(dataPath)));
		String line;
		while ((line = br.readLine()) != null) {
			String[] items = line.split("\t");
			String downer = items[0];
			String upper = items[1];
			if (Math.random() > 0.5)
				continue;
			double[] d = getClusterAttrs(downer, upper);
			Instance ins = new Instance(1, d);
			ins.setDataset(clusterDataset);
			int cluster = kMeans.clusterInstance(ins);
			if (!map.containsKey(cluster))
				map.put(cluster, new ArrayList<String>());
			List<String> list = map.get(cluster);
			list.add(line);
			map.put(cluster, list);
		}
		br.close();

		Set<Integer> clusterSet = map.keySet();
		for (int m : clusterSet) {
			PrintWriter pw = new PrintWriter(matrixFolderPath + "/" + "matrix" + m + ".txt");
			for (int k = 0; k < numOfAttr; k++) {
				attributes = new FastVector();
				for (int i = 0; i < numOfAttr; i++) {
					Attribute numeric = new Attribute("attr" + String.valueOf(i));
					attributes.addElement(numeric);
				}
				Attribute numeric = new Attribute("y_value");
				attributes.addElement(numeric);
				Instances dataset = new Instances("reg", attributes, 0);
				dataset.setClassIndex(dataset.numAttributes() - 1);

				List<String> list = map.get(m);
				for (String line1 : list) {
					String[] items = line1.split("\t");
					String downer = items[0];
					String upper = items[1];
					double[] attr = getAttrs(downer, upper, k);
					Instance instance = new Instance(1, attr);
					dataset.add(instance);
				}
				LinearRegression model = new LinearRegression();
				model.buildClassifier(dataset);
				double[] coff = model.coefficients();
				System.out.println("k=" + k);
				System.out.println("length=" + coff.length);
				for (int i = 0; i < coff.length; i++) {
					if (i == coff.length - 2)
						continue;
					pw.print(coff[i] + "\t");
				}
				pw.println();
				pw.flush();
			}
			pw.close();
		}
	}

	private double[] getAttrs(String word1, String word2, int index) {
		float[] f1 = vec.get(word1);
		double reg = vec.get(word2)[index];
		double[] d = new double[f1.length + 1];
		for (int i = 0; i < f1.length; i++)
			d[i] = f1[i];
		d[f1.length] = reg;
		return d;
	}

	private void extractPositiveByModel(String modelPath, String matrixFolderPath, String unlabledDataPath,
			String canPosDataPath, double theta) throws Exception {
		System.out.println("now load cluster model!");
		SimpleKMeans kMeans = (SimpleKMeans) SerializationHelper.read(modelPath);
		FastVector attributes = new FastVector();
		for (int i = 0; i < numOfAttr; i++) {
			Attribute numeric = new Attribute("attr" + String.valueOf(i));
			attributes.addElement(numeric);
		}
		Instances clusterDataset = new Instances("cluter", attributes, 0);
		System.out.println("cluster model loaded!");

		Map<Integer, Matrix> mMatrixMap = new HashMap<Integer, Matrix>();
		Map<Integer, Matrix> bMatrixMap = new HashMap<Integer, Matrix>();
		for (int k = 0; k < numOfCluster; k++) {
			// load matrix
			double[][] m = new double[numOfAttr][numOfAttr];
			double[][] b = new double[numOfAttr][1];

			BufferedReader br = new BufferedReader(
					new FileReader(new File(matrixFolderPath + "/" + "matrix" + k + ".txt")));
			String line;
			int lineCount = 0;
			while ((line = br.readLine()) != null) {
				String[] items = line.split("\t");
				for (int i = 0; i < numOfAttr; i++) {
					m[lineCount][i] = Double.parseDouble(items[i]);
				}
				b[lineCount][0] = Double.parseDouble(items[numOfAttr]);
				lineCount++;
			}
			br.close();
			Matrix mMatrix = new Matrix(m);
			mMatrixMap.put(k, mMatrix);
			Matrix bMatrix = new Matrix(b);
			bMatrixMap.put(k, bMatrix);
			System.out.println("matrix load!");
		}

		System.out.println("theta:\t" + theta);
		BufferedReader br = new BufferedReader(new FileReader(new File(unlabledDataPath)));
		PrintWriter pw = new PrintWriter(canPosDataPath);
		String line;
		while ((line = br.readLine()) != null) {

			String[] items = line.split("\t");
			String entity = items[0];
			String cat = items[1];

			double[] d = getClusterAttrs(entity, cat);
			Instance ins = new Instance(1, d);
			ins.setDataset(clusterDataset);
			int cluster = kMeans.clusterInstance(ins);

			Matrix mMatrix = mMatrixMap.get(cluster);
			Matrix bMatrix = bMatrixMap.get(cluster);

			Matrix entityMatrix = getWordMatrix(entity);
			Matrix catMatrix = getWordMatrix(cat);
			Matrix resultMatrix = mMatrix.times(entityMatrix).plus(bMatrix).minus(catMatrix);
			if (getVectorNorm(resultMatrix) < theta && !blackList.contains(cat)) {
				System.out.println(entity + "\t" + cat + "\t" + getVectorNorm(resultMatrix));
				pw.println(entity + "\t" + cat + "\t" + getVectorNorm(resultMatrix));
				pw.flush();
			}
		}
		br.close();
		pw.close();
	}

	public Matrix getWordMatrix(String word) {
		float[] f = vec.get(word);
		double[][] d = new double[f.length][1];
		for (int i = 0; i < f.length; i++)
			d[i][0] = f[i];
		return new Matrix(d);
	}

	public double getVectorNorm(Matrix m) {
		double[][] rm = m.getArray();
		double avg = 0;
		for (int i = 0; i < rm.length; i++) {
			double d = rm[i][0];
			avg += d;
		}
		avg = avg / rm.length;
		double norm = 0;
		for (int i = 0; i < rm.length; i++) {
			double d = rm[i][0];
			norm += Math.pow(d - avg, 2);
		}
		return Math.sqrt(norm);
	}

	private void relationSelection(int numOfSelection, String canPosDataPath, String validatePosDataPath)
			throws IOException {
		Map<String, Double> posWeightMap1 = new HashMap<String, Double>();
		Map<String, Double> posWeightMap2 = new HashMap<String, Double>();
		Map<String, Double> negWeightMap = new HashMap<String, Double>();// order!!

		BufferedReader br = new BufferedReader(new FileReader(new File(hearstPosScoreFile)));
		String line;
		while ((line = br.readLine()) != null) {
			String[] items = line.split("\t");
			String first = items[0];
			String second = items[1];
			double weight1 = Double.parseDouble(items[2]);
			double weight2 = Double.parseDouble(items[3]);
			posWeightMap1.put(first + "\t" + second, weight1);
			posWeightMap2.put(first + "\t" + second, weight2);
		}
		br.close();
		br = new BufferedReader(new FileReader(new File(hearstNegScoreFile)));
		while ((line = br.readLine()) != null) {
			String[] items = line.split("\t");
			String first = items[0];
			String second = items[1];
			double weight = Double.parseDouble(items[2]);
			negWeightMap.put(first + "\t" + second, weight);
		}
		br.close();

		double largestDistance = 0;
		Map<String, Double> distanceMap = new HashMap<String, Double>();
		br = new BufferedReader(new FileReader(new File(canPosDataPath)));
		while ((line = br.readLine()) != null) {
			String[] items = line.split("\t");
			String first = items[0];
			String second = items[1];
			double weight = Double.parseDouble(items[2]);
			distanceMap.put(first + "\t" + second, weight);
			if (weight > largestDistance)
				largestDistance = weight;
		}
		br.close();

		double largestWeight1 = 0;
		double largestWeight2 = 0;
		for (String s : posWeightMap1.keySet()) {
			if (distanceMap.containsKey(s)) {
				double weight = posWeightMap1.get(s);
				if (weight > largestWeight1)
					largestWeight1 = weight;
			}
		}
		for (String s : posWeightMap2.keySet()) {
			if (distanceMap.containsKey(s)) {
				double weight = posWeightMap2.get(s);
				if (weight > largestWeight2)
					largestWeight2 = weight;
			}
		}
		List<CandidateStat> candidateStats = new ArrayList<CandidateStat>();
		for (String s : distanceMap.keySet()) {
			String[] items = s.split("\t");
			String entity = items[0];
			String category = items[1];
			double pos1 = 0;
			if (posWeightMap1.containsKey(entity + "\t" + category))
				pos1 = posWeightMap1.get(entity + "\t" + category) / largestWeight1;
			double pos2 = 0;
			if (posWeightMap2.containsKey(entity + "\t" + category))
				pos2 = posWeightMap2.get(entity + "\t" + category) / largestWeight2;
			double hearstPos = (pos1 + pos2) / 2;
			double dis = distanceMap.get(entity + "\t" + category);
			double modelPos = 1 - dis / largestDistance;
			double posWeight = 0.5 * hearstPos + 0.5 * modelPos;
			double negWeight = 0;
			if (negWeightMap.containsKey(entity + "\t" + category))
				negWeight = negWeightMap.get(entity + "\t" + category);
			if (negWeightMap.containsKey(category + "\t" + entity))
				negWeight = negWeightMap.get(category + "\t" + entity);
			candidateStats.add(new CandidateStat(entity, category, posWeight, negWeight));

		}
		Optimizer optimize = new Optimizer(numOfSelection, 100, candidateStats);
		List<CandidateStat> selected = optimize.generateOptimizedCandidates();
		PrintWriter pw = new PrintWriter(validatePosDataPath);
		for (CandidateStat c : selected) {
			System.out.println(c.getEntity() + "\t" + c.getCategory());
			pw.println(c.getEntity() + "\t" + c.getCategory());
			pw.flush();
		}
		pw.close();
	}

	private void updatePositiveDataset(String oldDataset, String updated, String newDataset) throws IOException {
		List<String> content = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(new File(oldDataset)));
		String line;
		while ((line = br.readLine()) != null) {
			if (!content.contains(line))
				content.add(line);
		}
		br.close();
		br = new BufferedReader(new FileReader(new File(updated)));
		while ((line = br.readLine()) != null) {
			if (!content.contains(line))
				content.add(line);
		}
		br.close();
		PrintWriter pw = new PrintWriter(newDataset);
		for (String s : content) {
			pw.println(s);
			pw.flush();
		}
		pw.close();
	}

	private void updateUnlabeledDataset(String oldDataset, String updated, String newDataset) throws IOException {
		List<String> updatedList = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(new File(updated)));
		String line;
		while ((line = br.readLine()) != null) {
			updatedList.add(line);
		}
		br.close();
		PrintWriter pw = new PrintWriter(newDataset);
		br = new BufferedReader(new FileReader(new File(oldDataset)));
		while ((line = br.readLine()) != null) {
			if (!updatedList.contains(line)) {
				pw.println(line);
				pw.flush();
			}
		}
		br.close();
		pw.close();
	}

}
