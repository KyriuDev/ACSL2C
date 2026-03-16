package constants.acsl;

/**
 * Name:        ACSLKeyword.java
 * Content:     This enum lists the ACSL keywords/special characters that are currently supported by this tool.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    02/03/26
 */

public enum ACSLKeyword
{
	AT("@")
	;

	private final String keyword;

	ACSLKeyword(final String keyword)
	{
		this.keyword = keyword;
	}

	public String getKeyword()
	{
		return this.keyword;
	}
}
