package ast.acsl.nodes;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslType;
import constants.acsl.others.AcslTypeSpecifier;
import misc.Utils;

/**
 * Name:        TypeSpecifierNode.java
 * Content:	    This class defines a TypeSpecifierNode with a kind describing what specifier it actually is.
 * 				It can be any specifier type defined in AcslTypeSpecifier, such as "void", "int", "float", etc.
 * 				It should not have any child;
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    18/03/26
 */

public class TypeSpecifierNode extends AcslBaseNode
{
	private AcslTypeSpecifier kind;

	//Constructors

	public TypeSpecifierNode()
	{
		this(null);
	}

	public TypeSpecifierNode(final AcslTypeSpecifier kind)
	{
		super(AcslType.TYPE_SPECIFIER);
		this.kind = kind;
	}

	//Overrides

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append(String.format("- Type specifier \"%s\" has ", this.kind == null ? null : this.kind.getXmlTag()));

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

	@Override
	public String checkWellFormedness()
	{
		if (!this.getChildren().isEmpty())
		{
			return String.format(
				"TypeSpecifierNode is malformed: it should not have any child but has %d",
				this.getChildren().size()
			);
		}

		return null;
	}

	//Public methods

	public AcslTypeSpecifier getKind()
	{
		return this.kind;
	}

	public void setKind(final AcslTypeSpecifier kind)
	{
		this.kind = kind;
	}
}
