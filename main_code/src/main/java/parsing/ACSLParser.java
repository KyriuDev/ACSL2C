package parsing;

import ast.AbstractSyntaxNode;
import ast.AbstractSyntaxTree;
import ast.acsl.AcslFactory;
import ast.acsl.*;
import constants.ReturnCode;
import constants.acsl.others.AcslClauseKind;
import constants.acsl.others.AcslType;
import constants.acsl.xml.AcslXmlAttribute;
import constants.c.CCommentNature;
import constants.c.CCommentType;
import dto.CComment;
import exceptions.UnhandledElementException;
import misc.CommandManager;
import misc.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Name:        ACSLParser.java
 * Content:     This class is in charge of handling the parsing of the ACSL comments belonging to the original C
 *              program given as input.
 *              For each ACSL comment found in the program, it sequentially performs the following actions:
 *              	- A system call to SYNTAX in order to effectively parse the comment and obtain the corresponding
 *                    AST;
 *                  - The parsing of the resulting AST provided by SYNTAX in XML format.
 *              In the end, the user obtains an AST that can be manipulated by using the defined Java methods.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    10/03/26
 */

public class ACSLParser
{
	private static final String ABSTRACT_SYNTAX_TREE_FILE_NAME = "comment.xml";
	//TODO Clean this at some point!
	private static final String ACSL_BINARY_FILE_PATH = "/home/quentin/Documents/Post-doc/Frama-C/acsl";
	private static final String ACSL_COMMENT_FILE_NAME = "comment.acsl";
	private final List<CComment> acslComments;

	//Constructors

	public ACSLParser(final List<CComment> comments)
	{
		this.acslComments = comments;
	}

	/**
	 * This constructor is used for testing and debugging purposes only.
	 *
	 * @param commentFile the file containing the comment to parse
	 */
	public ACSLParser(final File commentFile)
	{
		final Scanner scanner;
		try
		{
			scanner = new Scanner(commentFile);
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}

		final StringBuilder builder = new StringBuilder();

		while (scanner.hasNextLine())
		{
			final String nextLine = scanner.nextLine();
			builder.append(" "); //Useful to avoid introducing parsing confusions in some rare cases
			builder.append(nextLine);
			//System.out.printf("Next line: %s%n", nextLine);
		}

		scanner.close();

		final CComment comment = new CComment(
			CCommentType.MULTI_LINE,
			CCommentNature.ACSL,
			-1,
			-1,
			builder.toString()
		);

		this.acslComments = new ArrayList<>();
		this.acslComments.add(comment);
	}

	//Public methods

	/**
	 * This method parses the ACSL comments given as input, creates the corresponding ASTs, and stores them in their
	 * corresponding CComment "abstractSyntaxTree" field with the method "CComment.setAbstractSyntaxTree(AST)".
	 * For now, it performs as follows for each comment of the asclComments list:
	 * - It writes the comment to a temporary file;
	 * - It calls the SYNTAX binary "acsl" on this temporary file, which outputs its result in another temporary file;
	 * - The resulting temporary file is an AST representation of the comment, which is parsed and stored in the
	 *   comment being handled.
	 */
	public void parse() throws UnhandledElementException
	{
		for (final CComment comment : this.acslComments)
		{
			final String tempDirPath = this.writeCommentToFile(comment);
			this.callSyntax(tempDirPath);
			final AbstractSyntaxTree commentTree = this.parseAcslComment(tempDirPath);
			comment.setAbstractSyntaxTree(commentTree);
			System.out.println("\nTree before collapse:");
			System.out.println(commentTree);
			System.out.println("\nTree after collapse:");
			commentTree.collapse();
			System.out.println(commentTree);
		}
	}

	//Private methods

	/**
	 * This method is in charge of writing the ACSL comment given as input to a temporary file.
	 * The temporary file is located in a temporary directory created by the JVM.
	 *
	 * @param comment the comment to write to the file.
	 */
	private String writeCommentToFile(final CComment comment)
	{
		final String tempDirPath = Utils.getTempDir();

		if (tempDirPath == null)
		{
			throw new RuntimeException("The temporary directory could not be created.");
		}

		final File file = new File(tempDirPath + File.separator + ACSL_COMMENT_FILE_NAME);
		final String commentContent = comment.getCleanContent();
		final PrintWriter printWriter;
		try
		{
			printWriter = new PrintWriter(file);
		}
		catch (final FileNotFoundException e)
		{
			throw new RuntimeException(String.format("File \"%s\" does not exist!", file.getAbsolutePath()));
		}
		printWriter.print(commentContent);
		printWriter.close();

		return tempDirPath;
	}

