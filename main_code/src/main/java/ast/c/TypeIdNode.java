package ast.c;

/**
 * Name:        TypeIdNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTTypeId" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class TypeIdNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Type id has ";
	}
}
