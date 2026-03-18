package ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Name:        AbstractSyntaxTree.java
 * Content:     This class defines a simple AbstractSyntaxTree, with some utility functions.
 *              It is basically defined as a root node.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    10/03/26
 */

public class AbstractSyntaxTree
{
	private AbstractSyntaxNode root;

	//Constructors

	public AbstractSyntaxTree()
	{
		this(null);
	}

	public AbstractSyntaxTree(final AbstractSyntaxNode root)
	{
		this.root = root;
	}

	//Overrides

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		this.root.stringify(builder, 0);

		return builder.toString();
	}

	//Public methods

	public void setRoot(final AbstractSyntaxNode root)
	{
		this.root = root;
	}

	public AbstractSyntaxNode getRoot()
	{
		return this.root;
	}

	/**
	 * This method is used to collapse the generated tree by rearranging the information stored in the XML file to make
	 * it more convenient to manipulate.
	 * For instance, a predicate or term node of binary type will see its "operator" child be removed and the information
	 * stored in the node itself.
	 * This method is followed by a call to the "checkWellFormedness()" method which prevents some undesired behaviours
	 * from happening.
	 * Note that the collapse may require several applications to properly collapse the tree, thus it is repeated until
	 * no collapse operations have been performed during the last iteration.
	 */
	public void collapseTree()
	{
		boolean collapsed = this.root.collapse();

		while (collapsed)
		{
			collapsed = this.root.collapse();
		}

		final List<String> incoherences = this.checkWellFormedness();

		if (!incoherences.isEmpty())
		{
			throw new RuntimeException(String.format(
				"The generated abstract syntax tree contains some incoherences: %s",
				incoherences
			));
		}
	}

	public List<String> checkWellFormedness()
	{
		final ArrayList<String> incoherences = new ArrayList<>();
		this.checkWellFormedness(this.root, incoherences);
		return incoherences;
	}

	//Private methods

	private void checkWellFormedness(final AbstractSyntaxNode currentNode,
									 final ArrayList<String> incoherences)
	{
		final String incoherence = currentNode.checkWellFormedness();

		if (incoherence != null)
		{
			incoherences.add(incoherence);
		}

		for (final AbstractSyntaxNode child : currentNode.getChildren())
		{
			this.checkWellFormedness(child, incoherences);
		}
	}
}
