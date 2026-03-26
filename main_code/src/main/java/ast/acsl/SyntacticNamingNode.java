package ast.acsl;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslPredicateOrTermKind;
import constants.acsl.others.AcslType;
import misc.Utils;

/**
 * Name:        SyntacticNamingNode.java
 * Content:	    This class defines a SyntacticNamingNode which represents the syntactic naming of an ACSL object, such
 * 				as a behaviour.
 * 				It has a NameNode child representing the name of the object, and a PredicateOrTermNode representing the
 * 				object itself.
 * 				For instance, the expression "behavior t_is_sorted : [...]" will be represented as a SyntacticNamingNode
 * 				whose children will be a NameNode containing "t_is_sorted", and a PredicateOrTermNode representing the
 * 				behaviour's behaviour.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    18/03/26
 */

public class SyntacticNamingNode extends PredicateOrTermNode
{
	private String name;

	//Constructors

	public SyntacticNamingNode()
	{
		super(AcslPredicateOrTermKind.SYNTACTIC_NAMING);
	}

	public SyntacticNamingNode(final String content)
	{
		super(AcslPredicateOrTermKind.SYNTACTIC_NAMING, content);
	}

	//Overrides

	/**
	 * A syntactic naming node is considered to be well-formed as long it as a non-null non-empty "name" and exactly one
	 * child being a predicate or a term.
	 *
	 * @return null if the current node is well-formed, an error message otherwise.
	 */
	@Override
	public String checkWellFormedness()
	{
		if (this.name == null
			|| this.name.isEmpty()
			|| this.getChildren().size() != 1
			|| ((AcslBaseNode) this.getChildren().get(0)).getType() != AcslType.PREDICATE_OR_TERM)
		{
			return String.format(
				"Syntactic naming node is malformed:" +
				"\n\t- Expected non-null non-empty name, got \"%s\";" +
				"\n\t- Expected 1 child, got %d;" +
				"\n\t- Expected the child to be a predicate or a term, got a \"%s\".",
				this.name == null ? null : this.name.trim(),
				this.getChildren().size(),
				!this.getChildren().isEmpty() ? ((AcslBaseNode) this.getChildren().get(0)).getType().getReadableName() : null
			);
		}

		return null;
	}

	/**
	 * A syntactic naming node can be collapsed in the sense that its NameNode child information can be stored
	 * directly in the node.
	 * This is what this method performs, thus ending with the removal of the no longer useful child.
	 */
	@Override
	public boolean collapse()
	{
		boolean collapsed = false;
		AbstractSyntaxNode childToRemove = null;

		for (final AbstractSyntaxNode child : this.getChildren())
		{
			if (child instanceof NameNode)
			{
				this.setName(((NameNode) child).getName());
				childToRemove = child;
				child.removeParent(this);
				collapsed = true;
				/*
					This break is useful to handle malformed SyntacticNamingNode when method "checkWellFormedness()"
					will be called.
					Otherwise, it may silently collapse multiple NameNodes children, which is a structural error.
				 */
				break;
			}
		}

		this.removeChild(childToRemove);

		return collapsed || super.collapse();
	}

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append(String.format("- Syntactic naming \"%s\" has ", this.name == null ? null : this.name));

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

	//Public methods

	public void setName(final String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}
}
