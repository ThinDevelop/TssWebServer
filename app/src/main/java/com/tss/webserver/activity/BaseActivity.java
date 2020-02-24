package com.tss.webserver.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.tss.webserver.AllinpaySYBApplication;
import com.tss.webserver.R;
import com.tss.webserver.entity.TransactionEntity;
import com.tss.webserver.utils.LogUtil;

import java.util.HashMap;

public abstract class BaseActivity extends Activity {

    private static final String TRANSACTION_ENTITY = "TransactionEntity";
    // SDK的AIDL服务对象
    //protected AidlDeviceService mDeviceService = null;
    protected AidlDeviceManager mDeviceManager = null;
    protected Context mContext;
    protected Activity mActivity;
    // 布局顶部标题栏
    protected LinearLayout mLeftBtn;
    protected TextView mTitle;
    // 交易数据对象，用于保存每个交易节点产生的数据
    private TransactionEntity mTransEntity;
    private static Toast mToast = null;
    private long lastClickTime = 0;
    protected boolean isCanceled = false;
    // 播放声音
    private SoundPool sp;
    private HashMap<Integer, Integer> spMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityManager.getActivityManager().addActivity(this);
        // 设置输入框不上浮
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        super.onCreate(savedInstanceState);
        setContentView(contentViewSourceID());
        mTransEntity = (TransactionEntity) getIntent().getSerializableExtra(TRANSACTION_ENTITY);
        if (null == mTransEntity) {
            mTransEntity = new TransactionEntity();
        }
        mContext = this;
        mActivity = this;
        // 初始化界面
        initView();
        initSound();
        // 绑定AIDL服务
        mDeviceManager = AllinpaySYBApplication.mDeviceManager;
        if (!bindService()) {
            onServiceBindFaild();
        }
    }

    private static boolean serviceStartFalg = false;

    /**
     * 绑定服务
     */
    public boolean bindService() {
        LogUtil.info("----------------------开始绑定服务-----------------------");
        Intent intent = new Intent();
        //intent.setPackage("com.centerm.lklposp");
        intent.setPackage("com.centerm.smartposservice");
        intent.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
        boolean flag = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        LogUtil.debug("bindService返回" + flag);
        return flag;
    }

    /**
     * 服务连接桥
     */
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            AllinpaySYBApplication.mDeviceManager = null;
            mDeviceManager = null;
            onServiceBindFaild();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AllinpaySYBApplication.mDeviceManager = mDeviceManager = AidlDeviceManager.Stub.asInterface(service);
            onServiceConnecteSuccess(mDeviceManager);
        }
    };

    @Override
    protected void onDestroy() {
        if (null != conn) {
            android.util.Log.i("huang", "onDestroy unbindservice");
            unbindService(conn);
            conn = null;
        }
        ActivityManager.getActivityManager().removeActivity(mActivity);
        isCanceled = true;
        super.onDestroy();
    }


    /**
     * 设置顶部返回按钮点击事件，返回到上一个activity
     */
    public void setTopDefaultReturn() {
        if (null == mLeftBtn) {
            mLeftBtn = (LinearLayout) findViewById(R.id.top_layout);
        }
        mLeftBtn.setVisibility(View.VISIBLE);
        mLeftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager.getActivityManager().removeActivity(mActivity);
            }
        });
    }

    /**
     * 防止重复点击
     */
    public boolean isCanClick() {
        long cur = System.currentTimeMillis();
        long time = cur - lastClickTime;
        time = Math.abs(time);
        if (time < 500) {
            return false;
        }
        lastClickTime = cur;
        return true;
    }

    /**
     * 防止重复点击
     *
     * @param duration 重复点击间隔时间 毫秒
     */
    public boolean isCanClick(int duration) {
        long cur = System.currentTimeMillis();
        long time = cur - lastClickTime;
        time = Math.abs(time);
        if (time < duration) {
            return false;
        }
        lastClickTime = cur;
        return true;
    }

    /**
     * 设置顶部返回按钮点击事件
     *
     * @param listener 点击事件
     */
    public void setTopReturnListener(OnClickListener listener) {
        if (null == mLeftBtn) {
            mLeftBtn = (LinearLayout) findViewById(R.id.top_layout);
        }
        mLeftBtn.setVisibility(View.VISIBLE);
        mLeftBtn.setOnClickListener(listener);
    }

    /**
     * 设置页面标题
     *
     * @param title 标题
     */
    public void setTopTitle(String title) {
        if (null == mTitle) {
            mTitle = (TextView) findViewById(R.id.top_title);
        }
        mTitle.setText(title);
    }

    /**
     * 启动下一个activity，会传递TransactionEntity对象到下个activity。
     *
     * @param nextClass 下个activity的class
     */
    public void goToNextActivity(Class<?> nextClass) {
        Intent it = new Intent(mContext, nextClass);
        it.putExtra(TRANSACTION_ENTITY, mTransEntity);
        startActivity(it);
    }

    /**
     * 启动下一个activity，会传递TransactionEntity对象到下个activity。
     *
     * @param it 意图
     */
    public void goToNextActivity(Intent it) {
        startActivity(it);
    }

    /**
     * 隐藏系统键盘
     *
     * @param v
     */
    public void hideSystemKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void initSound() {
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        spMap = new HashMap<Integer, Integer>();
        spMap.put(1, sp.load(this, R.raw.success, 1));
        spMap.put(2, sp.load(this, R.raw.pineffect, 1));
    }

    public void playSound(int sound, int number) {
        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = volumnCurrent / audioMaxVolumn;
        sp.play(spMap.get(sound), volumnRatio, volumnRatio, 1, number, 1f);
    }

    /**
     * 显示Toast的tip
     *
     * @param msg
     */
    public void showTip(String msg) {
        if (null == mToast) {
            mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * 设置activity的布局，实现时只需返回布局的ID。
     */
    public abstract int contentViewSourceID();

    /**
     * 创建activity时初始化页面
     */
    public abstract void initView();

    /**
     * 服务绑定成功的回调
     */
    public abstract void onServiceConnecteSuccess(AidlDeviceManager manager);

    /**
     * 服务绑定失败回调
     */
    public abstract void onServiceBindFaild();

    /**
     * 用于保存值。
     */
    public abstract boolean saveValue();

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

}
