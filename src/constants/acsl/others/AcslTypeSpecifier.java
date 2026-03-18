package constants.acsl.others;

/**
 * Name:        AcslTypeSpecifier.java
 * Content:	    This enum lists the ACSL type specifiers that are used in the context of this approach.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    18/03/26
 */
public enum AcslTypeSpecifier
{
	BOOLEAN("boolean"),
	CHAR("char"),
	DOUBLE("double"),
	ENUM("enum"),
	FLOAT("float"),
	INT("int"),
	INTEGER("integer"),
	LONG("long"),
	REAL("real"),
	SHORT("short"),
	SIGNED("signed"),
	STRUCT("struct"),
	UNION("union"),
	UNSIGNED("unsigned"),
	VOID("void")
	;

	private final String xmlTag;

	AcslTypeSpecifier(final String xmlTag)
	{
		this.xmlTag = xmlTag;
	}

	public String getXmlTag()
	{
		return this.xmlTag;
	}

	//Static methods

	public static AcslTypeSpecifier getTypeSpecifierFromKind(final String kind)
	{
		if (kind.equals(AcslTypeSpecifier.BOOLEAN.getXmlTag()))
		{
			return AcslTypeSpecifier.BOOLEAN;
		}
		else if (kind.equals(AcslTypeSpecifier.CHAR.getXmlTag()))
		{
			return AcslTypeSpecifier.CHAR;
		}
		else if (kind.equals(AcslTypeSpecifier.DOUBLE.getXmlTag()))
		{
			return AcslTypeSpecifier.DOUBLE;
		}
		else if (kind.equals(AcslTypeSpecifier.ENUM.getXmlTag()))
		{
			return AcslTypeSpecifier.ENUM;
		}
		else if (kind.equals(AcslTypeSpecifier.FLOAT.getXmlTag()))
		{
			return AcslTypeSpecifier.FLOAT;
		}
		else if (kind.equals(AcslTypeSpecifier.INT.getXmlTag()))
		{
			return AcslTypeSpecifier.INT;
		}
		else if (kind.equals(AcslTypeSpecifier.INTEGER.getXmlTag()))
		{
			return AcslTypeSpecifier.INTEGER;
		}
		else if (kind.equals(AcslTypeSpecifier.LONG.getXmlTag()))
		{
			return AcslTypeSpecifier.LONG;
		}
		else if (kind.equals(AcslTypeSpecifier.REAL.getXmlTag()))
		{
			return AcslTypeSpecifier.REAL;
		}
		else if (kind.equals(AcslTypeSpecifier.SHORT.getXmlTag()))
		{
			return AcslTypeSpecifier.SHORT;
		}
		else if (kind.equals(AcslTypeSpecifier.SIGNED.getXmlTag()))
		{
			return AcslTypeSpecifier.SIGNED;
		}
		else if (kind.equals(AcslTypeSpecifier.STRUCT.getXmlTag()))
		{
			return AcslTypeSpecifier.STRUCT;
		}
		else if (kind.equals(AcslTypeSpecifier.UNION.getXmlTag()))
		{
			return AcslTypeSpecifier.UNION;
		}
		else if (kind.equals(AcslTypeSpecifier.UNSIGNED.getXmlTag()))
		{
			return AcslTypeSpecifier.UNSIGNED;
		}
		else if (kind.equals(AcslTypeSpecifier.VOID.getXmlTag()))
		{
			return AcslTypeSpecifier.VOID;
		}
		else
		{
			throw new RuntimeException(String.format(
				"The kind \"%s\" does not correspond to any type specifier kind.",
				kind
			));
		}
	}
}
