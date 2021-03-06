package dsp.topN;

import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.Text;

import java.nio.charset.Charset;
import java.util.Arrays;

public class TopNComparator implements RawComparator<Text> {
    //decade \t pmi \t first \s second
    // decade \t ~ \t ~
	public TopNComparator() {
		super();
	}

    @Override
    public int compare(byte[] bytes, int i, int i1, byte[] bytes1, int i2, int i3) {
        try {
            byte[] first = Arrays.copyOfRange(bytes, i+1, i + i1);
            byte[] second = Arrays.copyOfRange(bytes1, i2+1, i2 + i3);
            String firstString = new String(first, Charset.forName("UTF-8"));
            String secondString = new String(second, Charset.forName("UTF-8"));
            return compareStrings(firstString, secondString);
        }catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @Override
    public int compare(Text o1, Text o2) {
        try {
        return compareStrings(o1.toString(),o2.toString());
        }catch (Throwable t) {
            System.out.println(t);
            throw t;
        }
    }

    public int compareStrings(String s, String s1) {
        String[] splits1 = s.split("\t");
        String[] splits2 = s1.split("\t");

        String decade1 = splits1[0];
        String decade2 = splits2[0];

        String pmiString1 = splits1[1];
        String pmiString2 = splits2[1];

        String words1 = splits1[2];
        String words2 = splits2[2];

        if (splits1.length < 2 || splits2.length < 2) {
            throw new RuntimeException("unexpected strings \"" + s + "\", " + s1 + "\"." );
        }

        if (decade1.compareTo(decade2) != 0) {
            return decade1.compareTo(decade2);
        }

        if (pmiString1.matches("~") || pmiString2.matches("~")) {
            // tilde ascii is bigger than digits ascii, so "~".compareTo("123") is bigger than 0
            return pmiString1.compareTo(pmiString2);
        }

        if (pmiString1.compareTo(pmiString2) != 0) {
            double pmi1 = Double.parseDouble(pmiString1);
            double pmi2 = Double.parseDouble(pmiString2);
            return Double.compare(pmi2, pmi1);
        }

        return words1.compareTo(words2);
    }

    public static void main(String[] args) {

        String s1="1900\t2.0\taaa bbb",
                s2="1910\t3.5\taa bb",
                s3="1900\t1.1\ta b",
                s4="1910\t1.1\ta a";
        TopNComparator comp = new TopNComparator();
        System.out.println(comp.compareStrings(s2,s1) > 0);
        System.out.println(comp.compareStrings(s1,s1) == 0);
        System.out.println(comp.compareStrings(s2,s4) > 0);
        System.out.println(comp.compareStrings(s3,s1) < 0);
        System.out.println(comp.compareStrings(s4,s3) > 0);
        System.out.println(comp.compareStrings("1920\t~\t~",s3) > 0);
    }
}
