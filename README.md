# MTL-QSAR
This repository has the Java source code for MTL-QSAR paper <a href="https://link.springer.com/article/10.1186/s13321-019-0392-1?wt_mc=Internal.Event.1.SEM.ArticleAuthorIncrementalIssue&utm_source=ArticleAuthorIncrementalIssue&utm_medium=email&utm_content=AA_en_06082018&ArticleAuthorIncrementalIssue_20191115" target="_blank">*Multi-Task Learning with a Natural Metric for Quantitative Structure Activity Relationship Learning*</a>

## License and Citation
The code in this repository is free and can as desired. However, please cite this paper if you find this code useful in your research or you use it for any other purpose:

```
@inproceedings{Sadawi2019,
 author = {Noureddin Sadawi, Ivan Olier, Joaquin Vanschoren, Jan N. van Rijn, Jeremy Besnard, Richard Bickerton, Crina Grosan, Larisa Soldatova, Ross D. King},
 title = {Multi-task learning with a natural metric for quantitative structure activity relationship learning},
 Journal = {Journal of Cheminformatics volume},
 publisher = {Springer},
 year = {2019},
 doi = {10.1186/s13321-019-0392-1},
 keywords = {Multi-task learning, Quantitative structure activity relationship, Sequence-based similarity, Random forest}  
} 
```

Please watch this video to see how to run the code: <a href="https://youtu.be/mZOr0HVwbKM" target="_blank">MTL-QSAR Video</a>

[![MTL-QSAR Video](https://img.youtube.com/vi/mZOr0HVwbKM/0.jpg)](https://www.youtube.com/watch?v=mZOr0HVwbKM "MTL-QSAR Video")



The datasets used in this experiments are available on <a href="https://www.openml.org/s/3" target="blank">MTL-QSAR Datasets on OpenML</a> 

* Please download the datasets and similarity values available on google drive here: <a href="https://drive.google.com/drive/folders/1WCQWIe7wlVfQ7vbwZgHXQy3YDRAPMUte?usp=sharing" target="_blank">MTL-QSAR Data on GDrive</a> 

* Unzip the ECFP6_1024.zip file and make sure the resulting folder replaces the empty folder ECFP6_1024/ which is inside MTL-QSAR/ project folder

* Also, unzip the ChEMBL20.zip file and make sure the resulting folder replaces the empty folder ChEMBL20/ which is inside MTL-QSAR/ project folder. The ChEMBL20/ folder contains the ChEMBL groups and classes of drug targets as explained in the paper. It also includes the similarity values of drug targets and the sequences used to compute these similarity values. 

* **Important Notice** The Feature-based MTL approach explained in the paper is implemented in the _MTL_ java class and The Instance-based MTL approach explained in the paper is implemented in the _PIDSimMTL_ java class.

* Currently the provided java project contains one sample Drug Target Class (under ChEMBL20\L5). 
  * The _AGC_protein_kinase_NDR_family.txt_ file contains a list of corresponding datasets that belong to drug targets which happen to be under the group/class _AGC_protein_kinase_NDR_family_. 
  * The file _AGC_protein_kinase_NDR_family_sequences.seq_ contains the sequences of each of the drug targets and the file _AGC_protein_kinase_NDR_family_pid.pid_ contains the percentage ID similarity values for those drug targets. 
  * The results of running standard RandomForest, Feature-based MTL and Instance-based MTL approaches as explained in the paper are in the folders: Results\StdRF, Results\MTL and Results\PIDSimMTL respectively. 
  * The folder ECFP6_1024\ contains the corresponding datasets (list of ccompounds and their activity values on the drug target) for these drug targets. Notice the dataset name is the drug target ID. 
  
  * The features are molecular fingerprints.

* The full data with all Drug Target Classes and their datasets and similarity values is available on the GDrive mentioned above.

* As part of validation our results, we have performed a randomisation procedure by shuffling the similarity values in the SimMTL approach. We have randomly selected 24 level 5 classes (the total number of drug targets is 120) and randomised their similarity values 1000 times. Each time we randomise we run SimMTL and compute RSME for each drug target. The file _Group-24-TID-100-Results-1000reps.csv_ available under the _MTL-QSAR\Results_ folder has the results. The first two rows are headers (group and drug target TID), the third row is the RSME value for that particular TID without randomising similarity values, and the remaining 1000 rows contain the RSME values after randomisation.

* Also, to validate results, we have performed a randomisation procedure by shuffling the Y-variable in the MTL approach. We have randomised it 1000 times and computed RSME for each drug target each iteration. The file _MTL-YRandom1000-StdRF.csv_ available under the _MTL-QSAR\Results_ folder has the results. The first row is the header (iteration number and drug target TIDs), the second row is the RSME value for that particular TID when performing STL (i.e. standard randomforest), and the remaining 1000 rows contain the RSME values for MTL after randomisation. It can be seen that the values in the standard randomforest row are always smaller than those of the other rows. This has been explained in the paper.

* The file _benefited-not-benefited.pdf_ contains lists of drug target classes that fully or not fully benefit from our settings. This is explained in more detail in the manuscript.




