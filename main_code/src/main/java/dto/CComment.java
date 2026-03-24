package dto;

import ast.AbstractSyntaxTree;
import constants.Color;
import constants.c.CCommentNature;
import constants.c.CCommentType;
import misc.Utils;

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
	private AbstractSyntaxTree abstractSyntaxTree;

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

	public String getCleanContent()
	{
		return this.content.replaceAll("//", "")
				.replaceAll("/\\*", "")
				.replaceAll("\\*/", "")
				.replaceAll("@", "");
	}

	public void setAbstractSyntaxTree(final AbstractSyntaxTree abstractSyntaxTree)
	{
		this.abstractSyntaxTree = abstractSyntaxTree;
	}

	public AbstractSyntaxTree getAbstractSyntaxTree()
	{
		if (this.abstractSyntaxTree == null)
		{
			System.out.println(Color.getYellowMessage(String.format(
				"Warning: the abstract syntax tree of comment \"%s\" has not been generated yet!",
				this.getContent()
			)));
		}

		return this.abstractSyntaxTree;
	}

	//Private methods
}
