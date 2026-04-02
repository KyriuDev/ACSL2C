package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;
import constants.c.CStorageClass;

/**
 * Name:        TypedefNameSpecifierNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTTypedefNameSpecifier" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CTypedefNameSpecifierNode extends ICDeclarationSpecifierNode
{
	private final String typeName;

	//Constructors

	public CTypedefNameSpecifierNode(final CStorageClass storageClass,
	                                 final boolean isConst,
	                                 final boolean isInline,
	                                 final boolean isRestrict,
	                                 final boolean isVolatile,
	                                 final String typeName)
	{
		super(storageClass, isConst, isInline, isRestrict, isVolatile);
		this.typeName = typeName;
	}

	//Overrides

	@Override
	public String getNodeHeader()
	{
		return String.format(
			"- Typedef name specifier \"%s %s\" has ",
			super.getNodeHeader(),
			this.typeName
		);
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createTypedefNameSpecifierNode(
			this.getStorageClass(),
			this.isConst(),
			this.isInline(),
			this.isRestrict(),
			this.isVolatile(),
			this.typeName
		);
	}

	//Public methods

	public String getTypeName()
	{
		return this.typeName;
	}
}
