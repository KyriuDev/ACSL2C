package acsl_to_c;

import ast.AbstractSyntaxNode;
import ast.AbstractSyntaxTree;
import ast.acsl.nodes.*;
import ast.c.*;
import ast.c.nodes.*;
import constants.acsl.others.AcslType;
import constants.acsl_to_c.HelperFunctionName;
import constants.acsl_to_c.HelperFunctionParameter;
import constants.c.*;
import dto.CComment;
import exceptions.UnhandledElementException;
import misc.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Name:        ACSL2ASTTranslator.java
 * Content:     This class is in charge of translating the ACSL contract given as input into several elements, all
 * 				represented as internal ASTs:
 * 				- A list of generic helper methods (in the sense that they do not differ from one program to another)
 * 				  that will be added at the end of the C file
 * 				- A list of specific helper methods that will be added at the end of the C file
 * 				- A list of specific structures that will be added at the beginning of the C file
 * 				- The portion of C code that will effectively replace the method call in the folded version of the
 * 				  program
 * 				This class only translates the methods/structures that are specific to the given contract, and does not
 * 				handle the "generic" ones (e.g., the "valid(void*, int, int)" method in charge of asserting the validity
 * 				of a given pointer in the given range).
 * 				Such "generic" methods/structures are handled separately by the TODO class.
 * 				Note also that this class creates the AST with the information contained in the function contract only.
 * 				It is thus improper for direct usage, as, for instance, the generated portion of C code will make use
 * 				of the parameters identifiers of the contract, instead of of the calling function.
 * 				This mapping is performed in a second phase by TODO class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    01/04/26
 */

public class ACSL2ASTTranslator
{
	private final List<AbstractSyntaxTree> genericHelperMethods;
	private final List<TranslationComponents> translationComponentsList;
	private final AbstractSyntaxTree programTree;

	//Constructors

	public ACSL2ASTTranslator(final AbstractSyntaxTree programTree)
	{
		this.programTree = programTree;
		this.genericHelperMethods = new ArrayList<>();
		this.translationComponentsList = new ArrayList<>();
	}

	//Public methods

	/**
	 * Main method if this class, which is in charge of translating the given ACSL contract into its corresponding
	 * pieces of C code, decomposed in helper methods, helper structures, and function folding.
	 */
	public void translate() throws UnhandledElementException
	{
		//Manage specific elements
		final ArrayList<Pair<AbstractSyntaxTree, CComment>> contractsToTranslate = new ArrayList<>();
		this.getContractsToTranslate(this.programTree.getRoot(), contractsToTranslate);

		for (final Pair<AbstractSyntaxTree, CComment> functionWithContract : contractsToTranslate)
		{
			//Should never raise an exception as the abstract syntax tree should always be there!
			final TranslationComponents contractTranslation = this.translateContract(
				functionWithContract.getFirstElement(),
				functionWithContract.getSecondElement().getAbstractSyntaxTree()
			);
			functionWithContract.getSecondElement().setTranslationComponents(contractTranslation);
			this.translationComponentsList.add(contractTranslation);
		}

		//Manage generic elements
		this.addArrayValidityCheckingGenericHelperFunction();
	}

	public List<AbstractSyntaxTree> getGenericHelperMethods()
	{
		return Collections.unmodifiableList(this.genericHelperMethods);
	}

	public List<TranslationComponents> getTranslationComponentsList()
	{
		return Collections.unmodifiableList(this.translationComponentsList);
	}

	//Private methods

