package sk.tuke.kpi.kp.bejeweled.service;

import sk.tuke.kpi.kp.bejeweled.entity.Comment;

import java.util.List;

public interface CommentService {
    void addComment(Comment comment) throws CommentException;
    List<Comment> getComments(String game) throws CommentException;
    void reset() throws CommentException;
}
