package edu.yu.einstein.genplay.dataStructure.list.primitiveList;

import java.util.List;

interface PrimitiveArrayWrapper<T> extends List<T> {

	void trimToSize(int size);
}
