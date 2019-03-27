# Iterative Extraction of Chinese Hypernymy Relations from User Generated Categories

### By Chengyu Wang (https://chywang.github.io)

**Introducion:** This software automantically extracts Chinese hypernymy relations from user generated entity-category pairs, based on a small training set of hypernymy relations. It iteratively updates the parameters of a piecewise linear projection model for hypernymy prediction via semi-supervised learning and pattern-based validation techniques. The projection model is trained using word embeddings of the iteratively enlarged training set.

**Paper** 
1. Wang et al. Transductive Non-linear Learning for Chinese Hypernym Prediction. ACL 2017
2. Wang et al. Predicting Hypernym-Hyponym Relations for Chinese Taxonomy Learning. KAIS (2019) (extended version)


**APIs**

+ AlgMain (in the alg package): The main software entry-point, with seven input argements required.

1. iterationNumber: The number of iterations of our algorithm.

2. numOfAttr: The dimensionlaity of the embedding vectors.

3. w2vFile: The embeddings of all Chinese words in either the training set or the unlabeled entity-category set. The start of each line of the file is the Chinose word, followed by the embedding vectors. All the values in a line are separated by a blank (' '). In practice, the embeddings can be learned by all deep neural language models.

> NOTE: Due to the large size of neural language models, we only upload the embedding vectors of words in the training and unlabeled sets. Please use your own neural language model instead, if you would like to try the algorithm over your datasets.

4. positiveFile: The path of the training set, containing hypernymy relations in the format of "word1 \t word2" pairs. 

> NOTE: A sample file "positive.txt" is provided. The data is sampled from Cilin (http://www.ltp-cloud.com/download) and a human-labeled dataset from Chinese Wikipedia.

5. unlabeledFile: The path of the unlabeled entity-category set. The format of the unlabeled set is the same as that of the training set.

> NOTE: A sample file "unlabeled.txt" is provided. The data is crawled from the Baidu Baike categories (https://baike.baidu.com). We only provide a very small portion of the data here.

6. hearstPosScoreFile: The positive scores of Chinese word pairs, computed based on Chinese Hearst-style patters.

> NOTE: Refer to the next part for more details.

6. hearstNegScoreFile: The negative scores of Chinese word pairs, computed based on Chinese co-hyponym patters.

> NOTE: The default values can be set as: "5", "50", "word_vectors.txt", "positive.txt", "unlabeled.txt", "hearst/positiveScore.txt" and "hearst/negativeScore.txt".

Output explanation:

+ Eval: A simple evaluation script,  with three input argements required. It outputs Precision, Recall and F1-score  as the evaluation scores. 

1. truthPath: The path of the testing set, with human-labeled results.

2. predictPath: The path of the model output file,.

3. thres: A threshold in (-1,1) for the model to assign relation labels to Chinese word pairs. (Please refer to the parameter 'θ' in the paper.)

> NOTE: The default values can be set as: "test.txt", "output.txt" and "0.1".

**Dependencies**

1. This software is run in the JaveSE-1.8 environment. With a large probability, it runs properly in other versions of JaveSE as well. However, there is no guarantee.

2. It requires the Weka toolkit for implementations of sone basic machine learning algorithms (https://www.cs.waikato.ac.nz/ml/weka/), and the JAMA library for matrix computation (https://math.nist.gov/javanumerics/jama/). We use weka.jar (version 3.6.10) and Jama-1.0.3.jar in this project.


**Notes on the Algorithm** 


**Citation**

If you find this software useful for your research, please cite the following papers.

> @inproceedings{coling2016,<br/>
&emsp;&emsp; author    = {Chengyu Wang and Xiaofeng He},<br/>
&emsp;&emsp; title     = {Chinese Hypernym-Hyponym Extraction from User Generated Categories},<br/>
&emsp;&emsp; booktitle = {Proceedings of the 26th International Conference on Computational Linguistics},<br/>
&emsp;&emsp; pages     = {1350--1361},<br/>
&emsp;&emsp; year      = {2016}<br/>
}

> @article{kais2018,<br/>
&emsp;&emsp; author    = {Chengyu Wang and Yan Fan and Xiaofeng He and Aoying Zhou},<br/>
&emsp;&emsp; title     = {Predicting hypernym--hyponym relations for Chinese taxonomy learning},<br/>
&emsp;&emsp; journal   = {Knowledge and Information Systems},<br/>
&emsp;&emsp; volume    = {58},<br/>
&emsp;&emsp; number    = {3},<br/>
&emsp;&emsp; pages     = {585--610},<br/>
&emsp;&emsp; year      = {2019}<br/>
}

More research works can be found here: https://chywang.github.io.





