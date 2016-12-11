/**
 * <p>DemoActivity Class</p>
 * @author zhuzhenlei 2014-7-17
 * @version V1.0  
 * @modificationHistory
 * @modify by user: 
 * @modify by reason:
*/
package cn.fuck.videotest;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import org.MediaPlayer.PlayM4.Player;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.hikvision.netsdk.*;

import static com.hikvision.netsdk.ADDR_QUERY_TYPE.QUERYSVR_BY_COUNTRYID;

/**
 * <pre>
 *  ClassName  DemoActivity Class
 * </pre>
 * 
 * @author zhuzhenlei
 * @version V1.0
 * @modificationHistory
 */
public class DemoActivity extends Activity
{
	private Button          m_oLoginBtn         	= null;
	private Button          m_oPreviewBtn           = null;
	private Button			m_oPlaybackBtn			= null;
	private Button			m_oParamCfgBtn			= null;
	private Button			m_oCaptureBtn			= null;
	private Button			m_oRecordBtn			= null;
	private	Button			m_oTalkBtn				= null;
	private	Button			m_oPTZBtn				= null;
	private Button			m_oOtherBtn			= null;
	//private SurfaceView 	m_osurfaceView			= null;
	private EditText        m_oIPAddr				= null;
	private EditText		m_oPort					= null;
	private EditText		m_oUser					= null;
	private EditText		m_oPsd					= null;
	
	private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;
	
	
	private int				m_iLogID				= -1;				// return by NET_DVR_Login_v30
	private int 			m_iPlayID				= -1;				// return by NET_DVR_RealPlay_V30
	private int				m_iPlaybackID			= -1;				// return by NET_DVR_PlayBackByTime	
	
	private int				m_iPort					= -1;				// play port
	private	int 			m_iStartChan 			= 0;				// start channel no
	private int				m_iChanNum				= 0;				//channel number
	private static PlaySurfaceView [] playView = new PlaySurfaceView[9];
	
	private final String 	TAG						= "DemoActivity";
	
	private boolean			m_bTalkOn				= false;
	private boolean			m_bPTZL					= false;
	private boolean			m_bMultiPlay			= false;
	
	private boolean			m_bNeedDecode			= true;
	private RelativeLayout widget_Play;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		widget_Play= (RelativeLayout) findViewById(R.id.widget_Play);

        
        if (!initeSdk())
        {
        	this.finish();
        	return;
        }
        
        if (!initeActivity())
        {
        	this.finish();
        	return;
        }


		new OtherFunction().Test_Hikonline();
        		
