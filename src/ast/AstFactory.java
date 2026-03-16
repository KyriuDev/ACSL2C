package ast;

import constants.acsl.ast.BinaryOperationNode;
import constants.acsl.ast.PredicateOrTermNode;
import constants.acsl.ast.RequiresClauseNode;
import constants.acsl.others.AcslClauseKind;
import constants.acsl.others.AcslPredicateOrTermKind;
import constants.acsl.others.AcslType;

/**
 * Name:        AstFactory.java
 * Content:	    This class contains utility methods to create AbstractSyntaxNode without the burden of correctly
 * 				using the AbstractSyntaxNode constructors manually, by wrapping them into nicer functions.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    16/03/26
 */

public class AstFactory
{
	private AstFactory()
	{

	}

	public static AbstractSyntaxNode createFunctionContractNode()
	{
		return new AbstractSyntaxNode(AcslType.FUNCTION_CONTRACT);
	}

	public static AbstractSyntaxNode createPredicateOrTermNode()
	{
		return new AbstractSyntaxNode(AcslType.PREDICATE_OR_TERM);
	}

	public static PredicateOrTermNode createPredicateOrTermNode(final String kind)
	{
		if (kind.equals(AcslPredicateOrTermKind.ARRAY_ACCESS.getName()))
		{

		}
		else if (kind.equals(AcslPredicateOrTermKind.BINARY_OPERATOR.getName()))
		{
			return new BinaryOperationNode();
		}
		else if (kind.equals(AcslPredicateOrTermKind.IDENTIFIER.getName()))
		{

		}
		else if (kind.equals(AcslPredicateOrTermKind.LITERAL.getName()))
		{

		}
		else if (kind.equals(AcslPredicateOrTermKind.UNARY_OPERATOR.getName()))
		{

		}
		else
		{
			throw new RuntimeException(String.format(
				"The kind \"%s\" does not correspond to any predicate or term kind.",
				kind
			));
		}
	}

	public static RequiresClauseNode createRequiresClauseNode()
	{
		return AstFactory.createRequiresClauseNode(null);
	}

	public static RequiresClauseNode createRequiresClauseNode(final AcslClauseKind clauseKind)
	{
		return new RequiresClauseNode(clauseKind);
	}

	public static AbstractSyntaxNode createRequiresClauseListNode()
	{
		return new AbstractSyntaxNode(AcslType.REQUIRES_CLAUSE_LIST);
	}

	public static AbstractSyntaxNode createRootNode()
	{
		return new AbstractSyntaxNode(AcslType.ROOT);
	}
}
