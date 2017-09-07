import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class DataDividerByUser {
	public static class DataDividerMapper extends Mapper<LongWritable, Text, IntWritable, Text> {

		// MAP_Method:divide data by user

		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			//input user,movie,rating
			String[] user_movie_rating = value.toString().trim().split(','); // we have 3 list here 
			int userID = Integer.parseInt(user_movie_rating[0]);
			String movieID = user_movie_rating[1];
			String rating_score = user_movie_rating[2];

			context.write(new IntWritable(userID), new Text(movieID + ':' + rating_score));

		}
	}

	public static class DataDividerReducer extends Reducer<IntWritable, Text, IntWritable, Text> {
		
		// reduce method

		@Override
		public void reduce(IntWritable key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			//merge data for one user
			StringBuilder strblder = new StringBuilder();
			while (values.iterator().hasNext()){
				strblder.append(',' + values.iterator().next());
			}
			
			// key-value pair: key = userID value = movie1: rating_score, movie2: rating_score.....
			context.write(key new Text(strblder.toString().replaceFirst(',', '')));

		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();

		Job job = Job.getInstance(conf);
		job.setMapperClass(DataDividerMapper.class);
		job.setReducerClass(DataDividerReducer.class);

		job.setJarByClass(DataDividerByUser.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);

		TextInputFormat.setInputPaths(job, new Path(args[0]));
		TextOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);
	}

}
