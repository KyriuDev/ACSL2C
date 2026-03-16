package parsing;

import ast.AbstractSyntaxNode;
import ast.AbstractSyntaxTree;
import ast.AstFactory;
import constants.ReturnCode;
import constants.acsl.ast.PredicateOrTermNode;
import constants.acsl.ast.RequiresClauseNode;
import constants.acsl.others.AcslClauseKind;
import constants.acsl.xml.AcslXmlAttribute;
import constants.acsl.xml.AcslXmlTag;
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
import java.util.List;

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
	public void parse()
	{
		for (final CComment comment : this.acslComments)
		{
			final String tempDirPath = this.writeCommentToFile(comment);
			this.callSyntax(tempDirPath);
			final AbstractSyntaxTree commentTree = this.parseAcslComment(tempDirPath);
			comment.setAbstractSyntaxTree(commentTree);
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
	private AbstractSyntaxTree parseAcslComment(final String tempDirPath)
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
		final AbstractSyntaxTree abstractSyntaxTree = new AbstractSyntaxTree(AstFactory.createRootNode());
		final NodeList rootList = document.getElementsByTagName(AcslXmlTag.ACSL_FILE);

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

			if (childElement.getTagName().equals(AcslXmlTag.FUNCTION_CONTRACT))
			{
				this.parseFunctionContract(childElement, abstractSyntaxTree.getRoot());
			}
			else
			{
				throwUnexpectedElementError(childElement.getTagName(), Utils.tagify(AcslXmlTag.ACSL_FILE));
			}
		}

		return abstractSyntaxTree;
	}

	//Parsing methods

	private void parseFunctionContract(final Element functionContract,
									   final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AbstractSyntaxNode functionContractNode = AstFactory.createFunctionContractNode();
		currentNode.addChildAndForceParent(functionContractNode);

		for (int i = 0; i < functionContract.getChildNodes().getLength(); i++)
		{
			final Node child = functionContract.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslXmlTag.REQUIRES_CLAUSE_LIST))
			{
				this.parseRequiresClauseList(childElement, functionContractNode);
			}
			else
			{
				throwUnexpectedElementError(childElement.getTagName(), Utils.tagify(AcslXmlTag.FUNCTION_CONTRACT));
			}
		}
	}

	private void parseRequiresClauseList(final Element requiresClauseList,
										 final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final AbstractSyntaxNode requiresClauseListNode = AstFactory.createRequiresClauseListNode();
		currentNode.addChildAndForceParent(requiresClauseListNode);

		for (int i = 0; i < requiresClauseList.getChildNodes().getLength(); i++)
		{
			final Node child = requiresClauseList.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslXmlTag.REQUIRES_CLAUSE))
			{
				this.parseRequiresClause(childElement, requiresClauseListNode);
			}
			else
			{
				throwUnexpectedElementError(childElement.getTagName(), Utils.tagify(AcslXmlTag.REQUIRES_CLAUSE_LIST));
			}
		}
	}

	private void parseRequiresClause(final Element requiresClause,
									 final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final String clauseKind = requiresClause.getAttribute(AcslXmlAttribute.KIND);
		final RequiresClauseNode requiresClauseNode = AstFactory.createRequiresClauseNode(AcslClauseKind.getKindFromName(clauseKind));
		currentNode.addChildAndForceParent(requiresClauseNode);

		for (int i = 0; i < requiresClause.getChildNodes().getLength(); i++)
		{
			final Node child = requiresClause.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslXmlTag.PREDICATE_OR_TERM))
			{
				this.parsePredicateOrTerm(childElement, requiresClauseNode);
			}
			else
			{
				throwUnexpectedElementError(childElement.getTagName(), Utils.tagify(AcslXmlTag.REQUIRES_CLAUSE));
			}
		}
	}

	private void parsePredicateOrTerm(final Element predicateOrTerm,
									  final AbstractSyntaxNode currentNode) throws UnhandledElementException
	{
		final String predicateOrTermKind = predicateOrTerm.getAttribute(AcslXmlAttribute.KIND);
		final PredicateOrTermNode predicateOrTermNode = AstFactory.createPredicateOrTermNode(predicateOrTermKind);
		currentNode.addChildAndForceParent(predicateOrTermNode);

		for (int i = 0; i < predicateOrTerm.getChildNodes().getLength(); i++)
		{
			final Node child = predicateOrTerm.getChildNodes().item(i);
			this.assertNodeTypeElement(child);
			final Element childElement = (Element) child;

			if (childElement.getTagName().equals(AcslXmlTag.PREDICATE_OR_TERM))
			{
				this.parsePredicateOrTerm(childElement, predicateOrTermNode);
			}
			else if (childElement.getTagName().equals(AcslXmlTag.OPERATOR))
			{
				this.parseOperator(childElement, predicateOrTermNode);
			}
			else
			{
				throwUnexpectedElementError(childElement.getTagName(), Utils.tagify(AcslXmlTag.PREDICATE_OR_TERM));
			}
		}
	}

	private void parseOperator(final Element operator,
							   final AbstractSyntaxNode currentNode)
	{
		final String operatorName = operator.getTextContent();
	}

	//Utility methods

	private void assertNodeTypeElement(final Node node)
	{
		if (node.getNodeType() != Node.ELEMENT_NODE)
		{
			throw new RuntimeException(String.format(
				"Expected node of type %d, got %d",
				node.getNodeType(),
				Node.ELEMENT_NODE
			));
		}
	}

	private void throwUnexpectedElementError(final String currentTag,
											 final String parentTag) throws UnhandledElementException
	{
		throw new UnhandledElementException(String.format(
			"The tag \"%s\" was not expected as child of an \"%s\" tag.",
			currentTag,
			Utils.tagify(parentTag)
		));
	}
}
