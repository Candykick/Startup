package com.candykick.startup.engine;

import com.candykick.startup.model.MarketCommentModel;
import com.candykick.startup.model.MarketListItemModel;
import com.candykick.startup.model.MarketPostModel;

import java.util.ArrayList;
import java.util.Map;

public interface MarketEngineInterface {
    // 타입별 전체 외주목록을 얻어오는 코드.
    // Input: type, Output: ArrayList<MarketListItemModel>
    void setMarketList(String type, CdkResponseCallback<ArrayList<MarketListItemModel>> responseCallback);

    // 타입별 전체 외주목록 중 특정 카테고리의 외주목록을 쿼리해서 얻어오는 코드.
    // Input: type + category, Output: ArrayList<MarketListItemModel>
    void setMarketListAsQuery(String type, String category, CdkResponseCallback<ArrayList<MarketListItemModel>> responseCallback);

    // 포스팅 전체 내용을 얻어오는 코드.
    // Input: type + postId, Output: MarketPostModel
    void setMarketPost(String type, String postId, CdkResponseCallback<MarketPostModel> responseCallback);

    // 포스팅의 모든 댓글을 얻어오는 코드.
    // Input: type + postId, Output: ArrayList<MarketCommentModel>
    void getMarketComments(String type, String postId, CdkResponseCallback<ArrayList<MarketCommentModel>> responseCallback);

    // 포스팅의 댓글 상위 5개를 얻어오는 코드.
    // Input: type + postId, Output: ArrayList<MarketCommentModel>
    void getMarketCommentsForMain(String type, String postId, CdkResponseCallback<ArrayList<MarketCommentModel>> responseCallback);

    // 포스팅에 새 댓글을 등록하는 코드.
    // Input: commentData -> listNum+1(type + postId), Output: X
    void registerNewComment(String type, String postId, Map<String, Object> commentData, int listNum, CdkResponseCallback<String> responseCallback);

    // 외주마켓에 새 글을 등록하는 코드.
    // Input: MarketPostModel -> id 생성 (type), Output: X
    void uploadMarketPost(String type, MarketPostModel postModel, CdkResponseCallback<String> responseCallback);
}