package ast.c;

import constants.c.CStorageClass;
import constants.c.CTypeQualifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Name:        IDeclarationSpecifierNode.java
 * Content:     This class serves as basis for declaration specifier nodes such as SimpleDeclSpecifierNode or
 * 				TypedefNameSpecifierNode which mostly contain the same information, that is, the one stored in this
 * 				class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public abstract class IDeclarationSpecifierNode extends CBaseNode
{
	private final ArrayList<CTypeQualifier> typeQualifiers;
	private final CStorageClass storageClass;

	//Constructors

	public IDeclarationSpecifierNode(final CStorageClass storageClass,
	                                 final boolean isConst,
	                                 final boolean isInline,
	                                 final boolean isRestrict,
	                                 final boolean isVolatile)
	{
		this.storageClass = storageClass;
		this.typeQualifiers = new ArrayList<>();

		if (isConst)
		{
			this.typeQualifiers.add(CTypeQualifier.CONST);
		}

		if (isInline)
		{
			this.typeQualifiers.add(CTypeQualifier.INLINE);
		}

		if (isRestrict)
		{
			this.typeQualifiers.add(CTypeQualifier.RESTRICT);
		}

		if (isVolatile)
		{
			this.typeQualifiers.add(CTypeQualifier.VOLATILE);
		}
	}

	//Overrides

	@Override
	public String getNodeHeader()
	{
		final StringBuilder builder = new StringBuilder();

		if (this.storageClass != CStorageClass.UNSPECIFIED)
		{
			builder.append(this.storageClass.getStorageClass())
					.append(" ");
		}

		String separator = "";

		for (final CTypeQualifier qualifier : this.typeQualifiers)
		{
			builder.append(separator)
					.append(qualifier.getTypeQualifier());
			separator = " ";
		}

		return builder.toString();
	}

	//Public methods

	public CStorageClass getStorageClass()
	{
		return this.storageClass;
	}

	public List<CTypeQualifier> getTypeQualifiers()
	{
		return Collections.unmodifiableList(this.typeQualifiers);
	}

	public boolean isConst()
	{
		return this.typeQualifiers.contains(CTypeQualifier.CONST);
	}

	public boolean isInline()
	{
		return this.typeQualifiers.contains(CTypeQualifier.INLINE);
	}

	public boolean isRestrict()
	{
		return this.typeQualifiers.contains(CTypeQualifier.RESTRICT);
	}

	public boolean isVolatile()
	{
		return this.typeQualifiers.contains(CTypeQualifier.VOLATILE);
	}

	/**
	 * This method is used to sort the qualifiers in a specific order, if the default order does not suit the needs
	 * of the user.
	 * It can safely either be overridden, implemented with a comparator, or manually implemented.
	 * By default, the elements are not (re)sorted.
	 */
	public void sortQualifiers()
	{

	}
}
