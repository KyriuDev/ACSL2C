package dto;

import constants.CCommentNature;
import constants.CCommentType;

/**
 * Name:        CComment.java
 * Content:     This class defines a CComment data object that will mostly contain:
 * 				    - the CCommentType of the considered comment;
 * 				    - the CCommentNature of the considered comment;
 * 				    - the starting line of the comment in the original source code;
 * 				    - the ending line of the comment in the original source code;
 * 				    - the content of the comment.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    02/03/26
 */

public class CComment
{
	private final CCommentType commentType;
	private final CCommentNature commentNature;
	private final int originalSourceCodeStartingLine;
	private final int originalSourceCodeEndingLine;
	private final String content;

	//Constructors

	public CComment(final CCommentType commentType,
					final CCommentNature commentNature,
					final int originalSourceCodeStartingLine,
					final int originalSourceCodeEndingLine,
					final String content)
	{
		this.commentType = commentType;
		this.commentNature = commentNature;
		this.originalSourceCodeStartingLine = originalSourceCodeStartingLine;
		this.originalSourceCodeEndingLine = originalSourceCodeEndingLine;
		this.content = content;
	}

	//Public methods

	public CCommentType getCommentType()
	{
		return this.commentType;
	}

	public boolean isSingleLineComment()
	{
		return this.commentType == CCommentType.SINGLE_LINE;
	}

	public CCommentNature getCommentNature()
	{
		return this.commentNature;
	}

	public boolean isRegularComment()
	{
		return this.getCommentNature() == CCommentNature.REGULAR;
	}

	public int getOriginalSourceCodeStartingLine()
	{
		return this.originalSourceCodeStartingLine;
	}

	public int getOriginalSourceCodeEndingLine()
	{
		return this.originalSourceCodeEndingLine;
	}

	public String getContent()
	{
		return this.content;
	}

	//Private methods
}
