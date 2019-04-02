/**
 * By Noureddin Sadawi .. Sep 2015
 * In this class we perform Multiple Task Learning by taking drug classes one
 * at a time, concatenating their corresponding datasets, adding values of similarities
 * between targets as new columns and adding each target's 
 * the similarity values are percent identity!
 * ID as a new column in the dataset
 * When we do cross-validation, we stratify based on the target ID
 * Notice in each Level, each drug class or group is represented by a text file
 * This text file contains a list of dataset names
 * Each of these datasets represents one drug target
 * The target ID can be worked out from the dataset name
 */


package mtl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;

import mtl.Utility;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;

public class PIDSimMTL {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			//this is where the drug classes are (each class is represnted by a textfile)
			//each tex file has a list of dataset names 
			//String workingDir = "/home/likewise-open/ACADEMIC/csstnns/test/subdatasets/Drug-Class-Datasets/MTL_TEST/";
			String workingDir = "/home/sadawi/Drug-Class-Datasets/FingerprintsCHEMBL20/L5/";
			// where the datsets are!
			String dataDir = "/home/sadawi/ECFP6_1024/";
			//String dataDir = "/home/likewise-open/ACADEMIC/csstnns/test/subdatasets/ECFP6_1024/";

			// list all dirs 
			File file = new File(workingDir);
			//File f = new File("."); // current directory
			FileFilter directoryFilter = new FileFilter() {
				public boolean accept(File file) {
					return file.isDirectory();
				}
			};

