package com.candykick.startup.engine;

import com.candykick.startup.model.UserAdvModel;
import com.candykick.startup.model.UserCeoModel;
import com.candykick.startup.model.UserInvModel;
import com.candykick.startup.model.UserOutModel;

import java.util.Map;

public interface UserEngineInterface {
    /*void registerCeoUser(UserCeoModel userCeoModel, CdkEmptyCallback responseCallback);
    void registerInvUser(UserInvModel userInvModel, CdkEmptyCallback responseCallback);
    void registerOutUser(UserOutModel userOutModel, CdkEmptyCallback responseCallback);
    void registerAdvUser(UserAdvModel userAdvModel, CdkEmptyCallback responseCallback);*/
    // 유저 등록.
    // 유저 종류에 상관없이 한 함수로 등록 가능하게 하기 위해 Map을 사용.
    // Input: Map<String, Object>, Output: X
    void registerUser(String loginMethod, Map<String, Object> userInfo, String password, CdkEmptyCallback responseCallback);

    void loginAsKakao(CdkEmptyCallback responseCallback);
    void loginAsGoogle(CdkEmptyCallback responseCallback);
    void loginAsEmail(CdkEmptyCallback responseCallback);

    void getCeoUserInfo(CdkResponseCallback<UserCeoModel> responseCallback);
    void getInvUserInfo(CdkResponseCallback<UserInvModel> responseCallback);
    void getOutUserInfo(CdkResponseCallback<UserOutModel> responseCallback);
    void getAdvUserInfo(CdkResponseCallback<UserAdvModel> responseCallback);
}
