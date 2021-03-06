package com.example.lwq.damaiclient.modle.IM.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.PublicKey;

/**
 * Created by lwq on 2015/12/14.
 */
@Table(name = "User")
public class User extends Model implements Serializable {

    @Column(name = "userId")
    public String userId;
    @Column(name = "clientId")
    public String clientId;
    @Column(name = "userName")
    public String userName;
    @Column(name = "userPhotoUrl")
    public String userPhotoUrl;
    public User(){
    }

    public User(JSONObject json){
        parseJSON(json);
    }

    public void parseJSON(JSONObject json){//解析用户json
        try {
            clientId = json.getString("clientId");
            userId = json.getString("id");
            userName = json.getString("username");
            if(json.has("photo")){
                userPhotoUrl = json.getJSONObject("photo").getString("url");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public User update() {
        User userExisit = new Select().from(User.class)
                .where("clientId = ? ", clientId).executeSingle();

        // 不存在
        if (userExisit == null) {
            userExisit = this;
        }else{
            if(userId!=null)
                userExisit.userId = userId;
            if(clientId!=null)
                userExisit.clientId = clientId;
            if(userName!=null)
                userExisit.userName = userName;
            if(userPhotoUrl!=null)
                userExisit.userPhotoUrl = userPhotoUrl;
        }

        userExisit.save();
        return userExisit;
    }

    public boolean same(){
        User userExisit = new Select().from(User.class)
                .where("clientId = ? ", clientId).executeSingle();
        if( (userExisit!=null) && userExisit.userName!=null && userExisit.userId!=null && (userExisit.userId.equals(userId)) && (userExisit.userName.equals(userName))){
            return (userExisit.userPhotoUrl==null && userPhotoUrl==null);
        }else{
            return false;
        }

    }

    public User getFromTable(){
        User userExisit = new Select().from(User.class).where("clientId = ? ", clientId).executeSingle();
        return userExisit;
    }
}
