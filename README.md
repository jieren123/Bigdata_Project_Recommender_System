## Overview 
The movie recommender system is based on **Item Collaborative Filtering** using **Hadoop MapReduce** in Java。

## Architecture Flowchat
![alt text](https://github.com/jieren123/Bigdata_Project_Recommender_System/blob/master/Pictures-Diagrams/RecommenderSystem_1.png
 "Recommender System")

## Dataset Description
- The dataset used is taken from Netflix Prize Challenge. 
- Movies and their numbers are extracted in a file called 'movie_title.txt'
- Each subsequent line in the dataset file contains: Customer-ID, Rating(integral scale from 1 to 5) and Date(YYYY-MM-DD).
- Data Preprocessing:
	 - Change the data in each movie file into the following format: User-ID, Movie-ID and Rating-score.
## Building Steps :
	- Divide data by User-ID
	- Build co-occurence matrix 
	- Normalize co-occurence matrix 
	- Build rating matrix 
	- Multiply co-occurrence matrix and rating matrix 
	- Generate recommender list 

## How to run 
```
hadoop com.sun.tools.javac.Main *.java
jar cf recommender.jar *.class
hadoop jar recommender.jar Driver /input /dataDividedByUser /coOccurrenceMatrix /Normalize /Multiplication /Sum
hdfs dfs -cat /Sum/*
```
## Result
- args0: Movie dataset
- args1: Output directory for DividerByUser job
- args2: Output directory for CoOccurrenceMatrixBuilder job
- args3: Output directory for Normalize job
- args4: Output directory for Multiplication job
- args5: Output directory for Sum job

