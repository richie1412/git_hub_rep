package com.wxxr.callhelper.qg.adapter;

import java.util.ArrayList;
import java.util.List;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.ContactInfo;
import com.wxxr.callhelper.qg.widget.LetterParser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactAdapter extends BaseAdapter implements Filterable
{
	private LayoutInflater inflater;
	// ���浱ǰ������������ϵ��
	private ArrayList<ContactInfo> contactinfoList;
	// ����������ϵ��
	private ArrayList<ContactInfo> oldInfoList;
	// LetterParser�ṩ����ת��Ϊƴ��
	private LetterParser letterParser;

	// �����б?�����б�
	private List<String> nameList;
	private List<String> phoneList;
	// ������ת��Ϊ���֣����硰����è��--->"wdm"--->936
	private List<String> nameToNumList;

	public ContactAdapter(Context context, List<ContactInfo> list)
	{
		inflater = LayoutInflater.from(context);
		letterParser = new LetterParser();

		if (contactinfoList != null)
		{
			contactinfoList.clear();
		}
		contactinfoList = (ArrayList<ContactInfo>) list;
		oldInfoList = contactinfoList;
		initSomeList();
	}

	private void initSomeList()
	{
		nameList = new ArrayList<String>();
		phoneList = new ArrayList<String>();
		nameToNumList = new ArrayList<String>();
		for (ContactInfo map : contactinfoList)
		{
			nameList.add(map.name);
			phoneList.add(map.number);
		}
		initNameToNumList();
	}

	private void initNameToNumList()
	{
		if (nameToNumList != null)
		{
			nameToNumList.clear();
		}
		for (String name : nameList)
		{
			String num = getNameNum(name);
			if (null != num)
			{
				nameToNumList.add(num);
			}
			else
			{
				nameToNumList.add(name);
			}
		}
	}

	private String getNameNum(String name)
	{
		if (name != null && name.length() != 0)
		{
			int len = name.length();
			char[] nums = new char[len];
			for (int i = 0; i < len; i++)
			{
				String tmp = name.substring(i);
				nums[i] = getOneNumFromAlpha(letterParser.getFirstAlpha(tmp));
			}
			return new String(nums);
		}
		return null;
	}

	private char getOneNumFromAlpha(char firstAlpha)
	{
		switch (firstAlpha)
		{
		case 'a':
		case 'b':
		case 'c':
			return '2';
		case 'd':
		case 'e':
		case 'f':
			return '3';
		case 'g':
		case 'h':
		case 'i':
			return '4';
		case 'j':
		case 'k':
		case 'l':
			return '5';
		case 'm':
		case 'n':
		case 'o':
			return '6';
		case 'p':
		case 'q':
		case 'r':
		case 's':
			return '7';
		case 't':
		case 'u':
		case 'v':
			return '8';
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			return '9';
		default:
			return '0';
		}
	}

	public void setData(List<ContactInfo> lists){
		this.contactinfoList = (ArrayList<ContactInfo>) lists;
	}
	
	@Override
	public int getCount()
	{
		return contactinfoList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return contactinfoList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	public class ViewHolder
	{
		public ImageView imPhoto;
		public TextView tvItemName;
		public TextView tvItemNumAddr;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		String name = "";
		String phone = "";
		String contactId = null;
		if (null == convertView)
		{
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.dialcontactitem, null);
			viewHolder.tvItemName = (TextView) convertView.findViewById(R.id.TvDialContactName);
			viewHolder.tvItemNumAddr = (TextView) convertView.findViewById(R.id.TvNumAddr);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ContactInfo map = contactinfoList.get(position);
		name = map.name;
		phone = map.number;

		contactId = String.valueOf(map.personId);
		viewHolder.tvItemName.setText(name);
		viewHolder.tvItemNumAddr.setText(phone);

		viewHolder.tvItemNumAddr.setTag(contactId);
		return convertView;
	}

	/***
	 * �����Ǹ�ListView��ӹ��˷��������԰������ּ������룬���ּ������֡�
	 */
	@Override
	public Filter getFilter()
	{
		Filter filter = new Filter()
		{

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results)
			{
				contactinfoList = (ArrayList<ContactInfo>) results.values;
				if (results.count > 0)
				{
					notifyDataSetChanged();
				}
				else
				{
					notifyDataSetInvalidated();
				}
			}

			@Override
			protected FilterResults performFiltering(CharSequence s)
			{
				FilterResults results = new FilterResults();
				ArrayList<ContactInfo> result = new ArrayList<ContactInfo>();

				if (oldInfoList != null && oldInfoList.size() != 0)
				{
					if (letterParser.isNumeric(s.toString()) && phoneList != null)
					{
						for (int i = 0; i < phoneList.size(); i++)
						{
							if (nameToNumList.get(i).contains(s))
							{
								result.add(oldInfoList.get(i));
							}
							else if (letterParser.numberMatch(phoneList.get(i), s.toString()))
							{
								result.add(oldInfoList.get(i));
							}
						}
					}
				}
				results.values = result;
				results.count = result.size();
				return results;
			}
		};
		return filter;
	}
}
