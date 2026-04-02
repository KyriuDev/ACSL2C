package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;
import constants.c.CElaboratedTypeSpecifier;

/**
 * Name:        ElaboratedTypeSpecifierNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTElaboratedTypeSpecifierNode" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class CElaboratedTypeSpecifierNode extends CBaseNode
{
	private final CElaboratedTypeSpecifier typeSpecifier;

	//Constructors

	public CElaboratedTypeSpecifierNode(final CElaboratedTypeSpecifier typeSpecifier)
	{
		this.typeSpecifier = typeSpecifier;
	}

	//Overrides

	@Override
	public String getNodeHeader()
	{
		return String.format("- Elaborated type specifier \"%s\" has ", typeSpecifier.toString());
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createElaboratedTypeSpecifierNode(this.typeSpecifier);
	}

	//Public methods

	public CElaboratedTypeSpecifier getTypeSpecifier()
	{
		return this.typeSpecifier;
	}
}
