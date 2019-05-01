package mtl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;


public class Utility {		

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
	 * Removes the first attribute (normally instance ID) from an ARFF file
	 * @param data the arff dataset
	 * @return the dataset without the first attribute
	 * @throws Exception
	 */
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

	/**
	 * Shuffles a double array (in place)
	 * @param array input double array
	 * @return shuffled array
	 */
	public static void shuffleArray(double[] array){
		Random rgen = new Random(); 
		for (int i=0; i<array.length; i++) {
			int randomPosition = rgen.nextInt(array.length);
			double temp = array[i];
			array[i] = array[randomPosition];
			array[randomPosition] = temp;
		} 		
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
			while((line = infile.readLine()) != null)
			{
				List<String> csvPieces = splitByCommasNotInQuotes(line);				
				csvList.add(csvPieces);
			}
			infile.close();			
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
	 * returns an array of file objects in a certain dir
	 * files have a certain extension
	 * @param dir the dir name
	 * @param ext extension of required files
	 * @return array of file objects
	 */
	public static File[] getFiles(File dir, final String ext) {
		return dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(ext);
			}
		});
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
}
