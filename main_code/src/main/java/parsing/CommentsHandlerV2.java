package parsing;

import constants.acsl.others.ACSLKeyword;
import constants.c.CCommentNature;
import constants.c.CCommentType;
import dto.CComment;
import misc.Pair;
import misc.Utils;
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Name:        CommentsHandlerV2.java
 * Content:     This class is in charge of handling the comments appearing in the original source code of the given
 * 				program.
 * 				More precisely, it associates each parsed comment to a pair of IASTNode such that the first/left
 * 				IASTNode of the pair corresponds to the portion of code appearing right before the comment, and the
 * 				second/right IASTNode of the pair corresponds to the portion of code appearing right after the comment.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class CommentsHandlerV2
{
	private final Map<CComment, Surrounding> commentsSurroundings;
	private final Map<IASTNode, List<CComment>> commentsPrecedingNodes;
	private final IASTTranslationUnit rootNode;

	//Constructors

	public CommentsHandlerV2(final IASTTranslationUnit rootNode)
	{
		this.rootNode = rootNode;
		this.commentsSurroundings = new HashMap<>();
		this.commentsPrecedingNodes = new HashMap<>();
	}

	//Public methods

	/**
	 * This method returns the list of comments that appear immediately before a given IASTNode.
	 * It is used to know when a comment should be dumped to the file.
	 * It somehow reverses and aggregates the information stored in commentsSurroundings.
	 * If this information is sufficient to properly write the comments back to their exact position in the C program,
	 * it probably means that the notion of surrounding is too powerful and could probably be replaced by something
	 * weaker, to lower the overall complexity of calling this class.
	 *
	 * @return a map whose keys are IASTNodes, and values are the list of comments that immediately precede these nodes.
	 */
	public Map<IASTNode, List<CComment>> getCommentsPrecedingNodes()
	{
		if (this.commentsPrecedingNodes.isEmpty()
			&& !this.commentsSurroundings.isEmpty())
		{
			//Comments following nodes have not been computed yet.
			for (final CComment comment : this.commentsSurroundings.keySet())
			{
				final Surrounding surrounding = this.commentsSurroundings.get(comment);
				final List<CComment> commentsBeforeNode = this.commentsPrecedingNodes.computeIfAbsent(
					surrounding.getClosestSucceedingNode(),
					a -> new ArrayList<>()
				);
				commentsBeforeNode.add(comment);
			}
		}

		return this.commentsPrecedingNodes;
	}

	/**
	 * This method is used to find the trailing comments of the C program, if any.
	 * Such comments are comments that appear at the very end of the C program, after everything else, and which
	 * must thus be written last, after the C program.
	 *
	 * @return the list of trailing comments, if any, or an empty list otherwise.
	 */
	public List<CComment> getTrailingComments()
	{
		final ArrayList<CComment> list = new ArrayList<>();

		for (final CComment comment : this.commentsSurroundings.keySet())
		{
			final Surrounding surrounding = this.commentsSurroundings.get(comment);

			if (surrounding.getClosestBoundary() instanceof IASTTranslationUnit
				&& surrounding.getClosestSucceedingNode() == null)
			{
				//The comment is at the bottommost level of the C program and does not have any following statement
				list.add(comment);
			}
		}

		return list;
	}

	/**
	 * This method computes the preceding and succeeding IASTNodes of each parsed comment of the given C program.
	 * Note that this method has the side effect of filling the class variable "commentsPrecedingAndSucceedingNodes".
	 * It must thus be used once and only once right after the parsing of the C program, if one wants to avoid dubious
	 * information in it.
	 *
	 * @return the class variable "commentsPrecedingAndSucceedingNodes".
	 */
	public Map<CComment, Surrounding> computeCommentsPrecedingAndSucceedingNodes()
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

			final Surrounding beforeAndAfterNodes = this.getBeforeAndAfterNodesOf(cComment);

			this.commentsSurroundings.put(cComment, beforeAndAfterNodes);
		}

		return this.commentsSurroundings;
	}

	/**
	 * This method simply returns the class variable "commentsPrecedingAndSucceedingNodes".
	 * It is up to the user to use either this method or the "computeCommentsPrecedingAndSucceedingNodes()" one,
	 * depending on whether the map has already been filled or not.
	 *
	 * @return the class variable "commentsPrecedingAndSucceedingNodes".
	 */
	public Map<CComment, Surrounding> getCommentsSurroundings()
	{
		return this.commentsSurroundings;
	}

	/**
	 * This method can be used to display on the standard output the mapping that were established between the
	 * comments and their preceding (resp. succeeding) IASTNodes.
	 * Nothing will be print if the "computeCommentsPrecedingAndSucceedingNodes()" method has not previously been
	 * called, or if the C program given as input does not contain any comment.
	 */
	public void displayMapping()
	{
		if (this.commentsSurroundings.isEmpty())
		{
			System.out.println("No mapping was found between the comments of the programs and some of its nodes.");
			return;
		}

		System.out.println("The following mappings between comments and program nodes were found:\n");

		for (final CComment comment : this.commentsSurroundings.keySet())
		{
			final Surrounding precedingAndSucceedingNodes = this.commentsSurroundings.get(comment);

			System.out.printf(
				"\t- Comment \"%s\" is preceded by node \"%s\" and succeeded by node \"%s\".\n%n",
				comment.getContent(),
				precedingAndSucceedingNodes.getClosestPrecedingNode() == null ? "null" : precedingAndSucceedingNodes.getClosestPrecedingNode().toString(),
				precedingAndSucceedingNodes.getClosestSucceedingNode() == null ? "null" : precedingAndSucceedingNodes.getClosestSucceedingNode().toString()
			);
		}
	}

	/**
	 * This method is a convenience method used only for test purposes of other parts of this project.
	 * It simply returns the first comment found by the iterator, or null if the C program does not contain any comment.
	 *
	 * @return the first comment found by the iterator.
	 */
	public CComment getRandomComment()
	{
		if (this.commentsSurroundings.isEmpty())
		{
			return null;
		}

		return this.commentsSurroundings.keySet().iterator().next();
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
	private Surrounding getBeforeAndAfterNodesOf(final CComment comment)
	{
		//First, we compute the closest boundary of our comment
		final IASTNode commentClosestBoundary = this.getClosestBoundaryOf(comment, this.rootNode, this.rootNode);
		System.out.printf("- Closest boundary of comment \"%s\" is %s.%n", comment.getContent(), commentClosestBoundary);

		//Then, we retrieve the preceding and succeeding statements of our comment inside its boundary
		final Pair<IASTNode, IASTNode> closestPrecedindAndSucceedingNodes = this.getClosestPrecedingAndSucceedingNodesOf(comment, commentClosestBoundary);

		return new Surrounding(
			commentClosestBoundary,
			closestPrecedindAndSucceedingNodes.getFirstElement(),
			closestPrecedindAndSucceedingNodes.getSecondElement()
		);
	}

	/**
	 * This method is used to compute the closest boundary of the given comment.
	 * <p>
	 * By closest boundary, we mean the closest node that encompasses the comment.
	 * If the comment is not at the top level of the C program, its closest boundary will be the root node of the AST,
	 * i.e., the IASTTranslationUnit.
	 * Otherwise, if the comment is inside a function, or a complex statement (if, while, etc.), the closest structure
	 * encompassing it will be returned.
	 * For instance, the closest boundary of "//Comment to get boundary of" while be the body of the while loop.
	 * Note that in this case, the function "main()" is also a boundary of the comment, but not the closest one.
	 * <p>
	 * int main(){
	 *     while (true){
	 *         //Comment to get boundary of
	 *     }
	 * }
	 * <p>
	 * In the not-so-common case of single line comments written on the same line as a statement, right next to it, the
	 * boundary might not be really meaningful, but this will be handled later.
	 *
	 * @param comment the comment to get the boundary of.
	 * @param currentClosestBoundary the current closest boundary of the comment, the root node of the AST by default.
	 * @param currentNode the current to check.
	 * @return the IASTNode that is the closest boundary of the given comment.
	 */
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
			//We found a boundary for our comment, let's check if it is at least as good as the one we already have.
			if (this.candidateIsBetterThanCurrentBoundary(currentNode, currentClosestBoundary))
			{
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

	/**
	 * This method is a utility method aiming at assessing whether a given candidate node would be a better closest
	 * boundary than the current closest boundary.
	 *
	 * @param candidate the candidate closest boundary.
	 * @param boundary the current closest boundary.
	 * @return true if the candidate is better than the current boundary, false otherwise.
	 */
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

	/**
	 * This method returns the closest preceding and succeeding nodes of the given comment, given the closest boundary
	 * of that comment.
	 * A null preceding node means that the comment is the first "statement" of the boundary, while a null succeeding
	 * node means that it is the last one.
	 *
	 * @param comment the comment to get the preceding/succeeding nodes of.
	 * @param closestBoundary the closest boundary of the comment.
	 * @return a pair containing the closest preceding and succeeding nodes of the given comment, if any.
	 */
	private Pair<IASTNode, IASTNode> getClosestPrecedingAndSucceedingNodesOf(final CComment comment,
																			 final IASTNode closestBoundary)
	{
		if (!Utils.iastNodeHasChildren(closestBoundary))
		{
			/*
				If the closest boundary does not contain any statement, then its body is composed uniquely of the
				comment.
				For instance:
				while (true){
					//My body is only composed of a comment!
				}
				Thus, the closest preceding and succeeding nodes are the boundary itself.
				Note that this may also be the case if the comment is a single line comment that is on the same line
				as a statement, for instance:
										int x = 0; //My comment is on the same line
				In such a case, the closest boundary of the comment might be erroneous, in the sense that its
				computation might have been too far.
				In the given example, the computed closest boundary is "x" instead of the full statement.
				Thus, we correct it.
			 */

			if (comment.isSingleLineComment()
				&& comment.getOriginalSourceCodeStartingLine() == closestBoundary.getFileLocation().getStartingLineNumber()
				&& comment.getOriginalSourceCodeStartingLine() == closestBoundary.getFileLocation().getEndingLineNumber())
			{
				/*
					We correct the boundary by going upwards in the tree until reaching a node that does not start on
					the same line as our boundary, or that does not end on the same line as our boundary.
				 */
				final IASTNode correctBoundary = this.correctBoundary(closestBoundary);
				return new Pair<>(correctBoundary, correctBoundary);
			}
			else
			{
				return new Pair<>(closestBoundary, closestBoundary);
			}
		}
		else
		{
			/*
				If the closest boundary contain statements, we know that these statements cannot encompass our comment
				by definition, as they would otherwise be the closest boundary of the comment.
				Thus, we can deduce that our comment lies between two statements of the current boundary, so we only
				need to check the line numbers of the children of the boundary to get the position of the comment, and
				consequently its preceding/succeeding nodes.
				More precisely, we look for the statement whose end is the closest to the start of our comment, and for
				the statement whose start is the closest to the end of our comment.
			 */
			IASTNode closestPrecedingNode = null;
			IASTNode closestSucceedingNode = null;

			for (final IASTNode child : closestBoundary.getChildren())
			{
				final int childStartingLine = Utils.getStartingLineNumberOf(child);
				final int childEndingLine = Utils.getEndingLineNumberOf(child);

				if (childEndingLine != -1)
				{
					if (childEndingLine <= comment.getOriginalSourceCodeStartingLine())
					{
						//The child is before our comment, check if it is a better candidate than closestPrecedingNode
						if (closestPrecedingNode == null)
						{
							closestPrecedingNode = child;
						}
						else
						{
							if (childEndingLine >= closestPrecedingNode.getFileLocation().getEndingLineNumber())
							{
								//The child is indeed a better candidate than closestPrecedingNode
								closestPrecedingNode = child;
							}
						}
					}
				}

				if (childStartingLine != -1)
				{
					if (childStartingLine >= comment.getOriginalSourceCodeEndingLine())
					{
						//The child is after our comment, check if it is a better candidate than closestSucceedingNode
						if (closestSucceedingNode == null)
						{
							closestSucceedingNode = child;
						}
						else
						{
							if (childStartingLine < closestSucceedingNode.getFileLocation().getStartingLineNumber())
							{
								//The child is indeed a better candidate than closestSucceedingNode
								closestSucceedingNode = child;
							}
						}
					}
				}
			}

			return new Pair<>(closestPrecedingNode, closestSucceedingNode);
		}
	}

	private IASTNode correctBoundary(final IASTNode closestBoundary)
	{
		final IASTNode parent = closestBoundary.getParent();

		if (parent == null)
		{
			return closestBoundary;
		}

		final int parentStartingLineNumber = Utils.getStartingLineNumberOf(parent);
		final int parentEndingLineNumber = Utils.getEndingLineNumberOf(parent);

		if (parentStartingLineNumber == -1
			|| parentEndingLineNumber == -1
			|| parentStartingLineNumber != closestBoundary.getFileLocation().getStartingLineNumber()
			|| parentEndingLineNumber != closestBoundary.getFileLocation().getEndingLineNumber())
		{
			//The parent is not as good as the current closest boundary
			return closestBoundary;
		}

		//The parent is as good as the current closest boundary, so we recursively check its own parent.
		return this.correctBoundary(parent);
	}

	/**
	 * The surrounding of a comment consists in the elements that allow to precisely identify its location in the
	 * program.
	 * In our case, the surrounding is composed of the closest boundary of the comment, its closest preceding node,
	 * and its closest succeeding node.
	 */
	public static class Surrounding
	{
		private final IASTNode closestBoundary;
		private final IASTNode closestPrecedingNode;
		private final IASTNode closestSucceedingNode;

		//Constructors

		public Surrounding(final IASTNode closestBoundary,
						   final IASTNode closestPrecedingNode,
						   final IASTNode closestSucceedingNode)
		{
			this.closestBoundary = closestBoundary;
			this.closestPrecedingNode = closestPrecedingNode;
			this.closestSucceedingNode = closestSucceedingNode;
		}

		//Public methods

		public IASTNode getClosestBoundary()
		{
			return this.closestBoundary;
		}

		public IASTNode getClosestPrecedingNode()
		{
			return this.closestPrecedingNode;
		}

		public IASTNode getClosestSucceedingNode()
		{
			return this.closestSucceedingNode;
		}

		//Private methods
	}
}
