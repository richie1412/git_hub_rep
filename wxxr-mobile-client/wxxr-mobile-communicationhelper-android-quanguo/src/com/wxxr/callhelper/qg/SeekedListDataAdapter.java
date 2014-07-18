package com.wxxr.callhelper.qg;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.wxxr.callhelper.qg.utils.ToPinYin;

/**
 * 拼音搜索模板管理类(适配器模式)
 * 
 * @author cuizaixi
 */
public class SeekedListDataAdapter implements ISeekedAdapter {
	private List<String> mSelection;
	private Map<String, List<ListedEntity>> mMap;
	private Map<String, Integer> mIndex;
	private List<ListedEntity> mListedEntities;
	private String[] mString;
	private List<Integer> mPositions;
	public SeekedListDataAdapter(String[] str) {
		mString = str;
		mMap = new HashMap<String, List<ListedEntity>>();
		mSelection = new ArrayList<String>();
		mIndex = new HashMap<String, Integer>();
		mListedEntities = getAllEntities();
		mPositions = new ArrayList<Integer>();
	};
	/**
	 * @return获取所有封装完成的实体
	 */
	public List<ListedEntity> getAllEntities() {
		List<ListedEntity> entities = new ArrayList<ListedEntity>();
		// List<String> sortData = sortData(this.mString);
		List<String> sortData = Arrays.asList(this.mString);
		for (String str : sortData) {
			ListedEntity entity = new ListedEntity();
			entity.setFirstPingyin(ToPinYin.getFirstPinyin(str));
			entity.setName(str);
			try {
				entity.setAllPinyin(ToPinYin.getPinYin(str));
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
			entities.add(entity);
		}

		return entities;
	};
	/**
	 * 边框搜索使用
	 */
	public void prepareEntitiesList() {
		for (ListedEntity entity : this.mListedEntities) {
			String firstPingyin = entity.getFirstPingyin();
			String upperCasePinyin = firstPingyin.toUpperCase();
			if (mSelection.contains(upperCasePinyin)) {
				mMap.get(upperCasePinyin).add(entity);
			} else {
				mSelection.add(upperCasePinyin);
				List<ListedEntity> list = new ArrayList<ListedEntity>();
				list.add(entity);
				mMap.put(upperCasePinyin, list);
			}
		}
		Collections.sort(mSelection);
		int position = 0;
		mPositions.add(position);
		for (String selection : mSelection) {
			mIndex.put(selection, position);
			position += mMap.get(selection).size();
			mPositions.add(position);
		}
	}
	public String[] getString() {
		return mString;
	}
	public void setString(String[] str) {
		this.mString = str;
	}
	public List<String> getSelection() {
		return mSelection;
	}
	public void setSelection(List<String> selection) {
		mSelection = selection;
	}
	public Map<String, List<ListedEntity>> getMap() {
		return mMap;
	}
	public void setMap(Map<String, List<ListedEntity>> map) {
		mMap = map;
	}
	public Map<String, Integer> getIndex() {
		return mIndex;
	}
	public void setIndex(Map<String, Integer> index) {
		mIndex = index;
	}
	public List<ListedEntity> getListedEntities() {
		return mListedEntities;
	}
	public void setListedEntities(List<ListedEntity> listedEntities) {
		mListedEntities = listedEntities;
	}
	/**
	 * 把字符按照汉语拼音A-Z排序
	 * 
	 * @param strs
	 * @return
	 */
	private static List<String> sortData(String[] strs) {
		Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
		Arrays.sort(strs, com);
		return Arrays.asList(strs);
	}
	public static class ListedEntity {
		private String firstPingyin;
		private String name;
		private String allPinyin;
		public String getAllPinyin() {
			return allPinyin;
		}
		public void setAllPinyin(String allPinyin) {
			this.allPinyin = allPinyin;
		}
		public ListedEntity(String firstPingyin, String name) {
			this.firstPingyin = firstPingyin;
			this.name = name;
		}
		public ListedEntity() {
		};
		public String getFirstPingyin() {
			return firstPingyin;
		}
		public void setFirstPingyin(String firstPingyin) {
			this.firstPingyin = firstPingyin;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	public List<Integer> getPositions() {
		return mPositions;
	}
	public void setPositions(List<Integer> positions) {
		mPositions = positions;
	}
}
