package com.wxxr.callhelper.qg;

import java.util.List;
import java.util.Map;

import com.wxxr.callhelper.qg.SeekedListDataAdapter.ListedEntity;
/**
 * 
 * @author cuizaixi
 * 
 */
public interface ISeekedAdapter {
	/**
	 * 
	 * @return 字母索引集合
	 */
	public List<String> getSelection();
	public void setSelection(List<String> selection);
	public Map<String, List<ListedEntity>> getMap();
	public void setMap(Map<String, List<ListedEntity>> map);
	public Map<String, Integer> getIndex();
	public void setIndex(Map<String, Integer> index);
	/**
	 * 
	 * @return 封装完成的实体(包括首字母和汉语名称属性);
	 */
	public List<ListedEntity> getListedEntities();
	public void setListedEntities(List<ListedEntity> listedEntities);
	public List<Integer> getPositions();
	/**
	 * 
	 * @param positions
	 */
	public void setPositions(List<Integer> positions);
}
