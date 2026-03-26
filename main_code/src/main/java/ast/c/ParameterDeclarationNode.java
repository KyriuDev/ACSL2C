package ast.c;

/**
 * Name:        ParameterDeclarationNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTParameterDeclaration" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class ParameterDeclarationNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Parameter declaration has ";
	}
}
