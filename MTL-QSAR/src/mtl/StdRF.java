/**
 * By Noureddin Sadawi .. April 2019
 * In this class we taking drug classes one at a time and list the drug targets that fall under each drug class
 * For each drug target, we use the corresponding dataset!
 * We perform 10 fold cross validation using Weka's RandomForest
 * The resulting file is saved in such a way that it contains:
 * the fold number, the drug id (i.e. instance id), actual value, predicted value
 * These values can be easily used to compute the average RMSE
 * Notice in each Level, each drug class or group is represented by a text file
 * This text file contains a list of dataset names
 * Each of these datasets represents one drug target
 * The drug target ID can be worked out from the dataset name
 */

package mtl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

//import Utility;

import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class StdRF {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			//this is where the drug classes are (each class is represented by a text file)
			//each text file has a list of dataset names 
			String workingDir = "ChEMBL20\\L5\\";
			// where the datsets are!
			String dataDir = "ECFP6_1024\\";

			//we go through the classes (text files) one at a time
			File[] csvFiles = Utility.getFiles(new File(workingDir),".txt");
			int numGroups = csvFiles.length;

			for(int g = 0; g < numGroups; g++){//now loop thru files
				String groupFile = csvFiles[g].getAbsolutePath();
				String fName = csvFiles[g].getName();									
				String groupName = fName.replaceFirst("[.][^.]+$", "");
				System.out.println("Group: "+groupName);
				BufferedReader infile = new BufferedReader( new FileReader( groupFile ) ); // input1.txt
				String datasetName;
				while((datasetName = infile.readLine()) != null)
				{							
					System.out.println("STD RF LEVELS " +dataDir+datasetName);

					String dsName = datasetName.replaceFirst("[.][^.]+$", "");
					PrintWriter out = new PrintWriter("Results/StdRF/StdRF_"+dsName+".csv");
					out.println("rep,fold,row_id,actual,prediction");
					DataSource target = new DataSource(dataDir+datasetName);
					Instances targetData = target.getDataSet();	

					targetData.setClassIndex(targetData.numAttributes() - 1);

					RandomForest rf = new RandomForest();
					rf.setNumTrees(100);
					int folds = 10;

					double[] actualValues = new double[targetData.numInstances()];
					double[] predictedValues = new double[targetData.numInstances()];
					int apIndex = 0;
					// perform cross-validation	 
					System.out.println("Starting cross validation:");
					for (int n = 0; n < folds; n++) {
						//get the folds
						Instances trainData = targetData.trainCV(folds, n);				
						trainData.setClassIndex(trainData.numAttributes() - 1);
						Instances testData = targetData.testCV(folds, n);								
						testData.setClassIndex(testData.numAttributes() - 1);

						//get IDs of test instances
						String[] testIDs = new String[testData.numInstances()];				
						for (int i = 0; i < testData.numInstances(); i++) {
							String v = testData.instance(i).stringValue(testData.attribute("MOLECULE_CHEMBL_ID"));
							testIDs[i] = v;
						}

						trainData = Utility.removeFstAttr(trainData);
						testData = Utility.removeFstAttr(testData);

						double[] actuals = Utility.getActuals(testData);//get actual values
						rf.buildClassifier(trainData);
						// finds the predictions
						double[] preds = Utility.makePredictions(rf, testData);
						//copy actual and predicted values of test fold into actualValues and predcitedValues
						for(int z = 0; z < testIDs.length; z++){
							actualValues[z+apIndex] = actuals[z];
							predictedValues[z+apIndex] = preds[z];						
							//System.out.println("1,"+n+","+testIDs[z]+","+actuals[z]+","+preds[z]);
						}
						apIndex += testIDs.length;
						//output repetition,fold,row_id,acual,prediction
						//for openML compatibility
						for(int z = 0; z < testIDs.length; z++){
							out.println("1,"+n+","+testIDs[z]+","+actuals[z]+","+preds[z]);
							//System.out.println("1,"+n+","+testIDs[z]+","+actuals[z]+","+preds[z]);
						}
						out.flush();
						System.out.println("Finished fold #: "+(n+1));
					}
					out.close();
				}
				infile.close();							
			}							
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}