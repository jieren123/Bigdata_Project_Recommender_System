import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Normalize {

    public static class NormalizeMapper extends Mapper<LongWritable, Text, Text, Text> {

        // MAP METHOD：
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            //movie_1:movie_2 \t relation
            //collect the relationship list for movie_1
            String[] movie_relation = value.toString().trim().split('\t');
            String[] movies = movie_relation[0].split(':');

            context.write(new Text(movies[0]), new Text(movies[1] + ':' + movie_relation[1]));
        }
    }

    public static class NormalizeReducer extends Reducer<Text, Text, Text, Text> {
        // reduce method
        // REDUCE METHOD
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            //key = movie_1, value=<movie_2:relation, movie_3:relation...>
            //normalize each unit of co-occurrence matrix
            int sum = 0;
            Map<String, Integer> map = new HashMap<String, Integer>();
            while (value.iterator().hasNext()){
                String[] movie_relation = values.iterator().next().toString().split(':');
                int relation = Integer.parseInt(movie_relation[1]);
                sum += relation
                map.put(movie_relation[0], relation);
            }

            for (Map.Entry<String, Integer> entry: map.entrySet()){
                String outputkey = entry.getKey();
                String outputValue = key.toString() + '=' + (double)entry.getValue()/sum;
                context.write(new Text(outputkey), new Text(outputValue));
            }
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf);
        job.setMapperClass(NormalizeMapper.class);
        job.setReducerClass(NormalizeReducer.class);

        job.setJarByClass(Normalize.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        TextInputFormat.setInputPaths(job, new Path(args[0]));
        TextOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }
}