	/**
	 * This method is the principal method of this class.
	 * Its purpose is to translate the ACSL contract (given as an AST) into its corresponding C code, also represented
	 * as an AST.
	 * The translation is based on the following guidelines:
	 *     1) We consider a weak view of the contracts, meaning that either the requirements of the contract are met, or
	 *        we do not do anything, thus translating to an "if" statement.
	 *     2) The requirements to be met by the contract are separated to distinguish each distinct "requires" clause
	 *        and/or behaviour, thus potentially leading to a conjunction of requirements in the "if" condition.
	 *     3) For clarity, requirements are always wrapped by helper functions, independently of their complexity, in
	 *        order to maintain some clarity in the code.
	 *        The helper functions will reuse the name of the behaviour if the requirements are nested in behaviours,
	 *        or a generic prefix otherwise.
	 *     4) Statements involving logical operators such as "A ==> B" (resp. "A <==> B") are currently translated
	 *        as "!A || B" (resp. "(!A || B) && (!B || A)").
	 *     5) Ensures clauses are translated in a "maximise information no loop" fashion, meaning that quantifiers
	 *        appearing in ensures clauses will never be translated as loops, and will always make use of the power
	 *        of the "__VERIFIER_nondet_<type>()" C external function.
	 *     6) Assign clauses are currently ignored.
	 *     7) The management of the ACSL operator "\old(value)" is ensured by a helper structure containing the values
	 *        of the elements targetted by the "\old(value)" operator before the call of the function.
	 *
	 * @param contractTree the contract to translate
	 * @return the components of the resulting translation
	 */
	private TranslationComponents translateContract(final AbstractSyntaxTree functionTree,
													final AbstractSyntaxTree contractTree) throws UnhandledElementException
	{
		//The translation will always start with a CompoundStatementNode
		final TranslationComponents translationComponents = new TranslationComponents(new AbstractSyntaxTree(CFactory.createCompoundStatementNode()));
		int globalRequiresClauseIndex = 1;

		for (final AbstractSyntaxNode child : contractTree.getRoot().getFirstChild().getChildren())
		{
			final AcslBaseNode acslChild = (AcslBaseNode) child;

			if (acslChild.getType() == AcslType.REQUIRES_CLAUSE_LIST)
			{
				//These are global "requires" clauses
				this.manageGlobalRequiresClause(acslChild, translationComponents, globalRequiresClauseIndex, functionTree);
				globalRequiresClauseIndex++;
			}
			else if (acslChild.getType() == AcslType.SIMPLE_CLAUSE_LIST)
			{
				//These are global "assigns"/"ensures" clauses
			}
			else if (acslChild.getType() == AcslType.NAMED_BEHAVIOR_LIST)
			{
				//These are behaviours
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a function contract!",
					((AcslBaseNode) child).getType().getReadableName()
				));
			}
		}

		return translationComponents;
	}

	private void manageGlobalRequiresClause(final AcslBaseNode requiresClause,
											final TranslationComponents translationComponents,
											final int requiresClauseIndex,
											final AbstractSyntaxTree functionTree) throws UnhandledElementException
	{
		this.createRequiresHelperFunction(requiresClause, translationComponents, requiresClauseIndex, functionTree, true);
		this.createRequiresHelperFunction(requiresClause, translationComponents, requiresClauseIndex, functionTree, false);
	}

	/**
	 * This method is in charge of creating the function called to verify that a function meets its requirements.
	 *
	 * @param requiresClause
	 * @param translationComponents
	 * @param requiresClauseIndex
	 */
	private void createRequiresHelperFunction(final AcslBaseNode requiresClause,
											  final TranslationComponents translationComponents,
											  final int requiresClauseIndex,
											  final AbstractSyntaxTree functionTree,
											  final boolean signatureOnly) throws UnhandledElementException
	{
		final AbstractSyntaxTree requiresClauseMetFunction = new AbstractSyntaxTree(
			signatureOnly ? CFactory.createSimpleDeclarationNode() : CFactory.createFunctionDefinitionNode()
		);

		if (!signatureOnly)
		{
			//Add function header comment
			((CBaseNode) requiresClauseMetFunction.getRoot()).addComment(new CComment(
				CCommentType.MULTI_LINE,
				String.format(
					"/*\n\tThis function was generated by the ACSL2C translator. It aims at verifying" +
					"\n\tthat the %s requirement of the contract is met.*/",
					requiresClauseIndex == 1 ? "1st" : requiresClauseIndex == 2 ? "2nd" : requiresClauseIndex == 3 ? "3rd" : requiresClauseIndex + "th"
				)
			));
		}

		//--- Function type: "_Bool"
		final CSimpleDeclSpecifierNode declSpecifierNode = CFactory.createSimpleDeclarationSpecifierNode(
			CType.BOOL
		);
		requiresClauseMetFunction.getRoot().addChildAndForceParent(declSpecifierNode);

		//--- Function declarator: "global_req_<index>_satisfied(<param_1>, <param_2>, ..., <param_n>)"
		final CFunctionDeclaratorNode functionDeclaratorNode = CFactory.createFunctionDeclaratorNode();
		requiresClauseMetFunction.getRoot().addChildAndForceParent(functionDeclaratorNode);

		//------ Name: "global_req_<index>_satisfied"
		final CNameNode nameNode = CFactory.createNameNode(String.format(
			HelperFunctionName.GLOBAL_REQUIREMENT_SATISFIED,
			requiresClauseIndex
		));
		functionDeclaratorNode.addChildAndForceParent(nameNode);

		//------ Parameters: <param_1>, <param_2>, ..., <param_n>
		final ArrayList<String> predefinedParametersNames = new ArrayList<>();
		final ArrayList<String> contractDefinedParametersNames = new ArrayList<>();
		this.retrievePredefinedParametersNames(requiresClause, predefinedParametersNames, contractDefinedParametersNames);
		//We keep only the predefined parameters
		predefinedParametersNames.removeAll(contractDefinedParametersNames);
		//We get their declaration in the C program
		final ArrayList<AbstractSyntaxNode> predefinedParameters = new ArrayList<>();
		this.retrievePredefinedParameters(functionTree.getRoot(), predefinedParameters, predefinedParametersNames);
		//We copy them and add them as child of the current function

		for (final AbstractSyntaxNode predefinedParameter : predefinedParameters)
		{
			functionDeclaratorNode.addChildAndForceParent(predefinedParameter.deepCopy());
		}

		if (!signatureOnly)
		{
			//--- Compound statement: <function_body>
			final CCompoundStatementNode compoundStatementNode = CFactory.createCompoundStatementNode();
			requiresClauseMetFunction.getRoot().addChildAndForceParent(compoundStatementNode);

			if (!this.requiresClauseHasQuantifiers(requiresClause))
			{
				//If the "requires" clause does not contain any quantifiers, it can (normally?) be inlined.
				final CReturnStatementNode returnStatementNode = this.inlineRequiresClause(requiresClause);
				compoundStatementNode.addChildAndForceParent(returnStatementNode);
			}
			else
			{
				//Otherwise, it is a bit more tricky :-)
			}
		}
	}

	private CReturnStatementNode inlineRequiresClause(final AcslBaseNode requiresClause) throws UnhandledElementException
	{
		final CReturnStatementNode returnStatementNode = CFactory.createReturnStatementNode();
		final AbstractSyntaxNode child = requiresClause.getFirstChild();

		if (child instanceof BinaryOperationNode)
		{
			final CBinaryExpressionNode binaryExpressionNode = this.convertAcslBinaryOperationNodeToC((BinaryOperationNode) child);
			returnStatementNode.addChildAndForceParent(binaryExpressionNode);
		}
		else
		{
			throw new UnhandledElementException(String.format(
				"Node type \"%s\" is not yet handled as child of a requires clause!",
				((AcslBaseNode) child).getType().getReadableName()
			));
		}

		return returnStatementNode;
	}

	private CBinaryExpressionNode convertAcslBinaryOperationNodeToC(final BinaryOperationNode binaryOperationNode) throws UnhandledElementException
	{
		final CBinaryExpressionNode binaryExpressionNode;

		if (binaryOperationNode.getChildren().size() == 2)
		{
			//The translation is immediate because the C representation is identical
			binaryExpressionNode = CFactory.createBinaryExpressionNode(
				binaryOperationNode.getOperator()
			);

			final CBaseNode firstChild = this.manageBinaryExpressionChild(binaryOperationNode.getChildren().get(0));
			final CBaseNode secondChild = this.manageBinaryExpressionChild(binaryOperationNode.getChildren().get(1));
			binaryExpressionNode.addChildAndForceParent(firstChild);
			binaryExpressionNode.addChildAndForceParent(secondChild);
		}
		else
		{
			/*
				In ACSL, binary operators are associative, which is not the case in C, thus we split the ACSL operators
				associations into conjunction of binary operations with two operands only.
				For instance, the ACSL binary operation "0 <= x <= y <= 100" will be translated to
				"0 <= x && x <= y && y <= 100" in C.
				To do so, we create a binary expression of the given operator for each couple of consecutive children.
			 */
			final ArrayList<CBinaryExpressionNode> binaryExpressionSubparts = new ArrayList<>();

			for (int i = 0; i < binaryOperationNode.getChildren().size() - 1; i++)
			{
				final AcslBaseNode firstChild = (AcslBaseNode) binaryOperationNode.getChildren().get(i);
				final AcslBaseNode secondChild = (AcslBaseNode) binaryOperationNode.getChildren().get(i + 1);

				final CBinaryExpressionNode currentBinaryExpression = CFactory.createBinaryExpressionNode(
					binaryOperationNode.getOperator()
				);
				binaryExpressionSubparts.add(currentBinaryExpression);

				final CBaseNode cFirstChild = this.manageBinaryExpressionChild(firstChild);
				final CBaseNode cSecondChild = this.manageBinaryExpressionChild(secondChild);
				currentBinaryExpression.addChildAndForceParent(cFirstChild);
				currentBinaryExpression.addChildAndForceParent(cSecondChild);
			}

			/*
				We now have all the subparts of the original expression, that are, for expression "0 <= x <= y <= 100":
					- "0 <= x"
					- "x <= y"
					- "y <= 100"
				We just have to connect them with logical ands to get "0 <= x && x <= y && y <= 100".
			 */
			CBinaryExpressionNode rightNode = binaryExpressionSubparts.remove(binaryExpressionSubparts.size() - 1);

			for (int i = binaryExpressionSubparts.size() - 1; i >= 0; i--)
			{
				final AbstractSyntaxNode leftNode = binaryExpressionSubparts.get(i);
				final CBinaryExpressionNode logicalAndNode = CFactory.createBinaryExpressionNode(
					CBinaryOperator.LOGICAL_AND
				);
				logicalAndNode.addChildAndForceParent(leftNode);
				logicalAndNode.addChildAndForceParent(rightNode);
				rightNode = logicalAndNode;
			}

			if (rightNode == null)
			{
				throw new RuntimeException("Right node should never be null!");
			}

			binaryExpressionNode = rightNode;
		}

		return binaryExpressionNode;
	}

	private CBaseNode manageBinaryExpressionChild(final AbstractSyntaxNode node) throws UnhandledElementException
	{
		final CBaseNode childNode;

		if (node instanceof IdentifierNode)
		{
			childNode = this.convertAcslIdentifierNodeToC((IdentifierNode) node);
		}
		else if (node instanceof BinaryOperationNode)
		{
			childNode = this.convertAcslBinaryOperationNodeToC((BinaryOperationNode) node);
		}
		else if (node instanceof ValidNode)
		{
			childNode = this.convertAcslValidNodeToC((ValidNode) node);
		}
		else
		{
			throw new UnhandledElementException(String.format(
				"Node type \"%s\" is not yet handled as child of a binary expression!",
				((AcslBaseNode) node).getType().getReadableName()
			));
		}

		return childNode;
	}

	private CBaseNode convertAcslValidNodeToC(final ValidNode validNode) throws UnhandledElementException
	{
		//We convert the ACSL ValidNode to a call to the C function "array_is_valid(void*, int, int)"
		final CFunctionCallExpressionNode functionCallExpressionNode = CFactory.createFunctionCallExpressionNode();
		//Add the function name
		final CIdExpressionNode functionNameIdExpressionNode = CFactory.createIdExpressionNode();
		functionCallExpressionNode.addChildAndForceParent(functionNameIdExpressionNode);
		final CNameNode functionName = CFactory.createNameNode(HelperFunctionName.CHECK_ARRAY_VALIDITY);
		functionNameIdExpressionNode.addChildAndForceParent(functionName);

		/*
			For now, we only support "\valid(value)" calls with value of types:
				- Identifier
				- Binary operation "+" composed of identifier and range
		 */
		final AbstractSyntaxNode child = validNode.getFirstChild();

		if (child instanceof BinaryOperationNode)
		{
			if (((BinaryOperationNode) child).getOperator() != CBinaryOperator.PLUS)
			{
				throw new UnhandledElementException(String.format(
					"ValidNode binary expression child should contain an operator \"+\", got \"%s\" instead.",
					((BinaryOperationNode) child).getOperator().getOperator()
				));
			}

			final AbstractSyntaxNode firstChild = child.getChildren().get(0);
			final AbstractSyntaxNode secondChild = child.getChildren().get(1);

			if (!(firstChild instanceof IdentifierNode)
				|| !(secondChild instanceof RangeNode))
			{
				throw new UnhandledElementException(
					"ValidNode binary expression child should have an IdentifierNode and a RangeNode as children."
				);
			}

			//Add the arguments "<array_name>", <range_lower_bound>, <range_upper_bound>
			//<array_name>
			final CIdExpressionNode arrayNameIdExpressionNode = this.convertAcslIdentifierNodeToC((IdentifierNode) firstChild);
			functionCallExpressionNode.addChildAndForceParent(arrayNameIdExpressionNode);

			//<range_lower_bound>
			final AbstractSyntaxNode lowerBound = this.manageBoundChild(secondChild.getChildren().get(0).getChildren().get(0));
			functionCallExpressionNode.addChildAndForceParent(lowerBound);

			//<range_upper_bound>
			final AbstractSyntaxNode upperBound = this.manageBoundChild(secondChild.getChildren().get(1).getChildren().get(0));
			functionCallExpressionNode.addChildAndForceParent(upperBound);
		}
		else if (child instanceof IdentifierNode)
		{
			//Add the arguments "<array_name>", 0, and 0
			//<array_name>
			final CIdExpressionNode arrayNameIdExpressionNode = this.convertAcslIdentifierNodeToC((IdentifierNode) child);
			functionCallExpressionNode.addChildAndForceParent(arrayNameIdExpressionNode);

			//0
			final CLiteralExpressionNode startIndexNode = CFactory.createLiteralExpressionNode("0");
			functionCallExpressionNode.addChildAndForceParent(startIndexNode);

			//0
			final CLiteralExpressionNode endIndexNode = CFactory.createLiteralExpressionNode("0");
			functionCallExpressionNode.addChildAndForceParent(endIndexNode);
		}
		else
		{
			throw new UnhandledElementException(String.format(
				"Node type \"%s\" is not yet handled as child of a \"\\valid()\" call!",
				((AcslBaseNode) child).getType().getReadableName()
			));
		}

		return functionCallExpressionNode;
	}

	private CBaseNode manageBoundChild(final AbstractSyntaxNode node) throws UnhandledElementException
	{
		final CBaseNode correspondingCNode;

		if (node instanceof LiteralNode)
		{
			correspondingCNode = this.convertAcslLiteralNodeToC((LiteralNode) node);
		}
		else if (node instanceof BinaryOperationNode)
		{
			correspondingCNode = this.convertAcslBinaryOperationNodeToC((BinaryOperationNode) node);
		}
		else
		{
			throw new UnhandledElementException(String.format(
				"Node type \"%s\" is not yet handled as child of a boundary node!",
				((AcslBaseNode) node).getType().getReadableName()
			));
		}

		return correspondingCNode;
	}

	private CLiteralExpressionNode convertAcslLiteralNodeToC(final LiteralNode node)
	{
		return CFactory.createLiteralExpressionNode(node.getContent());
	}

	private CIdExpressionNode convertAcslIdentifierNodeToC(final IdentifierNode identifierNode)
	{
		final CIdExpressionNode idExpressionNode = CFactory.createIdExpressionNode();
		final CNameNode nameNode = CFactory.createNameNode(identifierNode.getContent());
		idExpressionNode.addChildAndForceParent(nameNode);

		return idExpressionNode;
	}

	private boolean requiresClauseHasQuantifiers(final AbstractSyntaxNode currentNode)
	{
		if (currentNode instanceof QuantifierNode)
		{
			return true;
		}

		for (final AbstractSyntaxNode child : currentNode.getChildren())
		{
			final boolean hasQuantifiers = this.requiresClauseHasQuantifiers(child);

			if (hasQuantifiers)
			{
				return true;
			}
		}

		return false;
	}

	private void retrievePredefinedParameters(final AbstractSyntaxNode currentNode,
											  final ArrayList<AbstractSyntaxNode> predefinedParameters,
											  final ArrayList<String> predefinedParametersNames)
	{
		if (currentNode instanceof CParameterDeclarationNode)
		{
			if (((CParameterDeclarationNode) currentNode).hasNameIn(predefinedParametersNames))
			{
				predefinedParameters.add(currentNode);
			}
		}
		else
		{
			for (final AbstractSyntaxNode child : currentNode.getChildren())
			{
				this.retrievePredefinedParameters(child, predefinedParameters, predefinedParametersNames);
			}
		}
	}

	/**
	 * This method performs a depth-first search of the given tree (actually, a node, to ease the recursion) in order
	 * to collect all the identifiers referring to parameters that were not defined in the contract itself, but given
	 * to it as parameters of the function to which the contract is attached.
	 * TODO: one interesting thing to do (probably not here) would be to ensure that predefined parameters and contract
	 * TODO: parameters are not mixed up.
	 *
	 * @param currentNode the node to check
	 * @param predefinedParameters the list of predefined parameters, filled during the traversal
	 */
	private void retrievePredefinedParametersNames(final AbstractSyntaxNode currentNode,
	                                               final List<String> predefinedParameters,
	                                               final List<String> contractDefinedParameter)
	{
		if (currentNode instanceof IdentifierNode)
		{
			predefinedParameters.add(((IdentifierNode) currentNode).getContent());
		}
		else if (currentNode instanceof VariableIdentifierNode)
		{
			contractDefinedParameter.add(((VariableIdentifierNode) currentNode).getContent());
		}

		for (final AbstractSyntaxNode child : currentNode.getChildren())
		{
			this.retrievePredefinedParametersNames(child, predefinedParameters, contractDefinedParameter);
		}
	}

	//Generic elements

	private void addTestHelperStructure()
	{
		final AbstractSyntaxTree abstractSyntaxTree = new AbstractSyntaxTree(CFactory.createSimpleDeclarationNode());
		final CCompositeTypeSpecifierNode helperStructure = CFactory.createCompositeTypeSpecifierNode(CStorageClass.TYPEDEF);
		final String functionName = "my_function_name";

		//Set the name of the structure
		helperStructure.setName(functionName + "_old_values");

		//Add its components
		helperStructure.addComponent(new SimpleVariable(CType.UNSPECIFIED.setType("my_type_1"), "my_var_name_1"));
		helperStructure.addComponent(new SimpleVariable(CType.INT, "my_var_name_2"));

		//Connect the structure to the root node
		abstractSyntaxTree.getRoot().addChildAndForceParent(helperStructure);

		//Comment the structure
		((CBaseNode) abstractSyntaxTree.getRoot()).addComment(new CComment(
			CCommentType.MULTI_LINE,
			String.format(
				"/*\n\tThis structure was generated by the ACSL2C translator. It aims at storing" +
				"\n\tthe old values of the parameters given to the \"%s\" function," +
				"\n\tin order to mimic the behaviour of the \"\\old(value)\" ACSL operator.\n*/",
				functionName
			)
		));

		final TranslationComponents translationComponents = new TranslationComponents(
			null,
			new ArrayList<>(),
			List.of(abstractSyntaxTree)
		);

		this.translationComponentsList.add(translationComponents);
	}

	private void addArrayValidityCheckingGenericHelperFunction()
	{
		this.genericHelperMethods.add(this.createArrayValidityCheckingFunction(true));
		this.genericHelperMethods.add(this.createArrayValidityCheckingFunction(false));
	}

	private AbstractSyntaxTree createArrayValidityCheckingFunction(final boolean signatureOnly)
	{
		final AbstractSyntaxTree arrayValidityCheckingFunctionTree = new AbstractSyntaxTree(
			signatureOnly ? CFactory.createSimpleDeclarationNode() : CFactory.createFunctionDefinitionNode()
		);

		if (!signatureOnly)
		{
			//Add function header comment
			((CBaseNode) arrayValidityCheckingFunctionTree.getRoot()).addComment(new CComment(
				CCommentType.MULTI_LINE,
				"/*\n\tThis function was generated by the ACSL2C translator. It aims at verifying" +
						"\n\tthe memory validity of the array given as input, in the given bounds, in" +
						"\n\torder to mimic the behaviour of the \"\\valid(value)\" ACSL operator.\n*/"
			));
		}

		//--- Add function type: "int"
		final CSimpleDeclSpecifierNode simpleDeclarationSpecifier = CFactory.createSimpleDeclarationSpecifierNode(
			CStorageClass.UNSPECIFIED,
			CType.BOOL
		);
		arrayValidityCheckingFunctionTree.getRoot().addChildAndForceParent(simpleDeclarationSpecifier);

		//--- Add function name and parameters
		final CFunctionDeclaratorNode CFunctionDeclaratorNode = CFactory.createFunctionDeclaratorNode();
		arrayValidityCheckingFunctionTree.getRoot().addChildAndForceParent(CFunctionDeclaratorNode);

		//------ Function name: "is_array_valid"
		final CNameNode nameNode = CFactory.createNameNode(HelperFunctionName.CHECK_ARRAY_VALIDITY);
		CFunctionDeclaratorNode.addChildAndForceParent(nameNode);

		//------ Function parameters
		//--------- Parameter 1: "void *array"
		final CParameterDeclarationNode parameterDeclarationNode1 = CFactory.createParameterDeclarationNode();
		CFunctionDeclaratorNode.addChildAndForceParent(parameterDeclarationNode1);

		//------------ Parameter type: "void"
		final CSimpleDeclSpecifierNode parameter1DeclarationSpecifier = CFactory.createSimpleDeclarationSpecifierNode(
			CStorageClass.UNSPECIFIED,
			CType.VOID
		);
		parameterDeclarationNode1.addChildAndForceParent(parameter1DeclarationSpecifier);

		//------------ Parameter declaration: "*array"
		final CDeclaratorNode parameter1Declarator = CFactory.createDeclaratorNode();
		parameterDeclarationNode1.addChildAndForceParent(parameter1Declarator);

		//--------------- Parameter declaration: "*"
		final CPointerNode parameter1PointerNode1 = CFactory.createPointerNode();
		parameter1Declarator.addChildAndForceParent(parameter1PointerNode1);

		//--------------- Parameter declaration: "array"
		final CNameNode parameter1NameNode = CFactory.createNameNode(
			HelperFunctionParameter.CHECK_ARRAY_VALIDITY_FUNCTION_ARRAY
		);
		parameter1Declarator.addChildAndForceParent(parameter1NameNode);

		//--------- Parameter 2: "int start_index"
		final CParameterDeclarationNode parameterDeclarationNode2 = CFactory.createParameterDeclarationNode();
		CFunctionDeclaratorNode.addChildAndForceParent(parameterDeclarationNode2);

		//------------ Parameter type: "int"
		final CSimpleDeclSpecifierNode parameter2DeclarationSpecifier = CFactory.createSimpleDeclarationSpecifierNode(
			CStorageClass.UNSPECIFIED,
			CType.INT
		);
		parameterDeclarationNode2.addChildAndForceParent(parameter2DeclarationSpecifier);

		//------------ Parameter declaration: "start_index"
		final CDeclaratorNode parameter2Declarator = CFactory.createDeclaratorNode();
		parameterDeclarationNode2.addChildAndForceParent(parameter2Declarator);

		final CNameNode parameter2NameNode = CFactory.createNameNode(
			HelperFunctionParameter.CHECK_ARRAY_VALIDITY_FUNCTION_START_INDEX
		);
		parameter2Declarator.addChildAndForceParent(parameter2NameNode);

		//--------- Parameter 3: "int end_index"
		final CParameterDeclarationNode parameterDeclarationNode3 = CFactory.createParameterDeclarationNode();
		CFunctionDeclaratorNode.addChildAndForceParent(parameterDeclarationNode3);

		//------------ Parameter type: "int"
		final CSimpleDeclSpecifierNode parameter3DeclarationSpecifier = CFactory.createSimpleDeclarationSpecifierNode(
			CStorageClass.UNSPECIFIED,
			CType.INT
		);
		parameterDeclarationNode3.addChildAndForceParent(parameter3DeclarationSpecifier);

		//------------ Parameter declaration: "end_index"
		final CDeclaratorNode parameter3Declarator = CFactory.createDeclaratorNode();
		parameterDeclarationNode3.addChildAndForceParent(parameter3Declarator);

		final CNameNode parameter3NameNode = CFactory.createNameNode(
			HelperFunctionParameter.CHECK_ARRAY_VALIDITY_FUNCTION_END_INDEX
		);
		parameter3Declarator.addChildAndForceParent(parameter3NameNode);

		if (!signatureOnly)
		{
			//--- Add function body: "return 1";
			final CCompoundStatementNode body = CFactory.createCompoundStatementNode();
			arrayValidityCheckingFunctionTree.getRoot().addChildAndForceParent(body);

			//------ Add function body: "return";
			final CReturnStatementNode returnStatementNode = CFactory.createReturnStatementNode();
			body.addChildAndForceParent(returnStatementNode);

			//------ Add function body: "1";
			final CLiteralExpressionNode literalExpressionNode = CFactory.createLiteralExpressionNode(
					CBooleanValue.TRUE.getIntValueString()
			);
			returnStatementNode.addChildAndForceParent(literalExpressionNode);

			//Add "//TODO" comment
			returnStatementNode.addComment(new CComment(
					CCommentType.SINGLE_LINE,
					CCommentNature.REGULAR,
					"//TODO Find how to verify the memory validity of an array"
			));
		}

		return arrayValidityCheckingFunctionTree;
	}

	private void getContractsToTranslate(final AbstractSyntaxNode currentNode,
										 final ArrayList<Pair<AbstractSyntaxTree, CComment>> contracts)
	{
		if (currentNode instanceof CFunctionDefinitionNode)
		{
			for (final CComment comment : ((CBaseNode) currentNode).getPrecedingComments())
			{
				if (comment.isAcslComment())
				{
					contracts.add(new Pair<>(new AbstractSyntaxTree(currentNode), comment));
				}
			}
		}

		for (final AbstractSyntaxNode child : currentNode.getChildren())
		{
			this.getContractsToTranslate(child, contracts);
		}
	}

	//Sub-classes

	public static class SimpleVariable {
		private final CType type;
		private final String name;

		SimpleVariable(final CType type,
					   final String name)
		{
			this.type = type;
			this.name = name;
		}

		public CType getType()
		{
			return this.type;
		}

		public String getName()
		{
			return this.name;
		}
	}

	public static class TranslationComponents {
		private final List<AbstractSyntaxTree> specificHelperMethods;
		private final List<AbstractSyntaxTree> specificHelperStructures;
		private final AbstractSyntaxTree functionFolding;

		//Constructors

		public TranslationComponents(final AbstractSyntaxTree functionFolding)
		{
			this(functionFolding, new ArrayList<>(), new ArrayList<>());
		}

		public TranslationComponents(final AbstractSyntaxTree functionFolding,
									 final List<AbstractSyntaxTree> specificHelperMethods,
									 final List<AbstractSyntaxTree> specificHelperStructures)
		{
			this.specificHelperMethods = specificHelperMethods;
			this.specificHelperStructures = specificHelperStructures;
			this.functionFolding = functionFolding;
		}

		//Public methods

		public List<AbstractSyntaxTree> getSpecificHelperMethods()
		{
			return Collections.unmodifiableList(this.specificHelperMethods);
		}

		public List<AbstractSyntaxTree> getSpecificHelperStructures()
		{
			return Collections.unmodifiableList(this.specificHelperStructures);
		}

		public AbstractSyntaxTree getFunctionFolding()
		{
			return this.functionFolding;
		}
	}
}
