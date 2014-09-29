package project2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author nm5144
 * 
 *         This class is used to sort RavensObjects based on their descriptions, regardless of the ordering of attributes.
 *         It is based on the common language, and different languages may produce different orderings.
 */
final class RavensObjectComparator implements Comparator<RavensObject> {

	ArrayList<String> attributes;
	
	
	
	public RavensObjectComparator(HashMap<String, HashSet<String>> language) {
		attributes=new ArrayList<String>();
		attributes.addAll(language.keySet());
	}



	@Override
	public int compare(RavensObject a, RavensObject b) {
		int cmp;
		Collections.sort(attributes);
		for (String key : attributes) {
			ArrayList<String> aVals = new ArrayList<String>(), bVals = new ArrayList<String>();
			for (RavensAttribute attr : a.getAttributes())
				if (a.getName().equals(key))
					aVals.add(attr.getValue());
			for (RavensAttribute attr : b.getAttributes())
				if (b.getName().equals(key))
					bVals.add(attr.getValue());
			if (aVals.size() < bVals.size())
				return 1;
			if (aVals.size() > bVals.size())
				return -1;
			Collections.sort(aVals);
			Collections.sort(bVals);
			if (aVals.size() == 0)
				continue;
			for (int i = 0; i < aVals.size(); i++) {
				cmp = aVals.get(i).compareTo(bVals.get(i));
				if (cmp != 0)
					return cmp;
			}
		}
		return 0;
	}
}