	/**
	 * This method is in charge of performing the call to SYNTAX, which creates a temporary XML file in the
	 * temporary directory given as parameter.
	 *
	 * @param tempDirPath the path of the temporary directory.
	 */
	private void callSyntax(final String tempDirPath)
	{
		final String command = ACSL_BINARY_FILE_PATH;
		final String[] commandArgs = {
			tempDirPath + File.separator + ACSL_COMMENT_FILE_NAME
		};

		final CommandManager commandManager = new CommandManager(
			command,
			new File(tempDirPath),
			new File(tempDirPath + File.separator + ABSTRACT_SYNTAX_TREE_FILE_NAME),
			commandArgs
		);

		try
		{
			commandManager.execute();
		}
		catch (IOException | InterruptedException e)
		{
			throw new RuntimeException(String.format("SYNTAX call failed with a Java exception: %s", e));
		}

		if (commandManager.returnValue() != ReturnCode.TERMINATION_OK)
		{
			throw new RuntimeException(String.format(
				"SYNTAX emitted the following warning(s) and/or error(s), quite certainly meaning that the ACSL " +
				"comment is not well-formed: %s", commandManager.stdErr())
			);
		}

		if (!commandManager.stdOut().isEmpty())
		{
			throw new RuntimeException(String.format(
				"SYNTAX unexpectedly wrote to the standard output: %s", commandManager.stdOut())
			);
		}
	}

	/**
	 * This method is in charge of parsing the ACSL comment given the path to the temporary directory containing the
	 * XML abstract syntax tree representation of the comment.
	 * It starts by initialising the parser and then calls the "buildTreeFromDocument()" method which will effectively
	 * parse the document.
	 *
	 * @param tempDirPath the path of the temporary directory containing the XML tree representation of the comment.
	 * @return the abstract syntax tree corresponding to the comment.
	 */
	private AbstractSyntaxTree parseAcslComment(final String tempDirPath) throws UnhandledElementException
	{
		//Set up the XML parser
		final File abstractSyntaxTreePath = new File(tempDirPath + File.separator + ABSTRACT_SYNTAX_TREE_FILE_NAME);
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder;
		final Document document;

		try
		{
			builder = factory.newDocumentBuilder();
			document = builder.parse(abstractSyntaxTreePath);
		}
		catch (ParserConfigurationException | SAXException | IOException e)
		{
			throw new RuntimeException(String.format(
				"The XML parser failed to parse the abstract syntax tree corresponding to the comment: %s",
				e
			));
		}

		document.getDocumentElement().normalize();

		return this.buildTreeFromDocument(abstractSyntaxTreePath.getAbsolutePath(), document);
	}

