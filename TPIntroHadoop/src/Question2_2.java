import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import com.google.common.collect.MinMaxPriorityQueue;

public class Question2_2 {
	public static class MyMapper extends Mapper<LongWritable, Text, Text, Text> {

		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] parts = value.toString().split("\\t");

			Country country = Country.getCountryAt(
					Double.parseDouble(parts[11]),
					Double.parseDouble(parts[10]));

			if (country != null) {

				String[] tags = parts[8].split(",");
				for (String tag : tags) {
					context.write(new Text(country.toString()), new Text(
							java.net.URLDecoder.decode(tag, "UTF-8")));
				}
			}

		}
	}

	public static class MyCombiner extends Reducer<Text, Text, Text, Text> {

		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			HashMap<String, Integer> map = new HashMap<String, Integer>();
			Configuration configuration = context.getConfiguration();
			int k = Integer.parseInt(configuration.get("k"));

			MinMaxPriorityQueue<StringAndInt> maxOccurenceQueue = MinMaxPriorityQueue
					.maximumSize(k).create();

			for (Text tag : values) {
				if (map.get(tag.toString()) == null) {
					map.put(tag.toString(), 1);
				} else {
					int val = map.get(tag.toString());
					val++;
					map.put(tag.toString(), val);
				}
			}

			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				maxOccurenceQueue.add(new StringAndInt(
						new Text(entry.getKey()), entry.getValue()));

			}

			for (Iterator iterator = maxOccurenceQueue.iterator(); iterator
					.hasNext();) {
				StringAndInt stringAndInt = (StringAndInt) iterator.next();

				context.write(key, stringAndInt.getTtag());
			}

		}

	}

	public static class MyReducer extends Reducer<Text, Text, Text, Text> {
		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			for (Text tag : values) {
				context.write(key, tag);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		String input = otherArgs[0];
		String output = otherArgs[1];

		conf.set("k", args[2]);

		Job job = Job.getInstance(conf, "Question2_2");
		job.setJarByClass(Question2_2.class);

		job.setMapperClass(MyMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		job.setCombinerClass(MyCombiner.class);
		job.setNumReduceTasks(3);

		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job, new Path(input));
		job.setInputFormatClass(TextInputFormat.class);

		FileOutputFormat.setOutputPath(job, new Path(output));
		job.setOutputFormatClass(TextOutputFormat.class);

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