        m_oIPAddr.setText("112.27.209.98");
        m_oPort.setText("8900");
        m_oUser.setText("admin");
        m_oPsd.setText("ch123456");
    }

	@Override  
	protected void onSaveInstanceState(Bundle outState) {    
		outState.putInt("m_iPort", m_iPort);  
		super.onSaveInstanceState(outState);  
		Log.i(TAG, "onSaveInstanceState"); 
	}  
	@Override  
	protected void onRestoreInstanceState(Bundle savedInstanceState) {  
		m_iPort = savedInstanceState.getInt("m_iPort");  
		super.onRestoreInstanceState(savedInstanceState);  
		Log.i(TAG, "onRestoreInstanceState" ); 
	}  
    /** 
     * @fn initeSdk
     * @author zhuzhenlei
     * @brief SDK init
     * @param NULL [in]
     * @param NULL [out]
     * @return true - success;false - fail
     */
    private boolean initeSdk()
	{
		//init net sdk
    	if (!HCNetSDK.getInstance().NET_DVR_Init())
    	{
    		Log.e(TAG, "HCNetSDK init is failed!");
    		return false;
    	}
    	HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/",true);
    	return true;
	}
    // GUI init
    private boolean initeActivity()
    {   	
    	findViews();      
    	//m_osurfaceView.getHolder().addCallback(this);
    	setListeners();
    	return true;
    }
    // get controller instance
    private void findViews()
    {
    	m_oLoginBtn = (Button) findViewById(R.id.btn_Login);
    	m_oPreviewBtn = (Button) findViewById(R.id.btn_Preview);
    	m_oPlaybackBtn = (Button) findViewById(R.id.btn_Playback);
    	m_oParamCfgBtn = (Button) findViewById(R.id.btn_ParamCfg);
    	m_oCaptureBtn = (Button) findViewById(R.id.btn_Capture);
    	m_oRecordBtn = (Button) findViewById(R.id.btn_Record);
    	m_oTalkBtn = (Button) findViewById(R.id.btn_Talk);
    	m_oPTZBtn = (Button) findViewById(R.id.btn_PTZ);
    	m_oOtherBtn = (Button) findViewById(R.id.btn_OTHER);
    	//m_osurfaceView = (SurfaceView) findViewById(R.id.Sur_Player);
    	m_oIPAddr = (EditText) findViewById(R.id.EDT_IPAddr);
    	m_oPort = (EditText) findViewById(R.id.EDT_Port);
    	m_oUser = (EditText) findViewById(R.id.EDT_User);
    	m_oPsd = (EditText) findViewById(R.id.EDT_Psd);
    }
    // listen
    private void setListeners()
    {
    	m_oLoginBtn.setOnClickListener(Login_Listener);
    	m_oPreviewBtn.setOnClickListener(Preview_Listener);
    	m_oPlaybackBtn.setOnClickListener(Playback_Listener);
    	m_oParamCfgBtn.setOnClickListener(ParamCfg_Listener);
    	m_oCaptureBtn.setOnClickListener(Capture_Listener);
    	m_oRecordBtn.setOnClickListener(Record_Listener);
    	m_oTalkBtn.setOnClickListener(Talk_Listener);
    	m_oOtherBtn.setOnClickListener(OtherFunc_Listener);
    	m_oPTZBtn.setOnTouchListener(PTZ_Listener);
    }
    //ptz listener
    private Button.OnTouchListener PTZ_Listener = new OnTouchListener()
    {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			try
    		{
    			if(m_iLogID < 0)
				{
					Log.e(TAG,"please login on a device first");
					return false;
				}
    			if(event.getAction()== MotionEvent.ACTION_DOWN)
    			{
    				if(m_bPTZL == false)
    				{
    					if(!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.PAN_LEFT, 0))
    			        {
    						Log.e(TAG, "start PAN_LEFT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
    			        }
    			        else
    			        {
    			        	Log.i(TAG, "start PAN_LEFT succ");
    			        }
    				}
    				else
    				{
    					if(!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.PAN_RIGHT, 0))
    			        {
    						Log.e(TAG, "start PAN_RIGHT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
    			        }
    			        else
    			        {
    			        	Log.i(TAG, "start PAN_RIGHT succ");
    			        }
    				}
    			}
    			else if(event.getAction() == MotionEvent.ACTION_UP)
    			{
    				if(m_bPTZL == false)
    				{
    					if(!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.PAN_LEFT, 1))
    			        {
    						Log.e(TAG, "stop PAN_LEFT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
    			        }
    			        else
    			        {
    			        	Log.i(TAG, "stop PAN_LEFT succ");
    			        }
    					m_bPTZL = true;
    					m_oPTZBtn.setText("PTZ(R)");
    				}
    				else
    				{
    					if(!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.PAN_RIGHT, 1))
    			        {
    						Log.e(TAG, "stop PAN_RIGHT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
    			        }
    			        else
    			        {
    			        	Log.i(TAG, "stop PAN_RIGHT succ");
    			        }    					
    					m_bPTZL = false;
    					m_oPTZBtn.setText("PTZ(L)");
    				}
    			}
    			return true;
    		}
    		catch (Exception err)
			{
				Log.e(TAG, "error: " + err.toString());
				return false;
			}
		}
    };   
    //preset listener
    private Button.OnClickListener OtherFunc_Listener = new OnClickListener()
    {
    	public void onClick(View v)
    	{
//    		PTZTest.TEST_PTZ(m_iPlayID, m_iLogID, m_iStartChan);
//    		ConfigTest.TEST_Config(m_iPlayID, m_iLogID, m_iStartChan);;
//    		ManageTest.TEST_Manage(m_iLogID);
//    		AlarmTest.Test_SetupAlarm(m_iLogID);
//    		OtherFunction.TEST_OtherFunc(m_iPlayID, m_iLogID, m_iStartChan);
//    		OtherFunction.Test_RecycleGetStream(m_iLogID, m_iStartChan);
    	}
    };
    //Talk listener
    private Button.OnClickListener Talk_Listener = new Button.OnClickListener()
    {
    	public void onClick(View v)
    	{
    		try
    		{
    			if(m_bTalkOn == false)
    			{
    				if(VoiceTalk.startVoiceTalk(m_iLogID) >= 0)
    				{
    					m_bTalkOn = true;
    					m_oTalkBtn.setText("Stop");
    				}
    			}
    			else
    			{
    				if(VoiceTalk.stopVoiceTalk())
    				{
    					m_bTalkOn = false;
    					m_oTalkBtn.setText("Talk");
    				}    				
    			}
    		}
    		catch (Exception err)
			{
				Log.e(TAG, "error: " + err.toString());
			}
    	}
    };
    //record listener
    private Button.OnClickListener Record_Listener = new Button.OnClickListener()
    {
    	public void onClick(View v)
    	{
    		try
    		{
    			
    			
    		}
    		catch (Exception err)
			{
				Log.e(TAG, "error: " + err.toString());
			}
    	}
    };
    //capture listener
    private Button.OnClickListener Capture_Listener = new Button.OnClickListener()
    {
    	public void onClick(View v)
    	{
    		try
    		{
    			if(m_iPort < 0)
    			{
    				Log.e(TAG, "please start preview first");
    				return;
    			}
    			Player.MPInteger stWidth = new Player.MPInteger();
    			Player.MPInteger stHeight = new Player.MPInteger();
    		    if (!Player.getInstance().getPictureSize(m_iPort, stWidth, stHeight)){
    		    	Log.e(TAG, "getPictureSize failed with error code:" + Player.getInstance().getLastError(m_iPort));
    		        return;
    		    }
    		    int nSize = 5 * stWidth.value * stHeight.value;
    		    byte[] picBuf = new byte[nSize];
    		    Player.MPInteger stSize = new Player.MPInteger();
    		    if(!Player.getInstance().getBMP(m_iPort, picBuf, nSize, stSize))
    		    {
    		    	Log.e(TAG, "getBMP failed with error code:" + Player.getInstance().getLastError(m_iPort));
    		    	return ;
    		    }
    		    
    		    SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");     
    		    String   date   =   sDateFormat.format(new   java.util.Date());  
    		    FileOutputStream file = new FileOutputStream("/mnt/sdcard/" + date + ".bmp");
    		    file.write(picBuf, 0, stSize.value);
    		    file.close();
    		}
    		catch (Exception err)
			{
				Log.e(TAG, "error: " + err.toString());
			}
    	}
    };
    //playback listener
        private Button.OnClickListener Playback_Listener = new Button.OnClickListener()
    {
    	
		public void onClick(View v)
		{
			try
			{
				if(m_iLogID < 0)
				{
					Log.e(TAG,"please login on a device first");
					return ;
				}				
				if(m_iPlaybackID < 0)
				{					
					if(m_iPlayID >= 0 )
					{
						Log.i(TAG, "Please stop preview first");
						return;
					}
					PlaybackCallBack fPlaybackCallBack = getPlayerbackPlayerCbf();
					if (fPlaybackCallBack == null)
					{
					    Log.e(TAG, "fPlaybackCallBack object is failed!");
			            return;
					}
					NET_DVR_TIME struBegin = new NET_DVR_TIME();
					NET_DVR_TIME struEnd = new NET_DVR_TIME();
					
					struBegin.dwYear = 2015;
					struBegin.dwMonth = 12;
					struBegin.dwDay = 23;
					
					struEnd.dwYear = 2015;
					struEnd.dwMonth = 12;
					struEnd.dwDay = 24;
					
					m_iPlaybackID = HCNetSDK.getInstance().NET_DVR_PlayBackByTime(m_iLogID, 1, struBegin, struEnd);
					if(m_iPlaybackID >= 0)
					{
						if(!HCNetSDK.getInstance().NET_DVR_SetPlayDataCallBack(m_iPlaybackID, fPlaybackCallBack))
						{
							Log.e(TAG, "Set playback callback failed!");
							return ;
						}
						NET_DVR_PLAYBACK_INFO struPlaybackInfo = null ;
						if(!HCNetSDK.getInstance().NET_DVR_PlayBackControl_V40(m_iPlaybackID, PlaybackControlCommand.NET_DVR_PLAYSTART, null, 0, struPlaybackInfo))
						{
							Log.e(TAG, "net sdk playback start failed!");
							return ;
						}
						m_oPlaybackBtn.setText("Stop");
						int nProgress = -1;
						/*
						while(true)
					    {
					     	nProgress = HCNetSDK.getInstance().NET_DVR_GetPlayBackPos(m_iPlaybackID);
					       	System.out.println("NET_DVR_GetPlayBackPos:" + nProgress);
					       	if(nProgress < 0 || nProgress >= 100)
					       	{
					       		break;
					       	}
					      	try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
																		       	
					    }
					    */
				
					}
					else
					{
						Log.i(TAG, "NET_DVR_PlayBackByTime failed, error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
					}
				}
				else
				{
					if(!HCNetSDK.getInstance().NET_DVR_StopPlayBack(m_iPlaybackID))
					{
						Log.e(TAG, "net sdk stop playback failed");						
					}
					// player stop play
					stopSinglePlayer();
					m_oPlaybackBtn.setText("Playback");
					m_iPlaybackID = -1;
				}
			} 
			catch (Exception err)
			{
				Log.e(TAG, "error: " + err.toString());
			}
		}    	
    };
    
    /*
    private Button.OnClickListener Playback_Listener = new Button.OnClickListener()
    {
    	
		public void onClick(View v)
		{
			try
			{
				if(m_iLogID < 0)
				{
					Log.e(TAG,"please login on a device first");
					return ;
				}				
				if(m_iPlaybackID < 0)
				{					
					if(m_iPlayID >= 0 )
					{
						Log.i(TAG, "Please stop preview first");
						return;
					}
					PlaybackCallBack fPlaybackCallBack = getPlayerbackPlayerCbf();
					if (fPlaybackCallBack == null)
					{
					    Log.e(TAG, "fPlaybackCallBack object is failed!");
			            return;
					}
					
					m_iPlaybackID = HCNetSDK.getInstance().NET_DVR_PlayBackByName(m_iLogID, new String("ch0001_01000000154000100"));
					if(m_iPlaybackID >= 0)
					{
						if(!HCNetSDK.getInstance().NET_DVR_SetPlayDataCallBack(m_iPlaybackID, fPlaybackCallBack))
						{
							Log.e(TAG, "Set playback callback failed!");
							return ;
						}
						NET_DVR_PLAYBACK_INFO struPlaybackInfo = null ;
						if(!HCNetSDK.getInstance().NET_DVR_PlayBackControl_V40(m_iPlaybackID, PlaybackControlCommand.NET_DVR_PLAYSTART, null, 0, struPlaybackInfo))
						{
							Log.e(TAG, "net sdk playback start failed!");
							return ;
						}
						m_oPlaybackBtn.setText("Stop");
						int nProgress = -1;
						while(true)
					    {
					     	nProgress = HCNetSDK.getInstance().NET_DVR_GetPlayBackPos(m_iPlaybackID);
					       	System.out.println("NET_DVR_GetPlayBackPos:" + nProgress);
					       	if(nProgress < 0 || nProgress >= 100)
					       	{
					       		break;
					       	}
					      	try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
																       	
					    }
					    
				
					}
					else
					{
						Log.i(TAG, "NET_DVR_PlayBackByName failed, error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
					}
				}
				else
				{
					if(!HCNetSDK.getInstance().NET_DVR_StopPlayBack(m_iPlaybackID))
					{
						Log.e(TAG, "net sdk stop playback failed");						
					}
					// player stop play
					stopSinglePlayer();
					m_oPlaybackBtn.setText("Playback");
					m_iPlaybackID = -1;
				}
			} 
			catch (Exception err)
			{
				Log.e(TAG, "error: " + err.toString());
			}
		}    	
    };
*/
	

    //login listener
    private Button.OnClickListener Login_Listener = new Button.OnClickListener() 
	{
		public void onClick(View v) 
		{
			try
			{
				if(m_iLogID < 0)
				{
					// login on the device
					m_iLogID = loginDevice();
					if (m_iLogID < 0)
					{
						Log.e(TAG, "This device logins failed!");
						return;
					}
					// get instance of exception callback and set
					ExceptionCallBack oexceptionCbf = getExceptiongCbf();
					if (oexceptionCbf == null)
					{
					    Log.e(TAG, "ExceptionCallBack object is failed!");
					    return ;
					}
					
					if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf))
				    {
				        Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
				        return;
				    }
					
					m_oLoginBtn.setText("Logout");
					Log.i(TAG, "Login sucess ****************************1***************************");
				}
				else
				{
					// whether we have logout
					if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID))
					{
						Log.e(TAG, " NET_DVR_Logout is failed!");
						return;
					}
					m_oLoginBtn.setText("Login");
					m_iLogID = -1;
				}		
			} 
			catch (Exception err)
			{
				Log.e(TAG, "error: " + err.toString());
			}
		}
	};
	// Preview listener 
    private Button.OnClickListener Preview_Listener = new Button.OnClickListener() 
	{
		public void onClick(View v) 
		{
			try
			{
				((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
			    hideSoftInputFromWindow(DemoActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);				
				if(m_iLogID < 0)
				{
					Log.e(TAG,"please login on device first");
					return ;
				}
				if(m_bNeedDecode)
				{
					if(m_iChanNum > 1)//preview more than a channel
					{
						if(!m_bMultiPlay)
						{
							startMultiPreview();
					    	m_bMultiPlay = true;
					    	m_oPreviewBtn.setText("Stop");
						}
						else
						{
							stopMultiPreview();
							m_bMultiPlay = false;
							m_oPreviewBtn.setText("Preview");
						}
					}
					else	//preivew a channel
					{
						if(m_iPlayID < 0)
						{	
							startSinglePreview();
						}
						else
						{
							stopSinglePreview();
							m_oPreviewBtn.setText("Preview");
						}
					}
				}
				else
				{
					
				}								
			} 
			catch (Exception err)
			{
				Log.e(TAG, "error: " + err.toString());
			}
		}
	};
	// configuration listener
	private Button.OnClickListener ParamCfg_Listener = new Button.OnClickListener() 
	{
		public void onClick(View v)
		{
			try
			{
				paramCfg(m_iLogID);
			}
			catch (Exception err)
			{
				Log.e(TAG, "error: " + err.toString());
			}
		}
	};
	private void startSinglePreview()
	{
		if(m_iPlaybackID >= 0)
		{
			Log.i(TAG, "Please stop palyback first");
			return ;
		}
		RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
		if (fRealDataCallBack == null)
		{
		    Log.e(TAG, "fRealDataCallBack object is failed!");
            return ;
		}
		Log.i(TAG, "m_iStartChan:" +m_iStartChan);
		        
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = m_iStartChan;
        previewInfo.dwStreamType = 1; //substream
        previewInfo.bBlocked = 1;       
		// HCNetSDK start preview
        m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID, previewInfo, fRealDataCallBack);
		if (m_iPlayID < 0)
		{
		 	Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
		 	return ;
		}
		
		Log.i(TAG, "NetSdk Play sucess ***********************3***************************");										
		m_oPreviewBtn.setText("Stop");
	}
	private void startMultiPreview()
	{
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int i = 0;
        for(i = 0; i < 9; i++)
        {
        	if(playView[i] == null)
        	{
        		playView[i] = new PlaySurfaceView(this);       	       	  
                playView[i].setParam(metric.widthPixels);				    	
            	FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(  
            	FrameLayout.LayoutParams.WRAP_CONTENT,  
            	FrameLayout.LayoutParams.WRAP_CONTENT);   
            	params.bottomMargin = 2*playView[i].getCurHeight() - (i/3) * playView[i].getCurHeight();
            	params.leftMargin = (i%3) * playView[i].getCurWidth();
            	params.gravity = Gravity.BOTTOM | Gravity.LEFT;
            	addContentView(playView[i], params);
				//widget_Play.addView(playView[i], params);
        	}   	
 	   		playView[i].startPreview(m_iLogID, m_iStartChan + i);
        }
        m_iPlayID = playView[0].m_iPreviewHandle;
	}
	private void stopMultiPreview()
	{
		int i = 0;
		for(i = 0; i < 9;i++)
		{
			playView[i].stopPreview();
		}
		m_iPlayID = -1;
	}
	/** 
     * @fn stopSinglePreview
     * @author zhuzhenlei
     * @brief stop preview
     * @param NULL [in]
     * @param NULL [out]
     * @return NULL
     */
	private void stopSinglePreview()
	{
		if ( m_iPlayID < 0)
		{
			Log.e(TAG, "m_iPlayID < 0");
			return;
		}
		
		//  net sdk stop preview
		if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID))
		{
			Log.e(TAG, "StopRealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
			return;
		}
		
		m_iPlayID = -1;		
		stopSinglePlayer();
	}
	private void stopSinglePlayer()
	{
		Player.getInstance().stopSound();		
		// player stop play
		if (!Player.getInstance().stop(m_iPort)) 
        {
            Log.e(TAG, "stop is failed!");
            return;
        }	
		
		if(!Player.getInstance().closeStream(m_iPort))
		{
            Log.e(TAG, "closeStream is failed!");
            return;
        }
		if(!Player.getInstance().freePort(m_iPort))
		{
            Log.e(TAG, "freePort is failed!" + m_iPort);
            return;
        }
		m_iPort = -1;
	}
	/** 
     * @fn loginDevice
     * @author zhuzhenlei
     * @brief login on device
     * @param NULL [in]
     * @param NULL [out]
     * @return login ID
     */
	private int loginDevice()
	{
		// get instance
		m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
		if (null == m_oNetDvrDeviceInfoV30)
		{
			Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
			return -1;
		}
		String strIP = m_oIPAddr.getText().toString();
		int	nPort = Integer.parseInt(m_oPort.getText().toString());
		String strUser = m_oUser.getText().toString();
		String strPsd = m_oPsd.getText().toString();
		// call NET_DVR_Login_v30 to login on, port 8000 as default
		int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(strIP, nPort, strUser, strPsd, m_oNetDvrDeviceInfoV30);
		if (iLogID < 0)
		{
			Log.e(TAG, "NET_DVR_Login is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
			return -1;
		}
		if(m_oNetDvrDeviceInfoV30.byChanNum > 0)
		{
			m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
			m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
		}
		else if(m_oNetDvrDeviceInfoV30.byIPChanNum > 0)
		{
			m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
			m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
		}
		Log.i(TAG, "NET_DVR_Login is Successful!");
		
		return iLogID;
	}
	/** 
     * @fn paramCfg
     * @author zhuzhenlei
     * @brief configuration
     * @param iUserID - login ID [in]
     * @param NULL [out]
     * @return NULL
     */
	private void paramCfg(final int iUserID)
	{
		// whether have logined on
		if (iUserID < 0)
		{
			Log.e(TAG, "iUserID < 0");
			return;
		}		
		
		NET_DVR_COMPRESSIONCFG_V30 struCompress = new NET_DVR_COMPRESSIONCFG_V30();
		if(!HCNetSDK.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_COMPRESSCFG_V30, m_iStartChan, struCompress))
		{
			Log.e(TAG, "NET_DVR_GET_COMPRESSCFG_V30 failed with error code:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			Log.i(TAG, "NET_DVR_GET_COMPRESSCFG_V30 succ");
		}
		//set substream resolution to cif
	    struCompress.struNetPara.byResolution = 1;
	    if(!HCNetSDK.getInstance().NET_DVR_SetDVRConfig(iUserID, HCNetSDK.NET_DVR_SET_COMPRESSCFG_V30, m_iStartChan, struCompress))
	    {
	    	Log.e(TAG, "NET_DVR_SET_COMPRESSCFG_V30 failed with error code:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
	    }
	    else
	    {
	    	Log.i(TAG, "NET_DVR_SET_COMPRESSCFG_V30 succ");
	    }
	}
	/**
     * @fn getExceptiongCbf
     * @author zhuzhenlei
     * @brief process exception
     * @param NULL [in]
     * @param NULL [out]
     * @return exception instance
     */
	private ExceptionCallBack getExceptiongCbf()
	{
	    ExceptionCallBack oExceptionCbf = new ExceptionCallBack()
        {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle)
            {
            	System.out.println("recv exception, type:" + iType);
            }
        };
        return oExceptionCbf;
	}
	/** 
     * @fn getRealPlayerCbf
     * @author zhuzhenlei
     * @brief get realplay callback instance
     * @param NULL [in]
     * @param NULL [out]
     * @return callback instance
     */
	private RealPlayCallBack getRealPlayerCbf()
	{
	    RealPlayCallBack cbf = new RealPlayCallBack()
        {
             public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize)
             {
            	// player channel 1
            	DemoActivity.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME); 
             }
        };
        return cbf;
	}
	/** 
     * @fn getPlayerbackPlayerCbf
     * @author zhuzhenlei
     * @brief get Playback instance
     * @param NULL [in]
     * @param NULL [out]
     * @return callback instance
     */
	private PlaybackCallBack getPlayerbackPlayerCbf()
	{
		PlaybackCallBack cbf = new PlaybackCallBack()
        {            
			@Override
			public void fPlayDataCallBack(int iPlaybackHandle, int iDataType, byte[] pDataBuffer, int iDataSize)
			{
				// player channel 1
            	DemoActivity.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_FILE);	
			}
        };
        return cbf;
	}
	/** 
     * @fn processRealData
     * @author zhuzhenlei
     * @brief process real data
     * @param iPlayViewNo - player channel [in]
     * @param iDataType	  - data type [in]
     * @param pDataBuffer - data buffer [in]
     * @param iDataSize   - data size [in]
     * @param iStreamMode - stream mode [in]
     * @param NULL [out]
     * @return NULL
     */
	public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode)
	{
		if(!m_bNeedDecode)
		{
		//   Log.i(TAG, "iPlayViewNo:" + iPlayViewNo + ",iDataType:" + iDataType + ",iDataSize:" + iDataSize);
		}
		else
		{
			if(HCNetSDK.NET_DVR_SYSHEAD == iDataType)
		    {
		    	if(m_iPort >= 0)
	    		{
	    			return;
	    		}	    			
	    		m_iPort = Player.getInstance().getPort();	
	    		if(m_iPort == -1)
	    		{
	    			Log.e(TAG, "getPort is failed with: " + Player.getInstance().getLastError(m_iPort));
	    			return;
	    		}
	    		Log.i(TAG, "getPort succ with: " + m_iPort);
	    		if (iDataSize > 0)
	    		{
	    			if (!Player.getInstance().setStreamOpenMode(m_iPort, iStreamMode))  //set stream mode
	    			{
	    				Log.e(TAG, "setStreamOpenMode failed");
	    				return;
	    			}
	    			if (!Player.getInstance().openStream(m_iPort, pDataBuffer, iDataSize, 2*1024*1024)) //open stream
	    			{
	    				Log.e(TAG, "openStream failed");
	    				return;
	    			}
//	    			if (!Player.getInstance().play(m_iPort, m_osurfaceView.getHolder()))
//	    			{
//	    				Log.e(TAG, "play failed");
//	    				return;
//	    			}
	    			if(!Player.getInstance().playSound(m_iPort))
					{
						Log.e(TAG, "playSound failed with error code:" + Player.getInstance().getLastError(m_iPort));
						return;
					}
	    		}
		    }
		    else
		    {
		    	if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize))
    			{
//		    		Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
  		    	    for(int i = 0; i < 4000 && m_iPlaybackID >=0 ; i++)
		    		{
		    			if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize))
		    				Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
		    			else
		    				break;
		    			try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						
						}	    				
		    		}
		    	}

		    }		
		}
	    
	}
	/** 
     * @fn Cleanup
     * @author zhuzhenlei
     * @brief cleanup
     * @param NULL [in]
     * @param NULL [out]
     * @return NULL
     */
    public void Cleanup()
    {
        // release player resource
    	
    	Player.getInstance().freePort(m_iPort);
		m_iPort = -1;
        
        // release net SDK resource
		HCNetSDK.getInstance().NET_DVR_Cleanup();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
         switch (keyCode)
         {
         case KeyEvent.KEYCODE_BACK:
        	 	
        	  stopSinglePlayer();
        	  Cleanup();
              android.os.Process.killProcess(android.os.Process.myPid());
              break;
         default:
              break;
         }
     
         return true;
    }
}
