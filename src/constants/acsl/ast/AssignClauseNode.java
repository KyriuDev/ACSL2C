package constants.acsl.ast;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslType;

public class AssignClauseNode extends AbstractSyntaxNode
{
	public AssignClauseNode()
	{
		super(AcslType.ASSIGN_CLAUSE);
	}
}
