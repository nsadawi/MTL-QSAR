# MTL-QSAR
This repository has the Java source code for MTL-QSAR paper **Multi-Task Learning with a Natural Metric for Quantitative Structure
  Activity Relationship Learning**
  
The datasets used in this experiments are available on https://www.openml.org/s/3

* Please download the datasets and similarity values available on google drive here: https://drive.google.com/drive/folders/1WCQWIe7wlVfQ7vbwZgHXQy3YDRAPMUte?usp=sharing

* Unzip the ECFP6_1024.zip file and make sure the resulting folder replaces the empty folder ECFP6_1024/ which is inside MTL-QSAR/ project folder

* Also, unzip the ChEMBL20.zip file and make sure the resulting folder replaces the empty folder ChEMBL20/ which is inside MTL-QSAR/ project folder. The ChEMBL20/ folder contains the ChEMBL groups and classes of drug targets as explained in the paper. It also includes the similarity values of drug targets and the sequences used to compute these similarity values. 

* Currently the provided java project contains one sample Drug Target Class (under ChEMBL20\L5). 
 * The AGC_protein_kinase_NDR_family.txt file contains a list of corresponding datasets that belong to drug targets which happen to be under the group/class 'AGC_protein_kinase_NDR_family'. 
 * The file AGC_protein_kinase_NDR_family_sequences.seq contains the sequences of each of the drug targets and the file AGC_protein_kinase_NDR_family_pid.pid contains the percentage ID similarity values for those drug targets. 
 * The results of running standard RandomForest, MTL and SimMTL approaches as explained in the paper are in the folders: Results\StdRF, Results\MTL and Results\PIDSimMTL respectively. 
 * The folder ECFP6_1024\ contains the corresponding datasets (list of ccompounds and their activity values on the drug target) for these drug targets. Notice the dataset name is the drug target ID. 
 * The features are molecular fingerprints.

* The full data with all Drug Target Classes and their datasets and similarity values is available on the GDrive mentioned above.


Markup : * Bullet list
              * Nested bullet
                  * Sub-nested bullet etc
          * Bullet list item 2
