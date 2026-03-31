package ast.c;

/**
 * Name:        EqualsInitializerNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTEqualsInitializer" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class EqualsInitializerNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Equals initializer (=) has ";
	}
}
