package com.example.lwq.damaiclient.modle.IM.Fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arrownock.exception.ArrownockException;
import com.arrownock.social.AnSocialMethod;
import com.arrownock.social.IAnSocialCallback;
import com.example.lwq.damaiclient.R;
import com.example.lwq.damaiclient.activity.RecommendDetailActivity;
import com.example.lwq.damaiclient.adapter.RecommendAdapter;
import com.example.lwq.damaiclient.app.BaseFragment;
import com.example.lwq.damaiclient.modle.IM.Activity.AddFriendActivity;
import com.example.lwq.damaiclient.modle.IM.IMApp.AnIMManger;
import com.example.lwq.damaiclient.modle.IM.IMApp.UserManager;
import com.example.lwq.damaiclient.modle.IM.adapter.ChatAdapter;
import com.example.lwq.damaiclient.modle.IM.bean.Chat;
import com.example.lwq.damaiclient.modle.IM.bean.IMMessage;
import com.example.lwq.damaiclient.modle.IM.bean.User;
import com.example.lwq.damaiclient.utils.DialogUtils;
import com.example.lwq.damaiclient.utils.ListViewHeightBased;
import com.example.lwq.damaiclient.widget.AppBaryx;
import com.example.lwq.damaiclient.widget.DmListView;
import com.example.lwq.damaiclient.widget.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by lwq on 2015/12/4.
 */
public class ChatFragment extends BaseFragment implements View.OnClickListener,Observer {
    private View root;
    private ListView chatListView;
    private AppBaryx appBaryx;
    private TextView header_back,type_city, header_tab, header_title,header_conetnt;
    private ImageView imgae_right;
    private ProgressBar imag_ProgressBar;
    LinearLayout progress_lin;
    private AnIMManger  sIMManger;
    private List<User> userList=new ArrayList<User>();
    List<Chat> chatList=new ArrayList<Chat>();
    /** 标志位，标志已经初始化完成 */
    private boolean isPrepared,isFrist;
    ChatAdapter chatAdapter;
    PullToRefreshView pullToRefreshView;
    Handler handler = new Handler();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(null==root){
            root=inflater.inflate(R.layout.fragment_chat,null);
            initView();
            isPrepared=true;
            sIMManger = AnIMManger.getInstance(getActivity());//绑定注册所有Observer
            sIMManger.addObserver(this);
            lazyLoad();
        }
        // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) root.getParent();
        if (parent != null) {
            parent.removeView(root);
        }
        return root;
    }
    public void  initView(){
        appBaryx = (AppBaryx) root.findViewById(R.id.footbar);
        header_back= appBaryx.getHeader_back();
        type_city = appBaryx.getType_city();
        header_tab = appBaryx.getHeader_tab();
        header_title = appBaryx.getHeader_titletv();
        imgae_right = appBaryx.getImgae_right();
        header_conetnt= appBaryx.getHeader_conetnt();
        header_conetnt.setText("加好友");
        header_back.setText("消息中心");
        header_back.setVisibility(View.VISIBLE);
        header_conetnt.setVisibility(View.VISIBLE);
        type_city.setVisibility(View.GONE);
        header_title.setVisibility(View.GONE);
        imgae_right.setVisibility(View.GONE);
        header_tab.setVisibility(View.GONE);

        pullToRefreshView= (PullToRefreshView) root.findViewById(R.id.pullToRefreshView);
        chatListView = (ListView) root.findViewById(R.id.chat_listview);
        imag_ProgressBar = (ProgressBar) root.findViewById(R.id.imag_ProgressBar);
        progress_lin = (LinearLayout) root.findViewById(R.id.progress_lin);
        header_conetnt.setOnClickListener(this);
        header_back.setOnClickListener(this);
        pullToRefreshView.setIsCancleLoadMore();//设置取消
    }
    @Override
    protected void lazyLoad() {
        if (!isPrepared||!isVisible||isFrist) {
            return;
        }
        progress_lin.setVisibility(View.GONE);
        imag_ProgressBar.setVisibility(View.GONE);
        fetchMyRemoteFriend();
        isFrist=true;
    }
    public void  initData(){
         chatAdapter=new ChatAdapter(getActivity());
         chatListView.setAdapter(chatAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.header_back:
               AnIMManger.getInstance(getActivity()).logout();
                getActivity().finish();
                break;
            case R.id.header_conetnt:
                startActivity(new Intent(getActivity(), AddFriendActivity.class));
                break;
        }
    }
    @Override
    public void update(Observable observable, Object data) {
        if(data instanceof IMMessage) {
            Log.i("ChatFragment消息更新","进来");
            chatAdapter.fillLocalData();
        }
    }


    public void fetchMyRemoteFriend() {
        try{
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("user_id",  UserManager.getInstance(getActivity()).currentUser.userId);
            params.put("limit", 1000);
            UserManager.getInstance(getActivity()).anSocial.sendRequest("friends/list.json", AnSocialMethod.GET, params, new IAnSocialCallback() {
                @Override
                public void onFailure(JSONObject response) {
                    Log.i("fetchMyRemoteFriend", response.toString());
                }

                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        JSONArray users = response.getJSONObject("response").getJSONArray("friends");
                        Log.i("fetchMyRemoteFriend好友列表", users.toString());
                        for(int i =0;i<users.length();i++){
                            JSONObject userJson = users.getJSONObject(i);
                            User user = new User(userJson);
                            saveFriends(user);
                        }

                        initData();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (ArrownockException e) {
            e.printStackTrace();
        }
    }


    public void saveFriends(User user){
        UserManager.getInstance(getActivity()).saveUser(user);
    }
}
