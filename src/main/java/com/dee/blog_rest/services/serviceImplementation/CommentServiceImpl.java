package com.dee.blog_rest.services.serviceImplementation;

import com.dee.blog_rest.security.UserPrincipal;
import com.dee.blog_rest.entities.Comment;
import com.dee.blog_rest.entities.Post;
import com.dee.blog_rest.entities.User;
import com.dee.blog_rest.entities.role.RoleName;
import com.dee.blog_rest.exceptions.BadRequestException;
import com.dee.blog_rest.exceptions.BlogapiException;
import com.dee.blog_rest.repositories.CommentRepository;
import com.dee.blog_rest.repositories.PostRepository;
import com.dee.blog_rest.repositories.UserRepository;
import com.dee.blog_rest.requests_and_responses.ApiResponse;
import com.dee.blog_rest.requests_and_responses.CommentRequest;
import com.dee.blog_rest.requests_and_responses.PagedResponse;
import com.dee.blog_rest.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CommentServiceImpl implements CommentService {
	private static final String THIS_COMMENT = " this comment";

	private static final String YOU_DON_T_HAVE_PERMISSION_TO = "You don't have permission to ";

	private static final String COMMENT_DOES_NOT_BELONG_TO_POST = "Comment does not belong to post";

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public PagedResponse<Comment> getAllComments(Long postId, int page, int size) {
		validatePageNumberAndSize(page, size);
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
		if (postRepository.findById(postId).isPresent()) {
			Page<Comment> comments = commentRepository.findByPostId(postId, pageable);

			return new PagedResponse<>(comments.getContent(), comments.getNumber(), comments.getSize(),
					comments.getTotalElements(), comments.getTotalPages(), comments.isLast());
		}
		throw new BadRequestException("Could not find post with that id");
	}

	@Override
	public Comment addComment(CommentRequest commentRequest, Long postId, UserPrincipal currentUser) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new IllegalStateException("Resource not found"));
		User user = userRepository.findByEmail(currentUser.getEmail());
		Comment comment = new Comment();
		comment.setBody(commentRequest.getBody());
		comment.setUser(user);
		comment.setPost(post);
		comment.setCreatedBy(user.getId().toString());
		comment.setCreatedAt(Instant.now());
		comment.setUpdatedAt(Instant.now());
		return commentRepository.save(comment);
	}

	@Override
	public Comment getComment(Long postId, Long id) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new IllegalStateException("Resource not found"));
		Comment comment = commentRepository.findById(id)
				.orElseThrow(() ->new IllegalStateException("Resource not found"));
		if (comment.getPost().getId().equals(post.getId())) {
			return comment;
		}
		throw new BlogapiException(HttpStatus.BAD_REQUEST, COMMENT_DOES_NOT_BELONG_TO_POST);
	}

	@Override
	public Comment updateComment(Long postId, Long id, CommentRequest commentRequest,
			UserPrincipal currentUser) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new IllegalStateException("Resource not found"));
		Comment comment = commentRepository.findById(id)
				.orElseThrow(() -> new IllegalStateException("Resource not found"));

		if (!comment.getPost().getId().equals(post.getId())) {
			throw new BlogapiException(HttpStatus.BAD_REQUEST, COMMENT_DOES_NOT_BELONG_TO_POST);
		}
		if (comment.getUser().getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			comment.setBody(commentRequest.getBody());
			return commentRepository.save(comment);
		}
		throw new BlogapiException(HttpStatus.UNAUTHORIZED, YOU_DON_T_HAVE_PERMISSION_TO + "update" + THIS_COMMENT);
	}

	@Override
	public ApiResponse deleteComment(Long postId, Long id, UserPrincipal currentUser) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new IllegalStateException("Resource not found"));
		Comment comment = commentRepository.findById(id)
				.orElseThrow(() -> new IllegalStateException("Resource not found"));

		if (!comment.getPost().getId().equals(post.getId())) {
			return new ApiResponse(Boolean.FALSE, COMMENT_DOES_NOT_BELONG_TO_POST);
		}

		if (comment.getUser().getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			commentRepository.deleteById(comment.getId());
			return new ApiResponse(Boolean.TRUE, "You successfully deleted comment");
		}

		throw new BlogapiException(HttpStatus.UNAUTHORIZED, YOU_DON_T_HAVE_PERMISSION_TO + "delete" + THIS_COMMENT);
	}


	private void validatePageNumberAndSize(int page, int size) {
		if (page < 0) {
			throw new BadRequestException("Page number cannot be less than zero.");
		}

		if (size < 0) {
			throw new BadRequestException("Size number cannot be less than zero.");
		}

		if (size > 30) {
			throw new BadRequestException("Page size must not be greater than " + 30);
		}
	}
}
