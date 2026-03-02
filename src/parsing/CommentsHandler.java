package parsing;

import constants.ACSLKeyword;
import constants.CCommentNature;
import constants.CCommentType;
import constants.Str;
import dto.CComment;
import misc.Pair;
import misc.Utils;
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Name:        CommentsHandler.java
 * Content:     This class is in charge of handling the comments appearing in the original source code of the given
 * 				program.
 * 				More precisely, it associates each parsed comment to a pair of IASTNode such that the first/left
 * 				IASTNode of the pair corresponds to the portion of code appearing right before the comment, and the
 * 				second/right IASTNode of the pair corresponds to the portion of code appearing right after the comment.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    02/03/26
 */

public class CommentsHandler
{
	private final Map<CComment, Pair<IASTNode, IASTNode>> commentsPrecedingAndSucceedingNodes;
	private final IASTTranslationUnit rootNode;

	//Constructors

	public CommentsHandler(final IASTTranslationUnit rootNode)
	{
		this.rootNode = rootNode;
		this.commentsPrecedingAndSucceedingNodes = new HashMap<>();
	}

	//Public methods

	/**
	 * This method computes the preceding and succeeding IASTNodes of each parsed comment of the given C program.
	 * Note that this method has the side effect of filling the class variable "commentsPrecedingAndSucceedingNodes".
	 * It must thus be used once and only once right after the parsing of the C program, if one wants to avoid dubious
	 * information in it.
	 *
	 * @return the class variable "commentsPrecedingAndSucceedingNodes".
	 */
	public Map<CComment, Pair<IASTNode, IASTNode>> computeCommentsPrecedingAndSucceedingNodes()
	{
		for (final IASTComment comment : this.rootNode.getComments())
		{
			final IASTFileLocation fileLocation = comment.getFileLocation();
			final String commentContent = String.valueOf(comment.getComment());

			final CComment cComment = new CComment(
				//A comment is multi-line if its starting and ending line numbers are different.
				fileLocation.getStartingLineNumber() != fileLocation.getEndingLineNumber() ? CCommentType.MULTI_LINE : CCommentType.SINGLE_LINE,
				//A comment is considered as an ACSL one if the first character after the comments characters is a '@'.
				commentContent.substring(2).trim().startsWith(ACSLKeyword.AT.getKeyword()) ? CCommentNature.ACSL : CCommentNature.REGULAR,
				fileLocation.getStartingLineNumber(),
				fileLocation.getEndingLineNumber(),
				commentContent
			);

			final Pair<IASTNode, IASTNode> beforeAndAfterNodes = this.getBeforeAndAfterNodesOf(cComment);
			this.commentsPrecedingAndSucceedingNodes.put(cComment, beforeAndAfterNodes);
		}

		return this.commentsPrecedingAndSucceedingNodes;
	}

	/**
	 * This method simply returns the class variable "commentsPrecedingAndSucceedingNodes".
	 * It is up to the user to use either this method or the "computeCommentsPrecedingAndSucceedingNodes()" one,
	 * depending on whether the map has already been filled or not.
	 *
	 * @return the class variable "commentsPrecedingAndSucceedingNodes".
	 */
	public Map<CComment, Pair<IASTNode, IASTNode>> getCommentsPrecedingAndSucceedingNodes()
	{
		return this.commentsPrecedingAndSucceedingNodes;
	}

	/**
	 * This method can be used to display on the standard output the mapping that were established between the
	 * comments and their preceding (resp. succeeding) IASTNodes.
	 * Nothing will be print if the "computeCommentsPrecedingAndSucceedingNodes()" method has not previously been
	 * called, or if the C program given as input does not contain any comment.
	 */
	public void displayMapping()
	{
		if (this.commentsPrecedingAndSucceedingNodes.isEmpty())
		{
			System.out.println("No mapping was found between the comments of the programs and some of its nodes.");
			return;
		}

		System.out.println("The following mappings between comments and program nodes were found:\n");

		for (final CComment comment : this.commentsPrecedingAndSucceedingNodes.keySet())
		{
			final Pair<IASTNode, IASTNode> precedingAndSucceedingNodes = this.commentsPrecedingAndSucceedingNodes.get(comment);

			System.out.printf(
				"\t- Comment \"%s\" is preceded by node \"%s\" and succeeded by node \"%s\".\n%n",
				comment.getContent(),
				precedingAndSucceedingNodes.getFirstElement() == null ? "null" : precedingAndSucceedingNodes.getFirstElement().toString(),
				precedingAndSucceedingNodes.getSecondElement() == null ? "null" : precedingAndSucceedingNodes.getSecondElement().toString()
			);
		}
	}

	//Private methods

	/**
	 * This method basically computes the preceding and the succeeding IASTNode of the C comment given as parameter.
	 * <p>
	 * Note that if the considered C program starts with a comment, the preceding IASTNode of this comment will be null,
	 * and if it ends with a comment, the succeeding IASTNode of this comment will be null too.
	 * <p>
	 * Note also that in its current form, this method is highly inefficient, as it traverses the C program AST twice
	 * as many times as there are comments in the C program, thus leading to a complexity of
	 * O(2 * |nb comment| * |nb nodes|).
	 * <p>
	 * Note finally that, due to its inherent structure, the AST corresponding to the given C program may contain
	 * several ASTNodes being, in terms of line number, the closest preceding (resp. succeeding) nodes of the given
	 * comment.
	 * In such cases, the topmost IASTNode of the hierarchy is retained as closest preceding (resp. succeeding) node.
	 *
	 * @param comment the comment to compute preceding and succeeding IASTNodes for.
	 * @return a pair containing the preceding and the succeeding IASTNodes of the given comment.
	 */
	private Pair<IASTNode, IASTNode> getBeforeAndAfterNodesOf(final CComment comment)
	{
		final IASTNode commentClosestBoundary = this.getClosestBoundaryOf(comment, this.rootNode, this.rootNode);

		System.out.printf("- Closest boundary of comment \"%s\" is %s.%n", comment.getContent(), commentClosestBoundary);
		//final IASTNode closestPrecedingNode = comment.getOriginalSourceCodeStartingLine() == 1 ? null : this.getClosestPrecedingNode(comment, this.rootNode, this.rootNode);
		//final IASTNode closestSucceedingNode = this.getClosestSucceedingNode(comment, this.rootNode, this.rootNode);

		return new Pair<>(null, null);
	}

