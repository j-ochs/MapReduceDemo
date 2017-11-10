package cs128.cs128;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.IntSumReducer;
import org.apache.hadoop.util.GenericOptionsParser;

public class WordCount {
	
	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
		
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			//read in the data
			//split the data into words
			StringTokenizer itr = new StringTokenizer(value.toString());
			Text word = new Text();
			IntWritable one = new IntWritable(1);
			while (itr.hasMoreTokens()) {
				//output (word, 1) for each word
				word.set(itr.nextToken());
				context.write(word, one);
			}
		}
		
	}
	
	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		public void reducer(Text term, Iterable<IntWritable> ones, Context context) throws IOException, InterruptedException {
			int count = 0;
			Iterator<IntWritable> iterator = ones.iterator();
			while(iterator.hasNext()) {
				count++;
				iterator.next();
			}
			context.write(term, new IntWritable(count));
		}
		
	}

	public static void main(String[] args) throws IOException {
		
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		
		if (otherArgs.length == 2) {
			System.err.println("Usage: WordCount <in> <out>");
			System.exit(2);
		}
		
		Job job = Job.getInstance(conf, "Word Count");
		
		job.setJarByClass(WordCount.class);
		job.setMapperClass(TokenizerMapper.class);
		//job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setNumReduceTasks(10);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		

	}

}
