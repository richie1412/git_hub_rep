package com.wxxr.callhelper.qg.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.mm.sdk.platformtools.Log;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.rpc.UserDetailVO;
import com.wxxr.callhelper.qg.rpc.UserVO;
import com.wxxr.callhelper.qg.service.IClientCustomService;
import com.wxxr.callhelper.qg.utils.ImageUtils;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.utils.StringUtil;
import com.wxxr.callhelper.qg.utils.Tools;

/**
 * 个人中心——个人资料编辑界面
 * 
 * @author cuizaixi
 * 
 */
public class PrivateZooEditActivity extends BaseActivity {
	private static final int REQ_CODE_PHOTO_CROP = 2;
	public final static int REQ_CODE_DIALOG = 201;
	private TextView tv_right;
	private TextView tv_gender;
	private ImageView iv_icon;
	private TextView tv_region;
	private TextView tv_age;
	private EditText et_nick_name;
	private Bitmap photo;

	private LinearLayout ll_left;
	private LinearLayout ll_right;
	private LinearLayout ll_region;
	private LinearLayout ll_age;
	private LinearLayout ll_gender;

	public final static String ACTION_CROP_IMAGE = "android.intent.action.CROP";
	private static final int REQ_CODE_LOCALE_BG = 0;
	public final static String IMAGE_URI = "iamge_uri";
	public final static String CROP_IMAGE_URI = "crop_image_uri";

