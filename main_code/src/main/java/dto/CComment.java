package dto;

import acsl_to_c.ACSL2ASTTranslator;
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
	private ACSL2ASTTranslator.TranslationComponents translationComponents;

	//Constructors

	public CComment(final String content)
	{
		this(CCommentType.SINGLE_LINE, content);
	}

	public CComment(final CCommentType commentType,
	                final String content)
	{
		this(commentType, CCommentNature.REGULAR, content);
	}

	public CComment(final CCommentType commentType,
	                final CCommentNature commentNature,
	                final String content)
	{
		this(commentType, commentNature, -1, -1, content);
	}

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

	public boolean isAcslComment()
	{
		return this.getCommentNature() == CCommentNature.ACSL;
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
		if (this.isRegularComment())
		{
			throw new UnsupportedOperationException(
				"Abstract syntax tree representation of the comment only exists for ACSL comments!"
			);
		}

		this.abstractSyntaxTree = abstractSyntaxTree;
	}

	public AbstractSyntaxTree getAbstractSyntaxTree()
	{
		if (this.isRegularComment())
		{
			throw new UnsupportedOperationException(
				"Abstract syntax tree representation of the comment only exists for ACSL comments!"
			);
		}

		if (this.abstractSyntaxTree == null)
		{
			System.out.println(Color.getYellowMessage(String.format(
				"Warning: the abstract syntax tree of comment \"%s\" has not been generated yet!",
				this.getContent()
			)));
		}

		return this.abstractSyntaxTree;
	}

	public void setTranslationComponents(final ACSL2ASTTranslator.TranslationComponents translationComponents)
	{
		if (this.isRegularComment())
		{
			throw new UnsupportedOperationException(
				"Translation components of the comment only exists for ACSL comments!"
			);
		}

		this.translationComponents = translationComponents;
	}

	public ACSL2ASTTranslator.TranslationComponents getTranslationComponents()
	{
		if (this.isRegularComment())
		{
			throw new UnsupportedOperationException(
				"Translation components of the comment only exists for ACSL comments!"
			);
		}

		if (this.translationComponents == null)
		{
			System.out.println(Color.getYellowMessage(String.format(
				"Warning: the translation components of comment \"%s\" have not been generated yet!",
				this.getContent()
			)));
		}

		return this.translationComponents;
	}

	//Private methods
}
