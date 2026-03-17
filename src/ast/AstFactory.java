package ast;

import constants.acsl.ast.*;
import constants.acsl.others.AcslClauseKind;
import constants.acsl.others.AcslMemoryAllocationSet;
import constants.acsl.others.AcslPredicateOrTermKind;
import constants.acsl.others.AcslType;

/**
 * Name:        AstFactory.java
 * Content:	    This class contains utility methods to create AbstractSyntaxNodes without the burden of correctly
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

	public static AssignClauseNode createAssignClauseNode()
	{
		return new AssignClauseNode();
	}

	public static AbstractSyntaxNode createAssumesClauseListNode()
	{
		return new AbstractSyntaxNode(AcslType.ASSUMES_CLAUSE_LIST);
	}

	public static AbstractSyntaxNode createBinderNode()
	{
		return new AbstractSyntaxNode(AcslType.BINDER);
	}

	public static AbstractSyntaxNode createBindersNode()
	{
		return new AbstractSyntaxNode(AcslType.BINDERS);
	}

	public static BoundaryNode createBoundaryNode(final AcslType type)
	{
		return new BoundaryNode(type);
	}

	public static BehaviorNode createBehaviorNode()
	{
		return AstFactory.createBehaviorNode(null);
	}

	public static BehaviorNode createBehaviorNode(final String id)
	{
		return new BehaviorNode(id);
	}

	public static EnsuresClauseNode createEnsuresClauseNode()
	{
		return AstFactory.createEnsuresClauseNode(null);
	}

	public static EnsuresClauseNode createEnsuresClauseNode(final AcslClauseKind clauseKind)
	{
		return new EnsuresClauseNode(clauseKind);
	}

	public static AbstractSyntaxNode createFunctionContractNode()
	{
		return new AbstractSyntaxNode(AcslType.FUNCTION_CONTRACT);
	}

	public static AbstractSyntaxNode createIndexNode()
	{
		return new AbstractSyntaxNode(AcslType.INDEX);
	}

	public static AbstractSyntaxNode createLocationNode()
	{
		return new AbstractSyntaxNode(AcslType.LOCATION);
	}

	public static AbstractSyntaxNode createLocationsNode()
	{
		return new AbstractSyntaxNode(AcslType.LOCATIONS);
	}

	public static AbstractSyntaxNode createMemoryAllocationSetNode()
	{
		return AstFactory.createMemoryAllocationSetNode(null);
	}

	public static AbstractSyntaxNode createMemoryAllocationSetNode(final String content)
	{
		return new MemoryAllocationSetNode(
			content != null
					&& content.trim().equals(AcslMemoryAllocationSet.EMPTY.getKeyword())
		);
	}

	public static NameNode createNameNode()
	{
		return AstFactory.createNameNode(null);
	}

	public static NameNode createNameNode(final String name)
	{
		return new NameNode(name);
	}

	public static AbstractSyntaxNode createNamedBehaviorListNode()
	{
		return new AbstractSyntaxNode(AcslType.NAMED_BEHAVIOR_LIST);
	}

	public static OperatorNode createOperatorNode()
	{
		return AstFactory.createOperatorNode(null);
	}

	public static OperatorNode createOperatorNode(final String name)
	{
		return new OperatorNode(name);
	}

	public static AbstractSyntaxNode createPredicateOrTermNode()
	{
		return new AbstractSyntaxNode(AcslType.PREDICATE_OR_TERM);
	}

	public static PredicateOrTermNode createPredicateOrTermNode(final String kind,
																final String content)
	{
		if (kind.equals(AcslPredicateOrTermKind.ARRAY_ACCESS.getName()))
		{
			return new ArrayAccessNode();
		}
		else if (kind.equals(AcslPredicateOrTermKind.BINARY_OPERATOR.getName()))
		{
			return new BinaryOperationNode(content);
		}
		else if (kind.equals(AcslPredicateOrTermKind.EXISTS.getName()))
		{
			return new QuantifierNode(AcslPredicateOrTermKind.EXISTS, content);
		}
		else if (kind.equals(AcslPredicateOrTermKind.FOR_ALL.getName()))
		{
			return new QuantifierNode(AcslPredicateOrTermKind.FOR_ALL, content);
		}
		else if (kind.equals(AcslPredicateOrTermKind.IDENTIFIER.getName()))
		{
			return new IdentifierNode(content);
		}
		else if (kind.equals(AcslPredicateOrTermKind.LITERAL.getName()))
		{
			return new LiteralNode(content);
		}
		else if (kind.equals(AcslPredicateOrTermKind.RANGE.getName()))
		{
			return new RangeNode();
		}
		else if (kind.equals(AcslPredicateOrTermKind.RESULT.getName()))
		{
			return new ResultNode();
		}
		else if (kind.equals(AcslPredicateOrTermKind.SYNTACTIC_NAMING.getName()))
		{
			return new SyntacticNamingNode();
		}
		else if (kind.equals(AcslPredicateOrTermKind.UNARY_OPERATOR.getName()))
		{
			return new UnaryOperationNode(content);
		}
		else if (kind.equals(AcslPredicateOrTermKind.VALID.getName()))
		{
			return new ValidNode();
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

	public static AbstractSyntaxNode createSimpleClauseListNode()
	{
		return new AbstractSyntaxNode(AcslType.SIMPLE_CLAUSE_LIST);
	}

	public static AbstractSyntaxNode createTypesNode()
	{
		return new AbstractSyntaxNode(AcslType.TYPES);
	}
}
