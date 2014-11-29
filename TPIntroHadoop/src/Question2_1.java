import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.collections.map.HashedMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Counters.Counter;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.RawKeyValueIterator;
import org.apache.hadoop.mapred.Task.CombinerRunner;
import org.apache.hadoop.mapred.Task.TaskReporter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.yarn.webapp.example.MyApp.MyController;

public class Question2_1 {
	public static class MyMapper extends
			Mapper<LongWritable, Text, Text, Text> {
		
		
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
						
			for (String ligne : value.toString().split("\\n")) {
				String[] parts = ligne.split("\\s+");
				Country country = Country.getCountryAt(Integer.parseInt(parts[11]), Integer.parseInt(parts[10]));
				String[] tags = parts[8].split(",");
				for (String tag : tags) {
					context.write(new Text(country.toString()), new Text(tag));	
				}
				
			}
			
		}
	}

	public static class MyReducer extends
	Reducer<Text, Text, Text, IntWritable> {
		@Override
		protected void reduce(Text key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			
			HashMap<String, Integer> map=new HashMap<String, Integer>(); 

			for (String ligne : values.toString().split("\\n")) {
			String[] parts = ligne.split("\\s+");
			Country country = Country.getCountryAt(Integer.parseInt(parts[11]), Integer.parseInt(parts[10]));
			if(map.get(country.toString())==null){
				map.put(country.toString(), 1);
			} else {
				int val = map.get(country.toString());
				val++;
				map.put(country.toString(), val);
			};
		}
			
//			for (Text value : values) {
//				sum += value.get();
//			}
			
			
			context.write(key, new IntWritable(sum));
			// context.write(key, new LongWritable(sum));
		}
	}


	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		String input = otherArgs[0];
		String output = otherArgs[1];

		Job job = Job.getInstance(conf, "Question2_1");
		job.setJarByClass(Question1_1.class);

		job.setMapperClass(MyMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

//		job.setCombinerClass(MyReducer.class);
//		job.setNumReduceTasks(3);

		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		FileInputFormat.addInputPath(job, new Path(input));
		job.setInputFormatClass(TextInputFormat.class);

		FileOutputFormat.setOutputPath(job, new Path(output));
		job.setOutputFormatClass(TextOutputFormat.class);

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
