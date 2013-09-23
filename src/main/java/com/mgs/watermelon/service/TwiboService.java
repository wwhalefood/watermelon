package com.mgs.watermelon.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.mgs.watermelon.dao.TwiboDAO;
import com.mgs.watermelon.entity.MUser;
import com.mgs.watermelon.entity.Twibo;
import com.mgs.watermelon.entity.Twicomment;

@Service
public class TwiboService extends MongoBaseService<Twibo, ObjectId> {

	@Autowired
	private TwiboDAO twiboDAO;

	public TwiboDAO getTwiboDAO() {
		return twiboDAO;
	}

	public void setTwiboDAO(TwiboDAO twiboDAO) {
		this.twiboDAO = twiboDAO;
		super.setBaseDao(twiboDAO);
	}


	/**
	 * 发表twibo
	 * @param content
	 * @param user
	 * @return
	 */
	public Twibo postTwibo(String content, MUser user) {
		Twibo result = new Twibo();
		result.setContent(content);
		result.setTimestamp(new Date().getTime());
		result.setUser(user);
		baseDao.save(result);
		return result;
		
	}
	
	/**
	 * 简单分页
	 * @param user
	 * @param offset
	 * @param length
	 * @return
	 */
	public List<Twibo> getList(MUser user, Integer offset, Integer length) {
		List<MUser> lists = new ArrayList<MUser>();
		lists.add(user);
		for(MUser u : user.getFollows()){
			lists.add(u);
		}
		Query<Twibo> query = baseDao.createQuery();
		query.filter("user in ", lists).offset(offset).limit(length).order("-timestamp");
		QueryResults<Twibo> result = baseDao.find(query);
		return result.asList();
	}

	/**
	 * 发表评论
	 * @param twibo
	 * @param user
	 * @param content
	 * @return
	 */
	public Twicomment postComment(Twibo twibo, MUser user, String content) {
		Twicomment comment = new Twicomment();
		comment.setUser(user);
		comment.setContent(content);
		comment.setTimestamp(new Date().getTime());
		twibo.getTwicomments().add(comment);
		
		Query<Twibo> query = createQuery().filter("oid", twibo.getOid());
		UpdateOperations<Twibo> uo = baseDao.createUpdateOperations().set("twicomments", twibo.getTwicomments());
		UpdateResults<Twibo> results= baseDao.update(query, uo);
		if(results!=null && results.getUpdatedCount()==1){
			return comment;
		}
		return null;
	}

	public List<Twicomment> comments(Twibo twibo) {
		return null;
	}
	
}
