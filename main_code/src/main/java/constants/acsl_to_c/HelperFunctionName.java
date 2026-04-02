package constants.acsl_to_c;

/**
 * Name:        HelperFunctionName.java
 * Content:     This class stores the names of the standard helper functions useful to translate ACSL contracts to C.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    02/04/26
 */

public class HelperFunctionName
{
	private HelperFunctionName()
	{

	}

	public static final String CHECK_ARRAY_VALIDITY = "_array_is_valid";
	//This must be used with an appropriate call to "String.format()"!
	public static final String GLOBAL_REQUIREMENT_SATISFIED = "_global_req_%s_satisfied";
}
