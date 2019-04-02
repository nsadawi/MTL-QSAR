package mtl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.AddValues;
import weka.filters.unsupervised.attribute.Remove;
import weka.core.converters.ConverterUtils.DataSource;


public class Utility {

	/**
	 * formats a number
	 * @param n
	 * @return formatted
	 */
	public static String format(Number n) {
		NumberFormat format = DecimalFormat.getInstance();
		format.setRoundingMode(RoundingMode.FLOOR);
		format.setMinimumFractionDigits(0);
		format.setMaximumFractionDigits(2);
		return format.format(n);
	}

	/**
	 * prints out evaluation metrics
	 * @param eval
	 * @param cls
	 * @throws Exception
	 */
	public static void printEvalMetrics(Evaluation eval, Classifier cls) throws Exception{

		System.out.println(format(eval.meanAbsoluteError()) + ";" + 
				format(eval.rootMeanSquaredError())+";"+
				format(eval.relativeAbsoluteError())+";"+
				format(eval.rootRelativeSquaredError())+";"+
				format(eval.correlationCoefficient())+";"+
				format(eval.errorRate()));		
	}
	/**
	 * cross validates the model
	 * @param cls the model
	 * @param trData the training (target) data
	 * @param folds the # of folds to use in CV
	 * @throws Exception
	 */
	public static double[] crossValidateModel(Classifier cls, Instances trData) throws Exception{	
		double[] metrics = new double[6];
		int seed = 1;           // the seed for randomizing the data
		int folds = 10;
		Random rand = new Random(seed);		
		//Initialize evaluation with the train dataset structure:
		Evaluation eval = new Evaluation(trData);
		eval.crossValidateModel(cls, trData, folds, rand);
		//printEvalMetrics(eval,cls);
		metrics[0] = eval.rootMeanSquaredError();
		metrics[1] = eval.meanAbsoluteError(); 		
		metrics[2] = eval.relativeAbsoluteError();
		metrics[3] = eval.rootRelativeSquaredError();
		metrics[4] = eval.correlationCoefficient();
		metrics[5] = eval.errorRate();
		return metrics;
	}
	/**
	 * evaluates model using test set
	 * @param cls the model
	 * @param trData the training (target) data
	 * @param testData the test dataset
	 * @throws Exception
	 */
	public static double[] evalModelUsingTestSet(Classifier cls, Instances trData, Instances testData) throws Exception{
		double[] metrics = new double[6];
		//System.out.println();				
		//Initialize evaluation with the train dataset structure:
		Evaluation eval = new Evaluation(testData);
		//	Evaluate the built classifier with the test dataset:
		cls.buildClassifier(trData);
		eval.evaluateModel(cls, testData);
		//printEvalMetrics(eval,cls);
		metrics[0] = eval.rootMeanSquaredError();
		metrics[1] = eval.meanAbsoluteError(); 		
		metrics[2] = eval.relativeAbsoluteError();
		metrics[3] = eval.rootRelativeSquaredError();
		metrics[4] = eval.correlationCoefficient();
		metrics[5] = eval.errorRate();
		return metrics;
	}

	/**
	 * looks up and returns Similarity scores 
	 * given two target IDs and a list of pairs-scores
	 * 
	 * @param t1 1st target ID 
	 * @param t2 2nd target ID
	 * @param simScoresList a list of pairs-scores read from CSV file
	 * @return similarity score
	 */
	public static double getSimilarityScore(String t1, String t2, List<List<String>> simScoresList){
		//loop thru similarity scores
		for(List<String> line : simScoresList) 
		{
			//
			String tt1 = (line.get(0));
			String tt2 = (line.get(1));
			if ((tt1.equals(t1) && tt2.equals(t2) ) || (tt1.equals(t2) && tt2.equals(t1))){
				double score = Double.parseDouble(line.get(2));
				return score; 
			}
		}
		return -1.0;
	}

	/**
	 * Removes a certain int from an array of ints
	 * returns new array of ints
	 * @param array the origina array 
	 * @param e the element we want to remove
	 * @return
	 */
	public static int[] removeElement(int[] array, int e){
		int[] newArray = new int[array.length - 1];
		int z = 0;
		for(int x = 0; x < array.length; x++)		
			if(array[x] != e){		  
				newArray[z] = array[x];
				z++;
			}
		return newArray;
	}

