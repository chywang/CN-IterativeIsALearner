# Iterative Extraction of Chinese Hypernymy Relations from User Generated Categories

### By Chengyu Wang (https://chywang.github.io)

**Introduction:** This software automatically extracts Chinese hypernymy relations from user generated entity-category pairs, based on a small training set of hypernymy relations. It iteratively updates the parameters of a piecewise linear projection model for hypernymy prediction via semi-supervised learning and pattern-based validation techniques. The projection model is trained using word embeddings of the iteratively enlarged training set.

**Papers** 
1. Wang et al. Transductive Non-linear Learning for Chinese Hypernym Prediction. ACL 2017
2. Wang et al. Predicting Hypernym-Hyponym Relations for Chinese Taxonomy Learning. KAIS (2019) (extended version)


**APIs**

+ AlgMain (in the alg package): The main software entry-point, with seven input arguments required.

1. iterationNumber: The number of iterations of our algorithm.

2. numOfAttr: The dimensionality of the embedding vectors.

3. w2vFile: The embeddings of all Chinese words in either the training set or the unlabeled entity-category set. The start of each line of the file is the Chinese word, followed by the embedding vectors. All the values in a line are separated by a blank (' '). In practice, the embeddings can be learned by all deep neural language models.

> NOTE: Due to the large size of neural language models, we only upload the embedding vectors of words in the training and unlabeled sets. Please use your own neural language model instead, if you would like to try the algorithm over your datasets.

4. positiveFile: The path of the training set, containing hypernymy relations in the format of "word1 \t word2" pairs. 

> NOTE: A sample file "positive.txt" is provided. The data is sampled from Cilin (http://www.ltp-cloud.com/download) and a human-labeled dataset from Chinese Wikipedia.

5. unlabeledFile: The path of the unlabeled entity-category set. The format of the unlabeled set is the same as that of the training set.

> NOTE: A sample file "unlabeled.txt" is provided. The data is crawled from the Baidu Baike categories (https://baike.baidu.com). We only provide a very small portion of the data here.

6. hearstPosScoreFile: The positive scores of Chinese word pairs, computed based on Chinese Hearst-style patters.

> NOTE: Refer to the next part for more details.

7. hearstNegScoreFile: The negative scores of Chinese word pairs, computed based on Chinese co-hyponym patters.

> NOTE: The default values can be set as: "5", "50", "word_vectors.txt", "positive.txt", "unlabeled.txt", "hearst/positiveScore.txt" and "hearst/negativeScore.txt".

Output explanation:

To output the results in each iteration, the algorithm automatically creates a folder named "iter"+iteration number (starting from 0). In each folder, “canPos.txt” contains new hypernymy relations predicted by the algorithm in the corresponding iteration. "validatePos.txt" contains a subset of hypernymy relations in “canPos.txt”, which have been validated by the pattern-based technique.

+ HearstPositiveScore and HearstNegativeScore (in the hearst package): Two simple scripts to compute positive and negative pattern-based scores for Chinese word pairs, which are required to be pre-computed before we run the AlgMain program.

1. HearstPositiveScore: It uses the statistics in "hearstOneStat.txt" and "hearstTwoStat.txt" as input, which are the number of matches of Is-A and Such-As patterns for word pairs in the text corpus.

> Refer to the score $n_1(x_i, y)$ in the paper.

2. HearstNegativeScore:  It uses the statistics in "hearstTwoStatSingle.txt" and "hearstTwoStatPair.txt" as input, which are the number of matches of Co-Hyponym patterns for word pairs in the text corpus.

> Refer to the scores $n_2(x_i, x_j)$ and $n_2(x_i)$ in the paper.

**Dependencies**

1. This software is run in the JaveSE-1.8 environment. With a large probability, it runs properly in other versions of JaveSE as well. However, there is no guarantee.

2. It requires the Weka toolkit for implementations of sone basic machine learning algorithms (https://www.cs.waikato.ac.nz/ml/weka/), and the JAMA library for matrix computation (https://math.nist.gov/javanumerics/jama/). We use weka.jar (version 3.6.10) and Jama-1.0.3.jar in this project.


**Notes on the Algorithm** 


**Citations**

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





