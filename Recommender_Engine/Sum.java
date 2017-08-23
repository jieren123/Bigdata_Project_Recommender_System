import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
 
public class Sum {

    public static class SumMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {

        // map method
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            //pass data to reducer

            String[] line = value.toString().split('\t');
            context.write(new Text(line[0]), new DoubleWritable(Double.parseDdouble(line[1])));
        }
    }

    public static class SumReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
        // REDUCE METHOD
        @Override
        public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
                throws IOException, InterruptedException {

            //user:movie relation and calculate the sum
            double sum = 0;
            for (DoubleWritable value: values) {
                sum += value.get();
            }
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf);
        job.setMapperClass(SumMapper.class);
        job.setReducerClass(SumReducer.class);

        job.setJarByClass(Sum.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        TextInputFormat.setInputPaths(job, new Path(args[0]));
        TextOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }
}
