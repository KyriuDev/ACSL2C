package constants;

/**
 * Name:        CKeyword.java
 * Content:	    This enum defines some usual C keywords along with their actual (textual) representation.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    27/02/26
 */

public enum CKeyword
{
	ELSE("else"),
	ELSE_IF("else if"),
	IF("if"),
	RETURN("return"),
	WHILE("while")
	;

	private final String keyword;

	CKeyword(final String keyword)
	{
		this.keyword = keyword;
	}

	public String getKeyword()
	{
		return this.keyword;
	}
}
