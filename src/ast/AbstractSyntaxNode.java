package ast;

import constants.acsl.others.AcslType;
import misc.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Name:        AbstractSyntaxNode.java
 * Content:     This class defines a simple AbstractSyntaxNode, with some utility functions.
 *              It consists of a type, a set of children (if any), and a set of parents (if any).
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    10/03/26
 */

public class AbstractSyntaxNode
{
	private final ArrayList<AbstractSyntaxNode> children;
	private final ArrayList<AbstractSyntaxNode> parents;
	private final AcslType type;

	//Constructors

	public AbstractSyntaxNode(final AcslType type)
	{
		this.type = type;
		this.children = new ArrayList<>();
		this.parents = new ArrayList<>();
	}

	//Public methods

	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append(String.format("- %s has ", this.type.getReadableName()));

		if (this.children.isEmpty())
		{
			builder.append("no child.");
		}
		else if (this.children.size() == 1)
		{
			builder.append("1 child:");
			this.children.iterator().next().stringify(builder, depth + 1);
		}
		else
		{
			builder.append(String.format("%d children:", this.children.size()));

			for (final AbstractSyntaxNode child : this.children)
			{
				child.stringify(builder, depth + 1);
			}
		}
	}

	public AcslType getType()
	{
		return this.type;
	}

	/**
	 * This method returns the children nodes of the current node in a read-only mode, forcing classical add/remove
	 * methods to be used to modify the collection.
	 *
	 * @return the immutable list of children of the current node.
	 */
	public List<AbstractSyntaxNode> getChildren()
	{
		return Collections.unmodifiableList(this.children);
	}

	/**
	 * This method returns the parent nodes of the current node in a read-only mode, forcing classical add/remove
	 * methods to be used to modify the collection.
	 *
	 * @return the immutable list of parents of the current node.
	 */
	public List<AbstractSyntaxNode> getParents()
	{
		return Collections.unmodifiableList(this.parents);
	}

	public void addChild(final AbstractSyntaxNode child)
	{
		this.children.add(child);
	}

	public void addChildAndForceParent(final AbstractSyntaxNode child)
	{
		this.children.add(child);
		child.addParent(this);
	}

	public void addParent(final AbstractSyntaxNode parent)
	{
		this.parents.add(parent);
	}

	public void addParentAndForceChild(final AbstractSyntaxNode parent)
	{
		this.parents.add(parent);
		parent.addChild(this);
	}

	public void removeChild(final AbstractSyntaxNode child)
	{
		this.children.remove(child);
	}

	public void removeChildAndForceParent(final AbstractSyntaxNode child)
	{
		this.children.remove(child);
		child.removeParent(this);
	}

	public void removeParent(final AbstractSyntaxNode parent)
	{
		this.parents.remove(parent);
	}

	public void removeParentAndForceChild(final AbstractSyntaxNode parent)
	{
		this.parents.remove(parent);
		parent.removeChild(this);
	}

	public void removeAllChildren()
	{
		this.children.clear();
	}

	public void removeAllChildrenAndForceParent()
	{
		for (final AbstractSyntaxNode child : this.children)
		{
			child.removeParent(this);
		}

		this.children.clear();
	}

	public void removeAllParents()
	{
		this.parents.clear();
	}

	public void removeAllParentsAndForceChild()
	{
		for (final AbstractSyntaxNode parent : this.parents)
		{
			parent.removeChild(this);
		}

		this.parents.clear();
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
	public void collapse()
	{
		for (final AbstractSyntaxNode child : this.children)
		{
			child.collapse();
		}
	}

	//Private methods
}
