package dsp.stage3;

import dsp.Constants;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

/**
 * Created by hagai_lvi on 03/06/2016.
 */
public class Stage3ReducerTest {

	private ReduceDriver<Text, Text, Text, Text> reduceDriver;

	@Before
	public void setUp() {
		Reducer<Text, Text, Text, Text> reducer = new Stage3Reducer();
		reduceDriver = ReduceDriver.newReduceDriver(reducer);
	}

	@Test
	public void reduce() throws Exception {

		String year = "1950",
				w1 = "strange",
				w2 = "thing",
				w1Count = "3",
				w2Count = "2",
				twoGramCount = "1";
		reduceDriver.getConfiguration().set(Constants.COUNTER_NAME_PREFIX+year,"1000");

		reduceDriver.
				withInput(
						new Text(String.join(Stage3Mapper.SEPERATOR, year, w2) + " *"),
						Collections.singletonList(new Text(String.join(Stage3Mapper.SEPERATOR, w2, w2Count)))).
				withInput(
						new Text(String.join(Stage3Mapper.SEPERATOR, year, w2) + " +"),
						Collections.singletonList(new Text(
								String.join(Stage3Mapper.SEPERATOR, w1 + " " + w2, twoGramCount, w1, w1Count)))).
				withOutput(
						new Text(String.join(Stage3Mapper.SEPERATOR, year, w1 + " " + w2)),
						new Text(String.join(Stage3Mapper.SEPERATOR, twoGramCount, w2, w2Count, w1, w1Count, "7.380821783940931"))).
				runTest();
	}

}