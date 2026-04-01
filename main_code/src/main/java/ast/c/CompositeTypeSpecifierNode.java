package ast.c;

import acsl_to_c.ACSL2ASTTranslator;
import constants.c.CStorageClass;
import constants.c.CType;

/**
 * Name:        CompositeTypeSpecifierNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTCompositeTypeSpecifier" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class CompositeTypeSpecifierNode extends CBaseNode
{
	private final CStorageClass storageClass;

	//Constructors

	public CompositeTypeSpecifierNode(final CStorageClass storageClass)
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
		final SimpleDeclarationNode simpleDeclarationNode = CFactory.createSimpleDeclarationNode();
		this.addChildAndForceParent(simpleDeclarationNode);

		//Add variable type, either predefined or user-defined
		if (simpleVariable.getType() != CType.USER_DEFINED)
		{
			final SimpleDeclSpecifierNode simpleDeclSpecifierNode = CFactory.createSimpleDeclarationSpecifierNode(
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
			final TypedefNameSpecifierNode typedefNameSpecifierNode = CFactory.createTypedefNameSpecifierNode(
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
		final DeclaratorNode declaratorNode = CFactory.createDeclaratorNode();
		simpleDeclarationNode.addChildAndForceParent(declaratorNode);

		final NameNode nameNode = CFactory.createNameNode(simpleVariable.getName());
		declaratorNode.addChildAndForceParent(nameNode);
	}
}
