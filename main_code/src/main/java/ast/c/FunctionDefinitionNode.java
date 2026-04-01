package ast.c;

/**
 * Name:        FunctionDefinitionNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTFunctionDefinition" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class FunctionDefinitionNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Function definition has ";
	}
}
