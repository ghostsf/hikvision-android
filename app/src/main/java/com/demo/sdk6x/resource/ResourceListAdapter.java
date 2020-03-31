package com.demo.sdk6x.resource;

import java.util.List;

import com.demo.sdk6x.R;
import com.hikvision.vmsnetsdk.CameraInfo;
import com.hikvision.vmsnetsdk.ControlUnitInfo;
import com.hikvision.vmsnetsdk.RegionInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ResourceListAdapter extends BaseAdapter {
	
	private Context mContext = null;
	private List mList = null;
	private LayoutInflater mListContainer = null;
	
	
	public ResourceListAdapter(Context context, List list) {
		mContext = context;
		mList = list;
		// 创建视图容器并设置上下文
		mListContainer = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		if (mList == null) {
			return 0;
		}
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		if (mList == null) {
			return null;
		}
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			// 获取list_item布局文件的视图
			convertView = mListContainer.inflate(R.layout.resource_item_layout, null);
			// 获取控件对象
			viewHolder.tv = (TextView) convertView.findViewById(R.id.item_txt);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		//
		Object itemData = getItem(position);
		String itemName = getItemName(itemData);
		viewHolder.tv.setText(itemName);
		
		return convertView;
	}

	//获得资源列表名称
	private String getItemName(Object itemData) {
		if (itemData instanceof ControlUnitInfo) {
			ControlUnitInfo info = (ControlUnitInfo) itemData;
			return info.getName();
		}

		if (itemData instanceof RegionInfo) {
			RegionInfo info = (RegionInfo) itemData;
			return info.getName();
		}

		if (itemData instanceof CameraInfo) {
			CameraInfo info = (CameraInfo) itemData;
			return info.getName();
		}

		return null;
	}
	
	public void setData(List data) {
		this.mList = data;
		notifyDataSetChanged();
	}
	
	class ViewHolder {
		TextView tv = null;
	}
	
}
