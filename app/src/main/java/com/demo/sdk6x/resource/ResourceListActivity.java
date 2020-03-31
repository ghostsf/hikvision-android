package com.demo.sdk6x.resource;

import java.util.ArrayList;
import java.util.List;

import com.demo.sdk6x.R;
import com.demo.sdk6x.callback.MsgCallback;
import com.demo.sdk6x.callback.MsgIds;
import com.demo.sdk6x.constants.Constants;
import com.demo.sdk6x.data.TempData;
import com.demo.sdk6x.listviewlayout.ListViewForScrollView;
import com.demo.sdk6x.live.LiveActivity;
import com.demo.sdk6x.playback.PlayBackActivity;
import com.demo.sdk6x.utils.UIUtil;
import com.hikvision.vmsnetsdk.CameraInfo;
import com.hikvision.vmsnetsdk.ControlUnitInfo;
import com.hikvision.vmsnetsdk.RegionInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ResourceListActivity extends Activity implements MsgCallback,OnItemClickListener{

	/**
	 * 资源列表
	 */
	private ListViewForScrollView resourceListView;
	private List mList;
	private ResourceListAdapter mAdapter;
//	private ScrollView sv;
	/**
	 * 父节点资源类型，TYPE_UNKNOWN表示首次获取资源列表
	 */
	private int pResType = Constants.Resource.TYPE_UNKNOWN;
	/**
	 * 父控制中心的id，只有当parentResType为TYPE_CTRL_UNIT才有用
	 */
	private int pCtrlUnitId;
	/**
	 * 父区域的id，只有当parentResType为TYPE_REGION才有用
	 */
	private int pRegionId;
	/**
	 * 消息处理Handler
	 */
	private MsgHandler handler;
	/**
	 * 获取资源逻辑控制类
	 */
	private ResourceControl rc;
	private Dialog mDialog;
	
	private static final int GOTO_LIVE_OR_PLAYBACK = 0x0b;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resource_list_activity);
		
		resourceListView = (ListViewForScrollView) findViewById(R.id.ctrlunit_list);
		
		resourceListView.setOnItemClickListener(this);
//		sv = (ScrollView) findViewById(R.id.mScrollView);
//		sv.smoothScrollTo(0, 0);
		mList = new ArrayList();
		handler = new MsgHandler();
		rc = new ResourceControl();
		rc.setCallback(this);
//		initData();
		
		mAdapter = new ResourceListAdapter(ResourceListActivity.this ,mList);
		resourceListView.setAdapter(mAdapter);
		
		reqResList();
	}

//	private void setListViewHeightBasedOnChildren(ListView listView) {
//		ListAdapter listAdapter = listView.getAdapter();
//		if (listAdapter == null) {
//			return;
//		}
//
//		int totalHeight = 0;
//		for (int i = 0; i < listAdapter.getCount(); i++) {
//			View listItem = listAdapter.getView(i, null, listView);
//			listItem.measure(0, 0);
//			totalHeight += listItem.getMeasuredHeight();
//		}
//
//		ViewGroup.LayoutParams params = listView.getLayoutParams();
//		params.height = totalHeight
//		+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//		listView.setLayoutParams(params);
//	}
	
