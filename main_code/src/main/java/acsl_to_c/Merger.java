package acsl_to_c;


import ast.AbstractSyntaxTree;

/**
 * Name:        Merger.java
 * Content:     This class is in charge of merging the translation of a former ACSL contract transformed into C code
 * 				into the original C program.
 * 				It basically happens after the execution of ACSL2ASTTranslator, which translates the contract into C
 * 				code fragments.
 * 				It is in charge of handling correctness of the resulting program, e.g., the mapping of the function
 * 				arguments names with their corresponding calling environment name.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class Merger
{
	private final AbstractSyntaxTree mainProgramTree;
	private final ACSL2ASTTranslator translator;

	//Constructors

	public Merger(final AbstractSyntaxTree mainProgramTree,
				  final ACSL2ASTTranslator translator)
	{
		this.mainProgramTree = mainProgramTree;
		this.translator = translator;
	}

	//Public methods

	public void merge()
	{
		/*
			The helper structures are simply added as children of the root node, as they will be ordered later on by
			the writer itself.
			More advanced versions of this tool might want to slightly alter this behaviour so as not to rely on the
			behaviour of the writer, which, in a perfect world, should not decide where to put the elements of the
			program itself :-)
		 */

		for (final AbstractSyntaxTree helperStruct : this.translator.getHelperStructures())
		{
			this.mainProgramTree.getRoot().addChildAndForceParent(helperStruct.getRoot());
		}
	}

	//Private methods
}
