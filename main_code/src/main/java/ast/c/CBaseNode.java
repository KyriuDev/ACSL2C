package ast.c;

import ast.AbstractSyntaxNode;
import misc.Utils;

/**
 * Name:        CBaseNode.java
 * Content:     This class defines a simple CBaseNode, which is the root node of any other C node, with some
 * 				utility functions.
 * 				It basically inherits from AbstractSyntaxNode, and stores some more information such the type of the
 * 				considered C element.
 * 				The reason for this class to exist despite the existence and the use of the
 * 				"org.eclipse.cdt.internal.core.dom.parser.c.CAST<classname>" is simplicity.
 * 				Indeed, "org.eclipse.cdt.internal.core.dom.parser.c.CAST<classname>" classes are rather opaque and
 * 				sometimes complex to use (for instance, their authors explicitly state that any method call may alter
 * 				the structure of the considered AST, although we want to rely on it for some further transformations).
 * 				For these reasons, I decided to recreate my own implementation of these classes, with identical names
 * 				deprived of their "CAST" prefix and suffixed with "Node" (e.g., the "CASTTranslationUnit" class becomes
 * 				"TranslationUnitNode").
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public abstract class CBaseNode extends AbstractSyntaxNode
{
	//Constructors

	public CBaseNode()
	{

	}

	//Abstract methods
	public abstract String getNodeHeader();

	//Overrides

	@Override
	public void stringify(final StringBuilder builder,
	                      final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append(this.getNodeHeader());

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
	 * Eclipse-CDT plugin standard".
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
	 * Some nodes, such as TODO
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
}
