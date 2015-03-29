package cn.edu.sdu.online.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import cn.edu.sdu.online.entity.Place;
import cn.edu.sdu.online.entity.Task;
import cn.edu.sdu.online.entity.User;
import cn.edu.sdu.online.util.FormFile;
import cn.edu.sdu.online.util.ImageService;
import cn.edu.sdu.online.util.SocketHttpRequester;

public class NetCore {
	// private final static String ServerAddr =
	// "http://211.87.225.164:8080/Dajiabang/";
	private final static String ServerAddr = "http://202.194.14.195:8080/Dajiabang/";

	private final String LoginAddr = ServerAddr + "login.action";
	public static String jsessionid = null;
	// private final String RegisteAddr = ServerAddr + "user/register";
	private final String RegisteAddr = ServerAddr + "register.action";
	// SJQ
	private final String newListAddr = ServerAddr + "newList.action";
	private final String allListAddr = ServerAddr + "newListAll.action";
	private final String urgencyListAddr = ServerAddr + "urgencyList.action";
	private final String tipListAddr = ServerAddr + "tipList.action";
	private final String SearchAddr = ServerAddr + "searchList.action";
	// private final String ShakeAddr = ServerAddr + "action/shake";
	private final String TaskDetailAddr = ServerAddr + "taskDetail.action";
	// private final String ShowRecieveAddr = ServerAddr
	// + "action/showRecieveListByUser";
	private final String ShowReleaseAddr = ServerAddr + "showListByUser.action";
	private final String FinishAddr = ServerAddr + "finishTask.action";
	private final String DeleteAddr = ServerAddr + "deleteTask.action";
	// private final String ActivateAddr = ServerAddr + "action/activateTask";
	private final String PublishAddr = ServerAddr + "addTask.action";
	private final String UploadAddr = ServerAddr + "Upload.action";
	private final String UserinfoAddr = ServerAddr + "userinfo.action";
	private final String ChangeUserInfoAddr = ServerAddr
			+ "changeUserInfo.action";
	private final String ChangePasswordAddr = ServerAddr
			+ "changePassword.action";
	public final static String DownloadPictruesAddr = ServerAddr + "uploads";// 全图
	public final static String DownloadSmallPictruesAddr = ServerAddr + "small";// 缩略图

	// private final String GetUserInfoAddr = ServerAddr + "userinfo.action";
	// private final String GetMesCount = ServerAddr + "unreadnum.action";
	// private final String MakeMesReadAddr = ServerAddr + "readmessage.action";
	// private final String GetUserAllMesAddr = ServerAddr
	// + "usermessagelist.action";
	// private final String GetProductAllMesAddr = ServerAddr
	// + "productmessagelist.action";
	// private final String GetUnreadMesAddr = ServerAddr
	// + "showunreadlist.action";
	// // private final String SendMesAddr = ServerAddr + "sendmessage.action";
	// private final String GetSearchListAddr = ServerAddr + "search.action";
	// private final String GetSortListAddr = ServerAddr
	// + "ShowListByCategory.action";
	// private final String GetProductDetailAddr = ServerAddr
	// + "ProductDetail.action";
	// private final String AddProductAddr = ServerAddr + "AddProduct.action";
	// private final String GetDealListAddr = ServerAddr +
	// "ShowListByUser.action";
	// private final String MakeDealFinishAddr = ServerAddr +
	// "finishtrade.action";
	// private final String GetAllNewListAddr = ServerAddr + "Newlist.action";
	// private final String GetSchoolNewListAddr = ServerAddr +
	// "Newlist.action";
	// private final String UploadPictruesAddr = ServerAddr + "Upload.action";
	// private final String change_information = ServerAddr +
	// "updateuser.action";

	// http://202.194.14.195:8080/MyEnet/uploads/94e92c09-7233-4ba2-9fee-107bdcbee2ac/1400424861395.jpg

