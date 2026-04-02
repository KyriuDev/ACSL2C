package ast.acsl.nodes;

import constants.acsl.others.AcslType;

/**
 * Name:        BoundaryNode.java
 * Content:	    This class defines a BoundaryNode representing the (lower or upper) boundary of a RangeNode.
 * 				For instance, the expression "(0 .. n-1)" will be represented as a RangeNode having two BoundaryNodes
 * 				children, themselves respectively having children representing "0" and "n-1".
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    17/03/26
 */

public class BoundaryNode extends AcslBaseNode
{
	//Constructors

	public BoundaryNode(final AcslType type)
	{
		super(type);

		if (type != AcslType.LOWER_BOUND
			&& type != AcslType.UPPER_BOUND)
		{
			throw new RuntimeException();
		}
	}
}