			//get a list of subdirs in this dir
			File[] files = file.listFiles(directoryFilter);
			//we go through the classes (text files) one at a time
			for (File f : files) {//loop thru dirs
				if (f.isDirectory()) {	
					System.out.println("MTL PIDSim drug target: "+f.getName());
					File[] csvFiles = Utility.getFiles(new File(f.getCanonicalPath()),".txt");
					int numGroups = csvFiles.length;
					for(int g = 0; g < numGroups; g++){//now loop thru files
						String groupFile = csvFiles[g].getAbsolutePath();
						int numTIDs = Utility.countLines(groupFile);
						String allTIDs[] = new String [numTIDs];//array of species TIDs				

						String fName = csvFiles[g].getName();									
						String groupName = fName.replaceFirst("[.][^.]+$", "");
						System.out.println("\tPIDSimMTL Group: "+groupName);
						BufferedReader infile = new BufferedReader( new FileReader( groupFile ) ); // input1.txt
						String datasetName;
						int it = 0;
						while((datasetName = infile.readLine()) != null)
						{							
							System.out.println(dataDir+datasetName);
							String targetTID = datasetName.replaceFirst("[.][^.]+$", "");
							allTIDs[it] = targetTID;
							it++;
						}
						infile.close();

						//time at start of execution
						final long st = System.currentTimeMillis();
						//load similarities .. Organsim1,Organism2,score
						List<List<String>> simScoresList = Utility.readFile(f.getCanonicalPath()+"/"+groupName+"_pid.pid", true);

						//get list of all mol_IDs
						ArrayList<String> molIDs = new ArrayList<String>(); 
						//get string of all TIDs .. comma separated string
						//we use it for nominal label OrganismTID
						String orgLabels = "";
						for(int outerLoop = 0; outerLoop < numTIDs; outerLoop++){
							String targetTID = allTIDs[outerLoop];//set which one is the target
							//make organismIDs as labels
							orgLabels += targetTID;
							if(outerLoop < (numTIDs - 1))
								orgLabels += ",";

							String targetDS =  dataDir+targetTID+".csv" ;

							DataSource target = new DataSource(targetDS);
							Instances targetData = target.getDataSet();				
							targetData.setClassIndex(targetData.numAttributes() - 1);

							for (int k = 0; k < targetData.numInstances(); k++) {
								String v = targetData.instance(k).stringValue(targetData.attribute("MOLECULE_CHEMBL_ID"));
								molIDs.add(v);
							}				
						}

						// this is to merge all source datasets
						DataSource allSource = new DataSource(dataDir+(allTIDs[0])+".csv");
						Instances allSrcData = allSource.getDataSet();				
						allSrcData.setClassIndex(allSrcData.numAttributes() - 1);

						allSrcData = Utility.removeFstAttr(allSrcData);				
						allSrcData.setClassIndex(allSrcData.numAttributes() - 1);

						//add TID as an extra column in the dataset
						Add addTID = new Add();
						addTID.setAttributeIndex("first");
						addTID.setNominalLabels(orgLabels);
						addTID.setAttributeName("ORGANISM_TID");
						addTID.setInputFormat(allSrcData);
						allSrcData = Filter.useFilter(allSrcData, addTID);
						for (int i = 0; i < allSrcData.numInstances(); i++) {		        
							//nominal
							allSrcData.instance(i).setValue(0, allTIDs[0]);
							// 2. numeric
							//allSrcData.instance(i).setValue(0, allTIDs[0]);
						}
						allSrcData.setClassIndex(allSrcData.numAttributes() - 1);


						for(int i = 1; i < numTIDs ; i++){
							//System.out.println(allSrcData.numInstances()+" - "+allSrcData.numAttributes());
							DataSource tmpSource = new DataSource(dataDir+(allTIDs[i])+".csv");
							Instances tmpSrcData = tmpSource.getDataSet();				
							tmpSrcData.setClassIndex(tmpSrcData.numAttributes() - 1);				

							//remove categorical attribute MOLECULE_CHEMBL_ID
							tmpSrcData = Utility.removeFstAttr(tmpSrcData);				
							tmpSrcData.setClassIndex(tmpSrcData.numAttributes() - 1);

							//add TID as an extra column in the dataset
							addTID = new Add();
							addTID.setAttributeIndex("first");
							addTID.setNominalLabels(orgLabels);
							addTID.setAttributeName("ORGANISM_TID");
							addTID.setInputFormat(tmpSrcData);
							tmpSrcData = Filter.useFilter(tmpSrcData, addTID);
							for (int k = 0; k < tmpSrcData.numInstances(); k++) {		        
								tmpSrcData.instance(k).setValue(0, allTIDs[i]);
								// 2. numeric
								//tmpSrcData.instance(k).setValue(0, allTIDs[i]);
							}
							tmpSrcData.setClassIndex(tmpSrcData.numAttributes() - 1);

							//now do the merging						
							Enumeration enumer = tmpSrcData.enumerateInstances();
							while (enumer.hasMoreElements()) {
								Instance instance = (Instance) enumer.nextElement();
								//instance.attribute("MOLECULE_CHEMBL_ID");
								allSrcData.add(instance);
							}
							//System.out.println(allTIDs[i] + " "+tmpSrcData.numInstances());
						}

						//time of execution
						//final long st1 = System.currentTimeMillis();
						//System.out.println("Done Stage 1 of DistMTL in "+(st1-st)/(1000.0*60.0)+" Minutes");

						//now let's add columns for similarity values
						//the number of columns = number of organisms .. numTIDs
						for(int id = 0; id < numTIDs; id++){
							String tid = allTIDs[id];
							addTID = new Add();
							addTID.setAttributeIndex("first");
							addTID.setAttributeName("SIM_TO_"+tid);
							addTID.setInputFormat(allSrcData);
							allSrcData = Filter.useFilter(allSrcData, addTID);
							for (int k = 0; k < allSrcData.numInstances(); k++) {		        
								// 2. numeric
								//int v = (int)allSrcData.instance(k).value(allSrcData.attribute("ORGANISM_TID"));
								String sv = allSrcData.instance(k).stringValue(allSrcData.attribute("ORGANISM_TID"));
								String v = sv;
								if(tid.equals(v))//similarity between tid and itself
									allSrcData.instance(k).setValue(0, 1.0);//value is 1 .. perfect similarity
								else{
									double sim = Utility.getSimilarityScore(tid, v, simScoresList);
									allSrcData.instance(k).setValue(0, sim);//value is obtained from list
									if(sim == -1)
										System.out.println("ERROR: similarity between "+tid+" and "+v +" NOT defined!");
								}
							}
							allSrcData.setClassIndex(allSrcData.numAttributes() - 1);
						}

						ArrayList<String> molIDsNoDups = new ArrayList<String>(new LinkedHashSet<String>(molIDs));
						String labels = "" ;
						for(int j = 0; j < molIDsNoDups.size(); j++){						
							labels += molIDsNoDups.get(j);
							if(j < molIDsNoDups.size())
								labels += ",";											
						}
						Add filter;
						//newData = new Instances(data);
						// 1. nominal attribute
						filter = new Add();
						filter.setAttributeIndex("first");
						filter.setNominalLabels(labels);
						filter.setAttributeName("MOLECULE_CHEMBL_ID");
						filter.setInputFormat(allSrcData);
						allSrcData = Filter.useFilter(allSrcData, filter);
						for (int i = 0; i < allSrcData.numInstances(); i++) {
							// 1. nominal				
							allSrcData.instance(i).setValue(0, molIDs.get(i));
						}					
						//time of execution
						//final long st2 = System.currentTimeMillis();
						//System.out.println("Done Stage 2 of DistMTL in "+(st2-st1)/(1000.0*60.0)+" Minutes");


						//save the merged dataset if needed
						allSrcData.setRelationName("Concatenated_datasets_for_group_"+groupName);
						ArffSaver saver = new ArffSaver();
						saver.setInstances(allSrcData);
						saver.setFile(new File("PIDSimMTL-Datasets/"+groupName+".arff"));
						saver.writeBatch();

						int folds = 10;
						//

						PrintWriter out = new PrintWriter("results/PIDSimMTL_LEVELS/L5ChEMBL20/PIDSimMTL_"+groupName+".csv");
						out.println("rep,fold,organism_tid,row_id,actual,prediction");

						double[] actualValues = new double[allSrcData.numInstances()];
						double[] predictedValues = new double[allSrcData.numInstances()];
						int apIndex = 0;

						//set class id for nominal variable oranismTID
						allSrcData.setClassIndex(numTIDs+1);
						allSrcData.stratify(folds);
						// perform cross-validation	 
						//allSrcData.randomize(new Random(1));
						for (int n = 0; n < folds; n++) {
							//time of execution
							//						final long ft1 = System.currentTimeMillis();

							//get the folds	      
							Instances trainData = allSrcData.trainCV(folds, n);				
							trainData.setClassIndex(trainData.numAttributes() - 1);
							Instances testData = allSrcData.testCV(folds, n);								
							testData.setClassIndex(testData.numAttributes() - 1);

							//get IDs of test instances
							String[] testIDs = new String[testData.numInstances()];				
							for (int i = 0; i < testData.numInstances(); i++) {
								String v = testData.instance(i).stringValue(testData.attribute("MOLECULE_CHEMBL_ID"));
								testIDs[i] = v;
							}

							//get organismTIDs of test instances
							String[] testOrgIDs = new String[testData.numInstances()];				
							for (int i = 0; i < testData.numInstances(); i++) {
								String v = testData.instance(i).stringValue(testData.attribute("ORGANISM_TID"));
								testOrgIDs[i] = v;
							}

							double[] actuals = Utility.getActuals(testData);//get actual values

							//remove MOLECULE_CHEMBL_ID
							trainData = Utility.removeFstAttr(trainData);
							testData = Utility.removeFstAttr(testData);

							RandomForest rf = new RandomForest();
							rf.setNumTrees(100);
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
								out.println("1,"+n+","+testOrgIDs[z]+","+testIDs[z]+","+actuals[z]+","+preds[z]);
								//System.out.println("1,"+n+","+testIDs[z]+","+actuals[z]+","+preds[z]);
							}
							out.flush();
							//						final long ft2 = System.currentTimeMillis();
							//						System.out.println("Done fold "+n+ " of DistMTL in "+(ft2-ft1)/(1000.0*60.0)+" Minutes");
							// compute RMSE
							//double rmse = Utility.calcRMSE(preds,actuals);
							//RMSE += rmse;
						}
						out.close();
						// Now let's save the model
						//set class id for nominal variable oranismTID
						allSrcData.setClassIndex(numTIDs+1);
						allSrcData.stratify(folds);
						//remove MOLECULE_CHEMBL_ID
						//and set activity as class variable
						allSrcData = Utility.removeFstAttr(allSrcData);
						//build RF model with all data
						RandomForest rf = new RandomForest();
						rf.setNumTrees(100);
						rf.buildClassifier(allSrcData);
						//save the model
						weka.core.SerializationHelper.write("PIDSimMTL-Models/"+groupName+".model", rf);

						//					double RMSE = Utility.calcRMSE(predictedValues,actualValues);
						//					System.out.println("RMSE for Merged Dataset is "+RMSE);
						//					final long st3 = System.currentTimeMillis();
						//					System.out.println("Finished all of DistMTL in "+(st3-st)/(1000.0*60.0)+" Minutes");							
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}