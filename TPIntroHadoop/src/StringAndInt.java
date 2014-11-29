public class StringAndInt implements Comparable<StringAndInt> {

	private String tag;
	private Integer nbOccurence;

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

	@Override
	public int compareTo(StringAndInt arg0) {
		if (this.nbOccurence > arg0.getNbOccurence()) {
			return 1;
		} else if (this.nbOccurence == arg0.getNbOccurence()) {
			return 0;
		} else {
			return -1;
		}
	}

}
