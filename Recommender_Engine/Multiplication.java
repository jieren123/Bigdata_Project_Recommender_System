import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Multiplication {
	public static class CooccurrenceMapper extends Mapper<LongWritable, Text, Text, Text> {

		// MAP METHOD
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			//input: movie_2 \t movie_1=relation
			//pass data to reducer

			String[] line = value.toString().split('\t');
			context.write(new Text(line[0]), new Text(line[1]));
		}
	}

	public static class RatingMapper extends Mapper<LongWritable, Text, Text, Text> {

		// MAP METHOD
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			//input: user,movie,rating
			//pass data to reducer
			String[] line = value.toString().split(',');
			context.write(new Text(line[1]), new Text(line[0] + ':' + line[2]));

		}
	}

	public static class MultiplicationReducer extends Reducer<Text, Text, Text, DoubleWritable> {
		// REDUCE METHOD
		@Override
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			//key = movie_2 value = <movie_1=relation, movie_3=relation... userA:rating, userB:rating...>
			//collect the data for each movie, then do the multiplication
			Map<String, Double> relationMap = new HashMap<String, Double>();
			Map<String, Double> ratingMap = new HashMap<String, Double>();

			for (Text value:values){
				if (values.toString().contains('=')){
					String[] movie_relation = value.toString().split('=');
					relationMap.put(movie_relation[0], Double.parseDouble(movie_relation[1]));
				}
				else{
					String[] user_rating = value.toString().split(':');
					ratingMap.put(user_rating[0], Double.parseDouble(user_rating[1]));
				}
			}

			for(Map.Entry<String, Double> entry: relatoinMap.entrySet()){
				String movie = entry.getKey();
				double relation = entry.getValue();

				for (Map.Entry<String, Double> element: ratingMap.entrySet()){
					String user = element.getKey();
					double rating = element.getValue();
					context.wrtie(new Text(user + ':' + movie), new DoubleWritable(relation*rating));
				}
			}
		}
	}


	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();

		Job job = Job.getInstance(conf);
		job.setJarByClass(Multiplication.class);

		ChainMapper.addMapper(job, CooccurrenceMapper.class, LongWritable.class, Text.class, Text.class, Text.class, conf);
		ChainMapper.addMapper(job, RatingMapper.class, Text.class, Text.class, Text.class, Text.class, conf);

		job.setMapperClass(CooccurrenceMapper.class);
		job.setMapperClass(RatingMapper.class);

		job.setReducerClass(MultiplicationReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);

		MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, CooccurrenceMapper.class);
		MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, RatingMapper.class);

		TextOutputFormat.setOutputPath(job, new Path(args[2]));
		
		job.waitForCompletion(true);
	}
}