	/**
	 * Removes a certain String from an array of Strings
	 * returns new array of Strings
	 * @param array the origina array 
	 * @param e the element we want to remove
	 * @return
	 */
	public static String[] removeElement(String[] array, String e){
		String[] newArray = new String[array.length - 1];
		int z = 0;
		for(int x = 0; x < array.length; x++)		
			if(!array[x].equals(e) ){		  
				newArray[z] = array[x];
				z++;
			}
		return newArray;
	}

	/**
	 * Calculates RMSE given two arrays (predictions and actual values)
	 * @param predictions array of predicted values
	 * @param actuals array of actual values
	 * @return rmse
	 */
	public static double calcRMSE(double[] predictions, double[] actuals){
		double rmse = 0;
		double diff;
		for (int i = 0; i < actuals.length; i++) {
			diff = actuals[i] - predictions[i];
			rmse += diff * diff;
		}
		rmse /= actuals.length;
		return Math.sqrt(rmse);        	
	}

	/**
	 * Calculates MSE given two arrays (predictions and actual values)
	 * @param predictions array of predicted values
	 * @param actuals array of actual values
	 * @return mse
	 */
	public static double calcMSE(double[] predictions, double[] actuals){
		double mse = 0;
		double diff;
		for (int i = 0; i < actuals.length; i++) {
			diff = actuals[i] - predictions[i];
			mse += diff * diff;
		}
		mse /= actuals.length;
		return mse;        	
	}

	/**
	 * given a model and a dataset, find and return predictions
	 * @param cls an already built classifier
	 * @param test dataset
	 * @return array of predictions
	 * @throws Exception
	 */
	public static double[] makePredictions(Classifier cls, Instances test) throws Exception{
		// this loop finds the predictions	
		double[] preds = new double[test.numInstances()];
		for (int p = 0; p < test.numInstances(); p++) {						
			Instance newInst = test.instance(p);	
			//get class double value for current instance
			preds[p] = cls.classifyInstance(newInst);							
		}
		return preds;
	}

	/**
	 * Returns an array of actual values of a class from a dataset
	 * @param testDataset the dataset to be used
	 * @return a double array of actuall class values
	 * @throws Exception
	 */
	public static double[] getActuals(Instances testDataset) throws Exception{
		double[] actuals = new double[testDataset.numInstances()];
		for (int i = 0; i < testDataset.numInstances(); i++) {
			//get class double value for current instance
			double actualValue = testDataset.instance(i).classValue();
			actuals[i] = actualValue;			
		}
		return actuals;
	}

	/**
	 * checks if an int array contains an int element
	 *
	 * @param arr the int array
	 * @param e the element
	 * @return boolean true or false
	 */
	public static boolean arrayContains(int[] arr, int e){
		for(int i = 0; i < arr.length; i++){
			if(arr[i] == e)
				return true;
		}
		return false;
	}

	/**
	 * prints an int array to std output
	 *
	 * @param arr the int array
	 */
	public static void printIntArray(int[] arr){
		System.out.print("[");
		for(int i = 0; i < arr.length; i++){
			System.out.print(arr[i]+",");
		}
		System.out.print("\n");
	}

	public static Instances removeFstAttr(Instances data) throws Exception{
		Instances ndata = new Instances(data);
		//now remove MOLECULE_CHEMBL_ID from train and test
		String[] opts1 = new String[]{ "-R", "1"};
		//create a Remove object (this is the filter class)
		Remove remove1 = new Remove();
		//set the filter options
		remove1.setOptions(opts1);
		//pass the dataset to the filter
		remove1.setInputFormat(ndata);
		//apply the filter
		ndata = Filter.useFilter(ndata, remove1);
		//System.out.println(targetData.numAttributes());
		//set class index
		ndata.setClassIndex(ndata.numAttributes() - 1);
		return ndata;		
	}

	public static Instances removeAttributes(Instances data, String attrRem) throws Exception{
		Instances ndata = new Instances(data);
		//now remove MOLECULE_CHEMBL_ID from train and test
		String[] opts1 = new String[]{ "-R", attrRem};
		//create a Remove object (this is the filter class)
		Remove remove1 = new Remove();
		//set the filter options
		remove1.setOptions(opts1);
		//pass the dataset to the filter
		remove1.setInputFormat(ndata);
		//apply the filter
		ndata = Filter.useFilter(ndata, remove1);
		//System.out.println(targetData.numAttributes());
		//set class index
		ndata.setClassIndex(ndata.numAttributes() - 1);
		return ndata;		
	}



