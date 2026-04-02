package ast.acsl.nodes;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslType;
import misc.Utils;

/**
 * Name:        AcslBaseNode.java
 * Content:     This class defines a simple AcslBaseNode, which is the root node of any other ACSL node, with some
 * 				utility functions.
 * 				It basically inherits from AbstractSyntaxNode, and stores some more information such the type of the
 * 				considered ACSL element.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    18/03/26
 */

public class AcslBaseNode extends AbstractSyntaxNode
{
	private final AcslType type;

	//Constructors

	public AcslBaseNode(final AcslType type)
	{
		super();
		this.type = type;
	}

	//Overrides

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append(String.format("- %s has ", this.type.getReadableName()));

		if (this.getChildren().isEmpty())
		{
			builder.append("no child.");
		}
		else if (this.getChildren().size() == 1)
		{
			builder.append("1 child:");
			this.getChildren().iterator().next().stringify(builder, depth + 1);
		}
		else
		{
			builder.append(String.format("%d children:", this.getChildren().size()));

			for (final AbstractSyntaxNode child : this.getChildren())
			{
				child.stringify(builder, depth + 1);
			}
		}
	}

	/**
	 * This method checks whether the given abstract syntax node is "well-formed", in the sense of "coherent with the
	 * ACSL standard".
	 * It returns null if the node is well-formed, and a string detailing what makes the current node not well-formed
	 * otherwise.
	 * It should be overridden by specific nodes whose structure can be incorrect, yet compliant with the
	 * defined grammar.
	 * It can somehow be seen as a (very basic) type checking method.
	 * Note that in most cases, no coverage guarantee is given, i.e., this method may return "null" even if the
	 * checked node is malformed.
	 *
	 * @return null
	 */
	@Override
	public String checkWellFormedness()
	{
		return null;
	}

	/**
	 * This method is used to collapse a node whenever it can be collapsed.
	 * Basic nodes cannot be collapsed, in which case this function has no effect.
	 * Some nodes, such as BinaryOperationNodes, can be collapsed as the information of their OperatorNode child can
	 * be stored in the node itself, instead of as a (useless) child node.
	 * Such collapsable nodes should thus override this method to perform the proper collapse.
	 */
	@Override
	public boolean collapse()
	{
		boolean collapsed = false;

		for (final AbstractSyntaxNode child : this.getChildren())
		{
			collapsed = child.collapse() || collapsed;
		}

		return collapsed;
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		throw new UnsupportedOperationException("TODO!");
	}

	@Override
	public AbstractSyntaxNode deepCopy()
	{
		throw new UnsupportedOperationException("TODO!");
	}

	//Public methods

	public AcslType getType()
	{
		return this.type;
	}
}
