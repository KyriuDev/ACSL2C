package ast.c;

import constants.c.CStorageClass;

/**
 * Name:        TypedefNameSpecifierNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTTypedefNameSpecifier" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class TypedefNameSpecifierNode extends IDeclarationSpecifierNode
{
	private final String typeName;

	//Constructors

	public TypedefNameSpecifierNode(final CStorageClass storageClass,
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

	//Public methods

	public String getTypeName()
	{
		return this.typeName;
	}
}
