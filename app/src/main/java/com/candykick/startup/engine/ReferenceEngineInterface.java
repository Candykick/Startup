package com.candykick.startup.engine;

import com.candykick.startup.model.ReferenceModel;

import java.util.ArrayList;

/*
 파이어베이스 버전은 자료실의 모든 데이터를 통으로 로딩한 뒤, 자료 하나를 보여줄 때는 Intent로 필요한 값들을 전달하는 방식을 쓰고 있다.
 */

public interface ReferenceEngineInterface {
    // 자료실의 모든 자료 목록 받아오기
    void getReferenceList(CdkResponseCallback<ArrayList<ReferenceModel>> responseCallback);
}