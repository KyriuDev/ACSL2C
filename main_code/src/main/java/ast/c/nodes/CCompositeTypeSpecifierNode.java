package ast.c.nodes;

import acsl_to_c.ACSL2ASTTranslator;
import ast.AbstractSyntaxNode;
import ast.c.CFactory;
import constants.c.CStorageClass;
import constants.c.CType;

/**
 * Name:        CompositeTypeSpecifierNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTCompositeTypeSpecifier" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class CCompositeTypeSpecifierNode extends CBaseNode
{
	private final CStorageClass storageClass;

	//Constructors

	public CCompositeTypeSpecifierNode(final CStorageClass storageClass)
	{
		this.storageClass = storageClass;
	}

	//Overrides

	@Override
	public String getNodeHeader()
	{
		return String.format(
			"- Composite type specifier (%sstruct) has ",
			this.storageClass == CStorageClass.UNSPECIFIED ? "" : this.storageClass.toString() + " "
		);
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createCompositeTypeSpecifierNode(this.storageClass);
	}

	//Public methods

	public CStorageClass getStorageClass()
	{
		return this.storageClass;
	}

	//TODO Risky method, not really clean :-/
	public void setName(final String name)
	{
		this.addChildAndForceParent(CFactory.createNameNode(name));
	}

	//TODO Risky method, not really clean :-/
	public void addComponent(final ACSL2ASTTranslator.SimpleVariable simpleVariable)
	{
		//Variable base node
		final CSimpleDeclarationNode simpleDeclarationNode = CFactory.createSimpleDeclarationNode();
		this.addChildAndForceParent(simpleDeclarationNode);

		//Add variable type, either predefined or user-defined
		if (simpleVariable.getType() != CType.USER_DEFINED)
		{
			final CSimpleDeclSpecifierNode simpleDeclSpecifierNode = CFactory.createSimpleDeclarationSpecifierNode(
				CStorageClass.UNSPECIFIED,
				simpleVariable.getType(),
				true,
				false,
				false,
				false
			);

			simpleDeclarationNode.addChildAndForceParent(simpleDeclSpecifierNode);
		}
		else
		{
			final CTypedefNameSpecifierNode typedefNameSpecifierNode = CFactory.createTypedefNameSpecifierNode(
				CStorageClass.UNSPECIFIED,
				true,
				false,
				false,
				false,
				simpleVariable.getType().toString()
			);

			simpleDeclarationNode.addChildAndForceParent(typedefNameSpecifierNode);
		}

		//Add variable name
		final CDeclaratorNode CDeclaratorNode = CFactory.createDeclaratorNode();
		simpleDeclarationNode.addChildAndForceParent(CDeclaratorNode);

		final CNameNode nameNode = CFactory.createNameNode(simpleVariable.getName());
		CDeclaratorNode.addChildAndForceParent(nameNode);
	}
}
