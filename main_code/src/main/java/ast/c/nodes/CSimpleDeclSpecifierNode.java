package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;
import constants.c.CStorageClass;
import constants.c.CType;

/**
 * Name:        SimpleDeclSpecifierNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTSimpleDeclSpecifier" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CSimpleDeclSpecifierNode extends ICDeclarationSpecifierNode
{
	private final CType type;

	//Constructors

	public CSimpleDeclSpecifierNode(final CStorageClass storageClass,
	                                final CType type,
	                                final boolean isConst,
	                                final boolean isInline,
	                                final boolean isRestrict,
	                                final boolean isVolatile)
	{
		super(storageClass, isConst, isInline, isRestrict, isVolatile);
		this.type = type;
	}

	//Overrides

	@Override
	public String getNodeHeader()
	{
		return String.format(
			"- Simple declaration specifier \"%s %s\" has ",
			super.getNodeHeader(),
			this.type.getType()
		);
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createSimpleDeclarationSpecifierNode(
			this.getStorageClass(),
			this.type,
			this.isConst(),
			this.isInline(),
			this.isRestrict(),
			this.isVolatile()
		);
	}

	//Public methods

	public CType getType()
	{
		return this.type;
	}
}