	final static private Pattern splitSearchPattern = Pattern.compile("[\",]");

	/**
	 * Splits a csv line into a list of strings, 
	 * avoids splitting if the comma is in double quotes
	 * @param  s a csv text line as a string
	 * @return   A list of strings containing all text between commas in the text line
	 */ 
	private static List<String> splitByCommasNotInQuotes(String s) {
		if (s == null)
			return Collections.emptyList();

		List<String> list = new ArrayList<String>();
		Matcher m = splitSearchPattern.matcher(s);
		int pos = 0;
		boolean quoteMode = false;
		while (m.find())
		{
			String sep = m.group();
			if ("\"".equals(sep))
			{
				quoteMode = !quoteMode;
			}
			else if (!quoteMode && ",".equals(sep))
			{
				int toPos = m.start();
				list.add((s.substring(pos, toPos)).trim());
				pos = m.end();
			}
		}
		if (pos < s.length())
			list.add((s.substring(pos)).trim());
		return list;
	}


	/**
	 * read csv file line by line and return as list of list of strings
	 * we can skip 1st line in case it has column names
	 * @param  inFile a file name with full path
	 * @param  skipFstLine true or not
	 * @return  a list of list of strings containing file contents
	 */ 
	public static List<List<String>> readFile(String inFile, boolean skipFstLine){
		List<List<String>> csvList = new ArrayList<List<String>>(); // to store lines after they're split
		try
		{
			BufferedReader infile = new BufferedReader( new FileReader( inFile ) ); // input1.txt

			String line;
			if(skipFstLine)
				infile.readLine(); // skip 1st line - it's the headers - column names!
			//reading in the infile to the ArrayList
			boolean b = true;
			FileWriter fileWritter;
			BufferedWriter bufferWritter;
			while((line = infile.readLine()) != null)
			{

				List<String> csvPieces = splitByCommasNotInQuotes(line);
				/*
					String[] linePieces = line.split(",");
			    		List<String> csvPieces = new ArrayList<String>(linePieces.length);
			    		for(String piece : linePieces)
			    		{
			    			csvPieces.add(piece);
			    		}
				 */
				csvList.add(csvPieces);

			}
			infile.close();
			// iterate through the key-value bindings, printing them out
			//for (Map.Entry<String, Integer> kv : freq.entrySet()) {
			//System.out.println("Word: " + kv.getKey() + " - Freq: " + kv.getValue().intValue());
			//}
		}
		catch (Exception e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			System.out.println("EXCEPTION CAUGHT: " + sw.toString() );
			System.exit( 0 );
		}
		return csvList;
	}

	/**
	 * given a target ID, retrieves its list of preselected features
	 * @param targetTID
	 * @return array of features numbers
	 */
	public static int[] getListOfFeatures(int targetTID){
		int[] selectedFeatures = null;
		List<List<String>> list = Utility.readFile("features.txt",false);
		for(List<String> line : list)
		{   
			int id = Integer.parseInt(line.get(0));

			if(id == targetTID){				
				selectedFeatures = new int[line.size() - 1];
				//System.out.println(":"+(line.size() - 1));
				for(int i = 1; i < line.size(); i++){
					//System.out.println(line.get(i));
					selectedFeatures[i-1] = Integer.parseInt(line.get(i));
					//System.out.print(line.get(i)+",");
				}
				//System.out.println();
				break;
			}
		} 
		//System.out.println(selectedFeatures[3]);
		return selectedFeatures;
	}