	/**
	 * 服务器返回值：
	 * 
	 */

	public final static int ERROR = -1;
	public final static int NET_ERROR = 404;
	// 登录返回结果
	public final static int LOGIN_SUCCESS = 1;
	public final static int LOGIN_ERROR = 0;

	// 注册返回值结果
	public static final int REGISTER_ALREADY = 0;
	public static final int REGISTER_SUCCEESS = 1;
	public static final int REGISTER_FAILE = 2;
	// 修改个人信息返回值结果
	public final static int CHANGE_SUCCESS = 1;// 改变成功
	public final static int CHANGE_ERROR = 0;// 改变失败

	public final static int SEND_MESSAGE_SUCCESS = 0;
	public final static int SEND_MESSAGE_ERROR = 1;

	public final static int READ_MESSAGE_SUCCESS = 0;

	public final static int FINISH_DEAL_SUCCESS = 0;
	public final static int FINISH_DEAL_ERROR = 1;

	public final static int ADD_PRODUCT_SUCCESS = 0;
	public final static int ADD_PRODUCT_ERROR = 1;

	public final static int UPLOAD_PIC_SUCCESS = 0;
	public final static int UPLOAD_PIC_ERROR = 1;

	//
	// user的具体信息
	//
	/**
	 * 登陆
	 * 
	 * 必填字段// user.email // user.password
	 * 
	 * @param user
	 *            用户
	 */
	public String Login(User user) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user.email", user.getEmail()));
		params.add(new BasicNameValuePair("user.password", user.getPassword()));
		String jsonData = "";
		try {
			jsonData = loginAndGetCookies(LoginAddr, params);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// int result = getJsonResult(jsonData);
		// JsonObject jo=new JsonObject();
		// jo.get
		return jsonData;
		// gson.
	}

	/**
	 * 注册 必填字段// user.email // user.password // user.phone // user.sex 选填字段 //
	 * user.userName // user.headphoto
	 * 
	 * @param user
	 *            用户
	 */
	public String Registe(User user) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// 必填
		params.add(new BasicNameValuePair("user.email", user.getEmail()));
		params.add(new BasicNameValuePair("user.password", user.getPassword()));

		String result = "";
		Log.v("REGISTER_ACTIVITY", RegisteAddr);
		try {
			result = GetResultFromNet(RegisteAddr, params);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Gson gson = new Gson();
		// Map<String, Integer> map = new HashMap<String, Integer>();
		// map = gson.fromJson(result, Map.class);
		// double resultNum = map.get("result");
		// System.out.println(resultNum);
		return result;
	}

