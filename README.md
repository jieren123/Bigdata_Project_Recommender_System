## Project Overview 
The movie recommender system is based on **Item Collaborative Filtering** and **Hadoop MapReduce** in Java, by using **Item Collaborative Filtering**.

## Dataset Description
- The dataset used is taken from Netflix Prize Challenge. 
- Movies and their numbers are extracted in a file called 'movie_title.txt'
- Each subsequent line in the dataset file contains: Customer-ID, Rating(integral scale from 1 to 5) and Date(YYYY-MM-DD).
- Data Preprocessing:
	 - Change the data in each movie file into the following format: User-ID, Movie-ID and Rating-score.
	 
## Architecture Overview Diagram
![alt text](https://github.com/jieren123/Bigdata_Project_Recommender_System/blob/master/Pictures-Diagrams/RecommenderSystem_1.png
 "Recommender System")
 
## Algorithms used for recommender system
**Item Collaborative Filtering** is a form of collaborative filtering for recommender systems based on the similarity between items calculated using people's ratings of those items. Item CF is used beacause: 1. The total number of movie users weighs more than total number of movie products. 2.Item will not change frequently and has lower calculation than User CF due to the dynamic nature of user. 
3. It is more convincing to use user's historical data. 

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