	private UserDetailVO user;
	private String region;
	private TextView tv_middle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.private_zoo_personal_information_edit);
		findView();
		processLogic();
	}

	private void findView() {
		tv_right = (TextView) findViewById(R.id.tv_private_zoo_title_right);
		tv_middle = (TextView) findViewById(R.id.tv_private_zoo_title_middle);
		ll_left = (LinearLayout) findViewById(R.id.ll_private_zoo_title_left);
		ll_right = (LinearLayout) findViewById(R.id.ll_private_zoo_title_right);
		tv_gender = (TextView) findViewById(R.id.tv_private_zoo_gender);
		tv_age = (TextView) findViewById(R.id.tv_private_zoo_age);
		tv_region = (TextView) findViewById(R.id.tv_private_zoo_region);
		et_nick_name = (EditText) findViewById(R.id.et_private_zoo_nick_name);
		iv_icon = (ImageView) findViewById(R.id.iv_private_zoo_icon);
		ll_gender = (LinearLayout) findViewById(R.id.ll_private_zoo_gender);
		ll_age = (LinearLayout) findViewById(R.id.ll_private_zoo_age);
		ll_region = (LinearLayout) findViewById(R.id.ll_private_zoo_region);

	}

	private void processLogic() {
		iv_icon.setOnClickListener(this);
		ll_left.setOnClickListener(this);
		ll_right.setOnClickListener(this);
		ll_gender.setOnClickListener(this);
		ll_region.setOnClickListener(this);
		ll_age.setOnClickListener(this);
		tv_middle.setText("个人信息");
		// 设置编辑界面的用户信息—头像、昵称、性别、年龄
		String id = getService(IUserActivationService.class).getCurrentUserId();
		String filepath = this.getFilesDir() + File.separator + id ;
		File file = new File(filepath);
		if (file.exists()) {
			Bitmap bmp = ImageUtils.getBitmap(this, id);
			iv_icon.setVisibility(View.VISIBLE);
			iv_icon.setImageBitmap(bmp);
		}
		getService(IUserActivationService.class).getUserDetail(
				new CMProgressMonitor(PrivateZooEditActivity.this) {
					protected void handleFailed(Throwable cause) {
					}

					protected void handleDone(final Object returnVal) {

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								user = (UserDetailVO) returnVal;
								if (user.getNickName() != null
										&& !"".equals(user.getNickName())) {
									et_nick_name.setText(user.getNickName());
									et_nick_name.setSelection(user
											.getNickName().length());
								}
								if (user.isMale()) {
									tv_gender.setText("男");
								} else {
									tv_gender.setText("女");
								}
								if (user.getBirthday() != 0) {
									try {
										int age = Tools
												.getCurrentAgeByBirthdate(String
														.valueOf(user
																.getBirthday()));
										tv_age.setText(String.valueOf(age));
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						});
					}
				});
		String proviceCode = getService(IClientCustomService.class)
				.getProviceCode();
		String proviceName = Constant.getproviceName(proviceCode);
		if (proviceName != null) {
			tv_region.setText(proviceName);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_private_zoo_title_left:
			finish();
			break;
		case R.id.ll_private_zoo_title_right:
			ManagerSP.getInstance(PrivateZooEditActivity.this).update(
					Constant.REFRESH_HOME, 1);
			// tv_right.setTextColor(Color.parseColor("#ffffff"));
			if (photo != null) {
				try {
					String  id=getService(IUserActivationService.class).getCurrentUserId();
					ImageUtils.saveImage(this, id, photo);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 更新昵称、性别、年龄、手机号码所在地
			if (et_nick_name.getText().toString().length() > 0) {
				if (et_nick_name.getText().toString().length() > 6) {
					showMessageBox("昵称不能超过6个字符，请重新输入！");
					return;
				}
				user.setNickName(et_nick_name.getText().toString());
			} else if (et_nick_name.getText().toString().length() == 0) {
				user.setNickName("");
			}
	         region = tv_region.getText().toString();
			if (StringUtil.isNotEmpty(region)) {
                getService(IClientCustomService.class).setProviceCode(
                        Constant.getProviceCode(region));
                user.setProvinceCode(Constant.getProviceCode(region));
            }
			getService(IUserActivationService.class).syncUserDetail(user,
					new CMProgressMonitor(PrivateZooEditActivity.this) {

						@Override
						protected void handleFailed(Throwable cause) {
							showMessageBox("更新失败");
						}

						@Override
						protected void handleDone(Object returnVal) {
							showMessageBox("更新成功");
							Log.i("handleDone", user.toString());
						}
					});
			
			finish();
			break;
		case R.id.iv_private_zoo_icon:
			Intent in_icon = new Intent(PrivateZooEditActivity.this,
					ConfirmDialogActivity.class);
			in_icon.putExtra(Constant.DIALOG_KEY, Constant.ICON);
			startActivityForResult(in_icon, REQ_CODE_DIALOG);
			break;
		case R.id.ll_private_zoo_gender:
			Intent in_gender = new Intent(PrivateZooEditActivity.this,
					ConfirmDialogActivity.class);
			in_gender.putExtra(Constant.DIALOG_KEY, Constant.GENDER);
			in_gender.putExtra("gender", user.isMale());
			startActivityForResult(in_gender, 1);
			break;
		case R.id.ll_private_zoo_age:
			Intent in_age = new Intent(PrivateZooEditActivity.this,
					ConfirmDialogActivity.class);
				in_age.putExtra("birday", user.getBirthday());
			in_age.putExtra(Constant.DIALOG_KEY, Constant.AGE);
			startActivityForResult(in_age, 1);
			break;
		case R.id.ll_private_zoo_region:
			Intent in = new Intent(PrivateZooEditActivity.this,
					PrivateZooLocationActivity.class);
			startActivityForResult(in, 1);
			break;
		default:
			break;
		}
	}

	private void getLocalImage(int reqCode) {
		try {
			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, reqCode);
		} catch (Exception e1) {
			try {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, reqCode);
			} catch (Exception e2) {
			}
		}
	}

	@Override
	protected void onResume() {
		// tv_right.setTextColor(Color.parseColor("#7fffffff"));
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			if (requestCode == REQ_CODE_LOCALE_BG) {
				readLocalImage(data);
			} else if (requestCode == REQ_CODE_PHOTO_CROP) {
				readCropImage(data);
			} else if (requestCode == REQ_CODE_DIALOG) {
				getLocalImage(REQ_CODE_LOCALE_BG);
			}

		}
		if (resultCode == Constant.GENDER) {
			String gender = data.getStringExtra("gender");
			user.setMale(Tools.isMale(gender));
			setGender();
		} else if (resultCode == Constant.AGE) {
			String date = data.getStringExtra("date");
			user.setBirthday(Integer.parseInt(date));
			setAge();
		} else if (resultCode == Constant.REGION) {
			region = data.getStringExtra("region");
			if (!"".equals(region)) {
				tv_region.setText(region);
			}
		}

	}

	public void setGender() {
		if (user.isMale()) {
			tv_gender.setText("男");
		} else {
			tv_gender.setText("女");
		}
	}

	public void setAge() {

		try {
			int age = Tools.getCurrentAgeByBirthdate(String.valueOf(user
					.getBirthday()));
			tv_age.setText(String.valueOf(age));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @Title: readLocalImage
	 * @param data
	 * @return void
	 */
	private void readLocalImage(Intent data) {
		if (data == null) {
			return;
		}

		Uri uri = null;
		uri = data.getData();

		if (uri != null) {

			startPhotoCrop(uri, null, REQ_CODE_PHOTO_CROP);
		}
	}

	/**
	 * 
	 * @Title: readCropImage
	 * @param data
	 * @return void
	 */

	private void readCropImage(Intent data) {

		if (data == null) {
			return;
		}
		Uri uri = data.getParcelableExtra(CROP_IMAGE_URI);

		photo = getBitmap(uri);

		iv_icon.setImageBitmap(photo);

	}

	/**
	 * 
	 * @Title: getBitmap
	 * @param intent
	 * @return void
	 */
	private Bitmap getBitmap(Uri uri) {
		Bitmap bitmap = null;
		InputStream is = null;
		try {

			is = getInputStream(uri);

			bitmap = BitmapFactory.decodeStream(is);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ignored) {
				}
			}
		}
		return bitmap;
	}

	/**
	 * @Title: getInputStream
	 * @param mUri
	 * @return
	 * @return InputStream
	 */
	private InputStream getInputStream(Uri mUri) throws IOException {
		try {
			if (mUri.getScheme().equals("file")) {
				return new java.io.FileInputStream(mUri.getPath());
			} else {
				return this.getContentResolver().openInputStream(mUri);
			}
		} catch (FileNotFoundException ex) {
			return null;
		}
	}

	private void startPhotoCrop(Uri uri, String duplicatePath, int reqCode) {

		Intent intent = new Intent(ACTION_CROP_IMAGE);
		intent.putExtra(IMAGE_URI, uri);
		startActivityForResult(intent, reqCode);
	}
}
