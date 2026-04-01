package ast.c;

import constants.c.CElaboratedTypeSpecifier;

/**
 * Name:        ElaboratedTypeSpecifierNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTElaboratedTypeSpecifierNode" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class ElaboratedTypeSpecifierNode extends CBaseNode
{
	private final CElaboratedTypeSpecifier typeSpecifier;

	//Constructors

	public ElaboratedTypeSpecifierNode(final CElaboratedTypeSpecifier typeSpecifier)
	{
		this.typeSpecifier = typeSpecifier;
	}

	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Elaborated type specifier has ";
	}

	//Public methods

	public CElaboratedTypeSpecifier getTypeSpecifier()
	{
		return this.typeSpecifier;
	}
}
