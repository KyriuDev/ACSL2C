package ast.c;

/**
 * Name:        PointerNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTPointer" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class PointerNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Pointer (*) has ";
	}
}