	/**
	 *
	 * @param document
	 * @return
	 */
	private AbstractSyntaxTree buildTreeFromDocument(final String commentFilePath,
													 final Document document) throws UnhandledElementException
	{
		final AbstractSyntaxTree abstractSyntaxTree = new AbstractSyntaxTree(AcslFactory.createRootNode());
		final NodeList rootList = document.getElementsByTagName(AcslType.ROOT.getXmlTag());

		if (rootList == null
			|| rootList.getLength() == 0)
		{
			throw new RuntimeException(String.format("The XML file \"%s\" is malformed!", commentFilePath));
		}

		final Node root = rootList.item(0);

		for (int i = 0; i < root.getChildNodes().getLength(); i++)
		{
			final Node child = root.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.FUNCTION_CONTRACT.getXmlTag()))
			{
				this.parseFunctionContract(childElement, abstractSyntaxTree.getRoot());
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.ROOT.getXmlTag());
			}
		}

		return abstractSyntaxTree;
	}

	//Parsing methods

	private void parseFunctionContract(final Element functionContract,
									   final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AbstractSyntaxNode functionContractNode = AcslFactory.createFunctionContractNode();
		currentNode.addChildAndForceParent(functionContractNode);

		for (int i = 0; i < functionContract.getChildNodes().getLength(); i++)
		{
			final Node child = functionContract.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.NAMED_BEHAVIOR_LIST.getXmlTag()))
			{
				this.parseNamedBehaviorList(childElement, functionContractNode);
			}
			else if (childElement.getTagName().equals(AcslType.REQUIRES_CLAUSE_LIST.getXmlTag()))
			{
				this.parseRequiresClauseList(childElement, functionContractNode);
			}
			else if (childElement.getTagName().equals(AcslType.SIMPLE_CLAUSE_LIST.getXmlTag()))
			{
				this.parseSimpleClauseList(childElement, functionContractNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.FUNCTION_CONTRACT.getXmlTag());
			}
		}
	}

	private void parseNamedBehaviorList(final Element namedBehaviorList,
										final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AbstractSyntaxNode namedBehaviorListNode = AcslFactory.createNamedBehaviorListNode();
		currentNode.addChildAndForceParent(namedBehaviorListNode);

		for (int i = 0; i < namedBehaviorList.getChildNodes().getLength(); i++)
		{
			final Node child = namedBehaviorList.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.BEHAVIOR.getXmlTag()))
			{
				this.parseBehavior(childElement, namedBehaviorListNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.NAMED_BEHAVIOR_LIST.getXmlTag());
			}
		}
	}

	private void parseBehavior(final Element behavior,
							   final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final String id = behavior.getAttribute(AcslXmlAttribute.IDENTIFIER);
		final BehaviorNode behaviorNode = AcslFactory.createBehaviorNode(id);
		currentNode.addChildAndForceParent(behaviorNode);

		for (int i = 0; i < behavior.getChildNodes().getLength(); i++)
		{
			final Node child = behavior.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.ASSUMES_CLAUSE_LIST.getXmlTag()))
			{
				this.parseAssumesClauseList(childElement, behaviorNode);
			}
			else if (childElement.getTagName().equals(AcslType.REQUIRES_CLAUSE_LIST.getXmlTag()))
			{
				this.parseRequiresClauseList(childElement, behaviorNode);
			}
			else if (childElement.getTagName().equals(AcslType.SIMPLE_CLAUSE_LIST.getXmlTag()))
			{
				this.parseSimpleClauseList(childElement, behaviorNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.NAMED_BEHAVIOR_LIST.getXmlTag());
			}
		}
	}

	private void parseAssumesClauseList(final Element assumesClauseList,
									    final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AbstractSyntaxNode assumesClauseListNode = AcslFactory.createAssumesClauseListNode();
		currentNode.addChildAndForceParent(assumesClauseListNode);

		for (int i = 0; i < assumesClauseList.getChildNodes().getLength(); i++)
		{
			final Node child = assumesClauseList.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.PREDICATE_OR_TERM.getXmlTag()))
			{
				this.parsePredicateOrTerm(childElement, assumesClauseListNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.ASSUMES_CLAUSE_LIST.getXmlTag());
			}
		}
	}

	private void parseSimpleClauseList(final Element simpleClauseList,
							           final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AbstractSyntaxNode simpleClauseListNode = AcslFactory.createSimpleClauseListNode();
		currentNode.addChildAndForceParent(simpleClauseListNode);

		for (int i = 0; i < simpleClauseList.getChildNodes().getLength(); i++)
		{
			final Node child = simpleClauseList.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.ASSIGN_CLAUSE.getXmlTag()))
			{
				this.parseAssignClause(childElement, simpleClauseListNode);
			}
			else if (childElement.getTagName().equals(AcslType.ENSURES_CLAUSE.getXmlTag()))
			{
				this.parseEnsuresClause(childElement, simpleClauseListNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.SIMPLE_CLAUSE_LIST.getXmlTag());
			}
		}
	}

	private void parseAssignClause(final Element assignClause,
								   final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AssignClauseNode assignClauseNode = AcslFactory.createAssignClauseNode();
		currentNode.addChildAndForceParent(assignClauseNode);

		for (int i = 0; i < assignClause.getChildNodes().getLength(); i++)
		{
			final Node child = assignClause.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.LOCATIONS.getXmlTag()))
			{
				this.parseLocations(childElement, assignClauseNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.ASSIGN_CLAUSE.getXmlTag());
			}
		}
	}

	private void parseEnsuresClause(final Element ensuresClause,
								    final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final String clauseKind = ensuresClause.getAttribute(AcslXmlAttribute.KIND);
		final EnsuresClauseNode ensuresClauseNode = AcslFactory.createEnsuresClauseNode(AcslClauseKind.getKindFromName(clauseKind));
		currentNode.addChildAndForceParent(ensuresClauseNode);

		for (int i = 0; i < ensuresClause.getChildNodes().getLength(); i++)
		{
			final Node child = ensuresClause.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.PREDICATE_OR_TERM.getXmlTag()))
			{
				this.parsePredicateOrTerm(childElement, ensuresClauseNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.ENSURES_CLAUSE.getXmlTag());
			}
		}
	}

	private void parseRequiresClauseList(final Element requiresClauseList,
										 final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AbstractSyntaxNode requiresClauseListNode = AcslFactory.createRequiresClauseListNode();
		currentNode.addChildAndForceParent(requiresClauseListNode);

		for (int i = 0; i < requiresClauseList.getChildNodes().getLength(); i++)
		{
			final Node child = requiresClauseList.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.REQUIRES_CLAUSE.getXmlTag()))
			{
				this.parseRequiresClause(childElement, requiresClauseListNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.REQUIRES_CLAUSE_LIST.getXmlTag());
			}
		}
	}

	private void parseRequiresClause(final Element requiresClause,
									 final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final String clauseKind = requiresClause.getAttribute(AcslXmlAttribute.KIND);
		final RequiresClauseNode requiresClauseNode = AcslFactory.createRequiresClauseNode(AcslClauseKind.getKindFromName(clauseKind));
		currentNode.addChildAndForceParent(requiresClauseNode);

		for (int i = 0; i < requiresClause.getChildNodes().getLength(); i++)
		{
			final Node child = requiresClause.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.PREDICATE_OR_TERM.getXmlTag()))
			{
				this.parsePredicateOrTerm(childElement, requiresClauseNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.REQUIRES_CLAUSE.getXmlTag());
			}
		}
	}

	private void parseLocations(final Element locations,
								final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AbstractSyntaxNode locationsNode = AcslFactory.createLocationsNode();
		currentNode.addChildAndForceParent(locationsNode);

		for (int i = 0; i < locations.getChildNodes().getLength(); i++)
		{
			final Node child = locations.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.LOCATION.getXmlTag()))
			{
				this.parseLocation(childElement, locationsNode);
			}
			else if (childElement.getTagName().equals(AcslType.PREDICATE_OR_TERM.getXmlTag()))
			{
				this.parsePredicateOrTerm(childElement, locationsNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.LOCATIONS.getXmlTag());
			}
		}
	}

	private void parseLocation(final Element locations,
							   final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AbstractSyntaxNode locationNode = AcslFactory.createLocationNode();
		currentNode.addChildAndForceParent(locationNode);

		for (int i = 0; i < locations.getChildNodes().getLength(); i++)
		{
			final Node child = locations.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.MEMORY_ALLOCATION_SET.getXmlTag()))
			{
				this.parseMemoryLocationSet(childElement, locationNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.LOCATION.getXmlTag());
			}
		}
	}

	private void parseBinders(final Element binders,
							  final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AbstractSyntaxNode bindersNode = AcslFactory.createBindersNode();
		currentNode.addChildAndForceParent(bindersNode);

		for (int i = 0; i < binders.getChildNodes().getLength(); i++)
		{
			final Node child = binders.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.BINDER.getXmlTag()))
			{
				this.parseBinder(childElement, bindersNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.BINDERS.getXmlTag());
			}
		}
	}

	private void parseBinder(final Element binder,
							 final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AbstractSyntaxNode binderNode = AcslFactory.createBinderNode();
		currentNode.addChildAndForceParent(binderNode);

		for (int i = 0; i < binder.getChildNodes().getLength(); i++)
		{
			final Node child = binder.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.TYPES.getXmlTag()))
			{
				this.parseTypes(childElement, binderNode);
			}
			else if (childElement.getTagName().equals(AcslType.VARIABLE_IDENTIFIER.getXmlTag()))
			{
				this.parseVariableIdentifier(childElement, binderNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.BINDER.getXmlTag());
			}
		}
	}

	private void parseVariableIdentifier(final Element variableIdentifier,
										 final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final String kind = variableIdentifier.getAttribute(AcslXmlAttribute.KIND);
		final String content = variableIdentifier.getTextContent();
		final VariableIdentifierNode variableIdentifierNode = AcslFactory.createVariableIdentifierNode(kind, content);
		currentNode.addChildAndForceParent(variableIdentifierNode);

		for (int i = 0; i < variableIdentifier.getChildNodes().getLength(); i++)
		{
			final Node child = variableIdentifier.getChildNodes().item(i);

			if (child.getNodeType() == Node.TEXT_NODE)
			{
				continue;
			}

			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.VARIABLE_IDENTIFIER.getXmlTag()))
			{
				this.parseVariableIdentifier(childElement, variableIdentifierNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.VARIABLE_IDENTIFIER.getXmlTag());
			}
		}
	}

	private void parseTypes(final Element types,
							final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AbstractSyntaxNode typesNode = AcslFactory.createTypesNode();
		currentNode.addChildAndForceParent(typesNode);

		for (int i = 0; i < types.getChildNodes().getLength(); i++)
		{
			final Node child = types.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.TYPE_SPECIFIER.getXmlTag()))
			{
				this.parseTypeSpecifier(childElement, typesNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.TYPES.getXmlTag());
			}
		}
	}

	private void parseTypeSpecifier(final Element typeSpecifier,
									final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final String kind = typeSpecifier.getAttribute(AcslXmlAttribute.KIND);
		final AbstractSyntaxNode typesNode = AcslFactory.createTypeSpecifierNode(kind);
		currentNode.addChildAndForceParent(typesNode);

		if (typeSpecifier.getChildNodes().getLength() != 0)
		{
			throwNoChildrenExpectedException(AcslType.TYPE_SPECIFIER.getXmlTag());
		}
	}

	private void parseMemoryLocationSet(final Element location,
										final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final String content = location.getTextContent();
		final AbstractSyntaxNode memoryAllocationSet = AcslFactory.createMemoryAllocationSetNode(content);
		currentNode.addChildAndForceParent(memoryAllocationSet);

		for (int i = 0; i < location.getChildNodes().getLength(); i++)
		{
			final Node child = location.getChildNodes().item(i);

			if (child.getNodeType() == Node.TEXT_NODE)
			{
				continue;
			}

			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;
		}
	}

	private void parsePredicateOrTerm(final Element predicateOrTerm,
									  final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final String predicateOrTermKind = predicateOrTerm.getAttribute(AcslXmlAttribute.KIND);
		final String predicateOrTermContent = predicateOrTerm.getTextContent();
		final PredicateOrTermNode predicateOrTermNode = AcslFactory.createPredicateOrTermNode(
			predicateOrTermKind,
			predicateOrTermContent
		);
		currentNode.addChildAndForceParent(predicateOrTermNode);

		for (int i = 0; i < predicateOrTerm.getChildNodes().getLength(); i++)
		{
			final Node child = predicateOrTerm.getChildNodes().item(i);

			if (child.getNodeType() == Node.TEXT_NODE)
			{
				continue;
			}

			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.BINDERS.getXmlTag()))
			{
				this.parseBinders(childElement, predicateOrTermNode);
			}
			else if (childElement.getTagName().equals(AcslType.INDEX.getXmlTag()))
			{
				this.parseIndex(childElement, predicateOrTermNode);
			}
			else if (childElement.getTagName().equals(AcslType.LOWER_BOUND.getXmlTag()))
			{
				this.parseLowerBound(childElement, predicateOrTermNode);
			}
			else if (childElement.getTagName().equals(AcslType.NAME.getXmlTag()))
			{
				this.parseName(childElement, predicateOrTermNode);
			}
			else if (childElement.getTagName().equals(AcslType.OPERATOR.getXmlTag()))
			{
				this.parseOperator(childElement, predicateOrTermNode);
			}
			else if (childElement.getTagName().equals(AcslType.PREDICATE_OR_TERM.getXmlTag()))
			{
				this.parsePredicateOrTerm(childElement, predicateOrTermNode);
			}
			else if (childElement.getTagName().equals(AcslType.UPPER_BOUND.getXmlTag()))
			{
				this.parseUpperBound(childElement, predicateOrTermNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.PREDICATE_OR_TERM.getXmlTag());
			}
		}
	}

	private void parseName(final Element name,
						   final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final NameNode nameNode = AcslFactory.createNameNode(name.getTextContent());
		currentNode.addChildAndForceParent(nameNode);

		final NodeList children = name.getChildNodes();

		if (children.getLength() == 1)
		{
			final Node child = children.item(0);

			if (child.getNodeType() != Node.TEXT_NODE)
			{
				throwUnexpectedElementException(((Element) child).getTagName(), AcslType.NAME.getXmlTag());
			}
		}
		else if (children.getLength() != 0)
		{
			throwMaxChildrenNumberExceededException(AcslType.NAME.getXmlTag(), 1, children.getLength());
		}
	}

	private void parseIndex(final Element index,
							final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AbstractSyntaxNode indexNode = AcslFactory.createIndexNode();
		currentNode.addChildAndForceParent(indexNode);

		for (int i = 0; i < index.getChildNodes().getLength(); i++)
		{
			final Node child = index.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.PREDICATE_OR_TERM.getXmlTag()))
			{
				this.parsePredicateOrTerm(childElement, indexNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.INDEX.getXmlTag());
			}
		}
	}

	private void parseLowerBound(final Element lowerBound,
								 final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AbstractSyntaxNode lowerBoundNode = AcslFactory.createBoundaryNode(AcslType.LOWER_BOUND);
		currentNode.addChildAndForceParent(lowerBoundNode);

		for (int i = 0; i < lowerBound.getChildNodes().getLength(); i++)
		{
			final Node child = lowerBound.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.PREDICATE_OR_TERM.getXmlTag()))
			{
				this.parsePredicateOrTerm(childElement, lowerBoundNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.LOWER_BOUND.getXmlTag());
			}
		}
	}

	private void parseUpperBound(final Element upperBound,
								 final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AbstractSyntaxNode lowerBoundNode = AcslFactory.createBoundaryNode(AcslType.UPPER_BOUND);
		currentNode.addChildAndForceParent(lowerBoundNode);

		for (int i = 0; i < upperBound.getChildNodes().getLength(); i++)
		{
			final Node child = upperBound.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslType.PREDICATE_OR_TERM.getXmlTag()))
			{
				this.parsePredicateOrTerm(childElement, lowerBoundNode);
			}
			else
			{
				throwUnexpectedElementException(childElement.getTagName(), AcslType.UPPER_BOUND.getXmlTag());
			}
		}
	}

	private void parseOperator(final Element operator,
							   final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final String operatorName = operator.getTextContent();
		final OperatorNode operatorNode = AcslFactory.createOperatorNode(operatorName);
		currentNode.addChildAndForceParent(operatorNode);

		if (!operatorNode.getChildren().isEmpty())
		{
			throwNoChildrenExpectedException(AcslType.OPERATOR.getXmlTag());
		}
	}

	//Utility methods

	private void assertNodeTypeElement(final Node node)
	{
		if (node.getNodeType() != Node.ELEMENT_NODE)
		{
			throw new RuntimeException(String.format(
				"Expected node of type %d, got type %d instead.",
				node.getNodeType(),
				Node.ELEMENT_NODE
			));
		}
	}

	private void throwUnexpectedElementException(final String currentTag,
												 final String parentTag) throws UnhandledElementException
	{
		throw new UnhandledElementException(String.format(
			"The tag \"%s\" was not expected as child of a \"%s\" tag.",
			Utils.tagify(currentTag),
			Utils.tagify(parentTag)
		));
	}

	private void throwNoChildrenExpectedException(final String nodeTag) throws UnhandledElementException
	{
		throw new UnhandledElementException(String.format(
			"The tag \"%s\" is not supposed to have children.",
			Utils.tagify(nodeTag)
		));
	}

	private void throwMaxChildrenNumberExceededException(final String nodeTag,
														 final int maxNbChildren,
														 final int nbChildren) throws UnhandledElementException
	{
		throw new UnhandledElementException(String.format(
			"The tag \"%s\" is not supposed to have more than %d children but has %d.",
			Utils.tagify(nodeTag),
			maxNbChildren,
			nbChildren
		));
	}
}