	/**
	 * Merges two int arrays
	 * @param a int array 1
	 * @param b int array 2
	 * @return c new array
	 */
	public static int[] mergeIntArrays(int[] a, int[] b) {
		int aLen = a.length;
		int bLen = b.length;
		int[] c = new int[aLen+bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}

	/**
	 * removes duplicates from int array
	 * @param arr int array	
	 * @return array with no duplicates
	 */
	public static int[] removeDuplicates(int[] arr){
		int end = arr.length;
		Set<Integer> set = new HashSet<Integer>();
		for(int i = 0; i < end; i++){
			set.add(arr[i]);
		}
		int result[] = new int[set.size()];
		Iterator<Integer> it = set.iterator();
		int i = 0;
		while(it.hasNext()) {
			//System.out.println(it.next());
			result[i] = it.next();
			i++;
		}
		return result;
	}

	public static String getOrganismViaTID(List<List<String>> dhfrOrgList, String TID){
		for(List<String> csvCls : dhfrOrgList){
			if(TID.equals(csvCls.get(0)))
				return 	csvCls.get(1);
		}
		return "";
	}

	/**
	 * merges two datasets (instances)
	 * from http://stackoverflow.com/questions/10771558/how-to-merge-two-sets-of-weka-instances-together
	 * @param data1 fst dataset
	 * @param data2 2nd dataset
	 * @return merged
	 * @throws Exception
	 */
	public static Instances mergeInstances(Instances data1, Instances data2)
			throws Exception
	{
		// Check where are the string attributes
		int asize = data1.numAttributes();
		boolean strings_pos[] = new boolean[asize];
		for(int i=0; i<asize; i++)
		{
			Attribute att = data1.attribute(i);
			strings_pos[i] = ((att.type() == Attribute.STRING) ||
					(att.type() == Attribute.NOMINAL));
		}

		// Create a new dataset
		Instances dest = new Instances(data1);
		dest.setRelationName(data1.relationName() + "+" + data2.relationName());

		DataSource source = new DataSource(data2);
		Instances instances = source.getStructure();
		Instance instance = null;
		while (source.hasMoreElements(instances)) {
			instance = source.nextElement(instances);
			dest.add(instance);

			// Copy string attributes
			for(int i=0; i<asize; i++) {
				if(strings_pos[i]) {
					dest.instance(dest.numInstances()-1)
					.setValue(i,instance.stringValue(i));
				}
			}
		}

		return dest;
	}

	/**
	 * Creates a directory
	 * @param dirName
	 * @throws IOException
	 */
	public static void createDir(final String dirName) throws IOException {
		//final File homeDir = new File(System.getProperty("user.home"));
		final File dir = new File(dirName);
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IOException("Unable to create " + dir.getAbsolutePath());
		}
	}
	/**
	 * returns an array of file objects in a certain dir
	 * files have a certain extension
	 * @param dir the dir name
	 * @param ext extension of required files
	 * @return array of file objects
	 */
	public static File[] getFiles(File dir,final String ext) {
		return dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(ext);
			}
		});
	}

	/**
	 * Evaluates Tanimoto coefficient for two bit sets.
	 * Code adapted by Noureddin Sadawi from CDK Library
	 * @param bitset1 A bitset (such as a fingerprint) for the first molecule
	 * @param bitset2 A bitset (such as a fingerprint) for the second molecule
	 * @return The Tanimoto coefficient
	 */
	//    public static float calculateTanimotoCoefficient(BitSet bitset1, BitSet bitset2) throws Exception
	//    {
	//        float _bitset1_cardinality = bitset1.cardinality();
	//        float _bitset2_cardinality = bitset2.cardinality();
	//        if (bitset1.size() != bitset2.size()) {
	//            throw new Exception("Bisets must have the same bit length");
	//        }
	//        BitSet one_and_two = (BitSet)bitset1.clone();
	//        one_and_two.and(bitset2);
	//        float _common_bit_count = one_and_two.cardinality(); 
	//        float _tanimoto_coefficient = _common_bit_count/(_bitset1_cardinality + _bitset2_cardinality - _common_bit_count);
	//        return _tanimoto_coefficient;
	//    }

	/**
	 * Creates a BitSet from a binary string.
	 * @param bits A binary string
	 * @return The Generated BitSet
	 */
	//    public static BitSet createBitSet(String bits)
	//    {
	//      int len = bits.length();
	//      BitSet bs = new BitSet(len);
	//      for (int i = 0; i < len; i++)
	//      {
	//        bs.set(len - i - 1, bits.charAt(i) == '1');
	//      }
	//      return bs;
	//    }

	/**
	 * Evaluates the continuous Tanimoto coefficient for two real valued vectors.
	 *
	 * @param features1 The first feature vector
	 * @param features2 The second feature vector
	 * @return The continuous Tanimoto coefficient
	 */
	public static float calculateTanimotoCoefficient(double[] features1, double[] features2) throws Exception {

		if (features1.length != features2.length) {
			throw new Exception("Features vectors must be of the same length");
		}

		int n = features1.length;
		double ab = 0.0;
		double a2 = 0.0;
		double b2 = 0.0;

		for (int i = 0; i < n; i++) {
			ab += features1[i] * features2[i];
			a2 += features1[i]*features1[i];
			b2 += features2[i]*features2[i];
		}
		return (float)ab/(float)(a2+b2-ab);
	}

	/*
	 * detect duplicate in array by comparing size of List and Set
	 * since Set doesn't contain duplicate, size must be less for an array which contains duplicates
	 * Read more: http://javarevisited.blogspot.com/2012/02/how-to-check-or-detect-duplicate.html#ixzz3cf2iXQCZ
	 */
	public static boolean checkDuplicateUsingSet(String[] input){
		List<String> inputList = Arrays.asList(input);
		Set<String> inputSet = new HashSet<String>(inputList);
		if(inputSet.size() < inputList.size())
			return true;

		return false;
	}

	/**
	 * Counts how many lines a given file has
	 * @param  filename  a fully qualified filename 
	 * @return      The number of lines in the input file 
	 */  
	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}


	/**
	 * This method finds which instances in the source dataset are similar to instances in the target dataset
	 * It uses a similarity threshold 
	 * counts how many instances are above that threshold
	 * It avoids instances with sim = 1 (avoids duplication)
	 * @param target the target dataset instances
	 * @param source the source dataset instances
	 * @param orgSim the similarity between source and target organisms
	 * @param wSetting which weight to assign to a selected instance
	 * @return a list of instances
	 */
	public static List<Instance> findSimilarInstances(Instances target, Instances source, double orgSim, int wSetting){		
		double simThreshold = 0.5;

		List<Instance> instList = new ArrayList<Instance>();
		for(Instance srcInst : source){
			int numAttr = srcInst.numAttributes();
			String srcMolID = srcInst.stringValue(0);
			//System.out.println("trying src: "+srcMolID);
			double[] srcAttrs = new double[numAttr-1];
			String srcBits = "";
			for(int att = 1; att < numAttr; att++)
				srcAttrs[att-1] = srcInst.value(att);				
			int numSimInst = 0;
			//sum of simi values
			double simSum = 0.0;
			//let's keep track of the highest sim value
			double highestSim = 0.0;
			for(Instance trgInst : target){
				int tnumAttr = trgInst.numAttributes();
				String trgMolID = trgInst.stringValue(0);
				if(trgMolID.equals(srcMolID)){//avoid having the same instance again
					//System.out.println("shared drug: "+srcMolID);
					numSimInst = 0;
					highestSim = 0.0;
					simSum = 0.0;
					break;
				}else{
					double[] trgAttrs = new double[tnumAttr-1];
					String trgBits = "";
					for(int att = 1; att < tnumAttr; att++)
						trgAttrs[att-1] = trgInst.value(att);
					try{
						//BitSet srcBitSet = createBitSet(srcBits);
						//BitSet trgBitSet = createBitSet(trgBits);
						//float taniSimi = calculateTanimotoCoefficient(srcBitSet, trgBitSet);
						float taniSimi = calculateTanimotoCoefficient(srcAttrs, trgAttrs);
						simSum += taniSimi;
						if(taniSimi >= simThreshold && taniSimi < 1.0){
							//System.out.println(taniSimi);
							numSimInst++;
							if(highestSim < taniSimi)
								highestSim = taniSimi;
						}
					}
					catch(Exception e){
						e.printStackTrace();
						System.out.println("Error when computing Tanimoto Similarity");
					}
				}
			}
			if(numSimInst >= target.numInstances()/4){				
				if(wSetting == 0)
					srcInst.setWeight(orgSim);
				else
					if(wSetting == 1)
						srcInst.setWeight(highestSim);
					else
						if(wSetting == 2)
							srcInst.setWeight(1);
						else
							srcInst.setWeight(simSum/target.numInstances());

				instList.add(srcInst);				
				//System.out.println("Add "+srcMolID + " "+numSimInst);
			}
		}
		return instList;
	}

}
