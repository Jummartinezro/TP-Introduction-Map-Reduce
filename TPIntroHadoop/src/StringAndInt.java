import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class StringAndInt implements Comparable<StringAndInt>, Writable {

	private String tag;
	private Text ttag;
	private Integer nbOccurence;

	public StringAndInt() {
		super();
	}

	public StringAndInt(Text ttag, Integer value) {
		super();
		this.ttag = ttag;
		this.nbOccurence = value;
	}

	public StringAndInt(String key, Integer value) {
		this.tag = key;
		this.nbOccurence = value;
	}

	public String getTag() {
		return tag;
	}

	public int getNbOccurence() {
		return nbOccurence;
	}

	public Text getTtag() {
		return ttag;
	}

	public void setTtag(Text ttag) {
		this.ttag = ttag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setNbOccurence(Integer nbOccurence) {
		this.nbOccurence = nbOccurence;
	}

	@Override
	public int compareTo(StringAndInt arg0) {
		if (this.nbOccurence > arg0.getNbOccurence()) {
			return -1;
		} else if (this.nbOccurence == arg0.getNbOccurence()) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeChars(tag);
	}

}
