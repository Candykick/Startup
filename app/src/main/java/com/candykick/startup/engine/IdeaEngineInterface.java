package com.candykick.startup.engine;

import com.candykick.startup.model.IdeaBoardModel;
import com.candykick.startup.model.IdeaCommentModel;
import com.candykick.startup.model.IdeaPostModel;

import java.util.ArrayList;

public interface IdeaEngineInterface {

    // 아이디어 게시판 리스트 가져옴.
    // Input: X, Output: ArrayList<IdeaBoardModel>
    void getIdeaBoard(CdkResponseCallback<ArrayList<IdeaBoardModel>> responseCallback);

    // 게시판의 특정 포스팅을 포스팅ID값으로 쿼리해서 가져옴.
    // Input: postId, Output: IdeaPostModel
    void getPost(String postId, CdkResponseCallback<IdeaPostModel> responseCallback);

    // 게시글의 댓글 5개를 포스팅ID값으로 쿼리해서 가져옴.
    // Input: postId, Output: ArrayList<IdeaCommentModel>
    void getFiveComments(String postId, CdkResponseCallback<ArrayList<IdeaCommentModel>> responseCallback);

    // 게시글의 모든 댓글을 포스팅ID값으로 쿼리해서 가져옴.
    // Input: postId, Output: ArrayList<IdeaCommentModel>
    void getAllComments(String postId, CdkResponseCallback<ArrayList<IdeaCommentModel>> responseCallback);

    // 게시글에 새 댓글을 업로드함.
    // Input: commentModel -> postId, Output: X
    void uploadComment(String postId, IdeaCommentModel commentModel, CdkResponseCallback<String> responseCallback);

    // 게시글의 내 글을 삭제함. 포스팅ID값으로 쿼리해서 데이터를 지움.
    // Input: postId, Output: X
    void deletePost(String postId, CdkResponseCallback<String> responseCallback);

    // 새 글을 업로드함. 이 경우, postid = YYMMDD_hhmmss_idea(아이디어) / YYMMDD_hhmmss_team(팀빌딩)
    // 기존 글을 수정하는 경우도 여기에 포함됨. 이 경우, postid를 기존 글에서 가져옴.
    // Input: title, contents -> postid, Output: X
    void uploadPost(String postid, String title, String contents, CdkResponseCallback<String> responseCallback);
}
