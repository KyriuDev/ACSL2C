package ast;

import constants.acsl.ast.*;
import constants.acsl.others.*;

/**
 * Name:        AstFactory.java
 * Content:	    This class contains utility methods to create AcslBaseNodes without the burden of correctly
 * 				using the AcslBaseNode constructors manually, by wrapping them into nicer functions.
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

	public static AcslBaseNode createAssumesClauseListNode()
	{
		return new AcslBaseNode(AcslType.ASSUMES_CLAUSE_LIST);
	}

	public static AcslBaseNode createBinderNode()
	{
		return new AcslBaseNode(AcslType.BINDER);
	}

	public static AcslBaseNode createBindersNode()
	{
		return new AcslBaseNode(AcslType.BINDERS);
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

	public static AcslBaseNode createFunctionContractNode()
	{
		return new AcslBaseNode(AcslType.FUNCTION_CONTRACT);
	}

	public static AcslBaseNode createIndexNode()
	{
		return new AcslBaseNode(AcslType.INDEX);
	}

	public static AcslBaseNode createLocationNode()
	{
		return new AcslBaseNode(AcslType.LOCATION);
	}

	public static AcslBaseNode createLocationsNode()
	{
		return new AcslBaseNode(AcslType.LOCATIONS);
	}

	public static AcslBaseNode createMemoryAllocationSetNode()
	{
		return AstFactory.createMemoryAllocationSetNode(null);
	}

	public static AcslBaseNode createMemoryAllocationSetNode(final String content)
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

	public static AcslBaseNode createNamedBehaviorListNode()
	{
		return new AcslBaseNode(AcslType.NAMED_BEHAVIOR_LIST);
	}

	public static OperatorNode createOperatorNode()
	{
		return AstFactory.createOperatorNode(null);
	}

	public static OperatorNode createOperatorNode(final String name)
	{
		return new OperatorNode(name);
	}

	public static AcslBaseNode createPredicateOrTermNode()
	{
		return new AcslBaseNode(AcslType.PREDICATE_OR_TERM);
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
		else if (kind.equals(AcslPredicateOrTermKind.OLD.getName()))
		{
			return new OldNode();
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

	public static AcslBaseNode createRequiresClauseListNode()
	{
		return new AcslBaseNode(AcslType.REQUIRES_CLAUSE_LIST);
	}

	public static AcslBaseNode createRootNode()
	{
		return new AcslBaseNode(AcslType.ROOT);
	}

	public static AcslBaseNode createSimpleClauseListNode()
	{
		return new AcslBaseNode(AcslType.SIMPLE_CLAUSE_LIST);
	}

	public static TypeSpecifierNode createTypeSpecifierNode(final String kind)
	{
		return new TypeSpecifierNode(AcslTypeSpecifier.getTypeSpecifierFromKind(kind));
	}

	public static AcslBaseNode createTypesNode()
	{
		return new AcslBaseNode(AcslType.TYPES);
	}

	public static VariableIdentifierNode createVariableIdentifierNode(final String kind,
																	  final String content)
	{
		if (kind.equals(AcslVariableIdentifierKind.ARRAY.getName()))
		{
			return new ArrayNode(content);
		}
		else if (kind.equals(AcslVariableIdentifierKind.BRACKETS.getName()))
		{
			return new BracketsNode(content);
		}
		else if (kind.equals(AcslVariableIdentifierKind.IDENTIFIER.getName()))
		{
			return new VariableIdentifierNode(content);
		}
		else if (kind.equals(AcslVariableIdentifierKind.POINTER.getName()))
		{
			return new PointerNode(content);
		}
		else
		{
			throw new RuntimeException(String.format(
				"The kind \"%s\" does not correspond to any variable identifier kind.",
				kind
			));
		}
	}
}
