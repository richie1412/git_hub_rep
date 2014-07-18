package com.wxxr.callhelper.qg.widget.pick;

/**
 * ����list���ϵ� ������
 */
import java.util.List;
public class ArrayWheelAdapter1 implements WheelAdapter {
	public static final int DEFAULT_LENGTH = -1;
	private List<String> list;
	private int length;
	public ArrayWheelAdapter1(List<String> list, int length) {
		this.list = list;
		this.length = length;
	}
	public ArrayWheelAdapter1(List<String> list) {
		this(list, DEFAULT_LENGTH);
	}

	@Override
	public int getItemsCount() {
		return list.size();
	}

	@Override
	public int getMaximumLength() {
		return length;
	}
	@Override
	public String getItem(int index) {
		return list.get(index);
	}
}
