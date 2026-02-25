package visitors;

import misc.Utils;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.c.*;

import java.util.Arrays;

public class RecursiveVisitor
{
	/**
	 * Name:        RecursiveVisitor.java
	 * Content:	    This class defines a recursive AST visitor that allows "simpler" (in the sense of "more similar to
	 *              what we have in mind") traversal of the AST, by using a recursion-based DFS.
	 * Author:      Quentin Nivon
	 * Email:       quentin.nivon@uol.de
	 * Creation:    25/02/26
	 */

	private final IASTNode rootNode;

	//Constructors

	public RecursiveVisitor(final IASTNode rootNode)
	{
		this.rootNode = rootNode;
	}

	//Public methods

	public void printAST()
	{
		final StringBuilder builder = new StringBuilder();
		this.printAST(this.rootNode, 0, builder);
		System.out.println(builder.toString());
	}

	//Private methods

	private void printAST(final IASTNode node,
					 	  final int depth,
						  final StringBuilder builder)
	{
		builder.append(Utils.addLeadingTabulations(depth))
				.append(String.format("- Node \"%s\" (%s) has ", node.toString(), this.addNodeSpecificData(node)));

		if (node.getChildren() != null
			&& node.getChildren().length > 0)
		{
			builder.append(String.format("%d children:\n", node.getChildren().length));
		}
		else
		{
			builder.append("no child.\n");
		}

		for (final IASTNode child : node.getChildren())
		{
			printAST(child, depth + 1, builder);
		}
	}

	private String addNodeSpecificData(final IASTNode node)
	{
		if (node instanceof IASTName)
		{
			final IASTName iastName = (IASTName) node;
			return iastName.resolveBinding().getName();
		}
		else if (node instanceof IASTStatement)
		{
			final IASTStatement iastStatement = (IASTStatement) node;


			if (iastStatement instanceof IASTReturnStatement)
			{
				final IASTReturnStatement returnStatement = (IASTReturnStatement) iastStatement;

				if (returnStatement instanceof CASTReturnStatement)
				{

				}
			}

			return Arrays.toString(iastStatement.getAttributeSpecifiers());
		}
		else if (node instanceof IASTParameterDeclaration)
		{
			final IASTParameterDeclaration iastParameterDeclaration = (IASTParameterDeclaration) node;
			return String.format("%s/%s", iastParameterDeclaration.getDeclarator().toString(), iastParameterDeclaration.getDeclSpecifier().toString());
		}

		return "";
	}
}
