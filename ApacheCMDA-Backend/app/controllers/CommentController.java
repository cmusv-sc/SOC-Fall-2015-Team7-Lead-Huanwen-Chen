package controllers;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.PersistenceException;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import models.Comment;
import models.CommentRepository;
import play.mvc.*;

import java.util.Date;
import java.util.List;

@Named
@Singleton
public class CommentController extends Controller
{
    private final CommentRepository commentRepository;

    @Inject
    public CommentController(final CommentRepository commentRepository)
    {
        this.commentRepository = commentRepository;
    }

    public Result testCommentFunction()
    {
        return ok("test success");
    }

    public Result addComment()
    {
        JsonNode json = request().body().asJson();
        if (json == null)
        {
            System.out.println("Comment not created, expecting Json data");
            return badRequest("Comment not created, expecting Json data");
        }

        // Parse JSON file
        String username = json.findPath("username").asText();
        long userid = json.findPath("userid").asLong();
        long replytoid = json.findPath("replytoid").asLong();
        long workflowid = json.findPath("workflowid").asLong();
        String comment = json.findPath("comment").asText();
        Date date = new Date();

        try
        {
            Comment newComment = new Comment(username,userid,replytoid, workflowid, comment, date);
            Comment savedComment = commentRepository.save(newComment);
            System.out.println("Comment saved: " + savedComment.getId());
            return created(new Gson().toJson(savedComment.getId()));
        } catch (PersistenceException pe) {
        	pe.printStackTrace();
        	System.out.println("Comment not saved: " + comment);
        	return badRequest("Comment not saved: " + comment);
        }
    }

    public Result getComment(Long id, String format) {
    	if (id == null) {
    		System.out.println("workflowid is null or empty!");
    		return badRequest("workflowid is null or empty!");
    	}

    	List<Comment> commentList = commentRepository.findByWorkflowid(id);
    	String result = new String();
    	if (format.equals("json")) {
    		result = new Gson().toJson(commentList);
    	}
    	return ok(result);
    }
}

