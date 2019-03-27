# Iterative Extraction of Chinese Hypernymy Relations from User Generated Categories

### By Chengyu Wang (https://chywang.github.io)

**Introducion:** This software automantically extracts Chinese hypernymy relations from user generated entity-category pairs, based on a small training set of hypernymy relations. It iteratively updates the parameters of a piecewise linear projection model for hypernymy prediction via semi-supervised learning and pattern-based validation techniques. The projection model is trained using word embeddings of the iteratively enlarged training set.

**Paper:** 
1. Wang et al. Transductive Non-linear Learning for Chinese Hypernym Prediction. ACL 2017
2. Wang et al. Predicting Hypernym-Hyponym Relations for Chinese Taxonomy Learning. KAIS (2019) (extended version)


**APIs**

+ TransductLeaner: The main software entry-point, with five input argements required.

1. w2vPath: The embeddings of all Chinese words in either the training set or the testing set. The start of each line of the file is the Chinose word, followed by the embedding vectors. All the values in a line are separated by a blank (' '). In practice, the embeddings can be learned by all deep neural language models.

> NOTE: Due to the large size of neural language models, we only upload the embedding vectors of words in the training and testing sets. Please use your own neural language model instead, if you would like to try the algorithm over your datasets.

2. trainPath: The path of the training set in the format of "word1 \t word2 \t label" triples. As for the label, 1 is for the hypernymy relation and 0 is for the non-hypernymy relation.

3. testPath: The path of the testing set. The format of the testing set is the same as that of the training set.

4. outputPath: The path of the output file, containing the model prediction scores of all the pairs in the testing set. The output of each pair is a real value in (-1,1). (Please refer to the paper for detailed explanation.)

5. dimension: The dimensionlaity of the embedding vectors.

> NOTE: The default values can be set are: "word_vectors.txt", "train.txt", "test.txt", "output.txt" and "50".

+ Eval: A simple evaluation script,  with three input argements required. It outputs Precision, Recall and F1-score  as the evaluation scores. 

1. truthPath: The path of the testing set, with human-labeled results.

2. predictPath: The path of the model output file,.

3. thres: A threshold in (-1,1) for the model to assign relation labels to Chinese word pairs. (Please refer to the parameter 'θ' in the paper.)

> NOTE: The default values can be set as: "test.txt", "output.txt" and "0.1".

**Dependencies**

1. This software is run in the JaveSE-1.8 environment. With a large probability, it runs properly in other versions of JaveSE as well. However, there is no guarantee.

2. It requires the FudanNLP toolkit for Chinese NLP analysis (https://github.com/FudanNLP/fnlp/), and the JAMA library for matrix computation (https://math.nist.gov/javanumerics/jama/). We use Jama-1.0.3.jar in this project.

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





