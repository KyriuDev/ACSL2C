package ast;

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

public abstract class AbstractSyntaxNode
{
	private final ArrayList<AbstractSyntaxNode> children;
	private final ArrayList<AbstractSyntaxNode> parents;

	//Constructors

	public AbstractSyntaxNode()
	{
		this.children = new ArrayList<>();
		this.parents = new ArrayList<>();
	}

	//Abstract methods

	public abstract void stringify(final StringBuilder builder,
								   final int depth);

	public abstract String checkWellFormedness();

	public abstract boolean collapse();

	//Public methods

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

	public AbstractSyntaxNode getFirstChild()
	{
		return this.children.isEmpty() ? null : this.children.get(0);
	}

	public AbstractSyntaxNode getLastChild()
	{
		return this.children.isEmpty() ? null : this.children.get(this.children.size() - 1);
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
		this.addChild(child);
		child.addParent(this);
	}

	public void addChildAtIndex(final AbstractSyntaxNode child,
								final int index)
	{
		if (index < 0
			|| index >= this.children.size())
		{
			throw new IndexOutOfBoundsException(String.format(
				"The index should be between 0 and %d, got %d.",
				this.children.size() - 1,
				index
			));
		}

		this.children.add(index, child);
	}

	public void addChildAtIndexAndForceParent(final AbstractSyntaxNode child,
											  final int index)
	{
		this.addChildAtIndex(child, index);
		child.addParent(this);
	}

	public void addParent(final AbstractSyntaxNode parent)
	{
		this.parents.add(parent);
	}

	public void addParentAndForceChild(final AbstractSyntaxNode parent)
	{
		this.addParent(parent);
		parent.addChild(this);
	}

	public void addParentAtIndex(final AbstractSyntaxNode parent,
						         final int index)
	{
		if (index < 0
			|| index >= this.parents.size())
		{
			throw new IndexOutOfBoundsException(String.format(
				"The index should be between 0 and %d, got %d.",
				this.parents.size() - 1,
				index
			));
		}

		this.parents.add(index, parent);
	}

	public void addParentAtIndexAndForceParent(final AbstractSyntaxNode parent,
						                       final int index)
	{
		this.addParentAtIndex(parent, index);
		parent.addParent(this);
	}

	public void removeChild(final AbstractSyntaxNode child)
	{
		this.children.remove(child);
	}

	public void removeChildAndForceParent(final AbstractSyntaxNode child)
	{
		this.removeChild(child);
		child.removeParent(this);
	}

	public AbstractSyntaxNode removeChildAtIndexAndForceParent(final int index)
	{
		if (index < 0
			|| index >= this.children.size())
		{
			throw new IndexOutOfBoundsException(String.format(
				"Index should be between 0 and %d, got %d.",
				this.children.size() - 1,
				index
			));
		}

		final AbstractSyntaxNode childToRemove = this.children.remove(index);
		childToRemove.removeParent(this);

		return childToRemove;
	}

	public AbstractSyntaxNode removeFirstChildAndForceParent()
	{
		return this.removeChildAtIndexAndForceParent(0);
	}

	public AbstractSyntaxNode removeLastChildAndForceParent()
	{
		return this.removeChildAtIndexAndForceParent(this.children.size() - 1);
	}

	public void removeParent(final AbstractSyntaxNode parent)
	{
		this.parents.remove(parent);
	}

	public void removeParentAndForceChild(final AbstractSyntaxNode parent)
	{
		this.removeParent(parent);
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

		this.removeAllChildren();
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

		this.removeAllParents();
	}

	public boolean hasSuccessorOfType(final Class<? extends AbstractSyntaxNode> type)
	{
		if (type.isInstance(this))
		{
			return true;
		}

		for (final AbstractSyntaxNode child : this.getChildren())
		{
			boolean hasSuccessorOfType = child.hasSuccessorOfType(type);

			if (hasSuccessorOfType)
			{
				return true;
			}
		}

		return false;
	}

	//Private methods
}
