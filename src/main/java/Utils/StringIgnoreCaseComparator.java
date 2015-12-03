package Utils;

import java.util.Comparator;

public class StringIgnoreCaseComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		if (o1.equals(o2)) {
			return 0;
		} else if (o1.equalsIgnoreCase(o2)) {
			return 1;
		} else {
			return o1.toLowerCase().compareTo(o2.toLowerCase());
		}
	}
}