	private IASTNode getClosestBoundaryOf(final CComment comment,
										  final IASTNode currentClosestBoundary,
										  final IASTNode currentNode)
	{
		if (currentNode.getFileLocation() == null)
		{
			//If the current node has no corresponding file location, we return the current closest boundary
			if (currentNode.getChildren() != null
				&& currentNode.getChildren().length != 0)
			{
				throw new RuntimeException("I think this case is not reachable, so let's check it.");
			}

			return currentClosestBoundary;
		}

		if (currentNode.getFileLocation().getStartingLineNumber() <= comment.getOriginalSourceCodeStartingLine()
			&& currentNode.getFileLocation().getEndingLineNumber() >= comment.getOriginalSourceCodeEndingLine())
		{
			//System.out.println("boundary found");
			//We found a boundary for our comment, let's check if it is at least as good as the one we already have.
			if (this.candidateIsBetterThanCurrentBoundary(currentNode, currentClosestBoundary))
			{
				//System.out.println("better boundary found");
				//The current node is better than our current boundary, let's go deeper with this new boundary
				if (currentNode.getChildren() == null
					|| currentNode.getChildren().length == 0)
				{
					//There is no deeper node, so the current one is the boundary
					return currentNode;
				}
				else
				{
					IASTNode closestBoundary = currentNode;

					for (final IASTNode child : currentNode.getChildren())
					{
						final IASTNode candidateClosestBoundary = this.getClosestBoundaryOf(comment, currentNode, child);

						if (this.candidateIsBetterThanCurrentBoundary(candidateClosestBoundary, closestBoundary))
						{
							closestBoundary = candidateClosestBoundary;
						}
					}

					//System.out.printf("Closest boundary of comment \"%s\" is %s.%n", comment.getContent(), closestBoundary);
					return closestBoundary;
				}
			}
		}

		return currentClosestBoundary;
	}

	private boolean candidateIsBetterThanCurrentBoundary(final IASTNode candidate,
														 final IASTNode boundary)
	{
		if (candidate.getFileLocation() == null)
		{
			return false;
		}

		return candidate.getFileLocation().getStartingLineNumber() >= boundary.getFileLocation().getStartingLineNumber()
			&& candidate.getFileLocation().getEndingLineNumber() <= boundary.getFileLocation().getEndingLineNumber();
	}

	private IASTNode getClosestPrecedingNode(final CComment comment,
											 final IASTNode currentClosestPrecedingNode,
											 final IASTNode currentNode)
	{
		final int currentNodeStartingLine = Utils.getStartingLineNumberOf(currentNode);
		final int currentNodeEndingLine = Utils.getEndingLineNumberOf(currentNode);

		//If our current node does not have line number information, we cannot rely on it to attach the comment.
		if (currentNodeStartingLine == -1
			|| currentNodeEndingLine == -1)
		{
			return currentClosestPrecedingNode;
		}

		if (comment.getOriginalSourceCodeStartingLine() >= currentNodeStartingLine
			&& comment.getOriginalSourceCodeEndingLine() <= currentNodeEndingLine)
		{
			/*
				The comment belongs to the current node, although we do not precisely know where => we try to get more
				fine-grained information.
			 */
			IASTNode closestChild = null;

			for (final IASTNode child : currentNode.getChildren())
			{
				if (closestChild == null)
				{
					closestChild = this.getClosestPrecedingNode(comment, child, child);
				}
				else
				{
					final IASTNode currentClosestCandidate = this.getClosestPrecedingNode(comment, currentNode, child);

					if (currentClosestCandidate.getFileLocation().getEndingLineNumber()
						> closestChild.getFileLocation().getEndingLineNumber())
					{
						closestChild = currentClosestCandidate;
					}
				}
			}

			if (closestChild == null)
			{
				if (currentClosestPrecedingNode.getFileLocation().getStartingLineNumber()
					> currentNode.getFileLocation().getStartingLineNumber())
				{
					return currentClosestPrecedingNode;
				}
				else
				{
					return currentNode;
				}
			}
			else
			{
				return null;
			}
		}
		else if (comment.getOriginalSourceCodeStartingLine() >= currentNodeEndingLine)
		{
			/*
				The comment appears after the current node, so its closest preceding node is the closest between the
				current node and the currentClosestPrecedingNode.
			 */
			if (currentClosestPrecedingNode.getFileLocation().getEndingLineNumber() > currentNodeEndingLine)
			{
				return currentClosestPrecedingNode;
			}
			else
			{
				return currentNode;
			}
		}
		else if (comment.getOriginalSourceCodeEndingLine() <= currentNodeStartingLine)
		{
			//The comment appears before the current node => we went too far, return the currentClosestPrecedingNode.
			return currentClosestPrecedingNode;
		}
		else
		{
			/*
				The comment starts before the current node and ends after it.
			 */
			throw new RuntimeException("Is this even possible ????????");
		}
	}

	private IASTNode getClosestSucceedingNode(final CComment comment,
											  final IASTNode closestSucceedingNode,
										      final IASTNode currentNode)
	{
		return null;
	}
}