//	/**
//	 * 初始化数据
//	 */
//	private void initData() {
//		Intent intent = getIntent();
//		if (intent.hasExtra(Constants.IntentKey.CONTROL_UNIT_ID)) {
//			pResType = Constants.Resource.TYPE_CTRL_UNIT;
//			pCtrlUnitId = intent
//					.getIntExtra(Constants.IntentKey.CONTROL_UNIT_ID, 0);
//			Log.i(Constants.LOG_TAG,
//					"Getting resource from ctrlunit.parent id is "
//							+ pCtrlUnitId);
//		} else if (intent.hasExtra(Constants.IntentKey.REGION_ID)) {
//			pResType = Constants.Resource.TYPE_REGION;
//			pRegionId = intent.getIntExtra(Constants.IntentKey.REGION_ID, 0);
//			Log.i(Constants.LOG_TAG,
//					"Getting resource from region. parent id is " + pRegionId);
//		} else {
//			pResType = Constants.Resource.TYPE_UNKNOWN;
//			Log.i(Constants.LOG_TAG, "Getting resource for the first time.");
//		}
//	}
	
	/**
	 * 请求资源列表
	 */
	private void reqResList() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				int pId = 0;
				if (Constants.Resource.TYPE_CTRL_UNIT == pResType) {
					pId = pCtrlUnitId;
				} else if (Constants.Resource.TYPE_REGION == pResType) {
					pId = pRegionId;
				}
				rc.reqResList(pResType, pId);
			}
		}).start();
	}
	
	
	@SuppressLint("HandlerLeak")
    private final class MsgHandler extends Handler {
        @Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

			// 获取控制中心列表成功
			case MsgIds.GET_C_F_NONE_SUC:
				
				// 从控制中心获取下级资源列表成功
			case MsgIds.GET_SUB_F_C_SUC:
				
				// 从区域获取下级列表成功
			case MsgIds.GET_SUB_F_R_SUC:
				refreshResList((List)msg.obj);
				break;
				
			// 获取控制中心列表失败
			case MsgIds.GET_C_F_NONE_FAIL:
				
				// 调用getControlUnitList失败
			case MsgIds.GET_CU_F_CU_FAIL:
				
				// 调用getRegionListFromCtrlUnit失败
			case MsgIds.GET_R_F_C_FAIL:
				
				// 调用getCameraListFromCtrlUnit失败
			case MsgIds.GET_C_F_C_FAIL:
				
				// 从控制中心获取下级资源列表成失败
			case MsgIds.GET_SUB_F_C_FAIL:
				
				// 调用getRegionListFromRegion失败
			case MsgIds.GET_R_F_R_FAIL:
				
				// 调用getCameraListFromRegion失败
			case MsgIds.GET_C_F_R_FAIL:
				
				// 从区域获取下级列表失败
			case MsgIds.GET_SUB_F_R_FAILED:
				onGetResListFailed();
				break;
			case GOTO_LIVE_OR_PLAYBACK:
				CameraInfo cInfo = (CameraInfo)msg.obj;
				
				gotoLiveorPlayBack(cInfo);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 调用接口失败时，界面弹出提示
	 */
	private void onGetResListFailed() {
		UIUtil.showToast(this,
				getString(R.string.fetch_reslist_failed, UIUtil.getErrorDesc()));
	}

	/**
	 * 获取数据成功后刷新列表
	 * 
	 * @param data
	 */
	private void refreshResList(List data) {
		if (data == null || data.isEmpty()) {
			UIUtil.showToast(this, R.string.no_data_tip);
			return;
		}
		UIUtil.showToast(this, R.string.fetch_resource_suc);
//		if(mAdapter != null){
			mAdapter.setData(data);
//		}
	}

	@Override
	public void onMsg(int msgId, Object data) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		msg.what = msgId;
		msg.obj = data;
		handler.sendMessage(msg);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
		final Object itemData = mAdapter.getItem(position);
//		String itemName = getItemName(itemData);
		
			new Thread(new Runnable() {
			@Override
			public void run() {
				
				if (itemData instanceof CameraInfo) {
					CameraInfo info = (CameraInfo) itemData;
					//得到摄像头，进行预览或者回放
					Log.i(Constants.LOG_TAG,"get Camera:" + info.getName() + "---" + info.getDeviceID());
					onMsg(GOTO_LIVE_OR_PLAYBACK,info);
				}else{
					int pId = 0;
					if (itemData instanceof ControlUnitInfo) {
						ControlUnitInfo info = (ControlUnitInfo) itemData;
						pResType = Constants.Resource.TYPE_CTRL_UNIT;
						pId = Integer.parseInt(info.getControlUnitID());
					}

					if (itemData instanceof RegionInfo) {
						RegionInfo info = (RegionInfo) itemData;
						pResType = Constants.Resource.TYPE_REGION;
						pId = Integer.parseInt(info.getRegionID());
					}

					rc.reqResList(pResType, pId);
				}
				
			}

			
		}).start();
	}

	private void gotoLiveorPlayBack(final CameraInfo info) {
		// TODO Auto-generated method stub
		String[] datas = new String[]{"预览","回放"};
		mDialog = new AlertDialog.Builder(ResourceListActivity.this).setSingleChoiceItems(datas, 0, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mDialog.dismiss();
				switch (which) {
				case 0:
					gotoLive(info);
					break;
				case 1:
					gotoPlayback(info);
					break;
				default:
					break;
				}
			}

		}).create();
		mDialog.show();
	}

	/**实时预览
	 * @param info
	 */
	private void gotoLive(CameraInfo info) {
		// TODO Auto-generated method stub
		if(info == null){
            Log.e(Constants.LOG_TAG,"gotoLive():: fail");
            return;
        }
		Intent intent = new Intent(ResourceListActivity.this, LiveActivity.class);
		intent.putExtra(Constants.IntentKey.CAMERA_ID, info.getId());
		TempData.getIns().setCameraInfo(info);
		ResourceListActivity.this.startActivity(intent);
	}
	
	/**远程回放
	 * @param info
	 */
	private void gotoPlayback(CameraInfo info) {
		// TODO Auto-generated method stub
		if(info == null){
	        Log.e(Constants.LOG_TAG,"gotoPlayback():: fail");
	        return;
	    }
		Intent intent = new Intent(ResourceListActivity.this, PlayBackActivity.class);
		intent.putExtra(Constants.IntentKey.CAMERA_ID, info.getId());
		ResourceListActivity.this.startActivity(intent);
	}

	
}