	public String PublishTask(Task task) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("task.userId", task.getUserId()));
		params.add(new BasicNameValuePair("task.content", task.getContent()));
		params.add(new BasicNameValuePair("task.awardStatus", task
				.getAwardStatus() + ""));
		params.add(new BasicNameValuePair("task.tipAward", task.getTipAward()
				+ ""));
		params.add(new BasicNameValuePair("task.spiritAward", task
				.getSpiritAward() + ""));
		params.add(new BasicNameValuePair("task.limitTime", task.getLimitTime()
				+ ""));
		params.add(new BasicNameValuePair("task.details", task.getDetails()
				+ ""));
		params.add(new BasicNameValuePair("task.destination", task
				.getDestination()));
		params.add(new BasicNameValuePair("task.location", task.getLocation()));
		params.add(new BasicNameValuePair("task.x", task.getLocation_x() + ""));
		params.add(new BasicNameValuePair("task.y", task.getLocation_y() + ""));
		params.add(new BasicNameValuePair("task.x1", task.getLocation_x1() + ""));
		params.add(new BasicNameValuePair("task.y1", task.getLocation_y1() + ""));

		String jsonData = "";
		try {
			jsonData = GetResultFromNet(PublishAddr, params);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// int result = getJsonResult(jsonData);
		// JsonObject jo=new JsonObject();
		// jo.get
		return jsonData;
		// gson.
	}

	/**
	 * 获取任务列表（广场） *
	 * 
	 * @param stuNo
	 *            用户stuNo tag 0: new 1:all 2:urge 3:tip
	 */

	public String GetTask(int numCursor, Place place, int tag) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("numCursor", numCursor + ""));
		params.add(new BasicNameValuePair("task.x", place.getLatitude() + ""));
		params.add(new BasicNameValuePair("task.y", place.getLongitude() + ""));
		String result = "";
		try {
			switch (tag) {
			case 0:
				result = GetResultFromNet(newListAddr, params);
				break;
			case 1:
				result = GetResultFromNet(allListAddr, params);
				break;
			case 2:
				result = GetResultFromNet(urgencyListAddr, params);
				break;
			case 3:
				result = GetResultFromNet(tipListAddr, params);
				break;
			default:
				break;
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取任务列表（搜索） *
	 * 
	 */
	public String GetTaskListByKey(String key, int numCursor, Place place) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key + ""));
		params.add(new BasicNameValuePair("numCursor", numCursor + ""));
		params.add(new BasicNameValuePair("task.x", place.getLatitude() + ""));
		params.add(new BasicNameValuePair("task.y", place.getLongitude() + ""));
		String result = "";
		try {
			result = GetResultFromNet(SearchAddr, params);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	// /**
	// * 获取任务列表（摇） *
	// *
	// */
	// public String GetTaskListByLocation(float xLocation,
	// float yLocation, String location) {
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	//
	// params.add(new BasicNameValuePair("xLocation", xLocation + ""));
	// params.add(new BasicNameValuePair("yLocation", yLocation + ""));
	// params.add(new BasicNameValuePair("location", location + ""));
	// String result = "";
	// try {
	// result = GetResultFromNet(ShakeAddr, params);
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return result;
	// }
	//

	/**
	 * 获取任务的所有信息
	 * 
	 * @param productId
	 */
	public String GetProductAllMes(String taskId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", taskId));
		String result = "";
		try {
			result = GetResultFromNet(TaskDetailAddr, params);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取已发布任务列表
	 * 
	 * @param userId
	 *            用户id
	 */
	public String GetReleaseDealList(String userId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userId", userId));
		String result = "";
		try {
			result = GetResultFromNet(ShowReleaseAddr, params);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取最近联系的任务列表
	 * 
	 * @param userId
	 *            用户id
	 */
	public String GetRecieveDealList(String userId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", userId));
		String result = "";
		// try {
		// result = GetResultFromNet(ShowRecieveAddr, params);
		// } catch (ClientProtocolException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		return result;
	}

	/**
	 * 设置已完成
	 * 
	 * @param taskId
	 *            任务id tag=0,finish tag=1,delete
	 */
	public String setFDTask(String taskId, int tag) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", taskId));
		String result = "";
		try {
			if (tag == 0)
				result = GetResultFromNet(FinishAddr, params);
			else if (tag == 1)
				result = GetResultFromNet(DeleteAddr, params);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	// /**
	// * 重新激活
	// *
	// * @param taskId
	// * 任务id
	// */
	// public String setActivateTask(String taskId) {
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("id", taskId));
	// String result = "";
	// try {
	// result = GetResultFromNet(ActivateAddr, params);
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return result;
	// }

	/**
	 * 发布任务
	 * 
	 * @param product
	 *            商品信息
	 */
	// public String AddTask(Task task) {
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("task.userName",
	// task.getPublisherName()
	// + ""));
	// params.add(new BasicNameValuePair("task.taskContent", task
	// .getTaskContent() + ""));
	// params.add(new BasicNameValuePair("task.userId", task.getPublisherId() +
	// ""));
	// params.add(new BasicNameValuePair("task.awardCatogry", task
	// .getAwardCatogry() + ""));
	// params.add(new BasicNameValuePair("task.awardContent", task
	// .getAwardContent() + ""));
	// params.add(new BasicNameValuePair("task.telephone", task.getTelephone()
	// + ""));
	// params.add(new BasicNameValuePair("task.deadLine", task.getDeadLine()
	// + ""));
	// params.add(new BasicNameValuePair("task.taskDetail", task
	// .getTaskDetail() + ""));
	//
	// String jsonData = "";
	// try {
	// jsonData = GetResultFromNet(ReleaseAddr, params);
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return jsonData;
	// }

	/**
	 * 上传图片
	 */
	public String UpLoadPicture(String path, int pictureChoice, String userId) {
		String result = "";
		Map<String, String> params = new HashMap<String, String>();
		// params.put("method", "save");
		params.put("picturechoice", pictureChoice + "");
		params.put("userId", userId);

		File uploadFile = new File(path);
		FormFile formfile = new FormFile(uploadFile.getName(), uploadFile,
				"myFile", null);
		try {
			result = SocketHttpRequester.post(UploadAddr, params, formfile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取用户信息
	 * 
	 * @param email
	 *            用户email
	 */
	public String GetUserInfo(String email) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		String result = "";
		try {
			result = GetResultFromNet(UserinfoAddr, params);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String Change_person_information(User user) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new BasicNameValuePair("user.stuNo", user.stuNo));
		// params.add(new BasicNameValuePair("user.realName", user.realName));

		params.add(new BasicNameValuePair("user.id", user.getId()));
		params.add(new BasicNameValuePair("user.nickName", user.getNickName()));
		params.add(new BasicNameValuePair("user.sex", user.getSex() + ""));
		params.add(new BasicNameValuePair("user.phoneNo", user.getPhoneNo()));
		params.add(new BasicNameValuePair("user.school", user.getSchool()));
		params.add(new BasicNameValuePair("user.weixin", user.getWeixin()));
		params.add(new BasicNameValuePair("user.qq", user.getQq()));
		params.add(new BasicNameValuePair("user.birthday", user.getBirthday()));

		// System.out.println(user.userName+user.password+user.address+user.phone+user.email+user.dormNo+user.dormPhone);
		String result = "";
		try {
			result = GetResultFromNet(ChangeUserInfoAddr, params);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(result);

		return result;
	}

	// 修改密码
	public String ChangePassword(User user, String newpassword) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new BasicNameValuePair("user.stuNo", user.stuNo));
		// params.add(new BasicNameValuePair("user.realName", user.realName));

		params.add(new BasicNameValuePair("user.id", user.getId()));
		params.add(new BasicNameValuePair("user.password", user.getPassword()));

		params.add(new BasicNameValuePair("newpassword", newpassword));
		// Log.v("ChangePassW", "oldPass:"+oldPass+"newpass"+newPass);
		// System.out.println(user.userName+user.password+user.address+user.phone+user.email+user.dormNo+user.dormPhone);
		String result = "";
		try {
			result = GetResultFromNet(ChangePasswordAddr, params);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(result);

		return result;
	}

	// //
	// // message
	// //
	// /**
	// * 得到信息的数量
	// */
	// public int GetMesNum(String userId) {
	// int num = 0;
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("id", userId));
	// String result = "";
	// try {
	// result = GetResultFromNet(GetMesCount, params);
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// JSONObject jsonObject;
	// try {
	// jsonObject = new JSONObject(result);
	// num = jsonObject.getInt("result");
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return num;
	//
	// }
	//
	// /**
	// * 设置短信已读
	// *
	// * @param mesId
	// * 消息id
	// */
	// public String MakeMesRead(String mesId) {
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("id", mesId));
	// String result = "";
	// try {
	// result = GetResultFromNet(MakeMesReadAddr, params);
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return result;
	// }

	// /**
	// * 获取用户所有信息
	// *
	// * @param userId
	// * 用户id
	// */
	// public String GetUserAllMes(String userId) {
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("id", userId));
	// String result = "";
	// try {
	// result = GetResultFromNet(GetUserAllMesAddr, params);
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return result;
	// }
	//
	// /**
	// * 获取未读信息
	// *
	// * @param userId
	// * 用户id
	// */
	// public String GetUnreadMes(String userId) {
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("id", userId));
	// String result = "";
	// try {
	// result = GetResultFromNet(GetUnreadMesAddr, params);
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return result;
	// }

	// /**
	// * 发送信息 //message.sourceId // message.targetId // message.productId //
	// * message.content
	// *
	// * @param msg
	// * 信息
	// */
	// public String SendMes(Messages msg) {
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("message.sourceId", msg.sourceId));
	// params.add(new BasicNameValuePair("message.targetId", msg.targetId));
	// params.add(new BasicNameValuePair("message.productId", msg.productId));
	// params.add(new BasicNameValuePair("message.content", msg.content));
	// String result = "";
	// try {
	// result = GetResultFromNet(SendMesAddr, params);
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return result;
	// }

	/**
	 * 下载图片
	 * 
	 */
	public Bitmap DownloadPicture(String userId) {
		String path = DownloadPictruesAddr + "/" + userId + "/" + userId
				+ ".jpg";
		// Log.v("ShouYeFragment", "图片路径："+path);
		// String path = DownloadPictruesAddr + "/"
		// + "1e468b92-4d50-44e7-879b-d0cb262f872d" + "/"
		// + "1e468b92-4d50-44e7-879b-d0cb262f872d" + ".jpg";
		Bitmap bitmap = null;
		try {
			byte[] data = ImageService.getImage(path);// 获取图片数据
			if (data != null) {
				// 构建位图对象
				Log.v("ShouYeFragment", "data.length" + data.length);
				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				if (bitmap == null) {
					Log.v("ShouYeFragment", "获取图片失败");
				} else {
					Log.v("ShouYeFragment", "获取图片成功");
				}

			}
		} catch (Exception e) {

		}
		return bitmap;
	}

	//
	/**
	 * 
	 * @param url
	 *            地址
	 * @param params
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws Exception
	 */
	private String GetResultFromNet(String url, List<NameValuePair> params)
			throws ClientProtocolException, IOException {
		HttpPost httpRequest = new HttpPost(url);

		httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		httpRequest.setHeader("Cookie", "JSESSIONID=" + jsessionid);
		DefaultHttpClient httpClient = new DefaultHttpClient();

		HttpResponse httpResponse = httpClient.execute(httpRequest);

		String jsonData = "";
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			InputStream is = httpResponse.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while ((line = br.readLine()) != null) {
				jsonData += line + "\r\n";
			}
		}
		System.out.println(jsonData);
		return jsonData;
	}

	/**
	 * 登录并获得cookie
	 * 
	 */
	private String loginAndGetCookies(String url, List<NameValuePair> params)
			throws ClientProtocolException, IOException {
		HttpPost httpRequest = new HttpPost(url);
		httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = httpClient.execute(httpRequest);
		String jsonData = "";
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			CookieStore store = httpClient.getCookieStore();
			List<Cookie> cookies = store.getCookies();
			// Log.v("LoginActivity", cookies.toString());

			for (int i = 0; i < cookies.size(); i++) {
				if ("JSESSIONID".equals(cookies.get(i).getName())) {
					jsessionid = cookies.get(i).getValue();
					Log.v("LoginActivity", jsessionid);
					break;
				}
			}

			InputStream is = httpResponse.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while ((line = br.readLine()) != null) {
				jsonData += line + "\r\n";
			}
		}
		System.out.println(jsonData);
		return jsonData;
	}
	/**
	 * 发送信息 //message.sourceId // message.targetId // message.productId //
	 * message.content
	 * 
	 * @param msg
	 *            信息
	 */

}
